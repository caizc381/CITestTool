package com.mytijian.admin.web.controller.order.manager;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mytijian.admin.web.controller.order.OrderBaseController;
import com.mytijian.admin.web.controller.order.helper.NewOrderExportHelper;
import com.mytijian.offer.examitem.constant.enums.ExamItemToMealEnum;
import com.mytijian.offer.examitem.model.ExamItemSnap;
import com.mytijian.order.model.MongoOrder;
import com.mytijian.order.params.OrderQueryParams;
import com.mytijian.order.service.OrderService;
import com.mytijian.payment.enums.PaymentTypeEnum;
import com.mytijian.payment.enums.TradeTypeEnum;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.util.ExcelUtil;
import com.mytijian.util.MoneyUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NewOrderExportFileManager extends OrderBaseController {
    private final static Logger logger = LoggerFactory.getLogger(NewOrderExportFileManager.class);
    private final static String refundTotal = "refundTotal";
    private final static String onlineRefundTotal = "onlineRefundTotal";

    @Resource
    private OrderService orderService;

    @Resource(name = "newOrderExportHelper")
    private NewOrderExportHelper newOrderExportHelper;

    @Value("${temp.folder}")
    private String tempFolder;

    @SuppressWarnings({"rawtypes"})
    public String crmExportCheckbook(OrderQueryParams orderQueryParams) throws Exception {

        PageView<MongoOrder> orderPageView = getMongoOrdersByOrderQueryParams(orderQueryParams, null, null);
        List<MongoOrder> mongoOrders = orderPageView.getRecords();
        if (CollectionUtils.isNotEmpty(mongoOrders)) {
            Integer hospitalId = orderQueryParams.getHospitalId();
            InputStream stream = getClass().getClassLoader().getResourceAsStream("ordertemplate/crm_checkbook.xls");
            File targetFile = new File(UUID.randomUUID().toString()+".xls");
            FileUtils.copyInputStreamToFile(stream, targetFile);
            //total必须在orderList之前
            Map<String,Map<Integer, String>> refundMap = new HashMap<>(2);
            List<List<String>> total = getCrmCheckbookTotalMoney(mongoOrders,refundMap);
            List<List<String>> orderList = getCrmCheckbookOrderList(targetFile, hospitalId, mongoOrders,refundMap);
            InputStream is = new FileInputStream(targetFile);
            String tempOutputFilename = tempFolder + "crm_checkbook.xls";
            FileOutputStream out = new FileOutputStream(tempOutputFilename);
            HSSFWorkbook book = ExcelUtil.saveCrmCheckbookExcel(orderList, total, is);
            book.write(out);
            out.flush();
            out.close();
            logger.info("exportCheckbook size is =" + orderList.size() + " file: " + tempOutputFilename);
            return tempOutputFilename;
        }
        return null;
    }

    private Double enrichRefundField(List<MongoOrder> mongoOrders,List<Integer> paymentTypeList, Map<Integer, String> refundMaps) {
        List<Integer> orderIds = mongoOrders.stream().map(MongoOrder::getId).collect(Collectors.toList());
        List<Integer> orderStatues = Lists.newArrayList();
        orderStatues.add(TradeTypeEnum.CompleteRefund.getCode());
        Map<Integer, Double> payments = orderService.countOrderPayment(orderIds, orderStatues,paymentTypeList);
        Set<Integer> keyPayments = payments.keySet();
        Double totalSum = Double.valueOf(new DecimalFormat("0.00").format(0d));
        for (MongoOrder mongoOrder : mongoOrders) {
            String paymentStr = "0.00";
            int orderId = mongoOrder.getId();
            if (keyPayments.contains(orderId)) {
                Double payment = payments.get(orderId);
                if (payment != null) {
                    paymentStr = new DecimalFormat("0.00").format(payment);
                    totalSum += Double.valueOf(paymentStr);
                }
            }
            if (Objects.nonNull(refundMaps)) {
                refundMaps.put(mongoOrder.getId(), paymentStr);
            }
        }
        return totalSum;
    }


    private List<List<String>> getCrmCheckbookOrderList(File file, Integer hospitalId, List<MongoOrder> mongoOrders,Map<String,Map<Integer, String>> refundMap) throws IOException, InvalidFormatException {
        List<List<String>> template = ExcelUtil.analyzeExcel(file);
        List<List<String>> orderList = new ArrayList<>();
        orderList.add(template.get(0));
        Map<Integer, String> refundTotalMap = refundMap.get(refundTotal);
        Map<Integer, String> onlineRefundTotalMap = refundMap.get(onlineRefundTotal);
        List<String> keyList = template.get(1);
        for (MongoOrder mongoOrder : mongoOrders) {
            List<String> eachOrder = new ArrayList<String>();
            Map<String, String> itemMap = new HashMap<String, String>();
            itemMap.put(refundTotal, refundTotalMap.get(mongoOrder.getId()));
            itemMap.put(onlineRefundTotal, onlineRefundTotalMap.get(mongoOrder.getId()));
            for (int index = 0; index < keyList.size(); index++) {
                try {
                    eachOrder.add(newOrderExportHelper.getValue(keyList.get(index), mongoOrder, hospitalId, itemMap));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    eachOrder.add(null);
                }
            }
            orderList.add(eachOrder);
        }
        return orderList;
    }

    private List<List<String>> getCrmCheckbookTotalMoney(List<MongoOrder> mongoOrders, Map<String,Map<Integer, String>> refundMap) {
        Double refundTotalSum = 0d;
        Double onlineRefundTotalSum = 0d;
        Double offPayRefundTotalSum = 0d;
        //单项原价金额总额
        Double itemsMoneySum = 0d;
        if (CollectionUtils.isNotEmpty(mongoOrders)) {
            Map<Integer, String> refundTotalMaps = new HashMap<>();
            Map<Integer, String> onlineRefundTotalMaps = new HashMap<>();
            refundTotalSum = enrichRefundField(mongoOrders,null,refundTotalMaps);
            onlineRefundTotalSum = enrichRefundField(mongoOrders,Arrays.asList(PaymentTypeEnum.Alipay.getCode(), PaymentTypeEnum.Balance.getCode(), PaymentTypeEnum.Weixin.getCode()),onlineRefundTotalMaps);
            offPayRefundTotalSum = enrichRefundField(mongoOrders,Arrays.asList(PaymentTypeEnum.Offline.getCode()),null);
            refundMap.put(refundTotal, refundTotalMaps);
            refundMap.put(onlineRefundTotal, onlineRefundTotalMaps);
            itemsMoneySum = getItemsOriginalPriceSum(mongoOrders);
        }

        Double orderPriceSum = 0d;
        Double selfMoneySum = 0d;
        Double offlinePayMoneySum = 0d;
        //单项原价金额总额退款金额，因为单项无退款，为0
        Double itemsRefundMoneySum = 0d;
        for (MongoOrder mongoOrder : mongoOrders) {
            Integer orderPrice = mongoOrder.getOrderPrice();
            if (Objects.nonNull(orderPrice)) {
                orderPriceSum += Double.valueOf(orderPrice);
            }
            String selfMoney = mongoOrder.getSelfMoney();
            if (StringUtils.isNotBlank(selfMoney)) {
                selfMoneySum += Double.valueOf(selfMoney);
            }
            String offlinePayMoney = mongoOrder.getOfflinePayMoney();
            if (StringUtils.isNotBlank(offlinePayMoney)) {
                offlinePayMoneySum += Double.valueOf(offlinePayMoney);
            }
        }
        List<List<String>> totalList = new ArrayList<>();
        totalList.add(Collections.emptyList());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        orderPriceSum = orderPriceSum / 100;
        totalList.add(Arrays.asList("总额",decimalFormat.format(orderPriceSum),
                decimalFormat.format(selfMoneySum), decimalFormat.format(offlinePayMoneySum), decimalFormat.format(itemsMoneySum)));
        totalList.add(Arrays.asList("退款",decimalFormat.format(refundTotalSum),
                decimalFormat.format(onlineRefundTotalSum),
                decimalFormat.format(offPayRefundTotalSum), decimalFormat.format(itemsRefundMoneySum)));
        totalList.add(Arrays.asList("合计",decimalFormat.format(orderPriceSum - refundTotalSum),
                decimalFormat.format(selfMoneySum - onlineRefundTotalSum),
                decimalFormat.format(offlinePayMoneySum - offPayRefundTotalSum), decimalFormat.format(itemsMoneySum - itemsRefundMoneySum)));
        return totalList;
    }

    public Double getItemsOriginalPriceSum(List<MongoOrder> mongoOrders){
        Double totalSum = Double.valueOf(new DecimalFormat("0.00").format(0d));
        if (CollectionUtils.isEmpty(mongoOrders)) {
            return totalSum;
        }
        for (MongoOrder mongoOrder : mongoOrders) {
            String itemsDetail = mongoOrder.getItemsDetail();
            if (StringUtils.isNotBlank(itemsDetail)) {
                List<ExamItemSnap> examItemSnaps = JSON.parseArray(itemsDetail, ExamItemSnap.class);
                Integer sumOriginalPrice = 0;
                for (ExamItemSnap examItemSnap : examItemSnaps) {
                    if (examItemSnap.getTypeToMeal() != ExamItemToMealEnum.outMeal.getCode()) {
                        sumOriginalPrice += examItemSnap.getOriginalPrice();
                    }
                }
                String sumOriginalPriceStr = MoneyUtil.formatMoney(sumOriginalPrice);
                totalSum += Double.valueOf(sumOriginalPriceStr);
            }
        }
        return totalSum;
    }

}
