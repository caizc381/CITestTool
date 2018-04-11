package com.mytijian.admin.web.vo.orderrefund;

import com.mytijian.account.model.Account;
import com.mytijian.company.hospital.service.model.HospitalCompany;
import com.mytijian.order.base.service.model.OrderRefundApplyPayDetail;
import com.mytijian.resource.model.Hospital;

import java.util.Date;

/**
 * @author weifeng
 * @date 2017/8/4
 */
public class OrderRefundApplyRecordVO {
    private Date applyTime;
    private Account accountInfo;
    private Hospital fromSiteInfo;
    private HospitalCompany hospitalCompanyInfo;
    private Integer amount;
    private OrderRefundApplyPayDetail payDetail;
    private Integer operator;
    private String operatorName;
    private Date auditTime;
    private Integer status;
    private String reason;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public OrderRefundApplyPayDetail getPayDetail() {
        return payDetail;
    }

    public void setPayDetail(OrderRefundApplyPayDetail payDetail) {
        this.payDetail = payDetail;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Account getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(Account accountInfo) {
        this.accountInfo = accountInfo;
    }

    public Hospital getFromSiteInfo() {
        return fromSiteInfo;
    }

    public void setFromSiteInfo(Hospital fromSiteInfo) {
        this.fromSiteInfo = fromSiteInfo;
    }

    public HospitalCompany getHospitalCompanyInfo() {
        return hospitalCompanyInfo;
    }

    public void setHospitalCompanyInfo(HospitalCompany hospitalCompanyInfo) {
        this.hospitalCompanyInfo = hospitalCompanyInfo;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
