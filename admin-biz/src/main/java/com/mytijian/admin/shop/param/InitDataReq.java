package com.mytijian.admin.shop.param;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mytijian.organization.model.OrganizationManager;
import com.mytijian.organization.model.OrganizationMediatorInfo;
import com.mytijian.resource.model.Address;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalPeriodSetting;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.site.model.Site;
import com.mytijian.site.model.SiteTemplate;

public class InitDataReq {

	private Hospital hospital;
	private HospitalSettings settings;
	private Address address;
	private Site site;
	private SiteTemplate siteTemplate;
	
	private List<HospitalPeriodSetting> periodSettingList;
	
	private OrganizationMediatorInfo mediatorInfo;
	
	private OrganizationManager manager;
	
	private String opsManagerKeys;

	private Map<String, Integer> limitNumMap;

	public Map<String, Integer> getLimitNumMap() {
		return limitNumMap;
	}

	public void setLimitNumMap(Map<String, Integer> limitNumMap) {
		this.limitNumMap = limitNumMap;
	}

	public Hospital getHospital() {
		return hospital;
	}
	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}
	public HospitalSettings getSettings() {
		return settings;
	}
	public void setSettings(HospitalSettings settings) {
		this.settings = settings;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public SiteTemplate getSiteTemplate() {
		return siteTemplate;
	}
	public void setSiteTemplate(SiteTemplate siteTemplate) {
		this.siteTemplate = siteTemplate;
	}
	public List<HospitalPeriodSetting> getPeriodSettingList() {
		return periodSettingList;
	}
	public void setPeriodSettingList(List<HospitalPeriodSetting> periodSettingList) {
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
	public String getOpsManagerKeys() {
		return opsManagerKeys;
	}
	public void setOpsManagerKeys(String opsManagerKeys) {
		this.opsManagerKeys = opsManagerKeys;
	}
	
}
