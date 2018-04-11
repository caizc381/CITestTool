package com.mytijian.admin.web.vo.hospital;

public class BasicSettingsVO {
	
	private Integer hospitalId;

	private Integer businessAmount;
	
	private String opsRemark;
	
	private Integer isBreakfast;
	
	private String groupExamTel;
	
	private String serviceTel;
	
	private String technicalTel;


	public Integer getBusinessAmount() {
		return businessAmount;
	}

	public void setBusinessAmount(Integer businessAmount) {
		this.businessAmount = businessAmount;
	}

	public String getOpsRemark() {
		return opsRemark;
	}

	public void setOpsRemark(String opsRemark) {
		this.opsRemark = opsRemark;
	}

	public Integer getIsBreakfast() {
		return isBreakfast;
	}

	public void setIsBreakfast(Integer isBreakfast) {
		this.isBreakfast = isBreakfast;
	}

	public String getGroupExamTel() {
		return groupExamTel;
	}

	public void setGroupExamTel(String groupExamTel) {
		this.groupExamTel = groupExamTel;
	}

	public String getServiceTel() {
		return serviceTel;
	}

	public void setServiceTel(String serviceTel) {
		this.serviceTel = serviceTel;
	}

	public String getTechnicalTel() {
		return technicalTel;
	}

	public void setTechnicalTel(String technicalTel) {
		this.technicalTel = technicalTel;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	
}
