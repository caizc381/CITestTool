package com.mytijian.mediator.company.migrate.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mytijian.company.enums.ExamCompanyTypeEnum;
import com.mytijian.mediator.company.migrate.dao.ChannelCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.HospitalCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.PlatformCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.ChannelCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ExamCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.PlatformCompanyDO;
import com.mytijian.util.PinYinUtil;

@Service("pCompanyResolver")
public class PCompanyResolver {

	@Resource(name = "platformCompanyMapper")
	private PlatformCompanyMapper platformCompanyMapper;

	@Resource(name = "hospitalCompanyMapper")
	private HospitalCompanyMapper hospitalCompanyMapper;

	@Resource(name = "hospitalCompanyResolver")
	private HospitalCompanyResolver hospitalCompanyResolver;
	
	@Resource(name = "mCompanyResolver")
	private MCompanyResolver mCompanyResolver;
	
	@Resource(name = "channelCompanyMapper")
	private ChannelCompanyMapper channelCompanyMapper;

	/**
	 * 处理平台单位
	 * @param companyList
	 */
	public void handlePlatformCompany(List<ExamCompanyDO> companyList) {
		// 筛选出P单位
		List<ExamCompanyDO> pCompOldList = companyList
				.stream()
				.filter(p -> p.getType().intValue() == ExamCompanyTypeEnum.P
						.getCode()).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(pCompOldList)) {
			for (ExamCompanyDO pComp : pCompOldList) {
				// 构建新平台单位模型
				PlatformCompanyDO p = new PlatformCompanyDO();
				p.setName(pComp.getName());
				p.setPinyin(PinYinUtil.getFirstSpell(p.getName()));
				p.setInit(null);
				p.setDeleted(false);
				p.setDescription(pComp.getDescription());
				p.setPrefix(pComp.getPrefix()); // 员工号前缀
				p.setTbExamCompanyId(pComp.getId());
				
				// 查询P单位下面的医院单位
				List<HospitalCompanyDO> hospCompLists = hospitalCompanyResolver
						.getHospCompany(pComp);
				// 设置员工号导入属性
				if (CollectionUtils.isNotEmpty(hospCompLists)) {
					p.setEmployeeImport(hospCompLists.get(0)
							.getEmployeeImport());
				} else {
					p.setEmployeeImport(false);
				}
				
				// 插入平台单位
				platformCompanyMapper.insert(p);

				// 插入医院单位
				List<HospitalCompanyDO> hospCompList = this
						.getHospCompListUnderP(pComp, p.getId());
				if (CollectionUtils.isNotEmpty(hospCompList)) {
					hospitalCompanyMapper.insertList(hospCompList);
				}
				
				// 插入渠道商单位
				ChannelCompanyDO channelCompanyDO = mCompanyResolver
						.getChannelCompanyDO(pComp, p.getId());
				channelCompanyMapper.insert(channelCompanyDO);
			}
		}
	}

	/**
	 * 获取平台单位下的医院单位
	 * @param pComp
	 * @param pCompId
	 * @return
	 */
	public List<HospitalCompanyDO> getHospCompListUnderP(ExamCompanyDO pComp,
			Integer pCompId) {
		List<HospitalCompanyDO> hospCompList = hospitalCompanyResolver
				.getHospCompany(pComp);

		if (CollectionUtils.isNotEmpty(hospCompList)) {
			// 设置平台单位id
			for (HospitalCompanyDO hospComp : hospCompList) {
				hospComp.setPlatformCompanyId(pCompId);
			}

			return hospCompList;
		} else {
			return Collections.emptyList();
		}

	}

	public List<PlatformCompanyDO> resolvePCompany(
			List<ExamCompanyDO> pCompOldList) {
		if (CollectionUtils.isNotEmpty(pCompOldList)) {
			List<PlatformCompanyDO> list = new ArrayList<PlatformCompanyDO>();
			for (ExamCompanyDO pComp : pCompOldList) {
				PlatformCompanyDO p = new PlatformCompanyDO();
				p.setName(pComp.getName());
				p.setPinyin(PinYinUtil.getFirstSpell(p.getName()));
				p.setInit(null);
				p.setDeleted(false);
				p.setDescription(pComp.getDescription());
				p.setEmployeeImport(false);// 员工号导入
				p.setPrefix(pComp.getPrefix()); // 员工号前缀
				p.setTbExamCompanyId(pComp.getId());
				list.add(p);
			}

			return list;
		} else {
			return Collections.emptyList();
		}

	}

	/**
	 * tb_platform_company数据的插入和更新
	 * 
	 * @param examCompanyDO
	 */
	public void updatePlatformCompany(ExamCompanyDO examCompanyDO) {
		PlatformCompanyDO platformCompanyDO = platformCompanyMapper
				.selectByExamCompanyId(examCompanyDO.getId());

		// 新建平台单位
		if (platformCompanyDO == null) {
			List<ExamCompanyDO> examCompanyDOList = Lists.newArrayList();
			examCompanyDOList.add(examCompanyDO);
			this.handlePlatformCompany(examCompanyDOList);
		} else {
			List<PlatformCompanyDO> list = this.resolvePCompany(Arrays
					.asList(examCompanyDO));
			if (CollectionUtils.isNotEmpty(list)) {
				list.get(0).setId(platformCompanyDO.getId()); // 设置id
				// 更新平台单位
				platformCompanyMapper.updatePlatformCompany(list.get(0));

				//TODO  更新平台单位下的医院单位。P单位属性更改会影响医院单位吗？
				List<HospitalCompanyDO> hospCompList = this
						.getHospCompListUnderP(examCompanyDO,
								platformCompanyDO.getId());
				
				if (CollectionUtils.isNotEmpty(hospCompList)) {
					hospitalCompanyResolver.updateHospCompany(hospCompList);
				}
			}
		}
	}
}
