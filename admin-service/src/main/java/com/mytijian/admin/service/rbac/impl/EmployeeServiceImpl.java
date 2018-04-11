package com.mytijian.admin.service.rbac.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.mytijian.resource.service.HospitalService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.admin.api.rbac.constant.EmployeeStatusEnum;
import com.mytijian.admin.api.rbac.model.Department;
import com.mytijian.admin.api.rbac.model.DepartmentEmployee;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.api.rbac.model.EmployeeRole;
import com.mytijian.admin.api.rbac.model.Role;
import com.mytijian.admin.api.rbac.service.DepartmentEmployeeService;
import com.mytijian.admin.api.rbac.service.DepartmentService;
import com.mytijian.admin.api.rbac.service.EmployeeRoleService;
import com.mytijian.admin.api.rbac.service.EmployeeService;
import com.mytijian.admin.api.rbac.service.RoleDepartmentMenuService;
import com.mytijian.admin.api.rbac.service.RoleService;
import com.mytijian.admin.dao.rbac.dao.EmployeeMapper;
import com.mytijian.admin.dao.rbac.dataobject.EmployeeDO;
import com.mytijian.util.AssertUtil;
import com.mytijian.util.PinYinUtil;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {

	private final static Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	@Resource(name = "employeeMapper")
	private EmployeeMapper employeeMapper;

	@Resource(name = "departmentEmployeeService")
	private DepartmentEmployeeService departmentEmployeeService;

	@Resource(name = "departmentService")
	private DepartmentService departmentService;

	@Resource(name = "roleService")
	private RoleService roleService;

	@Resource(name = "roleDepartmentMenuService")
	private RoleDepartmentMenuService roleDepartmentMenuService;

	@Resource(name = "employeeRoleService")
	private EmployeeRoleService employeeRoleService;
	
	@Resource(name = "hospitalService")
	private HospitalService hospitalService;

	@Override
	public Employee getEmployeeInfo(Integer empId, String employNo, String loginName) {
		
		Employee employee = null;
		
		if (isAllNull(empId, employNo, loginName)) {
			logger.error("id and employNo is null, (EmployeeServiceImpl.getEmployeeInfoByIdOrNo)");
			return null;
		}

		Map<String, Object> params = Maps.newHashMap();
		params.put("empId", empId);
		params.put("empNo", employNo);
		params.put("loginName", loginName);
		EmployeeDO employeeDo = employeeMapper.selectEmployeeInfo(params);

		if (employeeDo != null) {
			employee = new Employee();
			BeanUtils.copyProperties(employeeDo, employee);
			// 获取用户部门信息
			getEmployWithDepartment(employee);
			// 获取角色信息
			getEmployWithRole(employee);
		}
		
		return employee;
	}

	@Override
	public List<Employee> getOperationInfoByDep(Integer depId) {
		
		List<Employee> employees = Lists.newArrayList();
		
		if (isAllNull(depId)) {
			logger.error("depId  is null");
			return null;
		}

		List<EmployeeDO> employeeDos = employeeMapper.selectEmployeeInfoByDep(depId);

		if (CollectionUtils.isNotEmpty(employeeDos)) {
			employeeDos.forEach(employeeDo -> {
				Employee employee = new Employee();
				BeanUtils.copyProperties(employeeDo, employee);
				// 获取用户部门信息
				getEmployWithDepartment(employee);
				// 获取角色信息
				getEmployWithRole(employee);
				employees.add(employee);
			});
		}
		
		return employees;
	}
	
	@Override
	public void initEmployeePinYin(){
		List<EmployeeDO> employeeDOs = employeeMapper.getAllEmployeeInfo();
		if(CollectionUtils.isNotEmpty(employeeDOs)){
			employeeDOs.forEach(employeeDO->{
				employeeMapper.updatePinYin(employeeDO.getId(), PinYinUtil.getFirstSpell(employeeDO.getEmployeeName()));
			});
		}
	}
	
	public Employee getOperationByHospitalId(Integer hospitalId){
		Employee employee = new Employee();
		Integer employeeId=hospitalService.getHospitalById(hospitalId).getOpsManagerId();
		if(employeeId!=null){
			EmployeeDO employeeDO = employeeMapper.getOperationById(employeeId);
			BeanUtils.copyProperties(employeeDO, employee);
			// 获取用户部门信息
			getEmployWithDepartment(employee);
		}
		return employee;
		
	}
	
	
	@Transactional
	@Override
	public void addEmployee(Employee employee) {
		if (isIncludeNull(employee)) {
			logger.error("EmployeeServiceImpl.addEmployee, employee is null");
			return;
		}

		EmployeeDO employeeDO = new EmployeeDO();
		BeanUtils.copyProperties(employee, employeeDO);
		employeeDO.setStatus(Optional.ofNullable(employee.getStatus()).orElse(EmployeeStatusEnum.NORMAL.getCode()));
		employeeDO.setPinYin(PinYinUtil.getFirstSpell(employee.getEmployeeName()));
		employeeMapper.insert(employeeDO);

		// 添加用户关联角色
		if (CollectionUtils.isNotEmpty(employee.getRoleIds())) {
			employeeRoleService.saveEmployeeRole(employeeDO.getId(), employee.getRoleIds());
		}
		
		// 添加用户关联部门
		if (employee.getDepartmentId() != null) {
			//DepartmentEmployee departmentEmployee = new DepartmentEmployee();
			//departmentEmployee.setDepartmentId(employee.getDepartmentId());
			//departmentEmployee.setEmployeeId(employeeDO.getId());
			//departmentEmployeeService.saveDepartmentEmployee(departmentEmployee);
			departmentEmployeeService.saveEmployeeDepartment(employeeDO.getId(), employee.getDepartmentId());
		}
	}

	@Override
	public void updateEmployee(Employee employee) {
		if (isIncludeNull(employee)) {
			logger.error("EmployeeServiceImpl.updateEmployee, employee is null");
			return;
		}

		EmployeeDO employeeDO = new EmployeeDO();
		BeanUtils.copyProperties(employee, employeeDO, "salt");
		employeeDO.setId(employee.getId());
		employeeDO.setPinYin(PinYinUtil.getFirstSpell(employee.getEmployeeName()));
		employeeMapper.update(employeeDO);
		
		// 添加用户关联角色
		if (CollectionUtils.isNotEmpty(employee.getRoleIds())) {
			// roleDepartmentService.updateRoleDepartment(employeeDO.getId(),
			employeeRoleService.deleteEmployeeRole(employee.getId());
			employeeRoleService.saveEmployeeRole(employee.getId(), employee.getRoleIds());
		}
		
		// 更新部门
		if (employee.getDepartmentId() != null) {
			DepartmentEmployee departmentEmployee = departmentEmployeeService.getByEmployeeId(employee.getId());
			if (departmentEmployee != null) {
				departmentEmployeeService.updateEmployeeDepartment(employee.getId(), employee.getDepartmentId());
			} else {
				//departmentEmployee = new DepartmentEmployee();
				//departmentEmployee.setDepartmentId(employee.getDepartmentId());
				//departmentEmployee.setEmployeeId(employeeDO.getId());
				//departmentEmployeeService.saveDepartmentEmployee(departmentEmployee);
				departmentEmployeeService.saveEmployeeDepartment(employeeDO.getId(), employee.getDepartmentId());
			}
		}
	}

	private boolean isIncludeNull(Object... object) {
		if (StringUtils.isEmpty(object))
			return true;
		for (Object obj : object) {
			if (StringUtils.isEmpty(obj))
				return true;
		}
		return false;
	}

	private boolean isAllNull(Object... object) {
		if (StringUtils.isEmpty(object))
			return true;
		for (Object obj : object) {
			if (!StringUtils.isEmpty(obj))
				return false;
		}
		return true;
	}

	@Override
	public int countTotalEmployees(String employeeName, Integer offset, Integer limit) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("loginName", employeeName);
		return employeeMapper.queryTotalEmployees(paramMap);
	}

	@Override
	public List<Employee> listEmployees(String employeeName, Integer offset, Integer limit) {
		List<Employee> employees = Lists.newArrayList();
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("employeeName", employeeName);
		paramMap.put("offset", offset);
		paramMap.put("limit", limit);
		List<EmployeeDO> employeeDOs = employeeMapper.queryEmployees(paramMap);
		if (!CollectionUtils.isEmpty(employeeDOs)) {
			employeeDOs.forEach(employeeDO -> {
				Employee employee = new Employee();
				BeanUtils.copyProperties(employeeDO, employee);
				getEmployWithDepartment(employee);
				getEmployWithRole(employee);
				employees.add(employee);
			});
		}
		return employees;
	}

	@Override
	public void deleteBatch(List<Integer> employeeIds) {
		employeeMapper.deleteBatch(employeeIds);
		;
	}

	private void getEmployWithDepartment(Employee employee) {
		if (employee == null) {
			return;
		}
		Department department = departmentService.getByEmployeeId(employee.getId());
		if (department != null) {
			employee.setDepartName(department.getDepartmentName());
			employee.setDepartmentId(department.getId());
			List<Integer> departmentIds = departmentService.listParentIdsById(department.getId());
			departmentIds.add(department.getId());
			employee.setParentDepartmentIdList(departmentIds);
		}
	}

	private void getEmployWithRole(Employee employee) {
		if (employee == null) {
			return;
		}
		List<EmployeeRole> employeeRoles = employeeRoleService.listEmployeeRoles(employee.getId());
		if (CollectionUtils.isNotEmpty(employeeRoles)) {
			List<Integer> roleIds = Lists.newArrayList();
			employeeRoles.forEach(employeeRole -> {
				roleIds.add(employeeRole.getRoleId());
			});
			List<Role> roles = roleService.listByIds(roleIds);
			employee.setRoles(roles);
			employee.setRoleIds(roleIds);
		} else {
			employee.setRoles(new ArrayList<>());
		}

	}

	@Override
	public List<String> listPermsByemployeeId(Integer employeeId) {
		List<String> permissions = employeeMapper.queryAllPerms(employeeId);
		if (CollectionUtils.isNotEmpty(permissions)) {
			permissions = permissions.stream().filter(permission -> !StringUtils.isEmpty(permission)).collect(Collectors.toList());
		}
		return permissions;
	}

	@Override
	public void updateEmployeePwd(Integer employeeId, String password) {
		
		if (employeeId == null || StringUtils.isEmpty(password)) {
			logger.error("EmployeeServiceImpl.updateEmployeePwd, employeeId : {}, password : {}", employeeId, password);
			return;
		}
		
		EmployeeDO employeeDO = new EmployeeDO();
		employeeDO.setId(employeeId);
		employeeDO.setPassword(password);
		employeeMapper.update(employeeDO);
		
	}

	@Override
	public List<Employee> listEmployeesByIds(List<Integer> ids) {
		List<Employee> employees = Lists.newArrayList();
		if(AssertUtil.isEmpty(ids)){
			return employees;
		}
		List<EmployeeDO> employeeDOs = employeeMapper.selectEmployeesByIds(ids);
		employeeDOs.forEach(employeeDO->{
			Employee employee = new Employee();
			BeanUtils.copyProperties(employeeDO, employee);
			employees.add(employee);
		});
		return employees;
	}
}
