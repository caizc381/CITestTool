package com.mytijian.admin.web.controller;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HealthChecker {

	@RequestMapping(value = "/isOK", method = RequestMethod.GET)
	public void isOk(HttpSession session,HttpServletResponse response) throws Exception {
		OutputStream out = response.getOutputStream();
		try {
			out.write("OK".getBytes());
		}catch(Exception e){
			
		}finally {
			 out.close();
		}
		
	}
}
