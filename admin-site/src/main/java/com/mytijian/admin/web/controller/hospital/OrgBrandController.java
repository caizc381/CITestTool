package com.mytijian.admin.web.controller.hospital;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;

import com.mytijian.organization.model.OrgBrand;
import com.mytijian.organization.service.OrgBrandService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类OrgBrandController.java的实现描述：机构品牌controller
 * @author ljx 2018年1月26日 下午5:33:24
 */
@RestController
public class OrgBrandController {
	

	@Resource
	private OrgBrandService orgBrandService;
	
	@GetMapping("/listAllOrgBrand")
	public List<OrgBrand> listAllOrgBrand()
	{
		return orgBrandService.listAllOrgBrand();
	}
}
