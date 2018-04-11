package com.mytijian.admin.web.vo.syncmsg;

import com.mytijian.common.dto.SyncMessageDTO;

public class SyncMessageVO extends SyncMessageDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 操作人姓名
	 */
	private String operatorName;

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
	
}
