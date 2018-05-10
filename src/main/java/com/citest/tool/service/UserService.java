package com.citest.tool.service;

import java.util.Map;

import com.citest.tool.model.User;

public interface UserService {

	User getUserInfo(String username);
	
	Object getList(Map<String, Object> map);
}
