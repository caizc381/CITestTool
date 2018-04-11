package com.mytijian.admin.web.controller.amount;

import static com.mytijian.account.exceptions.AccountException.USER_NAME_EXIT;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mytijian.account.dto.ManagerDto;
import com.mytijian.account.enums.AccountSourceEnum;
import com.mytijian.account.enums.AccountTypeEnum;
import com.mytijian.account.enums.RoleEnum;
import com.mytijian.account.exceptions.AccountException;
import com.mytijian.account.exceptions.LoginFailedException;
import com.mytijian.account.model.Account;
import com.mytijian.account.model.ManagerSettings;
import com.mytijian.account.model.User;
import com.mytijian.account.service.AccountService;
import com.mytijian.account.service.LoginService;
import com.mytijian.account.service.ManagerChannelRelService;
import com.mytijian.account.service.ManagerService;
import com.mytijian.account.service.UserService;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.web.util.RandomNumberUtil;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.admin.web.vo.account.ManagerVo;
import com.mytijian.uic.annotation.LoginRequired;
import com.mytijian.card.model.Card;
import com.mytijian.card.service.CardService;
import com.mytijian.gotone.api.SmsService;
import com.mytijian.gotone.api.model.SmsReq;
import com.mytijian.gotone.api.model.enums.TemplateCodeEnum;
import com.mytijian.payment.enums.TradeTypeEnum;
import com.mytijian.payment.exceptions.RechargeException;
import com.mytijian.payment.model.PaymentRecord;
import com.mytijian.payment.service.PaymentRecordService;
import com.mytijian.payment.service.PaymentService;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.model.Hospital;
import com.mytijian.util.AssertUtil;
import com.mytijian.web.intercepter.Token;

@Controller("managerController")
@RequestMapping("/manager")
public class ManagerController {
	
	private final static Logger logger = LoggerFactory
			.getLogger(ManagerController.class);
	
	@Resource(name = "managerService")
	private ManagerService managerService;
	
	@Resource(name = "loginService")
	private LoginService loginService;
	
	@Resource(name = "accountService")
	private AccountService accountService;
	
	@Resource(name = "userService")
	private UserService userService;
	
	@Resource(name = "smsService")
	private SmsService smsService;
	
	@Resource(name = "cardService")
	private CardService cardService;
	
	@Resource(name="paymentRecordService")
	private PaymentRecordService paymentRecordService;
	
	@Resource(name="paymentService")
	private PaymentService paymentService;
	
	@Resource(name = "managerChannelRelService")
	private ManagerChannelRelService managerChannelRelService;

	@Value("${defaultAccountCompanyId}")
	private Integer defaultAccountCompanyId;


	/**
	 * 通过关键字搜索平台客户经理
	 * 
	 * @param searchWord 关键字
	 *            
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public List<ManagerDto> getPlatformManager(String searchWord) {
		List<ManagerDto> result = managerService.getPlatformManager(searchWord);
		if(AssertUtil.isNotEmpty(result)){
			for(ManagerDto manager : result){
				Hospital hospital = managerChannelRelService.getChannelByPlatformManagerId(manager.getAccount().getId());
				if(hospital != null){
					manager.setHospitalName(hospital.getName());
				}
			}
		}
		return result;
	}
	
	/**
	 * 获取平台客户经理
	 * 
	 * @param managerId
	 */
	@RequestMapping(value = "/gain", method = RequestMethod.GET)
	@ResponseBody
	public ManagerVo getPlatformManager(Integer managerId) {
		ManagerDto result = managerService.selectPlatformManagerById(managerId);
		ManagerVo managerVo = new ManagerVo();
		managerVo.setUsername(result.getUserList().get(0).getUsername());
		managerVo.setName(result.getAccount().getName());
		managerVo.setMobile(result.getAccount().getMobile());
		managerVo.setIdentity(result.getIdentity());
		if(result.getManagerSettings() != null){
			managerVo.setIsSitePay(result.getManagerSettings().getIsSitePay());
		}
		Hospital hospital = managerChannelRelService.getChannelByPlatformManagerId(managerId);
		if(hospital != null){
			managerVo.setChannelId(hospital.getId());
		}
		return managerVo;
	}
	
	
	/**保存或编辑平台客户经理
	 * @param managerVo
	 * @param request
	 * @param session
	 * @throws AccountException
	 * @throws LoginFailedException
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void edit(@RequestBody ManagerVo managerVo, HttpServletRequest request, HttpSession session)
			throws AccountException, LoginFailedException {
		logger.info("平台客户经理管理新建/更新操作，date:{}, name:{}, mobile:{}, username:{}, isSitePay:{}, channelId:{}, idendity:{}",
				LocalDateTime.now(), managerVo.getName(), managerVo.getMobile(), managerVo.getUsername(),
				managerVo.getIsSitePay(), managerVo.getChannelId(), managerVo.getIdentity());
		Account account = new Account();
		account.setId(managerVo.getId());
		account.setName(managerVo.getName());
		account.setMobile(managerVo.getMobile());

		account.setType(AccountTypeEnum.Manager.getCode());
		account.setSystem(AccountSourceEnum.CRM.getCode());

        if (managerVo.getId() == null){
            User userExit = userService.getUserBySystemType(managerVo.getUsername(), AccountSourceEnum.CRM.getCode());
            if (userExit != null) {
                throw new AccountException(USER_NAME_EXIT, "登录账号已存在，请重新输入！");
            }
        }

		User user = new User();
		user.setUsername(managerVo.getUsername());

		user.setSystem(AccountSourceEnum.CRM.getCode());
		user.setUrl(request.getServerName() + request.getContextPath());

		ManagerSettings mSettings = new ManagerSettings();
		mSettings.setMangerId(managerVo.getId());
		mSettings.setAccountCompanyId(defaultAccountCompanyId); 		//平台客户经理默认挂账单位为每天健康
		managerVo.setIsSitePay(false);

		mSettings.setIsSitePay(managerVo.getIsSitePay());
		if(AssertUtil.isNull(managerVo.getId())){
			mSettings.setAgentReserve(true);
		}
		Integer accountId = managerService.saveManager(account, user, managerVo.getIdentity(), RoleEnum.PLATFORM_MANAGER.getCode(), null,
				mSettings);

		// TODO manae下线 ops 不存在这种情况 编辑本人
//		if (managerVo.getId() != null
//				&& managerVo.getId().intValue() == LoginUtil.getCurrentUser(session).getId().intValue()) {
//			String token = (String) session.getAttribute(LoginService.TOKEN_NAME);
//			loginService.updateAccount(token);
//		}
		Hospital hospital = managerChannelRelService.getChannelByPlatformManagerId(accountId);
		if(hospital == null && managerVo.getChannelId() != null){
			managerChannelRelService.addManagerChannelRel(accountId, managerVo.getChannelId());
		}
	}

	/**
	 * 逻辑删除平台客户经理
	 * @param managerId
	 */
	@RequestMapping(value = "/remove/{managerId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void remove(@PathVariable Integer managerId) {
		accountService.removeAccount(managerId);
		//删除平台客户经理支持的渠道商
		managerChannelRelService.removeManagerChannelRelByPlatformManagerId(managerId);
	}
	
