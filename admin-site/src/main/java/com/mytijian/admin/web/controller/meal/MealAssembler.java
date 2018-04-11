package com.mytijian.admin.web.controller.meal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mytijian.admin.web.vo.examitem.ExamItemWithOutIgnoreVo;
import com.mytijian.admin.web.vo.meal.MealVO;
import com.mytijian.calculate.HospitalCaculateUtil;
import com.mytijian.offer.examitem.constant.enums.ExamitemSpeciesEnum;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.model.ExamItemSpecies;
import com.mytijian.offer.examitem.service.ExamItemService;
import com.mytijian.offer.examitem.service.business.ExamItem2BusinessService;
import com.mytijian.offer.meal.constant.enums.MealStateEnum;
import com.mytijian.offer.meal.model.Meal;
import com.mytijian.offer.meal.service.MealService;
import com.mytijian.offer.meal.service.MealServiceHelper;
import com.mytijian.product.meal.model.StandardMealExamitem;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.service.HospitalService;

@Component("mealAssembler")
public class MealAssembler {

	private static final String MEAL = "meal";

	private static final String ITEM_SPECIES = "itemSpecies";

	private static final String HOSPITAL_EXAMITEM = "hospitalExamitem";

	private static final String MEAL_EXAMITEM = "mealExamitem";

	/**
	 * 平台散客折扣。医院进货价=售价*平台折扣
	 */
	private static final String PLATFORM_GUEST_DISCOUNT = "platformGuestDiscount";
	
	/**
	 * 渠道散客折扣。渠道供货价=售价*渠道折扣
	 */
	private static final String PLATFORM_CHANNEL_GUEST_DISCOUNT = "platformChannelGuestDiscount";

	@Resource(name = "examItemService")
	private ExamItemService examItemService;

	@Resource(name = "examItem2BusinessService")
	private ExamItem2BusinessService examItem2BusinessService;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;

	@Resource(name = "mealService")
	private MealService mealService;

	@Resource(name = "mealServiceHelper")
	private MealServiceHelper mealServiceHelper;

	/**
	 * 基础套餐相关信息
	 * 
	 * @param hospitalId
	 * @return
	 */
	public <T extends Meal> Map<String, Object> getMealPageInfo(
			Integer hospitalId) {
		Meal meal = mealService.getMealById(mealServiceHelper
				.getBasicMealId(hospitalId));
		return this.getMealPageInfo(meal);
	}
	
	/**
	 * 套餐相关信息
	 * 
	 * @param meal
	 * @return
	 */
	public <T extends Meal> Map<String, Object> getMealPageInfo(T meal) {
		Map<String, Object> pageInfo = new HashMap<String, Object>();
		HospitalSettings hospitalSettings = hospitalService
				.getHospitalSettingsById(meal.getHospitalId());

		// 【官方分类】下面的单项列表
		List<ExamItemSpecies> examItemSpecies = examItemService
				.getExamItemSpecies(meal.getHospitalId(), meal.getId(),
						ExamitemSpeciesEnum.BODY_SPECIES.getCode(), true);

		pageInfo.put(MEAL, this.mealModel2MealVO(meal, hospitalSettings));
		pageInfo.put(ITEM_SPECIES, examItemSpecies);

		pageInfo.put(HOSPITAL_EXAMITEM, this
				.convert2VO(examItem2BusinessService.listHospitalExamItem(meal
						.getHospitalId())));

		pageInfo.put(MEAL_EXAMITEM, this.convert2VO(examItem2BusinessService
				.listMealExamitem(meal.getId())));

		pageInfo.put(PLATFORM_GUEST_DISCOUNT,
				hospitalSettings.getPlatformGuestDiscount());
		
		pageInfo.put(PLATFORM_CHANNEL_GUEST_DISCOUNT,
				hospitalSettings.getPlatformChannelGuestDiscount());

		return pageInfo;
	}

