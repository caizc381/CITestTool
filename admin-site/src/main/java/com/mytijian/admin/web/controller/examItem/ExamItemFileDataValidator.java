/*
 * Copyright 2018 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.web.controller.examItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mytijian.admin.web.vo.examitem.ExamItemImportResultVO.ErrorAttr;
import com.mytijian.admin.web.vo.examitem.ExamItemImportResultVO.ErrorExamItem;
import com.mytijian.common.util.AssertUtil;

/**
 * 类ExamItemFileDataValidator.java的实现描述：TODO 类实现描述 
 * @author csj 2018年1月30日 下午3:33:27
 */
public class ExamItemFileDataValidator {
	/**
	 * 单项数据校验接口
	 * @param ExamItemInfos
	 * @return
	 */
	public static Result<List<ErrorExamItem>, List<ExamItemUpdate>> validateExamItemFileData(
			List<ExamItemFile> ExamItemInfos) {
		Result<List<ErrorExamItem>, List<ExamItemUpdate>> result = new Result<List<ErrorExamItem>, List<ExamItemUpdate>>();
		List<ErrorExamItem> errorExamItemList = new ArrayList<ErrorExamItem>();
		List<ExamItemUpdate> updateExamItemList = new ArrayList<ExamItemUpdate>();
		boolean isHisIdEmpty = false;
		for (ExamItemFile examItemFile : ExamItemInfos) {
			Boolean isCorrect = true;
			ErrorExamItem errorExamItem = new ErrorExamItem();
			ExamItemUpdate examItem = new ExamItemUpdate();
			List<ErrorAttr> errorAttrList = new ArrayList<ErrorAttr>();
			// his编码
			if (AssertUtil.isEmpty(examItemFile.getHisId())) {
				isCorrect = false;
				isHisIdEmpty = true;
				errorAttrList.add(new ErrorAttr("His编码", "his编码必填"));
			} else {
				examItem.setHisItemId(examItemFile.getHisId());
			}
			// 截取项目名称前256个字
			isCorrect = validateItemName(examItemFile, isCorrect, examItem, errorAttrList);
			// 校验性别是否合法
			isCorrect = validateGender(examItemFile, isCorrect, examItem, errorAttrList);
			// 校验价格为两位正小数
			isCorrect = validatePrice(examItemFile, isCorrect, examItem, errorAttrList);
			// 校验标准项ID
			isCorrect = validateStandardLibrary(examItemFile, isCorrect, examItem, errorAttrList);
			// 适宜人群
			validateFitPeople(examItemFile, examItem);
			// 不适宜人群
			validateUnfitPeople(examItemFile, examItem);
			// 项目简介
			validateDescription(examItemFile, examItem);
			// 项目详情
			validateDetail(examItemFile, examItem);

			if (isCorrect == false) {
				errorExamItem.setErrorExamItemName(examItemFile.getName());
				errorExamItem.setErrorAttrs(errorAttrList);
				errorExamItemList.add(errorExamItem);
			} else {
				updateExamItemList.add(examItem);
			}

		}

		if (!isHisIdEmpty) {
			Map<String, Long> map = ExamItemInfos.stream()
					.collect(Collectors.groupingBy(ExamItemFile::getHisId, Collectors.counting()));
			for (Map.Entry<String, Long> entry : map.entrySet()) {
				if (entry.getValue() > 1) {
					ErrorExamItem errorExamItem = new ErrorExamItem();
					List<ErrorAttr> errorAttrList = new ArrayList<ErrorAttr>();
					errorExamItem.setErrorExamItemName(entry.getKey());
					errorAttrList.add(new ErrorAttr("his编码", "重复"));
					errorExamItem.setErrorAttrs(errorAttrList);
					errorExamItemList.add(errorExamItem);
				}
			}
		}
		result.setA(errorExamItemList);
		result.setB(updateExamItemList);
		return result;
	}

	/**
	 * @param examItemFile
	 * @param examItem
	 */
	private static void validateDetail(ExamItemFile examItemFile, ExamItemUpdate examItem) {
		if (AssertUtil.isNotEmpty(examItemFile.getDetail())) {
			if (examItemFile.getDetail().length() > 4096) {
				examItem.setDetail(examItemFile.getDetail().substring(0, 4096));
			} else {
				examItem.setDetail(examItemFile.getDetail());
			}
		}
	}

	/**
	 * @param examItemFile
	 * @param examItem
	 */
	private static void validateDescription(ExamItemFile examItemFile, ExamItemUpdate examItem) {
		if (AssertUtil.isNotEmpty(examItemFile.getDescription())) {
			if (examItemFile.getDescription().length() > 512) {
				examItem.setDescription(examItemFile.getDescription().substring(0, 512));
			} else {
				examItem.setDescription(examItemFile.getDescription());
			}
		}
	}

