package com.mytijian.admin.api.rbac.service;

import java.util.List;

import com.mytijian.admin.api.rbac.model.Employee;


/**
 * 职工
 * @author mytijian
 *
 */
public interface EmployeeService {
	
	/**
	 * 获取职工信息
	 * @param id
	 * @param employNo
	 * @param loginName
	 * @return
	 */
	public Employee getEmployeeInfo(Integer id, String employNo, String loginName);
	
	/**
	 * 添加职工
	 * @param employee
	 */
	public void addEmployee(Employee employee);
	
	/**
	 * 更新职工信息
	 * @param employee
	 */
	public void updateEmployee(Employee employee);
	
	/**
	 * 更新用户密码
	 * @param employeeId
	 * @param password
	 */
	public void updateEmployeePwd(Integer employeeId, String password);
	
	public int countTotalEmployees(String employeeName, Integer offset, Integer limit);
	
	public List<Employee> listEmployees(String employeeName, Integer offset, Integer limit);
	
	public void deleteBatch(List<Integer> employeeIds);
	
	/**
	 * 通过id列表查询员工
	 * @param ids
	 * @return
	 */
	public List<Employee> listEmployeesByIds(List<Integer> ids);
	
	public List<String> listPermsByemployeeId(Integer employeeId);
	
	/**
	 * 获取运营经理
	 * @param depId
	 * @return
	 */
	public List<Employee> getOperationInfoByDep(Integer depId);
	
	/**
	 * 初始化员工拼音
	 */
	public void initEmployeePinYin();
	
	/**
	 * 根据医院获取运营经理
	 * @param OperationId
	 * @return
	 */
	public Employee getOperationByHospitalId(Integer HospitalId);
	
	
}
