package com.mytijian.admin.web.vo.amount;

import java.util.List;

import com.mytijian.account.model.Account;
import com.mytijian.gotone.api.model.beans.HospitalContact;
import com.mytijian.organization.model.Organization;
import com.mytijian.trade.monitor.model.AmountMonitorRule;

public class AmountVO {

	private Long amount;
	private Organization org;
	private AmountMonitorRule rule;
	private List<HospitalContact> contacts;
	private Account manage;
	private String pinyin;
	private String loginName;
	
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public List<HospitalContact> getContacts() {
		return contacts;
	}
	public void setContacts(List<HospitalContact> contacts) {
		this.contacts = contacts;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Organization getOrg() {
		return org;
	}
	public void setOrg(Organization org) {
		this.org = org;
	}
	public AmountMonitorRule getRule() {
		return rule;
	}
	public void setRule(AmountMonitorRule rule) {
		this.rule = rule;
	}
	public Account getManage() {
		return manage;
	}
	public void setManage(Account manage) {
		this.manage = manage;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
}
