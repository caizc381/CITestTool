package com.mytijian.mediator.company.migrate.controller.user;

import com.mytijian.mediator.company.migrate.service.user.AccountRelationshipFailService;
import com.mytijian.mediator.company.migrate.service.user.AccountRelationshipService;
import com.mytijian.mediator.company.migrate.service.user.ImportGroupIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/5/10.
 */
@RestController
public class UserMigrateController {
    private Logger logger = LoggerFactory.getLogger(UserMigrateController.class);

    @Autowired
    private AccountRelationshipService accountRelationshipService;
    @Autowired
    private AccountRelationshipFailService accountRelationshipFailService;
    @Autowired
    private ImportGroupIdService importGroupIdService;

    @RequestMapping(value = "/accountRelationshipMigrate", method = RequestMethod.GET)
    public String accountRelationshipMigrate() {
        long start = Calendar.getInstance().getTimeInMillis();

        for (; accountRelationshipService.migrate(); ) {
            continue;
        }

        String info = "--> tb_account_relationship migrate done : "
                + (Calendar.getInstance().getTimeInMillis() - start) + "ms";

        logger.info(info);

        return info;
    }

    @RequestMapping(value = "/accountRelationshipFailMigrate", method = RequestMethod.GET)
    public String accountRelationshipFailMigrate() {
        long start = Calendar.getInstance().getTimeInMillis();

        for (; accountRelationshipFailService.migrate(); ) {
            continue;
        }

        String info = "--> tb_account_relationship_fail migrate done : "
                + (Calendar.getInstance().getTimeInMillis() - start) + "ms";

        logger.info(info);

        return info;
    }

    @RequestMapping(value = "/importGroupSeqMigrate", method = RequestMethod.GET)
    public String importGroupSeqMigrate() {
        long start = Calendar.getInstance().getTimeInMillis();
        for (; importGroupIdService.migrate(); ) {
            continue;
        }

        String info = "tb_import_group_seq migrate done : "
                + (Calendar.getInstance().getTimeInMillis() - start) + "ms";

        logger.info(info);

        return info;
    }

    @RequestMapping("/test")
    public String test() {
        return "success";
    }
}
