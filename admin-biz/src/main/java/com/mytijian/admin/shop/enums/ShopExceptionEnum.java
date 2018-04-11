package com.mytijian.admin.shop.enums;

import com.mytijian.base.result.ErrorInfo;

/**
 * EX_1_0_RESOURCE_01_01_001
 * EX：Exception缩写，表示异常
 * 1：表示异常类型（0：系统异常，1：业务异常）
 * 0：表示日志级别（0：error，1：warn，2：info）
 * RESOURCE：模块名称
 * 01：子模块序号  ： 01 机构
 * 01：业务场景序号，例如，01：机构医院关系
 * 001：异常的序号
 */
public enum ShopExceptionEnum implements ErrorInfo {

    HOSOPITAL_IMPORT_FILE_ERROR("EX_1_0_shop_01_01_001", "体检中心导入文件解析错误");

    private String errorCode;

    private String errorMsg;


    private ShopExceptionEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
