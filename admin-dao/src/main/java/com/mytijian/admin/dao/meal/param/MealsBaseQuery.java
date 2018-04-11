package com.mytijian.admin.dao.meal.param;

import java.util.List;

import com.mytijian.base.page.Page;

public class MealsBaseQuery {
	
	private List<Integer> hospitals;
	
	/**
	 * 套餐类型
	 */
	private List<Integer> types;
	
	/**
	 * 排序条件
	 */
	private String orderCondition;
	
	/**
	 * 是否倒序
	 */
	private boolean isDesc;
	
	private Page page;
	
	/**
	 * 是否选择地址
	 */
	private boolean isSelectedAddress;
	
	private Integer standardMealId;

	public List<Integer> getHospitals() {
		return hospitals;
	}

	public void setHospitals(List<Integer> hospitals) {
		this.hospitals = hospitals;
	}

	public List<Integer> getTypes() {
		return types;
	}

	public void setTypes(List<Integer> types) {
		this.types = types;
	}

	public String getOrderCondition() {
		return orderCondition;
	}

	public void setOrderCondition(String orderCondition) {
		this.orderCondition = orderCondition;
	}

	public boolean isDesc() {
		return isDesc;
	}

	public void setDesc(boolean isDesc) {
		this.isDesc = isDesc;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public boolean getIsSelectedAddress() {
		return isSelectedAddress;
	}

	public void setIsSelectedAddress(boolean isSelectedAddress) {
		this.isSelectedAddress = isSelectedAddress;
	}

	public Integer getStandardMealId() {
		return standardMealId;
	}

	public void setStandardMealId(Integer standardMealId) {
		this.standardMealId = standardMealId;
	}
	
}
