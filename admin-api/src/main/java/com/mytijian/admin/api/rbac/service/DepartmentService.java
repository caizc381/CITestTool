package com.mytijian.admin.api.rbac.service;

import java.util.List;

import com.mytijian.admin.api.rbac.model.Department;

/**
 * 部门管理
 * 
 * @author feng
 * @email 
 * @date 
 */
public interface DepartmentService {
	
	/**
	 * 获取全部部门列表
	 * @return
	 */
	public List<Department> listDepartments(String departmentName, Integer offset, Integer limit);
	
	/**
	 * 获取部门总数
	 * @param departmentName
	 * @return
	 */
	public int countTotal(String departmentName);
	
	/**
	 * 获取部门列表
	 * @param departmentName 部门名称
	 * @param parentId
	 * @param offset
	 * @param limit
	 * @return
	 */
	// public List<Department> listDepartment(String departmentName, Integer parentId, Integer offset, Integer limit);
	
	/**
	 * 按照父子关系返回部门列表
	 * @return
	 */
	public List<Department> listDepartmentTree();
	
	/**
	 * 获取职工部门信息
	 * @param employeeId
	 */
	public Department getByEmployeeId(Integer employeeId);
	
	/**
	 * 根据用户Id获取用户部门信息
	 * @param employeeId
	 * @return
	 */
	//public Department getEmployeeDepartment(Integer employeeId);
	
	/**
	 * 添加职工部门
	 * @param employeeId
	 * @param departmentId
	 */
	//public void addEmployeeDepartment(Integer employeeId, Integer departmentId);
	
	/**
	 * 更新职工部门
	 * @param employeeId
	 * @param departmentId
	 */
	//public void updateEmployeeDepartment(Integer employeeId, Integer departmentId);
	
	/**
	 * 根据部门Id获取部门信息
	 * @param departmentId
	 * @return
	 */
	public Department getWithParentIdById(Integer departmentId);
	
	/**
	 * 根据部门Id获取部门信息
	 * @param departmentId
	 * @return
	 */
	public Department getById(Integer departmentId);
	
	/**
	 * 根据父级Id获取部门列表
	 * @param parentId 父级Id
	 * @return
	 */
	public List<Department> getByParentId(Integer parentId);
	
	/**
	 * 删除部门
	 * @param departmentIds
	 */
	public void deleteBatch(List<Integer> departmentIds);
	
	/**
	 * 根性部门信息
	 * @param department
	 */
	public void updateDepartment(Department department);
	
	/**
	 * 保存部门
	 * @param department
	 * @return
	 */
	public int saveDepartment(Department department);
	
	/**
	 * 根据当前Id获取父级Id列表
	 * @param id
	 * @return
	 */
	List<Integer> listParentIdsById(Integer id);
	
}
