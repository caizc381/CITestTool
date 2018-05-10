package com.citest.tool.service;

public interface LoginService {

	public static final String TOKEN_NAME = "user_token";
	public static final String TOKEN_NAME_NOLOGIN = "user_nologin_token";
	public static final String ACCOUNT_ROLE_IDS = "account_role_idS";
	public static final String ACCOUNT_FUNCTION_IDS="account_function_ids";
	public static final String ACCOUNT_FUNCTION_CODES="account_function_codes";
	public static final String ACCOUNT_ROLES="account_roles";
	public static final String ACCOUNT_FUNCTIONS = "account_functions";
	
	String login(String loginName,String password);
}
