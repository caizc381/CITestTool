package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;

public class ManagerAccountCompanyRelationDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4368108240240832356L;
	
	
	private Integer managerId;
	
	private Integer accountCompanyId;
	
	private Integer isSitePay;
	
	private Integer importWithoutIdcard;
	
	private Integer orderImmediately;
	
	private Integer keepLogin;
	
	private Integer removeAllItems;
	
	private Integer agentReserve;

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Integer getAccountCompanyId() {
		return accountCompanyId;
	}

	public void setAccountCompanyId(Integer accountCompanyId) {
		this.accountCompanyId = accountCompanyId;
	}

	public Integer getIsSitePay() {
		return isSitePay;
	}

	public void setIsSitePay(Integer isSitePay) {
		this.isSitePay = isSitePay;
	}

	public Integer getImportWithoutIdcard() {
		return importWithoutIdcard;
	}

	public void setImportWithoutIdcard(Integer importWithoutIdcard) {
		this.importWithoutIdcard = importWithoutIdcard;
	}

	public Integer getOrderImmediately() {
		return orderImmediately;
	}

	public void setOrderImmediately(Integer orderImmediately) {
		this.orderImmediately = orderImmediately;
	}

	public Integer getKeepLogin() {
		return keepLogin;
	}

	public void setKeepLogin(Integer keepLogin) {
		this.keepLogin = keepLogin;
	}

	public Integer getRemoveAllItems() {
		return removeAllItems;
	}

	public void setRemoveAllItems(Integer removeAllItems) {
		this.removeAllItems = removeAllItems;
	}

	public Integer getAgentReserve() {
		return agentReserve;
	}

	public void setAgentReserve(Integer agentReserve) {
		this.agentReserve = agentReserve;
	}
	
}
