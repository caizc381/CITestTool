package com.citest.tool.service.impl;


import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citest.tool.dao.UserDao;
import com.citest.tool.service.UserService;

//@Service(value="userService")
@Service
public class UserServiceImpl implements UserService{
	
	//@Resource(name = "userMapper")
	@Autowired
	private UserDao userDao;

	@Override
	public String getUserInfo(String username, String password) {
		userDao.getUserInfo(username, password);
		return null;
	}

	@Override
	public Object getList(Map<String, Object> map) {
		return userDao.getList(map);
	}

}
