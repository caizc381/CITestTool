package com.mytijian.admin.dao.address.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.address.dataobject.ProvinceDO;


@Repository("provinceMapper")
public interface ProvinceMapper {
	
	/**
	 * 查询全部省份
	 * @return
	 */
	List<ProvinceDO> selectAll();
	
	/**
	 * 根据Id获取省份信息
	 * @param id 主键 
	 * @return
	 */
	ProvinceDO selectById(Integer id);
	
	/**
	 * 根据省份Id 获取省份信息
	 * @param provinceId 省份Id
	 * @return
	 */
	ProvinceDO selectByProvinceId(Integer provinceId);
	
	/**
	 * 根据Id 或省份Id 获取省份信息
	 * @param map
	 * @return
	 */
	ProvinceDO selectByIdOrProvinceId(Map<String, Object> map);
	
	/**
	 * 根据区域Id获取区域所属省份列表
	 * @param parentId 省份所属区域Id
	 * @return
	 */
	List<ProvinceDO> selectByParentId(Integer parentId);
}
