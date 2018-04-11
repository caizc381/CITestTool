package com.mytijian.admin.web.controller.order;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.offer.examitem.model.ExamItemSnap;
import com.mytijian.order.base.mongo.MongoOrderReadService;
import com.mytijian.order.base.service.OrderRefundApplyService;
import com.mytijian.order.service.MongoOrderService;
import com.mytijian.order.service.OrderRefundLogService;
import com.mytijian.order.service.OrderService;
import com.mytijian.resource.service.HospitalService;

@RestController
public class OrderController {
    @Resource
    private OrderRefundApplyService orderRefundApplyService;

    @Resource
    private MongoOrderService mongoOrderService;

	@Resource(name = "mongoOrderReadService")
	public MongoOrderReadService mongoOrderReadService;
    
    @Resource(name = "orderRefundLogService")
	private OrderRefundLogService orderRefundLogService;

    @Resource
    private HospitalService hospitalService;
    
    @Resource(name = "orderService")
	private OrderService orderService;

    /**
	 * <pre>
	 * 获取订单详情
	 * @param orderId 订单id
	 * @param session 通过session中的token拿到用户信息
	 * @return 订单、单项快照以及价格
	 * @throws Exception
	 */
	@RequestMapping(value = "/manage/orderDetail", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getOrder(@RequestParam Integer orderId, HttpSession session) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Integer, ExamItemSnap> itemSnap = orderService.getExamItemsDetail(orderId);
		map.put("itemSnap", itemSnap);

		/*MongoOrder mongoOrder = mongoOrderReadService.getMongoOrderByOrderId(orderId, null);
		Order relOrder = orderService.getNormalOrderByOrderId(orderId);
		MealSnap mealSnap = JSON.parseObject(relOrder.getMealDetail(), MealSnap.class);
		Map<String, Integer> otherPriceMap = mealSnap.getOtherMoneys();
		map.put("refound", orderRefundLogService.countOrderRefund(orderId));
		map.put("locale", orderRefundLogService.getlocaleAddItems(orderId));
		if (relOrder.getHospital()!=null) {
			// 拒检项目是否退款
			map.put("refundRefused", hospitalService
					.getHospitalSettingsById(relOrder.getHospital().getId()).getRefundRefusedItem());
		}
		map.put("mealDiscount", AssertUtil.isNull(mealSnap)?"":mealSnap.getDiscount());
		map.put("order", mongoOrder);
		map.put("examItemPrice", otherPriceMap.get("体检项目"));*/
		return map;
	}
}
