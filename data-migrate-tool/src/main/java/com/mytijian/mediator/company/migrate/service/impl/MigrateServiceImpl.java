package com.mytijian.mediator.company.migrate.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mytijian.company.enums.ExamCompanyTypeEnum;
import com.mytijian.company.migrate.service.constant.CompanyOperationConstants;
import com.mytijian.mediator.company.migrate.dao.AccountRoleMapper;
import com.mytijian.mediator.company.migrate.dao.ChannelCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.CompanyMigrateLogMapper;
import com.mytijian.mediator.company.migrate.dao.CompanyOperationLogMapper;
import com.mytijian.mediator.company.migrate.dao.ExamCompanyHospitalRelMapper;
import com.mytijian.mediator.company.migrate.dao.ExamCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.HospitalCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.HospitalMapper;
import com.mytijian.mediator.company.migrate.dao.ManagerChannelRelationMapper;
import com.mytijian.mediator.company.migrate.dao.ManagerCompanyRelationMapper;
import com.mytijian.mediator.company.migrate.dao.PlatformCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.AccountRoleDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ChannelCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.CompanyMigrateLogDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.CompanyOperationLogDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ExamCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ManagerChannelRelationDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ManagerCompanyRelationDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.PlatformCompanyDO;
import com.mytijian.mediator.company.migrate.dao.resource.OrganizationSettingsMapper;
import com.mytijian.mediator.company.migrate.service.MigrateService;
import com.mytijian.mediator.company.migrate.service.constant.InitCompanyEnum;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.util.PinYinUtil;

@Service("migrateService")
public class MigrateServiceImpl implements MigrateService {

	private Logger logger = LoggerFactory.getLogger(MigrateServiceImpl.class);

	@Resource(name = "companyMigrateLogMapper")
	private CompanyMigrateLogMapper companyMigrateLogMapper;

	@Resource(name = "examCompanyMapper")
	private ExamCompanyMapper examCompanyMapper;

	@Resource(name = "platformCompanyMapper")
	private PlatformCompanyMapper platformCompanyMapper;

	@Resource(name = "hospitalMapper")
	private HospitalMapper hospitalMapper;

	@Resource(name = "hospitalCompanyMapper")
	private HospitalCompanyMapper hospitalCompanyMapper;

	@Resource(name = "channelCompanyMapper")
	private ChannelCompanyMapper channelCompanyMapper;

	@Resource(name = "examCompanyHospitalRelMapper")
	private ExamCompanyHospitalRelMapper examCompanyHospitalRelMapper;

	@Resource(name = "mCompanyResolver")
	private MCompanyResolver mCompanyResolver;

	@Resource(name = "hospitalCompanyResolver")
	private HospitalCompanyResolver hospitalCompanyResolver;

	@Resource(name = "pCompanyResolver")
	private PCompanyResolver pCompanyResolver;

	@Resource(name = "companyOperationLogMapper")
	private CompanyOperationLogMapper companyOperationLogMapper;
	
	@Resource(name = "organizationSettingsMapper")
	private OrganizationSettingsMapper organizationSettingsMapper;
	
	@Resource(name = "managerCompanyRelationMapper")
	private ManagerCompanyRelationMapper managerCompanyRelationMapper;
	
	@Resource(name = "accountRoleMapper")
	private AccountRoleMapper accountRoleMapper;
	
	@Resource(name = "managerChannelRelationMapper")
	private ManagerChannelRelationMapper managerChannelRelationMapper;
	
