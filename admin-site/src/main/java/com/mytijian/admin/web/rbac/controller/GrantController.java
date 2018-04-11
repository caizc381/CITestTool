package com.mytijian.admin.web.rbac.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.admin.api.rbac.model.RoleDepartment;
import com.mytijian.admin.api.rbac.param.RoleDepartmentQuery;
import com.mytijian.admin.api.rbac.service.RoleDepartmentService;
import com.mytijian.admin.dao.base.dataobject.PageUtils;
import com.mytijian.admin.web.util.CommonUtil;
import com.mytijian.admin.web.util.Page;
import com.mytijian.mediator.department.constans.DepartmentExceptionCode;
import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.mediator.role.constans.RoleDepartmentExceptionCode;
import com.mytijian.mediator.role.constans.RoleExceptionCode;

/**
 * 
 * 类GrantController.java的实现描述：授权相关
 * @author zhanfei.feng 2017年4月7日 下午3:59:22
 */
@RestController
@RequestMapping("/sys/grant")
public class GrantController {

	private final static Logger logger = LoggerFactory.getLogger(GrantController.class);

	@Resource(name = "roleDepartmentService")
	private RoleDepartmentService roleDepartmentService;

	/**
	 * 保存授权
	 * @param roleDepartment
	 * @return
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:authorize:save")
	public boolean saveGrant(@RequestBody RoleDepartment roleDepartment) {
		// 数据校验
		verifyAddGrantForm(roleDepartment);

		RoleDepartment roleDepartment2 = roleDepartmentService
				.getBydepartmentIdAndRoleId(roleDepartment.getDepartmentId(), roleDepartment.getRoleId());
		if (roleDepartment2 != null) {
			logger.error("saveGrant error, departmentId : {}, roleId: {}", roleDepartment.getDepartmentId(),
					roleDepartment.getRoleId());
			throw ExceptionFactory.makeFault(RoleDepartmentExceptionCode.ROLE_DEPARTMENT_ARLEADY_EXIST,
					new Object[] { null });
		}
		roleDepartmentService.addRoleDepartment(roleDepartment);

		return true;
	}

	/**
	 * 更新授权
	 * @param roleDepartment
	 * @return
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:authorize:update")
	public boolean updateGrant(@RequestBody RoleDepartment roleDepartment) {
		// 数据校验
		verifyUpdateGrantForm(roleDepartment);

		roleDepartmentService.updateRoleDepartment(roleDepartment.getId(), roleDepartment.getMenuIdList());
		return true;
	}

	@RequestMapping("/info/{roleDepartmentId}")
	@RequiresPermissions("sys:authorize:info")
	public RoleDepartment info(@PathVariable("roleDepartmentId") Integer roleDepartmentId) {
		return roleDepartmentService.selectRoleDepartmentById(roleDepartmentId);
	}

	/**
	 * 获取授权列表
	 * @param permissionName
	 * @param roleName
	 * @param departmentName
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/list")
	public PageUtils list(String permissionName, String roleName, String departmentName, Integer currPage,
			Integer pageSize) {
		Page page = CommonUtil.getPage(currPage, pageSize);
		RoleDepartmentQuery roleDepartmentQuery = new RoleDepartmentQuery();
		roleDepartmentQuery.setPageSize(page.getPageSize());
		roleDepartmentQuery.setCurrPage(page.getCurrPage());
		roleDepartmentQuery.setOffset(page.getOffset());
		roleDepartmentQuery.setLimit(page.getPageSize());
		roleDepartmentQuery.setPermissionName(permissionName);
		roleDepartmentQuery.setRoleName(roleName);
		roleDepartmentQuery.setDepartmentName(departmentName);

		List<RoleDepartment> roleDepartments = roleDepartmentService.listRoleDepartments(roleDepartmentQuery);
		int total = roleDepartmentService.countRoleDepartments(roleDepartmentQuery);

		return new PageUtils(roleDepartments, total, roleDepartmentQuery.getPageSize(),
				roleDepartmentQuery.getCurrPage());
	}

	/**
	 * 删除授权
	 * @param roleDepartmentIds
	 * @return
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:authorize:delete")
	public boolean delete(@RequestParam(value = "roleDepartmentIds") List<Integer> roleDepartmentIds) {
		roleDepartmentService.deleteBatch(roleDepartmentIds);
		return true;
	}

	/**
	 * 验证参数是否正确
	 */
	private void verifyAddGrantForm(RoleDepartment roleDepartment) {

		if (roleDepartment.getDepartmentId() == null) {
			logger.error("verifyAddGrantForm error, departmentId : {}", roleDepartment.getDepartmentId());
			throw ExceptionFactory.makeFault(DepartmentExceptionCode.DEPARTMENT_ID_EMPTY, new Object[] { null });
		}

		if (roleDepartment.getRoleId() == null) {
			logger.error("verifyAddGrantForm error, roleId : {}", roleDepartment.getRoleId());
			throw ExceptionFactory.makeFault(RoleExceptionCode.ROLE_ID_EMPTY, new Object[] { null });
		}

	}

	private void verifyUpdateGrantForm(RoleDepartment roleDepartment) {
		if (roleDepartment.getId() == null) {
			logger.error("verifyAddGrantForm error, id : {}", roleDepartment.getId());
			throw ExceptionFactory.makeFault(RoleDepartmentExceptionCode.ROLE_DEPARTMENT_ID_EMPTY,
					new Object[] { null });
		}
	}

}
