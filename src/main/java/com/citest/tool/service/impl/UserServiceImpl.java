package com.citest.tool.service.impl;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citest.tool.mapper.UserMapper;
import com.citest.tool.model.User;
import com.citest.tool.service.UserService;

//@Service(value="userService")
@Service
public class UserServiceImpl implements UserService{
	
	//@Resource(name = "userMapper")
	@Autowired
	private UserMapper userMapper;

	@Override
	public User getUserInfo(String username) {
		User user = userMapper.getUserInfo(username);
		return user;
	}

	@Override
	public Object getList(Map<String, Object> map) {
		return userMapper.getList(map);
	}

}
