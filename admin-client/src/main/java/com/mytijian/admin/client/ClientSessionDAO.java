/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.client;

import java.io.Serializable;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;

import com.mytijian.admin.shiro.remote.RemoteServiceInterface;
import com.mytijian.admin.shiro.util.SerializableUtils;

/**
 * 类ClientSessionDAO.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年8月17日 下午3:09:38
 */
public class ClientSessionDAO extends CachingSessionDAO {

    private RemoteServiceInterface remoteService;
    private String appKey;

    public void setRemoteService(RemoteServiceInterface remoteService) {
        this.remoteService = remoteService;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }


    @Override
    protected void doDelete(Session session) {
    	String sessionStr = SerializableUtils.serialize(session);
        remoteService.deleteSession(appKey, sessionStr);
    }

    @Override
    protected void doUpdate(Session session) {
    	String sessionStr = SerializableUtils.serialize(session);
        remoteService.updateSession(appKey, sessionStr);
    }


    @Override
    protected Serializable doCreate(Session session) {
    	String sessionStr = SerializableUtils.serialize(session);
        Serializable sessionId = remoteService.createSession(sessionStr);
        assignSessionId(session, sessionId);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
    	String sessionStr = remoteService.getSession(appKey, (String)sessionId);
        return SerializableUtils.deserialize(sessionStr);
    }
}