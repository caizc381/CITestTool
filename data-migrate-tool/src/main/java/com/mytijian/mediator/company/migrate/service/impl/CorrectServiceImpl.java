package com.mytijian.mediator.company.migrate.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.company.migrate.correct.dao.CompanyApplyLogMapper;
import com.mytijian.mediator.company.migrate.correct.dao.CrmHisCompanyMapper;
import com.mytijian.mediator.company.migrate.correct.dao.dataobj.CrmHisCompanyDO;
import com.mytijian.mediator.company.migrate.dao.HospitalCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalCompanyDO;
import com.mytijian.mediator.company.migrate.dao.resource.OrganizationSettingsMapper;
import com.mytijian.mediator.company.migrate.service.CorrectService;
import com.mytijian.mediator.company.migrate.service.constant.InitCompanyEnum;
import com.mytijian.resource.model.HospitalSettings;

@Service("correctService")
public class CorrectServiceImpl implements CorrectService {
	private Logger logger = LoggerFactory.getLogger(CorrectServiceImpl.class);

	@Resource(name = "companyApplyLogMapper")
	private CompanyApplyLogMapper companyApplyLogMapper;

	@Resource(name = "crmHisCompanyMapper")
	private CrmHisCompanyMapper crmHisCompanyMapper;
	
	@Resource(name = "organizationSettingsMapper")
	private OrganizationSettingsMapper organizationSettingsMapper;
	
	@Resource(name = "hospitalCompanyMapper")
	private HospitalCompanyMapper hospitalCompanyMapper;

	@Override
//	@Transactional(rollbackFor = Exception.class)
	public void updateCompanyApplyLog() {
		companyApplyLogMapper.correctCompanyApplyLog();
	}

	@Override
//	@Transactional(rollbackFor = Exception.class)
	public void correctCrmHisCompanyFor1585() {
		List<CrmHisCompanyDO> guestCompanyList = crmHisCompanyMapper
				.selectGuestCompany();
		if (CollectionUtils.isNotEmpty(guestCompanyList)) {
			for (CrmHisCompanyDO DO : guestCompanyList) {
				Integer hospId = DO.getHospitalId();
				HospitalSettings settings = organizationSettingsMapper
						.getHospitalSettingByHospitalId(hospId);
					     
				if (settings == null) {
					System.out.println("医院设置为空，hosp id " + hospId);
					logger.warn("医院设置为空，hosp id " + hospId);
					continue;
				}

				// 查询tb_hospital_company.id
				HospitalCompanyDO hosDo = null;
				if (DO.getCompanyName().equals(
						settings.getGuestOnlineCompAlias())) {
					hosDo = hospitalCompanyMapper
							.selectByExamCompanyIdAndOrganizationId(
									InitCompanyEnum.HOSPITAL_GUEST_ONLINE
											.getTbExamCompanyId(), hospId);
				} else if (DO.getCompanyName().equals(
						settings.getGuestOfflineCompAlias())) {
					hosDo = hospitalCompanyMapper
							.selectByExamCompanyIdAndOrganizationId(
									InitCompanyEnum.HOSPITAL_GUEST_OFFLINE
											.getTbExamCompanyId(), hospId);
				} else if (DO.getCompanyName().equals(
						settings.getmGuestCompAlias())) {
					hosDo = hospitalCompanyMapper
							.selectByExamCompanyIdAndOrganizationId(
									InitCompanyEnum.HOSPITAL_MTJK
											.getTbExamCompanyId(), hospId);
				}

				// UPDATE NEW COMPANY ID = ? WHERE ID =
				Integer newCompanyId = hosDo != null ? hosDo.getId() : null;
				crmHisCompanyMapper.updateNewCompanyIdFor1585(DO.getId(),
						newCompanyId);

			}
		}

	}

	@Override
//	@Transactional(rollbackFor = Exception.class)
	public void correctCrmHisCompany() {
		crmHisCompanyMapper.updateNewCompanyId();
	}

}
