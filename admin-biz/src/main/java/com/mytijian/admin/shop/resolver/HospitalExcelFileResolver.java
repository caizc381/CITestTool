package com.mytijian.admin.shop.resolver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mytijian.admin.shop.enums.ShopExceptionEnum;
import com.mytijian.admin.shop.factory.ExcelFieldNameHspImportAtrrMapFactory;
import com.mytijian.admin.shop.model.HospitalImportData;
import com.mytijian.admin.util.ExcelUtil;
import com.mytijian.base.result.BizException;

/**
 * 类HospitalFileResolver.java的实现描述：实现体检中心excel导入文件解析
 * @author ljx 2018年1月30日 下午2:23:06
 */
@Component
public class HospitalExcelFileResolver implements HospitalFileResolver {

	private Logger logger = LoggerFactory.getLogger(HospitalExcelFileResolver.class);

	/**实现体检中心导入文件解析
	 * @param hspExcelFile
	 * @return
	 */
	@Override
	public List<HospitalImportData> hospitalFileResolve(File hspExcelFile) {
		try {
			return analyzeExcel(WorkbookFactory.create(hspExcelFile));
		} catch (InvalidFormatException | IOException ex) {
			logger.error("resolve hospital import excel file error:", ex);
			throw new BizException(ShopExceptionEnum.HOSOPITAL_IMPORT_FILE_ERROR);
		}
	}

	private List<HospitalImportData> analyzeExcel(Workbook workbook) {

		List<HospitalImportData> hspImportDatas = new ArrayList<HospitalImportData>();
		Sheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();
		if (rows > 0) {
			// 获取表头信息，第2行是表头字段
			createHeaderFields(sheet.getRow(1));
			// 前3行是表头
			for (int i = 3; i < rows; i++) {
				hspImportDatas.add(getHspImportData(sheet.getRow(i)));

			}
		}
		return hspImportDatas;

	}

	private HospitalImportData getHspImportData(Row row) {
		if (row == null) {
			return null;
		}

		try {
			// 创建表头和对象属性映射
			Map<String, String> excelFieldNameToHspImportAtrrMap = ExcelFieldNameHspImportAtrrMapFactory
					.createExcelFieldNameToHspImportAtrrMap();
			HospitalImportData hspImportData = new HospitalImportData();
			short first = row.getFirstCellNum();
			short last = row.getLastCellNum();
			for (short j = first; j < last; j++) {
				Cell cell = row.getCell(j);
				String attrName = excelFieldNameToHspImportAtrrMap.get(headerFields.get(j));
				if (cell != null && attrName !=null) {
					HospitalImportData.class.getMethod("set" + attrName.substring(0, 1).toUpperCase()+ attrName.substring(1), new Class[] { String.class }).invoke(
							hspImportData, new Object[] { ExcelUtil.getCellStringValue(cell) });
				}
			}

			return hspImportData;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException ex) {
			logger.error("resolve hospital import excel file error:", ex);
			throw new BizException(ShopExceptionEnum.HOSOPITAL_IMPORT_FILE_ERROR);
		}
	}

	private List<String> headerFields = new ArrayList<String>();

	private void createHeaderFields(Row header) {
		if (header != null && headerFields.size() == 0) {
			short first = header.getFirstCellNum();
			short last = header.getLastCellNum();

			for (short j = first; j < last; j++) {
				Cell cell = header.getCell(j);
				if (cell != null) {
					headerFields.add(ExcelUtil.getCellStringValue(cell).replaceAll(" ",""));
				}
			}

		}
	}
}
