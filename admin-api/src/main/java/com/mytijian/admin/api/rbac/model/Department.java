package com.mytijian.admin.api.rbac.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 部门
 * @author feng
 *
 */
public class Department implements Serializable {
	
	private static final long serialVersionUID = 6006078287876251492L;

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
	
	/**
	 * 部门名称
	 */
	private String departmentName;
	
	/**
	 * 上级部门Id， 0:顶层目录
	 */
	private Integer parentId;
	
	/**
	 * 所属上级Id列表
	 */
	private List<Integer> parentIdList;
	
	/**
	 *  区域Id
	 */
	private Integer areaId;
	
	/**
	 * 省份Id
	 */
	private Integer provinceId;
	
	private List<?> list;

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

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public List<Integer> getParentIdList() {
		return parentIdList;
	}

	public void setParentIdList(List<Integer> parentIdList) {
		this.parentIdList = parentIdList;
	}
	
}
