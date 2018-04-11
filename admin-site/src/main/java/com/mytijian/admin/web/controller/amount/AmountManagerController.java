package com.mytijian.admin.web.controller.amount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Optional;
import com.mytijian.account.model.Account;
import com.mytijian.account.service.AccountService;
import com.mytijian.account.service.UserService;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.web.dto.ContactDTO;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.admin.web.vo.amount.AmountVO;
import com.mytijian.admin.web.vo.amount.RechargeVO;
import com.mytijian.gotone.api.HospitalContactMessageConfigService;
import com.mytijian.gotone.api.HospitalContactService;
import com.mytijian.gotone.api.model.HospitalContactAddReq;
import com.mytijian.gotone.api.model.HospitalContactAddResp;
import com.mytijian.gotone.api.model.HospitalContactMessageConfigReq;
import com.mytijian.gotone.api.model.HospitalContactReq;
import com.mytijian.gotone.api.model.HospitalContactResp;
import com.mytijian.gotone.api.model.beans.HospitalContact;
import com.mytijian.gotone.api.model.enums.NotifyTypeEnum;
import com.mytijian.gotone.api.model.enums.SendTypeEnum;
import com.mytijian.organization.model.Organization;
import com.mytijian.organization.param.OrganizationQuery;
import com.mytijian.organization.service.OrganizationService;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.trade.account.service.constant.TradeSubAccountType;
import com.mytijian.trade.monitor.model.AmountMonitorRule;
import com.mytijian.trade.monitor.param.AmountMonitorRuleQuery;
import com.mytijian.trade.monitor.service.AmountMonitorRuleService;
import com.mytijian.trade.pay.service.RedisParentCardAmountService;
import com.mytijian.trade.recharge.param.RechargeQueryParam;
import com.mytijian.trade.recharge.param.RechargeRequest;
import com.mytijian.trade.recharge.result.RechargeQueryResult;
import com.mytijian.trade.recharge.result.RechargeResponse;
import com.mytijian.trade.recharge.service.UnifyRechargeQueryService;
import com.mytijian.trade.recharge.service.UnifyRechargeService;
import com.mytijian.util.AssertUtil;
import com.mytijian.util.PinYinUtil;

