package com.mytijian.admin.web.vo.examitem;

import java.io.Serializable;
import java.util.Date;

public class ExamItemWithOutIgnoreVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5032809027396297660L;
	private int id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 详情
	 */
	private String detail;
	/**
	 * 适宜人群
	 */
	private String fitPeople;
	/**
	 * 不适宜人群
	 */
	private String unfitPeople;
	/**
	 * 性别 0:男,1:女,2:男女通用
	 */
	private Integer gender;
	/**
	 * 拼音简写
	 */
	private String pinyin;
	/**
	 * 体检中心id
	 */
	private Integer hospitalId;
	/**
	 * 套餐id
	 */
	private Integer mealId;
	/**
	 * 现价
	 */
	private Integer price;
	/**
	 * 组项目id
	 */
	private Integer groupId;
	/**
	 * 是否可打折
	 */
	private boolean discount = true;
	/**
	 * 是否是必选项
	 */
	private boolean basic = false;
	/**
	 * 是否显示
	 */
	private boolean show = true;
	/**
	 * 如果是必须选项，下拉框是否可以选
	 */
	private boolean enableSelect = true;
	/**
	 * 是否可以自定义小项
	 */
	private Boolean enableCustom = false;
	/**
	 * 顺序, 相同groupId的项目sequence应该一致
	 */
	private Integer sequence;

	/**
	 * 是否在套餐中被选中
	 */
	private boolean selected = true;

	/**
	 * His系统编号
	 */
	private String hisItemId;

	/**
	 * 类型：1.体检中心项目 2.外送 3.自有 4.小项 5.人数控制 6.废除项目
	 * 以resource_ddl.sql为准
	 * @see com.mytijian.offer.examitem.constant.enums.ExamItemTypeEnum
	 */
	private Integer itemType;
	
	/**
	 * 拒检标记 1-拒检,2-提交
	 */
	private String refuseStatus = String.valueOf(2);
	
	/**
	 * 是否是重点关注项目
	 */
	private Boolean focus;
	
	/**
	 * 科室标识
	 */
	private Integer departmentId;
	
	/**
	 * 是否同步单项价格
	 */
	private Boolean syncPrice;
	
	/**
	 * 项目标签
	 */
	private String tagName;
	
	/**
	 * 项目警告
	 */
	private String warning;
	
	/**
	 * 是否显示项目警告
	 */
	private Boolean showWarning;
	
	/**
	 * 瓶颈项目
	 */
	private boolean bottleneck;
	
	/**
	 * 据检是否退款，true：据检退款 false：据检不退款
	 */
	private Boolean refundRefusedItem;
	
	/**
	 * 创建时间
	 */
	private Date gmtCreated;

	/**
	 * 修改时间
	 */
	private Date gmtModified;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getMealId() {
		return mealId;
	}

	public void setMealId(Integer mealId) {
		this.mealId = mealId;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public boolean isDiscount() {
		return discount;
	}

	public void setDiscount(boolean discount) {
		this.discount = discount;
	}

	public boolean isBasic() {
		return basic;
	}

	public void setBasic(boolean basic) {
		this.basic = basic;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public boolean isEnableSelect() {
		return enableSelect;
	}

	public void setEnableSelect(boolean enableSelect) {
		this.enableSelect = enableSelect;
	}

	public Boolean getEnableCustom() {
		return enableCustom;
	}

	public void setEnableCustom(Boolean enableCustom) {
		this.enableCustom = enableCustom;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getHisItemId() {
		return hisItemId;
	}

	public void setHisItemId(String hisItemId) {
		this.hisItemId = hisItemId;
	}

	public Integer getItemType() {
		return itemType;
	}

	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}

	public String getRefuseStatus() {
		return refuseStatus;
	}

	public void setRefuseStatus(String refuseStatus) {
		this.refuseStatus = refuseStatus;
	}

	public Boolean getFocus() {
		return focus;
	}

	public void setFocus(Boolean focus) {
		this.focus = focus;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Boolean getSyncPrice() {
		return syncPrice;
	}

	public void setSyncPrice(Boolean syncPrice) {
		this.syncPrice = syncPrice;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public Boolean getShowWarning() {
		return showWarning;
	}

	public void setShowWarning(Boolean showWarning) {
		this.showWarning = showWarning;
	}

	public boolean isBottleneck() {
		return bottleneck;
	}

	public void setBottleneck(boolean bottleneck) {
		this.bottleneck = bottleneck;
	}

	public Boolean getRefundRefusedItem() {
		return refundRefusedItem;
	}

	public void setRefundRefusedItem(Boolean refundRefusedItem) {
		this.refundRefusedItem = refundRefusedItem;
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
	
	
}
