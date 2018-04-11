/*
 * Copyright 2017 mytijian.com All right reserved. This software is the
 * confidential and proprietary information of mytijian.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mytijian.com.
 */
package com.mytijian.admin.dao.address.dao;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.mytijian.admin.dao.address.dataobject.AreaDO;
import com.mytijian.origin.dbunit.plugin.DbUnitTestTemplate;

import static junit.framework.TestCase.*;

/**
 * 类AreaMapperNoRunTest.java的实现描述：TODO 类实现描述 
 * @author liangxing 2017年8月18日 下午4:04:43
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class AreaMapperTest extends DbUnitTestTemplate{
	
	@Resource(name="areaMapper")
	private AreaMapper areaMapper;
	
	@Test
	@DatabaseSetup("classpath:address/pre-insert.xml")
	//@ExpectedDatabase("classpath:address/pre-insert.xml")
	public void insertTest(){
		AreaDO areaDO = new AreaDO();
		areaDO.setAreaName("上城");
		areaMapper.insert(areaDO);
	}
	
	@Test
	@DatabaseSetup("classpath:address/exp-insert.xml")
	//@ExpectedDatabase("classpath:address/pre-insert.xml")
	public void deleteByIdTest(){
		areaMapper.deleteById(1);
	}

	@Test
	@DatabaseSetup("classpath:address/pre-insert.xml")
	public void selectAllTest(){
		List<AreaDO> list = areaMapper.selectAll();
		assertEquals(1, list.size());
	}
	
	
	@Test
	@DatabaseSetup("classpath:address/pre-insert.xml")
	public void selectByIdTest(){
		AreaDO areaDO = areaMapper.selectById(1);
		assertEquals("下沙", areaDO.getAreaName());
	}
	
	@Test
	@DatabaseSetup("classpath:address/pre-insert.xml")
	//@ExpectedDatabase("classpath:address/exp-update.xml")
	public void updateTest(){
		AreaDO areaDO = new AreaDO();
		areaDO.setId(1);
		areaDO.setAreaName("上海");
		areaMapper.update(areaDO);
	}
	
}
