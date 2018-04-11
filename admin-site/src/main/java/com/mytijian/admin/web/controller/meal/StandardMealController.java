package com.mytijian.admin.web.controller.meal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mytijian.admin.web.common.validator.ValidationService;
import com.mytijian.admin.web.vo.meal.HospitalVO;
import com.mytijian.admin.web.vo.meal.StandardMealExamitemVO;
import com.mytijian.admin.web.vo.meal.StandardMealVO;
import com.mytijian.offer.examitem.exception.ItemSelectException;
import com.mytijian.origin.pinyin.plugin.PinYinUtil;
import com.mytijian.product.item.constant.enums.ExamitemGroupOperateEnum;
import com.mytijian.product.item.dto.StandardExamitemGroupDto;
import com.mytijian.product.item.service.group.StandardExamitemGroupRelationOperateService;
import com.mytijian.product.meal.constant.enums.StandardMealTypeEnum;
import com.mytijian.product.meal.model.StandardMeal;
import com.mytijian.product.meal.model.StandardMealExamitem;
import com.mytijian.product.meal.model.StandardMealMapping;
import com.mytijian.product.meal.param.StandardMealQuery;
import com.mytijian.product.meal.param.StandardMealSelector;
import com.mytijian.product.meal.service.StandardMealMappingManagerService;
import com.mytijian.product.meal.service.StandardMealReadService;
import com.mytijian.product.meal.service.StandardMealWriteService;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.web.intercepter.Token;

@RestController
@RequestMapping("/meal/standard/")
public class StandardMealController {

	@Resource(name = "standardMealReadService")
	private StandardMealReadService standardMealReadService;

	@Resource(name = "standardMealWriteService")
	private StandardMealWriteService standardMealWriteService;

	@Resource(name = "validationService")
	private ValidationService validationService;
	
	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	@Resource(name = "standardExamitemGroupRelationOperateService")
	private StandardExamitemGroupRelationOperateService standardExamitemGroupRelationOperateService;
	
	@Resource(name = "standardMealMappingManagerService")
	private StandardMealMappingManagerService standardMealMappingManagerService;

	/**
	 * 标准套餐列表页,按创建时间倒序
	 * 
	 * @return
	 */
	@RequestMapping(value = "/mealList", method = { RequestMethod.GET })
	@ResponseBody
	public List<StandardMealVO> listMeal(
			@RequestParam(name = "hospitalId", required = false) Integer hospitalId) {
		StandardMealQuery query = new StandardMealQuery();
		query.setTypeList(Arrays.asList(StandardMealTypeEnum.MEAL.getCode()));
		StandardMealSelector selector = new StandardMealSelector();
		selector.setNeedExamitem(false);
		List<StandardMeal> meals = standardMealReadService.listStandardMeal(
				query, selector);

		
		List<StandardMealVO> voList = new ArrayList<StandardMealVO>();
		// 平台套餐关联标准套餐页面
		if (hospitalId != null) {
			List<StandardMealMapping> standardMealMappingList = standardMealMappingManagerService
					.listMealMappingByHospitalId(hospitalId, false, false);
			Map<Integer, Integer> relevanceMap = new HashMap<Integer, Integer>();

			if (CollectionUtils.isNotEmpty(standardMealMappingList)) {
				relevanceMap = standardMealMappingList.stream().collect(
						Collectors.toMap(
								StandardMealMapping::getStandardMealId,
								StandardMealMapping::getPlatformMealId));
			}
			if (CollectionUtils.isNotEmpty(meals)) {
				for (StandardMeal standardMeal : meals) {
					StandardMealVO vo = new StandardMealVO();
					BeanUtils.copyProperties(standardMeal, vo);
					Integer platformMealId = relevanceMap.get(vo.getId());
					if (platformMealId != null) {
						vo.setRelevancePlatformMeal(true);
					}
					voList.add(vo);
				}
			}
		} else {// 关联平台套餐数量
			for (StandardMeal standardMeal : meals) {
				StandardMealVO vo = new StandardMealVO();
				List<StandardMealMapping> list = 	standardMealMappingManagerService
						.listMealMappingByStandardMealId(standardMeal.getId(),
								false, false);
				
				standardMeal
						.setPlatformMealSize(list == null ? 0 : list.size());
				BeanUtils.copyProperties(standardMeal, vo);
				voList.add(vo);
			}
		}
			// 排序,按创建时间倒序
			Collections.sort(voList, new Comparator<StandardMealVO>() {

				@Override
				public int compare(StandardMealVO o1, StandardMealVO o2) {
					return ComparisonChain
							.start()
							.compare(o1.getGmtCreated(), o2.getGmtCreated(),
									Ordering.natural().reverse().nullsLast())
							.result();
				}
			});
		
		return voList;
	}

