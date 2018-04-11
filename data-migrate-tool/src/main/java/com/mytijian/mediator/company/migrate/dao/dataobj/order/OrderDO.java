package com.mytijian.mediator.company.migrate.dao.dataobj.order;

import java.util.Date;

/**
 * 订单do对象
 */
public class OrderDO {

    private Integer id;

    /**
     *批次id
     */
    private Integer batchId;


    /**
     * 订单号
     */
    private String orderNum;

    /**
     * 用户id
     */
    private Integer accountId;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单价格
     */
    private Integer orderPrice;

    /**
     * 体检时间
     */
    private Date examDate;

    /**
     * 预约时间段
     */
    private Integer examTimeIntervalId;

    /**
     *医院id
     */
    private Integer hospitalId;

    /**
     * 体检报告id
     */
    private Integer reportId;

    /**
     * 体检卡类型 CardTypeEnum
     */
    private Integer cardType;

    /**
     * 入口卡
     */
    private Integer entryCardId;

    /**
     * 折扣
     */
    private Double discount;

    /**
     * 是否导出
     */
    private Boolean isExport;

    /**
     * 订单来源：1：mytijian， 2：mobile，3：crm， 4：job
     */
    private Integer source;

    /**
     * 插入时间
     */
    private Date insertTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 导出体检单项快照
     */
    private String itemsDetail;


    /**
     * 套餐快照
     */
    private String mealDetail;

    /**
     * 单项包快照
     */
    private String packageSnapshotDetail;

    /**
     * 备注
     */
    private String remark;


    /**
     * 订单差额，正常订单此字段为空，用于以下订单，如：使用了隐藏金额的卡
     */
    private Integer differencePrice;

    /**
     * 来源站点
     */
    private  Integer fromSite;

    /**
     * 平台附加金额
     */
    private Integer platformAdjustPrice;

    /**
     * 订单获取次数
     */
    private Integer accessOrderTimes;

    /**
     * 体检报告获取次数
     */
    private Integer accessReportTimes;

    /**
     * 客户现场付款，1：是，0：否
     */
    private Boolean isSitePay;

    /**
     * 隐藏价格，1：是，0：否
     */
    private Boolean isHidePrice;

    /**
     *允许减项
     */
    private Boolean isReduceItem;

    /**
     * 允许改期
     */
    private Boolean isChangeDate;

    /**
     * 挂账单位id
     */
    private Integer accountCompanyId;

    /**
     * 医院单位
     */
    private Integer hospitalCompanyId;
    /**
     * 原单位id
     */
    private Integer oldExamCompanyId;

    /**
     * 渠道商单位id
     */
    private Integer channelCompanyId;

    /**
     * 操作人id
     */
    private Integer operatorId;

    /**
     *住宿id
     */
    private Integer recidenceId;

    /**
     * 邮寄地址
     */
    private Integer maillingRecordId;

    /**
     * 账单id
     */
    private Integer invoiceId;

    /**
     * 客户经理id
     */
    private Integer orderManagerId;
    
    /**
     * 挂账客户经理id
     */
    private Integer accountManagerId;

    /**
     * 渠道商客户经理id
     */
    private Integer orderChannelManagerId;

    /**
     * '机构类型 1：体检中心 2:健康管理中心'
     */
    private Integer fromSiteOrgType;

    /**
     * 是否需要纸质报告
     */
    private Boolean needPaperReport;


    /**
     * 立即导出
     */
    private Boolean exportImmediately;

    /**
     * 扩展属性
     */
    private String extAttr;

    /**
     * 扩展属性类型
     */
    private Integer extAttrType;

    /**
     * 订单原始金额
     */
    private  Integer orderOriginalPrice;

    /**
     *订单场景 急速预约
     */
    private  Integer orderScene;

    public Integer getOrderScene() {
        return orderScene;
    }

