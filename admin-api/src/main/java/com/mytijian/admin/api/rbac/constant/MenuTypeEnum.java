package com.mytijian.admin.api.rbac.constant;


public enum MenuTypeEnum {
	
    /**
     * 目录
     */
	CATALOG(1, "catalog"),
	
    /**
     * 菜单
     */
    MENU(2, "menu"),
    
    /**
     * 按钮
     */
    BUTTON(3, "button");

    private int code;
	private String message;
	
	private MenuTypeEnum(int code, String message) {
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
