/*
 * Copyright 2016 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.web.common.validator;
/**
 * 
 * @author yuefengyang
 *
 */
public class ValidationResult {
    
    /**
     * 是否通过
     */
    private boolean pass;
    
    /**
     * 失败原因
     */
    private String  failedReason;

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

}