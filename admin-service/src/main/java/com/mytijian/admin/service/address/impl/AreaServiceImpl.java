package com.mytijian.admin.service.address.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mytijian.admin.api.address.model.Area;
import com.mytijian.admin.api.address.model.Province;
import com.mytijian.admin.api.address.service.AreaService;
import com.mytijian.admin.api.address.service.ProvinceService;
import com.mytijian.admin.dao.address.dao.AreaMapper;
import com.mytijian.admin.dao.address.dataobject.AreaDO;

@Service("areaService")
public class AreaServiceImpl implements AreaService {

	@Resource(name = "areaMapper")
	private AreaMapper areaMapper;
	
	@Resource(name ="provinceService")
	private ProvinceService provinceService;
	
	@Override
	public List<Area> listAllAreas() {
		List<AreaDO> areaDOList = areaMapper.selectAll();
		return areaDOToArea(areaDOList);
	}

	@Override
	public Area getAreaById(Integer areaId) {
		if (areaId == null || areaId.intValue() <= 0) {
			return null;
		}
		AreaDO areaDO = areaMapper.selectById(areaId);
		return areaDOToArea(areaDO);
	}
	
	
	private Area areaDOToArea(AreaDO areaDO) {
		Area area = null;
		if (areaDO != null) {
			area = new Area();
			BeanUtils.copyProperties(areaDO, area);
		}
		return area;
	}
	
	private List<Area> areaDOToArea(List<AreaDO> areaDOList) {
		List<Area> areaList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(areaDOList)) {
			areaDOList.forEach(areaDO -> {
				Area area = new Area();
				BeanUtils.copyProperties(areaDO, area);
				areaList.add(area);
			});
		}
		return areaList;
	}

	@Override
	public List<Area> listAreasAndProvinces() {
		List<AreaDO> areaDOList = areaMapper.selectAll();
		List<Area> areas = areaDOToArea(areaDOList);
		if (CollectionUtils.isNotEmpty(areas)) {
			areas.forEach(area -> {
				List<Province> provinces = provinceService.listByAreaId(area.getId());
				area.setProvinceList(provinces);
			});
		}
		return areas;
	}

}
