package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;
import java.util.Date;

public class HospitalCompanyDO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4651845821349953410L;
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
	 * 是否展示报告
	 */
	private Boolean showReport;
	
	/**
	 * 是否支持员工号导入
	 */
	private Boolean employeeImport;
	/**
	 * 结算方式
	 */
	private Integer settlementMode;
	/**
	 * 是否支持即时导入
	 */
	private Boolean supportAnyTimeImport;
	/**
	 * his单位名
	 */
	private String hisName;
	/**
	 * 是否提前导出
	 */
	private Boolean advanceExportOrder;
	/**
	 * 是否发生检前短信
	 */
	private Boolean sendExamSms;
	/**
	 * 检前短息提前发送天数
	 */
	private Integer sendExamSmsDays;
	/**
	 * 拼音
	 */
	private String pinyin;
	/**
	 * 删除标记，status映射到该字段
	 */
	private Boolean deleted;
	/**
	 * 员工号前缀
	 */
	private String employeePrefix;
	/**
	 * 关联tb_exam_company.id
	 */
	private Integer tbExamCompanyId;
	
	/**
	 * 体检地址
	 */
	private String examinationAddress;
	
	/**
	 * 体检报告设置间隔时间对用户可见
	 */
	private Integer examreportIntervalTime;
	
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

	public Boolean getShowReport() {
		return showReport;
	}

	public void setShowReport(Boolean showReport) {
		this.showReport = showReport;
	}

	public Boolean getEmployeeImport() {
		return employeeImport;
	}

	public void setEmployeeImport(Boolean employeeImport) {
		this.employeeImport = employeeImport;
	}

	public Integer getSettlementMode() {
		return settlementMode;
	}

	public void setSettlementMode(Integer settlementMode) {
		this.settlementMode = settlementMode;
	}

	public Boolean getSupportAnyTimeImport() {
		return supportAnyTimeImport;
	}

	public void setSupportAnyTimeImport(Boolean supportAnyTimeImport) {
		this.supportAnyTimeImport = supportAnyTimeImport;
	}

	public String getHisName() {
		return hisName;
	}

	public void setHisName(String hisName) {
		this.hisName = hisName;
	}

	public Boolean getAdvanceExportOrder() {
		return advanceExportOrder;
	}

	public void setAdvanceExportOrder(Boolean advanceExportOrder) {
		this.advanceExportOrder = advanceExportOrder;
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

	public Integer getTbExamCompanyId() {
		return tbExamCompanyId;
	}

	public void setTbExamCompanyId(Integer tbExamCompanyId) {
		this.tbExamCompanyId = tbExamCompanyId;
	}

	public String getEmployeePrefix() {
		return employeePrefix;
	}

	public void setEmployeePrefix(String employeePrefix) {
		this.employeePrefix = employeePrefix;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getExaminationAddress() {
		return examinationAddress;
	}

	public void setExaminationAddress(String examinationAddress) {
		this.examinationAddress = examinationAddress;
	}

	public Integer getExamreportIntervalTime() {
		return examreportIntervalTime;
	}

	public void setExamreportIntervalTime(Integer examreportIntervalTime) {
		this.examreportIntervalTime = examreportIntervalTime;
	}
	
}
