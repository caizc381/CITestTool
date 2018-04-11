package com.mytijian.admin.dao.rbac.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.rbac.dataobject.RoleDepartmentMenuDO;


@Repository("roleDepartmentMenuMapper")
public interface RoleDepartmentMenuMapper extends BaseMapper<RoleDepartmentMenuDO> {
	
	public void insertBatch(Map<String, Object> map);
	
	public List<RoleDepartmentMenuDO> selectByIds(List<Integer> ids);
	
	public List<RoleDepartmentMenuDO> selectByRoleDepartmentIds(List<Integer> roleDepartmentIds);
	
	public List<Integer> queryMenuIdList(Integer roleId);
	
	public void deleteByRoleId(Integer roleId);
	
	//List<RoleDepartmentMenuDO> selectByRoleDepartmentIds(Map<String, Object> map);
	
	List<RoleDepartmentMenuDO> selectByRoleDepartmentId(Integer roleDepartmentId);
	
	void deleteByRoleDepartmentId(Integer roleDepartmentId);
	
	void deleteByRoleDepartmentIds(Map<String, Object> map);
	
}
