package com.mytijian.admin.dao.rbac.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.rbac.dataobject.EmployeeDO;


@Repository("employeeMapper")
public interface EmployeeMapper extends BaseMapper<EmployeeDO> {
	
	/**|
	 * 获取职工信息
	 * @param params (职工编号、主键、登录名称)
	 * @return
	 */
	public EmployeeDO selectEmployeeInfo(Map<String, Object> params);
	
	/**
	 * 查询用户的所有菜单ID
	 * @param employeeId
	 * @return
	 */
	// public List<Integer> getEmployeeAllMenuIds(Integer employeeId);
	
	public int queryTotalEmployees(Map<String, Object> paramMap);
	
	public List<EmployeeDO> queryEmployees(Map<String, Object> paramMap);
	/**
	 * 通过id列表查询员工
	 * @param ids
	 * @return
	 */
	public List<EmployeeDO> selectEmployeesByIds(List<Integer> ids);
	
	public void deleteBatch(List<Integer> employeeIds);
	
	
	public List<String> queryAllPerms(Integer employeeId);
	
	public List<EmployeeDO> selectEmployeeInfoByDep(Integer depId);
	
	public List<EmployeeDO> getAllEmployeeInfo();
	
	public void updatePinYin(@Param("id") Integer id,@Param("pinyin") String pinyin);
	
	public EmployeeDO getOperationById(Integer employeeId);
}
