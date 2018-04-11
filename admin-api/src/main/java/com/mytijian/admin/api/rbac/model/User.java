package com.mytijian.admin.api.rbac.model;

import java.io.Serializable;
import java.util.Date;

import com.mytijian.admin.api.rbac.constant.RoleType;


public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7174178076180623540L;

	private Integer id;
	
	private String loginName;//登录名称
	
	private String password;//用户密码
	
	private String salt;//密码加盐
	
	private Date gmtCreated;//创建时间
	
	private Date gmtModified;//修改时间
	
	private RoleType roleType;

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

	public RoleType getRoleType() {
		return roleType;
	}

	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}
	
	public String getRoleTypeText() {
        return roleType == null ? null : roleType.getText();
    }
}