@Controller
@RequestMapping("/amount")
public class AmountManagerController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private AmountMonitorRuleService amountMonitorRuleService;

	@Autowired
	private HospitalContactService hospitalContactService;
	
	@Autowired
	private HospitalContactMessageConfigService hospitalContactMessageConfigService;

	@Autowired
	private HospitalService HospitalService;

	@Autowired
	private UserService userService;

	@Autowired
	private UnifyRechargeService unifyRechargeService;

	@Autowired
	private UnifyRechargeQueryService unifyRechargeQueryService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private RedisParentCardAmountService redisParentCardAmountService;
	
	
	@ResponseBody
	@RequestMapping("/config/update")
	public AmountVO config(@RequestParam("orgId") Integer orgId, @RequestParam("amount")Long amount, @RequestParam("enable")Integer enable) {
		Hospital hospital = HospitalService.getHospitalById(orgId);
		AmountMonitorRuleQuery query = new AmountMonitorRuleQuery();
		query.setTargetAccountIds(new ArrayList<>());
		query.getTargetAccountIds().add(hospital.getDefaultManagerId());

		List<AmountMonitorRule> rules = amountMonitorRuleService.list(query);

		if (AssertUtil.isEmpty(rules)) {
			// add
			AmountMonitorRule rule = new AmountMonitorRule();
			rule.setTargetAccountId(hospital.getDefaultManagerId());
			rule.setThreshold(amount);
			rule.setEnable(enable);
			amountMonitorRuleService.addAmountMonitorRule(rule);
		} else {
			AmountMonitorRule rule = rules.get(0);
			rule.setThreshold(amount);
			rule.setEnable(enable);
			amountMonitorRuleService.updateAmountMonitorRule(rule);
		}

		AmountVO vo= buildVO(hospital, true);
		
		
		rules = amountMonitorRuleService.list(query);
		if (AssertUtil.isNotEmpty(rules)) {
			vo.setRule(rules.get(0));
		}
		return vo;
	}

	@ResponseBody
	@RequestMapping("/config")
	public AmountVO getConfigById(Integer orgId) {
		Hospital hospital = HospitalService.getHospitalById(orgId);
		AmountMonitorRuleQuery query = new AmountMonitorRuleQuery();
		query.setTargetAccountIds(new ArrayList<>());
		query.getTargetAccountIds().add(hospital.getDefaultManagerId());

		List<AmountMonitorRule> rules = amountMonitorRuleService.list(query);
		AmountVO vo= buildVO(hospital, true);
		if (AssertUtil.isNotEmpty(rules)) {
			vo.setRule(rules.get(0));
		}
		
		return vo;
	}

	@ResponseBody
	@RequestMapping(value = "/updateContact", method = RequestMethod.POST)
	public List<HospitalContact> updateContact(@RequestBody ContactDTO dto) {
		
		Integer orgId = dto.getOrgId();

		HospitalContactReq req = new HospitalContactReq();
		req.setNotifyTypeEnum(NotifyTypeEnum.AMOUNT_MONITOR_NOTIFY);
		req.setHospitalId(orgId);
		HospitalContactResp resp = hospitalContactService.queryByHospitalAndType(req);
		
		if (resp != null && AssertUtil.isNotEmpty(resp.getHospitalContactList())) {
			List<HospitalContact> currentList = resp.getHospitalContactList();

			List<HospitalContactAddReq> removes = new ArrayList<>();
			for (HospitalContact c : currentList) {
				HospitalContactAddReq f = new HospitalContactAddReq();
				f.setContactId(c.getContactId());
				f.setHospitalId(orgId);
				removes.add(f);
			}
			hospitalContactService.removeAndAddContactList(removes);
		}
		List<HospitalContact> contacts = dto.getContacts();
		if (contacts != null && contacts.size() > 0) {
			for (HospitalContact c : contacts) {
				HospitalContactAddReq add = new HospitalContactAddReq();
				add.setEmail(c.getEmail());
				add.setPhone(c.getPhone());
				add.setHospitalId(orgId);
						
				HospitalContactAddResp addresp = hospitalContactService.addHospitalContact(add);
				
				HospitalContactMessageConfigReq configReq = new HospitalContactMessageConfigReq();
				configReq.setContactId(addresp.getContactId());
				configReq.setHospitalId(orgId);
				configReq.setNotifyType(NotifyTypeEnum.AMOUNT_MONITOR_NOTIFY);
				configReq.setSendType(SendTypeEnum.email_and_phone);
				hospitalContactMessageConfigService.addHospitalContactMessageConfig(configReq);
				
			}
		}

		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/rech", method = { RequestMethod.POST, RequestMethod.GET })
	public RechargeResponse rech(@RequestParam("orgId") Integer orgId, @RequestParam("amount") Long amount,
			@RequestParam(name = "remark", required = false) String remark) {
		
		String currentUser = Optional.fromNullable(SessionUtil.getEmployee()).or(new Employee()).getEmployeeName();
		if (AssertUtil.isEmpty(currentUser)) {
			RechargeResponse resp = new RechargeResponse();
			resp.setSuccess(false);
			resp.setMessage("无法获取当前用户");
			return resp;
		}
		
		remark = (remark==null?"":remark) + "操作人: " + currentUser;
		RechargeRequest req = new RechargeRequest();
		Hospital hospital = HospitalService.getHospitalById(orgId);
		req.setAmount(amount);
		req.setTargetAccountId(hospital.getDefaultManagerId());
		req.setRemark(remark);
		req.setTargetSubaccountType(TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
		// XXX 获取 open
		// req.setOperator(); // currentUser
		RechargeResponse resp = unifyRechargeService.recharge(req);
		return resp;
	}

	@ResponseBody
	@RequestMapping("/rechquery")
	public PageView<RechargeVO> rechlist(Integer orgId, Integer currentPage, Integer pageSize, Integer rowCount) {

		
		Page page = new Page();
		page.setRowCount(Optional.fromNullable(rowCount).or(-1));
		page.setPageSize(Optional.fromNullable(pageSize).or(20));
		page.setCurrentPage(Optional.fromNullable(currentPage).or(1));

		RechargeQueryParam param = new RechargeQueryParam();
		param.setPage(page);
		
	
		if (orgId != null) {
			Hospital hospital = HospitalService.getHospitalById(orgId);
			if (hospital!=null) {
				param.setTargetAccountId(hospital.getDefaultManagerId());
			}
		}
		PageView<RechargeQueryResult> rechPageView = unifyRechargeQueryService.queryByPage(param);

		List<RechargeQueryResult> result = rechPageView.getRecords();

		List<RechargeVO> vos = new ArrayList<>();
		if (AssertUtil.isNotEmpty(result)) {
			result.forEach(x -> {
				RechargeVO vo = new RechargeVO();
				vo.setResult(x);
				OrganizationQuery q = new OrganizationQuery();
				q.setDeaultManagerId(x.getTargetAccoutId());
				Organization p = organizationService.getOrganizationByDefaultManageId(x.getTargetAccoutId());
				if (p != null) {
					vo.setHospitalName(p.getName());
					if (p.getAddress() != null) {
						vo.setAddress(p.getAddress().getBriefAddress());
					}
				}
				String userName = userService.getOneUsername(x.getTargetAccoutId());
				vo.setUserName(userName);

				
				Account manager = accountService.getAccountById(x.getTargetAccoutId());
				vo.setManager(manager);

				vos.add(vo);
			});
		}

		PageView<RechargeVO> resultPage = new PageView<>();
		resultPage.setPage(rechPageView.getPage());
		resultPage.setRecords(vos);
		return resultPage;
	}

	@ResponseBody
	@RequestMapping("/list")
	public List<AmountVO> list() {

		List<Hospital> orgs = HospitalService.getOrganizationList(2);
		List<Integer> managers = new ArrayList<>();

		List<AmountVO> vos = new ArrayList<>();

		orgs.forEach(x -> {
			managers.add(x.getDefaultManagerId());
			AmountVO vo = buildVO(x, false);
			vos.add(vo);
		});

		AmountMonitorRuleQuery rulequery = new AmountMonitorRuleQuery();
		//rulequery.setTargetSubaccountType(TradeSubAccountType.TRADE_PARENT_CARD_ACCOUNT);
		rulequery.setTargetAccountIds(managers);

		List<AmountMonitorRule> rules = amountMonitorRuleService.list(rulequery);
		Map<Integer, AmountMonitorRule> ruleMap = list2map(rules);
		vos.forEach(x -> {
			x.setRule(ruleMap.get(x.getOrg().getDefaultManagerId()));
		});

		return vos;
	}

	private AmountVO buildVO(Hospital x, boolean selectConctacts) {
		Organization org = new Organization();
		BeanUtils.copyProperties(x, org);
		org.setAddress(x.getAddress());
		
		AmountVO vo = new AmountVO();

		Account account = accountService.getAccountById(x.getDefaultManagerId());
		String loginName = userService.getOneUsername(x.getDefaultManagerId());
		vo.setAmount(redisParentCardAmountService.getParentCardAmountByAccountId(x.getDefaultManagerId()));
		vo.setLoginName(loginName);
		vo.setManage(account);
		vo.setOrg(org);
		String pinyin = PinYinUtil.getFirstSpell(x.getName());
		vo.setPinyin(pinyin);
		if (selectConctacts) {
		HospitalContactReq req = new HospitalContactReq();
		req.setNotifyTypeEnum(NotifyTypeEnum.AMOUNT_MONITOR_NOTIFY);
		req.setHospitalId(x.getId());
			try {
				HospitalContactResp resp = hospitalContactService.queryByHospitalAndType(req);
				if (resp != null && AssertUtil.isNotEmpty(resp.getHospitalContactList())) {
					vo.setContacts(resp.getHospitalContactList());
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return vo;
	}

	private Map<Integer, AmountMonitorRule> list2map(List<AmountMonitorRule> list) {
		list = Optional.fromNullable(list).or(new ArrayList<>());
		Map<Integer, AmountMonitorRule> map = new HashMap<>();
		list.stream().forEach(x -> {
			map.put(x.getTargetAccountId(), x);
		});
		return map;
	}

}
