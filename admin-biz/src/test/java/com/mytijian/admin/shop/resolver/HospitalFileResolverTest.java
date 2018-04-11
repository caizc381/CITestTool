package com.mytijian.admin.shop.resolver;


import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mytijian.admin.BaseTest;
import com.mytijian.admin.shop.model.HospitalImportData;

/**
 * 类HospitalFileResolver.java的实现描述：实现体检中心导入文件解析测试类
 * @author ljx 2018年1月30日 下午2:23:06
 */
public class HospitalFileResolverTest extends BaseTest{

	@Autowired
	HospitalFileResolver hospitalFileResolver;
	
	@Test
	public void test(){		
		URL url = HospitalFileResolverTest.class.getClassLoader().getResource("./hospital_import.xlsx");
		List<HospitalImportData>  hspitalImportDatas = hospitalFileResolver.hospitalFileResolve(new File(url.getFile()));
		System.out.println(hspitalImportDatas);
	}
}
