package com.mytijian.mediator.company.migrate.service.impl.user;

import com.mytijian.company.migrate.service.constant.CompanyOperationConstants;
import com.mytijian.mediator.company.migrate.constant.UserConstant;
import com.mytijian.mediator.company.migrate.dao.HospitalCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.HospitalMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.user.AccountRelationFail;
import com.mytijian.mediator.company.migrate.dao.dataobj.user.UserMigrateLogDO;
import com.mytijian.mediator.company.migrate.dao.user.AccountRelationshipFailMapper;
import com.mytijian.mediator.company.migrate.dao.user.UserMigrateLogMapper;
import com.mytijian.mediator.company.migrate.service.impl.user.helpser.UserHelper;
import com.mytijian.mediator.company.migrate.service.user.AccountRelationshipFailService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */
@Service
public class AccountRelationshipFailServiceImpl implements AccountRelationshipFailService {
    private Logger logger = LoggerFactory.getLogger(AccountRelationshipFailService.class);

    @Autowired
    private AccountRelationshipFailMapper accountRelationshipFailMapper;
    @Autowired
    private HospitalMapper hospitalMapper;
    @Autowired
    private UserMigrateLogMapper userMigrateLogMapper;
    @Autowired
    private UserHelper userHelper;

    @Override
    public boolean migrate() {
        UserMigrateLogDO migLog = userMigrateLogMapper
                .selectByTableName(UserConstant.TB_ACCOUNT_RELATIONSHIP_FAIL);

        if (CompanyOperationConstants.DONE.equals(migLog.getMigrateDone())) {
            System.out.println(UserConstant.TB_ACCOUNT_RELATIONSHIP_FAIL + "历史数据已迁移完成");
            logger.info(UserConstant.TB_ACCOUNT_RELATIONSHIP_FAIL + "历史数据已迁移完成");
            return false;
        }
        List<AccountRelationFail> accountRelationFails = selectAccountRelationshipFailsExamCompanyIdIsNull(migLog.getLastPrimaryKeyId() + 1, migLog.getLimitSize());
        if (CollectionUtils.isEmpty(accountRelationFails)) {
            // 迁移完毕
            userMigrateLogMapper.updateMigrateDone("done", UserConstant.TB_ACCOUNT_RELATIONSHIP_FAIL);
            return false;
        }
        userHelper.setExamCompanyIdAndOrganizationId(accountRelationFails);
        setOrganizationType(accountRelationFails);
        for (AccountRelationFail accountRelationFail : accountRelationFails) {
            if (isValid(accountRelationFail))
                accountRelationshipFailMapper.updateAccountFailRelationship(accountRelationFail);
        }
        // 更新lastpkid
        userMigrateLogMapper.updateLastPrimaryKeyIdByTableName(
                UserConstant.TB_ACCOUNT_RELATIONSHIP_FAIL,
                accountRelationFails.get(accountRelationFails.size() - 1).getId());
        return true;
    }

    private boolean isValid(AccountRelationFail accountRelationFail) {
        return accountRelationFail.getNewCompanyId() != null || accountRelationFail.getOrganizationType() != null || accountRelationFail.getOrganizationId() != null;
    }

    private List<AccountRelationFail> selectAccountRelationshipFailsExamCompanyIdIsNull(Integer nowId, Integer limit) {
        return accountRelationshipFailMapper.selectAccountRelationshipFailsExamCompanyIdIsNull(nowId, limit);
    }

    public void setOrganizationType(List<AccountRelationFail> accountRelationFails) {
        accountRelationFails.parallelStream().forEach(accountRelationFail -> {
            HospitalDO hospitalDO = hospitalMapper.selectBaseInfoByHospId(accountRelationFail.getOrganizationId());
            if (hospitalDO != null) {
                accountRelationFail.setOrganizationType(hospitalDO.getOrganizationType());
            }
        });
    }
}
