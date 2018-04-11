package com.mytijian.admin.web.controller.examItem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.web.facade.examItem.ExamItemFacade;
import com.mytijian.admin.web.util.CommonUtil;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.admin.web.vo.examitem.ExamItemImportResultVO;
import com.mytijian.admin.web.vo.examitem.ExamItemImportResultVO.ErrorExamItem;
import com.mytijian.admin.web.vo.examitem.ExamItemImportVO;
import com.mytijian.admin.web.vo.examitem.ExamItemStandardListVo;
import com.mytijian.admin.web.vo.examitem.ExamItemStandardVo;
import com.mytijian.admin.web.vo.examitem.HospitalExamItemVo;
import com.mytijian.offer.examitem.constant.enums.ExamItemRelationEnum;
import com.mytijian.offer.examitem.constant.enums.ExamItemTypeEnum;
import com.mytijian.offer.examitem.exception.ItemManageException;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.model.ExamItemSpecies;
import com.mytijian.offer.examitem.model.ItemSettingValue;
import com.mytijian.offer.examitem.model.dataobj.ExamItemStandardMappingRelationDO;
import com.mytijian.offer.examitem.service.ExamItemRelationService;
import com.mytijian.offer.examitem.service.ExamItemService;
import com.mytijian.offer.examitem.service.ExamItemSpeciesService;
import com.mytijian.origin.pinyin.plugin.PinYinUtil;
import com.mytijian.product.item.constant.enums.ExamItemStandardRelationStatusEnum;
import com.mytijian.product.item.constant.enums.ExamItemStandardStatusEnum;
import com.mytijian.product.item.model.ExamItemStandardRelation;
import com.mytijian.product.item.model.ExamitemStandard;
import com.mytijian.product.item.model.StandardExamitemGroup;
import com.mytijian.product.item.param.ExamItemStandardQuery;
import com.mytijian.product.item.service.ExamItemStandardRelationService;
import com.mytijian.product.item.service.ExamItemStandardService;
import com.mytijian.product.item.service.group.ExamitemGroupReadService;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.util.AssertUtil;
import com.mytijian.util.ExcelUtil;

/**
 * 
 * 类ExamItemController.java的实现描述：单项相关
 * @author zhanfei.feng 2017年4月7日 下午3:36:03
 */
@RestController
@RequestMapping("/examItem")
public class ExamItemController {

	private final static Logger logger = LoggerFactory.getLogger(ExamItemController.class);

	@Resource(name = "examItemFacade")
	private ExamItemFacade examItemFacade;

	@Resource(name = "examItemStandardService")
	private ExamItemStandardService examItemStandardService;

	@Resource(name = "examItemSpeciesService")
	private ExamItemSpeciesService examItemSpeciesService;

	@Resource(name = "examitemGroupReadService")
	private ExamitemGroupReadService examitemGroupReadService;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;

	@Resource(name = "examItemService")
	private ExamItemService examItemService;

	@Resource(name = "examItemRelationService")
	private ExamItemRelationService examItemRelationService;

	@Resource(name = "examItemStandardRelationService")
	private ExamItemStandardRelationService examItemStandardRelationService;

	/**
	 * 获取同组项目
	 * @return
	 */
	@RequestMapping(value = "/sameGroupItems")
	public List<StandardExamitemGroup> getGroup() {
		List<StandardExamitemGroup> itemGroups = examitemGroupReadService.listExamitemGroup();
		return itemGroups.stream().filter(p -> Objects.equal(p.getType(), ExamItemRelationEnum.SAME_GROUP.getCode()))
				.collect(Collectors.toList());
	}

