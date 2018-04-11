package com.mytijian.admin.dao.address.dataobject;

import java.io.Serializable;

/**
 * 区域
 * @author mytijian
 *
 */
public class AreaDO implements Serializable {
	
	private static final long serialVersionUID = 7586641865083605961L;

	/**
	 *  区域Id
	 */
	private Integer id;
	
	/**
	 * 区域名称
	 */
	private String areaName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
}
