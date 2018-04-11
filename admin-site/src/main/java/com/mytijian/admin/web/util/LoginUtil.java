package com.mytijian.admin.web.util;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mytijian.admin.web.exception.CommonConstants;
import com.mytijian.cache.RedisCacheClient;
import com.mytijian.cache.annotation.RedisClient;
import com.mytijian.util.AssertUtil;

/**
 * 
 * @author mytijian
 *
 */
@Component("loginUtil")
public class LoginUtil {

	private static final Logger logger = LoggerFactory.getLogger(LoginUtil.class);
	
	private static RedisCacheClient<String> webAdminTokenCache;

	@RedisClient(nameSpace="webAdmin:token", timeout = 60 * 60 * 12)
	public void setWebAppTokenCache(RedisCacheClient<String> ssoTokenCache) {
		LoginUtil.webAdminTokenCache = ssoTokenCache;
	}
	
	public static void addUniqueSubmitToken(HttpServletResponse response, HttpServletRequest request) {
		String token=request.getHeader(CommonConstants.UNIQUE_SUBMIT_TOKEN);
		if (AssertUtil.isNotEmpty(token)) {
			token =  webAdminTokenCache.get(token);
		}
		if (AssertUtil.isEmpty(token)) {
			StringBuilder uuid = new StringBuilder(60);
			uuid.append(UUID.randomUUID().toString()).append("_").append(System.currentTimeMillis());
			String uuidStr = uuid.toString();
			webAdminTokenCache.put(uuidStr, uuidStr);
			response.setHeader(CommonConstants.UNIQUE_SUBMIT_TOKEN, uuidStr); 
			response.setHeader("Access-Control-Expose-Headers", CommonConstants.UNIQUE_SUBMIT_TOKEN);
			logger.info("写入token[{}]到前端", uuidStr);
		}
	}
	
	public static void removeCookie(HttpServletRequest req, HttpServletResponse resp) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				cookie.setValue("");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				resp.addCookie(cookie);
			}
		}
	}
	
}