	/**
	 * 根据关键字，客户类型
	 * @param keywords 关键字
	 * @param clientClassify 客户端类型 0 无 客户分类
	 * @param crmClassify CRM 类型 0 无 客户分类
	 * @return
	 */
	@RequestMapping(value = "/standardList")
	public ExamItemStandardListVo getExamItemStandards(String keywords, Integer clientClassify, Integer crmClassify,
			Integer status) {
		ExamItemStandardQuery examItemStandardQuery = new ExamItemStandardQuery();
		examItemStandardQuery.setKeywords(keywords);
		examItemStandardQuery.setClientClassify(clientClassify);
		examItemStandardQuery.setCrmClassify(crmClassify);
		examItemStandardQuery.setStatus(status);
		return examItemFacade.listByExamItemStandardQuery(examItemStandardQuery);
	}

	/**
	 * 获取CRM/客户 类型
	 * @param type 1：CRM , 2: 客户
	 * @return
	 */
	@RequestMapping(value = "/speciesByType")
	public List<ExamItemSpecies> getSpeciesByType(@RequestParam("type") Integer type) {
		return examItemSpeciesService.getSpeciesByType(type);
	}

	/**
	 * 根据Id获取单项标准信息
	 * @param id 单项标准Id
	 * @return
	 */
	@RequestMapping(value = "/standardInfo/{id}")
	public ExamItemStandardVo standardInfo(@PathVariable("id") Integer id) {
		if (id == null || id.intValue() <= 0) {
			logger.error("ExamItemController.standardInfo is error id : {}", id);
			return null;
		}
		return examItemFacade.getExamitemStandardById(id);
	}

	/**
	 * 更新标准单项信息
	 * @param examitemStandard
	 * @return
	 */
	@RequestMapping("/standardSave")
	public boolean standardSave(@RequestBody ExamitemStandard examitemStandard) {
		checkSaveOrUpdatItemStardentForm(examitemStandard);
		examItemStandardService.saveExamitemStandard(examitemStandard);
		return true;
	}

	/**
	 * 更新标准单项信息
	 * @param examitemStandard
	 * @return
	 */
	@RequestMapping("/standardUpdate")
	public boolean standardUpdate(@RequestBody ExamitemStandard examItemStandard) {

		if (examItemStandard.getId() == null) {
			throw new RuntimeException("单项编号不能为空");
		}
		if (examItemStandard.getClientClassify() != null && examItemStandard.getClientClassify().intValue() == 0) {
			examItemStandard.setClientClassify(null);
		}
		if (examItemStandard.getCrmClassify() != null && examItemStandard.getCrmClassify().intValue() == 0) {
			examItemStandard.setCrmClassify(null);
		}
		examItemStandardService.updateExamitemStandard(examItemStandard);
		return true;
	}

	/**
	 * 更新标准单项状态
	 * @param examItemStandardIds 标准单项Ids 
	 * @param status 1:正常, 2：废除
	 * @return
	 */
	@RequestMapping("/standardUpdateStatus")
	public boolean standardUpdateStatus(@RequestParam(value = "examItemStandardIds") List<Integer> examItemStandardIds,
			@RequestParam(value = "status") Integer status) {
		examItemStandardService.updateBatchExamitemStandardStatusByIds(examItemStandardIds, status);
		if (status.intValue() == ExamItemStandardStatusEnum.INVALID.getCode()) {
			examItemFacade.updateExamItemStandardRelationByExamItemStandardIds(examItemStandardIds,
					ExamItemStandardRelationStatusEnum.LIFTED);
		}
		return true;
	}

	/**
	 * 更新标准单项类型
	 * @param examItemStandardIds 标准单项Ids 
	 * @param classType 1:CRM,2：客户
	 * @param classValue
	 * @return
	 */

	@RequestMapping("/standardUpdateClassType")
	public boolean standardUpdateClassType(
			@RequestParam(value = "examItemStandardIds") List<Integer> examItemStandardIds,
			@RequestParam(value = "classType") Integer classType,
			@RequestParam(value = "classValue") Integer classValue) {
		if (classValue != null) {
			classValue = classValue.intValue() == 0 ? null : classValue;
		}
		examItemStandardService.updateBatchExamitemStandardClassByIds(examItemStandardIds, classType, classValue);
		return true;
	}

