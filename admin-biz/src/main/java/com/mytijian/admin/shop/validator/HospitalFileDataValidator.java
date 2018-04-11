package com.mytijian.admin.shop.validator;

import com.google.common.collect.Lists;
import com.mytijian.account.enums.SystemTypeEnum;
import com.mytijian.account.model.User;
import com.mytijian.account.service.UserService;
import com.mytijian.admin.shop.factory.ExcelFieldNameHspImportAtrrMapFactory;
import com.mytijian.admin.shop.model.ErrorHospital;
import com.mytijian.admin.shop.model.ExcelFieldName;
import com.mytijian.admin.shop.model.HospitalDataValidateResult;
import com.mytijian.admin.shop.model.HospitalImportData;
import com.mytijian.resource.model.Address;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.AddressService;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.site.service.SiteService;
import com.mytijian.util.MobileValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author king
 */
@Service("hospitalFileDataValidator")
public class HospitalFileDataValidator implements HospitalImportDataValidator {



    @Autowired
    private AddressService addressService;


    private Map<String, String> userNameMap;


    private Map<String, String> urlMap;

    private Map<String,String> levelMap = new HashMap<>();


    @Autowired
    private UserService userService;


    @Autowired
    private SiteService siteService;


    /**
     * 数据库中存在的外部id
     */
    private Map<String,String> existOuterOrgCodeMap;

    /**
     * 表格中存在的外部id
     */
    private Map<String,String> outerOrgCodeMap;


    @Autowired
    private HospitalService hospitalService;


    /**
     * 体检中心数据校验接口
     *
     * @param HospitalInfos
     * @return
     */
    @Override
    public HospitalDataValidateResult validateHospitalFileData(List<HospitalImportData> HospitalInfos,Integer brandId) {

        //初始化url,userName数组
        initValidateFieldMap(brandId);

        //校验医院信息
        HospitalDataValidateResult hospitalDataValidateResult = validateHospitalInfos(HospitalInfos);

        //销毁url,userName数组
        cleanValidateFieldMap();

        return hospitalDataValidateResult;
    }

    private HospitalDataValidateResult validateHospitalInfos(List<HospitalImportData> HospitalInfos) {

        //校验医院信息。获取医院错误列表
        List<ErrorHospital> errorHospitals = Lists.newArrayList();
        for (HospitalImportData hospitalInfo : HospitalInfos) {
            //单个医院信息校验
            ErrorHospital errorHospital = validateSingleHospitalImportData(hospitalInfo);
            if (errorHospital != null) {
                errorHospitals.add(errorHospital);
            }
        }

        //构建校验结果
        HospitalDataValidateResult hospitalDataValidateResult = new HospitalDataValidateResult();
        if (!CollectionUtils.isEmpty(errorHospitals)) {
            hospitalDataValidateResult.setHaveError(true);
            hospitalDataValidateResult.setErrorHospitalInfos(errorHospitals);
        }
        return hospitalDataValidateResult;
    }


    /**
     * 初始化参数属性map
     * @param brandId 品牌id
     */
    private void initValidateFieldMap(Integer brandId) {
        if (userNameMap == null) {
            userNameMap = new HashMap<>();
        }
        if (urlMap == null) {
            urlMap = new HashMap<>();
        }
        if (outerOrgCodeMap == null){
            outerOrgCodeMap =new HashMap<>();
        }
        if (brandId!=null){
            buildHospitalBrandMap(brandId);
        }

        if (CollectionUtils.isEmpty(levelMap)){
            initLevelMap();
        }
    }

    private void initLevelMap() {
        levelMap.put("未知等级","未知等级");
        levelMap.put("三特","未知等级");
        levelMap.put("三甲","未知等级");
        levelMap.put("三乙","未知等级");
        levelMap.put("三丙","未知等级");
        levelMap.put("三级","未知等级");
        levelMap.put("二甲","未知等级");
        levelMap.put("二乙","未知等级");
        levelMap.put("二丙","未知等级");
        levelMap.put("二级","未知等级");
        levelMap.put("一甲","未知等级");
        levelMap.put("一乙","未知等级");
        levelMap.put("一丙","未知等级");
    }