	private static final Integer IS_PCOMPANY_LIMIT = 6;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean migrate() {

		CompanyMigrateLogDO migLog = companyMigrateLogMapper
				.selectById(CompanyMigrateLogDO.ID);

		if (CompanyOperationConstants.DONE.equals(migLog.getMigrateDone())) {
			System.out.println("历史数据已迁移完成");
			logger.info("历史数据已迁移完成");
			return false;
		}
		// 查询单位主表
		List<ExamCompanyDO> companyList = examCompanyMapper
				.selectByLastPrimaryKeyId(migLog.getLastPrimaryKeyId() + 1,
						migLog.getLimitSize());

//		logger.info("last pk id:{}", migLog.getLastPrimaryKeyId());

		if (CollectionUtils.isEmpty(companyList)) {
			// 迁移完毕
			companyMigrateLogMapper.updateMigrateDone("done");
			return false;
		}

		// 1：医院单位，tb_hospital_company
		// 2：散客单位 1585
		// 3：p单位，tb_platform_company、tb_hospital_company,tb_chanel_company
		// (对应的机构id196，平台每天健康)
		// 4：M散客
		// tb_hospital_company没有对应数据,tb_channel_company,对应的机构id196，平台每天健康

		// 迁移医院单位
		List<HospitalCompanyDO> hospCompList = hospitalCompanyResolver
				.handleHospComp(companyList);
		if (CollectionUtils.isNotEmpty(hospCompList)) {
			hospitalCompanyMapper.insertList(hospCompList);
		}

		// 迁移P单位
		pCompanyResolver.handlePlatformCompany(companyList);

		// 迁移M单位
		List<ChannelCompanyDO> mList = mCompanyResolver
				.handleMCompany(companyList);
		if (CollectionUtils.isNotEmpty(mList)) {
			channelCompanyMapper.insertList(mList);
		}
		// 更新lastpkid
		companyMigrateLogMapper.updateLastPrimaryKeyIdByTableName(
				CompanyOperationConstants.TB_EXAM_COMPANY,
				companyList.get(companyList.size() - 1).getId());

		return true;
	}