	/**
	 * 根据医院获取医院单项与单项映射信息
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping("hospitalItemAndStandardRela")
	public List<HospitalExamItemVo> getExamItemsByHospitalId(@RequestParam(value = "hospitalId") Integer hospitalId,
			@RequestParam(value = "examItemKeywords") String examItemKeywords,
			@RequestParam(value = "isRelevance") Integer isRelevance) {
		List<HospitalExamItemVo> hospExamItemVoList = examItemFacade
				.listMacHospitalExamItemStandardMapsByHospitalId(hospitalId, examItemKeywords);

		if (CollectionUtils.isEmpty(hospExamItemVoList)) {
			return Lists.newArrayList();
		}

		switch (isRelevance) {
		case 1:
			hospExamItemVoList = hospExamItemVoList.stream().filter(item -> item.getIsRelevance() == 1)
					.collect(Collectors.toList());
			break;
		case 2:
			hospExamItemVoList = hospExamItemVoList.stream().filter(item -> item.getIsRelevance() != 1)
					.collect(Collectors.toList());
			break;

		}
		return hospExamItemVoList;
	}

	/**
	 * 获取单项信息
	 * @param hospitalId 医院Id
	 * @param examItemId 单项Id
	 * @return
	 */
	@RequestMapping("getHospitalExamItemById")
	public ExamItem getHospitalExamItemById(@RequestParam(value = "hospitalId") Integer hospitalId,
			@RequestParam(value = "examItemId") Integer examItemId) {
		return examItemFacade.getHospitalExamItemByHospitalId(hospitalId, examItemId);
	}

	@RequestMapping("standardRelationUpdate")
	public void standardRelationUpdate(@RequestBody ItemSettingValue itemSettingValue) throws ItemManageException {

		checkStandardRelationUpdateForm(itemSettingValue);
		examItemFacade.overrideHospitalExamItem(itemSettingValue.getStandardId(), itemSettingValue,
				itemSettingValue.getExamItemId(), itemSettingValue.getHospitalId(), itemSettingValue.getIsRelevance());
	}

	private void checkStandardRelationUpdateForm(ItemSettingValue itemSettingValue) {
		if (itemSettingValue.getStandardId() == null) {
			logger.error("ExamItemController.checkStandardRelationUpdateForm error, standardId is null");
			throw new RuntimeException("标准单项Id不合法");
		}
		if (itemSettingValue.getExamItemId() == 0) {
			logger.error("ExamItemController.checkStandardRelationUpdateForm error, examItemId is 0");
			throw new RuntimeException("医院单项编号不合法");
		}
		if (itemSettingValue.getHospitalId() == null) {
			logger.error("ExamItemController.checkStandardRelationUpdateForm error, hospitalId is null");
			throw new RuntimeException("医院编号不合法");
		}
		if (itemSettingValue.getIsRelevance() == null) {
			logger.error("ExamItemController.checkStandardRelationUpdateForm error, isRelevance is null");
			throw new RuntimeException("isRelevance不合法");
		}
	}

