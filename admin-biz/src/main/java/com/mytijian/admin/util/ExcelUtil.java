package com.mytijian.admin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mytijian.base.result.BizException;

public class ExcelUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	/**
	 * 根据模板创建excel，默认模板的第一行（并且只有一行）是表头
	 * @param data
	 * @param templateStream
	 * @return
	 * @throws BizException
	 */
	public static HSSFWorkbook createHSSFWorkbookByTemplate(List<List<String>> data, InputStream templateStream,String sheetName)
			throws BizException {

		try {
			HSSFWorkbook workbook = new HSSFWorkbook(templateStream);
			if (StringUtils.isNotBlank(sheetName)){
				workbook.setSheetName(0,sheetName);
			}
			HSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 0; i < data.size(); i++) {
				HSSFRow row = sheet.createRow(i+1);
				for (int j = 0; j < data.get(i).size(); j++) {
					String value = data.get(i).get(j);
					HSSFCell cell = row.createCell(j);
					cell.setCellValue(value);
				}
			}
			return workbook;
		} catch (IOException e) {
			logger.error("create excel error: ", e);
			throw new BizException("10000", "创建excel文件错误");
		}
	}
	

	public static String getCellStringValue(Cell cell) {
		String cellValue = null;
		if (cell == null)
			return null;
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			cellValue = cell.getStringCellValue();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			cellValue = String.valueOf(cell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			cellValue = getFormulaValue(cell);
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			cellValue = null;
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			break;
		default:
			break;
		}
		return cellValue;
	}
	
	/**
	 * 获取函数公式值
	 * @param cell 单元格
	 * @return
	 */
	private static String getFormulaValue(Cell cell){
		String value = null;
		FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
		CellValue cellValue = evaluator.evaluate(cell);
		switch (cellValue.getCellType()){
			case HSSFCell.CELL_TYPE_STRING:
				value = cellValue.getStringValue();
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				value = String.valueOf(cellValue.getNumberValue());
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				value = null;
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				break;
			case HSSFCell.CELL_TYPE_ERROR:
				break;
			default:
				break;
		}
		return value;
	}


}
