package com.mytijian.admin.web.controller.withdraw;

import com.google.common.base.MoreObjects;
import com.mytijian.account.enums.RoleEnum;
import com.mytijian.account.exceptions.LoginFailedException;
import com.mytijian.account.model.Account;
import com.mytijian.account.service.AccountService;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.uic.util.LoginUtil;
import com.mytijian.payment.enums.WithdrawAuditStates;
import com.mytijian.payment.exceptions.PaymentException;
import com.mytijian.payment.exceptions.WithdrawAuditException;
import com.mytijian.payment.model.*;
import com.mytijian.payment.service.AccountingService;
import com.mytijian.payment.service.PaymentRecordService;
import com.mytijian.payment.service.WithdrawAuditService;
import com.mytijian.payment.service.WxBillService;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.util.AssertUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 提现审核
 * 
 * @author linzhihao
 */
@Controller
@RequestMapping("/withdrawaudit")
public class WithdrawAuditController {

	// 点击客服审核
	private final Integer CUSTOMER_AUDIT = 1;

	// 点击财务审核
	private final Integer FINANCE_AUDIT = 2;

	@Value("${customer_role_id}")
	private Integer customer_role_id;
	
	@Value("${finance_role_id}")
	private Integer finance_role_id;

	@Resource(name = "withdrawAuditService")
	private WithdrawAuditService withdrawAuditService;

	@Resource(name = "accountingService")
	private AccountingService accountingService;

	@Resource(name = "accountService")
	private AccountService accountService;

	@Resource(name = "paymentRecordService")
	private PaymentRecordService paymentRecordService;

	/**
	 * 一进入页面
	 * 
	 * @throws LoginFailedException
	 */
	@ResponseBody
	@RequestMapping(value = "/firstTrialList", method = RequestMethod.POST)
	public PageView<SaveWithdraw> firstTrialList(@RequestBody SearchDto sDto, Page page, HttpSession session)
			throws LoginFailedException {
		if (CUSTOMER_AUDIT.equals(sDto.getAuditType())) {
			// 获取客服列表
			return withdrawAuditService.getWithdrawListFromStateByPage(WithdrawAuditStates.Wait.getCode(), page);
		} else if (FINANCE_AUDIT.equals(sDto.getAuditType())) {
			// 获取财务列表
			return withdrawAuditService.getWithdrawListFromStateByPage(WithdrawAuditStates.FirstTrialAdopt.getCode(),
					page);
		} else {
			return withdrawAuditService.getWithdrawListByPage(sDto, page);
		}

	}

	@Resource(name = "wxBillService")
	private WxBillService wxBillService;

	/**
	 * 用户信息
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo", method = RequestMethod.POST)
	public Map<String, Object> userInfo(@RequestParam("id") Integer id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Account account = accountService.getAccountById(id);
		Accounting accounting = accountingService.getAccountingByAccountId(id);
		List<PaymentRDto> list = paymentRecordService.getPaymentRecordRecently(id);
		List<SaveWithdraw> withdrawList = withdrawAuditService.getByAccountId(id);

		list = list.stream().filter(record -> {
			return record.getStatus() == 1;
		}).collect(Collectors.toList());

		list.forEach(record -> {
			if ("微信支付".equals(record.getPayType())) {
				WxBill find = new WxBill();
				find.setWxOrderId(record.getTradeNo());
				record.setWxbill(wxBillService.find(find));
			}
		});
		map.put("account", account);
		map.put("accounting", accounting);
		map.put("list", list);
		map.put("withdrawList", withdrawList);
		return map;
	}

	/**
	 * 初审意见(客服)
	 * 
	 * @param id
	 * @param succ
	 * @param remark
	 * @throws WithdrawAuditException
	 * @throws LoginFailedException
	 * @throws PaymentException
	 */
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/doFirstTrial", method = RequestMethod.POST)
	public void firstTrial(@RequestParam("id") Integer id, @RequestParam("succ") Boolean succ,
			@RequestParam(value = "remark", required = false) String remark, HttpSession session)
			throws WithdrawAuditException, LoginFailedException, PaymentException {
		AssertUtil.notNull(id);
		AssertUtil.notNull(succ);
		// Account account = LoginUtil.getCurrentUser(session);

		Employee emp = SessionUtil.getEmployee();
		emp.getRoleIds();
		
		// 判断权限，是客服还是财务
		// OPS中可能存在交叉权限， 获取权限以最大权限为原则获取
		// 即 如果某个人同时是[客服]、[财务]，则取[财务]。
		boolean flagF = containsRole(emp.getRoleIds(), finance_role_id);
		boolean flagC = !flagF && containsRole(emp.getRoleIds(), customer_role_id);
		
		// 客服拒绝或者接受，接受传下订单号，然后改下状态就好了
		if (succ) {
			// 如果是客服通过
			withdrawAuditService.updateState(id, WithdrawAuditStates.FirstTrialAdopt, emp.getEmployeeName(), null, null);
		} else {
			if (flagC) {
				// 客服拒绝
				withdrawAuditService.updateState(id, WithdrawAuditStates.FirstTrialReject, emp.getEmployeeName(), remark,
						null);
			} else if (flagF) {
				// 财务拒绝
				withdrawAuditService.updateState(id, WithdrawAuditStates.FinalReviewReject, emp.getEmployeeName(), remark,
						null);
			}
			withdrawAuditService.sendWithdrawMsg(id, remark, false);
		}

	}

	private boolean containsRole(List<Integer> roleIds, Integer findRole) {
		return AssertUtil.isNotEmpty(roleIds) && findRole != null
				&& roleIds.stream().filter(roleId -> roleId.intValue() == findRole.intValue()).findAny().isPresent();
	}

	/**
	 * 财务打款信息的保存
	 * 
	 * @throws WithdrawAuditException
	 * @throws LoginFailedException
	 * @throws PaymentException
	 * 
	 */
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/transfer", method = RequestMethod.POST)
	public void payMoney(@RequestParam("id") Integer id, @RequestParam("serialNumber") String serialNumber,
			HttpSession session) throws WithdrawAuditException, LoginFailedException, PaymentException {
		Employee emp = SessionUtil.getEmployee();
		emp.getRoleIds();
		boolean flagF = containsRole(emp.getRoleIds(), finance_role_id);
		// Account account = LoginUtil.getCurrentUser(session);
		if (flagF) {
			// 如果是财务
			withdrawAuditService.updateState(id, WithdrawAuditStates.FinalReviewAdopt, emp.getEmployeeName(), null,
					serialNumber);
			withdrawAuditService.sendWithdrawMsg(id, null, true);
		} else {
			throw new LoginFailedException(LoginFailedException.INVALID_ROLE);
		}

	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/updateBill")
	public void downloadWxBill(Date begin, Date end) throws Exception {
		long oneDay = 60 * 60 * 24 * 1000L; // 一天秒数
		long currentTimeMillis = System.currentTimeMillis();
		Date min = new Date(currentTimeMillis - (oneDay * 90L)); // 最大下载90天内的订单
		Date yesterday = new Date(currentTimeMillis - oneDay);

		begin = MoreObjects.firstNonNull(begin, min);
		end = MoreObjects.firstNonNull(end, yesterday);

		long billTimeMillis = begin.getTime();

		while (billTimeMillis <= end.getTime()) {
			Date billDate = new Date(billTimeMillis);
			billTimeMillis += oneDay;
			wxBillService.deleteByDate(billDate);
			List<WxBill> bills = wxBillService.downloadBillFromWx(billDate);
			bills.forEach(wxBillService::add);
		}
	}
}
