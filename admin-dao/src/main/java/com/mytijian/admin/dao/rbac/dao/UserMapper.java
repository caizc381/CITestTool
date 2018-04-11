package com.mytijian.admin.dao.rbac.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.base.mapper.BaseMapper;
import com.mytijian.admin.dao.rbac.dataobject.UserDO;

@Repository("userMapper")
public interface UserMapper extends BaseMapper<UserDO> {

	/**
	 * 获取用户信息
	 * @param params
	 * @return
	 */
	public UserDO selectUserInfo(Map<String, Object> params);
	
}
