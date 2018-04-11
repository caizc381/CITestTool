package com.mytijian.admin.dao.rbac.dao;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.mytijian.admin.dao.rbac.dataobject.EmployeeDO;
import com.mytijian.test.DbUnitTestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup(value = "common.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = { "common.xml" }, type = DatabaseOperation.TRUNCATE_TABLE)
public class EmployeeMapperTest extends DbUnitTestTemplate{
	
	@Resource(name="employeeMapper")
	private EmployeeMapper employeeMapper;
	
	@Test
	public void getOperationInfoByDepTest(){
		List<EmployeeDO> list = employeeMapper.selectEmployeeInfoByDep(46);
		Assert.assertEquals(1, list.size());
	}
	
//	@Test
//	public void getAllEmployeeInfo(){
//		List<EmployeeDO> list =employeeMapper.getAllEmployeeInfo();
//		System.out.println(list.toString());
//	}
}
