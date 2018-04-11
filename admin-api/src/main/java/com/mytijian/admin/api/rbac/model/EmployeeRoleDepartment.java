package com.mytijian.admin.api.rbac.model;

public class EmployeeRoleDepartment {
	private Integer id;
	
	private Integer employeeId;
	
	private Integer roleDepartmentId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getRoleDepartmentId() {
		return roleDepartmentId;
	}

	public void setRoleDepartmentId(Integer roleDepartmentId) {
		this.roleDepartmentId = roleDepartmentId;
	}
}
