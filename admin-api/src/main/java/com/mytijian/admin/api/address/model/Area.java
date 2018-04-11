package com.mytijian.admin.api.address.model;

import java.io.Serializable;
import java.util.List;


public class Area implements Serializable {

	private static final long serialVersionUID = -1175505589936197898L;

	/**
	 *  区域Id
	 */
	private Integer id;
	
	/**
	 * 区域名称
	 */
	private String areaName;
	
	/**
	 * 省份列表
	 */
	private List<Province> provinceList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public List<Province> getProvinceList() {
		return provinceList;
	}

	public void setProvinceList(List<Province> provinceList) {
		this.provinceList = provinceList;
	}
}
