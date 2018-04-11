package com.mytijian.mediator.company.migrate.service.card;

/**
 * Created by mawanqun on 17/5/25.
 */
public interface CardBaseService {
    /**
     * 数据迁移
     * @return
     */
    boolean migrate(String tableName);
}
