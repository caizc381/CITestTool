package com.mytijian.admin.web.vo.meal;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.google.common.base.Objects;
import com.mytijian.admin.web.common.validator.constraints.Gender;
import com.mytijian.product.meal.constant.enums.StandardMealTypeEnum;
import com.mytijian.resource.model.Hospital;

public class StandardMealVO implements Serializable {
	// 套餐模板类型
	private Map<String, String> mealTemplateTypeMap = new HashMap<String, String>();
	// 加项包模板类型
	private Map<String, String> examPackageTemplateTypeMap = new HashMap<String, String>();

	public StandardMealVO() {
		mealTemplateTypeMap.put("111", "平台套餐模版:是,官方套餐模版:是,基础推荐套餐模版:是");
		mealTemplateTypeMap.put("011", "平台套餐模版:否,官方套餐模版:是,基础推荐套餐模版:是");
		mealTemplateTypeMap.put("001", "平台套餐模版:否,官方套餐模版:否,基础推荐套餐模版:是");
		mealTemplateTypeMap.put("000", "平台套餐模版:否,官方套餐模版:否,基础推荐套餐模版:否");
		mealTemplateTypeMap.put("100", "平台套餐模版:是,官方套餐模版:否,基础推荐套餐模版:否");
		mealTemplateTypeMap.put("110", "平台套餐模版:是,官方套餐模版:是,基础推荐套餐模版:否");
		mealTemplateTypeMap.put("101", "平台套餐模版:是,官方套餐模版:否,基础推荐套餐模版:是");
		mealTemplateTypeMap.put("010", "平台套餐模版:否,官方套餐模版:是,基础推荐套餐模版:否");

		examPackageTemplateTypeMap.put("11", "风险加项包模板:是,常规加项包模板:是");
		examPackageTemplateTypeMap.put("00", "风险加项包模板:否,常规加项包模板:否");
		examPackageTemplateTypeMap.put("10", "风险加项包模板:是,常规加项包模板:否");
		examPackageTemplateTypeMap.put("01", "风险加项包模板:否,常规加项包模板:是");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2418903191670671326L;

	/**
	 * id
	 */
	private Integer id;
	/**
	 * 名称
	 */
	@Length(max = 30, message = "套餐名称长度超过限制(最大30个字符)")
	@NotBlank(message = "套餐名称不能为空")
	private String name;

	/**
	 * 类型 1:标准套餐 2:标准加项包
	 * 
	 * @see com.mytijian.product.meal.constant.enums.StandardMealTypeEnum
	 */
	private Integer type;

	/**
	 * 模版类型
	 */
	private String templateType;

	/**
	 * 模版类型描述
	 */
	private String templateTypeDesc;

	/**
	 * 折扣，都是1
	 */
	@DecimalMin(value = "0.01", message = "折扣最低为0.01")
	@DecimalMax(value = "2.00", message = "折扣最高为2.00")
	private Double discount;

	/**
	 * 折后价格
	 */
	private Integer price;

	/**
	 * 单项合计
	 */
	private Integer initPrice;

	/**
	 * 展示价格的最低价
	 */
	private Integer minDisplayPrice;
	/**
	 * 展示价格的最高价
	 */
	private Integer maxDisplayPrice;

	/**
	 * 单项列表
	 */
	private List<StandardMealExamitemVO> standardMealItemList;

	/**
	 * 拼音
	 */
	private String pinyin;

	/**
	 * 描述
	 */
	@Length(max = 512, message = "套餐描述长度超过限制(最大512个字符串)")
	private String description;

	/**
	 * 排序
	 */
	private Integer sequence;

	/**
	 * 关键词
	 */
	private String keyword;

	/**
	 * 修改文案
	 */
	@Length(max = 16, message = "修改提示长度超过限制(最大16个字符串)")
	private String modifyText;

	/**
	 * 性别
	 * 
	 * @see com.mytijian.offer.examitem.constant.enums.GenderEnum
	 */
	@Gender(message = "性别的值无效", allowNull = false)
	private Integer gender;

	/**
	 * 关联的平台套餐数量
	 */
	private Integer platformMealSize;

	/**
	 * 创建时间
	 */
	private Date gmtCreated;

	/**
	 * 更新时间
	 */
	private Date gmtModified;

	/**
	 * 支持的体检中心列表。Hospital只包含id、name
	 */
	private List<Hospital> supportedHospitalList;

	/**
	 * 平台套餐模板
	 */
	private boolean isPlatformMealTemplate;
	/**
	 * 官方套餐模板
	 */
	private boolean isOfficeMealTemplate;
	/**
	 * 基础套餐模板
	 */
	private boolean isBasicMealTemplate;
	/**
	 * 风险加项包模板
	 */
	private boolean isRiskPackageTemplate;
	/**
	 * 常规加项包模板
	 */
	private boolean isCommonPackageTemplate;
	
	/**
	 * 是否被体检中心关联平台套餐
	 */
	private boolean isRelevancePlatformMeal;
	
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getInitPrice() {
		return initPrice;
	}

	public void setInitPrice(Integer initPrice) {
		this.initPrice = initPrice;
	}

	public Integer getMinDisplayPrice() {
		return minDisplayPrice;
	}

	public void setMinDisplayPrice(Integer minDisplayPrice) {
		this.minDisplayPrice = minDisplayPrice;
	}

	public Integer getMaxDisplayPrice() {
		return maxDisplayPrice;
	}

	public void setMaxDisplayPrice(Integer maxDisplayPrice) {
		this.maxDisplayPrice = maxDisplayPrice;
	}

	public List<StandardMealExamitemVO> getStandardMealItemList() {
		return standardMealItemList;
	}

	public void setStandardMealItemList(
			List<StandardMealExamitemVO> standardMealItemList) {
		this.standardMealItemList = standardMealItemList;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getModifyText() {
		return modifyText;
	}

	public void setModifyText(String modifyText) {
		this.modifyText = modifyText;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getPlatformMealSize() {
		return platformMealSize;
	}

	public void setPlatformMealSize(Integer platformMealSize) {
		this.platformMealSize = platformMealSize;
	}

	public Date getGmtCreated() {
		return gmtCreated;
	}

	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public List<Hospital> getSupportedHospitalList() {
		return supportedHospitalList;
	}

	public void setSupportedHospitalList(List<Hospital> supportedHospitalList) {
		this.supportedHospitalList = supportedHospitalList;
	}

	public void setTemplateTypeDesc(String templateTypeDesc) {
		this.templateTypeDesc = templateTypeDesc;
	}

	public String getTemplateTypeDesc() {
		if (StringUtils.isBlank(this.getTemplateType())) {
			return "未设置为任何模板";
		}
		if (Objects.equal(StandardMealTypeEnum.MEAL.getCode(), this.getType())) {
			return mealTemplateTypeMap.get(this.getTemplateType());
		} else if (Objects
				.equal(StandardMealTypeEnum.EXAMITEM_PACKAGE.getCode(),
						this.getType())) {
			return examPackageTemplateTypeMap.get(this.getTemplateType());
		}
		return "";
	}

	public boolean isPlatformMealTemplate() {
		if (StringUtils.isBlank(this.getTemplateType())) {
			return false;
		}

		if (Objects.equal(this.getType(), StandardMealTypeEnum.MEAL.getCode())) {
			return Objects.equal(this.getTemplateType().toCharArray()[0], '1') ? true
					: false;
		}

		return isPlatformMealTemplate;
	}

	public boolean isOfficeMealTemplate() {
		if (StringUtils.isBlank(this.getTemplateType())) {
			return false;
		}

		if (Objects.equal(this.getType(), StandardMealTypeEnum.MEAL.getCode())) {
			return Objects.equal(this.getTemplateType().toCharArray()[1], '1') ? true
					: false;
		}

		return isOfficeMealTemplate;
	}

	public boolean isBasicMealTemplate() {
		if (StringUtils.isBlank(this.getTemplateType())) {
			return false;
		}

		if (Objects.equal(this.getType(), StandardMealTypeEnum.MEAL.getCode())) {
			return Objects.equal(this.getTemplateType().toCharArray()[2], '1') ? true
					: false;
		}

		return isBasicMealTemplate;
	}

	public boolean isRiskPackageTemplate() {
		if (StringUtils.isBlank(this.getTemplateType())) {
			return false;
		}

		if (Objects.equal(this.getType(),
				StandardMealTypeEnum.EXAMITEM_PACKAGE.getCode())) {
			return Objects.equal(this.getTemplateType().toCharArray()[0], '1') ? true
					: false;
		}

		return isRiskPackageTemplate;
	}

	public boolean isCommonPackageTemplate() {
		if (StringUtils.isBlank(this.getTemplateType())) {
			return false;
		}
		if (Objects.equal(this.getType(),
				StandardMealTypeEnum.EXAMITEM_PACKAGE.getCode())) {
			return Objects.equal(this.getTemplateType().toCharArray()[1], '1') ? true
					: false;
		}
		return isCommonPackageTemplate;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public boolean isRelevancePlatformMeal() {
		return isRelevancePlatformMeal;
	}

	public void setRelevancePlatformMeal(boolean isRelevancePlatformMeal) {
		this.isRelevancePlatformMeal = isRelevancePlatformMeal;
	}

	public Boolean getAllowChangeItem() {
		return allowChangeItem;
	}

	public void setAllowChangeItem(Boolean allowChangeItem) {
		this.allowChangeItem = allowChangeItem;
	}
	
}
