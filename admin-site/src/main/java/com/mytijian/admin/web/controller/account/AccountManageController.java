package com.mytijian.admin.web.controller.account;

import com.mytijian.account.dto.AccountManageDto;
import com.mytijian.account.dto.AccountRelationUpdateDto;
import com.mytijian.account.model.Account;
import com.mytijian.account.service.AccountManageService;
import com.mytijian.account.service.AccountRelationService;
import com.mytijian.account.service.AccountService;
import com.mytijian.account.service.UserService;
import com.mytijian.uic.annotation.LoginRequired;
import com.mytijian.gotone.api.SmsService;
import com.mytijian.gotone.api.model.SmsReq;
import com.mytijian.gotone.api.model.constants.SmsConstants;
import com.mytijian.gotone.api.model.enums.TemplateCodeEnum;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AccountManageController {

	private final Logger log = LoggerFactory.getLogger(AccountManageController.class);
	
	@Resource(name = "userService")
	private UserService userService;
	
	@Resource(name = "accountManageService")
	private AccountManageService accountManageService;

	@Resource(name = "smsService")
	private SmsService smsService;

	@Resource(name = "accountRelationService")
	private AccountRelationService accountRelationService;

	@Resource(name = "accountService")
	private AccountService accountService;

	@RequestMapping(value = "/accountManagePageInfo", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public List<AccountManageDto> getAccountManagePageInfo(@RequestParam(value = "name", required = false) String name,
                                                           @RequestParam(value = "mobile", required = false) String mobile,
                                                           @RequestParam(value = "idCard", required = false) String idCard, Page page, HttpSession session) {
		if (AssertUtil.isEmpty(name) && AssertUtil.isEmpty(mobile) && AssertUtil.isEmpty(idCard)) {
			return null;
		}
		List<AccountManageDto> accountList = accountManageService.getAccountManage(name, mobile, idCard);
		return accountList;
	}

	@RequestMapping(value = "/account/resetPwd", method = RequestMethod.POST)
	@LoginRequired
	@ResponseStatus(value = HttpStatus.OK)
	public void managerResetPwd(@RequestParam(value = "accountId")Integer accountId,
			@RequestParam(value = "simplePasswd", defaultValue="false") boolean simplePasswd){

		if (simplePasswd) {
			userService.changePassword(accountId, "111111");
			log.info("accountId {} passwd has changed", accountId);
		} else {
			String newPwd = userService.resetRandomPwd(accountId);
			Account account = accountService.getAccountById(accountId);
			// 发送短信
			Map<String, Object> contextMap = new HashMap<>();
			contextMap.put("pwd", newPwd);
			contextMap.put("phone", SmsConstants.PHONE);

			SmsReq smsReq = new SmsReq();
			smsReq.setTemplateOrCode(TemplateCodeEnum.RESET_RANDOM_PWD.getValue());
			smsReq.setMobile(account.getMobile());
			smsReq.setParamMap(contextMap);
			smsService.send(smsReq);
		}
	}
	
	@RequestMapping(value = "/updateAccount", method= RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void resetPwd(@RequestBody AccountRelationUpdateDto accountRelationUpdateDto){
		if (accountRelationUpdateDto.getAge() != null) {
			accountRelationUpdateDto.setBirthYear( Calendar.getInstance().get(
					Calendar.YEAR)
					- accountRelationUpdateDto.getAge());
		}
		accountRelationService.updateAccountRelationById(accountRelationUpdateDto);
	}

}
