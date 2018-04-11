package com.mytijian.admin.api.rbac.model;

import java.io.Serializable;
import java.util.List;


/**
 * 权限
 * @author mytijian
 *
 */
public class Menu implements Serializable {

	private static final long serialVersionUID = -7672835076091868833L;

	private Integer id;
	
	/**
	 * 资源名称
	 */
	private String menuName;
	
	/**
	 * 授权标识
	 */
	private String perms;
	
	/**
	 * 资源路径
	 */
	private String menuUrl;
	
	/**
	 * 资源描述
	 */
	private String description;
	
	/**
	 * 资源父级Id
	 */
	private Integer parentId;
	
	/**
	 * 完整父级
	 */
    private List<Integer> parentIds;
	
	/**
	 * @see MenuTypeEnum
	 */
	private Integer menuType;
	
	/**
	 * 权重
	 */
	private Integer seq;
	
	/**
	 * 状态 @see ResourceEnum
	 */
	private Integer status;
	
	private List<?> list;
	
	private Boolean open;
	
	private String parentName;
	
	/**
	 * 图标
	 */
	private String menuIcon;

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPerms() {
		return perms;
	}

	public void setPerms(String perms) {
		this.perms = perms;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMenuType() {
		return menuType;
	}

	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}

	public List<Integer> getParentIds() {
		return parentIds;
	}

	public void setParentIds(List<Integer> parentIds) {
		this.parentIds = parentIds;
	}
	
}
