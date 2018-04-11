package com.mytijian.mediator.company.migrate.service.impl.order;

import com.mytijian.account.model.Account;
import com.mytijian.mediator.company.migrate.constant.OrderConstant;
import com.mytijian.mediator.company.migrate.dao.ChannelCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.OrderCompanyMigrateDAO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ChannelCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.order.OrderDO;
import com.mytijian.mediator.company.migrate.dao.order.OrderMapper;
import com.mytijian.mediator.company.migrate.dao.order.OrderMigrateLogMapper;
import com.mytijian.mediator.company.migrate.dao.user.UserHelperMapper;
import com.mytijian.mediator.company.migrate.service.OrderCompanyMigrateService;
import com.mytijian.mediator.company.migrate.service.impl.user.helpser.UserHelper;
import com.mytijian.order.base.snapshot.model.ChannelExamCompanySnapshot;
import com.mytijian.order.base.snapshot.model.ManagerSnapshot;
import com.mytijian.order.enums.OrderSourceEnum;
import com.mytijian.order.model.MongoOrder;
import com.mytijian.order.model.Order;
import com.mytijian.pool.ThreadPoolManager;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.util.IdCardValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service("orderCompanyMigrateService")
public class OrderCompanyMigrateServiceImpl implements OrderCompanyMigrateService {
	
	Logger log = LoggerFactory.getLogger(OrderCompanyMigrateServiceImpl.class);
    private static final Integer ORDER_AMOUNT_PAGE_SIZE = 500;
	private ExecutorService executorService = ThreadPoolManager.newFixedThreadPool(15, 20);
	
	@Resource(name = "orderCompanyMigrateDAO")
	OrderCompanyMigrateDAO dao;
	
	@Resource
    private MongoTemplate mongoTemplate;
	@Resource
	private OrderMigrateLogMapper orderMigrateLogMapper;
	@Resource
	private UserHelper userHelper;
	@Resource
	private UserHelperMapper userHelperMapper;
	@Resource
    private ChannelCompanyMapper channelCompanyMapper;

    @Resource
    private OrderMapper orderMapper;

	private static AtomicInteger total = new AtomicInteger(0);
	private static AtomicInteger success = new AtomicInteger(0);
	private static AtomicInteger fail = new AtomicInteger(0);
	private static AtomicInteger noOps = new AtomicInteger(0);
	private static AtomicInteger i = new AtomicInteger(0);
	private static AtomicInteger num = new AtomicInteger(0);
	private static AtomicInteger orderId = new AtomicInteger(0);

	@Override
	public String migrateData(String migratehspIds, String notMigratehspIds) throws SQLException {
		StringBuilder failOrderIds = new StringBuilder();
		StringBuilder noOpsOrderIds = new StringBuilder();
		OrderCompanyMigrateDAO.OrderIterator orderIterator = dao.selectAllOrderNum(migratehspIds, notMigratehspIds);
		Set<Integer> platformManagerIdSet = dao.selectAllPlatformManager();
		Set<Integer> mCompanyId = dao.selectAllMCompanyId();
		Integer mt = userHelperMapper.selectHospitalIdmt();

		while (orderIterator.hasNext()) {
            Order order = orderIterator.next();
            Runnable runnable = () -> {
				process(failOrderIds, noOpsOrderIds, order, platformManagerIdSet, mCompanyId, mt);
			};
			executorService.submit(runnable);
		}

		executorService.shutdown();
		while (true){
			if (executorService.isTerminated()){
				orderMigrateLogMapper.updateLastPrimaryKeyIdByTableName(
						OrderConstant.TB_ORDER,
						orderId.intValue());
				// 迁移完毕
				orderMigrateLogMapper.updateMigrateDone("done", OrderConstant.TB_ORDER);

				String result = "total:" + total.get() + "， success:" + String.valueOf(success.get());
				if (failOrderIds.length() > 0) {
					result = result + "，fail:" + String.valueOf(fail.get()) + "，ids=" + failOrderIds.toString();
				}
				if (noOpsOrderIds.length() > 0) {
					result = result + "，not migrate:" + String.valueOf(noOps) + ",because new_company_id is empty，ids=" + noOpsOrderIds.toString();
				}
				return result;
			}
		}

	}

