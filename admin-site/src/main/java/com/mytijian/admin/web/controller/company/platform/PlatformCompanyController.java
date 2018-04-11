package com.mytijian.admin.web.controller.company.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mytijian.account.model.Account;
import com.mytijian.account.service.AccountService;
import com.mytijian.admin.web.controller.company.CompanyUtil;
import com.mytijian.admin.web.vo.company.channel.ChannelManagerVO;
import com.mytijian.admin.web.vo.company.platform.PlatformCompanyVO;
import com.mytijian.company.channel.service.ChannelCompanyService;
import com.mytijian.company.channel.service.model.ChannelCompany;
import com.mytijian.company.model.ManagerExamCompanyRelation;
import com.mytijian.company.model.PlatformCompHospitalApply;
import com.mytijian.company.platform.service.PlatformCompanyService;
import com.mytijian.company.platform.service.model.PlatformCompany;
import com.mytijian.company.relation.service.CompanyManagerRelService;
import com.mytijian.company.service.CompanyApplyLogService;
import com.mytijian.company.service.ManagerCompanyRelationService;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.resource.service.OrganizationSettingsService;

@Controller
@RequestMapping("/company/platform")
public class PlatformCompanyController {

	@Resource(name = "platformCompanyService")
	private PlatformCompanyService platformCompanyService;

	@Resource(name = "companyApplyLogService")
	private CompanyApplyLogService companyApplyLogService;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;

	@Resource(name = "channelCompanyService")
	private ChannelCompanyService channelCompanyService;

	@Resource(name = "accountService")
	private AccountService accountService;

	@Resource(name = "managerCompanyRelationService")
	private ManagerCompanyRelationService managerCompanyRelationService;

	@Resource(name = "companyManagerRelService")
	private CompanyManagerRelService companyManagerRelService;

	@Resource(name = "organizationSettingsService")
	private OrganizationSettingsService organizationSettingsService;

	@RequestMapping(value = "/addCompany", method = RequestMethod.POST)
	@ResponseBody
	public PlatformCompany addCompany(
			@RequestParam(value = "companyName", required = true) String companyName) {

		if (StringUtils.isEmpty(companyName)) {
			throw new IllegalStateException("单位名为空");
		}

		return platformCompanyService.addPlatformCompany(companyName);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<PlatformCompany> getCompanyList(
			@RequestParam(value = "keyword", required = false) String keyword) {
		// 查询所有平台单位
		List<PlatformCompany> platformCompanyList = platformCompanyService
				.listPlatformCompanyByName(null);

		if (StringUtils.isNotBlank(keyword)) {
			platformCompanyList = platformCompanyList
					.stream()
					.filter(p -> p.getName().contains(keyword)
							|| p.getPinyin().contains(keyword))
					.collect(Collectors.toList());
		}
		return platformCompanyList;
	}

	@RequestMapping(value = "/companyInfo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCompanyInfo(
			@RequestParam(value = "companyId", required = true) Integer companyId) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 1、单位信息
		PlatformCompany platformCompany = platformCompanyService
				.getPlatformCompanyById(companyId);
		map.put("platformCompany", platformCompany);

		// 2、申请的体检中心
		List<PlatformCompHospitalApply> hospitalApplyList = companyApplyLogService
				.getHospitalWithApply(companyId);
		map.put("hospitalApplyList", hospitalApplyList);

		// 3、所有体检中心列表
		map.put("allHospList",
				CompanyUtil.resolveHospitalVOList(this.buildHospList()));

		// 4、所有渠道商列表
		map.put("allChannelList", CompanyUtil
				.resolveHospitalVOList(hospitalService
						.getOrganizationList(OrganizationTypeEnum.CHANNEL
								.getCode())));

		// 5、支持的渠道商
		List<ChannelCompany> channelCompanyList = channelCompanyService
				.listCompanyByPlatformCompanyId(companyId);

		if (CollectionUtils.isNotEmpty(channelCompanyList)) {
			Set<Integer> channelIdSet = new HashSet<Integer>();
			List<ManagerExamCompanyRelation> managerExamCompanyRelationList = new ArrayList<ManagerExamCompanyRelation>();
			for (ChannelCompany channelCompany : channelCompanyList) {
				channelIdSet.add(channelCompany.getOrganizationId());

				// 6 已经关联的客户经理
				List<ManagerExamCompanyRelation> compManagerRelationList = managerCompanyRelationService
						.getManagerCompanyRelationListForManage(channelCompany
								.getId());

				for (ManagerExamCompanyRelation v : compManagerRelationList) {
					Account account = accountService.getAccountById(v
							.getManagerId());
					v.setManagerName(account != null ? account.getName() : "");
					managerExamCompanyRelationList.add(v);
				}

			}

			List<Hospital> supportChannelList = hospitalService
					.getHospitalsByIds(new ArrayList<>(channelIdSet));
			map.put("supportChannelList", supportChannelList);

			map.put("companyManagerList", managerExamCompanyRelationList);
		}

		// 6 渠道支持的客户经理。独立接口
		// managerChannelRelService.listManagerByChannel(organizationId);

		return map;
	}

