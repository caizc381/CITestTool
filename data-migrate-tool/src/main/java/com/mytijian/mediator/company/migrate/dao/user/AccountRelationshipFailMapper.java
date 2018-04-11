package com.mytijian.mediator.company.migrate.dao.user;

import com.mytijian.mediator.company.migrate.dao.dataobj.user.AccountRelationFail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */
@Repository
public interface AccountRelationshipFailMapper {
    /**
     * 查询tb_account_relationship表中new_company_id为null的记录
     * @return
     */
    List<AccountRelationFail> selectAccountRelationshipFailsExamCompanyIdIsNull(@Param("nowId") Integer nowId, @Param(value = "limit") Integer limit);

    /**
     * 填充tb_account_relationship表中new_company_id、organization_id、organization_type字段
     */
    int updateAccountFailRelationship(AccountRelationFail accountRelationFail);
}
