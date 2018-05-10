package com.citest.tool.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebFilter(filterName="corsFilter",urlPatterns="/*")
public class CITestToolCORSFilter implements Filter{

	private Logger logger = LoggerFactory.getLogger(CITestToolCORSFilter.class);
	
	public CITestToolCORSFilter() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request =(HttpServletRequest)req;
		HttpServletResponse response=(HttpServletResponse)resp;
		
		String domain = request.getHeader("origin");
		response.setHeader("Access-Control-Allow-Origin", domain);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST,PUT,GET,OPTIONS,DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Cache-Control", "public,max-age=-0.001");;
		response.setHeader("Access-Control-Allow-Header","Origin, X-Requested-With, Content-Type, Accept, unique-submit-token");
		response.setHeader("Access-Control-Expose-Headers", "unique-submit-token");
		chain.doFilter(req, response);
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
