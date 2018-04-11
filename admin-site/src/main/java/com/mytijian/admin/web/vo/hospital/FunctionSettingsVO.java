package com.mytijian.admin.web.vo.hospital;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class FunctionSettingsVO {
	
	private Integer hospitalId;
	
	private Boolean acceptOfflinePay;
	
	private Boolean accountPay;
	
	private Boolean aliPay;
	
	private Boolean weiXinPay;
	
	private Integer allowAdjustPrice;
	
	private String calculatorService;
	
	private String drawBloodEnd;
	
	private String drawBloodStart;
	
	private String examEndTime;
	
	private String examStartTime;
	
	private Integer exportWithNoExamDate;
	
	private Integer isSmartRecommend;
	
	private Integer needLocalPay;
	
	private Integer openGroupExamReport;
	
	private Integer previousBookDays;
	
	private String previousBookTime;
	
	private Integer previousExportDays;
	
	private Integer sameDayOrderMaximum;
	
	private Integer settleCycle;
	
	private String workDay;
	
	private Integer showNonPlatformExamReport;


	/**
	 * 是否显示体检报告查看情况
	 */
	private Integer showExamReportInspectTab;

	private Integer supportManualRefund;
	
	/**
	 * 可接待人数/人数控制
	 */
	private Integer limitNum;

	public Integer getShowExamReportInspectTab() {
		return showExamReportInspectTab;
	}

	public void setShowExamReportInspectTab(Integer showExamReportInspectTab) {
		this.showExamReportInspectTab = showExamReportInspectTab;
	}

	public Integer getSupportManualRefund() {
		return supportManualRefund;
	}

	public void setSupportManualRefund(Integer supportManualRefund) {
		this.supportManualRefund = supportManualRefund;
	}

	public String getCalculatorService() {
		return calculatorService;
	}

	public void setCalculatorService(String calculatorService) {
		this.calculatorService = calculatorService;
	}

	public String getDrawBloodEnd() {
		return drawBloodEnd;
	}

	public void setDrawBloodEnd(String drawBloodEnd) {
		this.drawBloodEnd = drawBloodEnd;
	}

	public String getDrawBloodStart() {
		return drawBloodStart;
	}

	public void setDrawBloodStart(String drawBloodStart) {
		this.drawBloodStart = drawBloodStart;
	}

	public String getExamEndTime() {
		return examEndTime;
	}

	public void setExamEndTime(String examEndTime) {
		this.examEndTime = examEndTime;
	}

	public String getExamStartTime() {
		return examStartTime;
	}

	public void setExamStartTime(String examStartTime) {
		this.examStartTime = examStartTime;
	}

	public Integer getExportWithNoExamDate() {
		return exportWithNoExamDate;
	}

	public void setExportWithNoExamDate(Integer exportWithNoExamDate) {
		this.exportWithNoExamDate = exportWithNoExamDate;
	}

	public Integer getIsSmartRecommend() {
		return isSmartRecommend;
	}

	public void setIsSmartRecommend(Integer isSmartRecommend) {
		this.isSmartRecommend = isSmartRecommend;
	}

	public Integer getNeedLocalPay() {
		return needLocalPay;
	}

	public void setNeedLocalPay(Integer needLocalPay) {
		this.needLocalPay = needLocalPay;
	}

	public Integer getOpenGroupExamReport() {
		return openGroupExamReport;
	}

	public void setOpenGroupExamReport(Integer openGroupExamReport) {
		this.openGroupExamReport = openGroupExamReport;
	}

	public Integer getPreviousBookDays() {
		return previousBookDays;
	}

	public void setPreviousBookDays(Integer previousBookDays) {
		this.previousBookDays = previousBookDays;
	}

	public String getPreviousBookTime() {
		return previousBookTime;
	}

	public void setPreviousBookTime(String previousBookTime) {
		this.previousBookTime = previousBookTime;
	}

	public Integer getPreviousExportDays() {
		return previousExportDays;
	}

	public void setPreviousExportDays(Integer previousExportDays) {
		this.previousExportDays = previousExportDays;
	}

	public Integer getSameDayOrderMaximum() {
		return sameDayOrderMaximum;
	}

	public void setSameDayOrderMaximum(Integer sameDayOrderMaximum) {
		this.sameDayOrderMaximum = sameDayOrderMaximum;
	}

	public Integer getSettleCycle() {
		return settleCycle;
	}

	public void setSettleCycle(Integer settleCycle) {
		this.settleCycle = settleCycle;
	}


	public Boolean getWeiXinPay() {
		return weiXinPay;
	}

	public void setWeiXinPay(Boolean weiXinPay) {
		this.weiXinPay = weiXinPay;
	}

	public void setAllowAdjustPrice(Integer allowAdjustPrice) {
		this.allowAdjustPrice = allowAdjustPrice;
	}

	public String getWorkDay() {
		return workDay;
	}

	public void setWorkDay(String workDay) {
		this.workDay = workDay;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getAcceptOfflinePay() {
		return acceptOfflinePay;
	}

	public void setAcceptOfflinePay(Boolean acceptOfflinePay) {
		this.acceptOfflinePay = acceptOfflinePay;
	}

	public Boolean getAccountPay() {
		return accountPay;
	}

	public void setAccountPay(Boolean accountPay) {
		this.accountPay = accountPay;
	}

	public Boolean getAliPay() {
		return aliPay;
	}

	public void setAliPay(Boolean aliPay) {
		this.aliPay = aliPay;
	}

	public Integer getAllowAdjustPrice() {
		return allowAdjustPrice;
	}

	public Integer getShowNonPlatformExamReport() {
		return showNonPlatformExamReport;
	}

	public void setShowNonPlatformExamReport(Integer showNonPlatformExamReport) {
		this.showNonPlatformExamReport = showNonPlatformExamReport;
	}

	public Integer getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}

}