	private void process(StringBuilder failOrderIds, StringBuilder noOpsOrderIds, Order order, Set<Integer> platformManagerIdSet, Set<Integer> mCompanyId, Integer mt) {
		log.info("当前处理第{}",total.incrementAndGet());
		if (orderId.intValue() < order.getId()){
			orderId.set(order.getId());
		}
		try {
            Integer examCompanyId = order.getExamCompanyId();
            Integer managerId = order.getOwnerId()==null?order.getOperatorId():order.getOwnerId();
            if (managerId == null){
				return;
            }
            i.getAndIncrement();
            if (examCompanyId.equals(1585)){
                //单位是1585
                //HOSPITAL_GUEST_ONLINE(1, "个人网上预约",-100),
//					HOSPITAL_GUEST_OFFLINE(2, "现场散客",-101),
//							HOSPITAL_MTJK(3, "每天健康",-102);
                if (platformManagerIdSet.contains(managerId) || mt.equals(order.getFromSite())){
                    examCompanyId = -102;
                }else {
                    if (order.getSource() == OrderSourceEnum.CRM.getCode()){
                        examCompanyId = -101;
                    }else {
                        examCompanyId = -100;
                    }
                }

            }else{
                //m 单位
                if (mCompanyId.contains(examCompanyId)){
                    examCompanyId = -102;
                }
            }
            Integer newCompanyId = dao.selectHspCompanyId(examCompanyId, order.getHospital().getId());
            log.info("当前序号{}订单{}的examCompanyId ={},hospitalId={}，newcomapnyid={}",i,order.getId(),examCompanyId,order.getHospital().getId(),newCompanyId);
            if(newCompanyId != null){
                //平台客户经理，迁移渠道单位
                Integer channelCompanyId = null;
                ChannelCompanyDO channelCompanyDO = null;
                Integer mt1 = order.getFromSite() ==null?mt:order.getFromSite();
                if(platformManagerIdSet.contains(managerId) || mt.equals(order.getFromSite())){
                    UserHelper.CompanyDOHelper companyDOHelper = new UserHelper.CompanyDOHelper();
                    userHelper.getOrderChannelNewCompanyDO(order.getOldExamCompanyId(), managerId, companyDOHelper, mt,mt1);
                    if (companyDOHelper.getChannelCompanyDO() != null ) {
                        channelCompanyId = companyDOHelper.getChannelCompanyDO().getId();
                        channelCompanyDO = companyDOHelper.getChannelCompanyDO();
                    }
                }
                dao.updateOrderCompanyId(order.getId(), order.getBatchId(), order.getOrderNum(), newCompanyId, channelCompanyId);

                Query query = new Query(Criteria.where("orderNum").is(order.getOrderNum()));
                MongoOrder mongoOrder = mongoTemplate.findOne(query, MongoOrder.class,"mongoOrder");
                if (mongoOrder != null && mongoOrder.getExamCompanyId() != null) {
                    mongoOrder.setOldExamCompanyId(mongoOrder.getExamCompanyId());
                    mongoOrder.setExamCompanyId(newCompanyId);
                    if(channelCompanyDO != null){
                        ChannelExamCompanySnapshot channelCompanySnapshot = new ChannelExamCompanySnapshot();
                        BeanUtils.copyProperties(channelCompanyDO, channelCompanySnapshot);
                        mongoOrder.setChannelCompany(channelCompanySnapshot);
                    }
                    mongoTemplate.save(mongoOrder);
                }
                success.incrementAndGet();
            }else{
                noOps.incrementAndGet();
                noOpsOrderIds.append(order.getId()).append(",");
            }
            num .incrementAndGet();
        } catch (Exception e) {
            failOrderIds.append(order.getId()).append(",");
            fail.incrementAndGet();
            log.error("订单id=" + order.getId() + "数据迁移失败，原因：", e);
        }
		if (num.get() > 100) {
            // 更新lastpkid
            orderMigrateLogMapper.updateLastPrimaryKeyIdByTableName(
                    OrderConstant.TB_ORDER,
                    order.getId());
            num.set(0);
        }
	}

