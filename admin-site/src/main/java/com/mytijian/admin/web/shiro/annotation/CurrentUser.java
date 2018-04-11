/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.web.shiro.annotation;

import java.lang.annotation.*;

import com.mytijian.admin.web.shiro.Constants;

/**
 * <p>绑定当前登录的用户</p>
 * <p>不同于@ModelAttribute</p>
 * @author liangxing 2017年8月17日 下午7:55:11
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {

    /**
     * 当前用户在request中的名字
     *
     * @return
     */
    String value() default Constants.CURRENT_USER;

}