package com.mytijian.admin.web.intercepter;

import java.util.StringTokenizer;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import com.mytijian.admin.web.exception.CommonConstants;
import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.mediator.user.constans.UserExceptionCode;

/**
 * 
 * 安全拦截器, API的ACL控制器, 它负责拦截非法的访问
 */
public class SecurityInterceptor implements MethodInterceptor {

	private String debarApis = "";

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Session session = SecurityUtils.getSubject().getSession();
		StringTokenizer stringTokenizer = new StringTokenizer(debarApis,",");
        while (stringTokenizer.hasMoreTokens()) {
            String value = stringTokenizer.nextToken();
            if(value.trim().equals(methodInvocation.getMethod().getName())) {
                //skip掉不应该安全防护的API
                return methodInvocation.proceed();
            }
        }
		
		if (session.getAttribute(CommonConstants.EMPLOYEE_TOKEN) == null) {
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_NOT_LOGINED, new Object[] { null });
		}
		return methodInvocation.proceed();
	}

	public void setDebarApis(String debarApis) {
		this.debarApis = debarApis;
	}

}
