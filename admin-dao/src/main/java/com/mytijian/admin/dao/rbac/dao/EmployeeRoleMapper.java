package com.mytijian.admin.dao.rbac.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.rbac.dataobject.EmployeeRoleDO;


@Repository("employeeRoleMapper")
public interface EmployeeRoleMapper extends BaseMapper<EmployeeRoleDO> {
	
	/**
	 * 批量添加用户角色
	 * @param map
	 */
	void insertBatch(Map<String, Object> map);
	
	/**
	 *  删除用户关联角色
	 * @param employeeId 用户Id
	 */
	void deleteByEmployeeId(Integer employeeId);
	
	/**
	 * 根据用户Id获取用户角色
	 * @param employeeId 用户Id
	 * @return
	 */
	List<EmployeeRoleDO> selectByEmployeeId(Integer employeeId);
	/*
	
	
	
	*//**
	 * 根据用户ID，获取角色Id列表
	 *//*
	List<Integer> queryRoleIdList(Integer employeeId);
	
	*//**
	 * 获取用户角色部门列表
	 * @param employeeId 职工Id
	 * @return
	 *//*
	List<EmployeeRoleDepartmentDO> selectEmployeeRoleDepartments(Integer employeeId);*/
	
	
	
}