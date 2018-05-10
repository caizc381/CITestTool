package com.citest.tool.service.impl;

import javax.annotation.Resource;

import com.citest.tool.exceptions.LoginFailedException;
import com.citest.tool.model.User;
import com.citest.tool.service.LoginService;
import com.citest.tool.service.UserService;

public class LoginServiceImpl implements LoginService{
	
	@Resource(name="userService")
	private UserService userService;
	

	@Override
	public String login(String loginName, String password) {
		User user = userService.getUserInfo(loginName);
		validUser(user);
		return null;
	}
	
	private void validUser(User user) throws LoginFailedException {
		if (user == null) {
			throw new LoginFailedException(LoginFailedException.USERNAME_NOT_FOUND,"用户名不存在，请重新输入");
		}
	}

}
