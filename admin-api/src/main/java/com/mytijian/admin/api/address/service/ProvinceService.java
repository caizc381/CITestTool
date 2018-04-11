package com.mytijian.admin.api.address.service;

import java.util.List;

import com.mytijian.admin.api.address.model.Province;


public interface ProvinceService {
	
	/**
	 * 获取所有省份
	 * @return
	 */
	List<Province> listAllProvinces();
	
	/**
	 * 根据区域Id获取所有省份列表
	 * @param areaId 区域Id
	 * @return
	 */
	List<Province> listByAreaId(Integer areaId);
	
	/**
	 * 根据主键或省份Id获取省份信息
	 * @param id 主键
	 * @param provinceId 省份Id
	 * @return
	 */
	Province getByIdOrProvinceId(Integer id, Integer provinceId);
	
}
