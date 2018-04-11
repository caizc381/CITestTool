package com.mytijian.admin.web.controller.hospital;

import java.io.*;
import java.net.URLEncoder;
import java.util.Date;

import com.mytijian.util.DateUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mytijian.admin.shop.model.HospitalImportResult;
import com.mytijian.admin.shop.service.HospitalImportService;
import com.mytijian.resource.exceptions.HospitalException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

/**
 * 类OrgImportController.java的实现描述：机构导入controller
 * @author ljx 2018年1月26日 下午5:33:24
 */
@RestController
public class OrgImportController {
	
	@Autowired
	private HospitalImportService hospitalImportService;

	/**
	 * 机构导入
	 * @param file
	 * @param brandId
	 * @param type
	 * @return
	 * @throws HospitalException 
	 */
	@PostMapping("/orgImport")
	@ResponseBody
	public HospitalImportResult orgImport(@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "brandId", required = false) Integer brandId,
			@RequestParam(value = "type", required = true) String type) throws  IOException {
		
		File tempFile = new File("/tmp/" +file.getOriginalFilename());
		file.transferTo(tempFile);
		return hospitalImportService.hospitalImport(tempFile, brandId, type);
	}

	@GetMapping("/orgImportResult")
	public HospitalImportResult getOrgImport(@RequestParam("importId") String importId){

		return hospitalImportService.getHospitalImportResult(importId);
	}


	@GetMapping("/getOrgImportFile")
	public void getorgImportFile(HttpServletResponse response) throws IOException {

		InputStream tplInputStream = getClass().getClassLoader().getResourceAsStream(
				"orgImport/orgimport_hospital_template.xlsx");


		String origin = response.getHeader("Access-Control-Allow-Origin");
		response.reset();
		response.setContentType("application/vnd.ms-excel;charset=utf-8");

		response.setHeader("Content-Disposition",
				String.format("attachment;filename=\"%s\"",new String("体检中心导入模板.xlsx".getBytes("UTF-8"),"ISO-8859-1"))
				);
		response.addHeader("Access-Control-Allow-Credentials","true");
		response.addHeader("Access-Control-Allow-Origin",origin);


		byte[] bytes = IOUtils.toByteArray(tplInputStream);
		OutputStream outputStream = response.getOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		IOUtils.copy(bais, outputStream);
		outputStream.flush();
		outputStream.close();
		tplInputStream.close();
		bais.close();
	}


}
