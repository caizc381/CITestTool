package com.mytijian.admin.service.rbac.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.admin.api.rbac.model.Department;
import com.mytijian.admin.api.rbac.model.DepartmentEmployee;
import com.mytijian.admin.api.rbac.service.DepartmentEmployeeService;
import com.mytijian.admin.api.rbac.service.DepartmentService;
import com.mytijian.admin.dao.rbac.dao.DepartmentMapper;
import com.mytijian.admin.dao.rbac.dataobject.DepartmentDO;

@Service("departmentService")
public class DepartmentServiceImpl implements DepartmentService {

	private final static Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

	@Resource(name = "departmentMapper")
	private DepartmentMapper departmentMapper;

	
	@Resource(name = "departmentEmployeeService")
	private DepartmentEmployeeService departmentEmployeeService;

	@Override
	public List<Department> listDepartments(String departmentName, Integer offset, Integer limit) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("departmentName", departmentName);
		map.put("offset", offset);
		map.put("limit", limit);
		List<DepartmentDO> departments = departmentMapper.selectDepartments(map);
		return departmentDOToDepartment(departments);
	}

	@Override
	public int countTotal(String departmentName) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("departmentName", departmentName);
		return departmentMapper.countTotal(map);
	}
	
	@Override
	public List<Department> listDepartmentTree() {
		List<DepartmentDO> departmentDOs = departmentMapper.selectByParentId(0);
		List<Department> departments = departmentDOToDepartment(departmentDOs);
		return getDepartmentTreeList(departments);
	}

	@Override
	public Department getByEmployeeId(Integer employeeId) {
		Department department = null;
		if (employeeId == null || employeeId.intValue() <= 0) {
			logger.error("departmentService.getByEmployeeId error, employeeId : {}", employeeId);
			return null;
		}
		DepartmentEmployee departmentEmployee = departmentEmployeeService.getByEmployeeId(employeeId);
		
		if (departmentEmployee == null) {
			logger.error("departmentService.getByEmployeeId departmentEmployee is null, employeeId : {}", employeeId);
			return department;
		}
		
		return getWithParentIdById(departmentEmployee.getDepartmentId());
		
	}

	/*@Override
	public void addEmployeeDepartment(Integer employeeId, Integer departmentId) {
		DepartmentEmployeeDO departmentEmployeeDO = new DepartmentEmployeeDO();
		departmentEmployeeDO.setEmployeeId(employeeId);
		departmentEmployeeDO.setDepartmentId(departmentId);
		departmentEmployeeMapper.insert(departmentEmployeeDO);
	}*/

	/*@Override
	public void updateEmployeeDepartment(Integer employeeId, Integer departmentId) {
		DepartmentEmployeeDO departmentEmployeeDO = new DepartmentEmployeeDO();
		departmentEmployeeDO.setEmployeeId(employeeId);
		departmentEmployeeDO.setDepartmentId(departmentId);
		departmentEmployeeMapper.updateEmployeeDepartmentEmployeeByEmpoyeeId(departmentEmployeeDO);

	}
*/
	@Override
	public Department getWithParentIdById(Integer departmentId) {
		Department department = null;
		DepartmentDO departmentDO = departmentMapper.selectById(departmentId);
		/*if (departmentDO != null) {
			department = new Department();
			BeanUtils.copyProperties(departmentDO, department);
			department.setParentIdList(listParentIdsById(departmentId));
		}
		return department;*/
		department = departmentDOToDepartment(departmentDO);
		if (department != null) {
			department.setParentIdList(listParentIdsById(departmentId));
		}
		return department;
	}

	@Override
	public void deleteBatch(List<Integer> departmentIds) {
		departmentMapper.deleteBatch(departmentIds);
		departmentEmployeeService.deleteBatchByDepartmentIds(departmentIds);
	}

	@Override
	public void updateDepartment(Department department) {
		if (department == null) {
			return;
		}
		DepartmentDO departmentDO = new DepartmentDO();
		BeanUtils.copyProperties(department, departmentDO);
		departmentMapper.update(departmentDO);
		// TODO 判断区域省份是否合法、 本部门与上级部门关系
	}

	@Override
	public int saveDepartment(Department department) {
		if (department == null) {
			return 0;
		}
		DepartmentDO departmentDO = new DepartmentDO();
		BeanUtils.copyProperties(department, departmentDO);
		getSaveDepartment(departmentDO);
		return departmentMapper.insert(departmentDO);
	}

	private void getSaveDepartment(DepartmentDO departmentDO) {
		if (departmentDO != null) {
			/*
			 * if (departmentDO.getAreaId() == null) {
			 * departmentDO.setAreaId(0); } if (departmentDO.getParentId() ==
			 * null) { departmentDO.setParentId(0); }
			 */
			/*if (departmentDO.getProvinceId() == null) {
				departmentDO.setProvinceId(0);
			}*/
			if (departmentDO.getParentId() == null) {
				departmentDO.setParentId(0);
			}
		}
	}

	/*@Override
	public List<Department> listDepartment(String departmentName, Integer parentId, Integer offset, Integer limit) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("departmentName", departmentName);
		map.put("parentId", parentId);
		map.put("offset", offset);
		map.put("limit", limit);
		List<DepartmentDO> departments = departmentMapper.queryList(map);
		return DepartmentDOToDepartment(departments);
	}*/


	@Override
	public List<Department> getByParentId(Integer parentId) {
		List<DepartmentDO> departmentDOs = departmentMapper.selectByParentId(parentId);
		return departmentDOToDepartment(departmentDOs);
	}

	private List<Department> getDepartmentTreeList(List<Department> departments) {
		List<Department> subDepartmentList = Lists.newArrayList();
		for (Department department : departments) {
			department.setList(getDepartmentTreeList(getByParentId(department.getId())));
			// department.setParentIdList(listParentIdsById(department.getId()));
			subDepartmentList.add(department);
		}
		return subDepartmentList;
	}

	private List<Department> departmentDOToDepartment(List<DepartmentDO> departmentDOs) {
		List<Department> departmentList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(departmentDOs)) {
			departmentDOs.forEach(departmentDO -> {
				Department department = new Department();
				BeanUtils.copyProperties(departmentDO, department);
				departmentList.add(department);
			});
		}
		return departmentList;
	}

	private Department departmentDOToDepartment(DepartmentDO departmentDO) {
		Department department = null;
		if (departmentDO != null) {
			department = new Department();
			BeanUtils.copyProperties(departmentDO, department);
		}
		return department;
	}

	@Override
	public List<Integer> listParentIdsById(Integer departmentId) {
		List<Integer> parentIds = Lists.newArrayList();
		Integer departmentNew = departmentId;
		while (departmentNew != null) {
			DepartmentDO departmentDO = departmentMapper.selectById(departmentNew);
			if (departmentDO != null) {
				parentIds.add(departmentDO.getId());
				departmentNew = departmentDO.getParentId();
			} else {
				break;
			}
		}
		if (CollectionUtils.isNotEmpty(parentIds) && parentIds.size() > 0) {
			parentIds.remove(departmentId);
			List<Integer> list = Lists.newArrayList();
			for (int i = parentIds.size() - 1; i >= 0; i--) {
				list.add(parentIds.get(i));
			}
			parentIds = list;
		}
		return parentIds;
	}

	@Override
	public Department getById(Integer departmentId) {
		DepartmentDO departmentDO = departmentMapper.selectById(departmentId);
		return departmentDOToDepartment(departmentDO);
	}
}
