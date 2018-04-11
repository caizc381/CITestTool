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

import com.mytijian.admin.api.rbac.model.Department;
import com.mytijian.admin.api.rbac.service.DepartmentService;
import com.mytijian.admin.dao.base.dataobject.PageUtils;
import com.mytijian.admin.web.util.CommonUtil;
import com.mytijian.admin.web.util.Page;
import com.mytijian.mediator.department.constans.DepartmentExceptionCode;
import com.mytijian.mediator.exceptions.ExceptionFactory;

/**
 * 
 * 类DepartmentController.java的实现描述：部门相关
 * @author zhanfei.feng 2017年4月7日 下午3:56:32
 */
@RestController
@RequestMapping("/sys/department")
public class DepartmentController {

	private final static Logger logger = LoggerFactory.getLogger(DepartmentController.class);

	@Resource(name = "departmentService")
	private DepartmentService departmentService;

	/**
	 * 分页获取部门列表
	 * @param departmentName 部门名称
	 * @param currPage 当前页
	 * @param pageSize 每页显示数量
	 * @return
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:department:list")
	public PageUtils list(String departmentName, Integer currPage, Integer pageSize) {

		Page page = CommonUtil.getPage(currPage, pageSize);
		int offset = page.getOffset();
		pageSize = page.getPageSize();
		currPage = page.getCurrPage();
		// 查询列表数据
		List<Department> departments = departmentService.listDepartments(departmentName, offset, pageSize);
		int total = departmentService.countTotal(departmentName);
		return new PageUtils(departments, total, pageSize, currPage);
	}

	/**
	 * 所有部门列表
	 */
	@RequestMapping("/treeList")
	@RequiresPermissions("sys:department:list")
	public List<Department> treeList() {
		return departmentService.listDepartmentTree();
	}

	/**
	 * 保存 部门
	 * @param department
	 * @return
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:department:save")
	public boolean save(@RequestBody Department department) {
		// 数据校验
		verifyForm(department);

		departmentService.saveDepartment(department);

		return true;
	}
	
	/**
	 * 根据部门Id获取部门信息
	 * @param departmentId 部门id
	 * @return
	 */
	@RequestMapping("/info/{departmentId}")
	@RequiresPermissions("sys:department:info")
	public Department info(@PathVariable("departmentId") Integer departmentId) {
		return departmentService.getWithParentIdById(departmentId);
	}

	/**
	 * 修改部门
	 * @param department
	 * @return
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:department:update")
	public boolean update(@RequestBody Department department) {
		// 数据校验
		verifyUpdateForm(department);

		departmentService.updateDepartment(department);

		return true;
	}

	/**
	 * 根据部门id删除部门
	 * @param departmentIds
	 * @return
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:department:delete")
	public boolean delete(@RequestParam(value = "departmentIds") List<Integer> departmentIds) {
		departmentService.deleteBatch(departmentIds);
		return true;
	}

	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(Department department) {
		if (StringUtils.isEmpty(department.getDepartmentName())) {
			logger.error(DepartmentExceptionCode.DEPARTMENT_NAME_EMPTY + "departmentName : {}",
					department.getDepartmentName());
			throw ExceptionFactory.makeFault(DepartmentExceptionCode.DEPARTMENT_NAME_EMPTY, new Object[] { null });

		}
	}

	/**
	 * 验证参数是否正确
	 */
	private void verifyUpdateForm(Department department) {
		if (StringUtils.isEmpty(department.getId())) {
			logger.error(DepartmentExceptionCode.DEPARTMENT_ID_EMPTY + "departmentId : {}", department.getId());
			throw ExceptionFactory.makeFault(DepartmentExceptionCode.DEPARTMENT_ID_EMPTY, new Object[] { null });
		}
	}

}