    private  void buildHospitalBrandMap(Integer brandId){

        List<Integer> integers = hospitalService.listHospitalIdsByBrandId(brandId);
        List<Hospital> hospitalsByIds = hospitalService.getHospitalsByIds(integers);
        this.existOuterOrgCodeMap = hospitalsByIds.stream().filter(x-> !StringUtils.isEmpty(x.getOuterOrgCode() )).collect(Collectors.toMap(Hospital::getOuterOrgCode, Hospital::getOuterOrgCode));
    }

    /**
     * 清空属性map
     */
    private void cleanValidateFieldMap() {
        if (userNameMap != null) {
            this.userNameMap = null;
        }
            if (urlMap != null) {
            this.urlMap = null;
        }
        if (outerOrgCodeMap != null){
            this.outerOrgCodeMap = null;
        }
        if (!CollectionUtils.isEmpty(levelMap)){
            levelMap = new HashMap<>();
        }
    }


    /**
     * 纬度
     *
     * @param latitude
     * @return
     */
    private boolean isLatitude(String latitude) {

        if (!StringUtils.isEmpty(latitude)) {
            Double aDouble = Double.valueOf(latitude);
            return -90 < aDouble && aDouble < 90;
        }

        return false;
    }

    /**
     * 经度
     *
     * @param longitude
     * @return
     */
    private boolean isLongitude(String longitude) {
        if (!StringUtils.isEmpty(longitude)) {
            Double aDouble = Double.valueOf(longitude);
            return -180 < aDouble && aDouble < 180;
        }

        return false;
    }


    private boolean gender(String gender) {
        return !StringUtils.isEmpty(gender) && ("男".equals(gender) || "女".equals(gender));

    }

    private boolean isMobile(String mobile) {
        return !StringUtils.isEmpty(mobile) && MobileValidate.matches(mobile) && mobile.length()<20;
    }


    private boolean isCrmUsername(String crmUsername) {
        if (!userNameMap.containsKey(crmUsername)) {
            User user = userService.getUserBySystemType(crmUsername, SystemTypeEnum.CRM_LOGIN.getCode());
            if (user == null) {
                userNameMap.put(crmUsername, crmUsername);
                return true;
            }
        }
        return false;
    }



    private boolean isWorkDays(String workDay) {
        return "是".equals(workDay) || "否".equals(workDay);
    }

    private boolean isDate(String date) {
        try {
            if (!StringUtils.isEmpty(date)) {
                String[] str = date.split(":");
                if (str.length == 2) {
                    String s1 = str[0];
                    String s2 = str[1];
                    if (s1.length() <= 2 && s2.length() == 2) {
                        if (Integer.valueOf(s1) <= 24 && Integer.valueOf(s2) < 60) {
                            return true;
                        }
                    }

                }
            }
        } catch (Exception ex) {

        }

        return false;
    }


    private boolean validateStartEndDate(String start,String endDate) {
        String[] str = start.split(":");
        String s1 = str[0];
        String s2 = str[1];

        Integer start1 = Integer.valueOf(s1);
        Integer start2 = Integer.valueOf(s2);


        String[] str2 = endDate.split(":");
        String e1 = str2[0];
        String e2 = str2[1];

        Integer end1 = Integer.valueOf(e1);
        Integer end2 = Integer.valueOf(e2);


        return start1 < end1 || start1.equals(end1) && start2 <= end2;

    }


    private boolean isExamNoticeBreakFast(String string) {
        return "提供".equals(string) || "不提供".equals(string);
    }


