package com.citest.tool.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.citest.tool.service.UserService;
import com.citest.tool.util.CommonUtil;

//@RestController
@Controller
@RequestMapping(value="/user")
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	//@Resource(name = "userService")
	@Autowired
	private UserService userService;
	
	
	
//	@ResponseBody
//	@RequestMapping(value = "/login",method = RequestMethod.POST)
//	public void isLogin(String username,String password,HttpSession session,HttpServletRequest request,HttpServletResponse response) {
//		userService.getUserInfo(username, password);
//	}
	
	/***
     * api :localhost:8099/users?id=99
     *  http://localhost:8099/users?limit=2&page=2
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> list(HttpServletRequest request) {
        Map<String, Object> map = CommonUtil.getParameterMap(request);
        return new ResponseEntity<>(userService.getList(map), HttpStatus.OK);
    }


}