	void checkSaveOrUpdatItemStardentForm(ExamitemStandard examItemStandard) throws RuntimeException {
		if (examItemStandard.getGender() == null) {
			examItemStandard.setGender(2);
		}
		// TODO 暂时在此处加判断, 没有在service层加, 暂时先在上层进行拦截
		if (!CommonUtil.isStrFitLength(examItemStandard.getName(), 0, 50)) {
			logger.error("ExamItemController.checkSaveOrUpdatItemStardentForm error, name : {}",
					examItemStandard.getName());
			throw new RuntimeException("名称长度不合法");
		}
		if (!CommonUtil.isStrFitLength(examItemStandard.getPinyin(), 0, 100)) {
			logger.error("ExamItemController.checkSaveOrUpdatItemStardentForm error, pinyin : {}",
					examItemStandard.getPinyin());
			throw new RuntimeException("拼音长度不合法");
		}
		if (!CommonUtil.isStrFitLength(examItemStandard.getDetail(), 0, 4096)) {
			logger.error("ExamItemController.checkSaveOrUpdatItemStardentForm error, detail : {}",
					examItemStandard.getDetail());
			throw new RuntimeException("详情长度不合法");
		}
		if (!CommonUtil.isStrFitLength(examItemStandard.getDescription(), 0, 512)) {
			logger.error("ExamItemController.checkSaveOrUpdatItemStardentForm error, description : {}",
					examItemStandard.getDescription());
			throw new RuntimeException("描述长度不合法");
		}
		if (!CommonUtil.isStrFitLength(examItemStandard.getFitPeople(), 0, 256)) {
			logger.error("ExamItemController.checkSaveOrUpdatItemStardentForm error, fitPeople : {}",
					examItemStandard.getFitPeople());
			throw new RuntimeException("适应人群长度不合法");
		}
		if (!CommonUtil.isStrFitLength(examItemStandard.getUnfitPeople(), 0, 256)) {
			logger.error("ExamItemController.checkSaveOrUpdatItemStardentForm error, unfitPeople : {}",
					examItemStandard.getUnfitPeople());
			throw new RuntimeException("不适应人群长度不合法");
		}
		if (!CommonUtil.isStrFitLength(examItemStandard.getKeyword(), 0, 200)) {
			logger.error("ExamItemController.checkSaveOrUpdatItemStardentForm error, keyword : {}",
					examItemStandard.getKeyword());
			throw new RuntimeException("搜索关键字长度不合法");
		}
	}

	@RequestMapping(value = "/examItemImport")
	public ExamItemImportResultVO examItemImport(ExamItemImportVO examItemImportVO) throws Exception, IOException {
		Employee employee = SessionUtil.getEmployee();
		ExamItemImportResultVO examItemImportResultVO = new ExamItemImportResultVO();
		// 处理得到hospitalIds
		List<Integer> hospitalIds = new ArrayList<Integer>();
		if (examItemImportVO.getHospitalId() != null) {
			hospitalIds.add(examItemImportVO.getHospitalId());
		} else {
			hospitalIds.addAll(hospitalService.listHospitalIdsByBrandId(examItemImportVO.getOrgBandId()));
		}
		if (hospitalIds.isEmpty()) {
			throw new IllegalArgumentException("体检中心不存在，请重新选择");
		}

		// 解析excel
		File newFile = File.createTempFile("tmp", null);
		examItemImportVO.getFile().transferTo(newFile);
		List<ExamItemFile> examItemFileList = null;
		examItemFileList = examItemFileResolve(newFile);
		newFile.deleteOnExit();
		// 校验数据和保存数据
		Result<List<ErrorExamItem>, List<ExamItemUpdate>> examItemDataValidateResultList = ExamItemFileDataValidator
				.validateExamItemFileData(examItemFileList);
		if (examItemDataValidateResultList.getA().isEmpty()) {
			int successNum = saveExamItem(examItemDataValidateResultList.getB(), hospitalIds);
			examItemImportResultVO.setSuccess(true);
			examItemImportResultVO.setSuccessExamItemNumber(successNum);
		} else {
			examItemImportResultVO.setSuccess(false);
			examItemImportResultVO.setErrorExamItem(examItemDataValidateResultList.getA());
		}
		logger.info("event={},hospitalId={},brandId={},operatorId={},operatorName={},time={}", "单项导入",
				(examItemImportVO.getHospitalId() != null ? examItemImportVO.getHospitalId() : ""),
				(examItemImportVO.getOrgBandId() != null ? examItemImportVO.getOrgBandId() : ""),
				(employee != null ? employee.getId() : ""), (employee != null ? employee.getLoginName() : ""),
				LocalDateTime.now());
		return examItemImportResultVO;
	}

