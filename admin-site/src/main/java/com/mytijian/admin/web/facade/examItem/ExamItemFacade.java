package com.mytijian.admin.web.facade.examItem;

import java.util.List;

import com.mytijian.admin.web.vo.examitem.ExamItemStandardListVo;
import com.mytijian.admin.web.vo.examitem.ExamItemStandardVo;
import com.mytijian.admin.web.vo.examitem.HospitalExamItemVo;
import com.mytijian.offer.examitem.exception.ItemManageException;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.model.ItemSettingValue;
import com.mytijian.product.item.constant.enums.ExamItemStandardRelationStatusEnum;
import com.mytijian.product.item.param.ExamItemStandardQuery;

/**
 * 
 * 
 * @author 
 *
 */
public interface ExamItemFacade {

	@Deprecated
	ExamItemStandardListVo listByKeywordsAndClass(String keywords, Integer clientClassify, Integer crmClassify);

	ExamItemStandardListVo listByExamItemStandardQuery(ExamItemStandardQuery examItemStandardQuery);

	/**
	 * 根据id获取标准单项信息
	 * @param id
	 * @return
	 */
	ExamItemStandardVo getExamitemStandardById(Integer id);

	/**
	 * 根据医院Id获取医院标准单项映射关系
	 * @param hospitalId
	 * @param examItemKeywords
	 */
	List<HospitalExamItemVo> listMacHospitalExamItemStandardMapsByHospitalId(Integer hospitalId,
			String examItemKeywords);

	/**
	 * 根据医院Id和医院单项Id获取医院单项信息
	 * @param hospitalId 医院Id 选填
	 * @param examItemId 医院单项Id 必填
	 * @return
	 */
	ExamItem getHospitalExamItemByHospitalId(Integer hospitalId, Integer examItemId);

	/**
	 * 覆盖医院单项信息
	 * @param examItem
	 * @param standardId
	 * @throws ItemManageException 
	 */
	// void overrideHospitalExamItem(ExamItem examItem, Integer standardId);

	void overrideHospitalExamItem(Integer standardId, ItemSettingValue itemSettingValue, Integer examItemId,
			Integer hospitalId, Integer isRelevance) throws ItemManageException;

	/**
	 * 更新标准单项映射
	 * @param examItemStandardIds
	 * @param status
	 */
	void updateExamItemStandardRelationByExamItemStandardIds(List<Integer> examItemStandardIds,
			ExamItemStandardRelationStatusEnum unrelated);

}
