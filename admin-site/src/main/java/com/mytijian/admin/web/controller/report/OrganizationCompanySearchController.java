package com.mytijian.admin.web.controller.report;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.mediator.report.api.common.QuickQueryOrgAndCompanyValue;
import com.mytijian.mediator.report.service.QueryOrgAndCompanyService;
import com.mytijian.mediator.report.vo.QuickQueryOrgAndCompanyVo;

@RestController
@RequestMapping("/report/search")
public class OrganizationCompanySearchController {
	
	@Resource(name = "queryOrgAndCompanyService")
    private QueryOrgAndCompanyService queryOrgAndCompanyService;
	
	@RequestMapping(value="/queryOrg")
	@ResponseBody
	public List<QuickQueryOrgAndCompanyVo> queryOrganization(@RequestBody(required = true)QuickQueryOrgAndCompanyValue qureyValue){
		
		if(qureyValue.getHospitalPinyin() != null && !"".equals(qureyValue.getHospitalPinyin().trim()) ){
			qureyValue.setHospitalPinyin(qureyValue.getHospitalPinyin().toLowerCase());
		}
		
		
		
		return queryOrgAndCompanyService.queryOrganization(qureyValue);
	}
	
	@RequestMapping(value="/queryCompany")
	@ResponseBody
	public List<QuickQueryOrgAndCompanyVo> queryCompany(@RequestBody(required = false)QuickQueryOrgAndCompanyValue qureyValue){
		
		if(qureyValue == null){
			System.out.println("qureyValue is null");
		}
		
		if(qureyValue.getExamCompanyPinyin() != null && !"".equals(qureyValue.getExamCompanyPinyin())){
			qureyValue.setExamCompanyPinyin(qureyValue.getExamCompanyPinyin().toLowerCase());
		}
		
		return queryOrgAndCompanyService.queryCompany(qureyValue);
	}
	
}
