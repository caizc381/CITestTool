package com.mytijian.admin.api.rbac.model;

import java.io.Serializable;
import java.util.List;

public class RoleDepartmentMenu implements Serializable {
	
	private Integer id;
	
	private Integer roleDepartmentId;
	
	private Integer menuId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRoleDepartmentId() {
		return roleDepartmentId;
	}

	public void setRoleDepartmentId(Integer roleDepartmentId) {
		this.roleDepartmentId = roleDepartmentId;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
	
	/*private static final long serialVersionUID = 1855839421902221785L;
	
	private Integer id;
	
	*//**
	 * 权限名称
	 *//*
	private String permissionName;
	
	*//**
	 * 部门Id
	 *//*
	private Integer departmentId;
	
	*//**
	 *  角色Id
	 *//*
	private Integer roleId;
	
	*//**
	 * 职工Id
	 *//*
	private Integer employeeId;
	
	*//**
	 * @see 
	 *//*
	private Integer status;
	
	private List<Integer> menuIdList;

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

	public List<Integer> getMenuIdList() {
		return menuIdList;
	}

	public void setMenuIdList(List<Integer> menuIdList) {
		this.menuIdList = menuIdList;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}*/
}
