package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;
import java.util.Date;

public class ExamCompanyHospitalRelDO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2220534869372888959L;

	private Integer hospitalId;

	private Integer companyId;

	private Double discount;

	private Boolean showReport;

	private Boolean showInvoice;

	private Boolean employeeImport;

	private Date createdTime;

	private Date updateTime;

	private Integer settlementMode;

	private Boolean supportAnyTimeImport;

	private String companyAlias;

	private String hisName;

	private Integer status;

	private Boolean advanceExportOrder;

	private Boolean sendExamSms;

	private Integer sendExamSmsDays;
	
	/**
	 * 体检地址
	 */
	private String examinationAddress;
	
	/**
	 * 体检报告设置间隔时间对用户可见
	 */
	private Integer examreportIntervalTime;

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
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

	public Boolean getShowInvoice() {
		return showInvoice;
	}

	public void setShowInvoice(Boolean showInvoice) {
		this.showInvoice = showInvoice;
	}

	public Boolean getEmployeeImport() {
		return employeeImport;
	}

	public void setEmployeeImport(Boolean employeeImport) {
		this.employeeImport = employeeImport;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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

	public String getCompanyAlias() {
		return companyAlias;
	}

	public void setCompanyAlias(String companyAlias) {
		this.companyAlias = companyAlias;
	}

	public String getHisName() {
		return hisName;
	}

	public void setHisName(String hisName) {
		this.hisName = hisName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
