package com.mytijian.admin.web.controller.trade.card.util;

import com.mytijian.base.result.BizException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ExcelUtil {

    private final static Logger logger = LoggerFactory.getLogger(com.mytijian.admin.util.ExcelUtil.class);

    public  static HSSFWorkbook createHSSFWorkbookByTemplate(List<List<String>> data, InputStream templateStream)
            throws BizException {

        try {
            HSSFWorkbook workbook = new HSSFWorkbook(templateStream);
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
}