    private boolean isCorrectAddress(String address) {
        List<Address> allAddress = addressService.getAllAddress();

        Map<String, Integer> addressMap = allAddress.stream().filter(x -> x.getDistrict() != null).collect(Collectors.toMap(x -> x.getProvince() + x.getCity() + x.getDistrict(), Address::getId));

        return addressMap.containsKey(address);
    }


    private ErrorHospital validateSingleHospitalImportData(HospitalImportData hospitalInfo) {
        Map<String, ExcelFieldName> hspImportAtrrToExcelFieldNameMap = ExcelFieldNameHspImportAtrrMapFactory.createHspImportAtrrToExcelFieldNameMap();

        List<ErrorHospital.ErrorAttr> list = Lists.newArrayList();
        ErrorHospital errorHospital = new ErrorHospital();
        if (StringUtils.isEmpty(hospitalInfo.getName())) {
            //名字是空
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "name");
        }

        if (!StringUtils.isEmpty(hospitalInfo.getOuterOrgCode())){
            //存在
            validateOuterOrgCodeMap(hospitalInfo, hspImportAtrrToExcelFieldNameMap, list);
        }

        //等级为空
        if (StringUtils.isEmpty(hospitalInfo.getLevel()) || !levelMap.containsKey(hospitalInfo.getLevel())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "level");
        }

        //省格式不正确
        if (StringUtils.isEmpty(hospitalInfo.getProvince())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "province");
        }

        if (StringUtils.isEmpty(hospitalInfo.getCity())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "city");
        }

        if (StringUtils.isEmpty(hospitalInfo.getDistrict())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "district");
        }

        boolean emptyAddress =StringUtils.isEmpty(hospitalInfo.getProvince()) ||StringUtils.isEmpty(hospitalInfo.getCity())||StringUtils.isEmpty(hospitalInfo.getDistrict());
        if (!emptyAddress && !this.isCorrectAddress(hospitalInfo.getProvince()+hospitalInfo.getCity()+hospitalInfo.getDistrict())){
            ErrorHospital.ErrorAttr errorAttr = new ErrorHospital.ErrorAttr();
            errorAttr.setName("省市区");
            errorAttr.setErrorDesc("省、市、区格式不正确");
            list.add(errorAttr);
        }


        //市//区 不校验
        if (StringUtils.isEmpty(hospitalInfo.getAddress())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "address");
        }

        //纬度
        if (!this.isLatitude(hospitalInfo.getLatitude())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "latitude");
        }

        //经度
        if (!this.isLongitude(hospitalInfo.getLongitude())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "longitude");
        }

        //管理员名字
        if (StringUtils.isEmpty(hospitalInfo.getManagerName())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "managerName");
        }

        //管理员性别
        if (!this.gender(hospitalInfo.getGender())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "gender");
        }

        //管理员手机号
        if (!this.isMobile(hospitalInfo.getMobile())) {

            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "mobile");
        }

        //crm 用户名
        validateUserName(hospitalInfo,hspImportAtrrToExcelFieldNameMap,list);

        //体检电话
        if (!this.isMobile(hospitalInfo.getPhone())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "phone");
        }

        //团检电话
        if (!this.isMobile(hospitalInfo.getGroupExamTel())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "groupExamTel");
        }

        //医院关键字
        if (StringUtils.isEmpty(hospitalInfo.getKeywords())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "keywords");
        }

        //体检须知
        if (StringUtils.isEmpty(hospitalInfo.getExamNotice())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "examNotice");
        }

        //提供早餐
        if (!this.isExamNoticeBreakFast(hospitalInfo.getIsBreakfast())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "isBreakfast");

        }

        //工作日  周一 到周五
        if (!this.isWorkDays(hospitalInfo.getMonday())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "monday");

        }
        //工作日  周一 到周五
        if (!this.isWorkDays(hospitalInfo.getTuesday())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "tuesday");
        }

        //工作日  周一 到周五
        if (!this.isWorkDays(hospitalInfo.getWednesday())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "wednesday");
        }

        //工作日  周一 到周五
        if (!this.isWorkDays(hospitalInfo.getThursday())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "thursday");

        }

        //工作日  周一 到周五
        if (!this.isWorkDays(hospitalInfo.getFriday())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "friday");
        }

        //工作日  周一 到周五
        if (!this.isWorkDays(hospitalInfo.getSaturday())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "saturday");
        }

        //工作日  周一 到周五
        if (!this.isWorkDays(hospitalInfo.getSunday())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "sunday");
        }

        if (!isWorkDays(hospitalInfo)){
            ErrorHospital.ErrorAttr errorAttr = new ErrorHospital.ErrorAttr();
            errorAttr.setName("工作日");
            errorAttr.setErrorDesc("请至少选择一个工作日");
            list.add(errorAttr);
        }

        //体检开始时间
        if (!this.isDate(hospitalInfo.getExamStartTime())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "examStartTime");

        }

        //体检结束时间
        if (!this.isDate(hospitalInfo.getExamEndTime())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "examEndTime");
        }

        //体检开始时间大约体检结束时间
        if (this.isDate(hospitalInfo.getExamStartTime()) &&this.isDate(hospitalInfo.getExamEndTime()) && !validateStartEndDate(hospitalInfo.getExamStartTime(),hospitalInfo.getExamEndTime()) ){
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "examStartTime");
        }
        //抽血开始时间
        if (!this.isDate(hospitalInfo.getDrawBloodStart())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "drawBloodStart");
        }

        //抽血结束时间
        if (!this.isDate(hospitalInfo.getDrawBloodEnd())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "drawBloodEnd");

        }

        //抽血开始时间大约抽血结束时间
        if (this.isDate(hospitalInfo.getDrawBloodEnd()) &&this.isDate(hospitalInfo.getDrawBloodStart()) && !validateStartEndDate(hospitalInfo.getDrawBloodStart(),hospitalInfo.getDrawBloodEnd()) ){
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "drawBloodStart");
        }


        //医院体检时段
        if (StringUtils.isEmpty(hospitalInfo.getHospitalPeriod())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "hospitalPeriod");
        }

        //时段预约人数
        if (StringUtils.isEmpty(hospitalInfo.getLimitNum()) || !org.apache.commons.lang.StringUtils.isNumeric(hospitalInfo.getLimitNum())) {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap, list, "limitNum");
        }

        //url
        validateSiteUrl(hospitalInfo,hspImportAtrrToExcelFieldNameMap,list);



        if (!CollectionUtils.isEmpty(list)) {
            errorHospital.setErrorAttrs(list);
            errorHospital.setOrgName(StringUtils.isEmpty(hospitalInfo.getName()) ? "未填写机构名称" : hospitalInfo.getName());
            return errorHospital;
        }
        return null;
    }

    private boolean isWorkDays(HospitalImportData hospitalInfo){

        if (isWorkDays(hospitalInfo.getMonday()) && isWorkDays(hospitalInfo.getThursday()) && isWorkDays(hospitalInfo.getTuesday()) && isWorkDays(hospitalInfo.getWednesday()) && isWorkDays(hospitalInfo.getFriday()) && isWorkDays(hospitalInfo.getSaturday()) && isWorkDays(hospitalInfo.getSunday())) {
            if (countWorkDay(hospitalInfo.getMonday()) + countWorkDay(hospitalInfo.getThursday()) + countWorkDay(hospitalInfo.getTuesday()) + countWorkDay(hospitalInfo.getWednesday()) + countWorkDay(hospitalInfo.getFriday()) + countWorkDay(hospitalInfo.getSaturday()) + countWorkDay(hospitalInfo.getSunday()) >= 1) {
                return true;

            }else {
                return  false;
            }

        }
        return true;
    }

    private int countWorkDay(String day){
        if ("是".equals(day)){
            return  1;
        }
        return 0;
    }


    private void validateSiteUrl(HospitalImportData hospitalInfo, Map<String, ExcelFieldName> hspImportAtrrToExcelFieldNameMap, List<ErrorHospital.ErrorAttr> list) {

        if (StringUtils.isEmpty(hospitalInfo.getUrl())){
            putErrorAttr(hspImportAtrrToExcelFieldNameMap,list,"url"," 必填");
        }else {
            if (siteService.checkSiteUrl(null,hospitalInfo.getUrl())){
                putErrorAttr(hspImportAtrrToExcelFieldNameMap,list,"url","已经存在");
            }else {
                if (urlMap.containsKey(hospitalInfo.getUrl())){
                    putErrorAttr(hspImportAtrrToExcelFieldNameMap,list,"url","excel中存在重复的");
                }else {
                    urlMap.put(hospitalInfo.getUrl(),hospitalInfo.getUrl());
                }
            }
        }

    }

    private void validateUserName(HospitalImportData hospitalInfo, Map<String, ExcelFieldName> hspImportAtrrToExcelFieldNameMap, List<ErrorHospital.ErrorAttr> list) {

        if (StringUtils.isEmpty(hospitalInfo.getCrmUsername())){
            putErrorAttr(hspImportAtrrToExcelFieldNameMap,list,"crmUsername","必填");
        }else {
            if (userService.getUserBySystemType(hospitalInfo.getCrmUsername(), SystemTypeEnum.CRM_LOGIN.getCode()) != null){
                putErrorAttr(hspImportAtrrToExcelFieldNameMap,list,"crmUsername","已经存在");
            }else {
                if (userNameMap.containsKey(hospitalInfo.getCrmUsername())){
                    putErrorAttr(hspImportAtrrToExcelFieldNameMap,list,"crmUsername","excel中存在重复的");
                }else {
                    userNameMap.put(hospitalInfo.getCrmUsername(),hospitalInfo.getCrmUsername());
                }
            }
        }

    }

    private void validateOuterOrgCodeMap(HospitalImportData hospitalInfo, Map<String, ExcelFieldName> hspImportAtrrToExcelFieldNameMap, List<ErrorHospital.ErrorAttr> list) {
        //数据库存在
        if (!existOuterOrgCodeMap.containsKey(hospitalInfo.getOuterOrgCode())){
            if (outerOrgCodeMap.containsKey(hospitalInfo.getOuterOrgCode())){
                //excel 中有
                putErrorAttr(hspImportAtrrToExcelFieldNameMap,list,"outerOrgCode","excel中存在重复重复");
            }else {
                outerOrgCodeMap.put(hospitalInfo.getOuterOrgCode(),hospitalInfo.getOuterOrgCode());
            }
        }else {
            putErrorAttr(hspImportAtrrToExcelFieldNameMap,list,"outerOrgCode","该品牌已经存在相同的外部编码");
        }
    }

    private void putErrorAttr(Map<String, ExcelFieldName> hspImportAtrrToExcelFieldNameMap, List<ErrorHospital.ErrorAttr> list, String fieldName,String errorTip) {
        ErrorHospital.ErrorAttr errorAttr = new ErrorHospital.ErrorAttr();
        ExcelFieldName excelFieldName = hspImportAtrrToExcelFieldNameMap.get(fieldName);
        errorAttr.setName(excelFieldName.fieldName());
        errorAttr.setErrorDesc(errorTip);
        list.add(errorAttr);
    }

    private void putErrorAttr(Map<String, ExcelFieldName> hspImportAtrrToExcelFieldNameMap, List<ErrorHospital.ErrorAttr> list, String fieldName) {
        ErrorHospital.ErrorAttr errorAttr = new ErrorHospital.ErrorAttr();
        ExcelFieldName excelFieldName = hspImportAtrrToExcelFieldNameMap.get(fieldName);
        errorAttr.setName(excelFieldName.fieldName());
        errorAttr.setErrorDesc(excelFieldName.errorTip());
        list.add(errorAttr);
    }

}
