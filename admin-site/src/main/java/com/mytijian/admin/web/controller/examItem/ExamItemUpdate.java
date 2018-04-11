/*
 * Copyright 2018 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.web.controller.examItem;

/**
 * 类ExamItemUpdate.java的实现描述：TODO 类实现描述 
 * @author csj 2018年1月31日 下午7:14:56
 */
public class ExamItemUpdate {
	/**
	 * 体检项目His编码
	 */
	private String hisItemId;

	/**
	 * 项目名称
	 */
	private String name;

	/**
	 * 项目性别，0男 1女 2通用
	 */
	private Integer gender;

	/**
	 * 价格
	 */
	private Integer price;

	/**
	 * 标准项ID
	 */
	private Integer standardLibraryId;

	/**
	 * 适宜人群
	 */
	private String fitPeople;

	/**
	 * 不适宜人群
	 */
	private String unfitPeople;

	/**
	 * 项目简介
	 */
	private String description;

	/**
	 * 详细介绍
	 */
	private String detail;

	public String getHisItemId() {
		return hisItemId;
	}

	public void setHisItemId(String hisItemId) {
		this.hisItemId = hisItemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getStandardLibraryId() {
		return standardLibraryId;
	}

	public void setStandardLibraryId(Integer standardLibraryId) {
		this.standardLibraryId = standardLibraryId;
	}

	public String getFitPeople() {
		return fitPeople;
	}

	public void setFitPeople(String fitPeople) {
		this.fitPeople = fitPeople;
	}

	public String getUnfitPeople() {
		return unfitPeople;
	}

	public void setUnfitPeople(String unfitPeople) {
		this.unfitPeople = unfitPeople;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
