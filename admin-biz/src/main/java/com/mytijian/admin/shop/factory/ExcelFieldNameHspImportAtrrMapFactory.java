package com.mytijian.admin.shop.factory;

import com.mytijian.admin.shop.model.ExcelFieldName;
import com.mytijian.admin.shop.model.HospitalImportData;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ExcelFieldNameHspImportAtrrMapFactory {

    private static Map<String, String> excelFieldNameToHspImportAtrr = null;
    private static Map<String, ExcelFieldName> hspImportAtrrToExcelFieldNameMap = null;

    /**
     * 生成excel表头到医院导入数据对象属性映射
     *
     * @return
     */
    public static Map<String, String> createExcelFieldNameToHspImportAtrrMap() {
        if (excelFieldNameToHspImportAtrr == null) {
            excelFieldNameToHspImportAtrr = createExcelFieldNameToObjectAtrrMap(true);
        }
        return excelFieldNameToHspImportAtrr;
    }

    /**
     * 生成医院导入数据对象属性到excel表头映射
     *
     * @return
     */
    public static Map<String, ExcelFieldName> createHspImportAtrrToExcelFieldNameMap() {
        if (hspImportAtrrToExcelFieldNameMap == null) {
            hspImportAtrrToExcelFieldNameMap = createExcelFieldNameToObjectAtrrMap();
        }
        return hspImportAtrrToExcelFieldNameMap;
    }

    private static Map<String, String> createExcelFieldNameToObjectAtrrMap(boolean isExcelFieldNameToHspImportAtrr) {
        Map<String, String> map = new HashMap<String, String>();

        Field[] fields = HospitalImportData.class.getDeclaredFields();
        for (Field field : fields) {
            ExcelFieldName excelFieldName = field.getAnnotation(ExcelFieldName.class);
            if (excelFieldName != null) {
                if (isExcelFieldNameToHspImportAtrr) {
                    map.put(excelFieldName.fieldName(), field.getName());
                } else {
                    map.put(field.getName(), excelFieldName.fieldName());
                }
            }
        }
        return map;
    }

    private static Map<String, ExcelFieldName> createExcelFieldNameToObjectAtrrMap() {
        Map<String, ExcelFieldName> map = new HashMap<>();

        Field[] fields = HospitalImportData.class.getDeclaredFields();
        for (Field field : fields) {
            ExcelFieldName excelFieldName = field.getAnnotation(ExcelFieldName.class);
            if (excelFieldName != null) {
                map.put(field.getName(), excelFieldName);
            }
        }
        return map;
    }
}