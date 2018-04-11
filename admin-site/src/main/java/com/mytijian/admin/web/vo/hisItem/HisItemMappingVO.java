package com.mytijian.admin.web.vo.hisItem;

public class HisItemMappingVO {

	private int hisItemStandardId;
	
	private int hisItemId;
	
	private int hospitalId;
	
	private Integer unitId;
	
	private Boolean isMappingUnit = false;
	
	private Boolean isMappingMaxResult = false;
	
	private Boolean isMappingMinResult = false;
	
	private Boolean isMappingDetail = false;
	
	private Boolean isMappingAdvance = false;

	public int getHisItemStandardId() {
		return hisItemStandardId;
	}

	public void setHisItemStandardId(int hisItemStandardId) {
		this.hisItemStandardId = hisItemStandardId;
	}

	public int getHisItemId() {
		return hisItemId;
	}

	public void setHisItemId(int hisItemId) {
		this.hisItemId = hisItemId;
	}

	public int getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(int hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public Boolean getIsMappingUnit() {
		return isMappingUnit;
	}

	public void setIsMappingUnit(Boolean isMappingUnit) {
		this.isMappingUnit = isMappingUnit;
	}

	public Boolean getIsMappingMaxResult() {
		return isMappingMaxResult;
	}

	public void setIsMappingMaxResult(Boolean isMappingMaxResult) {
		this.isMappingMaxResult = isMappingMaxResult;
	}

	public Boolean getIsMappingMinResult() {
		return isMappingMinResult;
	}

	public void setIsMappingMinResult(Boolean isMappingMinResult) {
		this.isMappingMinResult = isMappingMinResult;
	}

	public Boolean getIsMappingDetail() {
		return isMappingDetail;
	}

	public void setIsMappingDetail(Boolean isMappingDetail) {
		this.isMappingDetail = isMappingDetail;
	}

	public Boolean getIsMappingAdvance() {
		return isMappingAdvance;
	}

	public void setIsMappingAdvance(Boolean isMappingAdvance) {
		this.isMappingAdvance = isMappingAdvance;
	}

}
