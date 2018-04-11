/*
 * Copyright 2018 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.web.vo.examitem;

import java.util.List;

/**
 * 类ExamItemImportResultVO.java的实现描述：TODO 类实现描述 
 * @author csj 2018年1月30日 下午2:29:22
 */
public class ExamItemImportResultVO {
	/**
	*如果成功 ，成功导入数量有值。如果失败，错误列表里有值
	*/
	private boolean isSuccess;
	private Integer successExamItemNumber;
	private List<ErrorExamItem> errorExamItem;

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public Integer getSuccessExamItemNumber() {
		return successExamItemNumber;
	}

	public void setSuccessExamItemNumber(Integer successExamItemNumber) {
		this.successExamItemNumber = successExamItemNumber;
	}

	public List<ErrorExamItem> getErrorExamItem() {
		return errorExamItem;
	}

	public void setErrorExamItem(List<ErrorExamItem> errorExamItem) {
		this.errorExamItem = errorExamItem;
	}

	public static class ErrorExamItem {
		private String errorExamItemName;
		private List<ErrorAttr> errorAttrs;

		public String getErrorExamItemName() {
			return errorExamItemName;
		}

		public void setErrorExamItemName(String errorExamItemName) {
			this.errorExamItemName = errorExamItemName;
		}

		public List<ErrorAttr> getErrorAttrs() {
			return errorAttrs;
		}

		public void setErrorAttrs(List<ErrorAttr> errorAttrs) {
			this.errorAttrs = errorAttrs;
		}

	}

	public static class ErrorAttr {
		private String name;
		private String errorDesc;

		public ErrorAttr(String name, String errorDesc) {
			super();
			this.name = name;
			this.errorDesc = errorDesc;
		}

		public String getName() {
			return name;
		}

		public String getErrorDesc() {
			return errorDesc;
		}

	}

}
