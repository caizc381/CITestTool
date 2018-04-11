package com.mytijian.admin.web.controller.report;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.plugins.Page;
import com.mytijian.mediator.report.api.common.QueryMealDataValue;
import com.mytijian.mediator.report.service.CompanyMealReportService;
import com.mytijian.mediator.report.vo.CompanyMealVo;
import com.mytijian.mediator.report.vo.CompanyVo;

@RestController
@RequestMapping("/report/meal")
public class CompanyMealReportCountController {
	
	private static final Integer DEFAULT_SIZE = 100;
	private static final Integer DEFAULT_CURRENT_PAGE = 1;
	
	@Resource(name = "companyMealReportService")
	private CompanyMealReportService companyMealReportService;
	
	@RequestMapping(value="/queryCompany")
	@ResponseBody
	public List<CompanyVo> queryCompanyUnderOrganization(@RequestParam(value = "hospitalId", required = true)Long hospitalId){
		
		return companyMealReportService.queryCompanyUnderOrganization(hospitalId);
	}
	
	@RequestMapping(value="/queryTotalMealInfo")
	@ResponseBody
	public CompanyMealVo queryTotalMealInfo(@RequestBody(required = false)QueryMealDataValue queryMealDataValue){
		
		return companyMealReportService.queryTotalMealInfo(queryMealDataValue);
		
	}
	
	@RequestMapping(value="/queryCompanyMeal")
	@ResponseBody
	public Page<CompanyMealVo> queryCompanyMeal(@RequestBody(required = false)QueryMealDataValue queryMealDataValue){
		
		Page<CompanyMealVo> page = new Page<CompanyMealVo>();
		
		if(queryMealDataValue == null){
			page.setSize(DEFAULT_SIZE);
			page.setCurrent(DEFAULT_CURRENT_PAGE);
		}else{
			if(queryMealDataValue.getPageSize() != null && queryMealDataValue.getCurrent() != null){
				page.setSize(queryMealDataValue.getPageSize());
				page.setCurrent(queryMealDataValue.getCurrent());
			}else{
				page.setSize(DEFAULT_SIZE);
				page.setCurrent(DEFAULT_CURRENT_PAGE);
			}
		}
		Page<CompanyMealVo> result = companyMealReportService.queryCompanyMeal(queryMealDataValue, page);
		return result;
		
	}
}
