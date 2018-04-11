package com.mytijian.admin.api.common.model;

import java.io.Serializable;

public class OpsLog implements Serializable {
	
	private static final long serialVersionUID = 2607456105235900281L;

	/**
	 * 操作者Id
	 */
	private int employeeId;
	
	/**
	 * 操作菜单
	 */
	private String action;
	
	/**
	 * 参数
	 */
	private String parameters;
	
	/**
	 * ip
	 */
	private String ip;

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
