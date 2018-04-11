package com.mytijian.admin.web.vo.hospital;

public class HospitalPeriodSettingVO {

	private Integer id;
	
	private Integer hospitalId;

	/**
	 * 内部使用时段 true:仅限内部使用  false:无限制
	 */
	private Boolean internalUsePeriod;
	
	private String name;
	
	/**
	 * 人数限制
	 */
	private Integer limitNum;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getInternalUsePeriod() {
		return internalUsePeriod;
	}

	public void setInternalUsePeriod(Boolean internalUsePeriod) {
		this.internalUsePeriod = internalUsePeriod;
	}

	public Integer getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
