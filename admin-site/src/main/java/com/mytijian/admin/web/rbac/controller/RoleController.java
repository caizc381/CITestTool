package com.mytijian.admin.web.rbac.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.admin.api.rbac.model.Role;
import com.mytijian.admin.api.rbac.service.RoleService;
import com.mytijian.admin.dao.base.dataobject.PageUtils;
import com.mytijian.admin.web.util.CommonUtil;
import com.mytijian.admin.web.util.Page;
import com.mytijian.common.dto.SyncMessageDTO;
import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.mediator.role.constans.RoleExceptionCode;

/**
 * 
 * 类RoleController.java的实现描述：角色相关
 * @author zhanfei.feng 2017年4月7日 下午4:02:57
 */
@RestController
@RequestMapping("/sys/role")
public class RoleController {

	private final static Logger logger = LoggerFactory.getLogger(MenuController.class);

	@Resource(name = "roleService")
	private RoleService roleService;

	/**
	 * 角色列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:role:list")
	public PageUtils list(String roleName, Integer currPage, Integer pageSize,Boolean isPage) {

		Page page = CommonUtil.getPage(currPage, pageSize);
		Integer offset = page.getOffset();
		pageSize = page.getPageSize();
		currPage = page.getCurrPage();
		
		if(!isPage){
			offset = null;
		}
		
		// 查询列表数据
		List<Role> roleList = roleService.listRoles(roleName, offset, pageSize);
		int total = roleService.countTotal(roleName, offset, pageSize);
		PageUtils pageUtil = new PageUtils(roleList, total, pageSize, currPage);
		return pageUtil;
	}

	@RequestMapping("/select")
	@RequiresPermissions("sys:role:select")
	public List<Role> select() {
		// 查询列表数据
		return roleService.listRoles(null, null, null);
	}

	/**
	 * 角色信息
	 */
	@RequestMapping("/info/{roleId}")
	@RequiresPermissions("sys:role:info")
	public Role info(@PathVariable("roleId") Integer roleId) {
		return roleService.getRoleInfoById(roleId);
	}

	/**
	 * 保存角色
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:role:save")
	public boolean save(@RequestBody Role role) {

		if (StringUtils.isEmpty(role.getRoleName())) {
			logger.error(RoleExceptionCode.ROLE_LOGINNAME_EMPTY + " roleName : {}", role.getRoleName());
			throw ExceptionFactory.makeFault(RoleExceptionCode.ROLE_LOGINNAME_EMPTY, new Object[] { null });
		}

		roleService.saveRole(role);

		return true;
	}

	/**
	 * 修改角色
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:role:update")
	public boolean update(@RequestBody Role role) {
		if (StringUtils.isEmpty(role.getRoleName())) {
			logger.error(RoleExceptionCode.ROLE_LOGINNAME_EMPTY + " roleName : {} ", role.getRoleName());
			throw ExceptionFactory.makeFault(RoleExceptionCode.ROLE_LOGINNAME_EMPTY, new Object[] { null });
		}

		if (StringUtils.isEmpty(role.getId())) {
			logger.error(RoleExceptionCode.ROLE_ID_EMPTY + " id : {} ", role.getId());
			throw ExceptionFactory.makeFault(RoleExceptionCode.ROLE_ID_EMPTY, new Object[] { null });
		}

		roleService.updateRole(role);

		return true;
	}

	/**
	 * 删除角色
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:role:delete")
	public boolean delete(@RequestParam(value = "roleIds") List<Integer> roleIds) {
		roleService.deleteBatch(roleIds);
		return true;
	}
	
	@RequestMapping(value = "/allValidSyncMessages")
	public List<SyncMessageDTO> getAllValidSyncMessages(){
		List<SyncMessageDTO> syncMsgList =null;
		return syncMsgList;
	}
	
}
