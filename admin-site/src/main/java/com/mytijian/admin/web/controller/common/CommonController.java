package com.mytijian.admin.web.controller.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.mytijian.admin.web.vo.resource.AreaVo;
import com.mytijian.admin.web.vo.resource.CityVo;
import com.mytijian.admin.web.vo.resource.DistrictVo;
import com.mytijian.admin.web.vo.resource.OrganizationVO;
import com.mytijian.admin.web.vo.resource.ProvinceVo;
import com.mytijian.common.dto.Address;
import com.mytijian.common.model.City;
import com.mytijian.common.model.District;
import com.mytijian.common.model.Province;
import com.mytijian.common.service.AreaService;
import com.mytijian.organization.model.Organization;
import com.mytijian.organization.service.OrganizationService;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.util.PinYinUtil;

@RestController
@RequestMapping("/common")
public class CommonController {
	
	private final static Logger logger = LoggerFactory.getLogger(CommonController.class);
	
	@Resource(name = "crmAreaService")
	private AreaService areaService;
	
	@Resource(name = "organizationService")
	private OrganizationService organizationService;
	
	@RequestMapping(value="/getAllAddress")
	@ResponseBody
	public AreaVo queryArea(){
		Address address = areaService.getAddress();
		AreaVo areaVo = new AreaVo();
		if(address == null){
			logger.info("地址查询结果为空。");
			return areaVo;
		}
		List<Province> provinces = address.getProvinces();
		if(CollectionUtils.isEmpty(provinces)){
			logger.info("地址查询所有省结果为空。");
			return areaVo;
		}
		List<ProvinceVo> provinceVoList = new ArrayList<ProvinceVo>();
		for(Province province : provinces ){
			ProvinceVo provinceVo = new ProvinceVo();
			provinceVo.setValue(province.getProvinceId());
			provinceVo.setLabel(province.getProvinceName());
			List<City> cityList = province.getCities();
			
			List<CityVo> cityVoList = new ArrayList<CityVo>();
			if(CollectionUtils.isNotEmpty(cityList)){
				for(City city : cityList){
					CityVo cityVo = new CityVo();
					cityVo.setValue(city.getCityId());
					cityVo.setLabel(city.getCityName());
					List<District> districtList = city.getDistricts();
					List<DistrictVo> districtVoList = new ArrayList<DistrictVo>();
					if(CollectionUtils.isNotEmpty(districtList)){
						for(District district : districtList){
							DistrictVo districtVo = new DistrictVo();
							districtVo.setValue(district.getDistrictId());
							districtVo.setLabel(district.getDistrictName());
							districtVoList.add(districtVo);
						}
					}
					cityVo.setChildren(districtVoList);
					cityVoList.add(cityVo);
				}
			}
			provinceVo.setChildren(cityVoList);
			provinceVoList.add(provinceVo);
		}
		areaVo.setProvinces(provinceVoList);
		return areaVo;
	}
	
	@RequestMapping(value="/queryOrganization" , method = RequestMethod.POST)
	@ResponseBody
	public List<OrganizationVO> queryOrganization(@RequestParam(value = "provinceId", required = false)Integer provinceId,@RequestParam(value = "cityId", required = false)Integer cityId,@RequestParam(value = "districtId", required = false)Integer districtId){
		
		List<Organization> organizationList = organizationService.listHospitalByAddressId(OrganizationTypeEnum.HOSPITAL.getCode(),provinceId, cityId, districtId);
		List<OrganizationVO>  organizationVOList = Lists.newArrayList();
		if(CollectionUtils.isEmpty(organizationList)){
			logger.info("体检中心列表查询为null。");
			return organizationVOList;
		}
		
		for(Organization organization : organizationList){
			OrganizationVO organizationVO = new OrganizationVO();
			BeanUtils.copyProperties(organization, organizationVO);
			organizationVO.setPinyin(PinYinUtil.getFirstSpell(organizationVO.getName()));
			organizationVOList.add(organizationVO);
		}
		return organizationVOList;
		
	}
}
