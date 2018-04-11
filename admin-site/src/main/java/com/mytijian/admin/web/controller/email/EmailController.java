package com.mytijian.admin.web.controller.email;

import com.alibaba.dubbo.container.page.Page;
import com.mytijian.exception.BizException;
import com.mytijian.gotone.api.EmailRecordQueryService;
import com.mytijian.gotone.api.EmailService;
import com.mytijian.gotone.api.model.EmailRecordReq;
import com.mytijian.gotone.api.model.EmailRecordResp;
import com.mytijian.gotone.api.model.EmailResendReq;
import com.mytijian.gotone.api.model.beans.EmailSendRecord;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
public class EmailController {

	@Resource(name = "emailService")
	private EmailService emailService;

	@Autowired
	private EmailRecordQueryService emailRecordQueryService;
	
	/**
	 * 获取全部的邮件发送记录
	 * @param hospitalName
	 * @param notifyType
	 * @param sendTime
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/manage/listEmailSendRecords", method = RequestMethod.GET)
	public EmailRecordResp listEmailSendRecords(
			@RequestParam(value = "currentPage", required = false) Integer currentPage,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "hospitalName", required = false) String hospitalName,
//			@RequestParam(value = "receiver", required = false)String receiver,
			@RequestParam(value = "notifyType", required = false)String notifyType,
			@RequestParam(value = "sendTime", required = false)String sendTime) {
		EmailRecordReq emailRecordReq = new EmailRecordReq();
		emailRecordReq.setHospitalName(hospitalName);
//		emailRecordReq.setReceiver(receiver);
		emailRecordReq.setNotifyType(notifyType);
		emailRecordReq.setDelete(true);
		emailRecordReq.setIsIdDesc(Integer.valueOf(1));
		if (StringUtils.isNotBlank(sendTime)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String d = sdf.format(Long.valueOf(sendTime));
			try {
				emailRecordReq.setSendTime(sdf.parse(d));
			} catch (ParseException e) {
				throw new BizException("时间格式不正确","时间格式不正确");
			}
		}
		if (null != currentPage && currentPage >0){
			emailRecordReq.setPageNo(currentPage);
		}
		if (null != pageSize && pageSize >0){
			emailRecordReq.setPageSize(pageSize);
		}
		EmailRecordResp resp = emailRecordQueryService.queryEmails(emailRecordReq);
		return resp;
	}

	/**
	 * 重新发送邮件发送记录中的某条邮件
	 * @param recordId
	 */
	@RequestMapping(value = "/manage/email/resend", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public void reSendSmsMessage(Integer recordId) {
		EmailRecordReq emailRecordReq = new EmailRecordReq();
		emailRecordReq.setRecordId(recordId);
		emailRecordReq.setDelete(true);
		emailRecordReq.setPageNo(1);
		emailRecordReq.setPageSize(100);
		EmailRecordResp resp = emailRecordQueryService.queryEmails(emailRecordReq);
		List<EmailSendRecord> records = resp.getEmailSendRecordList();
		if (!CollectionUtils.isEmpty(records)) {
			EmailSendRecord record = records.get(0);
			EmailResendReq emailResendReq = new EmailResendReq();
			emailResendReq.setRecordId(record.getId());
			emailService.resend(emailResendReq);
		}
	}
	
}
