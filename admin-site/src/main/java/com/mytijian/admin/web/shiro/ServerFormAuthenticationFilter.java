/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.web.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.util.StringUtils;

/**
 * 类ServerFormAuthenticationFilter.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年8月17日 下午7:11:52
 */
public class ServerFormAuthenticationFilter extends FormAuthenticationFilter {

    protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
        String fallbackUrl = (String) getSubject(request, response)
                .getSession().getAttribute("authc.fallbackUrl");
        if(StringUtils.isEmpty(fallbackUrl)) {
            fallbackUrl = getSuccessUrl();
        }
       // WebUtils.redirectToSavedRequest(request, response, fallbackUrl);
    }
    
    
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        
    }

}