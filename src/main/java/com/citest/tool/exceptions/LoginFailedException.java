package com.citest.tool.exceptions;

import com.mytijian.exception.LogLevel;

public class LoginFailedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1956864418345984844L;

	public static int USERNAME_NOT_FOUND = 1;
	public static int PASSWORD_ERROR=2;
	public static int USERNAME_IS_EMPTY=3;
	public static int PASSWORD_IS_EMPTY=4;
	public static int VALIDATION_CODE_IS_EMPTY=5;
	public static int VALIDATION_CODE_ERROR=6;
	public static int NOT_AUTHORIZED=7;
	public static int CRM_USER_FIRST_LOGIN_MODIFY_PASSWORD=8;
	public static int LOGIN_REQUIRED=9;
	public static int NO_ORDER_HAS_CARD=10;
	public static int HAS_ORDER=11;
	public static int NEED_UPDATE_PASSWORD=12;
	public static int WITHOUT_COMPLETE_INFOMATION=13;
	
	public static int MOBILE_FORMAT_ERROR=14;
	public static int MOBILE_NOT_BINDING=15;
	public static int ACCOUNT_ID_DISABLE=16;
	public static int MOBILE_NO_VERIFIED=17;
	
	public static int INVALID_ROLE=999999998;
	public static int INVALID_FUNC=999999999;
	
	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public LogLevel loglevel() {
		return LogLevel.warn;
	}
	
	public LoginFailedException(int code) {
		this.code = code;
	}
	
	public LoginFailedException(String message) {
		super(message);
	}
	
	public LoginFailedException(int code,String message) {
		super(message);
		this.code = code;
	}
}
