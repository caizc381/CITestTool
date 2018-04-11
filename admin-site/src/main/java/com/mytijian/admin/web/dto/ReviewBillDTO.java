package com.mytijian.admin.web.dto;

import java.io.Serializable;

public class ReviewBillDTO implements Serializable {

    private String sn;

    private Long platformActurallyPayAmount;

    private String remark;

    private Long discountAmount;

    private Long consumeQuotaAmount;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Long getPlatformActurallyPayAmount() {
        return platformActurallyPayAmount;
    }

    public void setPlatformActurallyPayAmount(Long platformActurallyPayAmount) {
        this.platformActurallyPayAmount = platformActurallyPayAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Long getConsumeQuotaAmount() {
        return consumeQuotaAmount;
    }

    public void setConsumeQuotaAmount(Long consumeQuotaAmount) {
        this.consumeQuotaAmount = consumeQuotaAmount;
    }
}
