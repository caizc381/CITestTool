package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;

public class ManagerCompanyRelationDO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6125590437721764861L;

	private Integer managerId;
	
	private Integer companyId;
	
	private Integer hospitalId;
	
	private String createManagerId;
	
	private Integer asAccountCompany;
	
	private Integer status;
	
	private String contactName;
	
	private String contactTel;
	
	private Integer newCompanyId;

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getCreateManagerId() {
		return createManagerId;
	}

	public void setCreateManagerId(String createManagerId) {
		this.createManagerId = createManagerId;
	}

	public Integer getAsAccountCompany() {
		return asAccountCompany;
	}

	public void setAsAccountCompany(Integer asAccountCompany) {
		this.asAccountCompany = asAccountCompany;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}
	
	
}
