package com.mytijian.admin.dao.rbac.dataobject;

import java.io.Serializable;

import com.mytijian.admin.dao.base.dataobject.Base;


/**
 * 权限
 * @author mytijian
 *
 */
public class MenuDO extends Base implements Serializable {

	private static final long serialVersionUID = 4077677156241739868L;

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
	 * 权重
	 */
	private Integer seq;
	
	/**
	 * 状态 @see ResourceEnum
	 */
	private Integer status;
	
	/**
	 * 菜单类型 @see MenuEnum
	 */
	private Integer  menuType;
	
	private String parentName;
	
	private String menuIcon;

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
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

	public Integer getMenuType() {
		return menuType;
	}

	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}
	
}
