package com.mytijian.admin.dao.rbac.param;

public class RoleDepartmentDAOQuery {
	
	/**
	 * 授权名称
	 */
	private String permissionName;
	
	/**
	 * 角色名称
	 */
	private String roleName;
	
	/**
	 * 部门名称
	 */
	private String departmentName;
	
	/**
	 * 起始索引
	 */
	private Integer offset;
	
	/**
	 *  查询条数
	 */
	private Integer limit;

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

}
