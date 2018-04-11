package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;
import java.util.Date;

public class ChannelCompanyDO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6915291515794804579L;

	/**
	 * 主键
	 */
	private Integer id;
	/**
	 * 创建时间
	 */
	private Date gmtCreated;
	/**
	 * 更新时间
	 */
	private Date gmtModified;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 平台单位id
	 */
	private Integer platformCompanyId;
	/**
	 * 机构id
	 */
	private Integer organizationId;

	/**
	 * 机构名称
	 */
	private String organizationName;
	/**
	 * 折扣
	 */
	private Double discount;

	/**
	 * 结算模式
	 */
	private Integer settlementMode;

	/**
	 * 是否发生检前短信
	 */
	private Boolean sendExamSms;
	/**
	 * 检前短信提前天数
	 */
	private Integer sendExamSmsDays;
	/**
	 * 拼音
	 */
	private String pinyin;
	/**
	 * 删除标记，原来的status映射到该字段
	 */
	private Boolean deleted;
	/**
	 * 关联tb_exam_company.id
	 */
	private Integer tbExamCompanyId;
	
	/**
	 * 地理描述
	 */
	private String description;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPlatformCompanyId() {
		return platformCompanyId;
	}

	public void setPlatformCompanyId(Integer platformCompanyId) {
		this.platformCompanyId = platformCompanyId;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getSettlementMode() {
		return settlementMode;
	}

	public void setSettlementMode(Integer settlementMode) {
		this.settlementMode = settlementMode;
	}

	public Boolean getSendExamSms() {
		return sendExamSms;
	}

	public void setSendExamSms(Boolean sendExamSms) {
		this.sendExamSms = sendExamSms;
	}

	public Integer getSendExamSmsDays() {
		return sendExamSmsDays;
	}

	public void setSendExamSmsDays(Integer sendExamSmsDays) {
		this.sendExamSmsDays = sendExamSmsDays;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Integer getTbExamCompanyId() {
		return tbExamCompanyId;
	}

	public void setTbExamCompanyId(Integer tbExamCompanyId) {
		this.tbExamCompanyId = tbExamCompanyId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
