package com.citest.tool.mapper;

import java.util.List;
import java.util.Map;

import com.citest.tool.model.User;

public interface UserMapper {

	User getUserInfo(String username);
	List<User> getList(Map<String,Object> map);
}
