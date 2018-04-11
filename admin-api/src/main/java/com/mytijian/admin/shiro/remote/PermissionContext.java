/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.shiro.remote;

import java.io.Serializable;
import java.util.Set;

/**
 * 类PermissionContext.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年8月17日 下午2:56:11
 */
public class PermissionContext implements Serializable {

	private static final long serialVersionUID = 6098825164782284821L;
	private Set<String> roles;
    private Set<String> permissions;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }


    @Override
    public String toString() {
        return "PermissionContext{" +
                ", roles=" + roles +
                ", permissions=" + permissions +
                '}';
    }
}