	/**
	 * 散客别名同步
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void init() {
		logger.info("开始执行单位初始化....");
		CompanyMigrateLogDO migLog = companyMigrateLogMapper
				.selectById(CompanyMigrateLogDO.ID);

		if (CompanyOperationConstants.DONE.equals(migLog.getInitDone())) {
			System.out.println("已初始化");
			logger.info("已初始化");
			return;
		}

		long start = Calendar.getInstance().getTimeInMillis();
		

		List<HospitalDO> allList = hospitalMapper.selectAll();

		// 医院列表
		List<HospitalDO> hospList = allList
				.stream()
				.filter(p -> p.getOrganizationType().intValue() == OrganizationTypeEnum.HOSPITAL
						.getCode()).collect(Collectors.toList());

		// 渠道商列表
		List<HospitalDO> channelList = allList
				.stream()
				.filter(p -> p.getOrganizationType() == OrganizationTypeEnum.CHANNEL
						.getCode()).collect(Collectors.toList());

		// 初始化医院的网上个人预约、现场散客、每天健康
		this.initHospitalGuestCompany(hospList, this.getInitHospitalCompany());

		// 初始化渠道商单位的个人网上预约、散客单位
		this.initChannelGuestCompany(channelList, this.getInitChannelCompany());

		// 设置已完成标记
		companyMigrateLogMapper.updateInitDone("done");
		System.out.println("====time === "
				+ (Calendar.getInstance().getTimeInMillis() - start));

		logger.info("finish init ,time(ms) : {}", (Calendar.getInstance()
				.getTimeInMillis() - start));

	}

	@Override
	public void initChannelGuestCompany(List<HospitalDO> channelList,
			List<ChannelCompanyDO> initChannelCompList) {
		if (CollectionUtils.isNotEmpty(channelList)) {
			
			for (HospitalDO channel : channelList) {
				HospitalSettings settings = organizationSettingsMapper
						.getHospitalSettingByHospitalId(channel.getId());
				if (settings == null) {
					System.out.println("医院设置为空，hosp id " + channel.getId());
					logger.error("医院设置为空，hosp id " + channel.getId());
					continue;
				}

				for (ChannelCompanyDO t : initChannelCompList) {
					t.setOrganizationId(channel.getId());
					t.setOrganizationName(channel.getName());

					setChannelPropertyFromOrg(settings, t);
				}
				
				HospitalDO cHospitalDO =  hospitalMapper.selectGuestNameByHospId(channel.getId());
				// 设置个人网上预约
				initChannelCompList
						.get(0)
						.setName(
								StringUtils.isEmpty(cHospitalDO
										.getGuestOnline()) ? InitCompanyEnum.CHANNEL_GUEST_ONLINE
										.getName() : cHospitalDO
										.getGuestOnline());
				initChannelCompList.get(0).setPinyin(
						PinYinUtil.getFirstSpell(initChannelCompList.get(0)
								.getName()));
				initChannelCompList.get(0).setTbExamCompanyId(
						InitCompanyEnum.CHANNEL_GUEST_ONLINE
								.getTbExamCompanyId());

				// 设置散客现场
				initChannelCompList
						.get(1)
						.setName(
								StringUtils.isEmpty(cHospitalDO.getGuestOffline()) ? InitCompanyEnum.CHANNEL_GUEST_OFFLINE
										.getName() : cHospitalDO.getGuestOffline());
				initChannelCompList.get(1).setPinyin(
						PinYinUtil.getFirstSpell(initChannelCompList.get(1)
								.getName()));
				initChannelCompList.get(1).setTbExamCompanyId(
						InitCompanyEnum.CHANNEL_GUEST_OFFLINE
								.getTbExamCompanyId());

				channelCompanyMapper.insertList(initChannelCompList);
			}
		}
	}

	@Override
	public List<ChannelCompanyDO> getInitChannelCompany() {
		List<PlatformCompanyDO> initChannelList = platformCompanyMapper
				.selectByOrganizationType("channel");

		// 初始化渠道商单位
		List<ChannelCompanyDO> initChannelCompList = new ArrayList<ChannelCompanyDO>();
		for (PlatformCompanyDO platformCompanyDO : initChannelList) {
			ChannelCompanyDO channel = new ChannelCompanyDO();
			channel.setDiscount(1d);
			channel.setPlatformCompanyId(platformCompanyDO.getId());
			channel.setDeleted(false);
			initChannelCompList.add(channel);
		}
		return initChannelCompList;
	}

	@Override
	public void initHospitalGuestCompany(List<HospitalDO> hospList,List<HospitalCompanyDO> initHospCompList) {
		if (CollectionUtils.isNotEmpty(hospList)) {

			for (HospitalDO hosp : hospList) {
				initHospitalGuestCompany(initHospCompList, hosp);
			}
		}
	}

	@Override
	public List<HospitalCompanyDO> getInitHospitalCompany() {
		List<PlatformCompanyDO> initHospList = platformCompanyMapper
				.selectByOrganizationType("hospital");
		
		// 初始化医院单位
		List<HospitalCompanyDO> initHospCompList = new ArrayList<HospitalCompanyDO>();
		for (PlatformCompanyDO hospDo : initHospList) {
			HospitalCompanyDO hosp = new HospitalCompanyDO();
			hosp.setDiscount(1d);
			hosp.setPlatformCompanyId(hospDo.getId());
			hosp.setDeleted(false);
			hosp.setEmployeeImport(false);
			initHospCompList.add(hosp);
		}
		return initHospCompList;
	}

	private void initHospitalGuestCompany(
			List<HospitalCompanyDO> initHospCompList, HospitalDO hosp) {
		HospitalSettings settings = organizationSettingsMapper
				.getHospitalSettingByHospitalId(hosp.getId());
		if (settings == null) {
			System.out.println("医院设置为空，hosp id " + hosp.getId());
			logger.warn("医院设置为空，hosp id " + hosp.getId());
			return ;
		}

		for (HospitalCompanyDO t : initHospCompList) {
			t.setOrganizationId(hosp.getId()); // 加机构名称
			t.setOrganizationName(hosp.getName());

			// 使用单位的是否显示报告.show_exam_report是C端用的
			t.setShowReport(settings.getShowCompanyReport());
			t.setSettlementMode(settings.getSettlementMode());
			t.setSendExamSms(settings.getSendExamSms());
			t.setSendExamSmsDays(settings.getSendExamSmsDays());
			// 是否提前导出订单,使用xls导出的，不自动导；不使用xls
			// 导出的，取医院默认的导出方式（自动导出还是按照xls排序导出）
//			Boolean advanceExportOrder = settings.getExportWithXls() ? false
//					: settings.getAdvanceExportCompanyOrder();

			Boolean advanceExportOrder = null;
			if (settings.getExportWithXls() == null) {
				advanceExportOrder = true;
			} else if (settings.getExportWithXls()) {
				advanceExportOrder = false;
			} else {
				advanceExportOrder = settings.getAdvanceExportCompanyOrder();
			}
			
			t.setAdvanceExportOrder(advanceExportOrder);
			
			// 设置单位地址
			t.setExaminationAddress(null);
			t.setExamreportIntervalTime(settings.getExamreportIntervalTime());
		}
		
		HospitalDO hospitalDO = hospitalMapper.selectGuestNameByHospId(hosp.getId());

		// 设置网上个人预约别名
		initHospCompList
				.get(0)
				.setName(
						StringUtils.isEmpty(hospitalDO.getGuestOnline()) ? InitCompanyEnum.HOSPITAL_GUEST_ONLINE
								.getName() : hospitalDO.getGuestOnline());
		initHospCompList.get(0).setPinyin(
				PinYinUtil.getFirstSpell(initHospCompList.get(0)
						.getName()));
		initHospCompList.get(0).setTbExamCompanyId(
				InitCompanyEnum.HOSPITAL_GUEST_ONLINE
						.getTbExamCompanyId());

		// 设置现场散客别名
		initHospCompList
				.get(1)
				.setName(
						StringUtils.isEmpty(hospitalDO
								.getGuestOffline()) ? InitCompanyEnum.HOSPITAL_GUEST_OFFLINE
								.getName() : hospitalDO
								.getGuestOffline());
		initHospCompList.get(1).setPinyin(
				PinYinUtil.getFirstSpell(initHospCompList.get(1)
						.getName()));
		initHospCompList.get(1).setTbExamCompanyId(
				InitCompanyEnum.HOSPITAL_GUEST_OFFLINE
						.getTbExamCompanyId());
		// 设置每天健康别名
		initHospCompList
				.get(2)
				.setName(
						StringUtils.isEmpty(hospitalDO.getmGuest()) ? InitCompanyEnum.HOSPITAL_MTJK
								.getName() : hospitalDO.getmGuest());
		initHospCompList.get(2).setPinyin(
				PinYinUtil.getFirstSpell(initHospCompList.get(2)
						.getName()));
		initHospCompList.get(2).setTbExamCompanyId(
				InitCompanyEnum.HOSPITAL_MTJK.getTbExamCompanyId());

		hospitalCompanyMapper.insertList(initHospCompList);
	}

	private void setChannelPropertyFromOrg(HospitalSettings settings,
			ChannelCompanyDO t) {
		t.setSettlementMode(settings.getSettlementMode());
		t.setSendExamSms(settings.getSendExamSms());
		t.setSendExamSmsDays(settings.getSendExamSmsDays());
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changedMigrateData() {
		// 查询出操作日志中需要处理的数据
		List<CompanyOperationLogDO> companyOperationLogDOList = companyOperationLogMapper
				.selectByCompleted(CompanyOperationConstants.TODO);

		// 待处理数据为空直接返回
		if (CollectionUtils.isEmpty(companyOperationLogDOList)) {
			return;
		}

		// 遍历待处理数据
		for (CompanyOperationLogDO companyOperationLogDO : companyOperationLogDOList) {
			try{
			// 建站初始化操作,读interface_name（机构id）根据类型，初始化相应的表
			if ("init".equals(companyOperationLogDO.getOperationType())) {
				HospitalDO hospitalDO = hospitalMapper
						.selectBaseInfoByHospId(Integer
								.parseInt(companyOperationLogDO
										.getInterfaceName()));
				if (hospitalDO == null) {
					logger.warn("找不到机构，id ："
							+ companyOperationLogDO.getInterfaceName());
					return;
				}

				if (hospitalDO.getOrganizationType() == OrganizationTypeEnum.HOSPITAL
						.getCode()) {

					initHospitalGuestCompany(Arrays.asList(hospitalDO),
							this.getInitHospitalCompany());

				} else if (hospitalDO.getOrganizationType() == OrganizationTypeEnum.CHANNEL
						.getCode()) {
					initChannelGuestCompany(Arrays.asList(hospitalDO),
							this.getInitChannelCompany());
				}
				
				continue;
			}

			/**
			 * 更新散客单位名称 1、读interface_name（机构id）根据类型，更新tb_hospital_company or
			 * tb_channel_company
			 */
			if (InitCompanyEnum.getTbExamComapnyId().contains(
					companyOperationLogDO.getTablePrimaryKeyId())) {
			
				HospitalDO hospitalDO = hospitalMapper
						.selectBaseInfoByHospId(Integer
								.parseInt(companyOperationLogDO
										.getInterfaceName()));
				if (hospitalDO == null) {
					logger.warn("找不到机构，id ："
							+ companyOperationLogDO.getInterfaceName());
					return;
				}

				HospitalDO hospAlias = hospitalMapper.selectGuestNameByHospId(Integer
						.parseInt(companyOperationLogDO.getInterfaceName()));

				if (hospitalDO.getOrganizationType() == OrganizationTypeEnum.HOSPITAL
						.getCode()) {
					// 通过tb_exam_company_id 和 机构id 更新 单位名

					String companyAlias = null;
					if (InitCompanyEnum.HOSPITAL_GUEST_ONLINE
							.getTbExamCompanyId().intValue() == companyOperationLogDO
							.getTablePrimaryKeyId()) {
						companyAlias = hospAlias.getGuestOnline();
					} else if (InitCompanyEnum.HOSPITAL_GUEST_OFFLINE
							.getTbExamCompanyId() == companyOperationLogDO
							.getTablePrimaryKeyId()) {
						companyAlias = hospAlias.getGuestOffline();
					} else if (InitCompanyEnum.HOSPITAL_MTJK
							.getTbExamCompanyId() == companyOperationLogDO
							.getTablePrimaryKeyId()) {
						companyAlias = hospAlias.getmGuest();
					}

					hospitalCompanyMapper
							.updateNameByTbExamCompanyIdAndOrganizationId(
									companyAlias, PinYinUtil
											.getFirstSpell(companyAlias),
									companyOperationLogDO
											.getTablePrimaryKeyId(), hospitalDO
											.getId());

				} else if (hospitalDO.getOrganizationType() == OrganizationTypeEnum.CHANNEL
						.getCode()) {
					//TODO 渠道商散客别名没有地方可以修改 ，暂时不做
				}

				continue;

			}
			
