package com.mytijian.admin.dao.rbac.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.rbac.dataobject.DepartmentDO;


@Repository("departmentMapper")
public interface DepartmentMapper extends BaseMapper<DepartmentDO> {
	
	/**
	 * 获取所有部门
	 * @param parentId
	 * @return
	 */
	List<DepartmentDO> selectDepartments(Map<String, Object> map);
	
	/**
	 * 获取部门总数
	 * @param map
	 * @return
	 */
	int countTotal(Map<String, Object> map);
	
	public void deleteBatch(List<Integer> departmentIds);
	
	/**
	 * 根据父级Id获取部门信息
	 * @param parentId 父级Id
	 * @return
	 */
	List<DepartmentDO> selectByParentId(Integer parentId);
	
	/**
	 * 获取当前节点的父级节点列表
	 * @param id
	 * @return
	 */
	String selectParentIds(Integer id);
	
}
