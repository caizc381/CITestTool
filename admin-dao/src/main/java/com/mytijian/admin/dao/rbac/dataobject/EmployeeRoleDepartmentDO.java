package com.mytijian.admin.dao.rbac.dataobject;

import java.io.Serializable;

/**
 * 用户角色部门
 * @author mytijian
 *
 */
public class EmployeeRoleDepartmentDO  implements Serializable{
	
	private static final long serialVersionUID = -2723115295249020760L;

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
