package com.mytijian.admin.dao.rbac.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.rbac.dataobject.DepartmentEmployeeDO;


@Repository("departmentEmployeeMapper")
public interface DepartmentEmployeeMapper extends BaseMapper<DepartmentEmployeeDO> {

	public DepartmentEmployeeDO getDepartmentEmployeeByEmpoyeeId(Integer employeeId);

	public void updateDepartmentIdByEmpoyeeId(DepartmentEmployeeDO departmentEmployeeDO);

	public void deleteBatchBydepartmentIds(List<Integer> departmentIds);
	
	// public void updateDepartmentIdByEmpoyeeId(@Param(value = "employeeId") Integer employeeId,
			// @Param(value = "departmentId") Integer departmentId);
}
