package com.mytijian.admin.web.controller.examreport;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.mytijian.admin.web.vo.examreport.ParseTextVo;
import com.mytijian.mediator.examreport.ExamReportExceptionCode;
import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.report.enums.DroolsRuleGroupEnum;
import com.mytijian.report.model.ExamExceptional;
import com.mytijian.report.model.parse.DroolsRule;
import com.mytijian.report.model.parse.ExamReportParseJobDetail;
import com.mytijian.report.model.parse.FormatText;
import com.mytijian.report.service.ExamReportQueryService;
import com.mytijian.report.service.parse.DroolsRuleService;
import com.mytijian.report.service.parse.ExamReportParseJobDetailService;
import com.mytijian.report.service.parse.ParseService;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;

@RestController
@RequestMapping("/examreport")
public class ExamReportController {

	private final Logger logger = LoggerFactory.getLogger(ExamReportController.class);
	
	@Resource(name = "examReportParseJobDetailService")
	private ExamReportParseJobDetailService examReportParseJobDetailService;
	
	@Resource(name = "droolsRuleService")
	private DroolsRuleService droolsRuleService;
	
	@Resource(name = "exceptionalParseService")
	private ParseService<ExamExceptional> ParseService;
	
	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	@Resource(name = "shardingExamReportQueryService")
	private ExamReportQueryService examReportQueryService;
	
	/**
	 * 查询体检报告解析任务
	 * @param keyWords
	 * @return
	 */
	@RequestMapping(value = "/jobDetail", method = RequestMethod.GET )
	public List<ExamReportParseJobDetail> examReportParseJobDetailList(String keyWords){
		logger.info("查询体检报告解析任务，keywords:{}, time:{}", keyWords, LocalDateTime.now());
		return examReportParseJobDetailService.getHospitalExamReportParseJobList(keyWords);
	}
	
	/**
	 * 继续/重新开始/关闭 解析任务
	 * @param hospitalId
	 * @param isReStar
	 */
	@RequestMapping(value = "/changeJob", method = RequestMethod.POST )
	@ResponseStatus(value = HttpStatus.OK)
	public void startParseJob(Integer hospitalId, int type, int status){
		DroolsRule rule = droolsRuleService.getDroolsRuleByHospitalIdAndType(hospitalId, 
				DroolsRuleGroupEnum.PARSE_EXAMREPORT.getType(), type);
		if(rule == null){
			throw ExceptionFactory.makeFault(ExamReportExceptionCode.RULE_NOT_EXISTS, new Object[] { null });
		}
		logger.info("开始体检报告解析, hospitalId:{}, type, status:{}, time:{}", hospitalId, type, status, LocalDateTime.now());
		examReportParseJobDetailService.updateExamReportParseJoebStatusById(hospitalId, type, status);
	}
	
	
	/**
	 * 保存解析规则
	 * @param hospitalId
	 * @param type
	 * @param rule
	 */
	@RequestMapping(value = "/saveRule", method = RequestMethod.POST )
	@ResponseStatus(value = HttpStatus.OK)
	public void saveExamReportParseRule(@RequestBody DroolsRule rule){
		rule.setCategory(DroolsRuleGroupEnum.PARSE_EXAMREPORT.getType());
		rule.setName("异常建议解析规则");
		if(rule.getId() == null){
			logger.info("解析规则添加, hospitalId:{}, time:{}", rule.getHospitalId(), LocalDateTime.now());
			droolsRuleService.addParseRule(rule);
		} else {
			logger.info("解析规则更新, hospitalId:{}, time:{}", rule.getHospitalId(), LocalDateTime.now());
			droolsRuleService.updateParseRule(rule);
		}
	}
	
	/**
	 * 解析输出文本
	 * @param rule
	 * @param parsedText
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/parse", method = RequestMethod.POST )
	public String parse(@RequestBody ParseTextVo parseText) throws UnsupportedEncodingException{
		List<FormatText> dataList = ParseService.parseText(parseText.getParsedText(), parseText.getRule());
		return JSON.toJSONString(dataList);
	}
	
	/**
	 * 随机获取一份体检报告
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value = "/randomExamReport", method = RequestMethod.GET )
	public String getRandomExamReport(Integer hospitalId, int type){
		return JSON.toJSONString(examReportQueryService.getRandomExamReportParseText(hospitalId, type));
	}
	
	/**
	 * 刷新整个页面
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value = "/rulePage", method = RequestMethod.GET )
	public Map<String, Object> pageInfo(int type){
		List<Hospital> hospitalList = hospitalService.getOrganizationList(OrganizationTypeEnum.HOSPITAL.getCode());
		Map<String, Object> pageInfo = infoByHospital(hospitalList.get(0).getId(), type);
		pageInfo.put("hospitalList", hospitalList);
		return pageInfo;
	}
	
	/**
	 * 根据体检中心获取页面信息
	 * @return
	 */
	@RequestMapping(value = "/infoByHospital", method = RequestMethod.GET )
	public Map<String, Object> infoByHospital(Integer hospitalId, int type){
		Map<String, Object> pageInfo = Maps.newHashMap();
		DroolsRule rule = droolsRuleService.getDroolsRuleByHospitalIdAndType(hospitalId, 
				DroolsRuleGroupEnum.PARSE_EXAMREPORT.getType(), type);
		if(rule != null){
			pageInfo.put("rule", rule);
		}
		String report = getRandomExamReport(hospitalId, type);
		if(report != null){
			pageInfo.put("report", report);
		}
		return pageInfo;
	}
	
}
