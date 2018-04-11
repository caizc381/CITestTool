/*
 * Copyright 2018 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.web.vo.examitem;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

/**
 * 类ExamItemImportVO.java的实现描述：TODO 类实现描述 
 * @author csj 2018年1月30日 下午2:53:08
 */
public class ExamItemImportVO {

	private Integer hospitalId;

	private Integer orgBandId;

	private MultipartFile file;

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getOrgBandId() {
		return orgBandId;
	}

	public void setOrgBandId(Integer orgBandId) {
		this.orgBandId = orgBandId;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}
