package com.mytijian.admin.service.rbac.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.admin.api.rbac.constant.MenuTypeEnum;
import com.mytijian.admin.api.rbac.model.Department;
import com.mytijian.admin.api.rbac.model.EmployeeRoleDepartment;
import com.mytijian.admin.api.rbac.model.Menu;
import com.mytijian.admin.api.rbac.model.Role;
import com.mytijian.admin.api.rbac.model.RoleDepartment;
import com.mytijian.admin.api.rbac.model.RoleDepartmentMenu;
import com.mytijian.admin.api.rbac.param.RoleDepartmentQuery;
import com.mytijian.admin.api.rbac.service.DepartmentService;
import com.mytijian.admin.api.rbac.service.MenuService;
import com.mytijian.admin.api.rbac.service.RoleDepartmentMenuService;
import com.mytijian.admin.api.rbac.service.RoleDepartmentService;
import com.mytijian.admin.api.rbac.service.RoleService;
import com.mytijian.admin.dao.rbac.dao.EmployeeRoleDepartmentMapper;
import com.mytijian.admin.dao.rbac.dao.RoleDepartmentMapper;
import com.mytijian.admin.dao.rbac.dataobject.EmployeeRoleDepartmentDO;
import com.mytijian.admin.dao.rbac.dataobject.RoleDepartmentDO;
import com.mytijian.admin.dao.rbac.param.RoleDepartmentDAOQuery;
import com.mytijian.util.AssertUtil;

@Service("roleDepartmentService")
public class RoleDepartmentServiceImpl implements RoleDepartmentService {
	
	private final static Logger logger = LoggerFactory.getLogger(RoleDepartmentServiceImpl.class);
	
	@Resource(name = "roleDepartmentMapper")
	private RoleDepartmentMapper roleDepartmentMapper;
	
	@Resource(name = "employeeRoleDepartmentMapper")
	private EmployeeRoleDepartmentMapper employeeRoleDepartmentMapper;

	@Resource(name = "menuService")
	private MenuService menuService;
	
	@Resource(name = "roleService")
	private RoleService roleService;
	
	@Resource(name = "roleDepartmentMenuService")
	private RoleDepartmentMenuService roleDepartmentMenuService;
	
	@Resource(name = "departmentService")
	private DepartmentService departmentService;
	
	@Override
	public void addRoleDepartment(RoleDepartment roleDepartment) {
		
		RoleDepartmentDO roleDepartmentDO = roleDepartmentToRoleDepartmentDO(roleDepartment);
		if (roleDepartmentDO == null) {
			logger.error("RoleDepartmentServiceImpl.addRoleDepartment error, roleDepartmentDO is null");
			return;
		}
		roleDepartmentMapper.insert(roleDepartmentDO);
		
		if (CollectionUtils.isNotEmpty(roleDepartment.getMenuIdList())) {
			// 新增授权关联
			roleDepartmentMenuService.saveRoleDepartmentMenu(roleDepartmentDO.getId(), getCurrentMenuIdAndParentIds(roleDepartment.getMenuIdList()));
		}		
	}
	
	@Override
	public void deleteByEmployeeId(Integer employeeId) {
		if (employeeId == null) {
			return;
		}
		employeeRoleDepartmentMapper.deleteByEmployeeId(employeeId);
	}
	
	/**
	 * 获取本级和父级菜单Id
	 * @param menuIds
	 * @return
	 */
	private List<Integer> getCurrentMenuIdAndParentIds(List<Integer> menuIds) {
		List<Integer> menuIdsAndParent = Lists.newArrayList();
		List<Integer> queryIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(menuIds)) {
			menuIdsAndParent.addAll(menuIds);
			queryIds.addAll(menuIds);
			while (queryIds.size() > 0) {
				List<Menu> menus = menuService.listMenuByIds(queryIds);
				queryIds.clear();
				if (CollectionUtils.isNotEmpty(menus)) {
					menus.forEach(menu -> {
						if (menu.getParentId() != null && menu.getParentId().intValue() > 0) {
							queryIds.add(menu.getParentId());
							if (!menuIdsAndParent.contains(menu.getParentId())) {
								menuIdsAndParent.add(menu.getParentId());
							}
						}
					});
				}
			}

		}

