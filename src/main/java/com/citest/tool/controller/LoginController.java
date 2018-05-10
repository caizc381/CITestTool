package com.citest.tool.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citest.tool.exceptions.LoginFailedException;
import com.citest.tool.service.UserService;

//@RestController
@Controller
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	//@Resource(name = "userService")
	@Autowired
	private UserService userService;
	
	
	
	@ResponseBody
	@RequestMapping(value = "/login",method = RequestMethod.POST)
	public void login(String username,String password,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws LoginFailedException {
		if (StringUtils.isBlank(username)) {
			throw new LoginFailedException(LoginFailedException.USERNAME_IS_EMPTY, "请输入用户名");
		}
		
		if (StringUtils.isBlank(password)) {
			throw new LoginFailedException(LoginFailedException.PASSWORD_IS_EMPTY, "请输入密码");
		}
		userService.getUserInfo(username, password);
	}
	
//	/***
//     * api :localhost:8099/users?id=99
//     *  http://localhost:8099/users?limit=2&page=2
//     * @param request
//     * @return
//     */
//    @RequestMapping(method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
//    @ResponseBody
//    public ResponseEntity<Object> list(HttpServletRequest request) {
//        Map<String, Object> map = CommonUtil.getParameterMap(request);
//        return new ResponseEntity<>(userService.getList(map), HttpStatus.OK);
//    }


}
