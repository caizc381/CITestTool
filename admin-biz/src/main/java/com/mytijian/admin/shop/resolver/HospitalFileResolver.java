package com.mytijian.admin.shop.resolver;

import java.io.File;
import java.util.List;

import com.mytijian.admin.shop.model.HospitalImportData;
/**
 * 类HospitalFileResolver.java的实现描述：实现体检中心导入文件解析
 * @author ljx 2018年1月30日 下午2:23:06
 */
public interface HospitalFileResolver {

	/**
	 * 解析体检中心导入文件
	 * @param hsplFile
	 * @return
	 */
	public List<HospitalImportData> hospitalFileResolve(File hsplFile);
}
