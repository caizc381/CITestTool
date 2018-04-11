package com.mytijian.admin.web.vo.meal;

import java.io.Serializable;

import com.mytijian.offer.meal.model.MealSetting;

/**
 * 
 * @author huangwei
 *
 */
public class MealVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5346253620372824790L;

	/**
	 * 套餐ID
	 */
	private Integer id;

	/**
	 * 套餐名
	 */
	private String name;

	/**
	 * 拼音简写
	 */
	private String pinyin;

	/**
	 * 原价
	 */
	private String initPrice;

	/**
	 * 折扣
	 */
	private Double discount;

	/**
	 * 是否可用
	 */
	private Integer disable;

	/**
	 * 关键词
	 */
	private String keyword;

	/**
	 * 注意事项
	 */
	private String tipText;

	/**
	 * 套餐类型
	 */
	private Integer type;

	/**
	 * 套餐设置
	 */
	private MealSetting mealSetting;

	/**
	 * 标价
	 */
	private String displayPrice;

	/**
	 * 性别 0:男,1:女,2:男女通用 @see com.mytijian.resource.enums.GenderEnum
	 */
	private Integer gender;

	/**
	 * 体检中心id
	 */
	private Integer hospitalId;

	/**
	 * 体检中心名称
	 */
	private String hospitalName;

	/**
	 * 医院地址
	 */
	private String address;

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

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 拓展字段 json 
	 */
	private String ext;

	/**
	 * 标准套餐ID
	 */
	private Integer standardMealId;

	/**
	 * 标准套餐名字
	 */
	private String standardMealName;

	/**
	 * 是否允许改项
	 */
	private Boolean allowChangeItem;

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

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getInitPrice() {
		return initPrice;
	}

	public void setInitPrice(String initPrice) {
		this.initPrice = initPrice;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getDisable() {
		return disable;
	}

	public void setDisable(Integer disable) {
		this.disable = disable;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getTipText() {
		return tipText;
	}

	public void setTipText(String tipText) {
		this.tipText = tipText;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public MealSetting getMealSetting() {
		return mealSetting;
	}

	public void setMealSetting(MealSetting mealSetting) {
		this.mealSetting = mealSetting;
	}

	public String getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(String displayPrice) {
		this.displayPrice = displayPrice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
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

	public Boolean getAllowChangeItem() {
		return allowChangeItem;
	}

	public void setAllowChangeItem(Boolean allowChangeItem) {
		this.allowChangeItem = allowChangeItem;
	}

}
