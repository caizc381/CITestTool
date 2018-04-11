package com.mytijian.admin.api.address.model;

import java.io.Serializable;

public class City implements Serializable {

	private static final long serialVersionUID = 5102101401070944066L;

	/**
	 * 
	 */
	private Integer id;
	
	/**
	 * 城市Id
	 */
	private Integer cityId;
	
	/**
	 * 城市名称
	 */
	private String cityName;
	
	/**
	 * 所属省份Id
	 */
	private Integer parentId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
}
