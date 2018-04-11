package com.mytijian.admin.web.vo.resource;

import java.io.Serializable;

public class HospitalCompanyVO implements Serializable {

	private static final long serialVersionUID = 2769705585742867707L;
	/**
	 * 单位Id， 主键
	 */
	private Integer id;

	/**
	 * 单位名称
	 */
	private String name;

    /**
     * 拼音
	 */
	private String pinyin;

	/**
	 * 单位类型
	 */
	private Integer type;

	/**
	 * 是否为平台单位
	 */
	private Boolean isPlatformCompay;


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

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getIsPlatformCompay() {
		return isPlatformCompay;
	}

	public void setIsPlatformCompay(Boolean platformCompay) {
		isPlatformCompay = platformCompay;
	}
}
