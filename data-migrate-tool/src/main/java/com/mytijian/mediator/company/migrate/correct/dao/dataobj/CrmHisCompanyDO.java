package com.mytijian.mediator.company.migrate.correct.dao.dataobj;

import java.io.Serializable;

public class CrmHisCompanyDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8190328739088622668L;

	private Integer id;

	private Integer tbExamCompanyId;

	private String companyName;

	private Integer hospitalId;

	private Integer newCompanyId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTbExamCompanyId() {
		return tbExamCompanyId;
	}

	public void setTbExamCompanyId(Integer tbExamCompanyId) {
		this.tbExamCompanyId = tbExamCompanyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

}
