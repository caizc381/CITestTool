package com.mytijian.mediator.company.migrate.dao.order;

import com.mytijian.mediator.company.migrate.dao.dataobj.order.OrderMigrateLogDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/5/10.
 */
@Repository
public interface OrderMigrateLogMapper {
    OrderMigrateLogDO selectByTableName(@Param("tableName") String tableName);

    void updateLastPrimaryKeyIdByTableName(
            @Param("tableName") String tableName,
            @Param("lastPrimaryKey") Integer lastPrimaryKey);

    void updateInitDone(@Param("initDone") String initDone);

    void updateMigrateDone(@Param("migrateDone") String migrateDone, @Param("tableName") String tableName);
}
