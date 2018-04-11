package com.mytijian.admin.web.controller.company;

import com.mytijian.uic.annotation.LoginRequired;
import com.mytijian.company.channel.service.ChannelCompanyService;
import com.mytijian.company.channel.service.model.ChannelCompany;
import com.mytijian.company.hospital.service.HospitalCompanyService;
import com.mytijian.company.hospital.service.model.HospitalCompany;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CompanyController {

	@Resource(name = "hospitalCompanyService")
	private HospitalCompanyService hospitalCompanyService;
	
	@Resource(name = "channelCompanyService")
	private ChannelCompanyService channelCompanyService;

	/**
	 * 根据体检中心管理员获得
	 * 
	 * @param hospitalId
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/allCompanies", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public List<HospitalCompany> listCompanyByCreators(
            @RequestParam(value = "hospitalId", required = false) Integer hospitalId,
            @RequestParam(value = "hasGuestCompany", required = false) Boolean hasGuestCompany, HttpSession session) {
		return hospitalCompanyService.listCompanyByHospital(hospitalId);
	}
	
	@RequestMapping(value = "/listChannelCompanyList", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public List<ChannelCompany> listChannelCompanyList(@RequestParam(value = "channelId") Integer hospitalId) {
		return channelCompanyService.listCompanyByChannel(hospitalId);
	}

}