	@Override
	public String migratePlatformManagerData(){
        StringBuilder result = new StringBuilder();
        StringBuilder failOrderIds = new StringBuilder();
        StringBuilder mongoOrderIds = new StringBuilder();
        log.info("客户经理对应的渠道站点开始");
        Date startDate = new Date();
        List<OrderDO> orderDOS = orderMapper.selectAllChannelOrders();
        Date endDate = new Date();
        long time = endDate.getTime() - startDate.getTime();
        log.info("查询耗时{}s,查询数量：{}",time/1000,orderDOS.size());
        log.info("需要处理的数据总共有{}条",orderDOS.size());
        result.append("需要处理的数据总共有").append(orderDOS.size()).append("条");
        orderDOS.forEach(order -> {
            try {
                order.setFromSiteOrgType(2);
                orderMapper.updateOrder(order);
                Query query = new Query(Criteria.where("orderNum").is(order.getOrderNum()));
                MongoOrder mongoOrder = mongoTemplate.findOne(query, MongoOrder.class,"mongoOrder");
                if (mongoOrder != null ) {
                    mongoOrder.setFromSite(order.getFromSite());
                    mongoOrder.setFromSiteOrgType(2);
                    mongoTemplate.save(mongoOrder);
                    mongoOrderIds.append(order.getOrderNum()).append(",");
                }
                success.incrementAndGet();
            } catch (Exception e) {
                failOrderIds.append(order.getOrderNum()).append(",");
                fail.incrementAndGet();
                log.error("订单num=" + order.getOrderNum() + "数据迁移失败，原因：", e);
            }
            log.info("当前处理完成第{}条",total.incrementAndGet());
        });
        log.info("处理成功{}条",success);
        log.info("处理失败{}条",fail);
        log.info("失败记录：{}",failOrderIds.toString());
        log.info("订正mongo的订单编号记录:{}",mongoOrderIds.toString());

        result.append("处理成功").append(success).append("条");
        result.append("处理失败").append(fail).append("条");
        result.append("失败记录:").append(failOrderIds.toString());
        result.append("订正mongo的订单编号记录:").append(mongoOrderIds.toString());
        return result.toString();
    }

    @Override
    public String orderManagerIdMigrate(){
        AtomicInteger total = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        StringBuilder result = new StringBuilder();
        StringBuilder failOrderIds = new StringBuilder();
        StringBuilder mongoOrderIds = new StringBuilder();
        Date startDate = new Date();
        log.info("===========迁移mysql和mongo的订单表的orderManagerId开始");
        int rowCount = orderMapper.selectAllManagerIdIsNullOrdersCount();
        log.info("需要处理的数据总共有{}条",rowCount);
        result.append("需要处理的数据总共有").append(rowCount).append("条");
        int count = (rowCount / ORDER_AMOUNT_PAGE_SIZE) + (rowCount % ORDER_AMOUNT_PAGE_SIZE > 0 ? 1 : 0);
        try {
            // 创建线程池添加任务
            ExecutorService executorService = Executors.newFixedThreadPool(16);
            ArrayList<Callable<List<OrderDO>>> tasks = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                int currentPage = i;
                tasks.add(() -> {
                    Page page = new Page();
                    page.setCurrentPage(currentPage);
                    page.setPageSize(ORDER_AMOUNT_PAGE_SIZE);
                    log.info("当前处理第{}页",currentPage);
                    return orderMapper.selectAllManagerIdIsNullOrdersByPage(page);
                });
            }
            // 执行并处理任务结果
            List<Future<List<OrderDO>>> futures = executorService.invokeAll(tasks);
            ArrayList<Callable<Integer>> taskActions = new ArrayList<>();
            futures.forEach(listFuture -> taskActions.add(() -> {
                try {
                    List<OrderDO> orderDOS = listFuture.get();
                    orderDOS.forEach(order -> {
                        try {
                            orderMapper.updateOrder(order);
                            Query query = new Query(Criteria.where("orderNum").is(order.getOrderNum()));
                            MongoOrder mongoOrder = mongoTemplate.findOne(query, MongoOrder.class,"mongoOrder");
                            if (mongoOrder != null ) {
                                mongoOrder.setOwnerId(order.getOrderManagerId());
                                mongoOrder.setOrderManager(getOrderManager(order.getOrderManagerId()));
                                mongoTemplate.save(mongoOrder);
                                mongoOrderIds.append(order.getOrderNum()).append(",");
                            }
                            success.incrementAndGet();
                        } catch (Exception e) {
                            failOrderIds.append(order.getOrderNum()).append(",");
                            fail.incrementAndGet();
                            log.error("订单num=" + order.getOrderNum() + "数据迁移失败，原因：", e);
                        }
                        log.info("当前处理完成第{}条",total.incrementAndGet());
                    });
                    return orderDOS.size();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return 0;
            }));
            executorService.invokeAll(taskActions);
            executorService.shutdown();
        } catch (Exception e) {
            fail.incrementAndGet();
            log.error("迁移orderManagerId数据失败"+e);
        }
        log.info("处理成功{}条",success);
        log.info("处理失败{}条",fail);
        log.info("失败记录：{}",failOrderIds.toString());
        log.info("订正mongo的订单编号记录:{}",mongoOrderIds.toString());
        result.append("处理成功").append(success).append("条");
        result.append("处理失败").append(fail).append("条");
        result.append("失败记录:").append(failOrderIds.toString());
        result.append("订正mongo的订单编号记录:").append(mongoOrderIds.toString());
        log.info("===========迁移mysql和mongo的订单表的orderManagerId结束");
        Date endDate = new Date();
        Long time = endDate.getTime() - startDate.getTime();
        log.info("总耗时{}分钟",time/1000/60);
        return result.toString();
    }

