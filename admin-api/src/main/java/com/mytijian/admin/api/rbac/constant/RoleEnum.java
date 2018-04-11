package com.mytijian.admin.api.rbac.constant;

public enum RoleEnum {
	
	NORMAL(1, "正常"),
	INVALID(2, "无效");
	
	private int code;
	private String message;
	
	private RoleEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
