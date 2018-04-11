package com.mytijian.admin.shop.service;

import java.io.File;

import com.mytijian.admin.shop.model.HospitalImportResult;
import com.mytijian.resource.exceptions.HospitalException;

/**
 * 类HospitalImportService.java的实现描述：医院导入
 * @author ljx 2018年1月31日 上午10:35:33
 */
public interface HospitalImportService {

	/**
	 * 医院导入
	 * @param hsplFile
	 * @param brandId 品牌id
	 * @param type 公立医院、民营品牌
	 * @return
	 * @throws HospitalException 
	 */
	public HospitalImportResult hospitalImport(File hsplFile, Integer brandId, String type);

	/**
	 * 获取医院导入状态
	 * @param uuid 编号
	 * @return
	 */
	public  HospitalImportResult getHospitalImportResult(String uuid);
}
