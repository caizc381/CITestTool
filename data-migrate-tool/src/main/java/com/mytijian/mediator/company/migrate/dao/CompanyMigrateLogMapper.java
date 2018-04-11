package com.mytijian.mediator.company.migrate.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.CompanyMigrateLogDO;

@Repository("companyMigrateLogMapper")
public interface CompanyMigrateLogMapper {
	CompanyMigrateLogDO selectById(@Param("id") Integer id);
	
	CompanyMigrateLogDO selectByTableName(@Param("tableName") String tableName);

	void updateLastPrimaryKeyIdByTableName(
			@Param("tableName") String tableName,
			@Param("lastPrimaryKey") Integer lastPrimaryKey);

	void updateInitDone(@Param("initDone") String initDone);

	void updateMigrateDone(@Param("migrateDone") String migrateDone);
}
