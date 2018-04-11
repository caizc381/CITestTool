package com.mytijian.admin.api.rbac.constant;

public enum EmployeeStatusEnum{
	
	NORMAL(1, "正常"),
	FROZEN(2, "冻结");
	
	private int code;
	private String message;
	
	private EmployeeStatusEnum(int code, String message) {
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
