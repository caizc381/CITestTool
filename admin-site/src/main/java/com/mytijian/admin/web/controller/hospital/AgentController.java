package com.mytijian.admin.web.controller.hospital;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import com.mytijian.mediator.order.enums.OrderExportState;
import com.mytijian.order.base.mongo.MongoOrderReadService;
import com.mytijian.order.base.mongo.model.MongoOrderSelector;
import com.mytijian.order.enums.OrderStatusEnum;
import com.mytijian.order.model.MongoOrder;
import com.mytijian.order.model.Order;
import com.mytijian.order.params.OrderQueryParams;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.admin.api.monitor.AgentInfoService;
import com.mytijian.base.page.Page;
import com.mytijian.base.page.PageView;
import com.mytijian.order.service.MongoOrderService;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.util.AssertUtil;

@RestController
public class AgentController {

	static final Logger logger = LoggerFactory.getLogger(AgentController.class);

	@Resource(name = "mongoOrderService")
	private MongoOrderService mongoOrderService;

	@Resource(name = "mongoOrderReadService")
	public MongoOrderReadService mongoOrderReadService;

	@Resource(name = "agentInfoService")
	private AgentInfoService agentInfoService;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;

	/**
	 * 获取医院agent 信息(分页了)
	 * 
	 * @param page
	 *            page对象
	 * @return pageview对象（包含page和rocords）
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/manage/agentInfo", method = RequestMethod.GET)
	@ResponseBody
	public PageView<Map> getAgentInfo(Page page) {

		return agentInfoService.getAgentInfo(page);
	}

	/**
	 * 获取医院异常订单
	 * 
	 * @param hospitalId
	 *            医院id
	 * @return list<Map>对象，为mongo中存在的agent对象
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/manage/abnormalOrders", method = RequestMethod.GET)
	@ResponseBody
	public List<MongoOrder> getAbnormalOrders(Integer hospitalId) {
		// 条件 status = 10或者exportState，其中1,2，3 and examdate in [now-提前导出天数，+∞]
		//String query = getQuery(hospitalId);

		//return mongoOrderService.getMongoOrder(query);
		return getQuery(hospitalId);
	}

	/**
	 * 前端定时任务获取agent状态，通过查看order信息，用来给前端更新agent状态
	 * 
	 * @param page
	 *            page 对象
	 * @return pageview对象（包含page和rocords）
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/manage/updateAgentStatus", method = RequestMethod.GET)
	@ResponseBody
	public PageView<Map> getAbnormalAgentInfos(Page page) {

		PageView<Map> agentMap = agentInfoService.getAgentInfo(page);
		// 获取医院ID并去重
		Set<Integer> hospitalIds = new HashSet<>();
		for (Map map : agentMap.getRecords()) {
			String hospitalId = map.get("hospitalId").toString();
			if (StringUtils.isNotBlank(hospitalId)) {
				hospitalIds.add(Integer.valueOf(hospitalId));
			}
		}
		//查询医院设置提前导出天数 & 获取异常状态的医院
		List<Integer> exceptionHospitalIds = new ArrayList<>();
		List<HospitalSettings> hospitalSettings = hospitalService.getHospitalSettingByIds(new ArrayList<>(hospitalIds));
		if (CollectionUtils.isNotEmpty(hospitalIds)) {
			Map<Integer, Date> dateMap = hospitalSettings.stream().collect(Collectors.toMap(HospitalSettings::getHospitalId, settings -> {
				Integer previousExportDays = settings.getPreviousExportDays();
				return Objects.isNull(previousExportDays) ? fromDate(1) : fromDate(previousExportDays);
			}));
			for (Integer hospitalId : dateMap.keySet()) {
				Date date = dateMap.get(hospitalId);
				//查询异常的订单
				if (hasException(hospitalId, date)) {
					// 有异常
					exceptionHospitalIds.add(hospitalId);
				}
			}
		}
		//设置异常状态值
		for (Map map : agentMap.getRecords()) {
			String hospitalId = map.get("hospitalId").toString();
			if (exceptionHospitalIds.contains(Integer.valueOf(hospitalId))){
				// 有异常
				map.put("status", 2);
			}
		}
		return agentMap;
	}

	private List<MongoOrder> getQuery(Integer hospitalId) {
		Date fromDate = null;
		HospitalSettings settings = hospitalService.getHospitalSettingsById(hospitalId);
		if (settings.getPreviousExportDays() == null) {
			fromDate = fromDate(1);
		} else {
			fromDate = fromDate(settings.getPreviousExportDays());
		}

		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String query = "{\"$or\":[{ \"status\":{\"$in\":[10]}},{\"$or\":[{\"exportState\":1},{\"exportState\":2},{\"exportState\":3}]}],\"hospital._id\":{\"$eq\":"
				+ hospitalId + "},\"examDate\":{\"$gte\":{\"$date\":\"" + sdf.format(fromDate) + "T16:00:00.000Z\"}}}";*/
		return getPreviousExportMongoOrders(hospitalId, fromDate);

	}

	/**
	 * 查询提前导出的订单
	 * @param hospitalId
	 * @param fromDate
	 * @return
	 */
	private List<MongoOrder> getPreviousExportMongoOrders(Integer hospitalId, Date fromDate) {
		// 订单状态+医院+体检日期
		OrderQueryParams orderQueryParams = new OrderQueryParams();
		orderQueryParams.setHospitalIds(Collections.singletonList(hospitalId));
		orderQueryParams.setExamStartDate(fromDate);
		orderQueryParams.setOrderStatuses(Collections.singletonList(OrderStatusEnum.exportFailed.getCode()));
		com.mytijian.pulgin.mybatis.pagination.PageView<MongoOrder> orderPageView = getMongoOrdersByOrderQueryParams(orderQueryParams, null, null);
		// 导出状态+医院+体检日期
		OrderQueryParams queryParams = new OrderQueryParams();
		queryParams.setHospitalIds(Collections.singletonList(hospitalId));
		queryParams.setExamStartDate(fromDate);
		queryParams.setExportStatuses(Arrays.asList(OrderExportState.ThrowExceptionWhenExport.getCode(),OrderExportState.ImportedIntoIntermediateTable.getCode(),OrderExportState.UnImportedIntoHis.getCode()));
		com.mytijian.pulgin.mybatis.pagination.PageView<MongoOrder> pageView = getMongoOrdersByOrderQueryParams(queryParams, null, null);
		// 获取合集去重和排序
		List<MongoOrder> result = new ArrayList<>();
		result.addAll(orderPageView.getRecords());
		result.addAll(pageView.getRecords());
		return result.stream().sorted(Comparator.comparing(MongoOrder::getInsertTime).reversed()).filter(distinctByKey(Order::getOrderNum)).collect(Collectors.toList());
	}

	/**
	 * 查询提前导出的订单
	 * @param hospitalId
	 * @param fromDate
	 * @return
	 */
	private boolean hasException(Integer hospitalId, Date fromDate) {
		// 订单状态+医院+体检日期
		OrderQueryParams orderQueryParams = new OrderQueryParams();
		orderQueryParams.setHospitalIds(Collections.singletonList(hospitalId));
		orderQueryParams.setExamStartDate(fromDate);
		orderQueryParams.setOrderStatuses(Collections.singletonList(OrderStatusEnum.exportFailed.getCode()));
		int rowCount = mongoOrderReadService.countBaseMongoOrder(orderQueryParams);
		if (rowCount > 0) {
			return true;
		}
		// 导出状态+医院+体检日期
		OrderQueryParams queryParams = new OrderQueryParams();
		queryParams.setHospitalIds(Collections.singletonList(hospitalId));
		queryParams.setExamStartDate(fromDate);
		queryParams.setExportStatuses(Arrays.asList(OrderExportState.ThrowExceptionWhenExport.getCode(),OrderExportState.ImportedIntoIntermediateTable.getCode(),OrderExportState.UnImportedIntoHis.getCode()));
		int exportStatusesRowCount = mongoOrderReadService.countBaseMongoOrder(queryParams);
		if (exportStatusesRowCount > 0) {
			return true;
		}
		return false;
	}

	public com.mytijian.pulgin.mybatis.pagination.PageView<MongoOrder> getMongoOrdersByOrderQueryParams(OrderQueryParams orderQueryParams, com.mytijian.pulgin.mybatis.pagination.Page page, MongoOrderSelector mongoOrderSelector) {
		int rowCount = mongoOrderReadService.countBaseMongoOrder(orderQueryParams);
		// 订单金额统计分页每页的大小
		if (Objects.isNull(page)) {
			Integer ORDER_AMOUNT_PAGE_SIZE = 200;
			List<MongoOrder> mongoOrderList = new ArrayList<>();
			int count = (rowCount / ORDER_AMOUNT_PAGE_SIZE) + (rowCount % ORDER_AMOUNT_PAGE_SIZE > 0 ? 1 : 0);
			IntStream.rangeClosed(1,count).forEach(i -> {
				com.mytijian.pulgin.mybatis.pagination.Page newPage = new com.mytijian.pulgin.mybatis.pagination.Page(i, ORDER_AMOUNT_PAGE_SIZE);
				newPage.setRowCount(rowCount);
				com.mytijian.pulgin.mybatis.pagination.PageView<MongoOrder> mongoOrderPageView = mongoOrderReadService.listMongoOrder(orderQueryParams, newPage, mongoOrderSelector);
				mongoOrderList.addAll(mongoOrderPageView.getRecords());
			});
			return new com.mytijian.pulgin.mybatis.pagination.PageView<>(mongoOrderList, null);
		}else{
			page.setRowCount(rowCount);
			return mongoOrderReadService.listMongoOrder(orderQueryParams, page, mongoOrderSelector);
		}
	}
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
	{
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}


	private Date fromDate(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.DATE, -days);

		return calendar.getTime();
	}
	
}
