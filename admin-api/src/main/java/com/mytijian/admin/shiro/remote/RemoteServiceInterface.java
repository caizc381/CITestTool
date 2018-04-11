/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.shiro.remote;

import java.io.Serializable;

/**
 * 类RemoteServiceInterface.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年8月17日 下午2:57:14
 */
public interface RemoteServiceInterface {

    public String getSession(String appKey, String sessionId);
    Serializable createSession(String session);
    public void updateSession(String appKey, String session);
    public void deleteSession(String appKey, String session);

    public PermissionContext getPermissions(String appKey, String username);
}