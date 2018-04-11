package com.mytijian.admin.api.rbac.model;

import java.io.Serializable;

/**
 * 部门职工
 * @author feng
 *
 */
public class DepartmentEmployee implements Serializable {

	private static final long serialVersionUID = -1763262817871101723L;

	/**
	 * 主键
	 */
	private Integer id;
	
	/**
	 * 部门Id
	 */
	private Integer departmentId;

	/**
	 * 职工Id
	 */
	private Integer employeeId;

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

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

}
