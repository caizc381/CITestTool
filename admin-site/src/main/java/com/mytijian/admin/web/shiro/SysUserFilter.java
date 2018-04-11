/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.web.shiro;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;

import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.api.rbac.service.EmployeeService;


/**
 * 类SysUserFilter.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年8月17日 下午7:12:53
 */
public class SysUserFilter extends PathMatchingFilter {

	@Resource(name = "employeeService")
	private EmployeeService employeeService;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        String username = ((Employee)(SecurityUtils.getSubject().getPrincipal())).getLoginName();
        Employee employee = employeeService.getEmployeeInfo(null, null, username);
        request.setAttribute(Constants.CURRENT_USER, employee);
        return true;
    }
}