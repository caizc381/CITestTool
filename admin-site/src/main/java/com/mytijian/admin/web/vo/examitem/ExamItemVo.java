package com.mytijian.admin.web.vo.examitem;

import com.mytijian.offer.examitem.model.ExamItem;


public class ExamItemVo extends ExamItem{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1252675072580103269L;

	private Integer clientClassify;
	
	private Integer crmClassify;
	
	private String clientClassifyName;
	
	private String crmClassifyName;

	public Integer getClientClassify() {
		return clientClassify;
	}

	public void setClientClassify(Integer clientClassify) {
		this.clientClassify = clientClassify;
	}

	public Integer getCrmClassify() {
		return crmClassify;
	}

	public void setCrmClassify(Integer crmClassify) {
		this.crmClassify = crmClassify;
	}

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
