package com.mytijian.admin.web.vo.hospital;

import java.util.List;

import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.organization.model.OrganizationManager;
import com.mytijian.organization.model.OrganizationMediatorInfo;
import com.mytijian.resource.model.Address;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalSettings;

public class OrganizationVo {

	private Hospital hospital;
	
	private Address address;
	
	private List<HospitalPeriodSettingVO> periodSettingList;
	
	private OrganizationMediatorInfo mediatorInfo;
	
	private OrganizationManager manager;
	
	private Employee opsManager;
	
	private HospitalSettings settings;
	
	private BasicSettingsVO basicSettings;
	
	private FunctionSettingsVO funSettings;
	
	private SiteVo siteVo;
	
	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<HospitalPeriodSettingVO> getPeriodSettingList() {
		return periodSettingList;
	}

	public void setPeriodSettingList(List<HospitalPeriodSettingVO> periodSettingList) {
		this.periodSettingList = periodSettingList;
	}

	public OrganizationMediatorInfo getMediatorInfo() {
		return mediatorInfo;
	}

	public void setMediatorInfo(OrganizationMediatorInfo mediatorInfo) {
		this.mediatorInfo = mediatorInfo;
	}


	public OrganizationManager getManager() {
		return manager;
	}

	public void setManager(OrganizationManager manager) {
		this.manager = manager;
	}

	public SiteVo getSiteVo() {
		return siteVo;
	}

	public void setSiteVo(SiteVo siteVo) {
		this.siteVo = siteVo;
	}

	public Employee getOpsManager() {
		return opsManager;
	}

	public void setOpsManager(Employee opsManager) {
		this.opsManager = opsManager;
	}

	public BasicSettingsVO getBasicSettings() {
		return basicSettings;
	}

	public void setBasicSettings(BasicSettingsVO basicSettings) {
		this.basicSettings = basicSettings;
	}

	public FunctionSettingsVO getFunSettings() {
		return funSettings;
	}

	public void setFunSettings(FunctionSettingsVO funSettings) {
		this.funSettings = funSettings;
	}

	public HospitalSettings getSettings() {
		return settings;
	}

	public void setSettings(HospitalSettings settings) {
		this.settings = settings;
	}
	
}
