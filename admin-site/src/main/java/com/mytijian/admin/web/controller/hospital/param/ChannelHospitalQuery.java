package com.mytijian.admin.web.controller.hospital.param;


import com.mytijian.pulgin.mybatis.pagination.Page;

import java.util.List;

public class ChannelHospitalQuery {

    /**
     * 地址id
     */
    private Integer addressId;

    /**
     * 医院id
     */
    private Integer hospitalId;

    /**
     * 机构id
     */
    private Integer channelId;


    /**
     * 医院列表
     */
    private List<Integer> hospitalIds;


    /**
     * 平台是否显示
     */
    private Integer showInList;

    /**
     * 是否设置 1 设置散客折扣  0 未设置散客折扣
     */
    private Boolean guestDiscount;


    /**
     * 是否设置  1.设置单位折扣  0.未设置单位折扣
     */
    private Boolean companyDiscount;


    /**
     * 是否分配给渠道   1.未分配  0.已分配
     */
    private Boolean isCancel;

    /**
     * 状态是否可用  1.可用  2.不可用
     */
    private Integer status;


    private Page page;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getShowInList() {
        return showInList;
    }

    public void setShowInList(Integer showInList) {
        this.showInList = showInList;
    }

    public Boolean getGuestDiscount() {
        return guestDiscount;
    }

    public void setGuestDiscount(Boolean guestDiscount) {
        this.guestDiscount = guestDiscount;
    }

    public Boolean getCompanyDiscount() {
        return companyDiscount;
    }

    public void setCompanyDiscount(Boolean companyDiscount) {
        this.companyDiscount = companyDiscount;
    }

    public Boolean getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Boolean isCancel) {
        this.isCancel = isCancel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getHospitalIds() {
        return hospitalIds;
    }

    public void setHospitalIds(List<Integer> hospitalIds) {
        this.hospitalIds = hospitalIds;
    }

    @Override
    public String toString() {
        return "ChannelHospitalQuery{" +
                "addressId=" + addressId +
                ", hospitalId=" + hospitalId +
                ", channelId=" + channelId +
                ", hospitalIds=" + hospitalIds +
                ", showInList=" + showInList +
                ", guestDiscount=" + guestDiscount +
                ", companyDiscount=" + companyDiscount +
                ", isCancel=" + isCancel +
                ", status=" + status +
                ", page=" + page +
                '}';
    }
}
