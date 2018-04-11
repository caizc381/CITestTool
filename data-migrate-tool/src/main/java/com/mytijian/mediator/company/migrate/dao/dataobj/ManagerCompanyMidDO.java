package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;

public class ManagerCompanyMidDO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5927277299766810867L;
	
	private Integer managerId;
	
	private Integer examCompanyId;
	
	private Integer platformCompanyId;
	
	private String companyName;

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Integer getExamCompanyId() {
		return examCompanyId;
	}

	public void setExamCompanyId(Integer examCompanyId) {
		this.examCompanyId = examCompanyId;
	}

	public Integer getPlatformCompanyId() {
		return platformCompanyId;
	}

	public void setPlatformCompanyId(Integer platformCompanyId) {
		this.platformCompanyId = platformCompanyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
}
