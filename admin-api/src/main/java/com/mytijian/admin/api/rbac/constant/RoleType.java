package com.mytijian.admin.api.rbac.constant;

public enum RoleType {

	SUPER_ADMIN("超级管理员"),ADMIN("管理员"),COMMON("普通成员");
	private String text;

    RoleType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
