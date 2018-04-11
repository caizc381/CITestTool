package com.mytijian.admin.web.rbac.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.mytijian.admin.api.rbac.constant.MenuTypeEnum;
import com.mytijian.admin.api.rbac.model.Menu;
import com.mytijian.admin.api.rbac.service.EmployeeService;
import com.mytijian.admin.api.rbac.service.MenuService;
import com.mytijian.admin.dao.base.dataobject.PageUtils;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.mediator.exceptions.ServiceException;
import com.mytijian.mediator.menu.constans.MenuExceptionCode;
import com.mytijian.util.AssertUtil;

/**
 * 
 * 类MenuController.java的实现描述：菜单相关
 * @author zhanfei.feng 2017年4月7日 下午4:01:34
 */
@RestController
@RequestMapping("/sys/menu")
public class MenuController {

	private final static Logger logger = LoggerFactory.getLogger(MenuController.class);

	@Resource(name = "employeeService")
	private EmployeeService employeeService;

	@Resource(name = "menuService")
	private MenuService menuService;

	/**
	 * 获取用户菜单列表
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/user", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Menu> getEmployeeMenu() throws ServiceException {
		return menuService.listEmployeeMenu(SessionUtil.getEmployeeId());
	}

	/**
	 * 获取菜单列表
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:menu:list")
	public PageUtils list(Integer currPage, Integer pageSize) {
		Map<String, Object> map = new HashMap<>();
		currPage = currPage == null ? 1 : currPage;
		pageSize = pageSize == null ? 12 : pageSize;
		map.put("offset", (currPage - 1) * pageSize);
		map.put("limit", pageSize);

		// 查询列表数据
		List<Menu> menuList = menuService.listMenus(map);
		int total = menuService.countTotal(map);
		PageUtils pageUtil = new PageUtils(menuList, total, pageSize, currPage);
		return pageUtil;
	}

	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@RequestMapping("/select")
	// @RequiresPermissions("sys:menu:select")
	public List<Menu> select() {
		// 查询列表数据
		// List<Menu> menuList = menuService.queryNotButtonList(0);

		// 添加顶级菜单
		/*Menu root = new Menu();
		root.setId(0);
		root.setMenuName("一级菜单");
		root.setParentId(-1);
		root.setOpen(true);
		menuList.add(root);
		System.out.print("选择菜单(添加、修改菜单)" + JSON.toJSON(menuList));*/

		List<Menu> menuList = menuService.listNoButtonMenus(0);
		if (!CollectionUtils.isEmpty(menuList)) {
			menuList.forEach(menu -> {
				List<Menu> menuChile = menuService.listMenusByParentId(menu.getId(), null);
				if (AssertUtil.isNotEmpty(menuChile)) {
//					menuChile.forEach(mc -> {
//						List<Menu> mcList = menuService.listMenusByParentId(mc.getId(), null);
//						if (mcList != null) {
//							mc.setList(mcList);
//						}
//					});
					menu.setList(menuChile);
				}
			});
		}

