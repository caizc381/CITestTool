package com.mytijian.admin.dao.rbac.dataobject;

import java.io.Serializable;

import com.mytijian.admin.dao.base.dataobject.Base;


/**
 * 角色和菜单关系
 * @author mytijian
 *
 */
public class RoleDepartmentDO extends Base implements Serializable{
	
	private static final long serialVersionUID = -3655003334492733726L;
	
	/**
	 * 权限名称
	 */
	private String permissionName;
	
	/**
	 * 部门Id
	 */
	private Integer departmentId;
	
	/**
	 *  角色Id
	 */
	private Integer roleId;
	
	/**
	 * 职工Id
	 */
	private Integer employeeId;
	
	/**
	 * @see 
	 */
	private Integer status;

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}
	
}
