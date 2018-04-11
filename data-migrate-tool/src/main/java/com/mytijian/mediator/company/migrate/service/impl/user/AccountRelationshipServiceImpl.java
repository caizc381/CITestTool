package com.mytijian.mediator.company.migrate.service.impl.user;

import com.mytijian.company.migrate.service.constant.CompanyOperationConstants;
import com.mytijian.mediator.company.migrate.constant.UserConstant;
import com.mytijian.mediator.company.migrate.dao.HospitalMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.user.AccountRelation;
import com.mytijian.mediator.company.migrate.dao.dataobj.user.UserMigrateLogDO;
import com.mytijian.mediator.company.migrate.dao.user.AccountRelationshipMapper;
import com.mytijian.mediator.company.migrate.dao.user.UserMigrateLogMapper;
import com.mytijian.mediator.company.migrate.service.impl.user.helpser.UserHelper;
import com.mytijian.mediator.company.migrate.service.user.AccountRelationshipService;
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
public class AccountRelationshipServiceImpl implements AccountRelationshipService {
    private Logger logger = LoggerFactory.getLogger(AccountRelationshipService.class);
    @Autowired
    private AccountRelationshipMapper accountRelationshipMapper;
    @Autowired
    private HospitalMapper hospitalMapper;
    @Autowired
    private UserMigrateLogMapper userMigrateLogMapper;
    @Autowired
    private UserHelper userHelper;

    @Override
    public boolean migrate() {
        UserMigrateLogDO migLog = userMigrateLogMapper
                .selectByTableName(UserConstant.TB_ACCOUNT_RELATIONSHIP);
        if (CompanyOperationConstants.DONE.equals(migLog.getMigrateDone())) {
            System.out.println(UserConstant.TB_ACCOUNT_RELATIONSHIP + "历史数据已迁移完成");
            logger.info(UserConstant.TB_ACCOUNT_RELATIONSHIP + "历史数据已迁移完成");
            return false;
        }
        List<AccountRelation> accountRelations = selectAccountRelationshipsExamCompanyIdIsNull(migLog.getLastPrimaryKeyId() + 1, migLog.getLimitSize());
        if (CollectionUtils.isEmpty(accountRelations)) {
            // 迁移完毕
            userMigrateLogMapper.updateMigrateDone("done", UserConstant.TB_ACCOUNT_RELATIONSHIP);
            return false;
        }
        userHelper.setExamCompanyIdAndOrganizationId(accountRelations);
        setOrganizationType(accountRelations);
        for (AccountRelation accountRelation : accountRelations) {
            if (isValid(accountRelation))
                accountRelationshipMapper.updateAccountRelationship(accountRelation);
        }
        // 更新lastpkid
        userMigrateLogMapper.updateLastPrimaryKeyIdByTableName(
                UserConstant.TB_ACCOUNT_RELATIONSHIP,
                accountRelations.get(accountRelations.size() - 1).getId());
        return true;
    }

    private boolean isValid(AccountRelation accountRelation) {
        return accountRelation.getNewCompanyId() != null || accountRelation.getOrganizationType() != null || accountRelation.getOrganizationId() != null;
    }

    private List<AccountRelation> selectAccountRelationshipsExamCompanyIdIsNull(Integer nowId, Integer limit) {
        return accountRelationshipMapper.selectAccountRelationshipsExamCompanyIdIsNull(nowId, limit);
    }

//    private void setExamCompanyIdAndOrganizationId(List<AccountRelation> accountRelations) {
//        accountRelations.parallelStream().forEach(accountRelation -> {
//            HospitalCompanyDO hospitalCompanyDO = null;
//            if (accountRelation.getCompanyId() == UserConstant.GUEST_COMPANY_ID) {
//                List<HospitalCompanyDO> hospitalCompanyDOS = hospitalCompanyMapper.selectByExamCompanyId(accountRelation.getCompanyId());
//                if (CollectionUtils.isNotEmpty(hospitalCompanyDOS)) {
//                    hospitalCompanyDO = hospitalCompanyDOS.get(0);
//                }
//            } else {
//                Integer hospitalId = userHelper.getHospitalIdByManagerId(accountRelation.getManagerId());
//                if (hospitalId != null) {
//                    hospitalCompanyDO = hospitalCompanyMapper.selectByExamCompanyIdAndOrganizationId(UserConstant.TB_EXAM_COMPANY_ID_GR, hospitalId);
//                }
//            }
//
//            if (hospitalCompanyDO != null) {
//                accountRelation.setExamCompanyId(hospitalCompanyDO.getId());
//                accountRelation.setOrganizationId(hospitalCompanyDO.getOrganizationId());
//            }
//        });
//    }

    public void setOrganizationType(List<AccountRelation> accountRelations) {
        accountRelations.parallelStream().forEach(accountRelation -> {
            HospitalDO hospitalDO = hospitalMapper.selectBaseInfoByHospId(accountRelation.getOrganizationId());
            if (hospitalDO != null) {
                accountRelation.setOrganizationType(hospitalDO.getOrganizationType());
            }
        });
    }
}
