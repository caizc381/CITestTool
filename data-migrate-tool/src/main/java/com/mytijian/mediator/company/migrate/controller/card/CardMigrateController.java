package com.mytijian.mediator.company.migrate.controller.card;

import com.mytijian.mediator.company.migrate.service.card.CardBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

/**
 * Created by mawanqun on 17/5/25.
 */
@RestController
public class CardMigrateController {
    private Logger logger = LoggerFactory.getLogger(CardMigrateController.class);

    @Autowired
    private CardBaseService cardBatchService;

    @RequestMapping(value = "/cardBatchMigrate", method = RequestMethod.GET)
    public String cardBatchMigrate(){
        return begin("tb_card_batch");
    }

    @RequestMapping(value = "/cardExamNoteMigrate", method = RequestMethod.GET)
    public String cardExamNoteMigrate(){
        return begin("tb_card_exam_note");
    }

    @RequestMapping(value = "/managerCardRelationMigrate", method = RequestMethod.GET)
    public String managerCardRelationMigrate(){
        return begin("tb_manager_card_relation");
    }

    private String begin(String tableName){
        long start = Calendar.getInstance().getTimeInMillis();

        for (; cardBatchService.migrate(tableName); ) {
            continue;
        }

        String info = tableName +" "
                + (Calendar.getInstance().getTimeInMillis() - start) + "ms";

        logger.info(info);

        return info;
    }

}
