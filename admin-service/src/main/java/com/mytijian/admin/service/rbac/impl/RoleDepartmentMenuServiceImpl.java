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
import com.mytijian.admin.api.rbac.model.RoleDepartmentMenu;
import com.mytijian.admin.api.rbac.service.RoleDepartmentMenuService;
import com.mytijian.admin.dao.rbac.dao.RoleDepartmentMenuMapper;
import com.mytijian.admin.dao.rbac.dataobject.RoleDepartmentMenuDO;

/**
 * 角色部门与菜单对应关系
 * 
 * @author feng
 * @email 373680866@qq.com
 * @date 2017年02月27日 上午10:42:30
 */
@Service("roleDepartmentMenuService")
public class RoleDepartmentMenuServiceImpl implements RoleDepartmentMenuService {
	
	@Resource(name = "roleDepartmentMenuMapper")
	private RoleDepartmentMenuMapper roleDepartmentMenuMapper;
	
	@Override
	public List<Integer> queryMenuIdList(Integer roleDeaprtmentMenuId) {
		return roleDepartmentMenuMapper.queryMenuIdList(roleDeaprtmentMenuId);
	}

	@Override
	public void saveRoleDepartmentMenu(Integer roleDeaprtmentMenuId, List<Integer> menuIdList) {
		// 先删除角色与菜单关系
		deleteRoleDepartmentMenu(roleDeaprtmentMenuId);
		
		if (CollectionUtils.isNotEmpty(menuIdList)){
			// 保存角色与菜单关系
			Map<String, Object> map = Maps.newHashMap();
			map.put("roleDeaprtmentMenuId", roleDeaprtmentMenuId);
			map.put("menuIdList", menuIdList);
			roleDepartmentMenuMapper.insertBatch(map);
		}
	}

	@Override
	public void deleteRoleDepartmentMenu(Integer roleDeaprtmentMenuId) {
		roleDepartmentMenuMapper.deleteByRoleDepartmentId(roleDeaprtmentMenuId);
	}
	
	private List<RoleDepartmentMenu> RoleDepartmentMenuDOToRoleDepartmentMenu(List<RoleDepartmentMenuDO> roleDepartmentMenuDOs) {
		List<RoleDepartmentMenu> roleDepartmentMenus = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(roleDepartmentMenuDOs)){
			roleDepartmentMenuDOs.forEach(roleDepartmentMenuDO -> {
				RoleDepartmentMenu roleDepartmentMenu = new RoleDepartmentMenu();
				BeanUtils.copyProperties(roleDepartmentMenuDO, roleDepartmentMenu);
				roleDepartmentMenus.add(roleDepartmentMenu);
			});
		}
		return roleDepartmentMenus;
	}

	@Override
	public List<RoleDepartmentMenu> listByIds(List<Integer> ids) {
		List<RoleDepartmentMenuDO> roleDepartmentMenuDOs = roleDepartmentMenuMapper.selectByIds(ids);
		return RoleDepartmentMenuDOToRoleDepartmentMenu(roleDepartmentMenuDOs);
	}

	@Override
	public void deleteByRoleDepartmentIds(List<Integer> roleDepartmentIds) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("roleDepartmentIds", roleDepartmentIds);
		roleDepartmentMenuMapper.deleteByRoleDepartmentIds(map);
	}

	@Override
	public List<RoleDepartmentMenu> listByRoleDepartId(Integer roleDepartmentId) {
		if (roleDepartmentId == null) {
			return new ArrayList<>();
		}
		
		List<RoleDepartmentMenuDO> roleDepartmentMenuDOs = roleDepartmentMenuMapper.selectByRoleDepartmentId(roleDepartmentId);
		
		return RoleDepartmentMenuDOToRoleDepartmentMenu(roleDepartmentMenuDOs);
	}

	@Override
	public List<RoleDepartmentMenu> listByRoleDepartmentIds(List<Integer> roleDepartmentIds) {
		List<RoleDepartmentMenuDO> roleDepartmentMenuDOs = roleDepartmentMenuMapper.selectByRoleDepartmentIds(roleDepartmentIds);
		return RoleDepartmentMenuDOToRoleDepartmentMenu(roleDepartmentMenuDOs);
	}
	
}
