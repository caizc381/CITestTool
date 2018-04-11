package com.mytijian.admin.web.vo.hospital;

import java.io.Serializable;

import com.mytijian.offer.examitem.constant.enums.UpdateItemStatusEnum;


public class ExamitemUpdateResult implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3846528194577222012L;

	private UpdateItemStatusEnum status;

	private String message;

	public UpdateItemStatusEnum getStatus() {
		return status;
	}

	public void setStatus(UpdateItemStatusEnum status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
