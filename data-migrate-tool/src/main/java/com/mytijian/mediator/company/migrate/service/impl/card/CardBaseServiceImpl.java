package com.mytijian.mediator.company.migrate.service.impl.card;

import com.mytijian.company.migrate.service.constant.CompanyOperationConstants;
import com.mytijian.mediator.company.migrate.dao.card.CardBaseMapper;
import com.mytijian.mediator.company.migrate.dao.card.CardMigrateLogMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.card.CardBase;
import com.mytijian.mediator.company.migrate.dao.dataobj.card.CardMigrateLogDo;
import com.mytijian.mediator.company.migrate.service.card.CardBaseService;
import com.mytijian.mediator.company.migrate.service.impl.user.helpser.UserHelper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mawanqun on 17/5/25.
 */
@Service
public class CardBaseServiceImpl implements CardBaseService {
    private Logger logger = LoggerFactory.getLogger(CardBaseServiceImpl.class);

    @Autowired
    private CardMigrateLogMapper cardMigrateLogMapper;

    @Autowired
    private CardBaseMapper cardBaseMapper;

    @Autowired
    private UserHelper userHelper;

    @Override
    public boolean migrate(String tableName) {
        CardMigrateLogDo cardMigrateLogDo = cardMigrateLogMapper.selectByTableName(tableName);
        if (CompanyOperationConstants.DONE.equals(cardMigrateLogDo.getMigrateDone())) {
            logger.info(tableName + "历史数据已迁移完成");
            return false;
        }
        List<CardBase> cardBases = selectCardBasesNewCompanyIdIsNull(cardMigrateLogDo.getLastPrimaryKeyId() + 1, cardMigrateLogDo.getLimitSize(),tableName);
        userHelper.setCardBaseNewCompanyIdAndOrganizationType(cardBases);
        if (CollectionUtils.isEmpty(cardBases)) {
            cardMigrateLogMapper.updateMigrateDone("done", tableName);
            return false;
        }
        for (CardBase cardBase : cardBases) {
            updateCardBase(cardBase, tableName);
        }
        cardMigrateLogMapper.updateLastPrimaryKeyIdByTableName(tableName,
                cardBases.get(cardBases.size() - 1).getId());
        return true;
    }

    private boolean isValid(CardBase cardBase) {
        return cardBase.getNewCompanyId() != null || cardBase.getOrganizationType() != null;
    }

    private List<CardBase> selectCardBasesNewCompanyIdIsNull(Integer nowId, Integer limit, String tableName) {
        if(tableName.equals("tb_card_batch")){
            return cardBaseMapper.selectCardBatchesNewCompanyIdIsNull(nowId, limit);
        }
        if(tableName.equals("tb_card_exam_note")){
            return cardBaseMapper.selectCardExamNoteNewCompanyIdIsNull(nowId, limit);
        }
        if(tableName.equals("tb_manager_card_relation")){
            return cardBaseMapper.selectManagerCardRelationNewCompanyIdIsNull(nowId, limit);
        }

        return null;
    }

    private void updateCardBase(CardBase cardBase, String tableName){

        if(isValid(cardBase)){
            if(tableName.equals("tb_card_batch")){
                cardBaseMapper.updateCardBatch(cardBase);
            }
            if(tableName.equals("tb_card_exam_note")){
                cardBaseMapper.updateCardExamNote(cardBase);
            }
            if(tableName.equals("tb_manager_card_relation")){
                cardBaseMapper.updateManagerCardRelation(cardBase);
            }
        }
    }

}
