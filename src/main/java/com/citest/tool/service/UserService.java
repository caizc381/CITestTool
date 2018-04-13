package com.citest.tool.service;

import java.util.Map;

public interface UserService {

	public String getUserInfo(String username,String password);
	
	Object getList(Map<String, Object> map);
}
