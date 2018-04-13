package com.citest.tool.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.citest.tool.bean.User;

//@Repository("userMapper")
//@Mapper
public interface UserDao {

	public String getUserInfo(String username, String password);
	List<User> getList(Map<String,Object> map);
}
