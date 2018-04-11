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
import com.mytijian.mediator.company.migrate.dao.ChannelCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.HospitalCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.PlatformCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.ChannelCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.PlatformCompanyDO;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class NewCompanyMapperTest extends DbUnitTestTemplate {

	@Resource(name = "platformCompanyMapper")
	private PlatformCompanyMapper platformCompanyMapper;
	
	@Resource(name = "hospitalCompanyMapper")
	private HospitalCompanyMapper hospitalCompanyMapper;
	
	@Resource(name = "channelCompanyMapper")
	private ChannelCompanyMapper channelCompanyMapper;

	@Test
	public void test() {
		PlatformCompanyDO platformCompanyDO = new PlatformCompanyDO();
		platformCompanyMapper.insert(platformCompanyDO);
	}
	
	@Test
	public void test2(){
		HospitalCompanyDO hospitalCompanyDO = new HospitalCompanyDO();
		hospitalCompanyMapper.insert(hospitalCompanyDO);
		
		List<HospitalCompanyDO> list = new ArrayList<HospitalCompanyDO>();
		list.add(hospitalCompanyDO);
		hospitalCompanyMapper.insertList(list);
	}
	
	@Test
	public void test3() {
		ChannelCompanyDO channelCompanyDO = new ChannelCompanyDO();
		channelCompanyMapper.insert(channelCompanyDO);

		List<ChannelCompanyDO> list = new ArrayList<ChannelCompanyDO>();
		list.add(channelCompanyDO);
		channelCompanyMapper.insertList(list);
	}
	
	@Test
	public void testSelectOrginationType(){
		List<PlatformCompanyDO> hosList = platformCompanyMapper
				.selectByOrganizationType("hospital");
		Assert.assertEquals(1, hosList.size());
	}
	
}
