package com.mytijian.admin.api.rbac.service;

import java.util.List;

import com.mytijian.admin.api.rbac.model.DepartmentEmployee;



/**
 * 用户部门关系
 * 
 * @author feng
 * @email 
 * @date 
 */
public interface DepartmentEmployeeService {
	
	/**
	 * 保存用户部门关系
	 */
	void saveDepartmentEmployee(DepartmentEmployee departmentEmployee);
	
	/**
	 * 保存用户部门关系
	 */
	void saveEmployeeDepartment(Integer employeeId, Integer departmentId);
	
	/**
	 * 更新用户部门关系
	 * @param employeeId
	 * @param departmentId
	 */
	void updateEmployeeDepartment(Integer employeeId, Integer departmentId);
	
	/**
	 * 获取用户部门关系
	 * @param employeeId
	 * @return
	 */
	DepartmentEmployee getByEmployeeId(Integer employeeId);
	
	/**
	 * 根据部门Ids 删除用户部门关系
	 * @param departmentIds
	 */
	void deleteBatchByDepartmentIds(List<Integer> departmentIds);

}
