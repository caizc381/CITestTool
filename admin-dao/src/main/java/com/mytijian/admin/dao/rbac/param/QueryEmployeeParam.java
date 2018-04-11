package com.mytijian.admin.dao.rbac.param;

import java.io.Serializable;

public class QueryEmployeeParam implements Serializable {
	
	private static final long serialVersionUID = 4880409289616127186L;

	private String loginName;
	
	private Integer offset;
	
	private Integer limit;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public QueryEmployeeParam(String loginName, Integer offset, Integer limit) {
		super();
		this.loginName = loginName;
		this.offset = offset;
		this.limit = limit;
	}
	
}