	/**
	 * 根据标准套餐组装平台套餐信息
	 * 
	 * @param map
	 * @param hospitalId
	 * @return
	 */
	public <T extends Meal> Map<String, Object> getMealPageInfo(
			Map<Integer, StandardMealExamitem> map, Integer hospitalId) {
		Map<String, Object> pageInfo = this.getMealPageInfo(hospitalId);
		HospitalSettings hospitalSettings = hospitalService
				.getHospitalSettingsById(hospitalId);
		// 1、新建套餐
		Meal meal = new Meal();
		meal.setId(0);
		meal.setDisable(MealStateEnum.NORMAL.getCode());
		pageInfo.put(MEAL, this.mealModel2MealVO(meal, hospitalSettings));

		// 2、设置关联的单项到套餐中
		@SuppressWarnings("unchecked")
		List<ExamItemWithOutIgnoreVo> hospitalExamitems = (List<ExamItemWithOutIgnoreVo>) pageInfo
				.get(HOSPITAL_EXAMITEM);
		
		@SuppressWarnings("unchecked")
		List<ExamItemWithOutIgnoreVo> mealExamitems = (List<ExamItemWithOutIgnoreVo>) pageInfo
				.get(MEAL_EXAMITEM);

		List<ExamItemWithOutIgnoreVo> mealExamitemVos = new ArrayList<>();
		for (ExamItemWithOutIgnoreVo examItem : hospitalExamitems) {
			StandardMealExamitem standardMealExamitem = map.get(examItem
					.getId());
			// 有对应的体检中心单项 && 不是基础套餐的单项，则加入到套餐单项中，默认选中
			if (standardMealExamitem != null
					&& !this.isExist(mealExamitems, examItem.getId())) {
				ExamItemWithOutIgnoreVo vo = new ExamItemWithOutIgnoreVo();
				BeanUtils.copyProperties(examItem, vo);
				vo.setMealId(meal.getId());
				vo.setBasic(standardMealExamitem.getBasic());
				vo.setEnableSelect(standardMealExamitem.getEnableSelect());
				vo.setSelected(true);
				mealExamitemVos.add(vo);
			}
		}

		
		mealExamitems.addAll(mealExamitemVos);

		return pageInfo;

	}
	
	private boolean isExist(List<ExamItemWithOutIgnoreVo> mealExamitems,
			Integer examItemId) {
		if(CollectionUtils.isNotEmpty(mealExamitems)){
			return mealExamitems.stream()
					.filter(p -> Objects.equal(p.getId(), examItemId))
					.findAny().isPresent();
		}
		return false;
	}

	private List<ExamItemWithOutIgnoreVo> convert2VO(
			List<ExamItem> mealExamItems) {
		List<ExamItemWithOutIgnoreVo> mealExamItemsVos = Lists.newArrayList();
		for (ExamItem examItem : mealExamItems) {
			ExamItemWithOutIgnoreVo examItemVo = new ExamItemWithOutIgnoreVo();
			BeanUtils.copyProperties(examItem, examItemVo);
			mealExamItemsVos.add(examItemVo);
		}
		return mealExamItemsVos;
	}
	
	private MealVO mealModel2MealVO(Meal meal, HospitalSettings settings) {
		MealVO mealVO = new MealVO();
		BeanUtils.copyProperties(meal, mealVO);
		mealVO.setId(meal.getId());
		if (settings == null) {
			mealVO.setDisplayPrice(getPirceString(meal.getDisplayPrice()));
			mealVO.setInitPrice(getPirceString(meal.getInitPrice()));
			mealVO.setSalePrice(getPirceString(meal.getPrice()));
			mealVO.setPurchasePrice(getPirceString(meal.getPurchasePrice()));
			mealVO.setSupplyPrice(getPirceString(meal.getSupplyPrice()));
		} else {
			String calculatorService = settings.getCalculatorService();
			Integer displayPrice = HospitalCaculateUtil.caculateDiscountPrice(
					calculatorService, 1.0, meal.getDisplayPrice() == null ? 0
							: meal.getDisplayPrice());
			mealVO.setDisplayPrice(getPirceString(HospitalCaculateUtil
					.caculateRoundPrice(calculatorService, displayPrice)));

			Integer initPrice = HospitalCaculateUtil.caculateDiscountPrice(
					calculatorService, 1.0, meal.getInitPrice() == null ? 0
							: meal.getInitPrice());

			Integer salePrice = HospitalCaculateUtil.caculateDiscountPrice(
					calculatorService, 1.0,
					meal.getPrice() == null ? 0 : meal.getPrice());

			Integer purchasePrice = HospitalCaculateUtil.caculateDiscountPrice(
					calculatorService, 1.0, meal.getPurchasePrice() == null ? 0
							: meal.getPurchasePrice());

			Integer supplyPrice = HospitalCaculateUtil.caculateDiscountPrice(
					calculatorService, 1.0, meal.getSupplyPrice() == null ? 0
							: meal.getSupplyPrice());

			mealVO.setInitPrice(getPirceString(HospitalCaculateUtil
					.caculateRoundPrice(calculatorService, initPrice)));
			mealVO.setSalePrice(getPirceString(HospitalCaculateUtil
					.caculateRoundPrice(calculatorService, salePrice)));
			mealVO.setPurchasePrice(getPirceString(HospitalCaculateUtil
					.caculateRoundPrice(calculatorService, purchasePrice)));
			mealVO.setSupplyPrice(getPirceString(HospitalCaculateUtil
					.caculateRoundPrice(calculatorService, supplyPrice)));

		}
		return mealVO;
	}

	private String getPirceString(Integer price) {
		BigDecimal div1 = new BigDecimal(price.toString());
		BigDecimal div2 = new BigDecimal(100);
		return div1.divide(div2, 2, RoundingMode.HALF_UP).toString();
	}
}
