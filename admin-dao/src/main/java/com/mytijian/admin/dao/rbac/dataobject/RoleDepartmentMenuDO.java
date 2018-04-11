package com.mytijian.admin.dao.rbac.dataobject;

import java.io.Serializable;

/**
 * 角色菜单
 * @author mytijian
 *
 */
public class RoleDepartmentMenuDO implements Serializable{
	
	private static final long serialVersionUID = -5382417933142838904L;

	private Integer id;
	
	private Integer roleDepartmentId;
	
	private Integer menuId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public Integer getRoleDepartmentId() {
		return roleDepartmentId;
	}

	public void setRoleDepartmentId(Integer roleDepartmentId) {
		this.roleDepartmentId = roleDepartmentId;
	}
	
}
