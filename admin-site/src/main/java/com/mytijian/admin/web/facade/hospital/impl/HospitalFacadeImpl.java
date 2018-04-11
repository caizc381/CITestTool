package com.mytijian.admin.web.facade.hospital.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mytijian.admin.web.dto.HospitalQueryDto;
import com.mytijian.admin.web.facade.hospital.HospitalFacade;
import com.mytijian.admin.web.vo.resource.AreaVo;
import com.mytijian.admin.web.vo.resource.CityVo;
import com.mytijian.admin.web.vo.resource.DistrictVo;
import com.mytijian.admin.web.vo.resource.HospitalCompanyVO;
import com.mytijian.admin.web.vo.resource.OrganizationVO;
import com.mytijian.admin.web.vo.resource.ProvinceVo;
import com.mytijian.company.hospital.service.HospitalCompanyService;
import com.mytijian.company.hospital.service.model.HospitalCompany;
import com.mytijian.resource.model.Address;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.AddressService;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.resource.service.hospital.param.HospitalQuery;
import com.mytijian.util.AssertUtil;
import com.mytijian.util.PinYinUtil;

@Service("hospitalFacade")
public class HospitalFacadeImpl implements HospitalFacade {

    @Resource(name = "hospitalService")
    private HospitalService hospitalService;


    @Resource(name = "addressService")
    private AddressService addressService;

    @Resource
    private HospitalCompanyService hospitalCompanyService;

    @Override
    public List<OrganizationVO> listHospitalIdAndNameByHospitalQuery(HospitalQuery hospitalQuery) {
        List<Hospital> hospitals = hospitalService.listByHospitalQuery(hospitalQuery);
        return HospitalToHospitalIdAndNameVO(hospitals);
    }

    @Override
    public AreaVo getAreaVo() {
        AreaVo areaVo = new AreaVo();
        List<ProvinceVo> provinceVoList = new ArrayList<>();
        List<Address> provinceList = addressService.getProvince();
        for (Address address : provinceList) {
            ProvinceVo provinceVo = new ProvinceVo();
            provinceVo.setValue(address.getId());
            provinceVo.setLabel(address.getProvince());
            List<CityVo> cityVoList = listCityVo(address.getId());
            provinceVo.setChildren(cityVoList);
            provinceVoList.add(provinceVo);
        }
        areaVo.setProvinces(provinceVoList);
        return areaVo;

    }

    private List<CityVo> listCityVo(Integer provinceId) {
        List<CityVo> cityVoList = new ArrayList<>();
        List<Address> cityList = addressService.getCitys(provinceId);
        for (Address address : cityList) {
            CityVo cityVo = new CityVo();
            cityVo.setValue(address.getId());
            cityVo.setLabel(address.getCity());
            List<DistrictVo> districtVoList = listDistrictVo(address.getId());
            cityVo.setChildren(districtVoList);
            cityVoList.add(cityVo);
        }
        return cityVoList;
    }

    private List<DistrictVo> listDistrictVo(Integer cityId) {
        List<DistrictVo> districtVoList = new ArrayList<>();
        List<Address> districtList = addressService.getDistricts(cityId);
        for (Address address : districtList) {
            DistrictVo districtVo = new DistrictVo();
            districtVo.setValue(address.getId());
            districtVo.setLabel(address.getDistrict());
            districtVoList.add(districtVo);
        }
        return districtVoList;
    }

    @Override
    public List<OrganizationVO> listHospital(HospitalQueryDto hospitalQueryDto) {

        List<Integer> ids = new ArrayList<>();

        if (hospitalQueryDto.getDistrictId() != null) {
            ids.add(hospitalQueryDto.getDistrictId());
        }

        if (hospitalQueryDto.getDistrictId() == null && hospitalQueryDto.getCityId() != null) {
            List<Address> districtList = addressService.getDistricts(hospitalQueryDto.getCityId());
            for (Address district : districtList) {
                ids.add(district.getId());
            }
        }

        if (hospitalQueryDto.getDistrictId() == null && hospitalQueryDto.getCityId() == null
                && hospitalQueryDto.getProvinceId() != null) {
            List<Address> cityList = addressService.getCitys(hospitalQueryDto.getProvinceId());
            for (Address city : cityList) {
                List<Address> districtList = addressService.getDistricts(city.getId());
                for (Address district : districtList) {
                    ids.add(district.getId());
                }
            }
        }

        HospitalQuery hospitalQuery = new HospitalQuery();

        if (AssertUtil.isNotEmpty(ids)) {
            hospitalQuery.setAddressId(ids);
        }
        hospitalQuery.setOrgType(hospitalQueryDto.getOrgType());
		hospitalQuery.setEnable(1);// 查询可用体检中心
        List<Hospital> hospitals = hospitalService.listByHospitalQuery(hospitalQuery);
        return convertToHospitalVOList(hospitals);
    }

    private List<OrganizationVO> convertToHospitalVOList(List<Hospital> hospitals) {
        List<OrganizationVO> hospitalVOList = new ArrayList<>();
        hospitals.stream().forEach(hospital -> {
            OrganizationVO hospitalVO = new OrganizationVO();
            hospitalVO.setId(hospital.getId());
            hospitalVO.setName(hospital.getName());
            hospitalVO.setPinyin(PinYinUtil.getFirstSpell(hospital.getName()));
            hospitalVO.setOrgType(hospital.getOrganizationType());
            hospitalVOList.add(hospitalVO);
        });
        return hospitalVOList;

    }

    @Override
    public List<HospitalCompanyVO> listCompany(Integer hospitalId) {
        List<HospitalCompany> hospitalCompanyList = hospitalCompanyService.listCompanyByHospital(hospitalId);
        List<HospitalCompanyVO> hospitalCompanyVOList = new ArrayList<>();
        hospitalCompanyList.stream().forEach(hospitalCompany -> {
            HospitalCompanyVO hospitalCompanyVO = new HospitalCompanyVO();
            hospitalCompanyVO.setId(hospitalCompany.getId());
            hospitalCompanyVO.setName(hospitalCompany.getName());
            hospitalCompanyVO.setPinyin(hospitalCompany.getPinyin());
            if (hospitalCompany.getPlatformCompanyId() != null && hospitalCompany.getPlatformCompanyId() > 2) {
                hospitalCompanyVO.setIsPlatformCompay(true);
            } else {
                hospitalCompanyVO.setIsPlatformCompay(false);
            }
            hospitalCompanyVOList.add(hospitalCompanyVO);
        });
        return hospitalCompanyVOList;
    }

    private List<OrganizationVO> HospitalToHospitalIdAndNameVO(List<Hospital> hospitals) {
        List<OrganizationVO> organizationVOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(hospitals) && hospitals.size() > 0) {
            hospitals.forEach(hospital -> {
                OrganizationVO organizationVO = new OrganizationVO();
                BeanUtils.copyProperties(hospital, organizationVO);
                organizationVOS.add(organizationVO);
            });
        }
        return organizationVOS;
    }

}
