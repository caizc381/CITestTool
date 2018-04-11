package com.mytijian.admin.web.controller.meal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.mytijian.admin.web.vo.examitem.ExamItemWithOutIgnoreVo;
import com.mytijian.admin.web.vo.meal.MealExamitemVO;
import com.mytijian.common.util.ArrayUtil;
import com.mytijian.offer.examitem.exception.ItemSelectException;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.service.ExamItemRelationService;
import com.mytijian.offer.examitem.service.ExamItemRelationValidateService;
import com.mytijian.offer.examitem.service.ExamItemService;
import com.mytijian.offer.examitem.service.MealItemPackageRuleRelationService;
import com.mytijian.offer.examitem.service.business.ExamItem2BusinessService;
import com.mytijian.offer.meal.dto.MealDto;
import com.mytijian.offer.meal.exception.MealException;
import com.mytijian.offer.meal.exception.MealItemVerifyException;
import com.mytijian.offer.meal.model.Meal;
import com.mytijian.offer.meal.service.Calculator;
import com.mytijian.offer.meal.service.MealService;
import com.mytijian.offer.meal.service.MealServiceHelper;
import com.mytijian.product.meal.service.StandardMealMappingManagerService;
import com.mytijian.product.meal.service.StandardMealReadService;
import com.mytijian.product.meal.service.StandardMealWriteService;
import com.mytijian.report.service.parse.DroolsRuleService;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.util.AssertUtil;
import com.mytijian.web.intercepter.Token;

/**
 * 包括创建、编辑套餐的接口
 * 
 * @author yuefengyang
 *
 */
@RestController
@RequestMapping("/meal/platform/")
public class PlatformMealUpdateController {

	@Resource(name = "examItemService")
	private ExamItemService examItemService;

	@Resource(name = "mealService")
	private MealService mealService;

	@Resource(name = "examItemRelationService")
	private ExamItemRelationService examItemRelationService;

	@Resource(name = "mealServiceHelper")
	private MealServiceHelper mealServiceHelper;

	@Resource(name = "mealItemPackageRuleRelationService")
	private MealItemPackageRuleRelationService mealItemPackageRuleRelationService;

	@Resource(name = "droolsRuleService")
	private DroolsRuleService droolsRuleService;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	@Resource(name="examItemRelationValidateService")
	private ExamItemRelationValidateService examItemRelationValidateService;
	
	@Resource(name = "mealAssembler")
	private MealAssembler mealAssembler;
	
	@Resource(name = "examItem2BusinessService")
	private ExamItem2BusinessService examItem2BusinessService;
	
	@Resource(name = "standardMealMappingManagerService")
	private StandardMealMappingManagerService standardMealMappingManagerService;

	@Resource(name = "standardMealReadService")
	private StandardMealReadService standardMealReadService;
	
	@Resource(name = "standardMealWriteService")
	private StandardMealWriteService standardMealWriteService;
	/**
	 * 返回体检中心的基础套餐及体检中心单项信息
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/hospitalBasicMeal", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> getHospitalBasicMeal(
			@RequestParam(value = "hospitalId", required = true) Integer hospitalId) {
		return mealAssembler.getMealPageInfo(hospitalId);
	}

	/**
	 * 用户每次选择一个项目时调用此接口,获得计算的金额
	 * 
	 * @param mealId
	 *            当前套餐，用于获得折扣信息
	 * @param selectedId
	 *            点选的项目，不论选中或者取消选择
	 * @param choosedIds
	 *            被选中项目集合，用逗号分隔
	 * @return
	 * @throws ItemSelectException
	 */
	@RequestMapping(value = "/calculatePrice", method = { RequestMethod.POST })
	@ResponseBody
	public Calculator.PriceEntry selectItem(
			@RequestParam("discount") Double discount,
			@RequestParam("hospitalId") Integer hospitalId,
			@RequestParam("choosedIds[]") int[] choosedIds)
			throws ItemSelectException {
		// TODO 无需mealid，算价只需hospitalid和discount。可提供一个接口
		Meal meal = new Meal();
		meal.setHospitalId(hospitalId);
		meal.setDiscount(discount);
		return examItemService.calculate(meal, choosedIds);
	}

	/**
	 * 验证单项关系
	 * 
	 * @param addId
	 * @param cancelId
	 * @param selectedIds
	 * @throws ItemSelectException
	 */
	@RequestMapping(value = "/validateItemRelation", method = { RequestMethod.POST })
	@ResponseStatus(value = HttpStatus.OK)
	public void validateItemRelation(
			@RequestParam(value = "addId", required = false) Integer addId,
			@RequestParam(value = "cancelId", required = false) Integer cancelId,
			@RequestParam(value = "selectedIds[]", required = false) int[] selectedIds)
			throws ItemSelectException {
		if (selectedIds != null) {
			examItemRelationValidateService.verifyConflictItem(addId, cancelId,
					ArrayUtil.parseFromArray(selectedIds));
		}else{
			examItemRelationValidateService.verifyConflictItem(addId, cancelId,
					Lists.newArrayList());
		}
	}

