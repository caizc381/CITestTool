package com.mytijian.mediator.company.migrate.dao.card;

import com.mytijian.mediator.company.migrate.dao.dataobj.card.CardMigrateLogDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/5/10.
 */
@Repository
public interface CardMigrateLogMapper {

    CardMigrateLogDo selectByTableName(@Param("tableName") String tableName);

    void updateLastPrimaryKeyIdByTableName(
            @Param("tableName") String tableName,
            @Param("lastPrimaryKey") Integer lastPrimaryKey);

    void updateMigrateDone(@Param("migrateDone") String migrateDone, @Param("tableName") String tableName);
}
