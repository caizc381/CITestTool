package com.mytijian.admin.web.vo.resource;

import java.io.Serializable;

public class OrganizationVO implements Serializable {

	/**
	 * 机构ID
	 */
	private Integer id;

	/**
	 * 机构名称
	 */
	private String name;

	/**
	 * 机构类型
	 */
	private Integer orgType;

	/**
	 * 机构简拼
	 */
	private String pinyin;
	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrgType() {
		return orgType;
	}

	public void setOrgType(Integer orgType) {
		this.orgType = orgType;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
}
