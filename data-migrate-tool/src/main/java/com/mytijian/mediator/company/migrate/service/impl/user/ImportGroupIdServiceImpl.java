package com.mytijian.mediator.company.migrate.service.impl.user;

import com.mytijian.company.migrate.service.constant.CompanyOperationConstants;
import com.mytijian.mediator.company.migrate.constant.UserConstant;
import com.mytijian.mediator.company.migrate.dao.dataobj.user.ImportGroupId;
import com.mytijian.mediator.company.migrate.dao.dataobj.user.UserMigrateLogDO;
import com.mytijian.mediator.company.migrate.dao.user.ImportGroupMapper;
import com.mytijian.mediator.company.migrate.dao.user.UserMigrateLogMapper;
import com.mytijian.mediator.company.migrate.service.impl.user.helpser.UserHelper;
import com.mytijian.mediator.company.migrate.service.user.ImportGroupIdService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 */
@Service
public class ImportGroupIdServiceImpl implements ImportGroupIdService {
    private Logger logger = LoggerFactory.getLogger(ImportGroupIdService.class);
    @Autowired
    private ImportGroupMapper importGroupMapper;
    @Autowired
    private UserMigrateLogMapper userMigrateLogMapper;
    @Autowired
    private UserHelper userHelper;

    @Override
    public boolean migrate() {
        UserMigrateLogDO migLog = userMigrateLogMapper
                .selectByTableName(UserConstant.TB_IMPORT_GROUP_SEQ);
        if (CompanyOperationConstants.DONE.equals(migLog.getMigrateDone())) {
            System.out.println(UserConstant.TB_IMPORT_GROUP_SEQ + "历史数据已迁移完成");
            logger.info(UserConstant.TB_IMPORT_GROUP_SEQ + "历史数据已迁移完成");
            return false;
        }
        List<ImportGroupId> importGroupIds = selectAccountRelationshipsExamCompanyIdIsNull(migLog.getLastPrimaryKeyId() + 1, migLog.getLimitSize());
        if (CollectionUtils.isEmpty(importGroupIds)) {
            // 迁移完毕
            userMigrateLogMapper.updateMigrateDone("done", UserConstant.TB_IMPORT_GROUP_SEQ);
            return false;
        }
        userHelper.setExamCompanyIdAndOrganizationType(importGroupIds);
        for (ImportGroupId importGroupId : importGroupIds) {
            if (isValid(importGroupId))
                importGroupMapper.updateImportGroupSeq(importGroupId);
        }
        // 更新lastpkid
        userMigrateLogMapper.updateLastPrimaryKeyIdByTableName(
                UserConstant.TB_IMPORT_GROUP_SEQ,
                importGroupIds.get(importGroupIds.size() - 1).getId());
        return true;
    }

    private boolean isValid(ImportGroupId importGroupId) {
        return importGroupId.getNewCompanyId() != null || importGroupId.getOrganizationType() != null;
    }

    private List<ImportGroupId> selectAccountRelationshipsExamCompanyIdIsNull(Integer nowId, Integer limit) {
        return importGroupMapper.selectimportGroupSeqsExamCompanyIdIsNull(nowId, limit);
    }
}
