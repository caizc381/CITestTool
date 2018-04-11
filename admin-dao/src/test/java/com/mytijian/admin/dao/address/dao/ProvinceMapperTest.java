/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.dao.address.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.mytijian.admin.dao.address.dataobject.ProvinceDO;
import com.mytijian.origin.dbunit.plugin.DbUnitTestTemplate;

import static junit.framework.TestCase.*;

/**
 * 类ProvinceMapperNoRunTest.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年8月18日 下午4:05:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class ProvinceMapperTest  extends DbUnitTestTemplate{
	
	@Resource(name="provinceMapper")
	private ProvinceMapper provinceMapper;
	
	@Test
	@DatabaseSetup("classpath:address/pre-province-insert.xml")
	public void selectAll() {
		List<ProvinceDO> list = provinceMapper.selectAll();
		assertEquals(1, list.size());
	}

	@Test
	@DatabaseSetup("classpath:address/pre-province-insert.xml")
	public void selectById() {
		ProvinceDO provinceDO = provinceMapper.selectById(1);
		assertEquals("北京", provinceDO.getProvinceName());
	}

	@Test
	@DatabaseSetup("classpath:address/pre-province-insert.xml")
	public void selectByProvinceId() {
		ProvinceDO provinceDO = provinceMapper.selectByProvinceId(1);
		assertEquals("北京", provinceDO.getProvinceName());
	}

	@Test
	@DatabaseSetup("classpath:address/pre-province-insert.xml")
	public void selectByIdOrProvinceId() {
		Map<String, Object>  map = new HashMap<String, Object>();
		map.put("id", 1);
		
		ProvinceDO provinceDO = provinceMapper.selectByIdOrProvinceId(map);
		assertEquals("北京", provinceDO.getProvinceName());
		
		Map<String, Object>  map1 = new HashMap<String, Object>();
		map1.put("provinceId", 1);
		ProvinceDO provinceDO1 = provinceMapper.selectByIdOrProvinceId(map1);
		assertEquals("北京", provinceDO1.getProvinceName());
	}

	@Test
	@DatabaseSetup("classpath:address/pre-province-insert.xml")
	public void selectByParentId() {
		List<ProvinceDO> list = provinceMapper.selectByParentId(0);
		assertEquals(1, list.size());
	}

}