	private int saveExamItem(List<ExamItemUpdate> examItemUpdateList, List<Integer> hospitalIds)
			throws IllegalAccessException, InvocationTargetException {
		for (Integer hospitalId : hospitalIds) {
			for (ExamItemUpdate examItemUpdate : examItemUpdateList) {
				importExamItem(hospitalId, examItemUpdate);
				importExamItemStandardRelation(hospitalId, examItemUpdate);
			}
		}
		return examItemUpdateList.size();
	}

	private List<ExamItemFile> examItemFileResolve(File newFile) throws Exception {
		List<ExamItemFile> examItemFileList = new ArrayList<ExamItemFile>();
		List<List<String>> fileList = new ArrayList<>();
		try {
			fileList = ExcelUtil.analyzeExcel(newFile);
		} catch (Exception e) {
			throw new IllegalArgumentException("模板不正确");
		}
		if (fileList == null) {
			throw new IllegalArgumentException("模板不正确");
		}
		// 校验表头，去掉三行表头
		if (fileList.size() >= 3) {
			if (!(("HIS编码".equals(fileList.get(1).get(0))) && ("项目名称".equals(fileList.get(1).get(1)))
					&& ("性别".equals(fileList.get(1).get(2))) && ("价格".equals(fileList.get(1).get(3)))
					&& ("标准项ID".equals(fileList.get(1).get(4))) && ("适宜人群".equals(fileList.get(1).get(5)))
					&& ("不适宜人群".equals(fileList.get(1).get(6))) && ("项目简介".equals(fileList.get(1).get(7)))
					&& ("详细介绍".equals(fileList.get(1).get(8))) && ("选项为：通用/男/女".equals(fileList.get(2).get(2)))
					&& ("填写数值".equals(fileList.get(2).get(3))))) {
				throw new IllegalArgumentException("表头错误，请下载单项模板");
			}
			fileList.remove(0);
			fileList.remove(0);
			fileList.remove(0);
		} else {
			throw new IllegalArgumentException("表头错误，请下载单项模板");
		}
		for (List<String> list : fileList) {
			if (!inNullList(list)) {
				ExamItemFile examItemFile = new ExamItemFile();
				// hisId为文本类型，读取纯数字会加上.0，如5会读成5.0,去掉尾数.0
				examItemFile.setHisId(subString(getDataBySize(0, list)));
				examItemFile.setName(getDataBySize(1, list));
				examItemFile.setGender(getDataBySize(2, list));
				examItemFile.setPrice(getDataBySize(3, list));
				examItemFile.setStandardLibraryId(subString(getDataBySize(4, list)));
				examItemFile.setFitPeople(getDataBySize(5, list));
				examItemFile.setUnfitPeople(getDataBySize(6, list));
				examItemFile.setDescription(getDataBySize(7, list));
				examItemFile.setDetail(getDataBySize(8, list));
				examItemFileList.add(examItemFile);
			}
		}
		return examItemFileList;
	}

	private String getDataBySize(Integer n, List<String> list) {
		if (n < list.size()) {
			return list.get(n);
		}
		return null;
	}

	private String subString(String str) {
		if (!AssertUtil.isEmpty(str)) {
			if (str.length() > 2) {
				if (".0".equals(str.substring(str.length() - 2, str.length()))) {
					str = str.substring(0, str.length() - 2);
				}
			}
			return str;
		} else {
			return null;
		}
	}

	private void importExamItemAlreadyExit(ExamItem examItem) {
		Set<String> setField = new HashSet<String>();
		setField.add("name");
		setField.add("pinyin");
		setField.add("name");
		setField.add("description");
		setField.add("detail");
		setField.add("fitPeople");
		setField.add("unfitPeople");
		setField.add("gender");
		setField.add("price");
		setField.add("type");
		examItemService.updateExamItemWithFiled(examItem, setField);
	}

