package com.mytijian.mediator.dao.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.company.migrate.dao.ExamCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.ExamCompanyDO;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class ExamCompanyMapperTest extends DbUnitTestTemplate {
	
	@Resource(name = "examCompanyMapper")
	private ExamCompanyMapper examCompanyMapper;
	
	@Test
	public void selectByLastPrimaryKeyIdTest()
	{
		List<ExamCompanyDO> examCompanyList = examCompanyMapper.selectByLastPrimaryKeyId(11,4);
		
		Assert.assertEquals(3,examCompanyList.size());
	}
	
	@Test
	public void selectById()
	{
		ExamCompanyDO examCompanyDO = examCompanyMapper.selectById(12);
		Assert.assertEquals("mtj",examCompanyDO.getName());
	}
}
