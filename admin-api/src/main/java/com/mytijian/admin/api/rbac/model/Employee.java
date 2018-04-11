package com.mytijian.admin.api.rbac.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Employee  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2802727266981755859L;
	
	private Integer id;
	
	/**
	 * 登录名称
	 */
	private String loginName;
	
	/**
	 * 员工编号
	 */
	private String employeeNo;
	
	/**
	 * 员工名称
	 */
	private String employeeName;
	
	/**
	 * 员工昵称
	 */
	private String nickname;
	
	/**
	 * 用户密码
	 */
	private String password;
	
	/**
	 * 默认 UserStatusEnum.NORMAL
	 * @see com.mytijian.mediator.ops.user.enums.EmployeeStatusEnum
	 */
	private Integer status;
	
	/**
	 * 手机号
	 */
	private String mobile;
	
	/**
	 * 密码加盐
	 */
	private String salt;
	
	private Date gmtCreated;
	
	private String departName;
	
	private Integer departmentId;
	
	private String pinYin;
	
	/**
	 * 所属上级Id列表
	 */
	private List<Integer> parentDepartmentIdList;
	
	private List<Integer> roleIds;
	
	private List<Role> roles;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getGmtCreated() {
		return gmtCreated;
	}

	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Employee() {
		super();
	}

	public List<Integer> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Integer> roleIds) {
		this.roleIds = roleIds;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Integer> getParentDepartmentIdList() {
		return parentDepartmentIdList;
	}
	
	public String getPinYin() {
		return pinYin;
	}

	public void setPinYin(String pinYin) {
		this.pinYin = pinYin;
	}

	public void setParentDepartmentIdList(List<Integer> parentDepartmentIdList) {
		this.parentDepartmentIdList = parentDepartmentIdList;
	}

}
