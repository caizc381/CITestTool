package com.mytijian.admin.shop.service.impl;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.account.enums.GenderEnum;
import com.mytijian.admin.shop.model.*;
import com.mytijian.admin.shop.validator.HospitalFileDataValidator;
import com.mytijian.cache.RedisCacheClient;
import com.mytijian.cache.annotation.RedisClient;
import com.mytijian.organization.model.OrganizationManager;
import com.mytijian.pool.ThreadPoolManager;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Address;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalPeriodSetting;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mytijian.admin.shop.param.InitDataReq;
import com.mytijian.admin.shop.resolver.HospitalFileResolver;
import com.mytijian.admin.shop.service.HospitalImportService;
import com.mytijian.admin.shop.service.HospitalInitDataService;
import com.mytijian.organization.dto.OrganizationManagerDto;
import com.mytijian.resource.exceptions.HospitalException;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.site.model.Site;
import com.mytijian.site.model.SiteTemplate;
import org.springframework.util.CollectionUtils;


/**
 * 类HospitalImportServiceImpl.java的实现描述：医院导入
 *
 * @author ljx 2018年1月31日 上午10:36:16
 */
@Service
public class HospitalImportServiceImpl implements HospitalImportService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalImportServiceImpl.class);

    @Autowired
    private HospitalFileResolver hospitalFileResolver;

    @Autowired
    private HospitalInitDataService hospitalInitDataService;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private HospitalFileDataValidator hospitalFileDataValidator;

    private ExecutorService executorService = ThreadPoolManager.newFixedThreadPool(15, 100);


    @RedisClient(nameSpace = "org_import")
    private RedisCacheClient<HospitalImportResult> redisClient;


    @Override
    public HospitalImportResult hospitalImport(File hsplFile, Integer brandId, String type) {
        //文件解析
        List<HospitalImportData> hospitalImportDatas = hospitalFileResolver.hospitalFileResolve(hsplFile);
        //数据校验
        HospitalDataValidateResult hospitalDataValidateResult = hospitalFileDataValidator.validateHospitalFileData(hospitalImportDatas,brandId);
        HospitalImportResult hospitalImportResult = new HospitalImportResult();
        if (hospitalDataValidateResult.isHaveError()) {
            hospitalImportResult.setIsSuccess(false);
            hospitalImportResult.setErrorHospitals(hospitalDataValidateResult.getErrorHospitalInfos());
            return hospitalImportResult;
        }

        String serialId = UUID.randomUUID().toString();
        hospitalImportResult.setTotalHospital(hospitalImportDatas.size());
        redisClient.put(serialId,hospitalImportResult);

        ExecutorService executorService = ThreadPoolManager.newSingleThreadExecutor();
        Runnable runnable = () -> addOrganization(brandId, type, hospitalImportDatas, serialId);
        executorService.submit(runnable);


        hospitalImportResult.setIsSuccess(true);
        hospitalImportResult.setImportSerial(serialId);

        return hospitalImportResult;
    }

    private void addOrganization(Integer brandId, String type, List<HospitalImportData> hospitalImportDatas, String serialId) {
        for (HospitalImportData hspImportData : hospitalImportDatas) {


            Runnable runnable = () -> {

            //构建初始化数据
            InitDataReq initDataReq = buildOrganizationParam(hspImportData, brandId, type);
            OrganizationManagerDto orgManager = hospitalInitDataService.initData(initDataReq);


            //添加机构
            try {
                Integer organizationId = hospitalService.addOrganization(orgManager);
                logger.info("添加机构成功，organizationId={}",organizationId);
                addSuccess(serialId,organizationId);
            } catch (Exception ex) {
                logger.warn("新建机构失败，机构名称{}", orgManager.getHospital().getName(), ex);
                ErrorHospital errorHospital = new ErrorHospital();
                errorHospital.setOrgName(orgManager.getHospital().getName());
                errorHospital.setExDescription(ex.getMessage());
                addError(serialId,errorHospital);
            }

            };

            executorService.submit(runnable);
        }

    }


    private synchronized void addSuccess(String serialId,Integer hospitalId){
        HospitalImportResult hospitalImportResult = redisClient.get(serialId);
        hospitalImportResult.setSuccessHospitalNumber(hospitalImportResult.getSuccessHospitalNumber()+1);
        List<Integer> successHospitalIds = hospitalImportResult.getSuccessHospitalIds();
        if (CollectionUtils.isEmpty(successHospitalIds)){
            hospitalImportResult.setSuccessHospitalIds(Lists.newArrayList(hospitalId));
        }else {
            successHospitalIds.add(hospitalId);
            hospitalImportResult.setSuccessHospitalIds(successHospitalIds);
        }

        redisClient.put(serialId,hospitalImportResult);

    }

    private synchronized void addError(String serialId,ErrorHospital errorHospital){
        HospitalImportResult hospitalImportResult = redisClient.get(serialId);
        List<ErrorHospital> errorHospitals = hospitalImportResult.getErrorHospitals();
        if (CollectionUtils.isEmpty(errorHospitals)){
            hospitalImportResult.setErrorHospitals(Lists.newArrayList(errorHospital));
        }else {
            errorHospitals.add(errorHospital);
            hospitalImportResult.setErrorHospitals(errorHospitals);
        }
        redisClient.put(serialId,hospitalImportResult);
    }

    @Override
    public  HospitalImportResult getHospitalImportResult(String uuid){
        HospitalImportResult hospitalImportResult = redisClient.get(uuid);
        if (hospitalImportResult != null){
            int errorSize = 0;
            if (!CollectionUtils.isEmpty(hospitalImportResult.getErrorHospitals())){
                errorSize = hospitalImportResult.getErrorHospitals().size();
            }
            int successSize = 0;
            if (!CollectionUtils.isEmpty(hospitalImportResult.getSuccessHospitalIds())){
                successSize = hospitalImportResult.getSuccessHospitalIds().size();
            }
            if (hospitalImportResult.getTotalHospital() == errorSize+successSize){
                hospitalImportResult.setIsSuccess(true);
                redisClient.put(uuid,hospitalImportResult);
                return  hospitalImportResult;
            }
        }
        return null;
    }

    private InitDataReq buildOrganizationParam(HospitalImportData hspImportData,Integer brandId,String type) {
        InitDataReq initDataReq = new InitDataReq();
        // 初始化医院数据
        initDataReq.setHospital(buildHospital(hspImportData,brandId,type));
        //初始化医院设置
        initDataReq.setSettings(buildHospitalSettings(hspImportData));
        //初始化地址信息
        initDataReq.setAddress(assemblerAddress(hspImportData));

        // TODO: 2018/2/1 check
        initDataReq.setOpsManagerKeys("");
        // 站点
        Site site = new Site();
        site.setUrl(hspImportData.getUrl());
        site.setMobileTemplateId(2);
        initDataReq.setSite(site);
        SiteTemplate siteTemplate = new SiteTemplate();
        siteTemplate.setDefaultCssId(2);
        initDataReq.setSiteTemplate(siteTemplate);
        // 检查业务联系人
//		initDataReq.setMediatorInfo(organization.getMediatorInfo());

        // 时段
        initDataReq.setPeriodSettingList(assembleHospitalPeriodSetting(hspImportData));

        Map<String, Integer> limitNumMap = Maps.newHashMap();
        limitNumMap.put(hspImportData.getHospitalPeriod(),Integer.valueOf(hspImportData.getLimitNum()));
        initDataReq.setLimitNumMap(limitNumMap);
        // 设置客户经理
        initDataReq.setManager(buildOrganizationManager(hspImportData));
        return initDataReq;
    }

    private List<HospitalPeriodSetting> assembleHospitalPeriodSetting(HospitalImportData hspImportData) {
        HospitalPeriodSetting hospitalPeriodSetting = new HospitalPeriodSetting();
        hospitalPeriodSetting.setName(substring(hspImportData.getHospitalPeriod(),15));
        hospitalPeriodSetting.setInternalUsePeriod(false);
        return Collections.singletonList(hospitalPeriodSetting);
    }

    private OrganizationManager buildOrganizationManager(HospitalImportData hospitalImportData) {

        OrganizationManager organizationManager = new OrganizationManager();
        organizationManager.setManagerName(substring(hospitalImportData.getManagerName(),10));
        organizationManager.setGender("男".equals(hospitalImportData.getGender()) ? GenderEnum.MALE.getCode() : GenderEnum.FEMALE.getCode());
        organizationManager.setMobile(hospitalImportData.getMobile());
        organizationManager.setUsername(hospitalImportData.getCrmUsername());

        return organizationManager;
    }

    private Address assemblerAddress(HospitalImportData hospitalImportData) {

        Address address = new Address();

        String province = hospitalImportData.getProvince();
        String city = hospitalImportData.getCity();
        String district = hospitalImportData.getDistrict();

        List<Address> allAddress = addressService.getAllAddress();

        Map<String, Integer> addressMap = allAddress.stream().filter(x -> x.getDistrict() != null).collect(Collectors.toMap(x -> x.getProvince() + x.getCity() + x.getDistrict(), Address::getId));

        Integer addressId = addressMap.get(province + city + district);
        address.setId(addressId);
        address.setAddress(substring(hospitalImportData.getAddress(),50));
        address.setLatitude(hospitalImportData.getLatitude());
        address.setLongitude(hospitalImportData.getLongitude());

        return address;
    }

    private HospitalSettings buildHospitalSettings(HospitalImportData hospitalImportData) {


        HospitalSettings hospitalSettings = new HospitalSettings();
        hospitalSettings.setAcceptOfflinePay(false);
        hospitalSettings.setAccountPay(true);
        hospitalSettings.setAliPay(true);

        hospitalSettings.setAllowAdjustPrice(true);
        //分
        hospitalSettings.setCalculatorService("3");
        //无体检日期导出
        hospitalSettings.setExportWithNoExamDate(true);
        //智能推荐
        hospitalSettings.setIsSmartRecommend(true);
        //是否需要确认现场付款
        hospitalSettings.setNeedLocalPay(true);
        //是否开启团检报告
        hospitalSettings.setOpenGroupExamReport(true);
        //运营经理备注
//        hospitalSettings.setOpsRemark();
        //提前导出日期
        hospitalSettings.setPreviousBookDays(2);
        hospitalSettings.setPreviousBookTime("18:00");
        hospitalSettings.setPreviousExportDays(2);
        hospitalSettings.setSameDayOrderMaximum(1);
        //技术支持电话 客服电话
        hospitalSettings.setServiceTel("400-0185-800");
        hospitalSettings.setTechnicalTel("400-0185-800");

        //结算周期
        hospitalSettings.setSettleCycle(10);
        hospitalSettings.setWeiXinPay(true);


        //年检查量
        hospitalSettings.setBusinessAmount(hospitalImportData.getBusinessAmount()!=null?Integer.valueOf(hospitalImportData.getBusinessAmount()):0);
        //抽血时间
        hospitalSettings.setDrawBloodStart(hospitalImportData.getDrawBloodStart());
        hospitalSettings.setDrawBloodEnd(hospitalImportData.getDrawBloodEnd());
        //体检时间
        hospitalSettings.setExamStartTime(hospitalImportData.getExamStartTime());
        hospitalSettings.setExamEndTime(hospitalImportData.getExamEndTime());
        //团建电话
        hospitalSettings.setGroupExamTel(hospitalImportData.getGroupExamTel());
        //isBreakfast
        hospitalSettings.setIsBreakfast("提供".equals(hospitalImportData.getIsBreakfast()));
        //workdDays

        String workDays = getWorkDays(hospitalImportData);
        hospitalSettings.setWorkDay(workDays);


        return hospitalSettings;

    }

    private String getWorkDays(HospitalImportData hospitalImportData) {
        List<String> workDays = Lists.newArrayList();
        if ("是".equals(hospitalImportData.getMonday())) {
            workDays.add("2");
        }
        if ("是".equals(hospitalImportData.getTuesday())) {
            workDays.add("3");
        }
        if ("是".equals(hospitalImportData.getWednesday())) {
            workDays.add("4");
        }
        if ("是".equals(hospitalImportData.getThursday())) {
            workDays.add("5");
        }
        if ("是".equals(hospitalImportData.getFriday())) {
            workDays.add("6");
        }
        if ("是".equals(hospitalImportData.getSaturday())) {
            workDays.add("7");
        }
        if ("是".equals(hospitalImportData.getSunday())) {
            workDays.add("1");
        }

        return workDays.stream().collect(Collectors.joining(","));
    }


    private Hospital buildHospital(HospitalImportData hospitalImportData,Integer brandId,String type) {

        Hospital hospital = new Hospital();

        //简介
        hospital.setBriefIntro(substring(hospitalImportData.getBriefIntro(),5000));
        //详情
        hospital.setDetailIntro(substring(hospitalImportData.getDetailIntro(),5000));
        //体检须知
        hospital.setExamNotice(substring(hospitalImportData.getExamNotice(),5000));
        //关键词
        hospital.setKeywords(substring(hospitalImportData.getKeywords(),50));
        //等级
        hospital.setLevel(hospitalImportData.getLevel());
        //名字
        hospital.setName(substring(hospitalImportData.getName(),30));
        //电话
        hospital.setPhone(hospitalImportData.getPhone());
        //交通信息
        hospital.setTrafficInfo(substring(hospitalImportData.getTrafficInfo(),500));
        //外部id
        hospital.setBrandId(brandId);
        hospital.setOuterOrgCode(hospitalImportData.getOuterOrgCode());

        //默认的========
        //机构类型
        hospital.setOrganizationType(OrganizationTypeEnum.HOSPITAL.getCode());
        hospital.setType(type);

        return hospital;
    }


    private String substring(String str, Integer length){
        if (str!= null && str.length()>length){
            str = str.substring(0,length);
        }
        return str;
    }


}
