package com.mytijian.admin.service.rbac.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.mytijian.admin.api.rbac.model.Department;
import com.mytijian.admin.api.rbac.model.DepartmentEmployee;
import com.mytijian.admin.api.rbac.service.DepartmentEmployeeService;
import com.mytijian.admin.api.rbac.service.DepartmentService;
import com.mytijian.admin.dao.rbac.dao.DepartmentEmployeeMapper;
import com.mytijian.admin.dao.rbac.dataobject.DepartmentEmployeeDO;

@Service("departmentEmployeeService")
public class DepartmentEmployeeServiceImpl implements DepartmentEmployeeService {

	private final static Logger logger = LoggerFactory.getLogger(DepartmentEmployeeServiceImpl.class);

	@Resource(name = "departmentService")
	private DepartmentService departmentService;
	
	@Resource(name = "departmentEmployeeMapper")
	private DepartmentEmployeeMapper departmentEmployeeMapper;
	

	@Override
	public void saveDepartmentEmployee(DepartmentEmployee departmentEmployee) {
		if (departmentEmployee == null) {
			return;
		}
		DepartmentEmployeeDO departmentEmployeeDO = new DepartmentEmployeeDO();
		BeanUtils.copyProperties(departmentEmployee, departmentEmployeeDO);
		departmentEmployeeMapper.insert(departmentEmployeeDO);
	}

	@Override
	public void saveEmployeeDepartment(Integer employeeId, Integer departmentId) {
		DepartmentEmployeeDO departmentEmployeeDO = new DepartmentEmployeeDO();
		departmentEmployeeDO.setEmployeeId(employeeId);
		departmentEmployeeDO.setDepartmentId(departmentId);
		departmentEmployeeMapper.insert(departmentEmployeeDO);
	}

	@Override
	public void updateEmployeeDepartment(Integer employeeId, Integer departmentId) {
		Department department = getById(departmentId);
		if (department == null) {
			logger.error("department is not exist(departmentEmployeeService.updateEmployeeDepartment), departmentId : {}", departmentId);
			return;
		}
		
	   DepartmentEmployeeDO departmentEmployeeDO = new DepartmentEmployeeDO();
	   departmentEmployeeDO.setDepartmentId(departmentId);
	   departmentEmployeeDO.setEmployeeId(employeeId);
	   departmentEmployeeMapper.updateDepartmentIdByEmpoyeeId(departmentEmployeeDO);
		 
		//departmentEmployeeMapper.updateDepartmentIdByEmpoyeeId(employeeId, departmentId);
	}

	@Override
	public DepartmentEmployee getByEmployeeId(Integer employeeId) {
		DepartmentEmployeeDO departmentEmployeeDO = departmentEmployeeMapper
				.getDepartmentEmployeeByEmpoyeeId(employeeId);
		/*
		 * if (departmentEmployeeDO != null) { departmentEmployee = new
		 * DepartmentEmployee(); BeanUtils.copyProperties(departmentEmployeeDO,
		 * departmentEmployee); } return departmentEmployee;
		 */
		return departmentEmployeeDOToDepartmentEmployee(departmentEmployeeDO);
	}

	/*
	 * @Override public List<DepartmentEmployee> listEmployeeDepartments(Integer
	 * employeeId) { List<DepartmentEmployee> departmentEmployees =
	 * Lists.newArrayList(); //List<DepartmentEmployeeDO> departmentEmployeeDOs
	 * = departmentEmployeeMapper. DepartmentEmployeeByEmpoyeeId(employeeId); if
	 * () {
	 * 
	 * } return null; }
	 */

	@Override
	public void deleteBatchByDepartmentIds(List<Integer> departmentIds) {
		departmentEmployeeMapper.deleteBatchBydepartmentIds(departmentIds);
	}

	private DepartmentEmployee departmentEmployeeDOToDepartmentEmployee(DepartmentEmployeeDO departmentEmployeeDO) {
		DepartmentEmployee departmentEmployee = null;
		if (departmentEmployeeDO != null) {
			departmentEmployee = new DepartmentEmployee();
			BeanUtils.copyProperties(departmentEmployeeDO, departmentEmployee);
		}
		return departmentEmployee;
	}
	
	private Department getById(Integer departmentId) {
		return departmentService.getWithParentIdById(departmentId);
	}
	
}
