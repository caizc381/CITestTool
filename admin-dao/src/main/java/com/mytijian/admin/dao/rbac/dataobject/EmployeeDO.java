package com.mytijian.admin.dao.rbac.dataobject;

import java.io.Serializable;

import com.mytijian.admin.dao.base.dataobject.Base;


/**
 * 职工
 * @author mytijian
 *
 */
public class EmployeeDO extends Base implements Serializable {
	
	private static final long serialVersionUID = 3794727542495836966L;

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
	 * 用户姓名首字母
	 */
	private String pinYin;
	
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

	public String getPinYin() {
		return pinYin;
	}

	public void setPinYin(String pinYin) {
		this.pinYin = pinYin;
	}
	
}
