package com.mytijian.mediator.company.migrate.dao.card;

import com.mytijian.mediator.company.migrate.dao.dataobj.card.CardBase;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mawanqun on 17/5/25.
 */
@Repository
public interface CardBaseMapper {

    List<CardBase> selectCardBatchesNewCompanyIdIsNull(@Param("nowId") Integer nowId, @Param(value = "limit") Integer limit);

    int updateCardBatch(CardBase cardBase);

    List<CardBase> selectCardExamNoteNewCompanyIdIsNull(@Param("nowId") Integer nowId, @Param(value = "limit") Integer limit);

    int updateCardExamNote(CardBase cardBase);

    List<CardBase> selectManagerCardRelationNewCompanyIdIsNull(@Param("nowId") Integer nowId, @Param(value = "limit") Integer limit);

    int updateManagerCardRelation(CardBase cardBase);
}

