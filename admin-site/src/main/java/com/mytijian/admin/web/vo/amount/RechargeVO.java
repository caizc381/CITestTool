package com.mytijian.admin.web.vo.amount;

import com.mytijian.account.model.Account;
import com.mytijian.trade.recharge.result.RechargeQueryResult;

public class RechargeVO {
	
	private RechargeQueryResult result;
	private String hospitalName;
	private String address;
	private String operator;
	private String userName;
	private Account manager;
	
	public Account getManager() {
		return manager;
	}
	public void setManager(Account manager) {
		this.manager = manager;
	}
	public RechargeQueryResult getResult() {
		return result;
	}
	public void setResult(RechargeQueryResult result) {
		this.result = result;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
