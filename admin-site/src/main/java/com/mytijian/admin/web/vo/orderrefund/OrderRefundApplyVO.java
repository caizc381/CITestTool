package com.mytijian.admin.web.vo.orderrefund;

import java.util.Date;

import com.mytijian.account.model.Account;
import com.mytijian.company.hospital.service.model.HospitalCompany;
import com.mytijian.order.base.service.model.OrderRefundApplyPayDetail;
import com.mytijian.resource.model.Hospital;

/**
 * @author weifeng
 * @date 2017/8/4
 */
public class OrderRefundApplyVO {
    private String orderNum;
    private Date applyTime;
    private Account accountInfo;
    private Hospital fromSiteInfo;
    private HospitalCompany hospitalCompanyInfo;
    private Integer amount;
    private Integer refundType;
    private OrderRefundApplyPayDetail payDetail;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
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

    public Integer getRefundType() {
        return refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }
}
