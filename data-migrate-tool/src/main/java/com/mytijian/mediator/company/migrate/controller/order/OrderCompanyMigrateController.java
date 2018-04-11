package com.mytijian.mediator.company.migrate.controller.order;

import com.mytijian.mediator.company.migrate.service.OrderCompanyMigrateService;
import com.mytijian.mediator.company.migrate.service.RollbackOrderCompanyService;
import com.mytijian.mediator.company.migrate.service.impl.order.OrderDetailMigrateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class OrderCompanyMigrateController {

	@Resource(name = "orderCompanyMigrateService")
	OrderCompanyMigrateService orderCompanyMigrateService;

	@Resource
	private OrderDetailMigrateService orderDetailMigrateService;

	@Resource
	private RollbackOrderCompanyService rollbackOrderCompanyService;

	@RequestMapping(value = "/migrateData", method = RequestMethod.GET)
	public String migrateData(String migratehspIds, String notMigratehspIds) throws Exception {
		return orderCompanyMigrateService.migrateData(migratehspIds, notMigratehspIds);
	}

	@RequestMapping(value = "/rollback", method = RequestMethod.GET)
	public void rollback(Integer hospitalId) throws Exception {
        rollbackOrderCompanyService.rollback(hospitalId);
	}
	
	@RequestMapping(value = "/migrateDataInfo", method = RequestMethod.GET)
	public String migrateDataInfo(String migratehspIds, String notMigratehspIds) throws Exception {
		StringBuilder info = new StringBuilder(); 
		info.append("迁移订单单位数据说明：\n");
		info.append("\n\t1.为了支持灰度发布，体检中心id可以配置，多个体检中心id通过逗号（英文）分隔\n");
		info.append("\n\t2.参数migratehspIds 需要迁移医院id列表，可以是All，代表全部医院，但是此时notMigratehspIds不能为空\n");
		info.append("\n\t3.notMigratehspIds 不需要迁移医院id列表\n");
		info.append("示例如下：\n");
		info.append("\n\t1.上灰度迁移医院示例，http://xxxx/action/migrateData?migratehspIds=1,2,3\n");
		info.append("\n\t2.从灰度上线迁移医院示例，http://xxxx/action/migrateData?migratehspIds=all&notMigratehspIds=1,2,3");
		
		return info.toString();
	}

	@RequestMapping(value = "/platformManagerMigrateData", method = RequestMethod.GET)
	public String platformManagerMigrateData() throws Exception {
		return orderCompanyMigrateService.migratePlatformManagerData();
	}

	@RequestMapping(value = "/orderManagerIdMigrate", method = RequestMethod.GET)
	public String orderManagerIdMigrate() throws Exception {
		return orderCompanyMigrateService.orderManagerIdMigrate();
	}

	@RequestMapping(value = "/orderCompanyMigrate", method = RequestMethod.GET)
	public String orderCompanyMigrate() throws Exception {
		return orderCompanyMigrateService.orderCompanyMigrate();
	}

	@RequestMapping(value = "/orderDetail", method = RequestMethod.GET)
	public String orderDetailMigrate() throws Exception {
		return orderDetailMigrateService.migrate();
	}


}
