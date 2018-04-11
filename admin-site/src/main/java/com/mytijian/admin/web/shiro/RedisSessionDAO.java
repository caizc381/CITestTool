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
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;

import com.mytijian.admin.shiro.util.SerializableUtils;
import com.mytijian.cache.RedisCacheClient;

/**
 * 类RedisCacheManager.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年8月17日 下午4:21:03
 */
public class RedisSessionDAO extends CachingSessionDAO {
	
	@Resource(name="shiroSessionCache")
	private RedisCacheClient<Serializable> shiroSessionCache;

	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        shiroSessionCache.put((String) sessionId, SerializableUtils.serialize(session));
        return session.getId();
	}
	
	@Override
	protected void doUpdate(Session session) {
		if(session instanceof ValidatingSession && !((ValidatingSession)session).isValid()) {
            return; //如果会话过期/停止 没必要再更新了
        }
		shiroSessionCache.put((String) session.getId(), SerializableUtils.serialize(session));
	}

	@Override
	protected void doDelete(Session session) {
		shiroSessionCache.remove((String) session.getId());
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		String sessionStr = (String) shiroSessionCache.get((String) sessionId);
		if(sessionStr == null ){
			return null;
		}
	    return SerializableUtils.deserialize(sessionStr);
	}
	
}
