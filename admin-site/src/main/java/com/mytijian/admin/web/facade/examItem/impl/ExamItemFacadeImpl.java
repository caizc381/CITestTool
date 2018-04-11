package com.mytijian.admin.web.facade.examItem.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mytijian.admin.web.facade.examItem.ExamItemFacade;
import com.mytijian.admin.web.vo.examitem.ExamItemIdAndName;
import com.mytijian.admin.web.vo.examitem.ExamItemStandardIdAndName;
import com.mytijian.admin.web.vo.examitem.ExamItemStandardListVo;
import com.mytijian.admin.web.vo.examitem.ExamItemStandardVo;
import com.mytijian.admin.web.vo.examitem.ExamItemVo;
import com.mytijian.admin.web.vo.examitem.HospitalExamItemVo;
import com.mytijian.offer.examitem.constant.enums.ExamitemSpeciesEnum;
import com.mytijian.offer.examitem.exception.ItemManageException;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.model.ExamItemInSpecies;
import com.mytijian.offer.examitem.model.ExamItemSpecies;
import com.mytijian.offer.examitem.model.ItemSettingValue;
import com.mytijian.offer.examitem.param.ExamItemQuery;
import com.mytijian.offer.examitem.service.ExamItemManageService;
import com.mytijian.offer.examitem.service.ExamItemService;
import com.mytijian.offer.examitem.service.ExamItemSpeciesService;
import com.mytijian.product.item.constant.enums.ExamItemStandardRelationStatusEnum;
import com.mytijian.product.item.constant.enums.ExamItemStandardStatusEnum;
import com.mytijian.product.item.model.ExamItemStandardRelation;
import com.mytijian.product.item.model.ExamitemStandard;
import com.mytijian.product.item.param.ExamItemStandardQuery;
import com.mytijian.product.item.service.ExamItemStandardRelationService;
import com.mytijian.product.item.service.ExamItemStandardService;


/**
 * 
 * 类ExamItemFacadeImpl.java的实现描述：TODO 类实现描述 
 * @author zhanfei.feng 2017年4月7日 下午1:20:32
 */
@Service("examItemFacade")
public class ExamItemFacadeImpl implements ExamItemFacade {

	private final static Logger logger = LoggerFactory.getLogger(ExamItemFacade.class);

	@Resource(name = "examItemStandardService")
	private ExamItemStandardService examItemStandardService;

	@Resource(name = "examItemSpeciesService")
	private ExamItemSpeciesService examItemSpeciesService;

	@Resource(name = "examItemManageService")
	private ExamItemManageService examItemManageService;

	@Resource(name = "examItemService")
	private ExamItemService examItemService;

	@Resource(name = "examItemStandardRelationService")
	private ExamItemStandardRelationService examItemStandardRelationService;

	@Override
	public ExamItemStandardListVo listByKeywordsAndClass(String keywords, Integer clientClassify, Integer crmClassify) {
		List<ExamitemStandard> examitemStandards = examItemStandardService.listByKeywordsAndClass(keywords,
				clientClassify, crmClassify);
		int totalCount = 0;
		int invalidCount = 0;
		Map<Integer, String> speciesIdAndNameMap = null;
		ExamItemStandardListVo examItemStandardListVo = new ExamItemStandardListVo();
		if (examitemStandards != null) {
			totalCount = examitemStandards.size();
			Set<Integer> crmClassIds = Sets.newHashSet();
			Set<Integer> clientClassIds = Sets.newHashSet();
			invalidCount = getInvalidCountAndClassifyIds(examitemStandards, crmClassIds, clientClassIds);
			speciesIdAndNameMap = getSpeciesIdAndName(getExamItemSpecies(crmClassIds, clientClassIds));
			examItemStandardListVo.setInvaildCount(invalidCount);
			examItemStandardListVo.setTotalCount(totalCount);
			examItemStandardListVo.setList(getExamItemStandardClassifyName(examitemStandards, speciesIdAndNameMap));
		}
		return examItemStandardListVo;
	}