		return menuIdsAndParent;
	}

	@Transactional
	@Override
	public void updateRoleDepartment(Integer roleDeaprtmentMenuId, List<Integer> menuIdList){
		/*// 删除该用户下的权限
		removeRoleDepartmentByEmployeeId(employeeId);
		//保存用户与角色关系
		Map<String, Object> map = Maps.newHashMap();
		map.put("employeeId", employeeId);
		map.put("roleDepartmentIds", roleDepartmentIds);
		employeeRoleDepartmentMapper.insertBatch(map);*/
		
		// 删除 用户部门关联的菜单信息
		roleDepartmentMenuService.deleteRoleDepartmentMenu(roleDeaprtmentMenuId);
		// 新增关联
		roleDepartmentMenuService.saveRoleDepartmentMenu(roleDeaprtmentMenuId,  getCurrentMenuIdAndParentIds(menuIdList));
	}

	@Override
	public List<EmployeeRoleDepartment> listEmployeeRoleDepartments(Integer employeeId) {
		if (employeeId == null) {
			return Lists.newArrayList();
		}
		List<EmployeeRoleDepartmentDO> roleDepartmentDOs = employeeRoleDepartmentMapper.selectEmployeeRoleDepartments(employeeId);
		return EmployeeRoleDepartmentDOToEmployeeRoleDepartment(roleDepartmentDOs);
	}
	
	private List<EmployeeRoleDepartment> EmployeeRoleDepartmentDOToEmployeeRoleDepartment(List<EmployeeRoleDepartmentDO> roleDepartmentDOs) {
		List<EmployeeRoleDepartment> list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(roleDepartmentDOs)) {
			roleDepartmentDOs.forEach(roleDepartmentDO -> {
				EmployeeRoleDepartment employeeRoleDepartment = new EmployeeRoleDepartment();
				BeanUtils.copyProperties(roleDepartmentDO, employeeRoleDepartment);
				list.add(employeeRoleDepartment);
			});
		}
		return list;
	}

	@Override
	public void updateRoleDepartment(RoleDepartment roleDepartment) {
		// 删除该用户下的权限
		deleteByEmployeeId(roleDepartment.getEmployeeId());
		RoleDepartmentDO roleDepartmentDO = roleDepartmentToRoleDepartmentDO(roleDepartment);
		if (roleDepartmentDO == null) {
			logger.error("RoleDepartmentServiceImpl.addRoleDepartment error, roleDepartmentDO is null");
			return;
		}
		roleDepartmentMapper.insert(roleDepartmentDO);
	}

	@Override
	public List<RoleDepartment> listBydepartmentIdAndRoleIds(Integer departmentId, List<Integer> roleIds) {
		if (departmentId == null || CollectionUtils.isEmpty(roleIds)) {
			return new ArrayList<>();
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("departmentId", departmentId);
		map.put("roleIds", roleIds);
		List<RoleDepartmentDO> roleDepartmentDOs = roleDepartmentMapper.selectByDepartmentIdAndRoleId(map);
		return roleDepartmentToRoleDepartmentDO(roleDepartmentDOs);
	}
	
	private List<RoleDepartment> roleDepartmentToRoleDepartmentDO (List<RoleDepartmentDO> roleDepartmentDOs) {
		List<RoleDepartment> rList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(roleDepartmentDOs)) {
			roleDepartmentDOs.forEach(roleDepartmentDO -> {
				RoleDepartment roleDepartment = new RoleDepartment();
				BeanUtils.copyProperties(roleDepartmentDO, roleDepartment);
				Role role = roleService.getRoleInfoById(roleDepartmentDO.getRoleId());
				if (role != null) {
					roleDepartment.setRoleName(role.getRoleName());
				}
				Department department =  departmentService.getWithParentIdById(roleDepartment.getDepartmentId());
				if (department != null) {
					roleDepartment.setDepartmentName(department.getDepartmentName());
				}
				rList.add(roleDepartment);
			});
		}
		return rList;
	}
	
	private RoleDepartmentDO roleDepartmentToRoleDepartmentDO (RoleDepartment roleDepartment) {
		RoleDepartmentDO roleDepartmentDO = null;
		if (roleDepartment != null) {
			roleDepartmentDO = new RoleDepartmentDO();
			BeanUtils.copyProperties(roleDepartment, roleDepartmentDO);
		}
		return roleDepartmentDO;
	}

	private RoleDepartment roleDepartmentDOToRoleDepartment (RoleDepartmentDO roleDepartmentDO) {
		RoleDepartment roleDepartment = null;
		if (roleDepartmentDO != null) {
			roleDepartment = new RoleDepartment();
			BeanUtils.copyProperties(roleDepartmentDO, roleDepartment);
			Role role = roleService.getRoleInfoById(roleDepartmentDO.getRoleId());
			if (role != null) {
				roleDepartment.setRoleName(role.getRoleName());
			}
			Department department =  departmentService.getWithParentIdById(roleDepartment.getDepartmentId());
			if (department != null) {
				roleDepartment.setDepartmentName(department.getDepartmentName());
			}
		}
		return roleDepartment;
	}
	
	@Override
	public RoleDepartment selectRoleDepartmentById(Integer id) {
		RoleDepartmentDO roleDepartmentDO = roleDepartmentMapper.selectById(id);
		List<Integer> menuIds = Lists.newArrayList();
		RoleDepartment roleDepartment = roleDepartmentDOToRoleDepartment(roleDepartmentDO);
		if (roleDepartmentDO != null) {
			Role role = roleService.getRoleInfoById(roleDepartmentDO.getRoleId());
			if (role != null) {
				roleDepartment.setRoleName(role.getRoleName());
			}
			// TODO
			List<RoleDepartmentMenu> roleDepartmentMenus = roleDepartmentMenuService.listByRoleDepartId(id);
			if (CollectionUtils.isNotEmpty(roleDepartmentMenus)) {
				roleDepartmentMenus.forEach(roleDepartmentMenu -> {
					menuIds.add(roleDepartmentMenu.getMenuId());
				});
			}
		}
		List<Menu> menuList = menuService.listMenuByIds(menuIds);
		menuIds.clear();
		Map<Integer, Long> childrenCountMap = menuService.getChildrenMenuCountByParentId();
		getMenuIds(menuIds, menuList, childrenCountMap);
		roleDepartment.setMenuIdList(menuIds);
		return roleDepartment;
	}

	private void getMenuIds(List<Integer> menuIds, List<Menu> menuList, Map<Integer, Long> childrenCountMap) {
		Map<Integer, Menu> tempMenuMap = Maps.newHashMap();
		Map<Integer, List<Menu>> menuMap = menuList.stream()
				.filter(menu -> {
					tempMenuMap.put(menu.getId(), menu);
					return true;
				})
				.sorted(Comparator.comparing(Menu::getParentId))
				.collect(Collectors.groupingBy(Menu::getParentId));
		menuMap.forEach((key, list) -> {
			if(childrenCountMap.get(key) == 0 && AssertUtil.isEmpty(list)){
				menuIds.add(key);
			}
			//查询目录下全部的菜单
			logger.info("key:{}, parent:{}, current count:{}", key, childrenCountMap.get(key), list.size());
//			if(childrenCountMap.get(key) == list.size()){
				List<Integer> tempMenuIds = Lists.newArrayList();
				int flag = 0;
				for(int i=0; i < list.size(); i++){
					Menu menu = list.get(i);
					//获取菜单下所有的按钮
					Long tempButtonCount = childrenCountMap.get(menu.getId());
					List<Menu> buttonList = menuMap.get(menu.getId());
					if((tempButtonCount == null || tempButtonCount == 0) && AssertUtil.isEmpty(buttonList)){
						menuIds.add(menu.getId());
					}
					if(menu.getMenuType().equals(MenuTypeEnum.MENU.getCode())){
						logger.info("Menu id:{}, tempMenu count:{}, button count:{}", 
								menu.getId(), tempButtonCount, buttonList != null?buttonList.size():null);
						if(AssertUtil.isNotEmpty(buttonList)){
							menuIds.addAll(buttonList.stream().map(button -> button.getId()).collect(Collectors.toList()));
						}
						if(tempButtonCount != null && AssertUtil.isNotEmpty(buttonList)
										&& tempButtonCount.intValue() == buttonList.size()){
							tempMenuIds.add(menu.getId());
							flag ++;
						}
					}
				}
				if(flag == childrenCountMap.get(key).intValue()){
					menuIds.add(key);
					menuIds.addAll(tempMenuIds);
				}
//			}
		});
	}

	@Override
	public List<RoleDepartment> listRoleDepartments(String permissionName, Integer offset, Integer limit) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("permissionName", permissionName);
		map.put("offset", offset);
		map.put("limit", limit);
		List<RoleDepartmentDO> roleDepartmentDOs = roleDepartmentMapper.selectRoleDepartments(map);
		return roleDepartmentToRoleDepartmentDO(roleDepartmentDOs);
	}

	@Override
	public int countRoleDepartments(String permissionName) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("permissionName", permissionName);
		return roleDepartmentMapper.countRoleDepartments(map);
	}

	@Override
	public void deleteBatch(List<Integer> roleDepartmentIds) {

		Map<String, Object> map = Maps.newHashMap();
		map.put("roleDepartmentIds", roleDepartmentIds);
		
		// 更新
		roleDepartmentMapper.deleteBatch(map);

		// 删除
		roleDepartmentMenuService.deleteByRoleDepartmentIds(roleDepartmentIds);
		
	}

	@Override
	public List<RoleDepartment> listRoleDepartments(RoleDepartmentQuery roleDepartmentQuery) {
		RoleDepartmentDAOQuery roleDepartmentDAOQuery = new RoleDepartmentDAOQuery();
		BeanUtils.copyProperties(roleDepartmentQuery, roleDepartmentDAOQuery);
		List<RoleDepartmentDO> roleDepartmentDOs = roleDepartmentMapper.selectByNames(roleDepartmentDAOQuery);
		List<RoleDepartment> roleDepartments = roleDepartmentToRoleDepartmentDO(roleDepartmentDOs);
		if (CollectionUtils.isNotEmpty(roleDepartments)) {
			Map<Integer, Long> childrenCountMap = menuService.getChildrenMenuCountByParentId();
			roleDepartments.forEach(roleDepartment -> {
				List<Integer> menuIds = Lists.newArrayList();
				List<RoleDepartmentMenu> roleDepartmentMenus = roleDepartmentMenuService.listByRoleDepartId(roleDepartment.getId());
				if (CollectionUtils.isNotEmpty(roleDepartmentMenus)) {
					roleDepartmentMenus.forEach(roleDepartmentMenu -> {
						menuIds.add(roleDepartmentMenu.getMenuId());
					});
					List<Menu> menuList = menuService.listMenuByIds(menuIds);
					menuIds.clear();
					getMenuIds(menuIds, menuList, childrenCountMap);
					roleDepartment.setMenuIdList(menuIds);
				}
			});
		}
		return roleDepartments;
	}

	@Override
	public int countRoleDepartments(RoleDepartmentQuery roleDepartmentQuery) {
		RoleDepartmentDAOQuery roleDepartmentDAOQuery = new RoleDepartmentDAOQuery();
		BeanUtils.copyProperties(roleDepartmentQuery, roleDepartmentDAOQuery);
		return roleDepartmentMapper.countByNames(roleDepartmentDAOQuery);
	}

	@Override
	public RoleDepartment getBydepartmentIdAndRoleId(Integer departmentId, Integer roleId) {
		if (departmentId == null || roleId == null) {
			return null;
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("departmentId", departmentId);
		map.put("roleId", roleId);
		RoleDepartmentDO roleDepartmentDO = roleDepartmentMapper.selectBydepartmentIdAndRoleId(map);
		return roleDepartmentDOToRoleDepartment(roleDepartmentDO);
	}
	
	/*
	private List<Menu> listMenuByIds(List<Integer> menuIds) {
		List<Menu> menuList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(menuIds)) {
			List<MenuDO> menuDos = menuMapper.selectByIds(menuIds);
			if (CollectionUtils.isNotEmpty(menuDos)) {
				menuDos.forEach(menu -> {
					Menu m = new Menu();
					BeanUtils.copyProperties(menu, m);
					menuList.add(m);
				});
			}
		}
		return menuList;
	}*/
}
