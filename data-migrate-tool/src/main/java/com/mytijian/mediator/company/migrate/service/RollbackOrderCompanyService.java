package com.mytijian.mediator.company.migrate.service;

import java.sql.SQLException;

/**
 * Created by king on 2017/6/29.
 */
public interface RollbackOrderCompanyService {


    void rollback(Integer hospitalId) throws SQLException;
}
