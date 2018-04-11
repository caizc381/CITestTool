package com.mytijian.mediator.company.migrate.dao.user;

import com.mytijian.account.model.Account;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/5/11.
 */
@Repository
public interface UserHelperMapper {
    Integer selectHospitalIdByManagerId(@Param("managerId") Integer managerId);

    Integer selectHospitalIdmt();

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    Account getAccountById(@Param("id") Integer id);

}
