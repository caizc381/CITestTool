package com.mytijian.admin.dao.rbac.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.rbac.dataobject.MenuDO;


@Repository("menuMapper")
public interface MenuMapper extends BaseMapper<MenuDO> {
	
	/**
	 * 根据父级Id获取菜单列表
	 * @param parentId 父级Id
	 */
	List<MenuDO> selectByParentId(Integer parentId);
	
	/**
	 * 查询菜单列表
	 * @param params
	 * @return
	 */
	List<MenuDO> selectMenus(Map<String, Object> params);
	
	/**
	 * 获取菜单总数
	 * @param params
	 * @return
	 */
	int countTotal(Map<String, Object> params);
	
	/**
	 * 获取没有按钮的菜单列表
	 * @param map
	 * @return
	 */
	List<MenuDO> selectNoButtonMenus(Map<String, Object> map);
	
	
	void deleteBatch(List<Integer> menuIds);
	
	List<MenuDO> selectByIds(List<Integer> menuIds);
	
	/**
	 * 获取所有菜单
	 * @param menuIds
	 * @return
	 */
	List<MenuDO> selectAllMenu();
}
