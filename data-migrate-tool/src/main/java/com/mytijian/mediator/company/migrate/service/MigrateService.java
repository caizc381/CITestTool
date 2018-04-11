package com.mytijian.mediator.company.migrate.service;

import java.util.List;

import com.mytijian.mediator.company.migrate.dao.dataobj.ChannelCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalDO;

public interface MigrateService {

	/**
	 * 初始化医院、渠道商单位表，个人网上预约、现场散客、每天健康
	 */
	void init();

	/**
	 * 根据tb_company_migrate_log读lastpkid和limit,查询tb_exam_company表，
	 * 迁移到tb_platform_company、tb_hospital_company、tb_channel_company。
	 * 迁移后，更新tb_company_migrate_log的lastpkid。
	 * 
	 */

	boolean migrate();

	/**
	 * 处理已迁移数据，有更新或新插入情况时.
	 * 定时执行，数据初始化和历史数据迁移完成后做。
	 */
	void changedMigrateData();
	
	/**
	 * 初始化医院散客单位
	 * @param hospList
	 */
	void initHospitalGuestCompany(List<HospitalDO> hospList,
			List<HospitalCompanyDO> initHospCompList);
	
	/**
	 * 初始化渠道商单位
	 * @param channelList
	 */
	void initChannelGuestCompany(List<HospitalDO> channelList,
			List<ChannelCompanyDO> initChannelCompList);
	
	/**
	 * 获取初始化渠道商单位
	 * @return
	 */
	List<ChannelCompanyDO> getInitChannelCompany() ;
	
	/**
	 * 获取初始化医院单位
	 * @return
	 */
	List<HospitalCompanyDO> getInitHospitalCompany() ;
	
	/**
	 * 更新散客单位设置
	 */
	void updateOrganizationGuestCompanySetting();
	
	/**
	 * 初始化散客现场单位和客户经理关系
	 */
	void initManagerOnlineGustCompanyRealation();
	
	/**
	 * 迁移平台单位到渠道商,当平台客户经理分配给了渠道商
	 */
	void migratePlatformCompanyToChannel(Integer mtjkOrgId);

}