	private List<Hospital> buildHospList() {
		List<Hospital> hospitalList = hospitalService
				.getOrganizationList(OrganizationTypeEnum.HOSPITAL.getCode());
		List<Integer> hospitalIdList = new ArrayList<Integer>();
		for (Hospital hospital : hospitalList) {
			hospitalIdList.add(hospital.getId());
		}

		List<HospitalSettings> hospitalSettingList = organizationSettingsService
				.listHospitalSettingByHospitalIdList(hospitalIdList);
		Map<Integer, HospitalSettings> setMap = new HashMap<Integer, HospitalSettings>();
		for (HospitalSettings settings : hospitalSettingList) {
			setMap.put(settings.getHospitalId(), settings);
		}

		for (Hospital hospital : hospitalList) {
			hospital.setSettings(setMap.get(hospital.getId()));
		}
		return hospitalList;
	}

	@RequestMapping(value = "/updateCompanyAndHospApply", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateCompanyAndHospApply(@RequestBody PlatformCompanyVO vo) {
		PlatformCompany updateCompany = platformCompanyService
				.getPlatformCompanyById(vo.getPlatformCompany().getId());
		// 1、更新单位
		if (!updateCompany.getName().equals(vo.getPlatformCompany().getName())) {
			updateCompany.setName(vo.getPlatformCompany().getName());
			platformCompanyService.updatePlatformCompany(updateCompany);
		}

		companyApplyLogService.apply(updateCompany.getId(),
				vo.getHospitalList());

	}

	@RequestMapping(value = "/updateChannelAndManager", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateChannelAndManager(@RequestBody PlatformCompanyVO vo) {
		PlatformCompany updateCompany = platformCompanyService
				.getPlatformCompanyById(vo.getPlatformCompany().getId());
		// 1、更新单位
		if (!updateCompany.getName().equals(vo.getPlatformCompany().getName())) {
			updateCompany.setName(vo.getPlatformCompany().getName());
			platformCompanyService.updatePlatformCompany(updateCompany);
		}

		// 2、创建渠道单位
		Integer pCompId = updateCompany.getId();
		if (CollectionUtils.isNotEmpty(vo.getChannelList())) {
			List<Integer> channelIdList = getChannelListByPlatformCompany(pCompId);

			for (Integer channelId : vo.getChannelList()) {
				if (!channelIdList.contains(channelId)) {
					ChannelCompany company = new ChannelCompany();
					company.setName(updateCompany.getName());
					company.setOrganizationId(channelId);
					channelCompanyService.addChannelCompany(company, pCompId);
				}
			}
		}

		// 3、单位客户关系
		List<ChannelManagerVO> channelManagerVOList = vo
				.getChannelManagerList();

		if (CollectionUtils.isNotEmpty(channelManagerVOList)) {
			for (ChannelManagerVO channelManagerVO : channelManagerVOList) {
				Optional<ChannelCompany> option = this
						.getChannelCompanyByPCompId(pCompId, channelManagerVO);
				// 新加的
				if (channelManagerVO.getAddOrRemove() != null
						&& channelManagerVO.getAddOrRemove() == 1) {
					if (option.isPresent()) {
						companyManagerRelService.addCompanyManagerRel(
								channelManagerVO.getId(), option.get().getId(),
								channelManagerVO.getOrganizationId(),false);
					}
				} else if (channelManagerVO.getAddOrRemove() != null
						&& channelManagerVO.getAddOrRemove() == -1) {// 解除关系
					if (option.isPresent()) {
						companyManagerRelService
								.deleteManagerExamCompanyRelation(option.get()
										.getId(), channelManagerVO
										.getOrganizationId(), channelManagerVO
										.getId());
					}
				}

			}
		}

	}

	private Optional<ChannelCompany> getChannelCompanyByPCompId(
			Integer pCompId, ChannelManagerVO channelManagerVO) {
		List<ChannelCompany> channelCompanyList = channelCompanyService
				.listCompanyByPlatformCompanyId(pCompId);
		Optional<ChannelCompany> option = channelCompanyList
				.stream()
				.filter(p -> p.getOrganizationId().intValue() == channelManagerVO
						.getOrganizationId()).findFirst();
		return option;
	}

	private List<Integer> getChannelListByPlatformCompany(Integer pCompanyId) {
		List<ChannelCompany> channelCompanies = channelCompanyService
				.listCompanyByPlatformCompanyId(pCompanyId);
		List<Integer> channelIdList = new ArrayList<Integer>();
		for (ChannelCompany channelCompany : channelCompanies) {
			channelIdList.add(channelCompany.getOrganizationId());
		}
		return channelIdList;
	}
}
