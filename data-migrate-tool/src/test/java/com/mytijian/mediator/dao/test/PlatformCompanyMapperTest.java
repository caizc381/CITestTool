package com.mytijian.mediator.dao.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.company.migrate.dao.PlatformCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.PlatformCompanyDO;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class PlatformCompanyMapperTest extends DbUnitTestTemplate  {
	
	@Resource(name = "platformCompanyMapper")
	private PlatformCompanyMapper platformCompanyMapper;
	
	@Test
	public void insertTest()
	{
		PlatformCompanyDO pcompDo = new PlatformCompanyDO();
		pcompDo.setName("每日健康");
		pcompDo.setDescription("test");
		pcompDo.setPinyin("mrjk");
		pcompDo.setInit("xxx");
		pcompDo.setDeleted(false);
		pcompDo.setTbExamCompanyId(5555);
		platformCompanyMapper.insert(pcompDo);
		
		PlatformCompanyDO platformCompanyDO = platformCompanyMapper.selectByExamCompanyId(5555);
		Assert.assertEquals("mrjk",platformCompanyDO.getPinyin());
	}
	
	@Test
	public void insertListTest()
	{
		PlatformCompanyDO pcompDo = new PlatformCompanyDO();
		pcompDo.setName("每日健康");
		pcompDo.setDescription("test");
		pcompDo.setPinyin("mrjk");
		pcompDo.setInit("xxx");
		pcompDo.setDeleted(false);
		pcompDo.setTbExamCompanyId(5555);
		
		List<PlatformCompanyDO> hcdoList = new ArrayList<>();
		hcdoList.add(pcompDo);
		platformCompanyMapper.insertList(hcdoList);
		
		PlatformCompanyDO platformCompanyDO = platformCompanyMapper.selectByExamCompanyId(5555);
		Assert.assertEquals("mrjk",platformCompanyDO.getPinyin());
	}
	
	@Test
	public void selectByOrganizationTypeTest()
	{
		List<PlatformCompanyDO> pcompDoList = platformCompanyMapper.selectByOrganizationType("hospital");
		Assert.assertEquals(1,pcompDoList.size());
	}
	
	@Test
	public void updatePlatformCompanyTest()
	{
		PlatformCompanyDO pcompDo = new PlatformCompanyDO();
		pcompDo.setName("每日健康");
		pcompDo.setDescription("test");
		pcompDo.setPinyin("mrjk");
		pcompDo.setInit("xxx");
		pcompDo.setDeleted(false);
		pcompDo.setTbExamCompanyId(5555);
		platformCompanyMapper.insert(pcompDo);
		PlatformCompanyDO platformCompanyDO = platformCompanyMapper.selectByExamCompanyId(5555);
		Assert.assertEquals("mrjk",platformCompanyDO.getPinyin());
		
		platformCompanyDO.setPinyin("ccccc");
		platformCompanyMapper.updatePlatformCompany(platformCompanyDO);
		
		PlatformCompanyDO pDO = platformCompanyMapper.selectByExamCompanyId(5555);
		Assert.assertEquals("ccccc",pDO.getPinyin());
		
	}
	
}
