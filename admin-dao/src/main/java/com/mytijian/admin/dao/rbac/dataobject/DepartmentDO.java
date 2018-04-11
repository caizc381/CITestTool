package com.mytijian.admin.dao.rbac.dataobject;

import java.io.Serializable;

import com.mytijian.admin.dao.base.dataobject.Base;


/**
 * 部门信息
 * @author mytijian
 *
 */
public class DepartmentDO extends Base implements Serializable {
	
	private static final long serialVersionUID = 7636218019515064841L;

	/**
	 * 部门名称
	 */
	private String departmentName;
	
	/**
	 * 上级部门Id， 0:顶层目录
	 */
	private Integer parentId;
	
	/**
	 *  区域Id
	 */
	private Integer areaId;
	
	/**
	 * 省份Id
	 */
	private Integer provinceId;

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

}