	/**
	 * @param examItemFile
	 * @param examItem
	 */
	private static void validateUnfitPeople(ExamItemFile examItemFile, ExamItemUpdate examItem) {
		if (AssertUtil.isNotEmpty(examItemFile.getUnfitPeople())) {
			if (examItemFile.getUnfitPeople().length() > 256) {
				examItem.setUnfitPeople(examItemFile.getUnfitPeople().substring(0, 256));
			} else {
				examItem.setUnfitPeople(examItemFile.getUnfitPeople());
			}
		}
	}

	/**
	 * @param examItemFile
	 * @param examItem
	 */
	private static void validateFitPeople(ExamItemFile examItemFile, ExamItemUpdate examItem) {
		if (AssertUtil.isNotEmpty(examItemFile.getFitPeople())) {
			if (examItemFile.getFitPeople().length() > 256) {
				examItem.setFitPeople(examItemFile.getFitPeople().substring(0, 256));
			} else {
				examItem.setFitPeople(examItemFile.getFitPeople());
			}
		}
	}

	/**
	 * @param examItemFile
	 * @param isCorrect
	 * @param examItem
	 * @param errorAttrList
	 * @return
	 */
	private static Boolean validateStandardLibrary(ExamItemFile examItemFile, Boolean isCorrect,
			ExamItemUpdate examItem, List<ErrorAttr> errorAttrList) {
		if (examItemFile.getStandardLibraryId() != null) {

			if (examItemFile.getStandardLibraryId().matches("[0-9]+")) {
				examItem.setStandardLibraryId(Integer.parseInt(examItemFile.getStandardLibraryId()));
			} else {
				isCorrect = false;
				errorAttrList.add(new ErrorAttr("标准项", "标准项不存在"));
			}
		}
		return isCorrect;
	}

	/**
	 * @param examItemFile
	 * @param isCorrect
	 * @param examItem
	 * @param errorAttrList
	 * @return
	 */
	private static Boolean validatePrice(ExamItemFile examItemFile, Boolean isCorrect, ExamItemUpdate examItem,
			List<ErrorAttr> errorAttrList) {
		if (AssertUtil.isEmpty(examItemFile.getPrice())) {
			isCorrect = false;
			errorAttrList.add(new ErrorAttr("价格", "价格必填"));
		} else {
			if (examItemFile.getPrice()
					.matches("(^[1-9]([0-9]+)?(\\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9])?$)")) {
				Double fen = Double.parseDouble(examItemFile.getPrice()) * 100;
				examItem.setPrice(Integer.valueOf(fen.intValue()));
			} else {
				isCorrect = false;
				errorAttrList.add(new ErrorAttr("价格", "内容不合法"));
			}
		}
		return isCorrect;
	}

	/**
	 * @param examItemFile
	 * @param isCorrect
	 * @param examItem
	 * @param errorAttrList
	 * @return
	 */
	private static Boolean validateGender(ExamItemFile examItemFile, Boolean isCorrect, ExamItemUpdate examItem,
			List<ErrorAttr> errorAttrList) {
		if (changeGender(examItemFile.getGender()) != 0 && changeGender(examItemFile.getGender()) != 1
				&& changeGender(examItemFile.getGender()) != 2) {
			isCorrect = false;
			errorAttrList.add(new ErrorAttr("性别", "内容不合法"));
		} else {
			examItem.setGender(changeGender(examItemFile.getGender()));
		}
		return isCorrect;
	}

	/**
	 * @param examItemFile
	 * @param isCorrect
	 * @param examItem
	 * @param errorAttrList
	 * @return
	 */
	private static Boolean validateItemName(ExamItemFile examItemFile, Boolean isCorrect, ExamItemUpdate examItem,
			List<ErrorAttr> errorAttrList) {
		if (AssertUtil.isEmpty(examItemFile.getName())) {
			isCorrect = false;
			errorAttrList.add(new ErrorAttr("项目名称", "项目名称必填"));
		} else {
			if (examItemFile.getName().length() > 256) {
				examItem.setName(examItemFile.getName().substring(0, 256));
			} else {
				examItem.setName(examItemFile.getName());
			}
		}
		return isCorrect;
	}

	private static Integer changeGender(String gender) {
		if ("男".equals(gender)) {
			return 0;
		}
		if ("女".equals(gender)) {
			return 1;
		}
		if ("通用".equals(gender)) {
			return 2;
		}
		return 3;
	}

}

class Result<A, B> {
	private A a;
	private B b;

	public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}

}