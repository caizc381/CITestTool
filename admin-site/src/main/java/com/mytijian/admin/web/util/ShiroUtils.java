package com.mytijian.admin.web.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.mytijian.admin.api.rbac.model.Employee;


/**
 * Shiro工具类
 * 
 * @author feng
 * @email 373680866@qq.com
 * @date 2017年02月27日 上午10:49:19
 */
public class ShiroUtils {

	public static Session getSession() {
		return SecurityUtils.getSubject().getSession();
	}

	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	public static Employee getEmployeeEntity() {
		return (Employee) SecurityUtils.getSubject().getPrincipal();
	}

	public static Integer getUserId() {
		if (getEmployeeEntity() != null) {
			return getEmployeeEntity().getId();
		}
		return 0;
	}

	public static void setSessionAttribute(Object key, Object value) {
		getSession().setAttribute(key, value);
	}

	public static Object getSessionAttribute(Object key) {
		return getSession().getAttribute(key);
	}

	public static boolean isLogin() {
		return SecurityUtils.getSubject().getPrincipal() != null;
	}

	public static void logout() {
		SecurityUtils.getSubject().logout();
	}

	public static String getKaptcha(String key) {
		String kaptcha = getSessionAttribute(key).toString();
		getSession().removeAttribute(key);
		return kaptcha;
	}

}

