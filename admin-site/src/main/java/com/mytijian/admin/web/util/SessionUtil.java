package com.mytijian.admin.web.util;

import com.mytijian.admin.api.rbac.model.Employee;

/**
 * 
 * 类SessionUtil.java的实现描述：session 工具类
 * @author zhanfei.feng 2017年4月7日 下午4:08:08
 */
public class SessionUtil {

	/**
	 * 获取职工信息
	 * @return
	 */
	public static Employee getEmployee() {
		return ShiroUtils.getEmployeeEntity();
	}

	/**
	 * 获取职工Id
	 * @return
	 */
	public static Integer getEmployeeId() {
		return getEmployee().getId();
	}
}