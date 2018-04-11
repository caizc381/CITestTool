package com.mytijian.admin.web.controller.site;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.admin.api.address.model.Area;
import com.mytijian.admin.api.address.model.Province;
import com.mytijian.admin.api.address.service.AreaService;
import com.mytijian.admin.api.address.service.ProvinceService;


/**
 * 
 * 类SiteController.java的实现描述：区域相关
 * @author zhanfei.feng 2017年4月7日 下午4:03:21
 */
@RestController
@RequestMapping("/site")
public class SiteController {

	private final static Logger logger = LoggerFactory.getLogger(SiteController.class);

	@Resource(name = "areaService")
	private AreaService areaService;
	
	@Resource(name = "provinceService")
	private ProvinceService provinceService;

	/**
	 * 获取全部区域列表
	 * @return
	 */
	@RequestMapping(value = "/areaList")
	public List<Area> areaList() {
		return areaService.listAllAreas();
	}

	/**
	 * 获取全部区域列表
	 * @return
	 */
	@RequestMapping(value = "/provinceList")
	public List<Province> provinceList() {
		return provinceService.listAllProvinces();
	}

	/**
	 * 获取全部区域省份列表
	 * @return
	 */
	@RequestMapping(value = "/areasAndProvinceList")
	public List<Area> treeList() {
		return areaService.listAreasAndProvinces();
	}
	
	/**
	 * 获取区域省份列表
	 * @return
	 */
	@RequestMapping(value = "/areaProvinces/{areaId}")
	public List<Province> areaProvinces(@PathVariable("areaId") Integer areaId) {
		if (areaId == null || areaId.intValue() <= 0) {
			logger.error("SiteController.areaProvinces error, areaId : {} ", areaId);
		}
		return provinceService.listByAreaId(areaId);
	}
	
	@RequestMapping("/areaInfo/{areaId}")
	public Area info(@PathVariable("areaId") Integer areaId) {
		if (areaId == null || areaId.intValue() <= 0) {
			logger.error("SiteController.info error, areaId : {} ", areaId);
		}
		return areaService.getAreaById(areaId);
	}
}
