package com.mytijian.admin.web.intercepter;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import com.mytijian.admin.web.exception.CommonConstants;
import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.mediator.user.constans.UserExceptionCode;

public class LoginStatusInterceptor extends AbstractHandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler)
			throws Exception {

		if (isIgnoreUrls(this.ignoreUrls, request.getRequestURI())) {
			return super.preHandle(request, response, handler);
		}

		String token = (String) request.getSession().getAttribute(CommonConstants.EMPLOYEE_TOKEN);
		if (StringUtils.isEmpty(token) && !isIgnoreUrls(this.ignoreUrls, request.getRequestURI())) {
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_NOT_LOGINED, new Object[] { null });
		}

		return super.preHandle(request, response, handler);
	}

	private boolean isIgnoreUrls(List<String> ignoreUrls, String requestURI) {
		for (String ignoreUrl : ignoreUrls) {
			if (!StringUtils.isEmpty(requestURI) && requestURI.contains(ignoreUrl)) {
				return true;
			}
		}
		return false;
	}

	private List<String> ignoreUrls;

	public void setIgnoreUrls(List<String> ignoreUrls) {
		this.ignoreUrls = ignoreUrls;
	}

}
