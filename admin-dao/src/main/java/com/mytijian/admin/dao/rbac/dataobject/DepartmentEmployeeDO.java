package com.mytijian.admin.dao.rbac.dataobject;

import java.io.Serializable;

/**
 * 职工部门关系
 * @author feng
 *
 */
public class DepartmentEmployeeDO implements Serializable {
	
	private static final long serialVersionUID = -7016020413562880206L;

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

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}
	
	
}
