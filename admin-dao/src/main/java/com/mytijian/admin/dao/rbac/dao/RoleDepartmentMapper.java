package com.mytijian.admin.dao.rbac.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.rbac.dataobject.RoleDepartmentDO;
import com.mytijian.admin.dao.rbac.param.RoleDepartmentDAOQuery;


@Repository("roleDepartmentMapper")
public interface RoleDepartmentMapper extends BaseMapper<RoleDepartmentDO> {
	
	/**
	 * 根据部门和角色获取部门角色关联信息
	 * @param params 
	 * 			departmentId 部门Id
	 * 			roleId 角色Id
	 * @return
	 */
	List<RoleDepartmentDO> selectByDepartmentIdAndRoleId(Map<String, Object> params);
	
	/**
	 * 删除部门角色关联信息
	 * @param params 
	 * 			departmentId 部门Id
	 * 			roleId 角色Id
	 * @return
	 */
	void deleteByDepartmentIdAndRoleId(Map<String, Object> params);
	
	void deleteByEmployeeId(Integer employeeId);
	
	void deleteBatch(Map<String, Object> map);
	
	List<RoleDepartmentDO> selectByIds(List<Integer> roleDepartmentIds);
	
	List<RoleDepartmentDO> selectByEmployeeId(Integer employeeId);
	
	List<RoleDepartmentDO> selectRoleDepartments(Map<String, Object> map);
	
	int countRoleDepartments(Map<String, Object> map);
	
	// TODO 修改
	List<RoleDepartmentDO> selectByNames(RoleDepartmentDAOQuery roleDepartmentDAOQuery);
	
	// TODO 修改
	int countByNames(RoleDepartmentDAOQuery roleDepartmentDAOQuery);

	RoleDepartmentDO selectBydepartmentIdAndRoleId(Map<String, Object> map);
} 
