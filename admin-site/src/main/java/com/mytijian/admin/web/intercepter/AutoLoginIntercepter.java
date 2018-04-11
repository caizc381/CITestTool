package com.mytijian.admin.web.intercepter;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mytijian.admin.web.util.LoginUtil;
import com.mytijian.util.CookiesUtils;

public class AutoLoginIntercepter extends AbstractHandlerInterceptorAdapter {

	private static final String REMEMBERME_TOKEN_NAME = "rememberme_token";
	public static final String EMPLOYEE_TOKEN = "employee_token";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler)
			throws Exception {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return true;
		}

		String rememberMeToken = null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(REMEMBERME_TOKEN_NAME)) {
				rememberMeToken = cookie.getValue();
				break;
			}
		}

		RequestMapping mapping = handler.getMethod().getAnnotation(RequestMapping.class);
		if (mapping != null && "/login".equals(mapping.value()[0])) {
			CookiesUtils.removeCookie(request, response, REMEMBERME_TOKEN_NAME);
			rememberMeToken = null;
		}

		if (rememberMeToken != null && request.getSession().getAttribute(EMPLOYEE_TOKEN) == null) {
			// TODO 暂无记住我
			String token = UUID.randomUUID().toString().replaceAll("-", "");
			if (token == null) {
				CookiesUtils.removeCookie(request, response, REMEMBERME_TOKEN_NAME);
			} else {
				request.getSession().setAttribute(EMPLOYEE_TOKEN, token);
				LoginUtil.addUniqueSubmitToken(response, request);
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		super.postHandle(request, response, handler, modelAndView);
	}

	@SuppressWarnings("unused")
	private void setCookie(HttpServletResponse response, String tokenName, String tokenValue) {
		Cookie cookie = new Cookie(tokenName, tokenValue);
		cookie.setPath("/");
		cookie.setMaxAge(30 * 24 * 3600);
		response.addCookie(cookie);
	}

}
