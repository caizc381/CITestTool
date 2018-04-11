package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;

public class ExamCompanyDO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1822989263875394779L;

	private Integer id;
	
	private String name;
	
	private String pinyin;
	
	private Integer type;
	
	private String prefix;
	
	private String description;
	
	private Integer referenceCountByHosp;

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getReferenceCountByHosp() {
		return referenceCountByHosp;
	}

	public void setReferenceCountByHosp(Integer referenceCountByHosp) {
		this.referenceCountByHosp = referenceCountByHosp;
	}

	
	
	
	
}