	private void importExamItem(Integer hospitalId, ExamItemUpdate examItemUpdate)
			throws IllegalAccessException, InvocationTargetException {
		ExamItem examItem = new ExamItem();
		BeanUtils.copyProperties(examItem, examItemUpdate);
		examItem.setHospitalId(hospitalId);
		examItem.setPinyin(PinYinUtil.getFirstSpell(examItemUpdate.getName()));
		examItem.setRefundRefusedItem(hospitalService.getHospitalSettingsById(hospitalId).getRefundRefusedItem());
		// 判断单项是否已经存在
		ExamItem exitItemExit = examItemService.getExamItemByHospitalAndHisItemId(hospitalId,
				examItemUpdate.getHisItemId());
		if (exitItemExit == null) {
			examItem.setItemType(ExamItemTypeEnum.HOSPITAL.getCode());
			examItem.setFocus(false);
			examItem.setSyncPrice(true);
			examItem.setShowWarning(false);
			examItemService.addExamItem(examItem);
		} else {
			// 更新tb_examitem
			examItem.setId(exitItemExit.getId());
			if (exitItemExit.getItemType() == ExamItemTypeEnum.DELETE.getCode()) {
				examItem.setItemType(ExamItemTypeEnum.HOSPITAL.getCode());
			} else {
				examItem.setItemType(exitItemExit.getItemType());
			}
			importExamItemAlreadyExit(examItem);
		}
	}

	private void importExamItemStandardRelation(Integer hospitalId, ExamItemUpdate examItemUpdate) {
		// 保存单项与标准单项的映射关系
		// 1.项目新增成功
		ExamItem isExitItem = examItemService.getExamItemByHospitalAndHisItemId(hospitalId,
				examItemUpdate.getHisItemId());
		// 2.标准单项存在
		ExamitemStandard isExitItemStandard = examItemStandardService
				.getExamitemStandardById(examItemUpdate.getStandardLibraryId());
		// 3.根据标准单项id找关系
		ExamItem relation = examItemStandardService.queryExamitemStandardRelation(hospitalId,
				examItemUpdate.getStandardLibraryId());
		// 4.根据单项id找关系
		List<ExamItemStandardRelation> examItemStandardRelation = examItemStandardRelationService.listByExamItemIds(
				new ArrayList<Integer>(Arrays.asList(isExitItem.getId())), ExamItemStandardRelationStatusEnum.RELATED);
		if ((isExitItem != null) && (isExitItemStandard != null)) {
			// 单项和标准项存在
			if (relation == null && isExitItemStandard.getStatus() == ExamItemStandardStatusEnum.NORMAL.getCode()) {
				// 标准项未被关联
				if (examItemStandardRelation.isEmpty()) {
					// 单项未被关联，则新增
					ExamItemStandardMappingRelationDO examItemStandardMappingRelationDO = new ExamItemStandardMappingRelationDO();
					examItemStandardMappingRelationDO.setExamItemId(isExitItem.getId());
					examItemStandardMappingRelationDO.setHospitalId(isExitItem.getHospitalId());
					examItemStandardMappingRelationDO.setStandardLibraryId(isExitItemStandard.getId());
					examItemStandardMappingRelationDO.setIsRelevance(1);
					examItemRelationService.addExamitemStandardRelation(examItemStandardMappingRelationDO);

				} else {
					// 则单项已经关联，则更新
					// 删除原有关系
					examItemRelationService.deleteExamitemStandardRelationByExamItemId(
							examItemStandardRelation.get(0).getExamItemId());
					// 新增一条关系
					ExamItemStandardMappingRelationDO examitemStandardRelation = new ExamItemStandardMappingRelationDO();
					examitemStandardRelation.setExamItemId(examItemStandardRelation.get(0).getExamItemId());
					examitemStandardRelation.setStandardLibraryId(isExitItemStandard.getId());
					examitemStandardRelation.setIsRelevance(1);
					examitemStandardRelation.setHospitalId(hospitalId);
					examItemRelationService.addExamitemStandardRelation(examitemStandardRelation);
				}
			}
		}
	}

	private Boolean inNullList(List<String> list) {
		if (list == null) {
			return true;
		}
		Boolean isNull = true;
		for (String string : list) {
			if (string != null) {
				isNull = false;
			}
		}
		return isNull;
	}

}
