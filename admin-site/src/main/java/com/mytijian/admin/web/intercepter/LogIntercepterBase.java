package com.mytijian.admin.web.intercepter;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;

import com.mytijian.admin.api.common.model.OpsLog;

public abstract class LogIntercepterBase implements MethodInterceptor {

	private boolean logClose = false;

	public void setLogClose(boolean logClose) {
		this.logClose = logClose;
	}

	/**
	 * 这个logger是程序员开发时从console看信息用的, 框架本身并不存储日志,所有日志都推送到阿里日志服务存储
	 */
	private Logger logger = Logger.getLogger(LogIntercepterBase.class);

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		if (this.logClose) {
			return methodInvocation.proceed();
		}
		Method method = methodInvocation.getMethod();
		String packName = method.getDeclaringClass().getPackage().getName();
		String who = null;
		Object result = null;
		String sessionId = SecurityUtils.getSubject().getSession().getId().toString();
		try {
			result = methodInvocation.proceed();
			final OpsLog afterUserLogItem = buildExecutedAfterUserLogItem(methodInvocation, "platform(ops)", "desc",
					who, packName, "refer(web)", sessionId);
			logger.info(afterUserLogItem.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * 构建在执行方法后的用户日志
	 * @param methodInvocation
	 * @param platform
	 * @param desc
	 * @param who
	 * @param packname
	 * @param refer
	 * @param sessionId
	 * @return
	 */
	protected abstract OpsLog buildExecutedAfterUserLogItem(MethodInvocation methodInvocation, String platform,
			String desc, String who, String packname, String refer, String sessionId);


}