    @Override
    public String orderCompanyMigrate() {
        AtomicInteger total = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        StringBuilder result = new StringBuilder();
        StringBuilder failOrderIds = new StringBuilder();
        StringBuilder mongoOrderIds = new StringBuilder();
        log.info("客户经理对应的渠道站点开始");
        Date startDate = new Date();
        List<OrderDO> orderDOS = orderMapper.selectAllChannelOrders();
        Date endDate = new Date();
        long time = endDate.getTime() - startDate.getTime();
        log.info("查询耗时{}s,查询数量：{}",time/1000,orderDOS.size());
        log.info("需要处理的数据总共有{}条",orderDOS.size());
        result.append("需要处理的数据总共有").append(orderDOS.size()).append("条");
        orderDOS.forEach(order -> {
            try {
                ChannelCompanyDO channelCompanyDO = channelCompanyMapper.selectByExamCompanyIdAndOrganizationId(order.getOldExamCompanyId(),order.getFromSite());
                if(channelCompanyDO != null){
                    order.setChannelCompanyId(channelCompanyDO.getId());
                    orderMapper.updateOrder(order);
                }
                Query query = new Query(Criteria.where("orderNum").is(order.getOrderNum()));
                MongoOrder mongoOrder = mongoTemplate.findOne(query, MongoOrder.class,"mongoOrder");
                if (mongoOrder != null ) {
                    if(channelCompanyDO != null){
                        ChannelExamCompanySnapshot channelExamCompanySnapshot = new ChannelExamCompanySnapshot();
                        BeanUtils.copyProperties(channelCompanyDO, channelExamCompanySnapshot);
                        mongoOrder.setChannelCompany(channelExamCompanySnapshot);
                        mongoTemplate.save(mongoOrder);
                        mongoOrderIds.append(order.getOrderNum()).append(",");
                    }
                }
                success.incrementAndGet();
            } catch (Exception e) {
                failOrderIds.append(order.getOrderNum()).append(",");
                fail.incrementAndGet();
                log.error("订单num=" + order.getOrderNum() + "数据迁移失败，原因：", e);
            }
            log.info("当前处理完成第{}条",total.incrementAndGet());
        });
        log.info("处理成功{}条",success);
        log.info("处理失败{}条",fail);
        log.info("失败记录：{}",failOrderIds.toString());
        log.info("订正mongo的订单编号记录:{}",mongoOrderIds.toString());

        result.append("处理成功").append(success).append("条");
        result.append("处理失败").append(fail).append("条");
        result.append("失败记录:").append(failOrderIds.toString());
        result.append("订正mongo的订单编号记录:").append(mongoOrderIds.toString());
        return result.toString();
    }

    private ManagerSnapshot getOrderManager(Integer managerId) {
        Account account = userHelperMapper.getAccountById(managerId);
        ManagerSnapshot snapshot = new ManagerSnapshot();
        snapshot.setEmployeeId(account.getEmployeeId());
        snapshot.setId(account.getId());
        snapshot.setIdType(account.getIdType());
        snapshot.setMobile(account.getMobile());
        snapshot.setName(account.getName());
        snapshot.setStatus(account.getStatus());
        snapshot.setType(account.getType());
        // 身份证
        String idCard = account.getIdCard();
        snapshot.setIdCard(idCard);
        // 不知道婚否
        // snapshot.setMarriageStatus(account.g);
        if (IdCardValidate.isIdcard(idCard)) {
            snapshot.setGender(IdCardValidate.getGender(idCard));
            snapshot.setAge(IdCardValidate.calculateAge(idCard, false));
            snapshot.setBirthYear(IdCardValidate.getBirthYear(idCard));
        }
        return snapshot;
    }


}
