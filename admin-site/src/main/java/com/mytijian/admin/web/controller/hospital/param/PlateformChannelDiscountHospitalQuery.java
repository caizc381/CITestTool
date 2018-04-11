package com.mytijian.admin.web.controller.hospital.param;


import com.mytijian.pulgin.mybatis.pagination.Page;

public class PlateformChannelDiscountHospitalQuery {

    /**
     * 地址id
     */
    private Integer addressId;

    /**
     * 医院id
     */
    private Integer hospitalId;


    private Page page;

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

}
