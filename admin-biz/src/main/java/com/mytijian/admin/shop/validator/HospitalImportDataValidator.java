package com.mytijian.admin.shop.validator;

import com.mytijian.admin.shop.model.HospitalDataValidateResult;
import com.mytijian.admin.shop.model.HospitalImportData;

import java.util.List;

public interface HospitalImportDataValidator {

    /**
     * 体检中心数据校验接口
     * @param HospitalInfos
     * @return
     */
    HospitalDataValidateResult validateHospitalFileData(List<HospitalImportData> HospitalInfos,Integer brandId);
}
