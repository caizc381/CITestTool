package com.mytijian.admin.dao.rbac.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.rbac.dataobject.RoleDO;


@Repository("roleMapper")
public interface RoleMapper extends BaseMapper<RoleDO> {
	
	List<RoleDO> listRoles(Map<String, Object> map);
	
	List<RoleDO> selectRoleByIds(Map<String, Object> map);
	
	int countTotal(Map<String, Object> map);
	
	void save(RoleDO role);
	
	
	void deleteBatch(List<Integer> roleIds);
	
	void deleteByEmployeeId(Integer employeeId);
	
	// List<RoleDO> getAllRoles();
}