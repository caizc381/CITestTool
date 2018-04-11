package com.mytijian.admin.api.rbac.service;

import java.util.List;

import com.mytijian.admin.api.rbac.model.Role;



/**
 * 角色
 * 
 * @author 
 * @email 
 * @date 
 */
public interface RoleService {
	
	/**
	 * 根据角色Id获取角色信息
	 * @param roleId
	 * @return
	 */
	Role getRoleInfoById(Integer roleId);
	
	/**
	 * 获取角色列表
	 * @param roleName 角色名称
	 * @param offset 起始索引
	 * @param limit 查询条数
	 * @return
	 */
	List<Role> listRoles(String roleName, Integer offset, Integer limit);
	
	/**
	 * 
	 * @param roleIds
	 * @return
	 */
	List<Role> listByIds(List<Integer> roleIds);
	
	/**
	 * 查询角色总数
	 * @param roleName
	 * @param offset
	 * @param limit
	 * @return
	 */
	int countTotal(String roleName, Integer offset, Integer limit);
	
	/**
	 * 保存角色
	 * @param role
	 */
	void saveRole(Role role);
	
	/**
	 * 更新角色
	 * @param role
	 */
	void updateRole(Role role);
	
	/**
	 * 批量删除角色
	 * @param roleIds
	 */
	void deleteBatch(List<Integer> roleIds);
	
	/**
	 * 保存更新角色
	 * @param employeeId
	 * @param roleds
	 */
	//void saveRole(Integer employeeId, List<Integer> roleds);
}
