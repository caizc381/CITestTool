package com.mytijian.admin.dao.rbac.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.rbac.dataobject.EmployeeRoleDepartmentDO;


@Repository("employeeRoleDepartmentMapper")
public interface EmployeeRoleDepartmentMapper extends BaseMapper<EmployeeRoleDepartmentDO> {
	
	void deleteByEmployeeId(Integer employeeId);
	
	void insertBatch(Map<String, Object> map);
	
	/**
	 * 根据用户ID，获取角色Id列表
	 */
	List<Integer> queryRoleIdList(Integer employeeId);
	
	/**
	 * 获取用户角色部门列表
	 * @param employeeId 职工Id
	 * @return
	 */
	List<EmployeeRoleDepartmentDO> selectEmployeeRoleDepartments(Integer employeeId);
	
}