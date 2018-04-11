package com.mytijian.mediator.company.migrate.dao.user;

import com.mytijian.mediator.company.migrate.dao.dataobj.user.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/5/12.
 */
@Repository
public interface RoleMapper {
    /**
     * 根据accountId获得role列表
     *
     * @param accountId
     * @return
     */
    List<Role> getAccountRoles(@Param(value = "accountId") Integer accountId);

}