		return menuList;
	}

	/**
	 * 角色授权菜单
	 */
	@RequestMapping("/perms")
	// @RequiresPermissions("sys:menu:perms")
	public List<Menu> perms() {
		// 查询列表数据
		return menuService.listMenus(new HashMap<String, Object>());
	}

	/**
	 * 角色授权菜单
	 */
	@RequestMapping("/permsInfo")
	// @RequiresPermissions("sys:menu:perms")
	public List<Menu> permsInfo() {
		// 查询列表数据
		List<Menu> menus = menuService.listMenusByParentId(0, null);
		if (!CollectionUtils.isEmpty(menus)) {
			menus.forEach(menu -> {
				List<Menu> menuChile = menuService.listMenusByParentId(menu.getId(), null);
				if (menuChile != null) {
					menuChile.forEach(mc -> {
						List<Menu> mcList = menuService.listMenusByParentId(mc.getId(), null);
						if (mcList != null) {
							mc.setList(mcList);
						}
					});

					menu.setList(menuChile);
				}
			});
		}
		return menus;
	}

	/**
	 * 菜单信息
	 */
	@RequestMapping("/info/{menuId}")
	@RequiresPermissions("sys:menu:info")
	public Menu info(@PathVariable("menuId") Integer menuId) {
		Menu menu = menuService.getById(menuId);
		if(menu != null && menu.getParentId() != 0){
			Menu parentMenu = menuService.getById(menu.getParentId());
			if(parentMenu != null && parentMenu.getParentId() != 0){
				Menu parentFatherMenu = menuService.getById(parentMenu.getParentId());
				List<Integer> list = Lists.newArrayList();
				list.add(parentFatherMenu.getId());
				list.add(menu.getParentId());
				menu.setParentIds(list);
			}
		}
		return menu;
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:menu:save")
	public boolean save(@RequestBody Menu menu) {
		// 数据校验
		verifyForm(menu);

		menuService.saveMenu(menu);

		return true;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:menu:update")
	public boolean update(@RequestBody Menu menu) {
		// 数据校验
		verifyForm(menu);

		menuService.updateMenu(menu);

		return true;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:menu:delete")
	public boolean delete(@RequestParam(value = "menuIds") List<Integer> menuIds) {
		menuService.deleteBatch(menuIds);
		return true;
	}

	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(Menu menu) {
		if (StringUtils.isEmpty(menu.getMenuName())) {
			logger.error(MenuExceptionCode.MENU_NAME_EMPTY + " menuName : {}", menu.getMenuName());
			throw ExceptionFactory.makeFault(MenuExceptionCode.MENU_NAME_EMPTY, new Object[] { null });

		}

		if (StringUtils.isEmpty(menu.getParentId())) {
			logger.error(MenuExceptionCode.MENU_PARENT_ID_EMPTY + " parentId : {}", menu.getParentId());
			throw ExceptionFactory.makeFault(MenuExceptionCode.MENU_PARENT_ID_EMPTY, new Object[] { null });
		}

		// 菜单
		if (menu.getMenuType().intValue() == MenuTypeEnum.MENU.getCode()) {
			if (StringUtils.isEmpty(menu.getMenuUrl())) {
				logger.error(MenuExceptionCode.MENU_URL_EMPTY + " menuUrl : {}", menu.getMenuUrl());
				throw ExceptionFactory.makeFault(MenuExceptionCode.MENU_URL_EMPTY, new Object[] { null });
			}
		}

		// 上级菜单类型默认为目录
		int parentType = MenuTypeEnum.CATALOG.getCode();
		if (menu.getParentId() != 0) {
			Menu parentMenu = menuService.getById(menu.getParentId());
			parentType = parentMenu.getMenuType();
		}

		// 目录、菜单
		if (menu.getMenuType().intValue() == MenuTypeEnum.CATALOG.getCode()
				|| menu.getMenuType() == MenuTypeEnum.MENU.getCode()) {
			if (parentType != MenuTypeEnum.CATALOG.getCode()) {
				logger.error(MenuExceptionCode.PARENT_MENU_SUPPORT_DIR_TYPE + " parentType : {}", parentType);
				throw ExceptionFactory.makeFault(MenuExceptionCode.PARENT_MENU_SUPPORT_DIR_TYPE, new Object[] { null });
			}
			return;
		}

		// 按钮
		if (menu.getMenuType().intValue() == MenuTypeEnum.BUTTON.getCode()) {
			if (parentType != MenuTypeEnum.MENU.getCode()) {
				logger.error(MenuExceptionCode.PARENT_MENU_SUPPORT_MENU_TYPE + " parentType : {}", parentType);
				throw ExceptionFactory.makeFault(MenuExceptionCode.PARENT_MENU_SUPPORT_MENU_TYPE,
						new Object[] { null });
			}
			return;
		}
	}
	
	/**
	 * 初始化体检中心按钮权限
	 * @return
	 */
	@RequestMapping(value = "/initHospitalMenu", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public String initHospitalMenu(){
		Menu menuHos = new Menu();
		menuHos.setMenuName("体检中心管理");
		menuHos.setMenuUrl("/hospitalManage");
		menuHos.setParentId(0);
		menuHos.setSeq(0);
		menuHos.setStatus(1);
		menuHos.setMenuType(MenuTypeEnum.CATALOG.getCode());
		Integer hosPid = menuService.saveMenu(menuHos);
		Menu list = new Menu();
		list.setMenuName("体检中心列表");
		list.setMenuUrl("/list");
		list.setParentId(hosPid);
		list.setSeq(0);
		list.setPerms("hospitalManage:list");
		list.setStatus(1);
		list.setMenuType(MenuTypeEnum.MENU.getCode());
		Integer listId = menuService.saveMenu(list);
		addMenuButton(listId, "进入CRM","hospitalManage:list:intoCRM");
		addMenuButton(listId, "重置CRM管理员密码", "hospitalManage:list:resetPassword");
		addMenuButton(listId, "执行单项更改任务", "hospitalManage:list:examitemJob");
		addMenuButton(listId, "开启/关闭体检中心平台显示", "hospitalManage:list:showInList");
		addMenuButton(listId, "开启单位同步", "hospitalManage:list:openSyncCompany");
		addMenuButton(listId, "切换深/浅对接", "hospitalManage:list:changeJoint");
		addMenuButton(listId, "新建体检中心", "hospitalManage:list:newHospital");
		addMenuButton(listId, "导出EXCEL", "hospitalManage:list:exportHospital");
		//各种编辑权限
		addMenuButton(listId, "基本信息编辑", "hospitalManage:list:basicEdit");
		addMenuButton(listId, "业务人员编辑", "hospitalManage:list:businessEdit");
		addMenuButton(listId, "基本信息更多编辑", "hospitalManage:list:basicMoreEdit");
		addMenuButton(listId, "CRM账号更改", "hospitalManage:list:crmUserNameEdit");
		addMenuButton(listId, "功能开通设置", "hospitalManage:list:functionSetting");
		addMenuButton(listId, "交易设置", "hospitalManage:list:tradeSetting");
		addMenuButton(listId, "体检预约时间设置", "hospitalManage:list:examTimeSetting");
		addMenuButton(listId, "添加时段", "hospitalManage:list:orderTime");
		addMenuButton(listId, "折扣信息设置", "hospitalManage:list:discount");
		addMenuButton(listId, "平台合作其他信息编辑", "hospitalManage:list:cooperationOther");
		addMenuButton(listId, "站点编辑", "hospitalManage:list:siteEdit");
		addMenuButton(listId, "医院合作联系人信息", "hospitalManage:list:hospitalContact");
		addMenuButton(listId, "医院合作签合约信息查看", "hospitalManage:list:signInfo");
		addMenuButton(listId, "医院合作签合约信息编辑", "hospitalManage:list:signEdit");
		addMenuButton(listId, "体检软件厂商对接信息", "hospitalManage:list:hisInfo");
		addMenuButton(listId, "消息设置编辑", "hospitalManage:list:msgSettingEdit");
		addMenuButton(listId, "问卷信息编辑", "hospitalManage:list:surveyInfoEdit");
		return "Success";
	}

	private void addMenuButton(Integer hosPid, String name, String perms) {
		Menu menu = new Menu();
		menu.setMenuName(name);
		menu.setSeq(0);
		menu.setStatus(1);
		menu.setParentId(hosPid);
		menu.setPerms(perms);
		menu.setMenuType(MenuTypeEnum.BUTTON.getCode());
		menuService.saveMenu(menu);
	}

}
