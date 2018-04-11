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
import com.mytijian.mediator.company.migrate.dao.ExamCompanyHospitalRelMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.ExamCompanyHospitalRelDO;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class ExamCompanyHospitalRelMapperTest extends DbUnitTestTemplate {
	
	@Resource(name = "examCompanyHospitalRelMapper")
	private ExamCompanyHospitalRelMapper examCompanyHospitalRelMapper;
	
	@Test
	public void selectByCompanyIdTest()
	{ 
		List<ExamCompanyHospitalRelDO> examCompanyHospitalRelDO = examCompanyHospitalRelMapper.selectByCompanyId(555);
		
		Assert.assertEquals(2,examCompanyHospitalRelDO.size());
	}
}
