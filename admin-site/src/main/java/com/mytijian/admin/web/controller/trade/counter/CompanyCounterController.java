package com.mytijian.admin.web.controller.trade.counter;

import com.mytijian.account.model.Account;
import com.mytijian.uic.util.LoginUtil;
import com.mytijian.counter.base.constant.BizSourceEnum;
import com.mytijian.counter.base.dto.BookCapacityReqDTO;
import com.mytijian.counter.base.dto.OrderableCapacityReqDTO;
import com.mytijian.counter.base.dto.OrderableCapacityRespDTO;
import com.mytijian.counter.base.dto.SourceDTO;
import com.mytijian.counter.base.service.CounterQueryService;
import com.mytijian.counter.dto.OrderableCapacityCell;
import com.mytijian.counter.exception.CounterLimitException;
import com.mytijian.counter.exception.CounterReserveException;
import com.mytijian.counter.exception.CounterServiceException;
import com.mytijian.counter.exception.LimitInfo;
import com.mytijian.exception.BizException;
import com.mytijian.offer.examitem.model.LimitItem;
import com.mytijian.offer.examitem.service.CountExamItemService;
import com.mytijian.order.enums.OrderSourceEnum;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/companycounter")
public class CompanyCounterController {

	private static final Integer GLOABLE_EXAM_ITEM_ID = -1;

	@Resource(name = "countExamItemService")
	private CountExamItemService countExamItemService;

	@Resource
	private CounterQueryService counterQueryService;

