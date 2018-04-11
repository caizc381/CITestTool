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
import com.google.common.collect.Lists;
import com.mytijian.mediator.company.migrate.dao.ChannelCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.ChannelCompanyDO;
import com.mytijian.test.DbUnitTestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "classpath:log.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "classpath:log.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class ChannelCompanyMapperTest extends DbUnitTestTemplate {
	
	@Resource(name = "channelCompanyMapper")
	private ChannelCompanyMapper channelCompanyMapper;
	
	@Test
	public void insertTest()
	{
		ChannelCompanyDO channelCompanyDO = new ChannelCompanyDO();
		channelCompanyDO.setId(1000);
		channelCompanyDO.setName("每日健康");
		channelCompanyDO.setPlatformCompanyId(2000);
		channelCompanyDO.setOrganizationId(1111);
		channelCompanyDO.setDiscount(0.88);
		channelCompanyDO.setSettlementMode(1);
		channelCompanyDO.setSendExamSms(true);
		channelCompanyDO.setSendExamSmsDays(2);
		channelCompanyDO.setDeleted(false);
		channelCompanyDO.setPinyin("mrjk");
		channelCompanyDO.setTbExamCompanyId(22222);
		channelCompanyDO.setOrganizationName("每日健康");
		channelCompanyMapper.insert(channelCompanyDO);
		
		ChannelCompanyDO ccDo = channelCompanyMapper.selectByExamCompanyId(22222);
		
		Assert.assertEquals("mrjk",ccDo.getPinyin());
	}
	
	@Test
	public void insertListTest()
	{
		List<ChannelCompanyDO> channelCompanyDOList = Lists.newArrayList();
		
		ChannelCompanyDO channelCompanyDO = new ChannelCompanyDO();
		channelCompanyDO.setId(1000);
		channelCompanyDO.setName("每日健康");
		channelCompanyDO.setPlatformCompanyId(2000);
		channelCompanyDO.setOrganizationId(1111);
		channelCompanyDO.setDiscount(0.88);
		channelCompanyDO.setSettlementMode(1);
		channelCompanyDO.setSendExamSms(true);
		channelCompanyDO.setSendExamSmsDays(2);
		channelCompanyDO.setDeleted(false);
		channelCompanyDO.setPinyin("mrjk");
		channelCompanyDO.setTbExamCompanyId(33333);
		channelCompanyDO.setOrganizationName("每日健康");
		channelCompanyDOList.add(channelCompanyDO);
		
		channelCompanyMapper.insertList(channelCompanyDOList);
		
		ChannelCompanyDO ccDo = channelCompanyMapper.selectByExamCompanyId(33333);
		
		Assert.assertEquals("mrjk",ccDo.getPinyin());
		
	}
	
	@Test
	public void updateChannelCompanyTest()
	{
		ChannelCompanyDO channelCompanyDO = new ChannelCompanyDO();
		channelCompanyDO.setId(1000);
		channelCompanyDO.setName("每日健康");
		channelCompanyDO.setPlatformCompanyId(2000);
		channelCompanyDO.setOrganizationId(1111);
		channelCompanyDO.setDiscount(0.88);
		channelCompanyDO.setSettlementMode(1);
		channelCompanyDO.setSendExamSms(true);
		channelCompanyDO.setSendExamSmsDays(2);
		channelCompanyDO.setDeleted(false);
		channelCompanyDO.setPinyin("mrjk");
		channelCompanyDO.setTbExamCompanyId(44444);
		channelCompanyDO.setOrganizationName("每日健康");
		channelCompanyMapper.insert(channelCompanyDO);
		
		ChannelCompanyDO ccDo = channelCompanyMapper.selectByExamCompanyId(44444);
		
		Assert.assertEquals("mrjk",ccDo.getPinyin());
		
		ccDo.setPinyin("test");
		channelCompanyMapper.updateChannelCompany(ccDo);
		Assert.assertEquals("test",ccDo.getPinyin());
		
	}
}
