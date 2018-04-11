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
import com.mytijian.admin.api.rbac.model.EmployeeRole;
import com.mytijian.admin.api.rbac.service.EmployeeRoleService;
import com.mytijian.admin.dao.rbac.dao.EmployeeRoleDepartmentMapper;
import com.mytijian.admin.dao.rbac.dao.EmployeeRoleMapper;
import com.mytijian.admin.dao.rbac.dataobject.EmployeeRoleDO;

@Service("employeeRoleService")
public class EmployeeRoleServiceImpl implements EmployeeRoleService {
	
	private final static Logger logger = LoggerFactory.getLogger(EmployeeRoleService.class);
	
	@Resource(name = "employeeRoleDepartmentMapper")
	private EmployeeRoleDepartmentMapper employeeRoleDepartmentMapper;
	
	@Resource(name = "employeeRoleMapper")
	private EmployeeRoleMapper employeeRoleMapper;
	
	@Override
	public List<EmployeeRole> listEmployeeRoles(Integer employeeId) {
		List<EmployeeRoleDO> employeeRoleDOs = employeeRoleMapper.selectByEmployeeId(employeeId);
		return employeeRoleDOToEmployeeRole(employeeRoleDOs);
	}

	@Override
	public void saveEmployeeRole(Integer employeeId, List<Integer> roleIds) {
		if (employeeId == null || CollectionUtils.isEmpty(roleIds)) {
			logger.error("EmployeeRoleService.saveEmployeeRole error, employeeId : {}, roleIds : {}", employeeId, roleIds);
			return;
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("employeeId", employeeId);
		map.put("roleIds", roleIds);
		employeeRoleMapper.insertBatch(map);
	}


	@Override
	public void deleteEmployeeRole(Integer employeeId) {
		employeeRoleMapper.deleteByEmployeeId(employeeId);
	}
	
	private List<EmployeeRole> employeeRoleDOToEmployeeRole (List<EmployeeRoleDO> employeeRoleDOs) {
		List<EmployeeRole> employeeRoles = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(employeeRoleDOs)) {
			employeeRoleDOs.forEach(employeeRoleDO -> {
				EmployeeRole employeeRole = new EmployeeRole();
				BeanUtils.copyProperties(employeeRoleDO, employeeRole);
				employeeRoles.add(employeeRole);
			});
		}
		return employeeRoles;
	}

}