	/**
	 * 单位预约当前时间段可预约量
	 * 
	 * @param companyId
	 *            体检单位id
	 * @param hospitalId
	 *            医院id
	 * @param orderNum
	 *            预约人数
	 * @param startDate
	 *            查询范围起始日期
	 * @param endDate
	 *            查询范围结束日期
	 * @param session
	 * @param return [<时间段, HospitalCapacityCell> ... <时间段,
	 *        HospitalCapacityCell>]
	 */
	@RequestMapping(value = "/book/capacity", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<Integer, OrderableCapacityCell>> getOrderableCapacity(
			                                          @RequestParam(value = "companyId") Integer companyId,
			                                          @RequestParam(value = "hospitalId") Integer hospitalId,
			                                          @RequestParam(value = "orderNum") Integer orderNum,
			                                          @RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			                                          @RequestParam(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			                                          HttpSession session) {
		//Account currentUser = LoginUtil.getCurrentUser(session);

		OrderableCapacityReqDTO orderableCapacityReqDTO = new OrderableCapacityReqDTO();
		orderableCapacityReqDTO.setCompanyId(companyId);
		orderableCapacityReqDTO.setHospitalId(hospitalId);
		orderableCapacityReqDTO.setStartDate(startDate);
		orderableCapacityReqDTO.setEndDate(endDate);
		orderableCapacityReqDTO.setOrderCount(orderNum);
		orderableCapacityReqDTO.setOperator(-1); // TODO 人数控制支持OPS类型时使用ops用户id
		orderableCapacityReqDTO.setSource(OrderSourceEnum.CRM.getCode()); 
		List<Map<Integer, OrderableCapacityRespDTO>>  orderableCapacityList = counterQueryService.getOrderableCapacity(orderableCapacityReqDTO);
		List<Map<Integer, OrderableCapacityCell>> result = convertToOrderableCapacityCellList(orderableCapacityList);
		return result;

	}
	private List<Map<Integer,OrderableCapacityCell>> convertToOrderableCapacityCellList(List<Map<Integer, OrderableCapacityRespDTO>> orderableCapacityList) {
		List<Map<Integer,OrderableCapacityCell>> orderCapacityCellList = new ArrayList<>();
		orderableCapacityList.stream().forEach(orderableCapacityMap -> {
			Map<Integer,OrderableCapacityCell>  orderableCapacityCellMap = new HashMap<>();
			orderableCapacityMap.forEach((periodId, orderableCapacityRespDTO) -> {
				OrderableCapacityCell orderableCapacityCell = new OrderableCapacityCell();
				orderableCapacityCell.setAvailableNum(orderableCapacityRespDTO.getAvailableNum());
				orderableCapacityCell.setEnough(orderableCapacityRespDTO.isEnough());
				orderableCapacityCell.setExpireDay(orderableCapacityRespDTO.isExpireDay());
				orderableCapacityCell.setFromComppany(orderableCapacityRespDTO.isFromComppany());
				orderableCapacityCell.setLimit(orderableCapacityRespDTO.getLimit());
				orderableCapacityCell.setRelease(orderableCapacityRespDTO.getRelease());
				orderableCapacityCell.setRest(orderableCapacityRespDTO.isRest());
				orderableCapacityCellMap.put(periodId, orderableCapacityCell);
			});
			orderCapacityCellList.add(orderableCapacityCellMap);
		});
		return orderCapacityCellList;
	}


	/**
	 * 改项批量预约，单位预约当前时间段可预约量检查
	 * @param companyId
	 * @param hospitalId
	 * @param dayRangeId
	 * @param examItemIdList
	 * @param orderNum
	 * @param date
	 */
	@RequestMapping(value = "/book/modifyItemCheckCapacity", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void checkCapacity(@RequestParam(value = "companyId") Integer companyId,
                              @RequestParam(value = "hospitalId") Integer hospitalId,
                              @RequestParam(value = "dayRangeId") Integer dayRangeId,
                              @RequestParam(value = "examItemIdList[]") Integer[] examItemIdList,
                              @RequestParam(value = "orderNum") Integer orderNum,
                              @RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, HttpSession session) {
		// 更新订单预约数量
//		Account currentUser = LoginUtil.getCurrentUser(session);
//		if (currentUser == null) {
//			return;
//		}
		List<LimitItem> alllimitItems = new ArrayList<>();
		LimitItem limitItem = new LimitItem();
		limitItem.setItemId(GLOABLE_EXAM_ITEM_ID);
		limitItem.setCount(1);
		alllimitItems.add(limitItem);

		List<LimitItem> limitItems = countExamItemService.getLimitItemListByIds(Arrays.asList(examItemIdList));

		if(limitItems.size() > 0){
			alllimitItems.addAll(limitItems);
		}

		
		
		// 下一步操作人没有使用
		BookCapacityReqDTO bookCapacityReqDto = new BookCapacityReqDTO.Builder(hospitalId,
				companyId, new SourceDTO(BizSourceEnum.BOOK_ORDER_LIMIT.name(), 
						OrderSourceEnum.CRM.getCode()), -1) // TODO 人数控制支持OPS类型时使用ops用户id
				.date(date)
				.period(dayRangeId)
				//未下单前的校验，订单号默认为空
				.examItemApplyCountList(alllimitItems, Arrays.asList(new String[orderNum]))
				.build();
		try{
			counterQueryService.checkExamItemCapacity(bookCapacityReqDto);
		}catch (BizException ex){
			List<LimitInfo> excessMaxInfos = new ArrayList<LimitInfo>();
			if(ex.getExtInfo() != null){
				for(Map.Entry<String, Object> entry : ex.getExtInfo().entrySet()){
					LimitInfo limitInfo = new LimitInfo();
					limitInfo.setOrderNum(orderNum);
					limitInfo.setItemName(entry.getKey());
					limitInfo.setAvailableNum((Integer) entry.getValue());
					excessMaxInfos.add(limitInfo);
				}
			}
			throw new CounterLimitException(CounterLimitException.HOSPITAL_ORDER_NUM_NOT_ENOUGH, ex.getErrorMsg(), excessMaxInfos);
		}


	}
	/**
	 * 当套餐项不包含基础套餐项错误时使用BasicMealItemVerifyException显示错误信息
	 * 
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({ CounterServiceException.class })
	@ResponseBody
	public Map<String, Object> handleAccountValidatorException(HttpServletResponse response, CounterServiceException ex) {
		response.setStatus(400);
		Map<String, Object> result = new HashMap<>();
		result.put("code", ex.getCode());
		result.put("text", ex.getMessage());
		return result;
	}
	
	/**
	 * 当套餐项不包含基础套餐项错误时使用BasicMealItemVerifyException显示错误信息
	 * 
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({ CounterLimitException.class })
	@ResponseBody
	public Map<String, Object> handleCounterLimitException(HttpServletResponse response, CounterLimitException ex) {
		response.setStatus(400);
		Map<String, Object> result = new HashMap<>();
		result.put("code", ex.getCode());
		result.put("text", ex.getMessage());
		result.put("limit", ex.getExcessLimitInfo());
		return result;
	}
	
	/**
	 * 当套餐项不包含基础套餐项错误时使用BasicMealItemVerifyException显示错误信息
	 * 
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({ CounterReserveException.class })
	@ResponseBody
	public Map<String, Object> handleCounterReseveException(HttpServletResponse response, CounterReserveException ex) {
		response.setStatus(400);
		Map<String, Object> result = new HashMap<>();
		result.put("code", ex.getCode());
		result.put("text", ex.getMessage());
		result.put("limit", ex.getExcessLimitInfo());
		return result;
	}
}
