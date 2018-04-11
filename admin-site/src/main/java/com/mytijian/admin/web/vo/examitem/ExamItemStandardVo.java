package com.mytijian.admin.web.vo.examitem;

import java.io.Serializable;

import com.mytijian.product.item.model.ExamitemStandard;


public class ExamItemStandardVo extends ExamitemStandard implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8210027815840386859L;

	/**
	 * 客户分类名称
	 */
	private String clientClassifyName;
	
	/**
	 * CRM 分类名称
	 */
	private String crmClassifyName;

	public String getClientClassifyName() {
		return clientClassifyName;
	}

	public void setClientClassifyName(String clientClassifyName) {
		this.clientClassifyName = clientClassifyName;
	}

	public String getCrmClassifyName() {
		return crmClassifyName;
	}

	public void setCrmClassifyName(String crmClassifyName) {
		this.crmClassifyName = crmClassifyName;
	}

	
	
}