	/**
	 * 保存套餐
	 *
	 * @param mealDto
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveMeal", method = { RequestMethod.POST })
	@ResponseBody
	@Token
	public Meal saveMeal(@RequestBody MealExamitemVO mealExamitemVO, HttpSession session)
			throws Exception {
		MealDto mealDto = new MealDto();
		BeanUtils.copyProperties(mealExamitemVO, mealDto);
		Meal meal = new Meal();
		BeanUtils.copyProperties(mealExamitemVO.getMeal(), meal);
		meal.setPurchasePrice(StringUtils.isBlank(mealExamitemVO.getMeal()
				.getPurchasePrice()) ? 0 : Integer.valueOf(mealExamitemVO
				.getMeal().getPurchasePrice()));
		meal.setSupplyPrice(StringUtils.isBlank(mealExamitemVO.getMeal()
				.getSupplyPrice()) ? 0 : Integer.valueOf(mealExamitemVO
				.getMeal().getSupplyPrice()));
		meal.setDisplayPrice(StringUtils.isBlank(mealExamitemVO.getMeal()
				.getDisplayPrice()) ? 0 : Integer.valueOf(mealExamitemVO
				.getMeal().getDisplayPrice()));
		
		mealDto.setMeal(meal);
		if(CollectionUtils.isNotEmpty(mealExamitemVO.getMealItemList())){
			List<ExamItem> examItems = new ArrayList<ExamItem>();
			for(ExamItemWithOutIgnoreVo vo :mealExamitemVO.getMealItemList() ){
				ExamItem examItem = new ExamItem();
				BeanUtils.copyProperties(vo, examItem);
				examItems.add(examItem);
			}
			
			mealDto.setMealItemList(examItems);
		}
		// 验证修改调整价格权限
		this.verifyAdjustPrice(mealDto.getMeal(), mealDto.getMealItemList(),
				true, false);
		// 保存套餐
		Meal platformMeal = mealService.saveMeal(mealDto);
		// 建立关联
		if (mealExamitemVO.getMeal().getStandardMealId() != null
				&& standardMealReadService.getById(mealExamitemVO.getMeal()
						.getStandardMealId()) != null) {
			standardMealMappingManagerService.addMapping(mealExamitemVO
					.getMeal().getStandardMealId(), platformMeal.getId(),
					mealExamitemVO.getMeal().getHospitalId());
		}

		return platformMeal;
	}

	private void verifyAdjustPrice(Meal meal, List<ExamItem> mealItemList,
			boolean enableAdjustPrice, boolean isPlatformManager)
			throws MealException {
		if (isPlatformManager) { // 平台客户经理都可以调整价格
			if (meal.getMealSetting().getAdjustPrice() > 0) {
				throw new MealException(
						MealException.PLATFORM_ADJUST_PRICE_ERR,
						"平台调整套餐金额必须大于0");
			}
			return;
		}

		// 如果套餐id等于基础套餐id，或套餐id为0或null，则为新增套餐
		Hospital hsp = hospitalService.getHospitalById(meal.getHospitalId());
		Integer baseMealId = mealServiceHelper.getBasicMealId(hsp);

		boolean newFlag = AssertUtil.isNull(meal.getId())
				|| AssertUtil.areEquals(meal.getId(), 0)
				|| AssertUtil.areEquals(meal.getId(), baseMealId);

		if (newFlag) {
			if ((!hsp.getSettings().getAllowAdjustPrice() || !enableAdjustPrice)
					&& meal.getMealSetting().getAdjustPrice() != 0) {
				throw new MealException(
						MealException.NO_ADJUST_PRICE_PRIVILEGE, "你没有调整套餐价格权利！");
			}

		} else {
			Meal qMeal = mealService.getMealById(meal.getId());
			if ((!hsp.getSettings().getAllowAdjustPrice() || !enableAdjustPrice)
					&& meal.getMealSetting().getAdjustPrice() != qMeal
							.getMealSetting().getAdjustPrice()) {
				throw new MealException(
						MealException.NO_ADJUST_PRICE_PRIVILEGE, "你没有调整套餐价格权利！");
			}
		}

		if (hsp.getSettings().getAllowAdjustPrice() && enableAdjustPrice) {
			if (meal.getMealSetting().getAdjustPrice() > 0) {
				Set<Integer> idList = mealItemList
						.stream()
						.filter(item -> item.isShow()
								&& item.isBasic()
								&& item.isSelected()
								&& ((item.getGroupId() != null && !item
										.getEnableSelect()) || (item
										.getGroupId() == null)))
						.map(item -> item.getId()).collect(Collectors.toSet());
				int[] choosedIds = ArrayUtils.toPrimitive(idList
						.toArray(new Integer[idList.size()]));
				Calculator.PriceEntry priceEntry = mealService
						.calculateBasicItemPrice(meal, choosedIds);
				if (meal.getMealSetting().getAdjustPrice() >= priceEntry
						.getPrice()) {
					throw new MealException(MealException.ADJUST_PRICE_INVALID,
							"售价的调整价格必须小于必选项目的价格之和");
				}
			}
		}

	}
	
	/**
	 * 当单项错误时使用ItemSelectException显示错误信息
	 * 
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({ ItemSelectException.class })
	@ResponseBody
	public List<ItemSelectException.Conflict> handleItemSelectException(
			HttpServletResponse response, ItemSelectException ex) {
		response.setStatus(400);
		return ex.getValue();
	}
	
	/**
	 * 当套餐项不包含基础套餐项错误时使用BasicMealItemVerifyException显示错误信息
	 *
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({ MealItemVerifyException.class })
	@ResponseBody
	public Map<String, Object> handleBasicMealItemVerifyException(
			HttpServletResponse response, MealItemVerifyException ex) {
		response.setStatus(400);
		Map<String, Object> result = new HashMap<>();
		result.put("exceptType", ex.getExceptionType());
		result.put("exceptItemList", ex.getItemIdList());
		if (AssertUtil.isNotEmpty(ex.getConflictItemList())) {
			result.put("exceptItemList", ex.getConflictItemList());
			result.put("conflictItemId", ex.getConflictItemId());
		}
		return result;
	}

}
