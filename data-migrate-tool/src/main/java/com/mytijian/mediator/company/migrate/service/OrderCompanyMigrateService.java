package com.mytijian.mediator.company.migrate.service;

import java.sql.SQLException;
/**
 * 数据迁移接口使用后会删除
 * @author Administrator
 *
 */
public interface OrderCompanyMigrateService {

	/**
	 * 迁移订单单位数据，为了支持灰度发布，体检中心id可以配置，体检中心多个体检中心id通过逗号（英文）分隔
	 * @param migratehspIds 需要迁移医院id，可以是All，代表全部医院，但是此时notMigratehspIds不能为空
	 * @param notMigratehspIds 不要迁移医院id列表
	 * @return
	 * @throws SQLException
	 */
	public String migrateData(String migratehspIds, String notMigratehspIds) throws SQLException;


	/**
	 * 数据迁移：平台客户经理和渠道订单相关的数据【如果机构来源是渠道的那么对应的客户经理的from_site更新为渠道的】
	 * @param
	 */
	String migratePlatformManagerData();

	/**
	 * 迁移mysql和mongo的订单表的orderManagerId
	 * @return
	 */
	String orderManagerIdMigrate();

	/**
	 * 迁移mysql和mongo的订单表的chanelCompany
	 * @return
	 */
	String orderCompanyMigrate();
}
