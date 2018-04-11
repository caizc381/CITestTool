package com.mytijian.admin.dao.address.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mytijian.admin.dao.address.dataobject.AreaDO;
import com.mytijian.admin.dao.base.mapper.BaseMapper;


@Repository("areaMapper")
public interface AreaMapper extends BaseMapper<AreaDO> {
	
	/**
	 * 查询全部区域
	 * @return
	 */
	List<AreaDO> selectAll();
}
