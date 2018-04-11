package com.mytijian.admin.web.vo.hospital;

import com.mytijian.resource.model.Hospital;

public class HospitalSiteVO {

	private Hospital hospital;
	
	private String color;
	
	private Integer showInvoice;
	
	private Integer templateId;
	
	private String templateName;
	
	private String url;

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getShowInvoice() {
		return showInvoice;
	}

	public void setShowInvoice(Integer showInvoice) {
		this.showInvoice = showInvoice;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
