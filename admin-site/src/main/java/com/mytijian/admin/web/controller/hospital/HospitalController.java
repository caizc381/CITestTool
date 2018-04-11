package com.mytijian.admin.web.controller.hospital;

import com.mytijian.admin.web.dto.HospitalQueryDto;
import com.mytijian.admin.web.facade.hospital.HospitalFacade;
import com.mytijian.admin.web.vo.resource.AreaVo;
import com.mytijian.admin.web.vo.resource.HospitalCompanyVO;
import com.mytijian.admin.web.vo.resource.OrganizationVO;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.service.hospital.param.HospitalQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@SuppressWarnings("deprecation")
@RestController
@RequestMapping("/hospital")
public class HospitalController {

	@Resource(name = "hospitalFacade")
	private HospitalFacade hospitalFacade;

	/**
	 * 获取体检中心列表
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/listMecHospitalIdAndName")
	public List<OrganizationVO> listMecHospitalIdAndName(String name) {
		HospitalQuery hospitalQuery = new HospitalQuery();
		hospitalQuery.setName(name);
		hospitalQuery.setKeywords(name);
		hospitalQuery.setOrgType(1);	
		return hospitalFacade.listHospitalIdAndNameByHospitalQuery(hospitalQuery);
	}

	/**
	 * 获取机构列表
	 *
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/listHospital")
	public List<OrganizationVO> listHospital(String name) {
		HospitalQueryDto hospitalQueryDto = new HospitalQueryDto();
		hospitalQueryDto.setName(name);
		hospitalQueryDto.setKeywords(name);
		hospitalQueryDto.setOrgType(OrganizationTypeEnum.HOSPITAL.getCode());
		return hospitalFacade.listHospital(hospitalQueryDto);
	}

	/**
	 * 获取机构列表
	 *
	 * @param hospitalQueryDto
	 * @return
	 */
	@RequestMapping(value = "/queryOrg", method = RequestMethod.POST)
	@ResponseBody
	public List<OrganizationVO> queryOrganization(@RequestBody(required = true) HospitalQueryDto hospitalQueryDto) {
		hospitalQueryDto.setOrgType(OrganizationTypeEnum.HOSPITAL.getCode());
		List<OrganizationVO> organizationVOList = hospitalFacade.listHospital(hospitalQueryDto);
		return organizationVOList;
	}

	/**
	 * 获取体检中心单位列表
	 *
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value = "/listCompany")
	public List<HospitalCompanyVO> listCompany(Integer hospitalId) {
		return hospitalFacade.listCompany(hospitalId);
	}
	
	

	/**
	 * 获取地区列表
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryArea", method = RequestMethod.GET)
	@ResponseBody
	public AreaVo queryArea() {
		return hospitalFacade.getAreaVo();
	}
}
