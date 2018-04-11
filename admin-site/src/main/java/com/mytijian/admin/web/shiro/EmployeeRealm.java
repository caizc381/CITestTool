package com.mytijian.admin.web.shiro;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.api.rbac.model.Menu;
import com.mytijian.admin.api.rbac.service.EmployeeService;
import com.mytijian.admin.api.rbac.service.MenuService;
import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.mediator.user.constans.UserExceptionCode;

/**
 * 
 * 类EmployeeRealm.java的实现描述：认证
 * @author zhanfei.feng 2017年4月7日 下午4:07:11
 */
public class EmployeeRealm extends AuthorizingRealm {

	private final static Logger logger = LoggerFactory.getLogger(EmployeeRealm.class);

	@Resource(name = "employeeService")
	private EmployeeService employeeService;

	@Resource(name = "menuService")
	private MenuService menuService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Employee employee = (Employee) principals.getPrimaryPrincipal();

		Integer employeeId = employee.getId();

		// 用户权限列表
		Set<String> permsSet = new HashSet<String>();
		// 获取用户权限列表
		/*List<Menu> employeeResources = employeeService.getEmployeeResources(employeeId);
		if (!CollectionUtils.isEmpty(employeeResources)) {
			List<String> permsList = Lists.newArrayList();
			employeeResources.forEach(employeeResource -> {
				String perms = employeeResource.getPerms();
				if (StringUtils.isEmpty(perms)) {
					permsList.add(perms);
				}
			});
			
			for(String perms : permsList){
				if(StringUtils.isEmpty(perms)){
					continue;
				}
				permsSet.addAll(Arrays.asList(perms.trim().split(",")));
			}
		}*/

		List<String> permsList = null;
		// 系统管理员，拥有最高权限
		if (employeeId == 1) {
			List<Menu> menuList = menuService.listMenus(new HashMap<String, Object>());
			permsList = new ArrayList<>(menuList.size());
			for (Menu menu : menuList) {
				permsList.add(menu.getPerms());
			}
		} else {
			permsList = employeeService.listPermsByemployeeId(employeeId);
		}

		// 用户权限列表
		for (String perms : permsList) {
			if (StringUtils.isEmpty(perms)) {
				continue;
			}
			permsSet.addAll(Arrays.asList(perms.trim().split(",")));
		}

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setStringPermissions(permsSet);
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

		String loginName = (String) token.getPrincipal();
		String password = new String((char[]) token.getCredentials());

		// 查询用户信息
		Employee employee = employeeService.getEmployeeInfo(null, null, loginName);

		// 账号不存在
		if (employee == null) {
			logger.error(UserExceptionCode.USER_NOT_EXIST + ", loginName : {}", loginName);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_NOT_EXIST, new Object[] { null });
		}

		// 密码错误
		if (!checkUserPwd(password, employee.getPassword(), employee.getSalt())) {
			logger.error(UserExceptionCode.USER_PASSWORD_INCORRECT + ", password : {}", password);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_PASSWORD_INCORRECT, new Object[] { null });
		}

		// 账号锁定
		if (employee.getStatus() == 0) {
			logger.error(UserExceptionCode.USER_FROZED + ", loginName : {}", loginName);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_FROZED, new Object[] { null });
		}

		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(employee, password, getName());
		return info;
	}

	private boolean checkUserPwd(String pwd, String realPwd, String salt) {
		if (!StringUtils.isEmpty(salt)) {
			pwd = salt + pwd;
		}
		return realPwd.equals(MD5(pwd));
	}

	public static String MD5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(str.getBytes("UTF-8"));
			byte[] byteArray = md.digest();
			StringBuffer md5StrBuff = new StringBuffer();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
				} else {
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
				}
			}
			return md5StrBuff.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
