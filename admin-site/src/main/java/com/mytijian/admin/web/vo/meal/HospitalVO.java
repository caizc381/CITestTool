package com.mytijian.admin.web.vo.meal;

import java.io.Serializable;
/**
 * 根据标准套餐来创建平台套餐时，显示的体检中心列表页
 * @author yuefengyang
 *
 */
public class HospitalVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8418736323538539875L;

	private Integer id;

	private String name;

	private String pinyin;

	/**
	 * 是否已经关联标准套餐
	 */
	private Boolean relatedStandardMeal;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public Boolean getRelatedStandardMeal() {
		return relatedStandardMeal;
	}

	public void setRelatedStandardMeal(Boolean relatedStandardMeal) {
		this.relatedStandardMeal = relatedStandardMeal;
	}

}
