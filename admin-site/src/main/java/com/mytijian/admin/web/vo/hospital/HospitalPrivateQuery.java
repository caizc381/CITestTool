package com.mytijian.admin.web.vo.hospital;

import com.mytijian.pulgin.mybatis.pagination.Page;

public class HospitalPrivateQuery {

	/**
	 * 根据业务量排序 1：正序 -1：倒序
	 */
	private Integer orderByBusinessAmount;

	/**
	 * 根据id排序 1：正序 -1：倒序
	 */
	private Integer orderById;

	/**
	 * 提前导出排序 1：正序 -1：倒序
	 */
	private Integer orderByPreviousExportDays;

	/**
	 * 提前预约时间排序 1：正序 -1：倒序
	 */
	private Integer orderByPreviousBookDays;

	/**
	 * 是否开启单位同步
	 */
	private Boolean openSyncCompany;
	
	/**
	 * 体检报告同步
	 */
	private Integer syncExamReportAbility;
	
	/**
	 * 是否支持平台定制套餐
	 */
	private Boolean isIndividuationPlatformMeal;
	
	/**
	 * 是否平台挂账
	 */
	private Boolean isPlatformSuspense;
	
	/**
	 * 是否支持预付开票
	 */
	private Boolean isPrepayInvoice;
	
	/**
	 * 向客户询问要发票，0：否，1：是，2：检后开票
	 */
	private Integer showInvoice;
	
	/**
	 * 站点模板id
	 */
	private Integer siteTemplateId;
	
	/**
	 * 根据平台单位折扣排序
	 */
	private Integer orderByPlatformCompDiscount;
	/**
	 * 平台散客折扣排序
	 */
	private Integer orderByPlatformGuestDiscount;
	/**
	 * 个人网上预约折扣排序
	 */
	private Integer orderByGuestOnlineCompDiscount;
	/**
	 * 普通单位折扣排序
	 */
	private Integer orderByHospitalCompDiscount;
	/**
	 * 前台散客折扣
	 */
	private Integer orderByGuestOfflineCompDiscount;

	private Page page;
	
	private Integer contractStatus;
	
	private Integer contractType;
	
	public Integer getOrderByBusinessAmount() {
		return orderByBusinessAmount;
	}

	public void setOrderByBusinessAmount(Integer orderByBusinessAmount) {
		this.orderByBusinessAmount = orderByBusinessAmount;
	}

	public Integer getOrderById() {
		return orderById;
	}

	public void setOrderById(Integer orderById) {
		this.orderById = orderById;
	}

	public Integer getOrderByPreviousExportDays() {
		return orderByPreviousExportDays;
	}

	public void setOrderByPreviousExportDays(Integer orderByPreviousExportDays) {
		this.orderByPreviousExportDays = orderByPreviousExportDays;
	}

	public Boolean getOpenSyncCompany() {
		return openSyncCompany;
	}

	public void setOpenSyncCompany(Boolean openSyncCompany) {
		this.openSyncCompany = openSyncCompany;
	}

	public Integer getSyncExamReportAbility() {
		return syncExamReportAbility;
	}

	public void setSyncExamReportAbility(Integer syncExamReportAbility) {
		this.syncExamReportAbility = syncExamReportAbility;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public Boolean getIsIndividuationPlatformMeal() {
		return isIndividuationPlatformMeal;
	}

	public void setIsIndividuationPlatformMeal(Boolean isIndividuationPlatformMeal) {
		this.isIndividuationPlatformMeal = isIndividuationPlatformMeal;
	}

	public Boolean getIsPlatformSuspense() {
		return isPlatformSuspense;
	}

	public void setIsPlatformSuspense(Boolean isPlatformSuspense) {
		this.isPlatformSuspense = isPlatformSuspense;
	}

	public Boolean getIsPrepayInvoice() {
		return isPrepayInvoice;
	}

	public void setIsPrepayInvoice(Boolean isPrepayInvoice) {
		this.isPrepayInvoice = isPrepayInvoice;
	}

	public Integer getOrderByPlatformCompDiscount() {
		return orderByPlatformCompDiscount;
	}

	public void setOrderByPlatformCompDiscount(Integer orderByPlatformCompDiscount) {
		this.orderByPlatformCompDiscount = orderByPlatformCompDiscount;
	}

	public Integer getOrderByPlatformGuestDiscount() {
		return orderByPlatformGuestDiscount;
	}

	public void setOrderByPlatformGuestDiscount(Integer orderByPlatformGuestDiscount) {
		this.orderByPlatformGuestDiscount = orderByPlatformGuestDiscount;
	}

	public Integer getOrderByGuestOnlineCompDiscount() {
		return orderByGuestOnlineCompDiscount;
	}

	public void setOrderByGuestOnlineCompDiscount(Integer orderByGuestOnlineCompDiscount) {
		this.orderByGuestOnlineCompDiscount = orderByGuestOnlineCompDiscount;
	}

	public Integer getOrderByHospitalCompDiscount() {
		return orderByHospitalCompDiscount;
	}

	public void setOrderByHospitalCompDiscount(Integer orderByHospitalCompDiscount) {
		this.orderByHospitalCompDiscount = orderByHospitalCompDiscount;
	}

	public Integer getOrderByGuestOfflineCompDiscount() {
		return orderByGuestOfflineCompDiscount;
	}

	public void setOrderByGuestOfflineCompDiscount(Integer orderByGuestOfflineCompDiscount) {
		this.orderByGuestOfflineCompDiscount = orderByGuestOfflineCompDiscount;
	}

	public Integer getShowInvoice() {
		return showInvoice;
	}

	public void setShowInvoice(Integer showInvoice) {
		this.showInvoice = showInvoice;
	}

	public Integer getSiteTemplateId() {
		return siteTemplateId;
	}

	public void setSiteTemplateId(Integer siteTemplateId) {
		this.siteTemplateId = siteTemplateId;
	}
	
	public Integer getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(Integer contractStatus) {
		this.contractStatus = contractStatus;
	}

	public Integer getContractType() {
		return contractType;
	}

	public void setContractType(Integer contractType) {
		this.contractType = contractType;
	}

	public Integer getOrderByPreviousBookDays() {
		return orderByPreviousBookDays;
	}

	public void setOrderByPreviousBookDays(Integer orderByPreviousBookDays) {
		this.orderByPreviousBookDays = orderByPreviousBookDays;
	}

}
