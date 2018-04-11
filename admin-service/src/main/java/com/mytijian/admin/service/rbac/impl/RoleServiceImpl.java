package com.mytijian.admin.service.rbac.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.admin.api.rbac.model.Role;
import com.mytijian.admin.api.rbac.service.RoleService;
import com.mytijian.admin.dao.rbac.dao.RoleMapper;
import com.mytijian.admin.dao.rbac.dataobject.RoleDO;


/**
 * 角色
 * 
 * @author 
 * @email 
 * @date 
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {
	
	@Resource(name = "roleMapper")
	private RoleMapper roleMapper;
	
	@Override
	public Role getRoleInfoById(Integer roleId) {
		RoleDO roleDO = roleMapper.selectById(roleId);
		return roleDOToRole(roleDO);
	}

	@Override
	public List<Role> listRoles(String roleName, Integer offset, Integer limit) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("roleName", roleName);
		map.put("offset", offset);
		map.put("limit", limit);
		List<RoleDO> roleDOs = roleMapper.listRoles(map);
		return roleDOToRole(roleDOs);
	}

	@Override
	public int countTotal(String roleName, Integer offset, Integer limit) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("roleName", roleName);
		return roleMapper.countTotal(map);
	}

	@Override
	public void saveRole(Role role) {
		RoleDO roleDO = new RoleDO();
		BeanUtils.copyProperties(role, roleDO);
		roleMapper.insert(roleDO);
		//保存角色与菜单关系
		//role.setMenuIdList(getCurrentMenuIdAndParentIds(role.getMenuIdList()));
		//roleMenuService.saveOrUpdate(roleDO.getId(), role.getMenuIdList());
	}

	@Override
	public void updateRole(Role role) {
		RoleDO roleDO = new RoleDO();
		BeanUtils.copyProperties(role, roleDO);
		roleDO.setId(role.getId());
		roleMapper.update(roleDO);
	}

	@Override
	public void deleteBatch(List<Integer> roleIds) {
		roleMapper.deleteBatch(roleIds);
	}
	
	private List<Role> roleDOToRole(List<RoleDO> roleDOs) {
		List<Role> roles = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(roleDOs)) {
			roleDOs.forEach(roleDO -> {
				Role role = new Role();
				BeanUtils.copyProperties(roleDO, role);
				roles.add(role);
			});
		}
		return roles;
	}
	
	private Role roleDOToRole(RoleDO roleDO){
		Role role = null;
		if (roleDO != null) {
			role = new Role();
			BeanUtils.copyProperties(roleDO, role);
		}
		return role;
	}

	@Override
	public List<Role> listByIds(List<Integer> roleIds) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("roleIds", roleIds);
		if (CollectionUtils.isNotEmpty(roleIds)) {
			 List<RoleDO> roleDOs = roleMapper.selectRoleByIds(map);
			 return roleDOToRole(roleDOs);
		}
		return new ArrayList<>();
	}
	
}
