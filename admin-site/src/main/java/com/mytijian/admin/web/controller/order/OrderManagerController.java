package com.mytijian.admin.web.controller.order;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.account.exceptions.LoginFailedException;
import com.mytijian.account.model.Account;
import com.mytijian.account.model.AccountRelationInCrm;
import com.mytijian.account.service.LoginService;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.web.controller.order.constant.ManageAppExceptionEnum;
import com.mytijian.admin.web.controller.order.constant.ManageAppLogConstant;
import com.mytijian.admin.web.controller.order.manager.NewOrderExportFileManager;
import com.mytijian.admin.web.controller.order.manager.OrderExportFileManager;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.admin.web.vo.order.OrderListVO;
import com.mytijian.uic.annotation.LoginRequired;
import com.mytijian.calculate.CalculatorServiceEnum;
import com.mytijian.uic.util.LoginUtil;
import com.mytijian.counter.exception.CounterLimitException;
import com.mytijian.exception.BizException;
import com.mytijian.gotone.api.SmsService;
import com.mytijian.gotone.api.model.SmsBatchResp;
import com.mytijian.gotone.api.model.SmsWithContentReq;
import com.mytijian.offer.examitem.model.ExamItemSnap;
import com.mytijian.offer.meal.model.MealSnap;
import com.mytijian.order.base.mongo.model.MongoOrderSelector;
import com.mytijian.order.base.service.*;
import com.mytijian.order.base.service.dto.OrderRefundApplyQueryDTO;
import com.mytijian.order.base.service.dto.OrderRefundApplyRecordDTO;
import com.mytijian.order.base.service.dto.OrderRefundApplyResultDTO;
import com.mytijian.order.base.service.dto.OrderRevokeDTO;
import com.mytijian.order.base.service.model.OrderChangeExamDateWrapper;
import com.mytijian.order.base.service.model.OrderRefundApply;
import com.mytijian.order.base.service.model.OrderRevokeWrapper;
import com.mytijian.order.base.service.model.UpdateOrderWrapper;
import com.mytijian.order.base.snapshot.model.ExamItemSnapshot;
import com.mytijian.order.base.snapshot.model.HospitalSnapshot;
import com.mytijian.order.base.snapshot.model.OrderMealSnapshot;
import com.mytijian.order.dto.OrderListDTO;
import com.mytijian.order.dto.OrderManageDto;
import com.mytijian.order.enums.OperateAppEnum;
import com.mytijian.order.enums.OrderStatusEnum;
import com.mytijian.order.exception.OrderException;
import com.mytijian.order.model.MongoOrder;
import com.mytijian.order.model.Order;
import com.mytijian.order.params.OrderQueryParams;
import com.mytijian.order.result.OrderCanUnexportResult;
import com.mytijian.order.service.*;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.trade.pay.constant.PayConstants;
import com.mytijian.trade.refund.model.TradeRefundRecord;
import com.mytijian.trade.refund.service.UnifyRefundQueryService;
import com.mytijian.util.AssertUtil;
import com.mytijian.util.DateUtils;
import com.mytijian.web.filter.ResponseWrapper;
import com.mytijian.web.intercepter.Token;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Controller
public class OrderManagerController extends OrderBaseController {
	
