package com.mytijian.admin.web.vo.resource;


import com.mytijian.offer.examitem.constant.enums.UpdateItemStatusEnum;

import java.io.Serializable;

/**
 * 单项更新任务结果
 * 类ExamitemUpdateResult.java的实现描述：TODO 类实现描述 
 * @author yuefengyang 2017年9月29日 下午5:21:40
 */
public class ExamitemUpdateResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8747075902726707286L;

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
