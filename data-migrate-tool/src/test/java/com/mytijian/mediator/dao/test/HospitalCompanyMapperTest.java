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
import com.mytijian.mediator.company.migrate.dao.HospitalCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalCompanyDO;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class HospitalCompanyMapperTest extends DbUnitTestTemplate {
	@Resource(name = "hospitalCompanyMapper")
	private HospitalCompanyMapper hospitalCompanyMapper;
	
	@Test
	public void insertTest()
	{
		HospitalCompanyDO hospitalCompanyDO = new HospitalCompanyDO();
		hospitalCompanyDO.setId(1000);
		hospitalCompanyDO.setName("每日健康");
		hospitalCompanyDO.setPlatformCompanyId(2000);
		hospitalCompanyDO.setOrganizationId(1111);
		hospitalCompanyDO.setDiscount(0.88);
		hospitalCompanyDO.setSettlementMode(1);
		hospitalCompanyDO.setSendExamSms(true);
		hospitalCompanyDO.setSendExamSmsDays(2);
		hospitalCompanyDO.setDeleted(false);
		hospitalCompanyDO.setPinyin("mrjk");
		hospitalCompanyDO.setTbExamCompanyId(22222);
		hospitalCompanyDO.setOrganizationName("每日健康");
		
		hospitalCompanyMapper.insert(hospitalCompanyDO);
		List<HospitalCompanyDO> hcDO  = hospitalCompanyMapper.selectByExamCompanyId(22222);
		Assert.assertEquals("mrjk",hcDO.get(0).getPinyin());
	}
	
	@Test
	public void insertListTest()
	{
		HospitalCompanyDO hospitalCompanyDO = new HospitalCompanyDO();
		hospitalCompanyDO.setId(1000);
		hospitalCompanyDO.setName("每日健康");
		hospitalCompanyDO.setPlatformCompanyId(2000);
		hospitalCompanyDO.setOrganizationId(1111);
		hospitalCompanyDO.setDiscount(0.88);
		hospitalCompanyDO.setSettlementMode(1);
		hospitalCompanyDO.setSendExamSms(true);
		hospitalCompanyDO.setSendExamSmsDays(2);
		hospitalCompanyDO.setDeleted(false);
		hospitalCompanyDO.setPinyin("mrjk");
		hospitalCompanyDO.setTbExamCompanyId(33333);
		hospitalCompanyDO.setOrganizationName("每日健康");
		
		List<HospitalCompanyDO> hcdoList = new ArrayList<>();
		hcdoList.add(hospitalCompanyDO);
		hospitalCompanyMapper.insertList(hcdoList);
		List<HospitalCompanyDO> hcDO  = hospitalCompanyMapper.selectByExamCompanyId(33333);
		Assert.assertEquals(1,hcDO.size());
	}
	
	@Test
	public void updateExamCompanyTest()
	{
		HospitalCompanyDO hospitalCompanyDO = new HospitalCompanyDO();
		hospitalCompanyDO.setId(1000);
		hospitalCompanyDO.setName("每日健康");
		hospitalCompanyDO.setPlatformCompanyId(2000);
		hospitalCompanyDO.setOrganizationId(1111);
		hospitalCompanyDO.setDiscount(0.88);
		hospitalCompanyDO.setSettlementMode(1);
		hospitalCompanyDO.setSendExamSms(true);
		hospitalCompanyDO.setSendExamSmsDays(2);
		hospitalCompanyDO.setDeleted(false);
		hospitalCompanyDO.setPinyin("mrjk");
		hospitalCompanyDO.setTbExamCompanyId(44444);
		hospitalCompanyDO.setOrganizationName("每日健康");
		hospitalCompanyMapper.insert(hospitalCompanyDO);
		
		List<HospitalCompanyDO> hcDOlist  = hospitalCompanyMapper.selectByExamCompanyId(44444);
		Assert.assertEquals("mrjk",hcDOlist.get(0).getPinyin());
		
		HospitalCompanyDO  hcDO = hcDOlist.get(0);
		hcDO.setPinyin("tttt");

		hospitalCompanyMapper.updateExamCompany(hcDO);
		List<HospitalCompanyDO> hcDOx = hospitalCompanyMapper.selectByExamCompanyId(44444);
		Assert.assertEquals(1,hcDOx.size());
		Assert.assertEquals("tttt",hcDOx.get(0).getPinyin());
		
	}
}
