package com.mytijian.admin.api.rbac.service;

import java.util.List;

import com.mytijian.admin.api.rbac.model.RoleDepartmentMenu;




/**
 * 角色与菜单对应关系
 * 
 * @author feng
 * @email 373680866@qq.com
 * @date 2017年02月27日 上午10:42:30
 */
public interface RoleDepartmentMenuService {
	
	
	//List<RoleDepartmentMenu> listByIds(List<Integer> roleDepartmentIds);
	
	/**
	 * 根据角色部门Id获取角色部门菜单关联信息
	 * @param roleDepartmentId
	 * @return
	 */
	List<RoleDepartmentMenu> listByRoleDepartId(Integer roleDepartmentId);
	
	/**
	 * 根据角色部门Ids获取角色部门菜单关联信息
	 * @param roleDepartmentId
	 * @return
	 */
	List<RoleDepartmentMenu> listByRoleDepartmentIds(List<Integer> roleDepartmentIds);
	
	/**
	 * 根据Id获取角色部门菜单关联信息
	 * @param roleDepartmentId
	 * @return
	 */
	List<RoleDepartmentMenu> listByIds(List<Integer> ids);
	
	/**
	 * 根据用户Id获取角色部门菜单关联信息
	 * @param roleDepartmentId
	 * @return
	 */
	//List<RoleDepartmentMenu> listByEmployeeId(Integer employeeId);
	
	/**
	 * 保存角色部门菜单关联信息
	 * @param roleDeaprtmentMenuId
	 * @param menuIdList
	 */
	void saveRoleDepartmentMenu(Integer roleDeaprtmentMenuId, List<Integer> menuIdList);
	
	/**
	 * 删除角色部门菜单关联信息
	 * @param roleDeaprtmentMenuId
	 * @param menuIdList
	 */
	void deleteRoleDepartmentMenu(Integer roleDeaprtmentMenuId);
	
	/**
	 * 根据角色部门Ids删除角色部门菜单关联信息
	 * @param roleDeaprtmentMenuId
	 * @param menuIdList
	 */
	void deleteByRoleDepartmentIds(List<Integer> roleDeaprtmentMenuId);
	
	/**
	 * 根据角色ID，获取菜单ID列表
	 */
	List<Integer> queryMenuIdList(Integer roleId);
	
	
	
}