	private final static Logger log = LoggerFactory.getLogger(OrderController.class);
	private final static Integer ORDER_CHECK_ALL_MAX_RANGE = 1000;

	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "loginService")
	private LoginService loginService;

	@Resource
	private OrderRevokeService orderRevokeService;
	
	@Resource(name = "orderManagerService")
	private OrderManagerService orderManagerService;
	
	@Resource(name = "orderRefundLogService")
	private OrderRefundLogService orderRefundLogService;

	@Resource(name = "orderRefundApplyService")
	private OrderRefundApplyService orderRefundApplyService;

	@Resource(name = "unifyRefundQueryService")
	private UnifyRefundQueryService unifyRefundQueryService;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	@Autowired
	private OrderReadService orderReadService;
	
	@Value("${temp.folder}")
	private String tempFolder;

	@Resource(name = "smsService")
    private SmsService smsService;

	@Resource(name = "orderExportFileManager")
	private OrderExportFileManager orderExportFileManager;

	@Resource(name = "orderLifeCycleService")
	private OrderLifeCycleService orderLifeCycleService;

	@Resource
	private NewOrderExportFileManager newOrderExportFileManager;
	
	@Resource(name = "orderDeleteService")
	private OrderDeleteService orderDeleteService;

	@Resource
	private OrderExportService orderExportService;

	
	/**
	 * <pre>
	 * 获取体检人订单集合
	 * @param accountId 体检人
	 * @param page 分页信息
	 * @param session 通过session中的token拿到用户信息
	 * @return 含有分页信息的订单集合
	 */
	@RequestMapping(value = "/order", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public PageView<OrderManageDto> getOrderList(@RequestParam("accountId") Integer accountId, Page page,
                                                 HttpSession session) {
		return orderManagerService.getOrderManageList(accountId, page);
	}
	
	/**
	 * <pre>
	 * 获取订单详情
	 * @param orderId 订单id
	 * @param session 通过session中的token拿到用户信息
	 * @return 订单、单项快照以及价格
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/orderDetail", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public Map<String, Object> getOrder(@RequestParam Integer orderId, HttpSession session) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Integer, ExamItemSnap> itemSnap = orderService.getExamItemsDetail(orderId);
		Order relOrder = orderService.getNormalOrderByOrderId(orderId);
		MealSnap mealSnap = JSON.parseObject(relOrder.getMealDetail(), MealSnap.class);
		Map<String, Integer> otherPriceMap = mealSnap.getOtherMoneys();
		map.putAll(orderRefundLogService.countOrderRefund(orderId));
		map.put("locale", orderRefundLogService.getlocaleAddItems(orderId));
		if (relOrder.getHospital()!=null) {
			// 拒检项目是否退款
			map.put("refundRefused", hospitalService.getHospitalSettingsById(relOrder.getHospital().getId()).getRefundRefusedItem());
		}
		map.put("mealDiscount", AssertUtil.isNull(mealSnap)?"":mealSnap.getDiscount());
		setMongoOrderInfo(orderId, map);
		map.put("itemSnap", itemSnap);
		if(otherPriceMap != null){
			map.put("examItemPrice", otherPriceMap.get("体检项目"));
		}
		return map;
	}

	private void setMongoOrderInfo(@RequestParam Integer orderId, Map<String, Object> map) {
		//初始化null（如果不返回map的key前端说总要判断麻烦）
		map.put("order", null);
		map.put("guideInfo", null);
		map.put("refundFlag", null);
		map.put("refundPrice", null);
		map.put("customerPayRefund", null);
		map.put("addItemPriceSum", null);
		map.put("refundPriceSum", null);
		map.put("summation", null);
		map.put("refundItemsClassifySnap",null);
		map.put("manualRefundLog",null);
		map.put("currency","元");

		MongoOrder orderMongo = mongoOrderReadService.getMongoOrderByOrderId(orderId,null);
		if(Objects.nonNull(orderMongo)){
			OrderRefundApplyRecordDTO orderRefundApplyRecord = orderRefundApplyService.getOrderRefundApplyRecord(orderMongo.getOrderNum());
			map.put("manualRefundLog",orderRefundApplyRecord);
			map.put("order", orderMongo);
			putRefundInfo(map, orderMongo);
			map.put("guideInfo", orderMongo.getGuideInfo());
			//设置进度单位
			int hospitalId;
			HospitalSnapshot hospitalSnapshot = orderMongo.getOrderHospital();
			if(hospitalSnapshot != null){
				hospitalId = hospitalSnapshot.getId();
			}else if(orderMongo.getHospital() != null){
				hospitalId = orderMongo.getHospital().getId();
			}else{
				hospitalId = orderMongo.getOrderMealSnapshot().getMealSnapshot().getOriginMeal().getHospitalId();
			}
			HospitalSettings hospitalSettings = hospitalService.getHospitalSettingsById(hospitalId);
			if (Objects.nonNull(hospitalSettings)) {
				String calculatorService = hospitalSettings.getCalculatorService();
				if (StringUtils.equals(calculatorService, CalculatorServiceEnum.YUAN_ROUND_CALCULATOR.getName())) {
					map.put("currency", "元");
				}
				if (StringUtils.equals(calculatorService, CalculatorServiceEnum.JIAO_ROUND_CALCULATOR.getName())) {
					map.put("currency", "角");
				}
				if (StringUtils.equals(calculatorService, CalculatorServiceEnum.FEN_ROUND_CALCULATOR.getName())) {
					map.put("currency", "分");
				}
				if (StringUtils.isBlank(calculatorService)) {
					map.put("currency", "元");
				}
			}
		}
	}

	private void putRefundInfo(Map<String, Object> map,MongoOrder mongoOrder) {
		//1设置退款金额&设置退款标示[预退款，退款]&设置客户支付的退还金额
		map.put("refundFlag", 1);//退款预退款标示：0预退款,1退款
		map.put("refundPrice", 0);//（预）退款金额
		OrderRefundApply orderRefundApply = getOrderRefundApply(mongoOrder.getOrderNum());
		if (Objects.nonNull(orderRefundApply)) {
			Integer status = orderRefundApply.getStatus();
			Integer amount = orderRefundApply.getAmount();
			if (Objects.equals(status, 0)) {//0: "待审核",1: "已同意",2: "已拒绝"
				map.put("refundFlag", 0);//退款预退款标示：0预退款,1退款
			}
			map.put("refundPrice", Objects.isNull(amount) || Objects.equals(status, 2) ? 0 : amount);
		}
		//计算客户支付的退还
		List<TradeRefundRecord> tradeRefundRecords = unifyRefundQueryService.listRefundRecordByOrderNums(Collections.singletonList(mongoOrder.getOrderNum()));
		if (CollectionUtils.isNotEmpty(tradeRefundRecords)) {
			long refundAmountSum = tradeRefundRecords.stream().filter(tradeRefundRecord -> {
				Integer tradeMethodType = tradeRefundRecord.getTradeMethodType();
				return Objects.equals(tradeMethodType, PayConstants.PayMethod.Alipay) || Objects.equals(tradeMethodType, PayConstants.PayMethod.AlipayScan) ||
						Objects.equals(tradeMethodType, PayConstants.PayMethod.Wxpay) || Objects.equals(tradeMethodType, PayConstants.PayMethod.WxpayScan) ||
						Objects.equals(tradeMethodType, PayConstants.PayMethod.Balance);
			}).mapToLong(TradeRefundRecord::getRefundAmount).sum();
			map.put("customerPayRefund", Integer.valueOf(String.valueOf(refundAmountSum)));
		}

		//2获取现场加项，原订单拒检和未检
		List<ExamItemSnapshot> refundItemsClassify = mongoOrder.getRefundItemsClassify();
		if (CollectionUtils.isNotEmpty(refundItemsClassify)) {
			//加项金额统计
			int addItemPriceSum = refundItemsClassify.stream().filter(examItemSnapshot -> Objects.equals(examItemSnapshot.getRefundState(), 2) && Objects.nonNull(examItemSnapshot.getPrice())).mapToInt(ExamItemSnapshot::getPrice).sum();
			int refundPriceSum = refundItemsClassify.stream().filter(examItemSnapshot -> Objects.equals(examItemSnapshot.getRefundState(), 1) && Objects.nonNull(examItemSnapshot.getPrice())).mapToInt(ExamItemSnapshot::getPrice).sum();
			map.put("addItemPriceSum", addItemPriceSum);
			map.put("refundPriceSum", refundPriceSum);
			map.put("summation", addItemPriceSum - refundPriceSum);
			List<ExamItemSnapshot> refundItemsClassifySnapList = refundItemsClassify.stream().filter(distinctByKey(ExamItemSnap::getId)).collect(Collectors.toList());
			Map<Integer, ExamItemSnapshot> refundItemsClassifySnapMap = refundItemsClassifySnapList.stream().collect(Collectors.toMap(ExamItemSnapshot::getId, Function.identity()));
			map.put("refundItemsClassifySnap",refundItemsClassifySnapMap);
		}
	}

	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>(8);
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private OrderRefundApply getOrderRefundApply(String orderNum){
		if (StringUtils.isBlank(orderNum)) {
			return null;
		}
		OrderRefundApplyQueryDTO orderRefundApplyQueryDTO = new OrderRefundApplyQueryDTO();
		orderRefundApplyQueryDTO.setOrderNums(Collections.singletonList(orderNum));
		OrderRefundApplyResultDTO orderRefundApplyList = orderRefundApplyService.getOrderRefundApplyList(orderRefundApplyQueryDTO);
		if (Objects.nonNull(orderRefundApplyList) && CollectionUtils.isNotEmpty(orderRefundApplyList.getOrderRefundApplyList())) {
			return orderRefundApplyList.getOrderRefundApplyList().stream().sorted(Comparator.comparing(OrderRefundApply::getApplyTime).reversed()).findFirst().get();
		}
		return null;
	}
	
	/**
	 * <pre>
	 * 获取订单单项
	 * @param orderNum 订单编号
	 */
	@RequestMapping(value="/orderExamItems/{orderNum}", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public List<Integer> getOrderItemsByOrderId(@PathVariable("orderNum") String orderNum,
			HttpSession session){
		
		OrderMealSnapshot snap = orderReadService.getOrderByOrderNum(orderNum).getOrderMealSnapshot();
		List<ExamItemSnapshot> items = snap.getExamItemSnapList();
		
		List<Integer> ids = new ArrayList<>();
		
		for (ExamItemSnapshot item : items) {
			ids.add(item.getId());
		}
		return ids;
	}
	
	/**
	 * <pre>
	 * 订单撤销
	 * @param orderId 订单id
	 * @param sendMsg 是否发送短信
	 * @param session 通过session中的token拿到用户信息
	 * @throws OrderException
	 * @throws com.mytijian.order.base.exceptions.OrderException 
	 */
	@RequestMapping(value = "/revoke", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	@LoginRequired
	@Token
	public void revokeOrder(@RequestParam("orderId") Integer orderId,
			@RequestParam("sendMsg")boolean sendMsg,
			HttpSession session) throws OrderException, com.mytijian.order.base.exceptions.OrderException {
//		String token = (String) session.getAttribute(LoginService.TOKEN_NAME);
		Account account = new Account();
		account.setId(-1);
//		
		OrderRevokeWrapper orderRevokeWrapper = new OrderRevokeWrapper();
		orderRevokeWrapper.setOrderId(orderId);
		orderRevokeWrapper.setOperator(account);
		orderRevokeWrapper.setSystem(OperateAppEnum.OPS.getCode());
		orderRevokeWrapper.setNeedCheckStatus(false);
		orderRevokeWrapper.setSendMsg(sendMsg);
		orderLifeCycleService.revokeOrder(orderRevokeWrapper);
	}
	
	/**
	 * <pre>
	 * 批量撤单订单
	 * @param orderIds 订单id集合
	 * @param sendMsg 是否发送短信
	 * @param session 通过session中的token拿到用户信息
	 * @return 无返回值
	 * @throws Exception
	 */
	@RequestMapping(value = "/revokeorder", method = RequestMethod.POST)
	@LoginRequired
	@ResponseStatus(HttpStatus.OK)
	@Token("order")
	public void revokeOrderBatch(@RequestParam("orderIds[]") Integer[] orderIds,
                                 @RequestParam(value = "sendMsg", required = false) boolean sendMsg, HttpSession session) throws OrderException {
		
		// TODO manage 下线 使用ops用户 替代 Account 然后System使用OperateAppEnum.OPS
		//Account operator = LoginUtil.getCurrentUser(session);
		
		Employee employee = SessionUtil.getEmployee();
		Account operator = new Account();
		operator.setId(employee.getId());
		operator.setName(employee.getEmployeeName());

		log.debug("==================manager revokeOrdersbatch start {}================== orderids is {},proxy is {}",new Date(),orderIds, operator);
//		revokeOrderService.revokeOrder(Arrays.asList(orderIds), operator.getId(), sendMsg, operator);
		OrderRevokeDTO orderRevokeDTO = new OrderRevokeDTO();
		//是否需要检查订单状态
		orderRevokeDTO.setNeedCheckStatus(false);
		orderRevokeDTO.setNeedSendMsg(sendMsg);
		orderRevokeDTO.setOrderIds(Arrays.asList(orderIds));
		orderRevokeDTO.setOperator(operator);
		orderRevokeDTO.setSystem(OperateAppEnum.OPS.getCode());
		
		orderRevokeService.revokeOrders(orderRevokeDTO);
		log.info("event:撤销订单 orderids:{}, sendMsg:{}", orderIds, sendMsg);
		log.debug("==================manager revokeOrdersbatch end {}==================",new Date());
	}

	
	/**
	 * <pre>
	 * 订单改期
	 * @param orderId 订单id
	 * @param newExamDate 新日期
	 * @param intervalId 时间段
	 * @param session 通过session中的token拿到用户信息
	 * @return 空或者受限订单id集合
	 * @throws Exception
	 */
	@RequestMapping(value = "/changeExamDate", method = RequestMethod.POST)
	@ResponseBody
	@LoginRequired
	@Token("order")
	public Map<String, List<Integer>> changeExamDate(Integer orderId,
                                                     @RequestParam("examDate") String newExamDate, @RequestParam("intervalId") Integer intervalId,
                                                     HttpSession session) throws Exception {
//		String token = (String) session.getAttribute(LoginService.TOKEN_NAME);
//		Account account = loginService.getAccount(token);
		Account account =new Account();
		account.setId(-1);
		Date examDate = DateUtils.parse("yyyy-MM-dd", newExamDate);
		List<Integer> sameOrderIds = new ArrayList<Integer>();
		List<Integer> limitOrderIds = new ArrayList<Integer>();
		Map<String, List<Integer>> result= new HashMap<String, List<Integer>>();
		try {
			log.debug("==================manager changeExamDate start {}================== orderId is {}",new Date(),orderId);
			OrderChangeExamDateWrapper orderChangeExamDateWrapper = new OrderChangeExamDateWrapper();
			orderChangeExamDateWrapper.setExamTimeIntervalId(intervalId);
			orderChangeExamDateWrapper.setOrderId(orderId);
			orderChangeExamDateWrapper.setOperator(account);
			orderChangeExamDateWrapper.setNewExamDate(examDate);
			orderChangeExamDateWrapper.setSystem(OperateAppEnum.OPS.getCode());
			orderLifeCycleService.changeExamDate(orderChangeExamDateWrapper);
			log.debug("==================manager changeExamDate end {}==================",new Date());
		} catch (com.mytijian.order.base.exceptions.OrderException ex) {
			sameOrderIds.add(orderId);
			result.put("sameOrderId", sameOrderIds);
		} catch(Exception ex){
			if(ex instanceof CounterLimitException){
				limitOrderIds.add(orderId);
				result.put("limitOrderId", limitOrderIds);
			}else{
				throw ex;
			}
		}
		return result.isEmpty() ? null : result;
	}
	
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/queryOrder", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
	public PageView<OrderListVO> queryOrder(@RequestBody OrderQueryParams orderQueryParams) {
		handleExportableParam(orderQueryParams);
		PageView<MongoOrder> orderPageView = getMongoOrdersByOrderQueryParams(orderQueryParams, orderQueryParams.getPage(), null);
		List<OrderListDTO> orderListDTOS = orderListService.buildMongoOrderExtInfo(orderPageView.getRecords());
		List<OrderListVO> orderListVOS = this.ConvertOrderListDTOToOrderListVO(orderListDTOS);
		return new PageView<>(orderListVOS,orderPageView.getPage());
	}

	@RequestMapping(value = "/queryAllOrderInfo", method = RequestMethod.POST)
	@ResponseBody
	@LoginRequired
	public List<MongoOrder> queryAllOrderInfo(@RequestBody OrderQueryParams orderQueryParams)
			throws Exception {
		handleExportableParam(orderQueryParams);
		int rowCount = mongoOrderReadService.countBaseMongoOrder(orderQueryParams);
		if (rowCount > ORDER_CHECK_ALL_MAX_RANGE) {
			throw new OrderException(OrderException.ExceptionType.queryOrderOutOfRange, OrderException.ExceptionType.queryOrderOutOfRange.getMessage());
		}
		MongoOrderSelector mongoOrderSelector = new MongoOrderSelector();
		mongoOrderSelector.setOrderBaseInfo(true);
		mongoOrderSelector.setAccountRelation(true);
		mongoOrderSelector.setOrderExamDate(true);
		mongoOrderSelector.setOrderAccount(true);
		PageView<MongoOrder> orderPageView = getMongoOrdersByOrderQueryParams(orderQueryParams, null, mongoOrderSelector);
		return orderPageView.getRecords();
	}
    
    @RequestMapping(value = "/batchSendMsg", method = RequestMethod.POST)
    @LoginRequired
    @ResponseStatus(HttpStatus.OK)
    public void batchSendMsg(@RequestParam(required = true) String orderIds,
                             @RequestParam(required = true) Integer hospitalId,
                             @RequestParam(required = true) String msg, HttpSession session)
                                     throws OrderException {
		OrderQueryParams orderQueryParams = new OrderQueryParams();
		orderQueryParams.setOrderIds(JSON.parseArray(orderIds, Integer.class));
		orderQueryParams.setHospitalIds(Collections.singletonList(hospitalId));
		int rowCount = mongoOrderReadService.countBaseMongoOrder(orderQueryParams);
		if (rowCount > ORDER_CHECK_ALL_MAX_RANGE) {
			throw new OrderException(OrderException.ExceptionType.queryOrderOutOfRange, OrderException.ExceptionType.queryOrderOutOfRange.getMessage());
		}
		MongoOrderSelector mongoOrderSelector = new MongoOrderSelector();
		mongoOrderSelector.setAccountRelation(true);
		PageView<MongoOrder> orderPageView = getMongoOrdersByOrderQueryParams(orderQueryParams, null, mongoOrderSelector);
		List<MongoOrder> mongoOrders = orderPageView.getRecords();
		if (CollectionUtils.isNotEmpty(mongoOrders)) {
			List<SmsWithContentReq> smsWithContentReqList = new ArrayList<>();
			mongoOrders.forEach(mongoOrder -> {
				AccountRelationInCrm accountRelation = mongoOrder.getAccountRelation();
				if (Objects.nonNull(accountRelation) && Objects.nonNull(accountRelation.getMobile())) {
					SmsWithContentReq req = new SmsWithContentReq();
					req.setMobile(accountRelation.getMobile());
					req.setContent(msg);
					smsWithContentReqList.add(req);
				}
			});

			if (CollectionUtils.isNotEmpty(smsWithContentReqList)) {
				SmsBatchResp smsBatchResp = smsService.sendBatchWithContent(smsWithContentReqList);
				log.info("hospital {},orderIds {}, manager batchSendMsg sendBatchWithContent return result is:{}",hospitalId,orderIds,JSON.toJSONString(smsBatchResp));
			}
		}
    }
    
    /**
     * 获取可以导出至体检软件的订单
     */
    @RequestMapping(value = "/getOrderCanExport", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Map<String, Object> getOrderCanExport(@RequestParam(required = true) String orderIds,
                                                 @RequestParam(required = true) Integer hospitalId,
                                                 HttpSession session) throws OrderException {
        List<Integer> orderCanExport = orderManagerService
                .getOrderCanExport(JSON.parseArray(orderIds, Integer.class), hospitalId);
        Map<String, Object> result = new HashMap<>();
        result.put("num", orderCanExport.size());
        result.put("orderIds", orderCanExport);
        return result;
    }
    
    /**
     * 获取可以导出xls的订单
     */
    @RequestMapping(value = "/getOrderCanExportXls", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Map<String, Object> getOrderCanExportXls(@RequestParam(required = true) String orderIds,
                                                 @RequestParam(required = true) Integer hospitalId,
                                                 HttpSession session) throws OrderException {
        List<Integer> orderCanExport = orderManagerService
                .getOrderCanExportXls(JSON.parseArray(orderIds, Integer.class), hospitalId);
        Map<String, Object> result = new HashMap<>();
        result.put("num", orderCanExport.size());
        result.put("orderIds", orderCanExport);
        return result;
    }
    
    /**
     * 批量导出订单到体检软件
     * 
     */
    @RequestMapping(value = "/batchExportOrder", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @LoginRequired
    public void batchExportOrder(@RequestParam(required = true) String orderIds,
                                 @RequestParam(required = true) Integer hospitalId,
                                 HttpSession session) throws OrderException {
        orderManagerService.exportOrder(JSON.parseArray(orderIds, Integer.class), hospitalId);
    }
    
    /**
     * 获取可以恢复未导的订单
     * 
     * @param orderIds
     * @return
     * @throws OrderException
     * @throws LoginFailedException
     */
    @RequestMapping(value = "/getOrderCanUnexport", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public OrderCanUnexportResult getOrderCanUnexport(@RequestParam(required = true) String orderIds,
                                                      @RequestParam(required = true) Integer hospitalId)
                                                              throws OrderException {
		log.info("visitCountForMethod getOrderCanUnexport...");
		List<Integer> orderIdList = JSON.parseArray(orderIds, Integer.class);
		OrderCanUnexportResult result = new OrderCanUnexportResult();
		if (CollectionUtils.isEmpty(orderIdList) || hospitalId == null) {
			return result;
		}
		OrderQueryParams orderQueryParams = new OrderQueryParams();
		orderQueryParams.setOrderIds(orderIdList);
		int rowCount = mongoOrderReadService.countBaseMongoOrder(orderQueryParams);
		if (rowCount > ORDER_CHECK_ALL_MAX_RANGE) {
			throw new OrderException(OrderException.ExceptionType.queryOrderOutOfRange, OrderException.ExceptionType.queryOrderOutOfRange.getMessage());
		}
		MongoOrderSelector mongoOrderSelector = new MongoOrderSelector();
		mongoOrderSelector.setOrderBaseInfo(true);
		PageView<MongoOrder> orderPageView = getMongoOrdersByOrderQueryParams(orderQueryParams, null, mongoOrderSelector);
		List<MongoOrder> mongoOrders = orderPageView.getRecords();
		List<Integer> orderIdsCanUnexport = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(mongoOrders)) {
			for (MongoOrder mongoOrder : mongoOrders) {
				if (!orderIdsCanUnexport.contains(mongoOrder.getId())) {
					if (OrderStatusEnum.exportFailed.getCode() == mongoOrder.getStatus()) {
						result.setExportFailedNum(result.getExportFailedNum() + 1);
						orderIdsCanUnexport.add(mongoOrder.getId());
					}else if (OrderStatusEnum.appointmentSuccess.getCode() == mongoOrder.getStatus()
							&& (mongoOrder.getIsExport() != null && mongoOrder.getIsExport())) {
						result.setExportedNum(result.getExportedNum() + 1);
						orderIdsCanUnexport.add(mongoOrder.getId());
					}
				}
			}
		}
		result.setOrderIds(orderIdsCanUnexport);
		return result;
    }

    /**
     * 批量将订单恢复为未导出
     * 
     * @param orderIds
     * @param session
     * @return
     * @throws OrderException
     * @throws LoginFailedException
     */
    @RequestMapping(value = "/batchChangeOrderToUnExport", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @LoginRequired
    public void batchChangeOrderToUnExport(@RequestParam(required = true) String orderIds,
                                           HttpSession session)
			throws OrderException, LoginFailedException, com.mytijian.order.base.exceptions.OrderException {
        //Account currentUser = LoginUtil.getCurrentUser(session);
        
    		Employee employee = SessionUtil.getEmployee();
		Account operator = new Account();
		operator.setId(-1);
		
		log.info("batchChangeOrderToUnExport: 操作人{} 订单id:{}", employee.getEmployeeName(), orderIds);
		
        orderExportService.changeToUnExports(JSON.parseArray(orderIds, Integer.class), operator,
                true, OperateAppEnum.OPS.getCode());
    }
    
    /**
     * <pre>
     * 根据订单id集合查询需要导出的订单信息
     * @param orderIds 订单id集合
     * @return 订单集合
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/orderInfoForExport", method = RequestMethod.POST)
    @ResponseBody
    public List<MongoOrder> orderInfoForExport(@RequestParam(required = true) String orderIds) {
		return getMongoOrdersByOrderIdList(JSON.parseArray(orderIds, Integer.class), null);
    }

    /**
     * <pre>
     * 导出订单信息
     * @param request http对象
     * @param session 通过session中的token拿到用户信息
     * @param response http对象
     * @param orderIds 订单id集合
     * @param hospitalId 体检中心
     * @param readOnly 是否只读
     * @throws Exception
     */
    @RequestMapping(value = "/exportOrder")
    @ResponseStatus(value = HttpStatus.OK)
    public void exportOrderList(HttpServletRequest request, HttpSession session, HttpServletResponse response,
            @RequestParam(value = "orderIds", required = true) String orderIds,
            @RequestParam(value = "hospitalId", required = true) Integer hospitalId,
            @RequestParam(value = "readOnly", required = true) boolean readOnly) throws Exception {
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
    		Employee employee = SessionUtil.getEmployee();
        log.info("exportOrder: 操作人:{} 参数 orderIds:{}, hospitalId:{}, readOnly:{}", 
        		employee.getEmployeeName(), orderIds, hospitalId, readOnly);

//        URL url = Thread.currentThread().getContextClassLoader().getResource("ordertemplate");
		InputStream stream = getClass().getClassLoader().getResourceAsStream("ordertemplate");
		File targetFile = new File("ordertemplate");
		FileUtils.copyInputStreamToFile(stream, targetFile);
        List<Integer> list = JSON.parseArray(orderIds, Integer.class);

        String filename = orderExportFileManager.exportMongoOrder(targetFile.getPath(), tempFolder, list, hospitalId);

        String name = filename.substring(filename.lastIndexOf(File.separator) + 1);

        response.setHeader("Content-Disposition", "attachment;filename=" + name);

        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);

        try {
            OutputStream outputStreamo = response.getOutputStream();

            IOUtils.copy(fileInputStream, outputStreamo);

            outputStreamo.flush();
            outputStreamo.close();
            fileInputStream.close();

            if (!readOnly) {
            
            	Account account = new Account();
            	account.setId(-1);
//            	account.setName(employee.getEmployeeName());
//                orderService.updateAfterExport(list, LoginUtil.getCurrentUser(session).getId(), true);
				UpdateOrderWrapper updateOrderWrapper = new UpdateOrderWrapper();
				updateOrderWrapper.setOperator(account);
				updateOrderWrapper.setOrderIds(list);
				updateOrderWrapper.setIsExport(true);
				updateOrderWrapper.setSystem(OperateAppEnum.OPS.getCode());
				orderExportService.updateOrderAfterExport(updateOrderWrapper);
            }
            
            log.info("event:导出订单信息 orderIds:{}, hospitalId{}, readOnly:{}", orderIds, hospitalId, readOnly);
        } catch (Exception e) {
            log.error("event:导出订单信息 orderIds:"+orderIds+", hospitalId:"+hospitalId+", readOnly:"+readOnly, e);
        }

    }

	/**
	 * <pre>
	 * 删除订单
	 * @param orderId 订单id
	 * @param session 通过session中的token拿到用户信息
	 * @throws com.mytijian.order.base.exceptions.OrderException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteorder", method = RequestMethod.DELETE)
	@LoginRequired
	@ResponseStatus(HttpStatus.OK)
	@Token("order")
	public void deleteOrder(@RequestParam(value = "orderId") Integer orderId, HttpSession session) throws com.mytijian.order.base.exceptions.OrderException{
		String token = (String) session.getAttribute(LoginService.TOKEN_NAME);
		Account operator = loginService.getAccount(token);
		orderDeleteService.deleteOrderByOrderId(orderId, operator, OperateAppEnum.OPS.getCode());
	}
	
	/**
	 * <pre>
	 * 查看订单
	 * orderId
	 */
	@RequestMapping(value = "/watchOrder/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public Map<String, Object> watchOrder(@PathVariable Integer orderId, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Integer, ExamItemSnap> itemSnap = Maps.newHashMap();
		try {
			itemSnap = orderService.getExamItemsDetail(orderId);
			Order relOrder = orderService.getNormalOrderByOrderId(orderId);
			MealSnap mealSnap = JSON.parseObject(relOrder.getMealDetail(), MealSnap.class);
			Map<String, Integer> otherPriceMap = mealSnap.getOtherMoneys();
			map.put("itemSnap", itemSnap);
			if(otherPriceMap != null){
				map.put("examItemPrice", otherPriceMap.get("体检项目"));
			}
			map.putAll(orderRefundLogService.countOrderRefund(orderId));
			setMongoOrderInfo(orderId, map);
		}catch(Exception e) {
			log.error("WatchOrder.error and orderId is {}",orderId,e);
		}
		return map;
	}

	/**
	 * 导出对账单
	 *
	 * @param session
	 * @return
	 * @throws OrderException
	 */
	@RequestMapping(value = "/exportCheckbook", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void exportCheckbook(HttpServletResponse response,HttpSession session, @RequestParam("orderIds[]") Integer[] ids
	) throws Exception {

		// TODO manage下线 这里只是为了 打日志
		//Account operator = LoginUtil.getCurrentUser(session);

		Employee employee = SessionUtil.getEmployee();
//		//校验数据
		List<Integer> orderIds = Arrays.asList(ids);
		if (orderIds.isEmpty()) {
			throw new BizException(ManageAppExceptionEnum.ORDER_IDS_IS_EMPTY);
		}
		if (orderIds.size() > 15000) {
			throw new BizException(ManageAppExceptionEnum.ORDER_SUM_IS_TOO_BIG);
		}
		//查看数据
		OrderQueryParams orderQueryParams = new OrderQueryParams();
		orderQueryParams.setOrderIds(orderIds);
		orderQueryParams.setSortField("insertTime");
		orderQueryParams.setSortDirection(Sort.Direction.ASC.name());
		String filename = newOrderExportFileManager.crmExportCheckbook(orderQueryParams);
		String name = filename.substring(filename.lastIndexOf(File.separator) + 1);

		response.reset();
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;filename=" + name);
		File file = new File(filename);
		FileInputStream fileInputStream = new FileInputStream(file);
		try {
			OutputStream outputStreamo = response.getOutputStream();
			IOUtils.copy(fileInputStream, outputStreamo);
			outputStreamo.flush();
			outputStreamo.close();
			fileInputStream.close();
		} catch (Exception e) {
			log.error("event={},bizType={},operatorId={},operatorName={},", ManageAppLogConstant.EXPORT_TO_EXCEL_FAIL,"manage导出订单查看",employee.getId(),employee.getEmployeeName(),e);
		}
	}
	
    @RequestMapping(value = "/isExatra", method = RequestMethod.GET)
    @ResponseBody
    @LoginRequired
    public String isExatra(HttpServletRequest request, HttpSession session,
                           HttpServletResponse response,
                           @RequestParam(value = "companyId", required = false) Integer companyId,
                           @RequestParam(value = "hospitalId") Integer hospitalId,
                           @RequestParam(value = "examStartDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date examStartDate,
                           @RequestParam(value = "examEndDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date examEndDate,
                           @RequestParam(value = "insertStartDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date insertStartDate,
                           @RequestParam(value = "insertEndDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date insertEndDate)
                                   throws Exception {
        if (examStartDate != null) {
            examStartDate = DateUtils.toDayStartSecond(examStartDate);
        }
        if (examEndDate != null) {
            examEndDate = DateUtils.toDayLastSecond(examEndDate);
        }
        if (insertStartDate != null) {
            insertStartDate = DateUtils.toDayStartSecond(insertStartDate);
        }
        if (insertEndDate != null) {
            insertEndDate = DateUtils.toDayLastSecond(insertEndDate);
        }
        int res = countBaseMongoOrder(hospitalId, companyId, examStartDate, examEndDate,
                insertStartDate, insertEndDate);
        if (res > 15000) {
            return "exatra";
        }
        if (res == 0) {
            return "nil";
        }
        return null;
    }
	
	private int countBaseMongoOrder(Integer hospitalId,Integer companyId,
			Date examStartDate,Date examEndDate,Date insertStartDate,Date insertEndDate){
		return mongoOrderReadService.countBaseMongoOrder(createOrderQueryParams(hospitalId, companyId, examStartDate, examEndDate, insertStartDate, insertEndDate));
	}

	
	private OrderQueryParams createOrderQueryParams(Integer hospitalId,Integer companyId,
			Date examStartDate,Date examEndDate,Date insertStartDate,Date insertEndDate) {
			OrderQueryParams orderQueryParams = new OrderQueryParams();
			orderQueryParams.setExamStartDate(examStartDate);
			orderQueryParams.setExamEndDate(examEndDate);
			orderQueryParams.setInsertStartDate(insertStartDate);
			orderQueryParams.setInsertEndDate(insertEndDate);
			List<Integer> examCompanyIds = Lists.newArrayList();
			if(companyId!=null) {
			examCompanyIds.add(companyId);
			}
			orderQueryParams.setExamCompanyIds(examCompanyIds);
			orderQueryParams.setHospitalId(hospitalId);
			List<Integer> orderStatus = Lists.newArrayList();
			orderStatus.add(OrderStatusEnum.examSuccess.getCode());
			orderStatus.add(OrderStatusEnum.partRefund.getCode());
			orderQueryParams.setOrderStatuses(orderStatus);
			return orderQueryParams;
	}
}
