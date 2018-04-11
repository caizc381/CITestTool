/*
 * Copyright 2018 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.web.controller.examItem;

/**
 * 类ExamItemFile.java的实现描述：TODO 类实现描述 
 * @author csj 2018年1月30日 下午3:03:08
 */
public class ExamItemFile {
	/**
	 * 体检项目His编码
	 */
	private String hisId;

	/**
	 * 项目名称
	 */
	private String name;

	/**
	 * 项目性别，0男 1女 2通用
	 */
	private String gender;

	/**
	 * 价格
	 */
	private String price;

	/**
	 * 标准项ID
	 */
	private String standardLibraryId;

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

	public String getHisId() {
		return hisId;
	}

	public void setHisId(String hisId) {
		this.hisId = hisId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getStandardLibraryId() {
		return standardLibraryId;
	}

	public void setStandardLibraryId(String standardLibraryId) {
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
