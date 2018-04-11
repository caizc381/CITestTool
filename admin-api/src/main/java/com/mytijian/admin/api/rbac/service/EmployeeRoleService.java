package com.mytijian.admin.api.rbac.service;

import java.util.List;

import com.mytijian.admin.api.rbac.model.EmployeeRole;



/**
 * 用户角色
 * 
 * @author 
 * @email 
 * @date 
 */
public interface EmployeeRoleService {
	
	/**
	 * 根据用户ID，获取角色ID列表
	 */
	List<EmployeeRole> listEmployeeRoles(Integer employeeId);
	
	/**
	 * 删除用户角色
	 * @param employeeId 职工编号
	 */
	void deleteEmployeeRole(Integer employeeId);
		
	/**
	 * 保存用户角色
	 * @param employeeId 职工Id
	 * @param roleIds 角色
	 * @return
	 */
	void saveEmployeeRole(Integer employeeId, List<Integer> roleIds);
}
