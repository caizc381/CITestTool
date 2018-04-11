package com.mytijian.admin.web.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author linzhihao
 */
@Component("wxProps")
public class WxProps {
	
	@Value("${mobileSiteBasePath}")
	private String mobileSiteBasePath;

	public String getMobileSiteBasePath() {
		return mobileSiteBasePath;
	}
	
}