    public void setOrderScene(Integer orderScene) {
        this.orderScene = orderScene;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Integer orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public Integer getExamTimeIntervalId() {
        return examTimeIntervalId;
    }

    public void setExamTimeIntervalId(Integer examTimeIntervalId) {
        this.examTimeIntervalId = examTimeIntervalId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public Integer getEntryCardId() {
        return entryCardId;
    }

    public void setEntryCardId(Integer entryCardId) {
        this.entryCardId = entryCardId;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Boolean getIsExport() {
		return isExport;
	}

	public void setIsExport(Boolean isExport) {
		this.isExport = isExport;
	}

	public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getItemsDetail() {
        return itemsDetail;
    }

    public void setItemsDetail(String itemsDetail) {
        this.itemsDetail = itemsDetail;
    }

    public String getMealDetail() {
        return mealDetail;
    }

    public void setMealDetail(String mealDetail) {
        this.mealDetail = mealDetail;
    }

    public String getPackageSnapshotDetail() {
        return packageSnapshotDetail;
    }

    public void setPackageSnapshotDetail(String packageSnapshotDetail) {
        this.packageSnapshotDetail = packageSnapshotDetail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getDifferencePrice() {
        return differencePrice;
    }

    public void setDifferencePrice(Integer differencePrice) {
        this.differencePrice = differencePrice;
    }

    public Integer getFromSite() {
        return fromSite;
    }

    public void setFromSite(Integer fromSite) {
        this.fromSite = fromSite;
    }

    public Integer getPlatformAdjustPrice() {
        return platformAdjustPrice;
    }

    public void setPlatformAdjustPrice(Integer platformAdjustPrice) {
        this.platformAdjustPrice = platformAdjustPrice;
    }

    public Integer getAccessOrderTimes() {
        return accessOrderTimes;
    }

    public void setAccessOrderTimes(Integer accessOrderTimes) {
        this.accessOrderTimes = accessOrderTimes;
    }

    public Integer getAccessReportTimes() {
        return accessReportTimes;
    }

    public void setAccessReportTimes(Integer accessReportTimes) {
        this.accessReportTimes = accessReportTimes;
    }

    public Integer getAccountCompanyId() {
        return accountCompanyId;
    }

    public void setAccountCompanyId(Integer accountCompanyId) {
        this.accountCompanyId = accountCompanyId;
    }

    public Integer getHospitalCompanyId() {
        return hospitalCompanyId;
    }

    public void setHospitalCompanyId(Integer hospitalCompanyId) {
        this.hospitalCompanyId = hospitalCompanyId;
    }

    public Integer getChannelCompanyId() {
        return channelCompanyId;
    }

    public void setChannelCompanyId(Integer channelCompanyId) {
        this.channelCompanyId = channelCompanyId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getRecidenceId() {
        return recidenceId;
    }

    public void setRecidenceId(Integer recidenceId) {
        this.recidenceId = recidenceId;
    }

    public Integer getMaillingRecordId() {
        return maillingRecordId;
    }

    public void setMaillingRecordId(Integer maillingRecordId) {
        this.maillingRecordId = maillingRecordId;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getOrderChannelManagerId() {
		return orderChannelManagerId;
	}

	public void setOrderChannelManagerId(Integer orderChannelManagerId) {
		this.orderChannelManagerId = orderChannelManagerId;
	}

	public Integer getFromSiteOrgType() {
        return fromSiteOrgType;
    }

    public void setFromSiteOrgType(Integer fromSiteOrgType) {
        this.fromSiteOrgType = fromSiteOrgType;
    }

    public Boolean getNeedPaperReport() {
        return needPaperReport;
    }

    public void setNeedPaperReport(Boolean needPaperReport) {
        this.needPaperReport = needPaperReport;
    }

    public Boolean getExportImmediately() {
        return exportImmediately;
    }

    public void setExportImmediately(Boolean exportImmediately) {
        this.exportImmediately = exportImmediately;
    }

    public String getExtAttr() {
        return extAttr;
    }

    public void setExtAttr(String extAttr) {
        this.extAttr = extAttr;
    }

    public Integer getExtAttrType() {
        return extAttrType;
    }

    public void setExtAttrType(Integer extAttrType) {
        this.extAttrType = extAttrType;
    }

    public Integer getOrderOriginalPrice() {
        return orderOriginalPrice;
    }

    public void setOrderOriginalPrice(Integer orderOriginalPrice) {
        this.orderOriginalPrice = orderOriginalPrice;
    }

	public Boolean getIsSitePay() {
		return isSitePay;
	}

	public void setIsSitePay(Boolean isSitePay) {
		this.isSitePay = isSitePay;
	}

	public Boolean getIsHidePrice() {
		return isHidePrice;
	}

	public void setIsHidePrice(Boolean isHidePrice) {
		this.isHidePrice = isHidePrice;
	}

	public Boolean getIsReduceItem() {
		return isReduceItem;
	}

	public void setIsReduceItem(Boolean isReduceItem) {
		this.isReduceItem = isReduceItem;
	}

	public Boolean getIsChangeDate() {
		return isChangeDate;
	}

	public void setIsChangeDate(Boolean isChangeDate) {
		this.isChangeDate = isChangeDate;
	}

	public Integer getOrderManagerId() {
		return orderManagerId;
	}

	public void setOrderManagerId(Integer orderManagerId) {
		this.orderManagerId = orderManagerId;
	}

	public Integer getAccountManagerId() {
		return accountManagerId;
	}

	public void setAccountManagerId(Integer accountManagerId) {
		this.accountManagerId = accountManagerId;
	}

	public Integer getOldExamCompanyId() {
		return oldExamCompanyId;
	}

	public void setOldExamCompanyId(Integer oldExamCompanyId) {
		this.oldExamCompanyId = oldExamCompanyId;
	}
	
}