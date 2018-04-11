package com.mytijian.admin.api.rbac.service;

import java.util.List;
import java.util.Map;

import com.mytijian.admin.api.rbac.model.Menu;

/**
 * 菜单管理
 * 
 * @author feng
 * @email 
 * @date 
 */
public interface MenuService {
	
	/**
	 * 根据父菜单，查询子菜单
	 * @param parentId 父菜单ID
	 * @param menuIdList 用户菜单ID
	 */
	List<Menu> listMenusByParentId(Integer parentId, List<Integer> menuIdList);
	
	/**
	 * 查询菜单列表
	 */
	List<Menu> listMenus(Map<String, Object> map);
	
	/**
	 * 查询总数
	 */
	int countTotal(Map<String, Object> map);
	
	/**
	 * 获取不包含按钮的菜单列表
	 * @param parentId 父级Id
	 * @return
	 */
	List<Menu> listNoButtonMenus(Integer parentId);
	
	/**
	 * 根据菜单Id获取菜单列表
	 * @param menuIds
	 * @return
	 */
	List<Menu> listMenuByIds(List<Integer> menuIds);
	
	/**
	 * 获取用户菜单列表
	 */
	//List<Menu> getEmployeeMenuList(Integer employeeId);
	
	/**
	 * 根据职工编号回去职工菜单列表
	 * @param employeeId 职工Id
	 * @return
	 */
	List<Menu> listEmployeeMenu(Integer employeeId);
	
	/**
	 * 根据菜单Id获取菜单信息
	 * @param menuId 菜单Id
	 * @return
	 */
	Menu getById(Integer menuId);
	
	/**
	 * 保存菜单
	 */
	Integer saveMenu(Menu menu);
	
	/**
	 * 修改菜单
	 */
	void updateMenu(Menu menu);
	
	/**
	 * 删除
	 */
	void deleteBatch(List<Integer> menuIds);
	
	/**
	 * 根据职工Id获取职工菜单Id
	 * @param employeeId
	 * @return
	 */
	List<Integer> listMenuIdByEmployeeId(Integer employeeId);
	
	/**
	 * 根据父id获取子标签数量
	 * @return
	 */
	Map<Integer, Long> getChildrenMenuCountByParentId();
	
}
