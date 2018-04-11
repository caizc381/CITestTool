package com.mytijian.admin.web.controller.order;

import com.mytijian.admin.web.vo.order.OrderListVO;
import com.mytijian.order.base.mongo.MongoOrderReadService;
import com.mytijian.order.base.mongo.model.MongoOrderSelector;
import com.mytijian.order.dto.OrderListDTO;
import com.mytijian.order.enums.OrderStatusEnum;
import com.mytijian.order.model.MongoOrder;
import com.mytijian.order.params.OrderQueryParams;
import com.mytijian.order.service.OrderListService;
import com.mytijian.order.service.OrderManagerService;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.resource.service.OrganizationSettingsService;
import com.mytijian.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Administrator
 * @date 2017/11/21.
 */
public class OrderBaseController {

    // 订单金额统计分页每页的大小
    private static final Integer ORDER_AMOUNT_PAGE_SIZE = 200;
    @Resource(name = "mongoOrderReadService")
    protected MongoOrderReadService mongoOrderReadService;

    @Resource(name = "orderManagerService")
    protected OrderManagerService orderManagerService;

    @Resource(name = "organizationSettingsService")
    protected OrganizationSettingsService orgSettingsService;

    @Resource(name = "orderListService")
    protected OrderListService orderListService;

    @Autowired
    private HospitalService hospitalService;

    public List<MongoOrder> getMongoOrdersByOrderIdList(List<Integer> orderIdList, MongoOrderSelector mongoOrderSelector) {
        Integer ORDER_AMOUNT_PAGE_SIZE = 200;
        List<MongoOrder> mongoOrderList = new ArrayList<>();
        if (CollectionUtils.isEmpty(orderIdList)){
            return mongoOrderList;
        }
        int rowCount = orderIdList.size();
        int count = (rowCount / ORDER_AMOUNT_PAGE_SIZE) + (rowCount % ORDER_AMOUNT_PAGE_SIZE > 0 ? 1 : 0);
        IntStream.rangeClosed(1,count).forEach(i -> {
            int skip = i < 1 ? 0 : (i - 1) * ORDER_AMOUNT_PAGE_SIZE;
            List<Integer> orderIds = orderIdList.stream().skip(skip).limit(ORDER_AMOUNT_PAGE_SIZE).collect(Collectors.toList());
            List<MongoOrder> mongoOrders = mongoOrderReadService.listMongoOrderByOrderIds(orderIds, mongoOrderSelector);
            mongoOrderList.addAll(mongoOrders);
        });
        return mongoOrderList;
    }

    /**
     * 公共方法：处理查询参数
     * @param orderQueryParams 订单查询对象
     */
    public void handleExportableParam(OrderQueryParams orderQueryParams) {
        if (orderQueryParams.getShowExportable() != null && orderQueryParams.getShowExportable()) {
            List<Integer> hospitalIds = orderQueryParams.getHospitalIds();
            if (CollectionUtils.isEmpty(hospitalIds)) {
                return;
            }
            //仅显示可导
            orderQueryParams.setOrderStatus(OrderStatusEnum.appointmentSuccess.getCode());
            orderQueryParams.setOrderStatuses(Arrays.asList(OrderStatusEnum.appointmentSuccess.getCode()));
            orderQueryParams.setIsExport(false);

            HospitalSettings hospitalSettings = orgSettingsService.getHospitalSettingsByHospitalId(hospitalIds.get(0));
            Integer previousExportDays = hospitalSettings.getPreviousExportDays();
            //根据配置的可导时间，计算体检时间段
            Date today = DateUtils.toDayStartSecond(new Date());
            if (previousExportDays == null) {
                if (orderQueryParams.getInsertStartDate() != null
                        && orderQueryParams.getInsertEndDate() != null) {
                    orderQueryParams.setExamStartDate(today);
                } else {
                    if (today.after(orderQueryParams.getExamStartDate())) {
                        orderQueryParams.setExamStartDate(today);
                    }
                }
            } else {
                Date examEndDate = orderManagerService.getExportXlsEndDate(hospitalSettings);
                if (orderQueryParams.getInsertStartDate() != null
                        && orderQueryParams.getInsertEndDate() != null) {
                    orderQueryParams.setExamStartDate(today);
                    orderQueryParams.setExamEndDate(examEndDate);
                } else {
                    if (today.after(orderQueryParams.getExamStartDate())) {
                        orderQueryParams.setExamStartDate(today);
                    }
                    if (examEndDate.before(orderQueryParams.getExamEndDate())) {
                        orderQueryParams.setExamEndDate(examEndDate);
                    }
                }
            }
        }
        handleFromSite(orderQueryParams);
    }

    private void handleFromSite(OrderQueryParams orderQueryParams) {
        if (CollectionUtils.isNotEmpty(orderQueryParams.getFromSites()) && orderQueryParams.getFromSites().contains(-1)) {
            List<Integer> channelIds = hospitalService.getOrganizationList(OrganizationTypeEnum.CHANNEL.getCode())
                    .stream()
                    .filter(hospital -> hospital.getEnable().equals(Hospital.STATUS_ENABLE))
                    .map(hospital -> hospital.getId())
                    .collect(Collectors.toList());
            orderQueryParams.setFromSites(channelIds);
        }
    }

    /**
     * 公共方法：根据订单查询对象分页查询订单
     * @param orderQueryParams 订单查询对象
     * @param page 分页对象
     * @param mongoOrderSelector 选择器
     * @return
     */
    public PageView<MongoOrder> getMongoOrdersByOrderQueryParams(OrderQueryParams orderQueryParams, Page page, MongoOrderSelector mongoOrderSelector) {
        int rowCount = mongoOrderReadService.countBaseMongoOrder(orderQueryParams);
        if (Objects.isNull(page)) {
            List<MongoOrder> mongoOrderList = new ArrayList<>();
            int count = (rowCount / ORDER_AMOUNT_PAGE_SIZE) + (rowCount % ORDER_AMOUNT_PAGE_SIZE > 0 ? 1 : 0);
            IntStream.rangeClosed(1,count).forEach(i -> {
                Page newPage = new Page(i,ORDER_AMOUNT_PAGE_SIZE);
                newPage.setRowCount(rowCount);
                PageView<MongoOrder> mongoOrderPageView = mongoOrderReadService.listMongoOrder(orderQueryParams, newPage, mongoOrderSelector);
                mongoOrderList.addAll(mongoOrderPageView.getRecords());
            });
            return new PageView<>(mongoOrderList, null);
        }else{
            page.setRowCount(rowCount);
            return mongoOrderReadService.listMongoOrder(orderQueryParams, page, mongoOrderSelector);
        }
    }

    /**
     * 转换OrderListDTO为OrderListVO
     * @param orderListDTOS
     * @return
     */
    public List<OrderListVO> ConvertOrderListDTOToOrderListVO(List<OrderListDTO> orderListDTOS){
        List<OrderListVO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderListDTOS)) {
            orderListDTOS.forEach(orderListDTO -> {
                OrderListVO orderListVO = new OrderListVO();
                BeanUtils.copyProperties(orderListDTO.getMongoOrder(),orderListVO);
                orderListVO.setHasSettlementOpen(orderListDTO.getHasSettlementOpen());
                orderListVO.setRefundScene(orderListDTO.getRefundScene());
                result.add(orderListVO);
            });
        }
        return result;
    }
}