	private List<ExamItemStandardVo> getExamItemStandardClassifyName(List<ExamitemStandard> examitemStandards,
			Map<Integer, String> speciesIdAndNameMap) {
		List<ExamItemStandardVo> examItemStandardVos = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(examitemStandards) && examitemStandards.size() > 0) {
			examitemStandards.forEach(examitemStandard -> {
				ExamItemStandardVo examItemStandardVo = new ExamItemStandardVo();
				BeanUtils.copyProperties(examitemStandard, examItemStandardVo);
				examItemStandardVo.setClientClassifyName(speciesIdAndNameMap.get(examitemStandard.getClientClassify()));
				examItemStandardVo.setCrmClassifyName(speciesIdAndNameMap.get(examitemStandard.getCrmClassify()));
				examItemStandardVos.add(examItemStandardVo);
			});
		}
		return examItemStandardVos;
	}

	/**
	 * 获取单项类别列表
	 * @param crmClassIds
	 * @param clientClassIds
	 * @return
	 */
	private List<ExamItemSpecies> getExamItemSpecies(Set<Integer> crmClassIds, Set<Integer> clientClassIds) {
		List<Integer> ids = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(crmClassIds)) {
			ids.addAll(crmClassIds);
		}

		if (CollectionUtils.isNotEmpty(clientClassIds)) {
			ids.addAll(clientClassIds);
		}
		return examItemSpeciesService.listSpeciesByIds(ids, null);
	}

	/**
	 * 获取单项类别和名称
	 * @param examItemSpecies
	 * @return
	 */
	private Map<Integer, String> getSpeciesIdAndName(List<ExamItemSpecies> examItemSpecies) {
		Map<Integer, String> maps = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(examItemSpecies) && examItemSpecies.size() > 0) {
			examItemSpecies.forEach(examItemSpecie -> {
				maps.put(examItemSpecie.getId(), examItemSpecie.getName());
			});
		}
		return maps;
	}

	private int getInvalidCountAndClassifyIds(List<ExamitemStandard> examitemStandards, Set<Integer> crmClassIds,
			Set<Integer> clientClassIds) {
		int invalidCount = 0;
		crmClassIds = crmClassIds == null ? Sets.newHashSet() : crmClassIds;
		clientClassIds = clientClassIds == null ? Sets.newHashSet() : clientClassIds;
		if (CollectionUtils.isNotEmpty(examitemStandards)) {
			for (ExamitemStandard examitemStandard : examitemStandards) {
				if (examitemStandard.getStatus() == null
						|| examitemStandard.getStatus().intValue() == ExamItemStandardStatusEnum.INVALID.getCode()) {
					invalidCount++;
				}
				if (examitemStandard.getCrmClassify() != null) {
					crmClassIds.add(examitemStandard.getCrmClassify());
				}
				if (examitemStandard.getClientClassify() != null) {
					clientClassIds.add(examitemStandard.getClientClassify());
				}
			}
		}
		return invalidCount;
	}

	@Override
	public ExamItemStandardVo getExamitemStandardById(Integer id) {
		if (id == null) {
			logger.error("ExamItemFacade.getExamitemStandardById id is null");
		}
		ExamitemStandard examitemStandard = examItemStandardService.getExamitemStandardById(id);
		return examitemStandardToExamItemStandardVo(examitemStandard);
	}

	private ExamItemStandardVo examitemStandardToExamItemStandardVo(ExamitemStandard examitemStandard) {
		if (examitemStandard != null) {
			ExamItemStandardVo examItemStandardVo = new ExamItemStandardVo();
			BeanUtils.copyProperties(examitemStandard, examItemStandardVo);
			List<Integer> ids = Lists.newArrayList();
			Integer existClientClassy = 0;
			Integer existCrmClassy = 0;
			if (examItemStandardVo.getClientClassify() != null) {
				// ExamItemSpecies examItemSpecies =
				// examItemSpeciesService.getById(examItemStandardVo.getClientClassify());
				existClientClassy = examItemStandardVo.getClientClassify();
				ids.add(existClientClassy);
			}
			if (examItemStandardVo.getCrmClassify() != null) {
				// ExamItemSpecies examItemSpecies =
				// examItemSpeciesService.getById(examItemStandardVo.getClientClassify());
				existCrmClassy = examItemStandardVo.getCrmClassify();
				ids.add(existCrmClassy);
			}

			if (ids.size() > 0) {
				List<ExamItemSpecies> examItemSpecies = examItemSpeciesService.listSpeciesByIds(ids, null);
				if (CollectionUtils.isNotEmpty(examItemSpecies) && examItemSpecies.size() > 0) {
					Map<Integer, String> map = examItemSpecies.stream()
							.collect(Collectors.toMap(ExamItemSpecies::getId, ExamItemSpecies::getName));
					if (existClientClassy.intValue() > 0) {
						examItemStandardVo.setClientClassifyName(map.get(existClientClassy));
					}
					if (existCrmClassy.intValue() > 0) {
						examItemStandardVo.setCrmClassifyName(map.get(existCrmClassy));
					}
				}
			}
			return examItemStandardVo;
		}
		return null;
	}

	@Override
	public List<HospitalExamItemVo> listMacHospitalExamItemStandardMapsByHospitalId(Integer hospitalId,
			String keywords) {

		List<HospitalExamItemVo> hospitalExamItemVos = Lists.newArrayList();

		if (hospitalId == null || hospitalId.intValue() <= 0) {
			logger.error("ExamItemFacade.listHospitalExamItemStandardMapsByHospitalId error hospitalId is null");
			return hospitalExamItemVos;
		}

		// 获取体检中心项目
		// List<ExamItem> examItems = examItemService.getHospitalExamItems(hospitalId);
		ExamItemQuery examItemQuery = new ExamItemQuery();
		examItemQuery.setHospitalId(hospitalId);
		examItemQuery.setKeywords(keywords);
		examItemQuery.setType(1);
		List<ExamItem> examItems = examItemService.listExamItemsByExamItemQuery(examItemQuery);

		return examItemsToHospitalExamItemVo(examItems);
		// return hospitalExamItemVos;
	}

	@Override
	public ExamItemVo getHospitalExamItemByHospitalId(Integer hospitalId, Integer examItemId) {
		if (examItemId == null) {
			logger.error("ExamItemFacade.getHospitalExamItemByHospitalId error examItemId is not fit");
			return null;
		}

		ExamItem examItem = examItemManageService.getExamItemInfoByItemId(examItemId);

		if (examItem == null) {
			logger.info("ExamItemFacade.getHospitalExamItemByHospitalId warn, examItem is null。 examItemId : {}",
					examItemId);
			return null;
		}

		if (hospitalId != null) {
			if (examItem.getHospitalId() == null || hospitalId.intValue() != examItem.getHospitalId().intValue()) {
				logger.error("ExamItemFacade.getHospitalExamItemByHospitalId error hospitalId is not fit");
				return null;
			}
		}

		ExamItemVo examItemVo = new ExamItemVo();
		BeanUtils.copyProperties(examItem, examItemVo);

		// 获取类型
		List<ExamItemInSpecies> examItemInSpecies = examItemManageService.listByItemId(examItemId);
		if (CollectionUtils.isNotEmpty(examItemInSpecies) && examItemInSpecies.size() > 0) {
			List<Integer> speciesIds = Lists.newArrayList();
			examItemInSpecies.forEach(examItemInSpecie -> {
				speciesIds.add(examItemInSpecie.getSpeciesId());
			});

			List<ExamItemSpecies> examItemInSpeciesList = examItemSpeciesService.listSpeciesByIds(speciesIds, null);
			getExamItemClassify(examItemVo, examItemInSpeciesList);
		}

		return examItemVo;
	}

	private void getExamItemClassify(ExamItemVo examItemVo, List<ExamItemSpecies> examItemInSpeciesList) {
		if (CollectionUtils.isNotEmpty(examItemInSpeciesList) && examItemInSpeciesList.size() > 0) {
			examItemInSpeciesList.forEach(examItemInSpeciesInfo -> {
				if (examItemInSpeciesInfo.getType() == ExamitemSpeciesEnum.OFFICIAL_SPECIES.getCode()) {
					examItemVo.setCrmClassify(examItemInSpeciesInfo.getId());
					examItemVo.setCrmClassifyName(examItemInSpeciesInfo.getName());
				} else if (examItemInSpeciesInfo.getType() == ExamitemSpeciesEnum.BODY_SPECIES.getCode()) {
					examItemVo.setClientClassify(examItemInSpeciesInfo.getId());
					examItemVo.setClientClassifyName(examItemInSpeciesInfo.getName());
				}
			});
		}
	}

	@Override
	public void overrideHospitalExamItem(Integer standardId, ItemSettingValue itemSettingValue, Integer examItemId,
			Integer hospitalId, Integer isRelevance) throws ItemManageException {
		// 查询医院单项是否存在
		ExamItem examItemInfo = examItemManageService.getExamItemInfoByItemId(examItemId);
		if (examItemInfo == null || hospitalId.intValue() != examItemInfo.getHospitalId().intValue()) {
			logger.error("ExamItemController.checkOverrideExamItem error, hospitalId : {}",
					hospitalId + " , examItemId : {}", examItemId);
			// throw ExceptionFactory.makeFault(UserExceptionCode.USER_LOGINNAME_EMPTY, new Object[] { null });
			throw new RuntimeException("医院Id编号与医院单项标号不符");
		}

		logger.info("医院单项被覆盖  ： examItemOld ： " + JSON.toJSONString(examItemInfo) + ", examItem : "
				+ JSON.toJSONString(itemSettingValue));

		// 覆盖信息
		// examItemManageService.updateItemInfo(examItem, ShiroUtils.getUserId());
		// 保存映射
		examItemManageService.updateExamStandardRelation(standardId, itemSettingValue, examItemId, hospitalId,
				isRelevance);
	}

	private List<HospitalExamItemVo> examItemsToHospitalExamItemVo(List<ExamItem> examItems) {
		List<HospitalExamItemVo> hospitalExamItemVos = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(examItems) && examItems.size() > 0) {
			List<ExamitemStandard> examitemStandards = Lists.newArrayList();
			List<Integer> examItemIds = examItems.stream().map(ExamItem::getId).collect(Collectors.toList());
			// 根据体检中心所有单项Id 查询 单项和标准单项关联关系
			List<ExamItemStandardRelation> examItemStandardRelations = examItemStandardRelationService
					.listByExamItemIds(examItemIds, ExamItemStandardRelationStatusEnum.RELATED);
			if (CollectionUtils.isNotEmpty(examItemStandardRelations)
					&& examItemStandardRelations.size() > 0) {
				List<Integer> examItemStandardIds = examItemStandardRelations
						.stream()
						.map(ExamItemStandardRelation::getStandardLibraryId)
						.collect(Collectors.toList());

				// 根据标准单项Id 获取标准单项信息
				examitemStandards = examItemStandardService.listByIdsAndStatus(
						examItemStandardIds, ExamItemStandardStatusEnum.NORMAL);

			}
			if (CollectionUtils.isNotEmpty(examitemStandards) && examitemStandards.size() > 0) {
				// 存储标准单项 和 标准单项名称
				Map<Integer, String> map = examitemStandards.stream()
						.filter(examitemStandard -> (examitemStandard.getStatus() != null
								&& examitemStandard.getStatus().intValue() == 1))
						.collect(Collectors.toMap(ExamitemStandard::getId, ExamitemStandard::getName));
				// 存储 单项Id 标准单项id 关联
				Map<Integer, Integer> relationMap = examItemStandardRelations.stream().collect(Collectors.toMap(
						ExamItemStandardRelation::getExamItemId, ExamItemStandardRelation::getStandardLibraryId));
				getHospitalExamItemVos(examItems, hospitalExamItemVos, map, relationMap);
			} else {
				getHospitalExamItemVos(examItems, hospitalExamItemVos);
			}
		}
		return hospitalExamItemVos;
	}
	
	private void getHospitalExamItemVos(List<ExamItem> examItems, List<HospitalExamItemVo> hospitalExamItemVos) {
		examItems.forEach(examItem -> {
			HospitalExamItemVo hvo = new HospitalExamItemVo();
			// 单项id 、 名称
			ExamItemIdAndName examItemIdAndName = new ExamItemIdAndName();
			examItemIdAndName.setId(examItem.getId());
			examItemIdAndName.setName(examItem.getName());
			examItemIdAndName.setShow(examItem.isShow());
			hvo.setExamItem(examItemIdAndName);
			// 标准单项id 、 名称
			// ExamItemStandardIdAndName examItemStandardIdAndName = new ExamItemStandardIdAndName();
			// examItemStandardIdAndName.setId(examItem.getId());
			// examItemStandardIdAndName.setName(examItem.getName());
			// hvo.setExamItemStandard(examItemStandardIdAndName);
			hvo.setIsRelevance(0);
			hospitalExamItemVos.add(hvo);
		});
	}

	private void getHospitalExamItemVos(List<ExamItem> examItems, List<HospitalExamItemVo> hospitalExamItemVos,
			Map<Integer, String> map, Map<Integer, Integer> relationMap) {
		examItems.forEach(examItem -> {
			HospitalExamItemVo hvo = new HospitalExamItemVo();
			ExamItemIdAndName examItemIdAndName = new ExamItemIdAndName();
			examItemIdAndName.setId(examItem.getId());
			examItemIdAndName.setName(examItem.getName());
			examItemIdAndName.setShow(examItem.isShow());
			hvo.setExamItem(examItemIdAndName);
			if (relationMap.containsKey(examItem.getId()) && map.containsKey(relationMap.get(examItem.getId()))) {
				ExamItemStandardIdAndName examItemStandardIdAndName = new ExamItemStandardIdAndName();
				examItemStandardIdAndName.setId(relationMap.get(examItem.getId()));
				examItemStandardIdAndName.setName(map.get(relationMap.get(examItem.getId())));
				hvo.setExamItemStandard(examItemStandardIdAndName);
				hvo.setIsRelevance(ExamItemStandardRelationStatusEnum.RELATED.getCode()); // 关联
			} else {
				hvo.setIsRelevance(ExamItemStandardRelationStatusEnum.UNRELATED.getCode()); // 未关联
			}
			hospitalExamItemVos.add(hvo);
		});
	}

	@Override
	public void updateExamItemStandardRelationByExamItemStandardIds(List<Integer> examItemStandardIds,
			ExamItemStandardRelationStatusEnum unrelated) {
		examItemStandardRelationService.updateByExamItemStandardIds(examItemStandardIds, unrelated);
	}

	@Override
	public ExamItemStandardListVo listByExamItemStandardQuery(ExamItemStandardQuery examItemStandardQueryy) {
		List<ExamitemStandard> examitemStandards = examItemStandardService
				.listByExamItemStandardQuery(examItemStandardQueryy);
		int totalCount = 0;
		int invalidCount = 0;
		Map<Integer, String> speciesIdAndNameMap = null;
		ExamItemStandardListVo examItemStandardListVo = new ExamItemStandardListVo();
		if (examitemStandards != null) {
			totalCount = examitemStandards.size();
			Set<Integer> crmClassIds = Sets.newHashSet();
			Set<Integer> clientClassIds = Sets.newHashSet();
			invalidCount = getInvalidCountAndClassifyIds(examitemStandards, crmClassIds, clientClassIds);
			speciesIdAndNameMap = getSpeciesIdAndName(getExamItemSpecies(crmClassIds, clientClassIds));
			examItemStandardListVo.setInvaildCount(invalidCount);
			examItemStandardListVo.setTotalCount(totalCount);
			examItemStandardListVo.setList(getExamItemStandardClassifyName(examitemStandards, speciesIdAndNameMap));
		}
		return examItemStandardListVo;
	}

	/*	private String getExamItemSpeciesName(Integer speciesId) {
			if (speciesId == null) {
				return null;
			}
			
			if (speciesMap.isEmpty()) {
				List<ExamItemSpecies> crmExamItemSpecies = examItemSpeciesService.getSpeciesByType(ExamItemStandardClassTypeEnum.CRM_CLASSIFY.getCode());
				List<ExamItemSpecies> clientExamItemSpecies = examItemSpeciesService.getSpeciesByType(ExamItemStandardClassTypeEnum.CLIENT_CLASSIFY.getCode());
				if (CollectionUtils.isNotEmpty(crmExamItemSpecies) && crmExamItemSpecies.size() > 0) {
					crmExamItemSpecies.forEach(crmExamItemSpecie -> {
						speciesMap.put(crmExamItemSpecie.getId(), crmExamItemSpecie.getName());
					});
				}
	
				if (CollectionUtils.isNotEmpty(clientExamItemSpecies) && clientExamItemSpecies.size() > 0) {
					clientExamItemSpecies.forEach(clientExamItemSpecie -> {
						speciesMap.put(clientExamItemSpecie.getId(), clientExamItemSpecie.getName());
					});
				}
			}
			
			return speciesMap.get(speciesId);
		}*/

}