	/**
	 * 详情
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{mealId}", method = { RequestMethod.GET })
	@ResponseBody
	public StandardMealVO getMeal(
			@PathVariable(name = "mealId", required = true) Integer mealId) {
		if (mealId == null) {
			throw new IllegalArgumentException("套餐id为空");
		}

		StandardMealQuery query = new StandardMealQuery();
		query.setMealIdList(Arrays.asList(mealId));

		StandardMealSelector selector = new StandardMealSelector();
		selector.setNeedExamitem(true);

		List<StandardMeal> meals = standardMealReadService.listStandardMeal(
				query, selector);
		if (CollectionUtils.isEmpty(meals)) {
			throw new IllegalStateException("套餐不存在");
		}

		StandardMealVO standardMealVO = new StandardMealVO();
		BeanUtils.copyProperties(meals.get(0), standardMealVO);

		if (CollectionUtils.isNotEmpty(meals.get(0).getStandardMealItemList())) {
			List<StandardMealExamitemVO> examitemVOList = new ArrayList<StandardMealExamitemVO>();
			for (StandardMealExamitem sourceItem : meals.get(0)
					.getStandardMealItemList()) {
				StandardMealExamitemVO targetItem = new StandardMealExamitemVO();
				BeanUtils.copyProperties(sourceItem, targetItem);
				examitemVOList.add(targetItem);
			}
			standardMealVO.setStandardMealItemList(examitemVOList);
		}
		return standardMealVO;
	}

	/**
	 * 更新模版套餐字段 
	 * @param mealId
	 * @param templateType 平台套餐模版，官方套餐模版，基础推荐套餐模版 ，分别传0，1，2 ;
	 * 					   风险加项包模板，常规加项包模板 ，分别传0，1
	 * @param setAsTemplate true or false
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/updateTemplate", method = { RequestMethod.POST })
	@ResponseStatus(HttpStatus.OK)
	public void updateIsTemplate(
			@RequestParam(name = "mealId", required = true) Integer mealId,
			@RequestParam(name = "templateType", required = true) Integer templateType,
			@RequestParam(name = "setAsTemplate", required = true) Boolean setAsTemplate) throws IllegalAccessException {
		StandardMeal standardMeal = standardMealReadService.getById(mealId);
		if (standardMeal == null) {
			throw new IllegalAccessException("套餐不存在");
		}
		char[] tempType = standardMeal.getTemplateType().toCharArray();
		tempType[templateType] = Objects.equal(setAsTemplate, true) ? '1' : '0';
		standardMealWriteService
				.updateTemplateType(mealId, new String(tempType));
	}

	/**
	 * 验证单项关系
	 * 
	 * @param addId 添加的单项id
	 * @param cancelId 取消的单项id
	 * @param selectedIds
	 * @throws ItemSelectException
	 */
	@RequestMapping(value = "/validateItemRelation", method = { RequestMethod.POST })
	@ResponseStatus(value = HttpStatus.OK)
	public void validateItemRelation(
			@RequestParam(value = "addId", required = false) Integer addId,
			@RequestParam(value = "cancelId", required = false) Integer cancelId,
			@RequestParam(value = "selectedIds[]", required = false) Integer[] selectedIds)
			throws ItemSelectException {
		if (addId == null && cancelId == null) {
			throw new IllegalArgumentException("未选择单项");
		}
		
		if(selectedIds == null){
			return ;
		}

		StandardExamitemGroupDto dto = new StandardExamitemGroupDto();
		dto.setSelectedItemIds(Arrays.asList(selectedIds));
		dto.setSelectedItemId(addId != null ? addId : cancelId);
		dto.setIsAdded(addId != null ? true : false);
		standardExamitemGroupRelationOperateService.operate(dto,
				ExamitemGroupOperateEnum.VALIDATE);
	}

