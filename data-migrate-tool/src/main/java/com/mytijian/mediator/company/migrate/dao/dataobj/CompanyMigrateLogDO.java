package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;
import java.util.Date;

public class CompanyMigrateLogDO implements Serializable {
	public static Integer ID = 1;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2936271871341229160L;

	private Integer id;

	/**
	 * 表名 tb_exam_company
	 */
	private String tableName;

	/**
	 * 最小的主键id
	 */
	private Integer minPrimaryKeyId;

	/**
	 * 已读取的上一个主键id
	 */
	private Integer lastPrimaryKeyId;

	/**
	 * 每次读取多少条
	 */
	private Integer limitSize;

	private Date gmtCreated;

	private Date gmtModified;

	/*
	 * 初始化是否完成
	 */
	private String initDone;

	/*
	 * 历史数据是否迁移完成
	 */
	private String migrateDone;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getMinPrimaryKeyId() {
		return minPrimaryKeyId;
	}

	public void setMinPrimaryKeyId(Integer minPrimaryKeyId) {
		this.minPrimaryKeyId = minPrimaryKeyId;
	}

	public Integer getLastPrimaryKeyId() {
		return lastPrimaryKeyId;
	}

	public void setLastPrimaryKeyId(Integer lastPrimaryKeyId) {
		this.lastPrimaryKeyId = lastPrimaryKeyId;
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

	public Integer getLimitSize() {
		return limitSize;
	}

	public void setLimitSize(Integer limitSize) {
		this.limitSize = limitSize;
	}

	public String getInitDone() {
		return initDone;
	}

	public void setInitDone(String initDone) {
		this.initDone = initDone;
	}

	public String getMigrateDone() {
		return migrateDone;
	}

	public void setMigrateDone(String migrateDone) {
		this.migrateDone = migrateDone;
	}

}
