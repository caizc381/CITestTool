package com.mytijian.admin.web.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AbstractHandlerInterceptorAdapter extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler instanceof HandlerMethod) {
			return preHandle(request, response, (HandlerMethod) handler);
		} else {
			return super.preHandle(request, response, handler);
		}
	}

	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler)
			throws Exception {
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (handler instanceof HandlerMethod) {
			postHandle(request, response, (HandlerMethod) handler, modelAndView);
		} else {
			super.postHandle(request, response, handler, modelAndView);
		}
	}

	protected void postHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (handler instanceof HandlerMethod) {
			afterCompletion(request, response, (HandlerMethod) handler, ex);
		} else {
			super.afterCompletion(request, response, handler, ex);
		}
	}

	protected void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler,
			Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}

}
