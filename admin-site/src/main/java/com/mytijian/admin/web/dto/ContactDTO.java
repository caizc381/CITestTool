package com.mytijian.admin.web.dto;

import java.util.List;

import com.mytijian.gotone.api.model.beans.HospitalContact;

public class ContactDTO {
	Integer orgId;
	List<HospitalContact> contacts;
	
	public Integer getOrgId() {
		return orgId;
	}
	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}
	public List<HospitalContact> getContacts() {
		return contacts;
	}
	public void setContacts(List<HospitalContact> contacts) {
		this.contacts = contacts;
	}
	
	
}
