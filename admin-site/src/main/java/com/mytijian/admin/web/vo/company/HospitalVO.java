package com.mytijian.admin.web.vo.company;

import com.mytijian.resource.model.Hospital;

public class HospitalVO extends Hospital {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2716600284036993022L;
	private String pinyin;

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

}
