package com.mytijian.mediator.company.migrate.service.impl.order;

import com.alibaba.fastjson.JSON;
import com.mytijian.mediator.company.migrate.dao.ChannelCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.ChannelCompanyDO;
import com.mytijian.mediator.company.migrate.dao.order.OrderMapper;
import com.mytijian.order.base.snapshot.model.ChannelExamCompanySnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by king on 2017/9/4.
 */
@Service
public class OrderDetailMigrateService {

    private Logger logger = LoggerFactory.getLogger(OrderDetailMigrateService.class);

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private ChannelCompanyMapper channelCompanyMapper;

    public String migrate() {

        int count=0;

        try {

            //select order_num FROM tb_exam_order_details  WHERE  `channel_company`  = 'null'
            //获取所有channelCompany 为空的订单
            List<String> strings = orderMapper.selectOrderExamDetail();
            if (CollectionUtils.isEmpty(strings)) {
                logger.info("当前订单体检详情表中没有channelCompany 为空的");
            }
            for (String orderNum : strings) {
                logger.info("当前处理的订单号为orderNum={}", orderNum);
                Integer channelCompanyId = orderMapper.selectChannelCompanyId(orderNum);
                if (channelCompanyId == null) {
                    logger.info("当前处理的订单号为orderNum={},订单表中channelCompanyId为空", orderNum);
                } else {
                    ChannelExamCompanySnapshot channelExamCompanySnapshot = new ChannelExamCompanySnapshot();
                    ChannelCompanyDO channelCompanyDO = channelCompanyMapper.selectById(channelCompanyId);
                    if (channelCompanyDO != null) {
                        BeanUtils.copyProperties(channelCompanyDO, channelExamCompanySnapshot);
                    }
                    channelExamCompanySnapshot.setId(channelCompanyId);
                    String channelCompany = JSON.toJSONString(channelExamCompanySnapshot);
                    orderMapper.updateOrderDetail(orderNum, channelCompany);
                    logger.info("更新订单号{}的channelCompany为{}", orderNum, channelCompany);
                }
                count++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("更新失败", ex);
        }

      return String.valueOf(count);
    }
}
