package com.mytijian.mediator.company.migrate.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mytijian.company.enums.ExamCompanyTypeEnum;
import com.mytijian.mediator.company.migrate.dao.ChannelCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.ExamCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.HospitalCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.HospitalMapper;
import com.mytijian.mediator.company.migrate.dao.ManagerCompanyRelationMapper;
import com.mytijian.mediator.company.migrate.dao.PlatformCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.ChannelCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ExamCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ManagerCompanyRelationDO;
import com.mytijian.mediator.company.migrate.dao.resource.OrganizationSettingsMapper;
import com.mytijian.mediator.company.migrate.dao.user.ManagerChannelRelMapper;
import com.mytijian.mediator.company.migrate.service.ManagerCompanyMigrateService;
import com.mytijian.mediator.company.migrate.service.constant.InitCompanyEnum;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.util.PinYinUtil;

@Service("managerCompanyMigrateService")
public class ManagerCompanyMigrateServiceImpl implements
		ManagerCompanyMigrateService {
	
	private Logger logger = LoggerFactory.getLogger(ManagerCompanyMigrateServiceImpl.class);

	@Resource(name = "managerCompanyRelationMapper")
	private ManagerCompanyRelationMapper managerCompanyRelationMapper;

	@Resource(name = "channelCompanyMapper")
	private ChannelCompanyMapper channelCompanyMapper;

	@Resource(name = "hospitalCompanyMapper")
	private HospitalCompanyMapper hospitalCompanyMapper;

	@Resource(name = "platformCompanyMapper")
	private PlatformCompanyMapper platformCompanyMapper;

	@Resource(name = "examCompanyMapper")
	private ExamCompanyMapper examCompanyMapper;

	@Resource(name="managerChannelRelMapper")
	private ManagerChannelRelMapper managerChannelRelMapper;

	@Resource
	private OrganizationSettingsMapper organizationSettingsMapper;

	@Resource
	private HospitalMapper hospitalMapper;

	@Value("${mtjkOrgId}")
	private Integer mtjkOrgId;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean migrate() {

		List<ManagerCompanyRelationDO> managerCompanyRelationList = managerCompanyRelationMapper
				.selectNewCompanyIdIsNull();
		if (CollectionUtils.isEmpty(managerCompanyRelationList)) {
			return false;
		}
		this.migrateHospitalCompany(managerCompanyRelationList);
		
		// 迁移为渠道商单位id
		this.migratePlatformCompany(managerCompanyRelationList);
		this.migrateChannelCompany(managerCompanyRelationList);
		// 不需要迁移1585
		return false;
	}

	/**
	 * 获取单位类型
	 * 
	 * @return
	 * @param
	 */
	private Integer getCompayType(Integer companyId) {
		ExamCompanyDO examCompanyDO = examCompanyMapper.selectById(companyId);

		if (examCompanyDO != null) {
			return examCompanyDO.getType();
		}
		
		logger.warn("在tb_exam_company找不到单位，id:{}", companyId);

		return null;
	}

	/**
	 * 迁移医院单位
	 * 
	 * @param managerCompanyRelationList
	 */
	private void migrateHospitalCompany(
			List<ManagerCompanyRelationDO> managerCompanyRelationList) {
		// 过滤出医院单位的客户经理和单位关系数据
		List<ManagerCompanyRelationDO> list = this.filter(
				managerCompanyRelationList, ExamCompanyTypeEnum.normal);

		for (ManagerCompanyRelationDO managerCompanyRelationDO : list) {
			HospitalCompanyDO hospitalCompanyDO = hospitalCompanyMapper
					.selectByExamCompanyIdAndOrganizationId(
							managerCompanyRelationDO.getCompanyId(),
							managerCompanyRelationDO.getHospitalId());

			if (hospitalCompanyDO == null) {
				logger.warn(
						"【migrateHospitalCompany】在tb_hospital_company没有找到单位，company_id :{},hosp id : {}",
						managerCompanyRelationDO.getCompanyId(),
						managerCompanyRelationDO.getHospitalId());
				continue;
			}
			managerCompanyRelationDO.setNewCompanyId(hospitalCompanyDO.getId());
			managerCompanyRelationMapper.update(managerCompanyRelationDO);
		}
	}

	/**
	 * 迁移平台单位
	 * 
	 * @param managerCompanyRelationList
	 */
	private void migratePlatformCompany(
			List<ManagerCompanyRelationDO> managerCompanyRelationList) {
		// 过滤出所有的平台客户经理和单位关系数据
		List<ManagerCompanyRelationDO> list = this.filter(
				managerCompanyRelationList, ExamCompanyTypeEnum.P);

		for (ManagerCompanyRelationDO managerCompanyRelationDO : list) {
			// 迁移为渠道商单位id
			ChannelCompanyDO channelCompanyDO = channelCompanyMapper
					.selectByExamCompanyId(managerCompanyRelationDO
							.getCompanyId());
			
			if (channelCompanyDO == null) {
				logger.warn(
						"【migratePlatformCompany】在tb_channel_company没有找到单位，company_id :{}",
						managerCompanyRelationDO.getCompanyId());

				continue;
			}

			managerCompanyRelationDO.setNewCompanyId(channelCompanyDO.getId());
			managerCompanyRelationMapper.update(managerCompanyRelationDO);

		}
	}

	/**
	 * 迁移渠道商单位
	 * @param managerCompanyRelationList
	 */
	private void migrateChannelCompany(
			List<ManagerCompanyRelationDO> managerCompanyRelationList) {
		// 获取M散客单位平台客户经理和单位关系数据
		List<ManagerCompanyRelationDO> list = this.filter(
				managerCompanyRelationList, ExamCompanyTypeEnum.M);
		Map<Integer, String> hosMap = hospitalMapper.selectAll().stream().collect(Collectors.toMap(HospitalDO::getId, HospitalDO::getName));
		for (ManagerCompanyRelationDO managerCompanyRelationDO : list) {

			//获取关联的渠道商
			Integer organizationId = managerChannelRelMapper.selectChannelIdsByPlatformManagerId(managerCompanyRelationDO.getManagerId());
			if (organizationId == null){
				logger.info("当前客户经理没有关联渠道商managerCompanyRelationDO={}", JSON.toJSONString(managerCompanyRelationDO));
				continue;
			}
			ChannelCompanyDO channelCompanyDO;
			channelCompanyDO= channelCompanyMapper.selectByExamCompanyIdAndOrganizationId(managerCompanyRelationDO.getCompanyId(), organizationId);
			if (channelCompanyDO == null){
				//获取渠道商医院设置
				HospitalSettings settings = organizationSettingsMapper
						.getHospitalSettingByHospitalId(organizationId);
				//获取M单位
				ExamCompanyDO examCompanyDO = examCompanyMapper
						.selectById(managerCompanyRelationDO.getCompanyId());
				//构建channelCompanyDO
				channelCompanyDO = this.resolve(examCompanyDO, InitCompanyEnum.HOSPITAL_MTJK.getId(), settings, organizationId,hosMap);

				//插入新的渠道商
				channelCompanyMapper.insert(channelCompanyDO);
			}

			managerCompanyRelationDO.setNewCompanyId(channelCompanyDO.getId());
			managerCompanyRelationMapper.update(managerCompanyRelationDO);
		}

	}
	private ChannelCompanyDO resolve(ExamCompanyDO comp,
									 Integer platformCompId, HospitalSettings settings, Integer organizationId, Map<Integer,String> hashMap) {
		ChannelCompanyDO channel = new ChannelCompanyDO();
		channel.setDeleted(false);
		channel.setDiscount(1d);
		channel.setName(comp.getName());
		channel.setPinyin(PinYinUtil.getFirstSpell(channel.getName()));
		channel.setOrganizationId(organizationId);
		channel.setOrganizationName(hashMap.get(organizationId));
		channel.setPlatformCompanyId(platformCompId);

		channel.setTbExamCompanyId(comp.getId());
		channel.setDescription(comp.getDescription());// 设置地理描述
		this.setChannelPropertyFromOrg(settings, channel);

		return channel;
	}

	private void setChannelPropertyFromOrg(HospitalSettings settings,
										  ChannelCompanyDO t) {
		if (settings != null) {
			t.setSettlementMode(settings.getSettlementMode());
			t.setSendExamSms(settings.getSendExamSms());
			t.setSendExamSmsDays(settings.getSendExamSmsDays());
		} else {
			t.setSettlementMode(0);
			t.setSendExamSms(false);
			t.setSendExamSmsDays(1);
		}
	}

	private List<ManagerCompanyRelationDO> filter(
			List<ManagerCompanyRelationDO> managerCompanyRelationList,
			ExamCompanyTypeEnum typeEnum) {
		List<ManagerCompanyRelationDO> list = new ArrayList<ManagerCompanyRelationDO>();
		for (ManagerCompanyRelationDO DO : managerCompanyRelationList) {
			Integer type = this.getCompayType(DO.getCompanyId());
			if (type != null && type == typeEnum.getCode()) {
				list.add(DO);
			}
		}
		return list;
	}

	@Override
	public void cleanData() {
		logger.info("清洗tb_channel_company老数据开始");
		List<ManagerCompanyRelationDO> managerCompanyRelationList = managerCompanyRelationMapper
				.selectNewCompanyIdIsNotNull();
		migrateChannelCompany(managerCompanyRelationList);
		logger.info("清洗tb_channel_company老数据结束");
	}
}
