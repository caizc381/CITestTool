package com.mytijian.admin.service.address.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.admin.api.address.model.Province;
import com.mytijian.admin.api.address.service.ProvinceService;
import com.mytijian.admin.dao.address.dao.ProvinceMapper;
import com.mytijian.admin.dao.address.dataobject.ProvinceDO;

@Service("provinceService")
public class ProvinceServiceImpl implements ProvinceService {

	@Resource(name = "provinceMapper")
	private ProvinceMapper provinceMapper;

	@Override
	public List<Province> listAllProvinces() {
		List<ProvinceDO> provinceDOList = provinceMapper.selectAll();
		return ProvinceDOToProvince(provinceDOList);
	}

	@Override
	public List<Province> listByAreaId(Integer areaId) {
		if (areaId == null) {
			return new ArrayList<Province>();
		}

		List<ProvinceDO> provinceDOList = provinceMapper.selectByParentId(areaId);
		return ProvinceDOToProvince(provinceDOList);
	}

	@Override
	public Province getByIdOrProvinceId(Integer id, Integer provinceId) {
		if (id == null && provinceId == null) {
			return null;
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", id);
		map.put("provinceId", provinceId);
		ProvinceDO provinceDO = provinceMapper.selectByIdOrProvinceId(map);
		return ProvinceDOToProvince(provinceDO);
	}

	private Province ProvinceDOToProvince(ProvinceDO provinceDO) {
		Province province = null;
		if (provinceDO != null) {
			province = new Province();
			BeanUtils.copyProperties(provinceDO, province);
		}
		return province;
	}

	private List<Province> ProvinceDOToProvince(List<ProvinceDO> provinceDOList) {
		List<Province> provinceList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(provinceDOList)) {
			provinceDOList.forEach(provinceDO -> {
				Province province = new Province();
				BeanUtils.copyProperties(provinceDO, province);
				provinceList.add(province);
			});
		}
		return provinceList;
	}
}
