package com.mytijian.mediator.dao.test;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.mediator.company.migrate.dao.CompanyMigrateLogMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.CompanyMigrateLogDO;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class CompanyMigrateLogMapperTest extends DbUnitTestTemplate {

	@Resource(name = "companyMigrateLogMapper")
	private CompanyMigrateLogMapper companyMigrateLogMapper;

	@Test
	public void test() {
		CompanyMigrateLogDO comp  = companyMigrateLogMapper.selectByTableName("tb_exam_company");
		Assert.assertNotNull(comp);
		
		companyMigrateLogMapper.updateLastPrimaryKeyIdByTableName("tb_exam_company", 900);
		
		comp  = companyMigrateLogMapper.selectByTableName("tb_exam_company");
		Assert.assertEquals(900, comp.getLastPrimaryKeyId().intValue());
	}
	
}
