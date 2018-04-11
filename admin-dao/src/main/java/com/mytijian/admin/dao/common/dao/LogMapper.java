package com.mytijian.admin.dao.common.dao;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.common.dataobject.LogDO;


@Repository("logMapper")
public interface LogMapper extends BaseMapper<LogDO> {
	
}
