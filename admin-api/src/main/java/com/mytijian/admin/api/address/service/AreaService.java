package com.mytijian.admin.api.address.service;

import java.util.List;

import com.mytijian.admin.api.address.model.Area;


/**
 * 区域
 * @author feng
 *
 */
public interface AreaService {
	
	/**
	 * 获取全部区域
	 * @return
	 */
	List<Area> listAllAreas();
	
	/**
	 * 根据区域Id获取区域信息
	 * @param areaId 区域Id
	 * @return
	 */
	Area getAreaById(Integer areaId);
	
	/**
	 * 获取全部区域和省份
	 * @return
	 */
	List<Area> listAreasAndProvinces();
}
