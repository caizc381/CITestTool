package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;

public class AccountRoleDO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1117953961062875136L;
	
	private Integer accountId;
	
	private Integer roleId;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	
}