	/**
	 * 创建
	 * 
	 * @param standardMealVO
	 */
	@Token
	@RequestMapping(value = "/addMeal", method = { RequestMethod.POST })
	@ResponseStatus(HttpStatus.OK)
	public void addMeal(
			@RequestBody(required = true) @Validated StandardMealVO standardMealVO,
			BindingResult result) {
		/**
		 * 字段验证.验证顺序问题 1mealname : 30 2modify_text : 16 3套餐描述：512 4折扣： 0.01~2.00
		 */
		if (result.hasErrors()) {
			throw new IllegalArgumentException(result.getAllErrors().get(0)
					.getDefaultMessage());
		}
		
		if (standardMealVO.getId() != null) {
			throw new IllegalArgumentException("套餐id不为空");
		}

		StandardMeal standardMeal = this.resolve(standardMealVO);

		standardMealWriteService.addStandardMeal(standardMeal);
	}
	
	/**
	 * 更新
	 * 
	 * @param standardMealVO
	 */
	@Token
	@RequestMapping(value = "/updateMeal", method = { RequestMethod.POST })
	@ResponseStatus(HttpStatus.OK)
	public void updateMeal(
			@RequestBody(required = true) @Validated StandardMealVO standardMealVO,
			BindingResult result) {

		/**
		 * 字段验证.验证顺序问题 1mealname : 30 2modify_text : 16 3套餐描述：512 4折扣： 0.01~2.00
		 */
		if (result.hasErrors()) {
			throw new IllegalArgumentException(result.getAllErrors().get(0)
					.getDefaultMessage());
		}

		if (standardMealVO.getId() == null) {
			throw new IllegalArgumentException("套餐id不能为空");
		}
		
		StandardMeal standardMeal = this.resolve(standardMealVO);
		standardMealWriteService.updateStandardMeal(standardMeal);
	}
	
	private StandardMeal resolve(StandardMealVO standardMealVO) {
		StandardMeal standardMeal = new StandardMeal();
		List<StandardMealExamitem> examitems = new ArrayList<StandardMealExamitem>();
		BeanUtils.copyProperties(standardMealVO, standardMeal);
		if (CollectionUtils
				.isNotEmpty(standardMealVO.getStandardMealItemList())) {
		for (StandardMealExamitemVO examitemVO : standardMealVO
				.getStandardMealItemList()) {
			StandardMealExamitem target = new StandardMealExamitem();
			BeanUtils.copyProperties(examitemVO, target);

			examitems.add(target);
		}
		standardMeal.setStandardMealItemList(examitems);}
		return standardMeal;
	}

	/**
	 * 创建平台套餐页面，显示体检中心列表
	 * 
	 * @param standardMealId
	 * @return
	 */
	@RequestMapping(value = "/listHospitalForAddPlatformMeal", method = { RequestMethod.GET })
	@ResponseBody
	public List<HospitalVO> listHospitalForAddPlatformMeal(
			@RequestParam(name = "standardMealId", required = true) Integer standardMealId) {
		if (standardMealId == null) {
			throw new IllegalArgumentException("标准套餐id为空");
		}

		// 已关联的体检中心
		List<Hospital> relatedHospitalList = standardMealReadService
				.listHospitalByStandardMealId(standardMealId);
		
		List<Integer> hospitalIdList = relatedHospitalList.stream()
				.map(Hospital::getId).collect(Collectors.toList());
		List<Hospital> allHospitalList = hospitalService
				.getOrganizationList(OrganizationTypeEnum.HOSPITAL.getCode());

		List<HospitalVO> volist = new ArrayList<HospitalVO>();
		for (Hospital hospital : allHospitalList) {
			HospitalVO vo = new HospitalVO();
			vo.setId(hospital.getId());
			vo.setName(hospital.getName());
			vo.setPinyin(PinYinUtil.getFirstSpell(hospital.getName()));
			// 是否已关联
			if (hospitalIdList.contains(hospital.getId())) {
				vo.setRelatedStandardMeal(true);
			} else {
				vo.setRelatedStandardMeal(false);
			}
			volist.add(vo);

		}
		return volist;

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

}
