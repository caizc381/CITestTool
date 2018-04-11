package com.mytijian.admin.web.controller.company.channel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mytijian.account.model.Account;
import com.mytijian.account.service.AccountService;
import com.mytijian.account.service.ManagerChannelRelService;
import com.mytijian.admin.web.controller.company.CompanyUtil;
import com.mytijian.admin.web.vo.company.HospitalVO;
import com.mytijian.admin.web.vo.company.channel.ChannelCompanyVO;
import com.mytijian.admin.web.vo.company.channel.ChannelManagerVO;
import com.mytijian.company.channel.service.ChannelCompanyService;
import com.mytijian.company.channel.service.model.ChannelCompany;
import com.mytijian.company.model.ManagerExamCompanyRelation;
import com.mytijian.company.relation.service.CompanyManagerRelService;
import com.mytijian.company.service.ManagerCompanyRelationService;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.service.HospitalService;

@Controller
@RequestMapping("/company/channel")
public class ChannelCompanyController {

	@Resource(name = "channelCompanyService")
	private ChannelCompanyService channelCompanyService;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;

	@Resource(name = "managerCompanyRelationService")
	private ManagerCompanyRelationService managerCompanyRelationService;

	@Resource(name = "accountService")
	private AccountService accountService;

	@Resource(name = "managerChannelRelService")
	private ManagerChannelRelService managerChannelRelService;

	@Resource(name = "companyManagerRelService")
	private CompanyManagerRelService companyManagerRelService;

	/**
	 * 渠道商单位列表
	 * 
	 * @param organizationId
	 * @param keyword
	 * @return
	 */
	@RequestMapping(value = "/companyList", method = RequestMethod.GET)
	@ResponseBody
	public List<ChannelCompany> getCompanyList(
			@RequestParam(value = "organizationId", required = false) Integer organizationId,
			@RequestParam(value = "keyword", required = false) String keyword) {
		// 1、查询平台单位为【每天健康】的渠道单位
		List<ChannelCompany> channelCompanyList = channelCompanyService
				.listCompanyByPlatformCompanyId(3);

		// 2、根据条件筛选
		channelCompanyList = this.filter(organizationId, keyword,
				channelCompanyList);
		// 3、排序
		this.orderByCreatedDateDesc(channelCompanyList);
		return channelCompanyList;
	}

	private void orderByCreatedDateDesc(List<ChannelCompany> channelCompanyList) {
		channelCompanyList.sort(new Comparator<ChannelCompany>() {

			@Override
			public int compare(ChannelCompany o1, ChannelCompany o2) {
				if (o1.getGmtCreated() == null || o2.getGmtCreated() == null) {
					return 1;
				}
				return o2.getGmtCreated().compareTo(o1.getGmtCreated());
			}
		});
	}

	private List<ChannelCompany> filter(Integer organizationId, String keyword,
			List<ChannelCompany> channelCompanyList) {
		if (organizationId != null) {
			channelCompanyList = channelCompanyList
					.stream()
					.filter(p -> p.getOrganizationId().intValue() == organizationId
							.intValue()).collect(Collectors.toList());
		}

		// 拼音简写搜索单位
		if (StringUtils.isNotBlank(keyword)) {
			channelCompanyList = channelCompanyList
					.stream()
					.filter(p -> p.getName().contains(keyword)
							|| p.getPinyin().contains(keyword))
					.collect(Collectors.toList());
		}
		return channelCompanyList;
	}

