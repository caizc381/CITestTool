package com.mytijian.admin.web.controller.hospital;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mytijian.admin.util.ExcelUtil;
import com.mytijian.admin.web.controller.hospital.param.PlateformChannelDiscountHospitalQuery;
import com.mytijian.admin.web.controller.hospital.param.PlatformChannelTradeSettingReq;
import com.mytijian.admin.web.controller.hospital.util.PlatformChannelDiscountUtil;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.organization.param.OrganizationQuery;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.util.DateUtils;

/**
 * 渠道管理controller
 *
 * @author king
 */
@Controller
public class ChannelDiscountManagementController {
	
	private final static Logger logger = LoggerFactory.getLogger(ChannelDiscountManagementController.class);

    @Resource
    private HospitalService hospitalService;

    /**
	 * 获取渠道折扣管理体检中心列表
	 * @param addressId
	 * @param hospitalName
	 * @return
	 */
	@PostMapping(value = "/listPlatformChannelDiscountHospitals")
	@ResponseBody
	public PageView<Hospital> listPlatformChannelDiscountHospitals(@RequestBody PlateformChannelDiscountHospitalQuery query){
		return getPlatformChannelDiscountHospitals(query.getAddressId(), query.getHospitalId(), query.getPage());
	}
	
	/**
	 * 导出渠道折扣管理体检中心列表
	 * @param addressId
	 * @param hospitalId
	 * @return
	 * @throws IOException 
	 */
	@GetMapping(value = "/exportPlatformChannelDiscount")
	@ResponseStatus(value = HttpStatus.OK)
	public void exportPlatformChannelDiscount(Integer addressId, Integer hospitalId, HttpServletResponse response)
			throws IOException {
		// 生成excel
		PageView<Hospital> result = getPlatformChannelDiscountHospitals(addressId, hospitalId, null);
		InputStream tplInputStream = getClass().getClassLoader().getResourceAsStream(
				"qdDiscountTemplate/qd_defaultdiscount_template.xls");
		HSSFWorkbook workbook = ExcelUtil.createHSSFWorkbookByTemplate(
				PlatformChannelDiscountUtil.convertExportPlatformChannelDiscountData(result.getRecords()),
				tplInputStream,null);
		// 输出excel到客户端
		String origin = response.getHeader("Access-Control-Allow-Origin");
		response.reset();
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setHeader("Content-Disposition",
				"attachment;filename=" + "qd_defaultdiscount_" + DateUtils.format(DateUtils.YYYYMMDDSS, new Date())
						+ ".xls");
		response.addHeader("Access-Control-Allow-Credentials","true");
		response.addHeader("Access-Control-Allow-Origin",origin);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		OutputStream outputStream = response.getOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		IOUtils.copy(bais, outputStream);
		outputStream.flush();
		outputStream.close();
		tplInputStream.close();
		baos.close();
		bais.close();

	}

	private PageView<Hospital> getPlatformChannelDiscountHospitals(Integer addressId,Integer hospitalId, Page page) {
		OrganizationQuery query = new OrganizationQuery();
		query.setAddressId(addressId);
		query.setHospitalId(hospitalId);
		query.setPage(page);
		query.setOrderById(1);
		query.setOrganizationType(OrganizationTypeEnum.HOSPITAL.getCode());
		return hospitalService.listHospitalInfo(query);
	}
	
	/**
	 * 保存平台渠道默认折扣
	 * @param tradeSetting
	 */
	@PostMapping(value = "/savePlatformChannelTradeSetting")
	@ResponseStatus(HttpStatus.OK)
	public void savePlatformChannelTradeSetting(@RequestBody PlatformChannelTradeSettingReq tradeSetting) {
		logger.info(
				"save plateform channel discount, hospitalId={}, platformChannelGuestDiscount={}, platformChannelCompDiscount={}, operatorId={}",
				tradeSetting.getHospitalId(), tradeSetting.getPlatformChannelGuestDiscount(),
				tradeSetting.getPlatformChannelCompDiscount(), SessionUtil.getEmployee().getId());
		HospitalSettings setting = new HospitalSettings();
		setting.setHospitalId(tradeSetting.getHospitalId());
		setting.setPlatformChannelGuestDiscount(tradeSetting.getPlatformChannelGuestDiscount());
		setting.setPlatformChannelCompDiscount(tradeSetting.getPlatformChannelCompDiscount());
		hospitalService.updateOrganizationSetting(setting);
		logger.info("save plateform channel discount success");
	}
}

