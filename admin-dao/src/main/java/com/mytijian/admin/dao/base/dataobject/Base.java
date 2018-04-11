package com.mytijian.admin.dao.base.dataobject;

import java.io.Serializable;
import java.util.Date;

public class Base implements Serializable {
	
	private static final long serialVersionUID = 4930614798262974429L;

	/**
	 * 主键
	 */
	private Integer id;
	
	/**
	 * 创建时间
	 */
	private Date gmtCreated;
	
	/**
	 * 修改时间
	 */
	private Date gmtModified;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getGmtCreated() {
		return gmtCreated;
	}

	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	
}
