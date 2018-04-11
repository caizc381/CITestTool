package com.mytijian.admin.web.controller.order.manager;

import com.mytijian.admin.web.controller.order.helper.OrderExportHelper;
import com.mytijian.offer.examitem.constant.enums.ExamItemToMealEnum;
import com.mytijian.offer.examitem.exception.ItemSelectException.ConflictType;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.model.ExamItemSnap;
import com.mytijian.offer.examitem.service.ExamItemService;
import com.mytijian.order.service.MongoOrderService;
import com.mytijian.order.service.OrderService;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Component("orderExportFileManager")
public class OrderExportFileManager {

    private final static Logger logger = LoggerFactory.getLogger(OrderExportFileManager.class);
    
    @Resource(name = "mongoOrderService")
    private MongoOrderService mongoOrderService;
    @Resource(name = "orderService")
    private OrderService      orderService;
    @Resource(name = "examItemService")
    private ExamItemService examItemService;
    @Resource(name = "orderExportHelper")
    private OrderExportHelper orderExportHelper;
    @Value("${temp.folder}")
    private String tempFolder;

    private final int HZLYY = 1;

    private final int ARMY117 = 2;
    
    public String exportMongoOrder(String templatePath, String tempFolder, List<Integer> orderIds,
                                   Integer hospitalId) throws Exception {
        List<Map> dboList = mongoOrderService.getMongoOrderByIds(orderIds);

        List<List<String>> orderList = new ArrayList<List<String>>();

        InputStream stream = getClass().getClassLoader().getResourceAsStream(getTemplate(templatePath, hospitalId));
        File targetFile = new File(UUID.randomUUID().toString()+".xls");
        FileUtils.copyInputStreamToFile(stream, targetFile);

        try {
        getOrderData(targetFile, hospitalId, dboList, orderList);
        } catch(Exception e) {
        	e.printStackTrace();
        	logger.error("订单导出为excel错误{}",e);
        }
        InputStream is = new FileInputStream(targetFile);

        String filename = tempFolder + getFilename(hospitalId) + ".xls";

        FileOutputStream out = new FileOutputStream(filename);

        HSSFWorkbook book = ExcelUtil.saveExcel(orderList, is);
        book.write(out);

        out.flush();
        out.close();

        logger.info(
                "Export Mongo Order successful! site =" + orderList.size() + " file: " + filename);

        return filename;
    }
    
    private static String getFilename(Integer hospitalId) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return hospitalId + "_" + format.format(Calendar.getInstance().getTime());
    }

    private String getTemplate(String templatePath, Integer hospitalId) {
        if (hospitalId.intValue() == HZLYY || hospitalId.intValue() == ARMY117) {
            return templatePath + File.separator + hospitalId.intValue() + ".xls";
        } else {
            return templatePath + File.separator + "mediator.xls";
        }
    }

    private void getOrderData(File file, Integer hospitalId, List<Map> dboList,
                              List<List<String>> orderList) throws FileNotFoundException {
        List<List<String>> template = null;
        try {
            template = ExcelUtil.analyzeExcel(file);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        orderList.add(template.get(0));

        List<String> keyList = template.get(1);

        for (Map dbObject : dboList) {

            List<String> eachOrder = new ArrayList<String>();

            Map<String, String> itemMap = new HashMap<String, String>();
            Integer orderId = new Integer(dbObject.get("id").toString());
            if (hospitalId != HZLYY && hospitalId != ARMY117) {
                try {
                    List<ExamItemSnap> snaps = orderService.getExamItemSnap(orderId);
                    itemMap = getAddedAndMinusItems(snaps, hospitalId);
                    String orderItems = getOrderItems(snaps, hospitalId);
                    itemMap.put("orderItems", orderItems);
                } catch (Exception e) {
                    logger.error("getAddedAndMinusItems error, orderId:"
                            + dbObject.get("id").toString() + ", hospitalId:" + hospitalId + ".",
                            e);
                }
            }

            for (int index = 0; index < keyList.size(); index++) {
                eachOrder.add(orderExportHelper.getValue(keyList.get(index), dbObject, hospitalId,
                        itemMap));
            }

            orderList.add(eachOrder);
        }

    }

    private Map<String, String> getAddedAndMinusItems(List<ExamItemSnap> snaps,
                                                      Integer hospitalId) {
        Map<String, String> map = new HashMap<String, String>();
        StringBuilder addedItems = new StringBuilder();
        StringBuilder minusItems = new StringBuilder();

        if (CollectionUtils.isEmpty(snaps)) {
            return map;
        }
        for (ExamItemSnap itemSnap : snaps) {
            // 减项
            if (itemSnap.getTypeToMeal() == ExamItemToMealEnum.outMeal.getCode()) {

                parseItem(minusItems, itemSnap, hospitalId);
                // 加项
            } else if (itemSnap.getTypeToMeal() == ExamItemToMealEnum.addToMeal.getCode()) {

                parseItem(addedItems, itemSnap, hospitalId);
            }
        }

        map.put("addItems", addedItems.toString());
        map.put("minusItems", minusItems.toString());
        return map;
    }

    private String getOrderItems(List<ExamItemSnap> snaps, Integer hospitalId) {
        StringBuilder orderItems = new StringBuilder();
        if (CollectionUtils.isNotEmpty(snaps)) {
            for (ExamItemSnap itemSnap : snaps) {
                // 加项
                if (itemSnap.getTypeToMeal() == ExamItemToMealEnum.addToMeal.getCode()) {
                    continue;
                }
                parseItem(orderItems, itemSnap, hospitalId);
            }
            return orderItems.toString();
        }
        return null;
    }

    private void parseItem(StringBuilder builder, ExamItemSnap itemSnap, Integer hospitalId) {
        // 组合项
        if (StringUtils.isBlank(itemSnap.getHisId())) {
            List<Integer> itemList = examItemService.getConflictItems(itemSnap.getId(),
                    ConflictType.COMPOSE);
            for (Integer id : itemList) {
                ExamItem examItem = examItemService.getExamItemByItemId(id).getExamItem();
                toItemString(builder, examItem);
            }
        } else {
            ExamItem examItem = examItemService.getExamItemByHospitalAndHisItemId(hospitalId,
                    itemSnap.getHisId());
            if (examItem == null) {
                return;
            }
            toItemString(builder, examItem);
        }
    }

    private void toItemString(StringBuilder builder, ExamItem examItem) {
        builder.append(examItem.getName()).append(" ").append("(").append(examItem.getHisItemId())
                .append(",").append("¥" + MoneyUtil.formatMoney(examItem.getPrice())).append(")")
                .append(System.lineSeparator());
    }
}
