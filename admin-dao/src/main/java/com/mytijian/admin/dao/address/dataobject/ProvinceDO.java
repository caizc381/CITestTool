package com.mytijian.admin.dao.address.dataobject;

import java.io.Serializable;

public class ProvinceDO implements Serializable {
	
	private static final long serialVersionUID = 7250187436104831862L;

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
