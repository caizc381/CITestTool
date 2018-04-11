package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;
import java.util.Date;

/**
 * 单位表操作日志
 * @author yuefengyang
 *
 */
public class CompanyOperationLogDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4950895955311942997L;

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
	 * 表名
	 */
	private String tableName;

	/**
	 * 主键id
	 */
	private Integer tablePrimaryKeyId;

	/**
	 * update、insert、delete
	 */
	private String operationType;

	/**
	 * todo / done
	 */
	private String completed;
	
	/**
	 * 操作接口
	 */
	private String interfaceName;

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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getTablePrimaryKeyId() {
		return tablePrimaryKeyId;
	}

	public void setTablePrimaryKeyId(Integer tablePrimaryKeyId) {
		this.tablePrimaryKeyId = tablePrimaryKeyId;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getCompleted() {
		return completed;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
}
