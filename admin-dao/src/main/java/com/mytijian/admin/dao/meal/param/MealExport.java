package com.mytijian.admin.dao.meal.param;

public class MealExport {
	
	/**
	 * 地区
	 */
	private String address;
	
	private Integer hospitalId;
	
	private String hospitalName;
	
	private Integer mealId;
	
	private String mealName;
	
	private String gender;
	
	private Integer standardMealId;
	
	private String standardMealName;
	
	/**
	 * 关键字
	 */
	private String key;
	
	/**
	 * 套餐描述
	 */
	private String description;
	
	private String examItemName;
	
	/**
	 * 体检项对应的标准项
	 */
	private String standardItamName;
	
	/**
	 * 套餐折扣
	 */
	private double discount;
	
	/**
	 * 单项合计价格
	 */
	private String itemSummation;
	
	/**
	 * 标价
	 */
	private String displayPrice;
	
	/**
	 * 进货价
	 */
	private String purchasePrice;
	
	/**
	 * 供货价
	 */
	private String supplyPrice;
	
	/**
	 * 销售价
	 */
	private String salePrice;
	
	private String updateDate;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public Integer getMealId() {
		return mealId;
	}

	public void setMealId(Integer mealId) {
		this.mealId = mealId;
	}

	public String getMealName() {
		return mealName;
	}

	public void setMealName(String mealName) {
		this.mealName = mealName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getStandardMealId() {
		return standardMealId;
	}

	public void setStandardMealId(Integer standardMealId) {
		this.standardMealId = standardMealId;
	}

	public String getStandardMealName() {
		return standardMealName;
	}

	public void setStandardMealName(String standardMealName) {
		this.standardMealName = standardMealName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExamItemName() {
		return examItemName;
	}

	public void setExamItemName(String examItemName) {
		this.examItemName = examItemName;
	}

	public String getStandardItamName() {
		return standardItamName;
	}

	public void setStandardItamName(String standardItamName) {
		this.standardItamName = standardItamName;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getItemSummation() {
		return itemSummation;
	}

	public void setItemSummation(String itemSummation) {
		this.itemSummation = itemSummation;
	}

	public String getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(String displayPrice) {
		this.displayPrice = displayPrice;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getSupplyPrice() {
		return supplyPrice;
	}

	public void setSupplyPrice(String supplyPrice) {
		this.supplyPrice = supplyPrice;
	}

	public String getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	
	
}