	/**
	 * 重置平台客户经理密码
	 * 
	 * @param accountId
	 */
	@RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void resetPwd(@RequestParam(value = "accountId") Integer accountId) {
		//随机生成6位数字
		String pwd = RandomNumberUtil.generate(6);
		userService.changePassword(accountId, pwd);

		try {
			Map<String, String> contextMap = new HashMap<>();
			contextMap.put("password", pwd);
			Account account = accountService.getAccountById(accountId);

			SmsReq smsReq = new SmsReq();
			smsReq.setMobile(account.getMobile());
			smsReq.setTemplateOrCode(TemplateCodeEnum.MANAGE_RESET_PWD.getValue());
			Map<String, Object> map = Collections.unmodifiableMap(contextMap);
			smsReq.setParamMap(map);
			smsService.send(smsReq);
		} catch (Exception e) {
			logger.error("修改密码发送短信错误失败：", e);
		}

	}
	
	/**
	 * 获取平台客户经理账务记录
	 * @param managerId
	 * @return
	 * @throws LoginFailedException
	 */
	@RequestMapping(value = "/accounting", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public Map<String, Object> managerAccounting(Integer managerId, Page page) throws LoginFailedException {
		Map<String, Object> map = new HashMap<String, Object>();
		ManagerDto manager = managerService.selectPlatformManagerById(managerId);
		List<Card> cards = cardService.getCardByaccountId(managerId, true);
		
		PaymentRecord pr = new PaymentRecord();
		pr.setAccountId(managerId);
		pr.setTradeType(TradeTypeEnum.Recharge.getCode());
		PageView<PaymentRecord> pdList = paymentRecordService.getPaymentRecordByPage(pr, page);
		
		//根据expenseAccount转换出充值人名字
		List<PaymentRecord> prList = (List<PaymentRecord>) pdList.getRecords();
		if (AssertUtil.isNotEmpty(prList)) {
			Map<String, Account> accountMap = new HashMap<String, Account>();
			prList.forEach(paymentRecord -> {
				String acId = paymentRecord.getExpenseAccount();
				if (accountMap.get(acId) == null) {
					Account ac = accountService.getAccountById(Integer.parseInt(acId));
					paymentRecord.setExpenseAccount(ac.getName());
					accountMap.put(ac.getId().toString(), ac);
				} else {
					paymentRecord.setExpenseAccount(accountMap.get(acId).getName());
				}
			});
		}
		pdList.setRecords(prList);
		
		map.put("manager", manager);
		map.put("parentCard", AssertUtil.isNotEmpty(cards) ? cards.get(0) : null);
		map.put("rechargeRecord", pdList);
		
		return map;
	}
	
	/**
	 * 充值
	 * @param money
	 * @param remark 备注
	 * @param managerId 平台客户经理Id
	 * @param session
	 * @throws RechargeException
	 * @throws LoginFailedException
	 */
	@RequestMapping(value = "/recharge", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	@Token("manager")
	public void managerAccounting(@RequestParam("money") Integer money,
                                  @RequestParam(value="remark", required=false) String remark,
                                  @RequestParam("managerId") Integer managerId, HttpSession session) throws RechargeException, LoginFailedException {
		// Account currentUser = LoginUtil.getCurrentUser(session);
		
		// TODO manage 下线 母卡重置 操作人id不记录， 操作人放在log中
		Employee employee = SessionUtil.getEmployee();
		if (remark==null) {
			remark = "操作人:"+employee.getEmployeeName();
		} else {
			remark += " 操作人: "+employee.getEmployeeName();
		}
		paymentService.rechargeManager(managerId, money, -1, remark);
		
	}
	
}
