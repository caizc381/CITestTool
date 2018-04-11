package com.mytijian.admin.service.rbac.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.admin.api.rbac.constant.MenuStatusEnum;
import com.mytijian.admin.api.rbac.constant.MenuTypeEnum;
import com.mytijian.admin.api.rbac.model.DepartmentEmployee;
import com.mytijian.admin.api.rbac.model.EmployeeRole;
import com.mytijian.admin.api.rbac.model.Menu;
import com.mytijian.admin.api.rbac.model.RoleDepartment;
import com.mytijian.admin.api.rbac.model.RoleDepartmentMenu;
import com.mytijian.admin.api.rbac.service.DepartmentEmployeeService;
import com.mytijian.admin.api.rbac.service.DepartmentService;
import com.mytijian.admin.api.rbac.service.EmployeeRoleService;
import com.mytijian.admin.api.rbac.service.MenuService;
import com.mytijian.admin.api.rbac.service.RoleDepartmentMenuService;
import com.mytijian.admin.api.rbac.service.RoleDepartmentService;
import com.mytijian.admin.api.rbac.service.RoleService;
import com.mytijian.admin.dao.rbac.dao.MenuMapper;
import com.mytijian.admin.dao.rbac.dataobject.MenuDO;
import com.mytijian.util.AssertUtil;


/**
 * 菜单管理
 * 
 * @author feng
 * @email
 * @date
 */

@Service("menuService")
public class MenuServiceImpl implements MenuService {

