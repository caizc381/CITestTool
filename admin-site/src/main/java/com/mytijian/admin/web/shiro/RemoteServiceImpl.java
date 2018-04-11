/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.web.shiro;

import java.io.Serializable;

import javax.annotation.Resource;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.mytijian.admin.shiro.remote.PermissionContext;
import com.mytijian.admin.shiro.remote.RemoteServiceInterface;
import com.mytijian.admin.shiro.util.SerializableUtils;

/**
 * 类RemoteServiceImpl.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年8月23日 下午3:24:04
 */
public class RemoteServiceImpl implements RemoteServiceInterface {

    @Resource(name="sessionDAO")
    private SessionDAO sessionDAO;

    @Override
    public String getSession(String appKey, String sessionId) {
    	Session session = sessionDAO.readSession(sessionId);
        return SerializableUtils.serialize(session);
    }

    @Override
    public Serializable createSession(String session) {
    	Session se = SerializableUtils.deserialize(session);
        return sessionDAO.create(se);
    }

    @Override
    public void updateSession(String appKey, String session) {
    	Session se = SerializableUtils.deserialize(session);
        sessionDAO.update(se);
    }

    @Override
    public void deleteSession(String appKey, String session) {
    	Session se = SerializableUtils.deserialize(session);
        sessionDAO.delete(se);
    }

    @Override
    public PermissionContext getPermissions(String appKey, String username) {
        PermissionContext permissionContext = new PermissionContext();
        return permissionContext;
    }
}
