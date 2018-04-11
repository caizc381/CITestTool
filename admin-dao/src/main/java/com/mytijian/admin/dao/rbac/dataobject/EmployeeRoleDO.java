package com.mytijian.admin.dao.rbac.dataobject;

import java.io.Serializable;

/**
 * 用户角色关联表
 * @author mytijian
 *
 */
public class EmployeeRoleDO  implements Serializable{
	
	private static final long serialVersionUID = -486609037726784573L;

	private Integer id;
	
	private Integer employeeId;
	
	private Integer roleId;

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

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	
}
