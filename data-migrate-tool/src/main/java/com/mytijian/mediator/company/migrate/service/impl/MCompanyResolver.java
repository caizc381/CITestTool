package com.mytijian.mediator.company.migrate.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mytijian.company.enums.ExamCompanyTypeEnum;
import com.mytijian.mediator.company.migrate.dao.ChannelCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.ChannelCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ExamCompanyDO;
import com.mytijian.mediator.company.migrate.dao.resource.OrganizationSettingsMapper;
import com.mytijian.mediator.company.migrate.service.constant.InitCompanyEnum;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.util.PinYinUtil;

@Service("mCompanyResolver")
public class MCompanyResolver {

	@Resource(name = "channelCompanyMapper")
	private ChannelCompanyMapper channelCompanyMapper;

	@Value("${mtjkOrgId}")
	private Integer mtjkOrgId;
	
	@Resource(name = "organizationSettingsMapper")
	private OrganizationSettingsMapper organizationSettingsMapper;

	/**
	 * 处理M单位
	 * @param companyList
	 * @return
	 */
	public List<ChannelCompanyDO> handleMCompany(List<ExamCompanyDO> companyList) {
		// 筛选出M单位
		List<ExamCompanyDO> mCompOldList = companyList
				.stream()
				.filter(p -> p.getType().intValue() == ExamCompanyTypeEnum.M
						.getCode()).collect(Collectors.toList());
		
		if (CollectionUtils.isNotEmpty(mCompOldList)) {
			List<ChannelCompanyDO> resultList = new ArrayList<ChannelCompanyDO>();
			HospitalSettings settings = organizationSettingsMapper
					.getHospitalSettingByHospitalId(mtjkOrgId);

			for (ExamCompanyDO comp : mCompOldList) {
				ChannelCompanyDO channel = this.resolve(comp,
						InitCompanyEnum.HOSPITAL_MTJK.getId(), settings);
				resultList.add(channel);
			}

			return resultList;
		} else {
			return Collections.emptyList();
		}
	}

	public void setChannelPropertyFromOrg(HospitalSettings settings,
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

	/**
	 * tb_channel_company数据的插入和更新
	 * 
	 * @param examCompanyDO
	 */
	public void changeChannelCompanyData(ExamCompanyDO examCompanyDO) {
		// 从新的渠道商单位表查询是否存在
		ChannelCompanyDO channelCompanyDO = channelCompanyMapper
				.selectByExamCompanyId(examCompanyDO.getId());

		List<ChannelCompanyDO> channelCompanyDOList = handleMCompany(Arrays
				.asList(examCompanyDO));

		if (channelCompanyDO == null) {
			channelCompanyMapper.insertList(channelCompanyDOList);
		} else {
			for (ChannelCompanyDO channlComDo : channelCompanyDOList) {
				channlComDo.setId(channelCompanyDO.getId()); // 设置渠道商单位主键
				channelCompanyMapper.updateChannelCompany(channlComDo);
			}

		}

	}

	/**
	 * 平台单位迁移时，在渠道商（每天健康）建单位。
	 * 
	 * @param examCompanyDO
	 * @param platformCompId
	 *            tb_platform_company.id
	 * @return
	 */
	public ChannelCompanyDO getChannelCompanyDO(ExamCompanyDO comp,
			Integer platformCompId) {
		HospitalSettings settings = organizationSettingsMapper
				.getHospitalSettingByHospitalId(mtjkOrgId);
		return this.resolve(comp, platformCompId, settings);
	}

	private ChannelCompanyDO resolve(ExamCompanyDO comp,
			Integer platformCompId, HospitalSettings settings) {
		ChannelCompanyDO channel = new ChannelCompanyDO();
		channel.setDeleted(false);
		channel.setDiscount(1d);
		channel.setName(comp.getName());
		channel.setPinyin(PinYinUtil.getFirstSpell(channel.getName()));
		channel.setOrganizationId(mtjkOrgId);
		channel.setOrganizationName("每天健康管理");
		channel.setPlatformCompanyId(platformCompId);

		channel.setTbExamCompanyId(comp.getId());
		channel.setDescription(comp.getDescription());// 设置地理描述

		this.setChannelPropertyFromOrg(settings, channel);

		return channel;
	}
}
