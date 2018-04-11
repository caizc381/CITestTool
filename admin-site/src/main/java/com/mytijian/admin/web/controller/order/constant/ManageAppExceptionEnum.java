package com.mytijian.admin.web.controller.order.constant;

import com.mytijian.exception.ErrorInfo;

/**
 *
 * EX_1_0_COMPANY_01_01_001
 * EX :Exception缩写，表示异常
 * 1：表示异常类型（0：系统异常，1：业务异常）
 * 0：表示日志级别（0：error，1：warn，2：info）
 * COMPANY :模块名称
 * 01:子模块序号
 * 01：业务场景序号
 * 001：异常需要
 * manage app controller Exception
 */
public enum ManageAppExceptionEnum implements ErrorInfo {

    ORDER_IDS_IS_EMPTY("EX_1_0_MANAGE_01_01_001","订单列表为空"),
    ORDER_SUM_IS_TOO_BIG("EX_1_0_MANAGE_01_01_002","最多导出查看15000条");

    private String errorCode;
    private String errorMsg;

    private ManageAppExceptionEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }
}
