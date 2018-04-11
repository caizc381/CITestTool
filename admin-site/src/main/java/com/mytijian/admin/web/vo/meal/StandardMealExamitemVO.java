package com.mytijian.admin.web.vo.meal;

import java.io.Serializable;
import java.util.Date;

public class StandardMealExamitemVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6340353137013331638L;
	
	/**
	 * id
	 * 
	 ***/
	private Integer id;

	/**
	 * 是否必选
	 */
	private Boolean basic;
	
	/**
	 * 下拉框选择
	 */
	private Boolean enableSelect;
	
	/**
	 * 套餐id
	 */
	private Integer mealId;

	/**
	 * 项目名称
	 */
	private String name;

	/**
	 * 项目名称拼音
	 */
	private String pinyin;

	/**
	 * 详情
	 * 
	 */
	private String detail;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 适应人群
	 */
	private String fitPeople;

	/**
	 * 不适应人群
	 */
	private String unfitPeople;

	/**
	 * 序列号
	 */
	private Integer sequence;

	/**
	 * 搜索关键字
	 */
	private String keyword;

	/**
	 * 新增时间
	 */
	private Date createTime;

	/**
	 * 客户分类
	 */
	private Integer clientClassify;

	/**
	 * CRM分类
	 */
	private Integer crmClassify;

	/**
	 * 最后一次编辑时间
	 */
	private Date updateTime;

	/**
	 * 返回数据
	 */
	private Integer examItemId;

	/**
	 * 状态
	 * 
	 * @see com.mytijian.product.item.constant.enums.ExamItemStandardStatusEnum
	 */
	private Integer status;

	/**
	 * 性别 0:男,1:女,2:男女通用
	 * 
	 * @see com.mytijian.offer.examitem.constant.enums.GenderEnum
	 */
	private Integer gender;

	/**
	 * 价格。 标准单项价格暂时都是0
	 */
	private Integer price;
	
	public Boolean getEnableSelect() {
		return enableSelect;
	}

	public void setEnableSelect(Boolean enableSelect) {
		this.enableSelect = enableSelect;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getBasic() {
		return basic;
	}

	public void setBasic(Boolean basic) {
		this.basic = basic;
	}

	public Integer getMealId() {
		return mealId;
	}

	public void setMealId(Integer mealId) {
		this.mealId = mealId;
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

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getClientClassify() {
		return clientClassify;
	}

	public void setClientClassify(Integer clientClassify) {
		this.clientClassify = clientClassify;
	}

	public Integer getCrmClassify() {
		return crmClassify;
	}

	public void setCrmClassify(Integer crmClassify) {
		this.crmClassify = crmClassify;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getExamItemId() {
		return examItemId;
	}

	public void setExamItemId(Integer examItemId) {
		this.examItemId = examItemId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
	
}