			if (companyOperationLogDO.getTablePrimaryKeyId() == null) {
				continue;
			}
			
			// 根据tb_exam_company表的id查出数据
			ExamCompanyDO examCompanyDO = examCompanyMapper
					.selectById(companyOperationLogDO.getTablePrimaryKeyId());
			if (examCompanyDO == null) {
				logger.warn(
						"not found in tb_exam_company,tb_exam_company_id : {},log id : {}",
						companyOperationLogDO.getTablePrimaryKeyId(),
						companyOperationLogDO.getId());
				continue;
			}
			Integer type = examCompanyDO.getType();
			if (type == ExamCompanyTypeEnum.normal.getCode()) // 医院单位
			{
				hospitalCompanyResolver.changeHospitalCompanyData(examCompanyDO);

			} else if (type == ExamCompanyTypeEnum.P.getCode()) // 平台单位
			{
				pCompanyResolver.updatePlatformCompany(examCompanyDO);

			} else if (type == ExamCompanyTypeEnum.M.getCode()) // 渠道商单位
			{
//				mCompanyResolver.changeChannelCompanyData(examCompanyDO);
				logger.info("这里有个M单位等待处理，单位id={},单位名称={}",examCompanyDO.getId(),examCompanyDO.getName());
				
			}
			} catch (Exception e) {
				logger.error("changedMigrateData error,company operation id : "
						+ companyOperationLogDO.getId(), e);
			}
			
		}
		
		// 更新为done
		List<Integer> ids = new ArrayList<Integer>();
		companyOperationLogDOList.stream().forEach(p -> ids.add(p.getId()));
		companyOperationLogMapper.batchUpdateIsCompleted(ids,
				CompanyOperationConstants.DONE);
		
	}
	
	/**
	 * 更新机构散客单位设置
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateOrganizationGuestCompanySetting() {

		logger.info("开始更新散客单位设置....");
		long startTime = new Date().getTime();
		List<HospitalDO> allList = hospitalMapper.selectAll();

		// 医院列表
		List<HospitalDO> hospList = getHospital(allList);
		// 渠道商列表
		List<HospitalDO> channelList = getChannel(allList);
		
		updateHospitalGuestCompanySetting(hospList);
		updateChannelGuestCompanySetting(channelList);

		logger.info("散客单位设置更新完成,耗时{}ms。", new Date().getTime() - startTime);
	}

	/**
	 * 体检中心散客单位设置更新
	 * @param hospList
	 */
	private void updateHospitalGuestCompanySetting(List<HospitalDO> hospList) {
		if (CollectionUtils.isNotEmpty(hospList)) {

			for (HospitalDO hospitalDO : hospList) {
				List<HospitalCompanyDO> hospitalCompanyList = hospitalCompanyMapper
						.selectHospitalGuestCompany(hospitalDO.getId());

				HospitalSettings settings = organizationSettingsMapper
						.getHospitalSettingByHospitalId(hospitalDO.getId());

				if (CollectionUtils.isEmpty(hospitalCompanyList) || settings == null) {
					continue;
				}

				for (HospitalCompanyDO hospitalCompanyDO : hospitalCompanyList) {
					// 使用单位的是否显示报告.show_exam_report是C端用的
					hospitalCompanyDO.setShowReport(settings.getShowCompanyReport());
					hospitalCompanyDO.setSettlementMode(settings.getSettlementMode());
					hospitalCompanyDO.setSendExamSms(settings.getSendExamSms());
					hospitalCompanyDO.setSendExamSmsDays(settings.getSendExamSmsDays());
					Boolean advanceExportOrder = null;
					if (settings.getExportWithXls() == null) {
						advanceExportOrder = true;
					} else if (settings.getExportWithXls()) {
						advanceExportOrder = false;
					} else {
						advanceExportOrder = settings.getAdvanceExportCompanyOrder();
					}
					hospitalCompanyDO.setAdvanceExportOrder(advanceExportOrder);
					// 设置单位地址
					hospitalCompanyDO.setExaminationAddress(null);
					hospitalCompanyDO.setExamreportIntervalTime(settings.getExamreportIntervalTime());
					hospitalCompanyMapper.updateExamCompany(hospitalCompanyDO);
				}

			}
		}
	}
	
	/**
	 * 渠道商散客单位设置更新
	 * @param hospList
	 */
	private void updateChannelGuestCompanySetting(List<HospitalDO> channelList) {
		if (CollectionUtils.isNotEmpty(channelList)) {
			for (HospitalDO hospitalDO : channelList) {
				List<ChannelCompanyDO> channelCompanyList = channelCompanyMapper
						.selectChannelGusetCompany(hospitalDO.getId());
				
				HospitalSettings settings = organizationSettingsMapper
						.getHospitalSettingByHospitalId(hospitalDO.getId());
				
				if(CollectionUtils.isNotEmpty(channelCompanyList) || settings == null){
					continue;
				}
				for (ChannelCompanyDO channelCompanyDO : channelCompanyList) {
					channelCompanyDO.setSettlementMode(settings.getSettlementMode());
					channelCompanyDO.setSendExamSms(settings.getSendExamSms());
					channelCompanyDO.setSendExamSmsDays(settings.getSendExamSmsDays());
					channelCompanyMapper.updateChannelCompany(channelCompanyDO);
				}
			}
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void initManagerOnlineGustCompanyRealation() {
		logger.info("开始初始化现场散客客户经理关系...");

		long startTime = new Date().getTime();

		// 获取所有CRM客户经理
		List<AccountRoleDO> accountRoleDOList = accountRoleMapper.selectAllCrmManager();
		
		Set<Integer> mangerIdSet = new HashSet<Integer>();
		
		for(AccountRoleDO accountRoleDO : accountRoleDOList){
			if(accountRoleDO.getRoleId() == 5 || accountRoleDO.getRoleId() == 7){
				mangerIdSet.add(accountRoleDO.getAccountId());
			}else{
				Integer managerId = accountRoleMapper.selectIsSitePaymanagerId(accountRoleDO.getAccountId());
				if(managerId != null){
					mangerIdSet.add(accountRoleDO.getAccountId());
				}
			}
		}
		
		for(Integer mangerId : mangerIdSet){
			initConditionRoleOnlineGustCompanyRealation(mangerId);
		}
		
		logger.info("初始化现场散客客户经理关系结束,用时{}ms...", new Date().getTime() - startTime);
	}
	
	/**
	 * 为操作员或主管角色分配现场散客单位
	 * 
	 * @param managerId
	 */
	private void initConditionRoleOnlineGustCompanyRealation(Integer managerId){
		Integer organizationId = accountRoleMapper.selectManagerBelongOrganization(managerId);
		if(organizationId == null){
			return;
		}
		// 获取机构的现场散客单位信息
		HospitalCompanyDO hospitalCompanyDO = hospitalCompanyMapper
				.selectOnlineGuestCompanyByOrganizationId(organizationId);
		if(hospitalCompanyDO == null){
			return;
		}
		//组织数据插入数据
		ManagerCompanyRelationDO managerCompanyRelationDO = new ManagerCompanyRelationDO(); 
		managerCompanyRelationDO.setManagerId(managerId);
		managerCompanyRelationDO.setCompanyId(1585);
		managerCompanyRelationDO.setHospitalId(organizationId);
		managerCompanyRelationDO.setCreateManagerId(managerId.toString());
		managerCompanyRelationDO.setAsAccountCompany(1);
		managerCompanyRelationDO.setStatus(1);
		managerCompanyRelationDO.setNewCompanyId(hospitalCompanyDO.getId());
		managerCompanyRelationMapper.insertGuestCompany(managerCompanyRelationDO);
	}
	
	/**
	 * 获取所有的体检中心
	 * @param allList
	 * @return
	 */
	private List<HospitalDO> getHospital(List<HospitalDO> allList) {
		// 医院列表
		List<HospitalDO> hospList = allList.stream()
				.filter(p -> p.getOrganizationType().intValue() == OrganizationTypeEnum.HOSPITAL.getCode())
				.collect(Collectors.toList());
		return hospList;
	}
	
	/**
	 * 获取所有的渠道商
	 * @param allList
	 * @return
	 */
	private List<HospitalDO> getChannel(List<HospitalDO> allList) {
		// 渠道商列表
		List<HospitalDO> channelList = allList.stream()
				.filter(p -> p.getOrganizationType() == OrganizationTypeEnum.CHANNEL.getCode())
				.collect(Collectors.toList());
		return channelList;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void migratePlatformCompanyToChannel(Integer mtjkOrgId) {
		logger.info("开始平台客户经理分配给渠道商后,将其下单位也分配给渠道商的数据迁移...");
		long startTime = new Date().getTime();
		
		//查找所有角色是4（平台客户经理）的客户经理
		List<Integer> platformManagerIds = managerCompanyRelationMapper.selectPlatformMamager();
		if (CollectionUtils.isEmpty(platformManagerIds)) {
			 return;
		}
		//查询出所有分配给了渠道商客户经理
		List<ManagerChannelRelationDO> managerChannelRelList = managerChannelRelationMapper.selectByManagerIds(platformManagerIds);
		if (CollectionUtils.isEmpty(managerChannelRelList)) {
			 return;
		}
		
		for(ManagerChannelRelationDO managerChannelRelationDO : managerChannelRelList){
			//查询出该客户经理管理下的单位
			List<ManagerCompanyRelationDO> belongManagerCompanyList = managerCompanyRelationMapper.selectByManagerId(managerChannelRelationDO.getManagerId());
			
			if (CollectionUtils.isEmpty(belongManagerCompanyList)) {
				 return;
			}
			
			for(ManagerCompanyRelationDO managerCompanyRelDO : belongManagerCompanyList){
				//查询该单位的设置
				ChannelCompanyDO channelCompany = channelCompanyMapper.selectById(managerCompanyRelDO.getNewCompanyId());
				if(channelCompany == null){
					continue;
				}
				//是P单位
				if(channelCompany.getPlatformCompanyId() >= IS_PCOMPANY_LIMIT){
					ChannelCompanyDO chCompanyForInsert = new ChannelCompanyDO();
					BeanUtils.copyProperties(channelCompany, chCompanyForInsert,"id","organizationId","organizationName");
					HospitalDO hospital = hospitalMapper.selectBaseInfoByHospId(managerChannelRelationDO.getChannelId());
					if(hospital == null){
						logger.info("拥有不存在的机构,机构ID为：{}",managerChannelRelationDO.getChannelId());
						continue;
					}
					chCompanyForInsert.setOrganizationId(hospital.getId());
					chCompanyForInsert.setOrganizationName(hospital.getName());
					ChannelCompanyDO  ccDO = channelCompanyMapper.selectByExamCompanyIdAndOrganizationId(chCompanyForInsert.getTbExamCompanyId(), chCompanyForInsert.getOrganizationId());
					ManagerCompanyRelationDO managerCompanyRelationDOForInsert = new ManagerCompanyRelationDO();
					if(ccDO == null){
						channelCompanyMapper.insert(chCompanyForInsert);
						BeanUtils.copyProperties(managerCompanyRelDO,managerCompanyRelationDOForInsert,"newCompanyId");
						managerCompanyRelationDOForInsert.setNewCompanyId(chCompanyForInsert.getId());
					}else{
						BeanUtils.copyProperties(managerCompanyRelDO,managerCompanyRelationDOForInsert,"newCompanyId");
						managerCompanyRelationDOForInsert.setNewCompanyId(ccDO.getId());
					}
					managerCompanyRelationMapper.deleteByManagerIdNewCompanyIdAndHospitalId(managerCompanyRelDO.getHospitalId(), managerCompanyRelDO.getManagerId(), managerCompanyRelDO.getNewCompanyId());
					managerCompanyRelationDOForInsert.setAsAccountCompany(0);
					managerCompanyRelationMapper.insertGuestCompany(managerCompanyRelationDOForInsert);
				}
				
			}
			
		}
		logger.info("平台客户经理分配给渠道商后,将其下单位也分配给渠道商的数据迁移完成,用时{}ms...",new Date().getTime() - startTime);
	}
	
}