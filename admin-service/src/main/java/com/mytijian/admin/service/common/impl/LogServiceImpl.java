package com.mytijian.admin.service.common.impl;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.mytijian.admin.api.common.model.OpsLog;
import com.mytijian.admin.api.common.service.LogService;
import com.mytijian.admin.dao.common.dao.LogMapper;
import com.mytijian.admin.dao.common.dataobject.LogDO;

@Service("logService")
public class LogServiceImpl implements LogService {
	
	@Resource(name="logMapper")
	private LogMapper logMapper;
	
	@Override
	public void addLog(OpsLog log) {
		logMapper.insert(logToLogDO(log));
	}
	
	private LogDO logToLogDO(OpsLog log) {
		LogDO logDO = null;
		if (log != null) {
			logDO = new LogDO();
			BeanUtils.copyProperties(log, logDO);
		}
		return logDO;
	}
	
}
