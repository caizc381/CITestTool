package com.mytijian.admin.dao.rbac.dataobject;

import java.io.Serializable;
import java.util.Date;

import com.codahale.metrics.MetricRegistryListener.Base;

public class UserDO extends Base implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5532786644426372993L;
	
	private Integer id;

	private String loginName;//登录名称
	
	private String password;//用户密码
	
	private String salt;//密码加盐
	
	private Date gmtCreated;
	
	private Date gmtModified;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Date getGmtCreated() {
		return gmtCreated;
	}

	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	
}
