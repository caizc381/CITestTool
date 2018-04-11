package com.mytijian.admin.dao.rbac.dataobject;

import java.io.Serializable;

import com.mytijian.admin.dao.base.dataobject.Base;


/**
 * 角色
 * @author mytijian
 *
 */
public class RoleDO extends Base implements Serializable{
	
	private static final long serialVersionUID = 453719526956652349L;
	
	/**
	 * 角色名称
	 */
	private String roleName;
	
	/**
	 * 角色描述
	 */
	private String description;
	
	/**
	 * 排序
	 */
	private Integer seq;
	
	/**
	 * 状态 @see RoleEnum
	 */
	private Integer status;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
}
