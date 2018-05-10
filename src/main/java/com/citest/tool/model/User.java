package com.citest.tool.model;

import java.io.Serializable;

import com.citest.tool.common.DomainObjectBase;

public class User extends DomainObjectBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2813916253568453283L;

	private Integer id;
	
	private Integer accountId;
	
	private String  username;
	private String password;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
