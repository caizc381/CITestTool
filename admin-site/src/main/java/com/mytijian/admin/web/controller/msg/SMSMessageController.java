package com.mytijian.admin.web.controller.msg;

import com.mytijian.gotone.api.QueryService;
import com.mytijian.gotone.api.SmsService;
import com.mytijian.gotone.api.model.QuerySmsReq;
import com.mytijian.gotone.api.model.QuerySmsResp;
import com.mytijian.gotone.api.model.SmsWithContentReq;
import com.mytijian.gotone.api.model.beans.SmsRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class SMSMessageController {

	@Resource(name = "queryService")
	private QueryService queryService;
	
	@Resource(name = "smsService")
	private SmsService smsService;
	
	/**
	 * 获取某个号码最近10天的发送记录，如果传入手机号码为空，则返回一个空列表
	 * @param mobile
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listSMSSendRecords")
	public List<SmsRecord> listSMSSendRecords(String mobile) {
		QuerySmsReq querySmsReq = new QuerySmsReq();
		querySmsReq.setMobile(mobile);
		querySmsReq.setPageNo(1);
		querySmsReq.setPageSize(1000);
		QuerySmsResp resp = queryService.querySms(querySmsReq);
		return resp.getSmsRecordList();
	}
	
	/**
	 * 重新发送短信发送记录中的某条短信
	 * 号码、内容都和源发送记录一致
	 * @param recordId
	 */
	@RequestMapping("/reSendSmsMessage")
	@ResponseStatus(HttpStatus.OK)
	public void reSendSmsMessage(Integer recordId) {
		QuerySmsReq querySmsReq = new QuerySmsReq();
		querySmsReq.setRecordId(recordId);
		querySmsReq.setPageNo(1);
		querySmsReq.setPageSize(1000);
		QuerySmsResp resp = queryService.querySms(querySmsReq);
		List<SmsRecord> records = resp.getSmsRecordList();
		if (!CollectionUtils.isEmpty(records)) {
			SmsWithContentReq smsWithContentReq = new SmsWithContentReq();
			smsWithContentReq.setMobile(records.get(0).getMobile());
			smsWithContentReq.setContent(records.get(0).getContent());
			smsService.sendWithContent(smsWithContentReq);
		}
	}
}
