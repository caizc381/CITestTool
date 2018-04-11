package com.mytijian.admin.web.intercepter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.ShiroHttpServletResponse;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.fastjson.JSON;
import com.mytijian.admin.api.common.model.OpsLog;
import com.mytijian.admin.api.common.service.LogService;
import com.mytijian.admin.web.util.ShiroUtils;

public class LogIntercepter extends LogIntercepterBase {

	@Resource(name = "logService")
	private LogService logService;

	@Override
	protected OpsLog buildExecutedAfterUserLogItem(MethodInvocation methodInvocation, String platform, String desc,
			String who, String packname, String refer, String sessionId) {
		StringBuffer args = getArgsLog(methodInvocation);
		OpsLog log = buildItem(methodInvocation, platform, desc, who, packname, "", "done", args.toString(), refer,
				sessionId);
		logService.addLog(log);
		return log;
	}

	private OpsLog buildItem(MethodInvocation methodInvocation, String platform, String desc, String who,
			String packname, String excep, String result, String args, String refer, String sessionId) {
		// String uuid= (String) SecurityUtils.getSubject().getSession().getAttribute("UUID");
		OpsLog log = new OpsLog();
		log.setAction(methodInvocation.getMethod().getName());
		log.setEmployeeId(ShiroUtils.getUserId());
		log.setParameters(args);
		log.setIp(getIpAddr());
		return log;
	}

	private StringBuffer getArgsLog(MethodInvocation methodInvocation) {
		StringBuffer args = new StringBuffer();
		if (methodInvocation.getArguments() != null && methodInvocation.getArguments().length > 0) {
			for (Object arg : methodInvocation.getArguments()) {
				if (arg instanceof org.apache.catalina.connector.ResponseFacade) {
					continue;
				}

				if (arg instanceof ShiroHttpServletRequest) {
					continue;
				}

				if (arg instanceof org.apache.catalina.session.StandardSessionFacade) {
					continue;
				}
				if (arg instanceof ShiroHttpServletResponse){
					continue;
				}

				if (arg instanceof ShiroHttpSession){
					continue;
				}
				if(arg instanceof org.springframework.web.multipart.MultipartFile){
					continue;
				}
				if (arg instanceof WebStatFilter.StatHttpServletResponseWrapper){
					continue;
				}
				
				if(arg instanceof StandardMultipartHttpServletRequest){
					continue;
				}
				try {
					if (arg != null) {
						try{
							arg = JSON.toJSONString(arg);
						}catch (Exception ex){
							// do nothing
						}
					}
					args.append(arg).append("|");
				} catch (Exception e) {
					// do nothing
				}
			}
		}
		return args;
	}

	// HttpServletRequest request
	private String getIpAddr() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}
}