	/**
	 * 所有渠道列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/channelList", method = RequestMethod.GET)
	@ResponseBody
	public List<HospitalVO> getChannelList() {
		return CompanyUtil.resolveHospitalVOList(hospitalService
				.getOrganizationList(OrganizationTypeEnum.CHANNEL.getCode()));
	}

	/**
	 * 填加渠道商单位
	 * 
	 * @param companyName
	 * @param organizationId
	 */
	@RequestMapping(value = "/addCompany", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void addCompany(
			@RequestParam(value = "companyName", required = true) String companyName,
			@RequestParam(value = "organizationId", required = true) Integer organizationId) {

		ChannelCompany channelCompany = new ChannelCompany();
		channelCompany.setName(companyName);
		channelCompany.setOrganizationId(organizationId);
		channelCompanyService.addChannelCompany(channelCompany);
	}

	/**
	 * 查询渠道下的平台客户经理
	 * 
	 * @param organizationId
	 * @return
	 */
	@RequestMapping(value = "/getManagerByChannel", method = RequestMethod.GET)
	@ResponseBody
	public List<ChannelManagerVO> getManagerByChannel(
			@RequestParam(value = "organizationId", required = true) Integer organizationId) {
		List<Account> list = managerChannelRelService
				.listManagerByChannel(organizationId);
		List<ChannelManagerVO> voList = new ArrayList<ChannelManagerVO>();

		for (Account account : list) {
			ChannelManagerVO vo = new ChannelManagerVO();
			vo.setId(account.getId());
			vo.setName(account.getName());
			vo.setOrganizationId(organizationId);
			Hospital hospital = hospitalService
					.getHospitalBaseInfoById(organizationId);
			if (hospital != null) {
				vo.setOrganizationName(hospital.getName());
			}
			voList.add(vo);

		}
		return voList;
	}

	@RequestMapping(value = "/companyInfo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCompany(
			@RequestParam(value = "companyId", required = true) Integer companyId) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 1 单位信息
		ChannelCompany channelCompany = channelCompanyService
				.getChannelCompanyById(companyId);
		map.put("channelCompany", channelCompany);

		// 2 已经关联的客户经理
		List<ManagerExamCompanyRelation> compManagerRelationList = managerCompanyRelationService
				.getManagerCompanyRelationListForManage(companyId);

		List<ManagerExamCompanyRelation> managerExamCompanyRelationList = new ArrayList<ManagerExamCompanyRelation>();
		for (ManagerExamCompanyRelation v : compManagerRelationList) {
			Account account = accountService.getAccountById(v.getManagerId());
			v.setManagerName(account != null ? account.getName() : "");
			managerExamCompanyRelationList.add(v);
		}

		map.put("companyManagerList", managerExamCompanyRelationList);

		// 3 渠道商支持的客户经理
		List<Account> managerList = managerChannelRelService
				.listManagerByChannel(channelCompany.getOrganizationId());

		map.put("channelManagerList", managerList);
		return map;
	}

	@RequestMapping("/update")
	@ResponseStatus(value = HttpStatus.OK)
	public void updateChannelCompany(@RequestBody ChannelCompanyVO vo) {

		// 1、更新单位名
		ChannelCompany updateCompany = channelCompanyService
				.getChannelCompanyById(vo.getChannelCompany().getId());
		if (!vo.getChannelCompany().getName().equals(updateCompany.getName())) {
			updateCompany.setName(vo.getChannelCompany().getName());
			channelCompanyService.updateChannelCompany(updateCompany);
		}

		for (ManagerExamCompanyRelation relation : vo.getCompanyManagerList()) {
			relation.setNewCompanyId(updateCompany.getId());
			relation.setAsAccountCompany(false);
			relation.setStatus(true);
			relation.setHospitalId(updateCompany.getOrganizationId());
		}

		// 2、更新关系
		companyManagerRelService.updateCompanyManagerRel(null,
				updateCompany.getOrganizationId(), updateCompany.getId(),
				vo.getCompanyManagerList());

	}
	
	@RequestMapping(value = "/openCompanyAndMealSync/{hospitalId}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void initCompany(@PathVariable Integer hospitalId) {
		// 开通单位同步
		HospitalSettings setting = hospitalService
				.getHospitalSettingsById(hospitalId);
		setting.setOpenSyncCompany(true);
		// 开启套餐同步
		setting.setOpenSyncMeal(true);
		hospitalService.updateOrganizationSetting(setting);
	}
}
