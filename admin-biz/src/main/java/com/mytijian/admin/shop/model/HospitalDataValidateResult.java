package com.mytijian.admin.shop.model;

import java.util.List;

public class HospitalDataValidateResult {

    /**
     * 是否有错误
     */
    private boolean isHaveError;
    /**
     * 错误体检中心信息
     */
    private List<ErrorHospital> errorHospitalInfos;

    public boolean isHaveError() {
        return isHaveError;
    }

    public void setHaveError(boolean isHaveError) {
        this.isHaveError = isHaveError;
    }

    public List<ErrorHospital> getErrorHospitalInfos() {
        return errorHospitalInfos;
    }

    public void setErrorHospitalInfos(List<ErrorHospital> errorHospitalInfos) {
        this.errorHospitalInfos = errorHospitalInfos;
    }

}
