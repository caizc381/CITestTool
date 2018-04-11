package com.mytijian.admin.api.address.model;

import java.io.Serializable;

public class Province implements Serializable {

	private static final long serialVersionUID = -7139734368053945567L;

	private Integer id;

	/**
	 * 省份Id
	 */
	private Integer provinceId;

	/**
	 * 省份名称
	 */
	private String provinceName;

	/**
	 * 所属区域Id
	 */
	private Integer parentId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
}