	private final static Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);

	@Resource(name = "menuMapper")
	private MenuMapper menuMapper;
	
	@Resource(name = "roleService")
	private RoleService roleService;
	
	@Resource(name = "departmentService")
	private DepartmentService departmentService;

	@Resource(name = "roleDepartmentService")
	private RoleDepartmentService roleDepartmentService;
		
	@Resource(name = "roleDepartmentMenuService")
	private RoleDepartmentMenuService roleDepartmentMenuService;

	@Resource(name = "departmentEmployeeService")
	private DepartmentEmployeeService departmentEmployeeService;

	@Resource(name = "employeeRoleService")
	private EmployeeRoleService employeeRoleService;

	@Override
	public List<Menu> listMenusByParentId(Integer parentId, List<Integer> menuIdList) {
		List<Menu> employeeMenuList = Lists.newArrayList();
		List<MenuDO> menuList = menuMapper.selectByParentId(parentId);

		if (menuIdList == null) {
			return menuDOToMenu(menuList);
		}

		if (CollectionUtils.isNotEmpty(menuList)) {
			menuList.forEach(ml -> {
				if (menuIdList.contains(ml.getId())) {
					Menu me = new Menu();
					BeanUtils.copyProperties(ml, me);
					employeeMenuList.add(me);
				}
			});
		}

		return employeeMenuList;
	}

	@Override
	public List<Menu> listNoButtonMenus(Integer parentId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("parentId", parentId);
		List<MenuDO> menuDOs = menuMapper.selectNoButtonMenus(map);
		return menuDOToMenu(menuDOs);
	}

	@Override
	public List<Menu> listEmployeeMenu(Integer employeeId) {
		if (employeeId == null) {
			logger.error("MenuService.listEmployeeMenu employeeId is null");
			return null;
		}
		if (employeeId.intValue() == 1) {
			return listAllMenus(null);
		}
		List<Integer> menuIdList = listMenuIdByEmployeeId(employeeId);
		// 用户菜单列表
		return listAllMenus(menuIdList);
	}

	@Override
	public Menu getById(Integer menuId) {
		if (menuId == null) {
			logger.error("MenuService.getMenuInfoById menuId is null");
			return null;
		}
		MenuDO menuDo = menuMapper.selectById(menuId);
		return menuDOToMenu(menuDo);
	}

	@Override
	public List<Menu> listMenus(Map<String, Object> map) {
		List<MenuDO> menuDOs = menuMapper.selectMenus(map);
		return menuDOToMenu(menuDOs);
	}

	@Override
	public int countTotal(Map<String, Object> map) {
		return menuMapper.countTotal(map);
	}

	@Override
	public Integer saveMenu(Menu menu) {
		MenuDO menuDO = new MenuDO();
		BeanUtils.copyProperties(menu, menuDO);
		if (menuDO.getStatus() == null)
			menuDO.setStatus(MenuStatusEnum.NORMAL.getCode());
		if (menuDO.getSeq() == null)
			menuDO.setSeq(0);
		menuMapper.insert(menuDO);
		return menuDO.getId();
	}

	@Override
	public void updateMenu(Menu menu) {
		MenuDO menuDO = new MenuDO();
		BeanUtils.copyProperties(menu, menuDO);
		menuMapper.update(menuDO);
	}

	@Override
	public void deleteBatch(List<Integer> menuIds) {
		if (!CollectionUtils.isEmpty(menuIds)) {
			menuMapper.deleteBatch(menuIds);
		}
	}

	/**
	 * 获取所有菜单列表
	 */
	private List<Menu> listAllMenus(List<Integer> menuIdList) {
		// 查询根菜单列表
		List<Menu> menuList = listMenusByParentId(0, menuIdList);
		// 递归获取子菜单
		getMenuTreeList(menuList, menuIdList);
		return menuList;
	}

	/**
	 * 递归
	 */
	private List<Menu> getMenuTreeList(List<Menu> menuList, List<Integer> menuIdList) {
		List<Menu> subMenuList = Lists.newArrayList();
		for (Menu entity : menuList) {
			if (entity.getMenuType() == MenuTypeEnum.CATALOG.getCode()) { // 目录
				entity.setList(getMenuTreeList(listMenusByParentId(entity.getId(), menuIdList), menuIdList));
			}
			subMenuList.add(entity);
		}
		return subMenuList;
	}

	@Override
	public List<Menu> listMenuByIds(List<Integer> menuIds) {
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
	}

	@Override
	public List<Integer> listMenuIdByEmployeeId(Integer employeeId) {

		List<Integer> menuIds = Lists.newArrayList();

		// 获取用户的部门
		DepartmentEmployee employeeDepartment = departmentEmployeeService.getByEmployeeId(employeeId);
		if (employeeDepartment == null) {
			return menuIds;
		}

		// 获取用户的角色
		List<EmployeeRole> employeeRoles = employeeRoleService.listEmployeeRoles(employeeId);

		if (CollectionUtils.isEmpty(employeeRoles)) {
			return menuIds;
		}
		List<Integer> roleIds = Lists.newArrayList();
		employeeRoles.forEach(employeeRole -> {
			roleIds.add(employeeRole.getRoleId());
		});

		// 获取角色部门关联信息列表
		List<RoleDepartment> roleDepartments = roleDepartmentService.listBydepartmentIdAndRoleIds(employeeDepartment.getDepartmentId(), roleIds);

		if (CollectionUtils.isNotEmpty(roleDepartments)) {
			List<Integer> roleDepartmentIds = Lists.newArrayList();
			roleDepartments.forEach(roleDepartment -> {
				roleDepartmentIds.add(roleDepartment.getId());
			});

			// 获取角色部门与菜单的关联信息
			List<RoleDepartmentMenu> roleDepartmentMenus = roleDepartmentMenuService
					.listByRoleDepartmentIds(roleDepartmentIds);
			if (CollectionUtils.isNotEmpty(roleDepartmentMenus)) {
				List<Integer> relationMenuIds = Lists.newArrayList();
				roleDepartmentMenus.forEach(roleDepartmentMenu -> {
					relationMenuIds.add(roleDepartmentMenu.getMenuId());
				});

				List<Menu> menus = listMenuByIds(relationMenuIds);
				if (CollectionUtils.isNotEmpty(menus)) {
					menus.forEach(menu -> {
						menuIds.add(menu.getId());
					});
				}
			}
		}

		return menuIds;
	}

	private List<Menu> menuDOToMenu(List<MenuDO> menuList) {
		List<Menu> menus = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(menuList)) {
			menuList.forEach(menu -> {
				Menu me = new Menu();
				BeanUtils.copyProperties(menu, me);
				menus.add(me);
			});
		}
		return menus;
	}

	private Menu menuDOToMenu(MenuDO menuDO) {
		Menu menu = null;
		if (menuDO != null) {
			menu = new Menu();
			BeanUtils.copyProperties(menuDO, menu);
		}
		return menu;
	}

	@Override
	public Map<Integer, Long> getChildrenMenuCountByParentId() {
		List<MenuDO> mapList = menuMapper.selectAllMenu();
		Map<Integer, Long> resultMap = Maps.newHashMap();
		if(AssertUtil.isNotEmpty(mapList)){
			resultMap = mapList.stream().collect(Collectors.groupingBy(MenuDO::getParentId, Collectors.counting()));
		}
		return resultMap;
	}
	
}
