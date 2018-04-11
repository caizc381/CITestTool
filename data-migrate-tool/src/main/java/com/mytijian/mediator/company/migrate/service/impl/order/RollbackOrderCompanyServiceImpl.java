package com.mytijian.mediator.company.migrate.service.impl.order;

import com.mytijian.mediator.company.migrate.dao.OrderCompanyMigrateDAO;
import com.mytijian.mediator.company.migrate.service.RollbackOrderCompanyService;
import com.mytijian.order.base.snapshot.model.ChannelExamCompanySnapshot;
import com.mytijian.order.model.MongoOrder;
import com.mytijian.order.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by king on 2017/6/29.
 */
@Service
public class RollbackOrderCompanyServiceImpl implements RollbackOrderCompanyService {

    Logger log = LoggerFactory.getLogger(RollbackOrderCompanyServiceImpl.class);

    @Resource(name = "orderCompanyMigrateDAO")
    OrderCompanyMigrateDAO dao;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void rollback(Integer hospitalId) throws SQLException {

        //获取医院更新的所有订单
        log.info("开始调用回滚接口");
        Set<Order> strings = dao.selectOrderNum(hospitalId);
        log.info("总共有那么多订单",strings.size());
        for (Order string : strings) {
            if (string.getExamCompanyId() == null || string.getOrderNum() == null || string.getOrderNum().isEmpty()) {
                continue;
            }
            log.info("当前订单号码orderNum{}",string.getOrderNum());
            //更新details
            dao.updateOrderDetail(string.getOrderNum(), string.getExamCompanyId());
            //更新
            Query query = new Query(Criteria.where("orderNum").is(string.getOrderNum()));
            MongoOrder mongoOrder = mongoTemplate.findOne(query, MongoOrder.class, "mongoOrder");
            if (mongoOrder != null) {
                mongoOrder.setExamCompanyId(mongoOrder.getOldExamCompanyId());
                ChannelExamCompanySnapshot channelCompanySnapshot = new ChannelExamCompanySnapshot();
                mongoOrder.setChannelCompany(channelCompanySnapshot);
                mongoTemplate.save(mongoOrder);
            }
        }
        log.info("调用接口结束");
    }

}
