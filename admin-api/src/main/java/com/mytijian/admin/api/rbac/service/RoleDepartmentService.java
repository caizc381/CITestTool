package com.mytijian.admin.api.rbac.service;

import java.util.List;

import com.mytijian.admin.api.rbac.model.EmployeeRoleDepartment;
import com.mytijian.admin.api.rbac.model.RoleDepartment;
import com.mytijian.admin.api.rbac.param.RoleDepartmentQuery;



/**
 * 授权
 * @author mytijian
 *
 */
public interface RoleDepartmentService {
	
	/**
	 * 绑定授权
	 * @param roleDepartment
	 */
	void addRoleDepartment(RoleDepartment roleDepartment);
	
	/**
	 * 更新授权
	 * @param roleDepartment
	 */
	void updateRoleDepartment(RoleDepartment roleDepartment);
	
	/**
	 * 删除用户授权
	 * @param employeeId
	 */
	void deleteByEmployeeId(Integer employeeId);
	
	/**
	 * 更新用户授权
	 * @param employeeId
	 * @param roleds
	 */
	void updateRoleDepartment(Integer employeeId, List<Integer> roleds);
	
	/**
	 * 根据部门和角色Id获取 角色部门关联列表
	 * @param departmentId 部门Id
	 * @param roleIds 角色Ids
	 * @return
	 */
	List<RoleDepartment> listBydepartmentIdAndRoleIds(Integer departmentId, List<Integer> roleIds);
	
	/**
	 * 根据角色、部门获取关联信息
	 * @param departmentId
	 * @param roleId
	 * @return
	 */
	RoleDepartment getBydepartmentIdAndRoleId(Integer departmentId, Integer roleId);
	
	/**
	 * 获取职工部门角色
	 * @param employeId 职工Id
	 * @return
	 */
	List<EmployeeRoleDepartment> listEmployeeRoleDepartments(Integer employeId);
	
	/**
	 * 根据id获取角色部门管理信息
	 * @param id
	 * @return
	 */
	RoleDepartment selectRoleDepartmentById(Integer id);
	
	/**
	 * 获取授权列表
	 * @param permissionName 授权索引
	 * @param offset 起始索引
	 * @param limit 查询条数
	 * @return
	 */
	List<RoleDepartment> listRoleDepartments(String permissionName, Integer offset, Integer limit);
	
	/**
	 * 获取授权列表
	 * @param permissionName 授权名称
	 * @param roleName 角色名称
	 * @param departmentName 部门名称
	 * @param offset 起始索引
	 * @param limit 查询条数
	 * @return
	 */
	List<RoleDepartment> listRoleDepartments(RoleDepartmentQuery roleDepartmentQuery);
	
	int countRoleDepartments(RoleDepartmentQuery roleDepartmentQuery);
	
	/**
	 * 获取授权列表总数
	 * @param permissionName
	 * @return
	 */
	int countRoleDepartments(String permissionName);
	
	/**
	 * 批量删除
	 * @param roleDepartmentIds
	 */
	void deleteBatch(List<Integer> roleDepartmentIds);
	//Role queryObject(Integer roleId);
	
	//List<Role> queryList(Map<String, Object> map);
	
	//List<Role> queryList(String roleName, Integer offset, Integer limit);
	
	// int queryTotal(Map<String, Object> map);
	//int queryTotal(String roleName, Integer offset, Integer limit);
	
	//void save(Role role);
	
	//void update(Role role);
	
	//void deleteBatch(List<Integer> roleIds);
	
	//void saveOrUpdate(Integer employeeId, List<Integer> roleds);

}
