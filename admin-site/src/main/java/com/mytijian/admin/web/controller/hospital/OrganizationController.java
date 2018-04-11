package com.mytijian.admin.web.controller.hospital;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.account.enums.AccountStatusEnum;
import com.mytijian.account.enums.SystemTypeEnum;
import com.mytijian.account.exceptions.AccountException;
import com.mytijian.account.model.Account;
import com.mytijian.account.model.AccountInfo;
import com.mytijian.account.model.User;
import com.mytijian.account.service.AccountInfoService;
import com.mytijian.account.service.AccountService;
import com.mytijian.account.service.UserService;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.api.rbac.service.EmployeeService;
import com.mytijian.admin.shop.param.InitDataReq;
import com.mytijian.admin.shop.service.HospitalInitDataService;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.admin.web.vo.hospital.BasicSettingsVO;
import com.mytijian.admin.web.vo.hospital.CooperationSettingsVO;
import com.mytijian.admin.web.vo.hospital.ExamitemUpdateResult;
import com.mytijian.admin.web.vo.hospital.FunctionSettingsVO;
import com.mytijian.admin.web.vo.hospital.HospitalPeriodSettingVO;
import com.mytijian.admin.web.vo.hospital.HospitalPrivateQuery;
import com.mytijian.admin.web.vo.hospital.HospitalSignVO;
import com.mytijian.admin.web.vo.hospital.HospitalSiteInfoVO;
import com.mytijian.admin.web.vo.hospital.HospitalSiteVO;
import com.mytijian.admin.web.vo.hospital.OrgQueryParam;
import com.mytijian.admin.web.vo.hospital.OrganizationVo;
import com.mytijian.admin.web.vo.rbac.ManagerVo;
import com.mytijian.cache.RedisCacheClient;
import com.mytijian.cache.annotation.RedisClient;
import com.mytijian.calculate.CalculatorServiceEnum;
import com.mytijian.company.service.SyncCrmHisCompanyService;
import com.mytijian.gotone.api.SmsTemplateService;
import com.mytijian.gotone.api.model.SmsTemplateReq;
import com.mytijian.gotone.api.model.SmsTemplateUpdateBatchResp;
import com.mytijian.gotone.api.model.SmsTemplateUpdatePriorityReq;
import com.mytijian.gotone.api.model.beans.SmsTemplate;
import com.mytijian.gotone.api.model.enums.InitTemplateEnum;
import com.mytijian.mediator.report.vo.AreaVo;
import com.mytijian.mediator.report.vo.CityVo;
import com.mytijian.mediator.report.vo.DistrictVo;
import com.mytijian.mediator.report.vo.ProvinceVo;
import com.mytijian.offer.examitem.constant.enums.UpdateItemStatusEnum;
import com.mytijian.offer.meal.service.MealManageService;
import com.mytijian.organization.dto.OrganizationManagerDto;
import com.mytijian.organization.enums.OrganizationMediatorTypeEnum;
import com.mytijian.organization.enums.SignEnum;
import com.mytijian.organization.model.OrganizationManager;
import com.mytijian.organization.model.OrganizationMediatorInfo;
import com.mytijian.organization.model.Sign;
import com.mytijian.organization.param.OrganizationQuery;
import com.mytijian.organization.service.OrganizationMediatorInfoService;
import com.mytijian.organization.service.SignService;
import com.mytijian.pool.ThreadPoolManager;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.exceptions.HospitalException;
import com.mytijian.resource.model.Address;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalInitialization;
import com.mytijian.resource.model.HospitalPeriodSetting;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.service.AddressService;
import com.mytijian.resource.service.HospitalInitializationService;
import com.mytijian.resource.service.HospitalPeriodSettingService;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.resource.service.OrganizationSettingsService;
import com.mytijian.site.enums.ResourceTypeEnum;
import com.mytijian.site.enums.TemplateTypeEnum;
import com.mytijian.site.exception.SiteException;
import com.mytijian.site.model.Site;
import com.mytijian.site.model.SiteResource;
import com.mytijian.site.model.SiteSubWebCss;
import com.mytijian.site.model.SiteTemplate;
import com.mytijian.site.param.SiteQuery;
import com.mytijian.site.service.ImageService;
import com.mytijian.site.service.SiteResourceService;
import com.mytijian.site.service.SiteService;
import com.mytijian.site.service.SiteSubWebCssService;
import com.mytijian.site.service.SiteTemplateService;
import com.mytijian.survey.dto.HospitalSurveyRelationDTO;
import com.mytijian.survey.dto.SurveyDTO;
import com.mytijian.survey.service.SurveyService;
import com.mytijian.util.AssertUtil;
import com.mytijian.wx.model.WxConfig;
import com.mytijian.wx.service.WxConfigService;


@Controller
public class OrganizationController {
	private final static Logger logger = LoggerFactory.getLogger(OrganizationController.class);
	
	@Resource(name = "userService")
	private UserService userService;
	
	@Resource(name = "accountService")
	private AccountService accountService;
	
	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	@Resource(name = "siteService")
	private SiteService siteService;

	@Resource(name = "siteTemplateService")
	private SiteTemplateService siteTemplateService;
	
	@Resource(name = "siteSubWebCssService")
	private SiteSubWebCssService siteSubWebCssService;
	
	@Resource(name = "addressService")
	private AddressService addressService;
	
	@Resource(name = "employeeService")
	private EmployeeService employeeService;
	
	@Resource(name = "organizationMediatorInfoService")
	private OrganizationMediatorInfoService organizationMediatorInfoService;
	
	@Resource(name = "accountInfoService")
	private AccountInfoService accountInfoService;
	
	@Resource(name = "organizationSettingsService")
	private OrganizationSettingsService organizationSettingsService;
	
	@Resource(name = "hospitalPeriodSettingService")
	private HospitalPeriodSettingService hospitalPeriodSettingService;

	@Resource(name = "siteResourceService")
	private SiteResourceService siteResourceService;

	@Resource(name = "signService")
	private SignService signService;
	

	@Resource(name = "imageService")
	private ImageService imageService;
	
	@Resource(name = "wxConfigService")
	private WxConfigService wxConfigService;
	
    @Value("${temp.folder}")
    private String tempFolder;

	@Resource(name = "smsTemplateService")
	private SmsTemplateService smsTemplateService;
	
	@Resource(name = "surveyService")
	private SurveyService surveyService;
	
	@Resource(name = "mealManageService")
    private MealManageService mealManageService;
	
	@Resource(name = "syncCrmHisCompanyService")
	private SyncCrmHisCompanyService syncCrmHisCompanyService;
	
	@Autowired
	private HospitalInitDataService hospitalInitDataService;

	@Autowired
	private HospitalInitializationService hospitalInitializationService;

	@RedisClient(nameSpace = "offer:examitem:update", timeout = 60 * 60 * 12)   
    private RedisCacheClient<ExamitemUpdateResult> examitemUpdateClient;
	
	private final String TEMPLATE_IMAGE = "http://mytijian-img.oss-cn-hangzhou.aliyuncs.com/template/mobile_template.png";
	
	private final String MOBILE_URL = "www.mytijian.com/m/";
	
	private ExecutorService executorService = ThreadPoolManager.newFixedThreadPool(10, 100);
	/**
	 * 验证crm账户重复性
	 * 
	 * @param userName
	 * @throws AccountException
	 */
	@RequestMapping(value = "/validateUserName", method = RequestMethod.GET)
	@ResponseBody
	public int validateUserName(@RequestParam("userName") String userName,
			@RequestParam(value = "userId", required = false) Integer userId,
			@RequestParam("organizationType") Integer organizationType) throws Exception {

		Integer sysTem;
		if (OrganizationTypeEnum.HOSPITAL.getCode().equals(organizationType)) {
			sysTem = SystemTypeEnum.CRM_LOGIN.getCode();
		} else if (OrganizationTypeEnum.CHANNEL.getCode().equals(organizationType)) {
			sysTem = SystemTypeEnum.CHANNEL_LOGIN.getCode();
		} else {
			throw new AccountException(AccountException.SYSTEM_LOGIN_TYPE_EMPTY, "站点类型不能为空");
		}

		User user = userService.getUserBySystemType(userName, sysTem);
		if(userId == null && user != null){
			throw new Exception("管理员用户名已经存在！");//新增账户时，账户重复
		}
		if(userId != null && user!= null && !user.getId().equals(userId)){
			throw new Exception("管理员用户名已经存在！");//更新信息时，账户重复
		}
		return 1;
	}
	
	/**
	 * 添加机构
	 * 
	 * @param orgManagerVo
	 * @throws Exception
	 */
	@RequestMapping(value = "/organization", method = RequestMethod.POST)
	@ResponseBody
	public Integer addOrganization(@RequestBody OrganizationVo organization) throws Exception {
		validateUserName(organization.getManager().getUsername(), null, SystemTypeEnum.CRM_LOGIN.getCode());
		validateUrl(organization.getSiteVo().getUrl());
		OrganizationManagerDto organMana = buildOrganizationParam(organization);
		return hospitalService.addOrganization(organMana);
	}

	private OrganizationManagerDto buildOrganizationParam(OrganizationVo organization) {
		InitDataReq initDataReq = new InitDataReq();
		// 初始化医院数据
		initDataReq.setHospital(organization.getHospital());
		initDataReq.setSettings(organization.getSettings());
		initDataReq.setAddress(organization.getAddress());
		initDataReq.setOpsManagerKeys(getOpsManagerKeys(organization.getHospital().getOpsManagerId()));
		// 站点

		Site site = new Site();
		site.setUrl(organization.getSiteVo().getUrl());
		site.setMobileTemplateId(organization.getSiteVo().getMobileTemplateId());
		initDataReq.setSite(site);
		SiteTemplate siteTemplate = new SiteTemplate();
		siteTemplate.setDefaultCssId(organization.getSiteVo().getCssId());
		initDataReq.setSiteTemplate(siteTemplate);
		// 检查业务联系人
		initDataReq.setMediatorInfo(organization.getMediatorInfo());
		//时段
		Map<String, Integer> limitNumMap = Maps.newHashMap();
		List<HospitalPeriodSetting> periodSettingList = getPeriodSettingList(organization, limitNumMap);
		initDataReq.setPeriodSettingList(periodSettingList);
		initDataReq.setLimitNumMap(limitNumMap);
		// 设置客户经理
		initDataReq.setManager(organization.getManager());
		return hospitalInitDataService.initData(initDataReq);
	}

	private List<HospitalPeriodSetting> getPeriodSettingList(OrganizationVo organization,
															 Map<String, Integer> limitNumMap) {
		List<HospitalPeriodSettingVO> periodSettingVOList = organization.getPeriodSettingList();
		List<HospitalPeriodSetting> periodSettingList = Lists.newArrayList();
		for(HospitalPeriodSettingVO periodSettingVO : periodSettingVOList){
			HospitalPeriodSetting perdiod = new HospitalPeriodSetting();
			BeanUtils.copyProperties(periodSettingVO, perdiod, "limitNum");
			periodSettingList.add(perdiod);
			if(limitNumMap != null){
				limitNumMap.put(periodSettingVO.getName(), periodSettingVO.getLimitNum());
			}
		}
		return periodSettingList;
	}


	private String getOpsManagerKeys(Integer opsManagerId) {
		if(opsManagerId != null){
			List<Employee> employeeList = employeeService.listEmployeesByIds(Arrays.asList(opsManagerId));
			Employee employee = employeeList.get(0);
			return employee.getEmployeeName() + "," + employee.getPinYin();
		}
		return null;
	}

	/**
	 * 验证url
	 * @param url
	 * @throws SiteException
	 */
	@RequestMapping(value = "/validateUrl", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void validateUrl(String url) throws SiteException {
		if (AssertUtil.isEmpty(url)) {
			throw new SiteException(SiteException.URL_NULL, "网址为空");
		}
		boolean isExist = siteService.checkSiteUrl(null, url);

		if (isExist) {
			throw new SiteException(SiteException.URL_EXIST, "网址已经存在" + url);
		}
	}
	
	/**
	 * 获取站点模板和css列表
	 * 
	 * @param hospitalId
	 * @param orgType
	 *            机构类型1：hospital，2：channel
	 * @return
	 */
	@RequestMapping(value = "/siteTemplateAndCss", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> siteResource(@RequestParam("orgType") Integer orgType) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<SiteTemplate> mobileTemList = siteTemplateService
				.getGlobalSiteTemplateListByType(TemplateTypeEnum.MOBILE.getCode(), orgType);
		List<SiteSubWebCss> cssList = siteSubWebCssService
				.getSiteSubWebCssListByType(TemplateTypeEnum.MOBILE.getCode());
		resultMap.put("mobileTemplateList", mobileTemList);
		resultMap.put("cssList", cssList);
		return resultMap;
	}
	
	/**
	 * 获取手机模板图片
	 * @return
	 */
	@RequestMapping(value = "/templageImage", method = RequestMethod.GET)
	@ResponseBody
	public String templateImage(){
		return TEMPLATE_IMAGE;
	}
	
	@RequestMapping(value="/areaAddress", method = RequestMethod.GET)
	@ResponseBody
	public AreaVo areaAddress(){
		List<Address> addressList = addressService.getAllAddress();
		List<Address> noCityProvinceList = Lists.newArrayList();
		//地址根据省、市  分组
		Map<String, Map<String, List<Address>>> addressMap = addressList.stream()
				.filter(address -> {
					if(AssertUtil.isEmpty(address.getCity())){
						noCityProvinceList.add(address);
					}
					return AssertUtil.isNotEmpty(address.getCity());
				})
				.collect(Collectors.groupingBy(Address::getProvince, Collectors.groupingBy(Address::getCity)));
		AreaVo area = new AreaVo();
		List<ProvinceVo> provinceList = Lists.newArrayList();
		addressMap.forEach((province, provinceMap) -> {
			//先获取省的名称信息
			ProvinceVo provinceVo = new ProvinceVo();
			provinceVo.setLabel(province);
			//增加省的children信息
			List<CityVo> cityList = Lists.newArrayList();
			provinceMap.forEach((city, districtList) -> {
				//获取市的名称信息
				CityVo cityVo = new CityVo();
				cityVo.setLabel(city);
				List<DistrictVo> districtVoList = Lists.newArrayList();
				for(int i=0; i < districtList.size(); i++){
					Address address = districtList.get(i);
					if(AssertUtil.isEmpty(address.getDistrict())){
						cityVo.setValue(Long.valueOf(address.getId()));
						provinceVo.setValue(Long.valueOf(address.getParentId()));
					} else {
						if(cityVo.getValue() == null){
							cityVo.setValue(Long.valueOf(address.getParentId()));
						}
						DistrictVo districtVo = new DistrictVo();
						districtVo.setLabel(address.getDistrict());
						districtVo.setValue(Long.valueOf(address.getId()));
						districtVoList.add(districtVo);
					}
				}
				cityVo.setChildren(districtVoList);
				cityList.add(cityVo);
			});
			provinceVo.setChildren(cityList);
			provinceList.add(provinceVo);
		});
		if(AssertUtil.isNotEmpty(noCityProvinceList)){
			noCityProvinceList.forEach(noCity -> {
				if(addressMap.get(noCity.getProvince()) == null){
					ProvinceVo provinceVo = new ProvinceVo();
					provinceVo.setLabel(noCity.getProvince());
					provinceVo.setValue(Long.valueOf(noCity.getId()));
					provinceVo.setChildren(Lists.newArrayList());
					provinceList.add(provinceVo);
				}
			});
		}
		provinceList.sort(Comparator.comparing(ProvinceVo::getValue));
		area.setProvinces(provinceList);
		return area;
	}
	
	/**
	 * 获取体检中心列表
	 * @param keywords
	 * @return
	 */
	@RequestMapping(value = "/hospitalList", method = RequestMethod.GET)
	@ResponseBody
	public List<Hospital> getHospitalList(String keywords){
		List<Hospital> hospitalList = hospitalService.selectAllHospital();
		return hospitalList;
	}
	
	/**
	 * 查询基本信息列表
	 * @return
	 */
	@RequestMapping(value = "/listHospitalBaseInfo", method = RequestMethod.POST)
	@ResponseBody
	public PageView<Map<String, Object>> getListHospitalBasicInfo(@RequestBody OrgQueryParam orgQueryParam){
		OrganizationQuery query = orgQueryParam.getOrganizationQuery();
		HospitalPrivateQuery privateQuery = orgQueryParam.getPrivateQuery();
		query.setOrderById(privateQuery.getOrderById());
		query.setOrderbyBusinessAmount(privateQuery.getOrderByBusinessAmount());
		query.setPage(privateQuery.getPage());
		query.setOrganizationType(OrganizationTypeEnum.HOSPITAL.getCode());
		PageView<Hospital> hospitalPageView = hospitalService.listHospitalInfo(query);
		PageView<Map<String, Object>> result = new PageView<>();
		List<Map<String, Object>> reList = Lists.newArrayList();
		result.setRecords(reList);
		result.setPage(hospitalPageView.getPage());
		for(Hospital hos : hospitalPageView.getRecords()){
			Map<String, Object> map = Maps.newHashMap();
			map.put("hospital", hos);
			//体检中心运营经理
			Employee employee = employeeService.getOperationByHospitalId(hos.getId());
			map.put("opsManager", employee);
			//体检中心管理员
			ManagerVo managerVo = getHospitalManager(hos);
			map.put("manager", managerVo);
			//体检中心业务员
			OrganizationMediatorInfo mediatorInfo = organizationMediatorInfoService
					.getOrganizationMediatorInfoById(hos.getId(), 
					OrganizationMediatorTypeEnum.BUSINESS_CONTACTS.getCode());
			map.put("organizationMediatorInfo", mediatorInfo);
			reList.add(map);
		}
		return result;
	}

	private ManagerVo getHospitalManager(Hospital hos) {
		Account account = accountService.getAccountById(hos.getDefaultManagerId());
		ManagerVo managerVo = new ManagerVo();
		if (account != null) {
			BeanUtils.copyProperties(account, managerVo);
			String userName = userService.getOneUsername(account.getId());
			if(AssertUtil.isNotEmpty(userName)){
				managerVo.setUsername(userName);
			}
		}
		return managerVo;
	}

	/**
	 * 更新体检中心基本信息
	 * @param hospital
	 */
	@RequestMapping(value = "/organizationBaseInfo", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateHospitalBasicInfo(@RequestBody Hospital hospital){
		Employee employee = SessionUtil.getEmployee();
		validateOrgOuterCode(hospital);
		if (hospital.getBrandId() ==null){
			hospital.setBrandId(-1);
		}
		hospitalService.updateHospitalInfo(hospital);
		logger.info("event={},hospitalId={},operatorId={},operatorName={},time={},reqJson={}","更新体检中心基本信息",
				hospital.getId(), (employee != null ? employee.getId() : ""),
				(employee != null ? employee.getLoginName() : ""),
				LocalDateTime.now(), JSON.toJSONString(hospital));
	}


	private void validateOrgOuterCode( Hospital hospital){

		if (!StringUtils.isEmpty(hospital.getOuterOrgCode()) && hospital.getBrandId() != null){
			List<Integer> hospitalIdsByBrandId = hospitalService.listHospitalIdsByBrandId(hospital.getBrandId());
			List<Hospital> hospitalsByIds = hospitalService.getHospitalsByIds(hospitalIdsByBrandId);
			Map<String,Integer> hashMap = hospitalsByIds.stream().filter(x-> !org.springframework.util.StringUtils.isEmpty(x.getOuterOrgCode() )).collect(Collectors.toMap(Hospital::getOuterOrgCode, Hospital::getId));
			if (hashMap != null && hashMap.get(hospital.getOuterOrgCode()) != null && hashMap.get(hospital.getOuterOrgCode())!= hospital.getId()){
				throw new RuntimeException("该品牌已经存在相同的外部编码");
			}
		}
	}
	
	/**
	 * 更新体检中心的业务联系人，管理员，运营经理信息
	 * @param organizationVo
	 * @throws Exception 
	 */
	@RequestMapping(value = "/businessManager", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateBusinessManager(@RequestBody OrganizationVo organizationVo) throws Exception{
		Employee employee = SessionUtil.getEmployee();
		OrganizationManager manager = organizationVo.getManager();
		//更新账户信息
		updateAccountInfo(manager);
		//更新登录信息
		updateUser(manager);
		//更新业务联系人
		updateMediatorInfo(organizationVo);
		//更新运营经理信息
		Hospital hospital = updateOpsManager(organizationVo);
		//更新设置信息
		updateSettings(organizationVo, hospital);
		logger.info("event={},hospitalId={},operatorId={},operatorName={},time={},reqJson={}",
				"更新体检中心的业务联系人", hospital.getId(),	(employee != null ? employee.getId() : ""),
				(employee != null ? employee.getLoginName() : ""),
				LocalDateTime.now(), JSON.toJSONString(organizationVo));
	}

	private void updateMediatorInfo(OrganizationVo organizationVo) {
		OrganizationMediatorInfo mediatorInfo = organizationVo.getMediatorInfo();
		mediatorInfo.setOrganizationId(organizationVo.getHospital().getId());
		mediatorInfo.setType(OrganizationMediatorTypeEnum.BUSINESS_CONTACTS.getCode());
		if(mediatorInfo != null){
			if(mediatorInfo.getId() != null){
				organizationMediatorInfoService.updateOrganizationMediatorInfo(mediatorInfo);
			} else {
				organizationMediatorInfoService.addOrganizationMediatorInfo(mediatorInfo);
			}
		}
	}

	private void updateSettings(OrganizationVo organizationVo, Hospital hospital) {
		BasicSettingsVO basicSettings = organizationVo.getBasicSettings();
		HospitalSettings settings = new HospitalSettings();
		BeanUtils.copyProperties(basicSettings, settings,"isBreakfast");
		settings.setMobileFieldOrder(null);
		settings.setIsBreakfast(1 == basicSettings.getIsBreakfast());
		settings.setHospitalId(hospital.getId());
		organizationSettingsService.update(settings);
	}

	private Hospital updateOpsManager(OrganizationVo organizationVo) {
		Hospital hospital = organizationVo.getHospital();
		Employee employee = organizationVo.getOpsManager();
		hospital.setOpsManagerId(employee.getId());
		hospital.setOpsManagerKeys(employee.getEmployeeName() + "," + employee.getPinYin());
		hospitalService.updateHospitalInfo(hospital);
		return hospital;
	}

	private void updateUser(OrganizationManager manager) throws Exception {
		validateUserName(manager.getUsername(), manager.getUserId(), OrganizationTypeEnum.HOSPITAL.getCode());
		User user = new User();
		user.setId(manager.getUserId());
		user.setUsername(manager.getUsername());
		userService.updateUser(user);
	}

	private void updateAccountInfo(OrganizationManager manager) {
		Account account = new Account();
		BeanUtils.copyProperties(manager, account, "managerName");
		account.setName(manager.getManagerName());
		account.setStatus(AccountStatusEnum.NORMAL.getCode());
		AccountInfo info = new AccountInfo();
		info.setAccountId(manager.getId());
		info.setGender(manager.getGender());
		accountInfoService.updateAccountWrapper(account, info, null, null);
	}
	
	/**
	 * 更新体检中心基本信息的更多信息
	 * @param hospital
	 */
	@RequestMapping(value = "/hospitalMoreInfo", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateHospitalMoreInfo(@RequestBody OrganizationVo organizationVo){
		Employee employee = SessionUtil.getEmployee();
		hospitalService.updateHospitalInfo(organizationVo.getHospital());
		BasicSettingsVO basicSettings = organizationVo.getBasicSettings();
		HospitalSettings settings = new HospitalSettings();
		settings.setMobileFieldOrder(null);
		BeanUtils.copyProperties(basicSettings, settings,"isBreakfast");
		settings.setIsBreakfast(1 == basicSettings.getIsBreakfast());
		organizationSettingsService.update(settings);
		logger.info("event={},hospitalId={},operatorId={},operatorName={},time={},reqJson={}","更新体检中心基本信息的更多信息",  
				organizationVo.getHospital().getId(), (employee != null ? employee.getId() : ""),
				(employee != null ? employee.getLoginName() : ""),
				LocalDateTime.now(), JSON.toJSONString(organizationVo));
	}
	
	/**
	 * 查询体检中心功能配置页面列表信息
	 * @param orgQueryParam
	 * @return
	 */
	@RequestMapping(value = "/listFunction", method = RequestMethod.POST)
	@ResponseBody
	public PageView<Hospital> getFunSetting(@RequestBody OrgQueryParam orgQueryParam){
		OrganizationQuery query = orgQueryParam.getOrganizationQuery();
		HospitalPrivateQuery privateQuery = orgQueryParam.getPrivateQuery();
		query.setOrderbyBusinessAmount(privateQuery.getOrderByBusinessAmount());
		query.setOrganizationType(OrganizationTypeEnum.HOSPITAL.getCode());
		query.setOrderById(privateQuery.getOrderById());
		query.setOpenSyncCompany(privateQuery.getOpenSyncCompany());
		query.setSyncExamReportAbility(privateQuery.getSyncExamReportAbility());
		query.setOrderByPreviousExportDays(privateQuery.getOrderByPreviousExportDays());
		query.setOrderByPreviousBookDays(privateQuery.getOrderByPreviousBookDays());
		query.setPage(privateQuery.getPage());
		PageView<Hospital> hospitalPageView = hospitalService.listHospitalInfo(query);
		return hospitalPageView;
	}
	
	/**
	 * 基本信息详情页面
	 * @param hospitalId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@RequestMapping(value = "/hospitalDetail")
	@ResponseBody
	public Map<String,Object> hospitalDetail(Integer hospitalId) throws IllegalAccessException, InvocationTargetException{
 		Map<String,Object> detail = new HashMap<String,Object>(); 
		Hospital hospital = hospitalService.getHospitalById(hospitalId);
		HospitalSettings settings = (HospitalSettings) hospital.getSettings();
		hospital.setSettings(null);
		//体检中心信息
		detail.put("hospital", hospital);
		BasicSettingsVO basicSettings = converterToBasicSettingsVO(settings);
		detail.put("basicSettings", basicSettings);
		if(hospital.getAddress() != null && hospital.getAddress().getId() != null){
			Integer[] addressIds = getAddressIds(hospital.getAddress().getId());
			detail.put("addressIds", addressIds);
		}
		//运营经理信息
		Employee employee = employeeService.getOperationByHospitalId(hospitalId);
		detail.put("opsManager", employee != null ? employee : new Employee());
		//体检中心管理员
		Account account = accountService.getHospitalDefaultManager(hospitalId);
		OrganizationManager orgManager=new OrganizationManager();
		if(account != null){
			BeanUtils.copyProperties(account, orgManager);
			orgManager.setManagerName(account.getName());
			AccountInfo info = accountInfoService.getAccountInfo(account.getId());
			if(info != null){
				orgManager.setGender(info.getGender());
			}
			List<User> userList = userService.getUserListByAccountId(account.getId());
			if (AssertUtil.isNotEmpty(userList)) {
				orgManager.setUsername(userList.get(0).getUsername());
				orgManager.setUserId(userList.get(0).getId());
			}
		}
		detail.put("manager", orgManager);
		//业务联系人
		OrganizationMediatorInfo mediatorInfo = organizationMediatorInfoService.getOrganizationMediatorInfoById(hospitalId, 
				OrganizationMediatorTypeEnum.BUSINESS_CONTACTS.getCode());
		detail.put("mediatorInfo", mediatorInfo != null? mediatorInfo : new OrganizationMediatorInfo());
		return detail;
	}

	private BasicSettingsVO converterToBasicSettingsVO(HospitalSettings settings) {
		BasicSettingsVO basicSettings = new BasicSettingsVO();	
		BeanUtils.copyProperties(settings, basicSettings, "isBreakfast");
		Boolean isBreakfast = settings.getIsBreakfast();
		basicSettings.setIsBreakfast((isBreakfast != null && isBreakfast)? 1 : 0);
		return basicSettings;
	}
	
	private Integer[] getAddressIds(int addressId){
		Address address = addressService.getAddressById(addressId);
		if(address != null){
			Integer provinceId = null;
			Integer cityId = null;
			Integer districtId = null;
			if(AssertUtil.isNotEmpty(address.getDistrict())){
				districtId = addressId;
				cityId = address.getParentId();
				Address cityAddress = addressService.getAddressById(cityId);
				provinceId = cityAddress.getParentId();
			} else if(AssertUtil.isNotEmpty(address.getCity())){
				cityId = addressId;
				provinceId = address.getParentId();
			} else {
				provinceId = addressId;
			}
			List<Integer> list = Lists.newArrayList();
			list.add(provinceId);
			if(cityId != null){
				list.add(cityId);
			}
			if(districtId != null){
				list.add(districtId);
			}
			Integer[] result = new Integer[list.size()];
			return list.toArray(result);
		}
		return null;
	}
	
	/**
	 * 功能设置详情
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value = "/funSettingsInfo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> funSettingsInfo(Integer hospitalId){
		Map<String,Object> funSettingsMap = new HashMap<String,Object>();
		funSettingsMap.put("periodSetting",hospitalPeriodSettingService.getHospitalPeriodSetting(hospitalId));
		HospitalSettings setting = hospitalService.getHospitalSettingsById(hospitalId);
		setting.setCalculatorService(
				String.valueOf(CalculatorServiceEnum.getCodeByName(setting.getCalculatorService())));
		FunctionSettingsVO funSettings = converterToFunctionSettingsVO(setting);
		funSettingsMap.put("funSettings", funSettings);
		return funSettingsMap;
	}

	private FunctionSettingsVO converterToFunctionSettingsVO(HospitalSettings setting) {
		FunctionSettingsVO funSettings = new FunctionSettingsVO();
		BeanUtils.copyProperties(setting, funSettings,"allowAdjustPrice",
				"isSmartRecommend","needLocalPay","openGroupExamReport","exportWithNoExamDate" , "supportManualRefund");
		funSettings.setAllowAdjustPrice((setting.getAllowAdjustPrice() != null && setting.getAllowAdjustPrice())?1:0);
		funSettings.setIsSmartRecommend((setting.getIsSmartRecommend() != null && setting.getIsSmartRecommend())?1:0);
		funSettings.setNeedLocalPay((setting.getNeedLocalPay() != null && setting.getNeedLocalPay())?1:0);
		funSettings.setOpenGroupExamReport((setting.getOpenGroupExamReport() != null && setting.getOpenGroupExamReport())?1:0);
		funSettings.setExportWithNoExamDate((setting.getExportWithNoExamDate() != null && setting.getExportWithNoExamDate())?1:0);
		funSettings.setSupportManualRefund((setting.getSupportManualRefund() != null && setting.getSupportManualRefund())?1:0);
		funSettings.setShowNonPlatformExamReport((setting.getShowNonPlatformExamReport() != null && setting.getShowNonPlatformExamReport())?1:0);
		funSettings.setShowExamReportInspectTab((setting.getShowExamReportInspectTab() != null && setting.getShowExamReportInspectTab())?1:0);
		return funSettings;
	}
	
	/**
	 * 更新功能设置
	 * @param settings
	 */
	@RequestMapping(value = "/funSetting")
	@ResponseStatus(value = HttpStatus.OK)
	public void updateFunSetting(@RequestBody FunctionSettingsVO funSettings){
		Employee employee = SessionUtil.getEmployee();
		funSettings.setCalculatorService(CalculatorServiceEnum.getNameByCode(Integer.parseInt(funSettings.getCalculatorService())));
		HospitalSettings settings = converterToHospitalSettings(funSettings);
		hospitalService.updateOrganizationSetting(settings);
		logger.info("event={},hospitalId={},operatorId={},operatorName={},time={},reqJson={}","更新功能设置", 
				settings.getHospitalId(), (employee != null ? employee.getId() : ""),
				(employee != null ? employee.getLoginName() : ""),
				LocalDateTime.now(), JSON.toJSONString(funSettings));
	}

	private HospitalSettings converterToHospitalSettings(FunctionSettingsVO funSettings) {
		HospitalSettings settings = new HospitalSettings();
		settings.setMobileFieldOrder(null);
		BeanUtils.copyProperties(funSettings, settings,"allowAdjustPrice",
				"isSmartRecommend","needLocalPay","openGroupExamReport","exportWithNoExamDate", "supportManualRefund");
		settings.setSupportManualRefund(1 == funSettings.getSupportManualRefund());
		if(settings.getAccountPay() == null){
			settings.setAccountPay(false);
		}
		if(settings.getAliPay() == null){
			settings.setAliPay(false);
		}
		if(settings.getWeiXinPay() == null){
			settings.setWeiXinPay(false);
		}
		if(settings.getAcceptOfflinePay() == null){
			settings.setAcceptOfflinePay(false);
		}
		settings.setAllowAdjustPrice(1 == funSettings.getAllowAdjustPrice());
		settings.setIsSmartRecommend(1 == funSettings.getIsSmartRecommend());
		settings.setNeedLocalPay(1 == funSettings.getNeedLocalPay());
		settings.setOpenGroupExamReport(1 == funSettings.getOpenGroupExamReport());
		settings.setExportWithNoExamDate(1 == funSettings.getExportWithNoExamDate());
		settings.setShowNonPlatformExamReport(1 == funSettings.getShowNonPlatformExamReport());
		settings.setShowExamReportInspectTab(1 == funSettings.getShowExamReportInspectTab());
		return settings;
	}
	
	/**
	 * 更新功能详情
	 * @param organizationVo
	 */
	@RequestMapping(value = "/examTimeSetting")
	@ResponseStatus(value = HttpStatus.OK)
	public void updateExamTimeSetting(@RequestBody OrganizationVo organizationVo){
		List<HospitalPeriodSetting> periodList = getPeriodSettingList(organizationVo, null);
		hospitalService.updateHospitalPeriod(periodList.get(0).getHospitalId(),periodList);
		FunctionSettingsVO funSettings = organizationVo.getFunSettings();
		funSettings.setCalculatorService(CalculatorServiceEnum.getNameByCode
				(Integer.parseInt(funSettings.getCalculatorService())));
		HospitalSettings settings = converterToHospitalSettings(funSettings);
		hospitalService.updateOrganizationSetting(settings);
		Employee employee = SessionUtil.getEmployee();
		logger.info("event={},hospitalId={},operatorId={},operatorName={},time={},reqJson={}","更新体检预约时间设置", 
				settings.getHospitalId(), (employee != null ? employee.getId() : ""),
				(employee != null ? employee.getLoginName() : ""),
				LocalDateTime.now(), JSON.toJSONString(funSettings));
	}
	
	/**
	 * 查询体检中心平台合作列表信息
	 * @param orgQueryParam
	 * @return
	 */
	@RequestMapping(value = "/cooperationInfoList", method = RequestMethod.POST)
	@ResponseBody
	public PageView<Hospital> getCooperationInfoList(@RequestBody OrgQueryParam orgQueryParam){
		OrganizationQuery query = orgQueryParam.getOrganizationQuery();
		query.setOrganizationType(OrganizationTypeEnum.HOSPITAL.getCode());
		HospitalPrivateQuery privateQuery = orgQueryParam.getPrivateQuery();
		query.setIsIndividuationPlatformMeal(privateQuery.getIsIndividuationPlatformMeal());
		query.setIsPlatformSuspense(privateQuery.getIsPlatformSuspense());
		query.setIsPrepayInvoice(privateQuery.getIsPrepayInvoice());
		query.setOrderById(privateQuery.getOrderById());
		query.setOrderByPlatformCompDiscount(privateQuery.getOrderByPlatformCompDiscount());
		query.setOrderByPlatformGuestDiscount(privateQuery.getOrderByPlatformGuestDiscount());
		query.setOrderByGuestOnlineCompDiscount(privateQuery.getOrderByGuestOnlineCompDiscount());
		query.setOrderByHospitalCompDiscount(privateQuery.getOrderByHospitalCompDiscount());
		query.setOrderByGuestOfflineCompDiscount(privateQuery.getOrderByGuestOfflineCompDiscount());
		query.setPage(privateQuery.getPage());
		PageView<Hospital> hospitalPageView = hospitalService.listHospitalInfo(query);
		return hospitalPageView;
	}
	
	/**
	 * 基本信息详情页面
	 * @param hospitalId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@RequestMapping(value = "/cooperationInfo", method=RequestMethod.GET)
	@ResponseBody
	public CooperationSettingsVO cooperationInfo(Integer hospitalId){
		HospitalSettings settings = organizationSettingsService.getHospitalSettingsByHospitalId(hospitalId);
		CooperationSettingsVO settingsVo = new CooperationSettingsVO();
		settingsVo.setHospitalId(settings.getHospitalId());
		Boolean isIndividuationPlatformMeal = settings.getIsIndividuationPlatformMeal();
		settingsVo.setIsIndividuationPlatformMeal((isIndividuationPlatformMeal != null && isIndividuationPlatformMeal)?1:0);
		Boolean isPlatformSuspense = settings.getIsPlatformSuspense();
		settingsVo.setIsPlatformSuspense((isPlatformSuspense != null && isPlatformSuspense)?1:0);
		Boolean isPrepayInvoice = settings.getIsPrepayInvoice();
		settingsVo.setIsPrepayInvoice((isPrepayInvoice != null && isPrepayInvoice)?1:0);
		Boolean isPlatformEmployeeImport = settings.getIsPlatformEmployeeImport();
		settingsVo.setIsPlatformEmployeeImport((isPlatformEmployeeImport != null && isPlatformEmployeeImport)?1:0);
		settingsVo.setLowestConsumption(settings.getLowestConsumption());
		settingsVo.setGuestOfflineCompDiscount(settings.getGuestOfflineCompDiscount());
		settingsVo.setGuestOnlineCompDiscount(settings.getGuestOnlineCompDiscount());
		settingsVo.setHospitalCompDiscount(settings.getHospitalCompDiscount());
		settingsVo.setPlatformCompDiscount(settings.getPlatformCompDiscount());
		settingsVo.setPlatformGuestDiscount(settings.getPlatformGuestDiscount());
		return settingsVo;
	}
	
	/**
	 * 更新平台合作的折扣信息
	 * @param settingsVo
	 */
	@RequestMapping(value = "/updateCooperationInfoDiscount", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateCooperationInfoDiscount(@RequestBody CooperationSettingsVO settingsVo){
		Employee employee = SessionUtil.getEmployee();
		HospitalSettings settings = new HospitalSettings();
		settings.setMobileFieldOrder(null);
		BeanUtils.copyProperties(settingsVo, settings);
		organizationSettingsService.update(settings);
		logger.info("event={},hospitalId={},operatorId={},operatorName={},time={},reqJson={}","更新平台合作的折扣信息",
				settings.getHospitalId(), (employee != null ? employee.getId() : ""),
				(employee != null ? employee.getLoginName() : ""),
				LocalDateTime.now(), JSON.toJSONString(settingsVo));
	}
	
	/**
	 * 更新平台合作的其他信息
	 * @param settingsVo
	 */
	@RequestMapping(value = "/updateCooperationMoreInfo", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateCooperationMoreInfo(@RequestBody CooperationSettingsVO settingsVo){
		HospitalSettings settings = new HospitalSettings();
		settings.setMobileFieldOrder(null);
		settings.setIsIndividuationPlatformMeal(settingsVo.getIsIndividuationPlatformMeal() == 1);
		settings.setIsPlatformSuspense(settingsVo.getIsPlatformSuspense() == 1);
		settings.setIsPrepayInvoice(settingsVo.getIsPrepayInvoice() == 1);
		settings.setIsPlatformEmployeeImport(settingsVo.getIsPlatformEmployeeImport() == 1);
		settings.setLowestConsumption(settingsVo.getLowestConsumption());
		settings.setHospitalId(settingsVo.getHospitalId());
		organizationSettingsService.update(settings);
		Employee employee = SessionUtil.getEmployee();
		logger.info("event={},hospitalId={},operatorId={},operatorName={},time={},reqJson={}",
				"更新平台合作的其他信息", settings.getHospitalId(),(employee != null ? employee.getId() : ""),
				(employee != null ? employee.getLoginName() : ""),
				LocalDateTime.now(), JSON.toJSONString(settingsVo));
	}
	
	/**
	 * 获取站点设置信息列表
	 * @param settingsVo
	 * @return
	 */
	@RequestMapping(value = "/listHospitalSite", method = RequestMethod.POST)
	@ResponseBody
	public PageView<HospitalSiteVO> listHospitalSite(@RequestBody OrgQueryParam orgQueryParam){
		OrganizationQuery query = orgQueryParam.getOrganizationQuery();
		query.setOrganizationType(OrganizationTypeEnum.HOSPITAL.getCode());
		HospitalPrivateQuery privateQuery = orgQueryParam.getPrivateQuery();
		query.setShowInvoice(privateQuery.getShowInvoice());
		query.setOrderById(privateQuery.getOrderById());
		PageView<Hospital> hospitalPageView = hospitalService.listHospitalInfo(query);
		List<Hospital> list = hospitalPageView.getRecords();
		//获取站点信息
		Page page = privateQuery.getPage();
		List<HospitalSiteVO> records = getHospitalSiteRecords(query, privateQuery, list, page);
		return new PageView<>(records, privateQuery.getPage());
	}

	private List<HospitalSiteVO> getHospitalSiteRecords(OrganizationQuery query, HospitalPrivateQuery privateQuery,
			List<Hospital> list, Page page) {
		SiteQuery sq = new SiteQuery();
		List<Integer> hospitalIds = Lists.newArrayList();
		Integer templateId = privateQuery.getSiteTemplateId();
		if(query.getHospitalId() != null){
			hospitalIds.add(query.getHospitalId());
		}
		if(query.getHospitalId() == null && templateId == null && 
				page != null && AssertUtil.isNotEmpty(list)){
			page.setRowCount(list.size());
			list = list.stream().skip(page.getPageSize() * (page.getCurrentPage() - 1))
			.limit(page.getPageSize())
			.filter(hos -> {
				hospitalIds.add(hos.getId());
				return true;
			})
			.collect(Collectors.toList());
		}
		sq.setHospitalIds(hospitalIds);
		sq.setTemplateIds(templateId != null ?Arrays.asList(privateQuery.getSiteTemplateId()) : null);
		List<Site> siteList = siteService.selectSite(sq, true);
		Map<Integer, Site> siteMap = siteList.stream()
				.collect(Collectors.toMap(Site::getHospitalId, val -> val));
		List<HospitalSiteVO> records = Lists.newArrayList();
		List<SiteSubWebCss> cssList = siteSubWebCssService.getSiteSubWebCssList();
		Map<Integer, SiteSubWebCss>	cssMap = cssList.stream()
				.collect(Collectors.toMap(SiteSubWebCss::getId, val -> val));
		List<SiteTemplate> templateList = siteTemplateService
				.getGlobalSiteTemplateListByType(TemplateTypeEnum.MOBILE.getCode(), OrganizationTypeEnum.HOSPITAL.getCode());
		Map<Integer, SiteTemplate>templateMap = templateList.stream()
				.collect(Collectors.toMap(SiteTemplate::getId, val -> val));
		if(query.getHospitalId() == null && templateId == null){
			//查询条件hospitalId和templateId为空的情况下，数据量以hospital的查询结果为准
			list.forEach(hos -> {
				HospitalSiteVO hso = getHospitalSiteVO(siteMap, cssMap, templateMap, hos);
				records.add(hso);
			});
		} else {
			//查询条件hospitalId或者templateId不为空的情况下，取交集
			if(page != null && AssertUtil.isNotEmpty(siteList)){
				page.setRowCount(siteList.size());
				Map<Integer, Hospital> hosMap = list.stream()
						.collect(Collectors.toMap(Hospital::getId, val -> val));
				siteList.stream().skip(page.getPageSize() * (page.getCurrentPage() - 1))
				.limit(page.getPageSize())
				.forEach(site -> {
					Hospital hos = hosMap.get(site.getHospitalId());
					if(hos != null){
						HospitalSiteVO hso = getHospitalSiteVO(siteMap, cssMap, templateMap, hosMap.get(site.getHospitalId()));
						records.add(hso);
					}
				});
			}
		}
		return records;
	}

	private HospitalSiteVO getHospitalSiteVO(Map<Integer, Site> siteMap, Map<Integer, SiteSubWebCss> cssMap,
			Map<Integer, SiteTemplate> templateMap, Hospital hos) {
		HospitalSiteVO hso = new HospitalSiteVO();
		HospitalSettings settings = null;
		if(hos.getSettings() != null){
			settings = (HospitalSettings) hos.getSettings();
			hso.setShowInvoice(settings.getShowInvoice());
		}
		hso.setHospital(hos);
		Site site = siteMap.get(hos.getId());
		if(site != null){
			String color = cssMap.get(site.getTemplate().getDefaultCssId()).getName();
			hso.setColor(color);
			hso.setTemplateId(site.getId());
			hso.setUrl(site.getUrl());
			SiteTemplate template = templateMap.get(site.getMobileTemplateId());
			String templateName = "";
			if(template == null){
				SiteTemplate customTemplate = siteTemplateService.getSiteTemplateWithParent(site.getMobileTemplateId());
				if(customTemplate != null){
					templateName = customTemplate.getName();
				}
			} else {
				templateName = template.getName();
			}
			hso.setTemplateName(templateName);
		}
		return hso;
	}
	
	/**
	 * 获取站点设置相关信息
	 * @param hospitalId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/hospitalSiteInfo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> hospitalSiteInfo(Integer hospitalId){
		Map<String, Object> map = Maps.newHashMap();
		HospitalSiteInfoVO infoVo = new HospitalSiteInfoVO();
		infoVo.setHospitalId(hospitalId);
		Map<Integer, List<SiteResource>> siteResourceMap = siteResourceService.getSortResourceByHspId(hospitalId);
		infoVo.setMobileMainBannerRes(siteResourceMap.get(ResourceTypeEnum.MOBILE_MAIN_BANNER.getCode()));
		infoVo.setMobileDeputyBannerRes(siteResourceMap.get(ResourceTypeEnum.MOBILE_DEPUTY_BANNER.getCode()));
		List<SiteResource> logos = siteResourceMap.get(ResourceTypeEnum.MOBILE_LOGO.getCode());
		if (AssertUtil.isNotEmpty(logos)) {
			infoVo.setLogoRes(logos.get(0));
		}
		List<SiteResource> qrCodes = siteResourceMap.get(ResourceTypeEnum.QR_CODE.getCode());
		if (AssertUtil.isNotEmpty(qrCodes)) {
			infoVo.setQrCodeRes(qrCodes.get(0));
		}
		List<SiteResource> covers = siteResourceMap.get(ResourceTypeEnum.COVER.getCode());
		if (AssertUtil.isNotEmpty(covers)) {
			infoVo.setCoverRes(covers.get(0));
		}
		infoVo.setEnvironmentRes(siteResourceMap.get(ResourceTypeEnum.ENVIRONMENT_BIG_IMAGE.getCode()));
		Site site = siteService.getSiteByHospitalId(hospitalId);
		infoVo.setTemplateId(site.getMobileTemplateId());
		infoVo.setUrl(site.getUrl());
		infoVo.setSiteId(site.getId());
		SiteTemplate mobileTem = siteTemplateService.getSiteTemplateById(site.getMobileTemplateId());
		Map<String, Object> resourceMap = siteResource(OrganizationTypeEnum.HOSPITAL.getCode());
		List<SiteTemplate> mobileTemList = (List<SiteTemplate>) resourceMap.get("mobileTemplateList");
		if(mobileTem != null){
			infoVo.setTemplateName(mobileTem.getName());
			SiteSubWebCss css = siteSubWebCssService.getSiteSubWebCssById(mobileTem.getDefaultCssId());
			infoVo.setCssId(mobileTem.getDefaultCssId());
			infoVo.setCssName(css.getName());
			if (mobileTem.getIsCustom()) {
				mobileTemList.add(mobileTem);
			}
		}
		map.put("siteInfo", infoVo);
		map.put("cssList", resourceMap.get("cssList"));
		map.put("mobileTemplateList", mobileTemList);
		return map;
	}
	
	/**
	 * 更新站点设置信息
	 * @param infoVo
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateHospitalSiteInfo", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateHospitalSiteInfo(@RequestBody HospitalSiteInfoVO infoVo,
			HttpSession session) throws Exception{
		validateUrlByUpdate(infoVo);
		Integer hospitalId = infoVo.getHospitalId();
		List<SiteResource> mobileMainBannerRes = handleResource(infoVo.getMobileMainBannerRes(), 
				hospitalId, ResourceTypeEnum.MOBILE_MAIN_BANNER.getCode());
		List<SiteResource> mobileDeputyBannerRes = handleResource(infoVo.getMobileDeputyBannerRes(), 
				hospitalId, ResourceTypeEnum.MOBILE_DEPUTY_BANNER.getCode());
		List<SiteResource> environmentRes = handleResource(infoVo.getEnvironmentRes(), 
				hospitalId, ResourceTypeEnum.ENVIRONMENT_BIG_IMAGE.getCode());
		List<SiteResource> logoRes = handleResource(infoVo.getLogoRes() != null? 
				Arrays.asList(infoVo.getLogoRes()) : null, 
				hospitalId, ResourceTypeEnum.MOBILE_LOGO.getCode());
		List<SiteResource> qrCodeRes = handleResource(infoVo.getQrCodeRes() != null ? 
				Arrays.asList(infoVo.getQrCodeRes()) :null, 
				hospitalId, ResourceTypeEnum.QR_CODE.getCode());
		List<SiteResource> coverRes = handleResource(infoVo.getCoverRes() != null ? 
				Arrays.asList(infoVo.getCoverRes()) :null, 
				hospitalId, ResourceTypeEnum.COVER.getCode());
		Site site = new Site();
		site.setHospitalId(infoVo.getHospitalId());
		site.setMobileTemplateId(infoVo.getTemplateId());
		site.setUrl(infoVo.getUrl());
		siteService.updateSiteInfo(site, infoVo.getCssId(), mobileMainBannerRes,
				mobileDeputyBannerRes, 
				environmentRes,
				logoRes, 
				qrCodeRes,
				coverRes);
		Employee employee = SessionUtil.getEmployee();
		logger.info("event={},hospitalId={},operatorId={},operatorName={},time={},reqJson={}","更新站点设置信息", 
				infoVo.getHospitalId(), (employee != null ? employee.getId() : ""),
				(employee != null ? employee.getLoginName() : ""),
				LocalDateTime.now(), JSON.toJSONString(infoVo));
	}

	private void validateUrlByUpdate(HospitalSiteInfoVO infoVo) throws SiteException {
		if (infoVo.getIsChannelEdit()!= null && infoVo.getIsChannelEdit()){
			return;
		}
		if (AssertUtil.isEmpty(infoVo.getUrl())) {
			throw new SiteException(SiteException.URL_NULL, "网址为空");
		}
		Site site = siteService.getSite(infoVo.getUrl());
		if (site != null && site.getId() != infoVo.getSiteId().intValue()) {
			throw new SiteException(SiteException.URL_EXIST, "网址已经存在" + infoVo.getUrl());
		}
	}
	
	private List<SiteResource> handleResource(List<SiteResource> resourceList, int hospitalId, int type){
		if(AssertUtil.isEmpty(resourceList)){
			return resourceList;
		}
		String path = "hospital/" + hospitalId;
		for(SiteResource sr : resourceList){
			if(sr == null){
				continue;
			}
			if(sr.getHospitalId() == null){
				sr.setHospitalId(hospitalId);
			}
			if(sr.getType() == null){
				sr.setType(type);
				String value = "";
				if(type == ResourceTypeEnum.MOBILE_MAIN_BANNER.getCode()){
					value = path +  ImageService.MOBILE_MAIN_BANNER_DIR;
				}
				if(type == ResourceTypeEnum.MOBILE_DEPUTY_BANNER.getCode()){
					value = path +  ImageService.MOBILE_DEPUTY_BANNER_DIR;
				}
				if(type == ResourceTypeEnum.ENVIRONMENT_BIG_IMAGE.getCode()){
					value = path +  ImageService.BIG_DIR;
				}
				if(type == ResourceTypeEnum.MOBILE_LOGO.getCode()){
					value = path + "/";
				}
				if(type == ResourceTypeEnum.QR_CODE.getCode()){
					value = path +  "/";
				}
				if(type == ResourceTypeEnum.COVER.getCode()){
					value = path +  ImageService.COVER;
				}
				sr.setValue(value + sr.getName());
			}
		}
		return resourceList;
	}

	/**
	 * 签约对接列表页
	 * @param orgQueryParam
	 * @return
	 */
	@RequestMapping(value = "/hospitalListBySign", method = RequestMethod.POST)
	@ResponseBody
	public PageView<HospitalSignVO> listSignInfo(@RequestBody OrgQueryParam orgQueryParam){
		OrganizationQuery query = orgQueryParam.getOrganizationQuery();
		HospitalPrivateQuery privateQuery = orgQueryParam.getPrivateQuery();
		Page page= privateQuery.getPage();
		query.setContractStatus(privateQuery.getContractStatus());
		query.setContractType(privateQuery.getContractType());
		query.setOrderById(privateQuery.getOrderById());
		query.setPage(null);
		query.setOrganizationType(OrganizationTypeEnum.HOSPITAL.getCode());
		PageView<Hospital> hospitalPageView = hospitalService.listHospitalInfo(query);
		List<HospitalSignVO> hospitalSignVOList = new ArrayList<HospitalSignVO>();
		List<Sign> listSign = signService.selectSignByTypeAndStatus(query.getContractType(), query.getContractStatus());
		Map<Integer,List<Sign>> signInfo = listSign.stream().collect(Collectors.groupingBy(Sign::getOrganizationId));
		List<Hospital> tempList = hospitalPageView.getRecords();
		if(privateQuery.getContractStatus() != null || privateQuery.getContractType() != null){
			if(AssertUtil.isEmpty(signInfo.keySet())){
				tempList = Lists.newArrayList();
			} else {
				tempList = tempList.stream().filter(hos -> {
					
					List<Sign> signList = signInfo.get(hos.getId());
					if(AssertUtil.isEmpty(signList)){
						return false;
					}
					return true;
				}).collect(Collectors.toList());
			}
		}
		//分页处理
		if(AssertUtil.isNotEmpty(tempList)){
			page.setRowCount(tempList.size());
			tempList = tempList.stream().skip(page.getPageSize() * (page.getCurrentPage() - 1))
						.limit(page.getPageSize()).collect(Collectors.toList());
		}
		if(AssertUtil.isNotEmpty(tempList)){
			tempList.forEach(hospital -> {
				HospitalSignVO hospitalSignVo = getHospitalSignVo(privateQuery, signInfo, hospital);
				hospitalSignVOList.add(hospitalSignVo);
			});
		}
		return new PageView<>(hospitalSignVOList, page); 

	}

	private HospitalSignVO getHospitalSignVo(HospitalPrivateQuery privateQuery, 
			Map<Integer, List<Sign>> signInfo, Hospital hospital) {
		Map<Integer, OrganizationMediatorInfo> organizationMediatorInfoMap;
		HospitalSignVO hospitalSignVO =new HospitalSignVO();
		List<Sign> signList = signInfo.get(hospital.getId());
		HospitalSettings settings=(HospitalSettings)hospital.getSettings();
		if(privateQuery.getContractStatus()==null && privateQuery.getContractType()==null){
			hospitalSignVO.setHospital(hospital);
			if(signList!=null){
				hospitalSignVO.setSignMap(signInfo.get(hospital.getId()).stream().collect(Collectors.toMap(Sign::getType, sign -> sign)));
			}
			hospitalSignVO.setCooperateCompany(settings.getCooperateCompany());
			hospitalSignVO.setCooperateType(settings.getCooperateType());
			List<OrganizationMediatorInfo> organizationMediatorList=organizationMediatorInfoService.listOrganizationMediator(hospital.getId());
			organizationMediatorInfoMap=organizationMediatorList.stream().collect(Collectors.toMap(OrganizationMediatorInfo::getType, organizationMediatorInfo -> organizationMediatorInfo));
			hospitalSignVO.setExamMediator(organizationMediatorInfoMap.get(OrganizationMediatorTypeEnum.EXAMREPORT_DIRECTOR.getCode()));
			hospitalSignVO.setInfoMediator(organizationMediatorInfoMap.get(OrganizationMediatorTypeEnum.INFORMATION_CONTACTS.getCode()));
			hospitalSignVO.setHisMediator(organizationMediatorInfoMap.get(OrganizationMediatorTypeEnum.COMPANY_CONTACTS.getCode()));
		}else{
			if(signList!=null){
				List<Sign> signByHos = signService.getSignByHospitalId(hospital.getId());
				hospitalSignVO.setSignMap(signByHos.stream().collect(Collectors.toMap(Sign::getType, sign -> sign)));
				hospitalSignVO.setHospital(hospital);
				hospitalSignVO.setCooperateCompany(settings.getCooperateCompany());
				hospitalSignVO.setCooperateType(settings.getCooperateType());
				List<OrganizationMediatorInfo> organizationMediatorList=organizationMediatorInfoService.listOrganizationMediator(hospital.getId());
				organizationMediatorInfoMap=organizationMediatorList.stream().collect(Collectors.toMap(OrganizationMediatorInfo::getType, organizationMediatorInfo -> organizationMediatorInfo));
				hospitalSignVO.setExamMediator(organizationMediatorInfoMap.get(OrganizationMediatorTypeEnum.EXAMREPORT_DIRECTOR.getCode()));
				hospitalSignVO.setInfoMediator(organizationMediatorInfoMap.get(OrganizationMediatorTypeEnum.INFORMATION_CONTACTS.getCode()));
				hospitalSignVO.setHisMediator(organizationMediatorInfoMap.get(OrganizationMediatorTypeEnum.COMPANY_CONTACTS.getCode()));
			}
		}
		return hospitalSignVO;
	}
	
	@RequestMapping(value = "/getCooperateCompany", method = RequestMethod.POST)
	@ResponseBody
	public List<String> getCooperateCompany(){
		return hospitalService.listCooperateCompany();
	}
	
	/**
	 * 签约详情页
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value = "/signDetail",method = RequestMethod.GET)
	@ResponseBody
	public Map<String ,Object> signDetail(Integer hospitalId){
		Map<String,Object> map = Maps.newHashMap();
		List<OrganizationMediatorInfo> organizationMediatorList=organizationMediatorInfoService.listOrganizationMediator(hospitalId);
		Map<Integer,OrganizationMediatorInfo> organizationMediatorInfoMap=organizationMediatorList.stream().collect(Collectors.toMap(OrganizationMediatorInfo::getType, organizationMediatorInfo -> organizationMediatorInfo));
		List<Sign> signList=signService.getSignByHospitalId(hospitalId);
		Map<Integer,Sign> signMap=signList.stream().collect(Collectors.toMap(Sign::getType, sign ->sign));
		OrganizationMediatorInfo examMediator = organizationMediatorInfoMap.get(OrganizationMediatorTypeEnum.EXAMREPORT_DIRECTOR.getCode());
		map.put("examMediator", examMediator);
		OrganizationMediatorInfo hisMediator = organizationMediatorInfoMap.get(OrganizationMediatorTypeEnum.COMPANY_CONTACTS.getCode());
		map.put("hisMediator", hisMediator);
		OrganizationMediatorInfo infoMediator = organizationMediatorInfoMap.get(OrganizationMediatorTypeEnum.INFORMATION_CONTACTS.getCode());
		map.put("infoMediator", infoMediator);
		Sign collectionContract = signMap.get(SignEnum.collectionContract.getType());
		map.put("collectionContract", collectionContract);
		Sign discountContract = signMap.get(SignEnum.discountContract.getType());
		map.put("discountContract",  discountContract);
		Sign secrecyContract = signMap.get(SignEnum.secrecyContract.getType());
		map.put("secrecyContract",  secrecyContract);
		Sign serviceContract = signMap.get(SignEnum.serviceContract.getType());
		map.put("serviceContract",  serviceContract);
		Hospital hospital = hospitalService.getHospitalById(hospitalId);
		HospitalSettings setting = (HospitalSettings) hospital.getSettings();
		hospital.setSettings(null);
		map.put("hospital", hospital);
		map.put("coopCompanyRemark", setting.getCoopCompanyRemark());
		map.put("cooperateCompany", setting.getCooperateCompany());
		map.put("cooperateType", setting.getCooperateType());
		map.put("mediatorPrice", setting.getMediatorPrice());
		return map;
	}
	
	/**
	 * 医院合作签约信息编辑页面
	 * @param hospitalSignVO
	 */
	@RequestMapping(value = "/editSignInfo", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void editSignInfo(@RequestBody List<Sign> signList){
		if(!signList.isEmpty()){
			for(Sign signInfo : signList){
				if(signInfo.getId()==null && (signInfo.getStartDate()!=null || signInfo.getEndDate() != null 
						|| AssertUtil.isNotEmpty(signInfo.getAppendixInfo()) || AssertUtil.isNotEmpty(signInfo.getClausesInfo()))){
					signService.addSignInfo(signInfo);
				}else if(signInfo.getId()!=null && (signInfo.getStartDate()!=null || signInfo.getEndDate() != null 
						|| AssertUtil.isNotEmpty(signInfo.getAppendixInfo()) || AssertUtil.isNotEmpty(signInfo.getClausesInfo()))){
					signService.updateSignInfo(signInfo);
				}else if(signInfo.getId()!=null && signInfo.getStartDate()==null && signInfo.getEndDate() == null 
						&& AssertUtil.isEmpty(signInfo.getAppendixInfo()) && AssertUtil.isEmpty(signInfo.getClausesInfo())){
					signService.deleteSignInfo(signInfo);
				}
			}
		}
	}
	
	/**
	 * 医院合作联系人信息编辑页面
	 * @param mediatorInfo
	 */
	@RequestMapping(value = "/editMediatorInfo",method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void editMediatorInfo(@RequestBody List<OrganizationMediatorInfo> mediatorInfoList){
		if(!mediatorInfoList.isEmpty()){
			for(OrganizationMediatorInfo mediatorInfo : mediatorInfoList){
				if(mediatorInfo.getId()!=null){
					organizationMediatorInfoService.updateOrganizationMediatorInfo(mediatorInfo);
				}else{
					organizationMediatorInfoService.addOrganizationMediatorInfo(mediatorInfo);
				}
			}
		}
	}
	
	/**
	 * 体检软件厂商对接信息编辑页面
	 * @param hospitalSignVO
	 */
	@RequestMapping(value = "/editHisMediatorInfo" , method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void editHisMediatorInfo(@RequestBody HospitalSignVO hospitalSignVO){
		Hospital hospital = hospitalSignVO.getHospital();
		OrganizationMediatorInfo hisMediator=hospitalSignVO.getHisMediator();
		if(hisMediator!=null){
			hisMediator.setOrganizationId(hospital.getId());
			hisMediator.setType(OrganizationMediatorTypeEnum.COMPANY_CONTACTS.getCode());
			if(hisMediator.getId()==null){
				organizationMediatorInfoService.addOrganizationMediatorInfo(hisMediator);
			}else{
				organizationMediatorInfoService.updateOrganizationMediatorInfo(hisMediator);
			}
		}
		HospitalSettings settings= new HospitalSettings();
		settings.setHospitalId(hospital.getId());
		settings.setCoopCompanyRemark(hospitalSignVO.getCoopCompanyRemark());
		settings.setCooperateCompany(hospitalSignVO.getCooperateCompany());
		settings.setCooperateType(hospitalSignVO.getCooperateType());
		settings.setMediatorPrice(hospitalSignVO.getMediatorPrice());
		settings.setMobileFieldOrder(null);
		hospitalService.updateOrganizationSetting(settings);
        Employee employee = SessionUtil.getEmployee();
        logger.info("event={},hospitalId={},operatorId={},operatorName={},time={},reqJson={}","更新体检软件厂商", 
        		settings.getHospitalId(), (employee != null ? employee.getId() : ""),
        		(employee != null ? employee.getLoginName() : ""),
                LocalDateTime.now(), JSON.toJSONString(hospitalSignVO));

	}
	
	/**
	 * 导出体检中心信息
	 * @param hospitalIds
	 * @param isBasicInfo
	 * @param isCooperation
	 * @param isFunction
	 * @param isSign
	 * @param isSite
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/hospitalInfoExport" , method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void exportHospitalInfo(@RequestParam("hospitalIds")List<Integer> hospitalIds,
			@RequestParam(value = "isBasicInfo", required = false)Boolean isBasicInfo,
			@RequestParam(value = "isCooperation", required = false)Boolean isCooperation,
			@RequestParam(value = "isFunction", required = false)Boolean isFunction,
			@RequestParam(value = "isSign", required = false)Boolean isSign,
			@RequestParam(value = "isSite", required = false)Boolean isSite, 
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		if(AssertUtil.isEmpty(hospitalIds) || 
				(isBasicInfo == null || !isBasicInfo) && 
				(isSite == null || !isSite) && 
				(isCooperation == null || !isCooperation) && 
				(isFunction == null || !isFunction) && 
				(isSign == null || !isSign)){
			return;
		}
		if(hospitalIds.size() > 100){
			throw new Exception("每次导出数量不能超过100个！");
		}
		InputStream stream = getClass().getClassLoader().getResourceAsStream("hospitalTemplate/hospitalExport_template.xls");
		File targetFile = new File("hospitalExport_template.xls");
		FileUtils.copyInputStreamToFile(stream, targetFile);
		
		FileInputStream fileInputStream = new FileInputStream(targetFile);
		HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
		Map<Integer, Hospital> hosMap = Maps.newHashMap();
		if(isBasicInfo != null && isBasicInfo){
			createBasicInfoSheet(hospitalIds, workbook, hosMap);
		}
		
		if(isFunction != null && isFunction){
			createFunSheet(hospitalIds, workbook, hosMap);
		}
		if(isCooperation != null && isCooperation){
			createCooSheet(hospitalIds, workbook, hosMap);
		}
		if(isSite != null && isSite){
			createSiteSheet(hospitalIds, workbook, hosMap);
		}
		
		if(isSign != null && isSign){
			createSignSheet(hospitalIds, workbook, hosMap);
		}
		removeSheet(isBasicInfo, isCooperation, isFunction, isSign, isSite, workbook);
		String fileName = tempFolder + LocalDate.now() + "体检中心信息.xls";
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        String origin = response.getHeader("Access-Control-Allow-Origin");
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=" + 
        		new String((LocalDate.now() + "体检中心信息.xls").getBytes("GBK"), "ISO-8859-1"));
        response.addHeader("Access-Control-Allow-Credentials","true");
        response.addHeader("Access-Control-Allow-Origin",origin);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        try {
            OutputStream outputStreamo = response.getOutputStream();
            FileInputStream fInputStream = new FileInputStream(new File(fileName));
            IOUtils.copy(fInputStream, outputStreamo);

            outputStreamo.flush();
            outputStreamo.close();
            fInputStream.close();
            stream.close();
        } catch (Exception e) {
            logger.error("hospital export error", e);
        }
	}

	private void removeSheet(Boolean isBasicInfo, Boolean isCooperation, Boolean isFunction, Boolean isSign,
			Boolean isSite, HSSFWorkbook workbook) {
		if(isBasicInfo == null || !isBasicInfo){
			workbook.setSheetHidden(workbook.getSheetIndex("基本信息"), 1);
		}
		
		if(isFunction == null || !isFunction){
			workbook.setSheetHidden(workbook.getSheetIndex("功能配置"), 1);
		}
		if(isCooperation == null || !isCooperation){
			workbook.setSheetHidden(workbook.getSheetIndex("平台合作"), 1);
		}
		if(isSite == null || !isSite){
			workbook.setSheetHidden(workbook.getSheetIndex("站点"), 1);
		}
		
		if(isSign == null || !isSign){
			workbook.setSheetHidden(workbook.getSheetIndex("签约对接"), 1);
		}
	}

	private void createSignSheet(List<Integer> hospitalIds, HSSFWorkbook workbook, Map<Integer, Hospital> hosMap) {
		HSSFSheet sheet = workbook.getSheetAt(4);
		int row = 0;
		for(Integer hospitalId : hospitalIds){
			row += 1;
			Hospital hospital = hosMap.get(hospitalId);
			if(hospital == null){
				hospital = hospitalService.getHospitalById(hospitalId);
				hosMap.put(hospitalId, hospital);
			}
			HSSFRow hssfrow = sheet.createRow(row);
			hssfrow.createCell(0).setCellValue(hospital.getId());
			hssfrow.createCell(1).setCellValue(hospital.getName());
			Map<String ,Object> signMap = signDetail(hospitalId);
			OrganizationMediatorInfo examMediator = (OrganizationMediatorInfo) signMap.get("examMediator");
			if(examMediator != null){
				hssfrow.createCell(2).setCellValue(examMediator.getName());
				hssfrow.createCell(3).setCellValue(getGenderStr(examMediator.getGender()));
				hssfrow.createCell(4).setCellValue(examMediator.getMobile());
				hssfrow.createCell(5).setCellValue(examMediator.getTelephone());
			}
			OrganizationMediatorInfo infoMediator = (OrganizationMediatorInfo) signMap.get("infoMediator");
			if(infoMediator != null){
				hssfrow.createCell(6).setCellValue(infoMediator.getName());
				hssfrow.createCell(7).setCellValue(getGenderStr(infoMediator.getGender()));
				hssfrow.createCell(8).setCellValue(infoMediator.getMobile());
				hssfrow.createCell(9).setCellValue(infoMediator.getTelephone());
			}
			
			Sign serviceContract = (Sign) signMap.get("serviceContract");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(serviceContract != null){
				hssfrow.createCell(10).setCellValue(serviceContract.getStartDate() != null ? sdf.format(serviceContract.getStartDate()): null);
				hssfrow.createCell(11).setCellValue(serviceContract.getEndDate() != null ? sdf.format(serviceContract.getEndDate()) : null);
				hssfrow.createCell(12).setCellValue(serviceContract.getClausesInfo());
				hssfrow.createCell(13).setCellValue(getSignStatusStr(serviceContract.getStatus()));
			}
			Sign secrecyContract = (Sign) signMap.get("secrecyContract");
			if(secrecyContract != null){
				hssfrow.createCell(14).setCellValue(secrecyContract.getStartDate() != null ? sdf.format(secrecyContract.getStartDate()): null);
				hssfrow.createCell(15).setCellValue(secrecyContract.getEndDate() != null ? sdf.format(secrecyContract.getEndDate()) : null);
				hssfrow.createCell(16).setCellValue(secrecyContract.getClausesInfo());
				hssfrow.createCell(17).setCellValue(getSignStatusStr(secrecyContract.getStatus()));
			}
			Sign collectionContract = (Sign) signMap.get("collectionContract");
			if(collectionContract != null){
				hssfrow.createCell(18).setCellValue(collectionContract.getStartDate() != null ? sdf.format(collectionContract.getStartDate()): null);
				hssfrow.createCell(19).setCellValue(collectionContract.getEndDate() != null ? sdf.format(collectionContract.getEndDate()) : null);
				hssfrow.createCell(20).setCellValue(collectionContract.getClausesInfo());
				hssfrow.createCell(21).setCellValue(getSignStatusStr(collectionContract.getStatus()));
			}
			Sign discountContract = (Sign) signMap.get("discountContract");
			if(discountContract != null){
				hssfrow.createCell(22).setCellValue(discountContract.getStartDate() != null ? sdf.format(discountContract.getStartDate()): null);
				hssfrow.createCell(23).setCellValue(discountContract.getEndDate() != null ? sdf.format(discountContract.getEndDate()) : null);
				hssfrow.createCell(24).setCellValue(discountContract.getClausesInfo());
				hssfrow.createCell(25).setCellValue(getSignStatusStr(discountContract.getStatus()));
			}
			OrganizationMediatorInfo hisMediator = (OrganizationMediatorInfo) signMap.get("hisMediator");
			if(hisMediator != null){
				hssfrow.createCell(26).setCellValue(hisMediator.getName());
				hssfrow.createCell(27).setCellValue(hisMediator.getMobile());
				hssfrow.createCell(28).setCellValue(hisMediator.getTelephone());
			}
			HospitalSettings setting = (HospitalSettings) hospital.getSettings();
			if(setting != null){
				setting = hospitalService.getHospitalSettingsById(hospitalId);
				hospital.setSettings(setting);
			}
			hssfrow.createCell(29).setCellValue(setting.getCooperateCompany());
			Integer cooperateType = setting.getCooperateType();
			if(cooperateType != null && cooperateType == 1){
				hssfrow.createCell(30).setCellValue("自对接");
			}
			if(cooperateType != null && cooperateType == 2){
				hssfrow.createCell(30).setCellValue("厂家对接");
			}
			hssfrow.createCell(31).setCellValue(setting.getMediatorPrice() != null? (setting.getMediatorPrice() + "万"):"");
			hssfrow.createCell(32).setCellValue(setting.getCoopCompanyRemark());
		}
	}

	private String getGenderStr(Integer examGender) {
		String gender = "";
		if(examGender != null && examGender == 0){
			gender = "男";
		}
		if(examGender != null && examGender == 1){
			gender = "女";
		}
		if(examGender != null && examGender == 2){
			gender = "未知";
		}
		return gender;
	}
	
	private String getSignStatusStr(Integer status){
		if(status == null){
			return "";
		}
		if(status == 1){
			return "即将过期";
		}
		if(status == 2){
			return "已经过期";
		}
		if(status == 3){
			return "正常";
		}
		return "";
	}

	private void createSiteSheet(List<Integer> hospitalIds, HSSFWorkbook workbook, Map<Integer, Hospital> hosMap) {
		HSSFSheet sheet = workbook.getSheetAt(3);
		int row = 0;
		for(Integer hospitalId : hospitalIds){
			row += 1;
			Hospital hospital = hosMap.get(hospitalId);
			if(hospital == null){
				hospital = hospitalService.getHospitalById(hospitalId);
				hosMap.put(hospitalId, hospital);
			}
			HSSFRow hssfrow = sheet.createRow(row);
			hssfrow.createCell(0).setCellValue(hospital.getId());
			hssfrow.createCell(1).setCellValue(hospital.getName());
			Site site = siteService.getSiteByHospitalId(hospitalId);
			hssfrow.createCell(5).setCellValue(MOBILE_URL + site.getUrl());
			SiteTemplate mobileTem = siteTemplateService.getSiteTemplateById(site.getMobileTemplateId());
			if(mobileTem != null){
				hssfrow.createCell(2).setCellValue(mobileTem.getName());
				SiteSubWebCss css = siteSubWebCssService.getSiteSubWebCssById(mobileTem.getDefaultCssId());
				hssfrow.createCell(3).setCellValue(css.getName());
			}
			HospitalSettings setting = (HospitalSettings) hospital.getSettings();
			if(setting == null){
				setting = hospitalService.getHospitalSettingsById(hospitalId);
				hospital.setSettings(setting);
			}
			if(setting != null){
				Integer showInvoince = setting.getShowInvoice();
				String invoince = "";
				if(showInvoince == null || showInvoince == 0){
					invoince = "无";
				}
				if(showInvoince == 1){
					invoince = "询问";
				}
				if(showInvoince == 2){
					invoince = "检后开票";
				}
				
				hssfrow.createCell(4).setCellValue(invoince);
				List<WxConfig> configList = wxConfigService.listWxConfig();
				if(AssertUtil.isNotEmpty(configList)){
					List<Integer> wxConfigList = configList.stream()
							.map(config -> config.getHospitalId())
							.collect(Collectors.toList());
					if(wxConfigList.contains(hospital.getId())){
						hssfrow.createCell(6).setCellValue("接入");
					} else {
						hssfrow.createCell(6).setCellValue("未接入");
					}
				}
			}
		}
	}

	private void createCooSheet(List<Integer> hospitalIds, HSSFWorkbook workbook, Map<Integer, Hospital> hosMap) {
		HSSFSheet sheet = workbook.getSheetAt(2);
		int row = 0;
		for(Integer hospitalId : hospitalIds){
			row += 1;
			Hospital hospital = hosMap.get(hospitalId);
			if(hospital == null){
				hospital = hospitalService.getHospitalById(hospitalId);
				hosMap.put(hospitalId, hospital);
			}
			HSSFRow hssfrow = sheet.createRow(row);
			hssfrow.createCell(0).setCellValue(hospital.getId());
			hssfrow.createCell(1).setCellValue(hospital.getName());
			hssfrow.createCell(2).setCellValue(hospital.getShowInList() == 0 ? "不显示":"显示");
			HospitalSettings setting = (HospitalSettings) hospital.getSettings();
			if(setting == null){
				setting = hospitalService.getHospitalSettingsById(hospitalId);
				hospital.setSettings(setting);
			}
			if(setting != null){
				Boolean isPlatformSuspense = setting.getIsPlatformSuspense();
				hssfrow.createCell(3).setCellValue((isPlatformSuspense == null || !isPlatformSuspense)?"未开通":"已开通");
				Boolean isPrepayInvoince = setting.getIsPrepayInvoice();
				hssfrow.createCell(4).setCellValue((isPrepayInvoince == null || !isPrepayInvoince)?"未开通":"已开通");
				Boolean isIndividuationPlatformMeal = setting.getIsIndividuationPlatformMeal();
				hssfrow.createCell(5).setCellValue((isIndividuationPlatformMeal == null || !isIndividuationPlatformMeal)?"未开通":"已开通");
				hssfrow.createCell(6).setCellValue(setting.getLowestConsumption() == null?"0":String.valueOf(setting.getLowestConsumption()));
				hssfrow.createCell(7).setCellValue(setting.getPlatformGuestDiscount() == null?"0":String.valueOf(setting.getPlatformGuestDiscount()));
				hssfrow.createCell(8).setCellValue(setting.getPlatformCompDiscount() == null?"0":String.valueOf(setting.getPlatformCompDiscount()));
				hssfrow.createCell(9).setCellValue(setting.getGuestOnlineCompDiscount() == null?"0":String.valueOf(setting.getGuestOnlineCompDiscount()));
				hssfrow.createCell(10).setCellValue(setting.getGuestOfflineCompDiscount() == null?"0":String.valueOf(setting.getGuestOfflineCompDiscount()));
				hssfrow.createCell(11).setCellValue(setting.getHospitalCompDiscount() == null?"0":String.valueOf(setting.getHospitalCompDiscount()));
			}
		}
	}

	private void createFunSheet(List<Integer> hospitalIds, HSSFWorkbook workbook, Map<Integer, Hospital> hosMap) {
		HSSFSheet sheet = workbook.getSheetAt(1);
		int row = 0;
		for(Integer hospitalId : hospitalIds){
			row += 1;
			Hospital hospital = hosMap.get(hospitalId);
			if(hospital == null){
				hospital = hospitalService.getHospitalById(hospitalId);
				hosMap.put(hospitalId, hospital);
			}
			HSSFRow hssfrow = sheet.createRow(row);
			hssfrow.createCell(0).setCellValue(hospital.getId());
			hssfrow.createCell(1).setCellValue(hospital.getName());
			HospitalSettings setting = (HospitalSettings) hospital.getSettings();
			if(setting == null){
				setting = hospitalService.getHospitalSettingsById(hospitalId);
				hospital.setSettings(setting);
			}
			if(setting != null){
				Boolean exportWithXls = setting.getExportWithXls();
				hssfrow.createCell(2).setCellValue((exportWithXls != null && exportWithXls)? "浅对接":"深对接");
				Integer syncExamReportAbility = setting.getSyncExamReportAbility();
				String examReportAbility = "";
				if(syncExamReportAbility == null || syncExamReportAbility == 0){
					examReportAbility = "无对接";
				} else if(syncExamReportAbility == 1){
					examReportAbility = "全量报告";
				} else if(syncExamReportAbility == 2){
					examReportAbility = "增量报告";
				}
				hssfrow.createCell(3).setCellValue(examReportAbility);
				Boolean openSyncCompany = setting.getOpenSyncCompany();
				hssfrow.createCell(4).setCellValue((openSyncCompany == null || !openSyncCompany)?"未开启":"已开启");
				Boolean allowAdjustPrice = setting.getAllowAdjustPrice();
				hssfrow.createCell(5).setCellValue((allowAdjustPrice == null || !allowAdjustPrice)?"未开启":"已开启");
				Boolean exportWithNoExamDate = setting.getExportWithNoExamDate();
				hssfrow.createCell(6).setCellValue((exportWithNoExamDate == null || !exportWithNoExamDate)?"不允许":"允许");
				Integer orderMax = setting.getSameDayOrderMaximum();
				hssfrow.createCell(7).setCellValue(orderMax == null ?"0":(String.valueOf(orderMax)));
				Boolean openGroupExamReport = setting.getOpenGroupExamReport();
				hssfrow.createCell(8).setCellValue((openGroupExamReport == null || !openGroupExamReport)?"未开启":"已开启");
				Boolean isSmartRecommend = setting.getIsSmartRecommend();
				hssfrow.createCell(9).setCellValue((isSmartRecommend == null || !isSmartRecommend)?"未开启":"已开启");
				int precisionCode = CalculatorServiceEnum.getCodeByName(setting.getCalculatorService());
				String precision = "";
				if(precisionCode == 1){
					precision = "元";
				}
				if(precisionCode == 2){
					precision = "角";
				}
				if(precisionCode == 3){
					precision = "分";
				}
				hssfrow.createCell(10).setCellValue(precision);
				String settleCycle = setting.getSettleCycle() != null ? ("每月" + setting.getSettleCycle() + "日") : "";
				hssfrow.createCell(11).setCellValue(settleCycle);
				Boolean accountPay = setting.getAccountPay();
				hssfrow.createCell(12).setCellValue((accountPay == null || !accountPay)?"未开通":"已开通");
				Boolean aliPay = setting.getAliPay();
				hssfrow.createCell(13).setCellValue((aliPay == null || !aliPay)?"未开通":"已开通");
				Boolean weixinPay = setting.getWeiXinPay();
				hssfrow.createCell(14).setCellValue((weixinPay == null || !weixinPay)?"未开通":"已开通");
				Boolean acceptOfflinePay = setting.getAcceptOfflinePay();
				hssfrow.createCell(15).setCellValue((acceptOfflinePay == null || !acceptOfflinePay)?"未开通":"已开通");
				Boolean needLocalPay = setting.getNeedLocalPay();
				hssfrow.createCell(16).setCellValue((needLocalPay == null || !needLocalPay)?"不需要确认":"需要确认");
				hssfrow.createCell(17).setCellValue(setting.getWorkDay());
				
				hssfrow.createCell(18).setCellValue(setting.getExamStartTime() + "-" + setting.getExamEndTime());
				String drawBlood = "";
				if(AssertUtil.isNotEmpty(setting.getExamStartTime()) && AssertUtil.isNotEmpty(setting.getExamEndTime())){
					drawBlood = setting.getExamStartTime() + "-" + setting.getExamEndTime();
				} else if(AssertUtil.isNotEmpty(setting.getExamStartTime()) || AssertUtil.isNotEmpty(setting.getExamEndTime())){
					drawBlood = AssertUtil.isNotEmpty(setting.getExamStartTime())?setting.getExamStartTime():setting.getExamEndTime();
				}
				hssfrow.createCell(19).setCellValue(drawBlood);
				List<HospitalPeriodSetting> periodSettings = hospitalPeriodSettingService.getHospitalPeriodSetting(hospitalId);
				if(AssertUtil.isNotEmpty(periodSettings)){
					List<String> periodNameList = periodSettings.stream().map(period -> period.getName()).collect(Collectors.toList());
					hssfrow.createCell(20).setCellValue(String.join("|", periodNameList));
				}
				hssfrow.createCell(21).setCellValue(setting.getPreviousBookDays() + "天  " + setting.getPreviousBookTime());
				hssfrow.createCell(22).setCellValue(setting.getPreviousExportDays() + "");
			}
		}
	}

	private void createBasicInfoSheet(List<Integer> hospitalIds, HSSFWorkbook workbook, Map<Integer, Hospital> hosMap)
			throws IllegalAccessException, InvocationTargetException {
		HSSFSheet sheet = workbook.getSheetAt(0);
		int row = 0;
		for(Integer hospitalId : hospitalIds){
			row += 1;
			Map<String,Object> hospitalInfoMap = hospitalDetail(hospitalId);
			Hospital hospital = (Hospital) hospitalInfoMap.get("hospital");
			hosMap.put(hospitalId, hospital);
			HSSFRow hssfrow = sheet.createRow(row);	
			hssfrow.createCell(0).setCellValue(hospital.getId());
			hssfrow.createCell(1).setCellValue(hospital.getName());
			hssfrow.createCell(2).setCellValue(hospital.getType());
			hssfrow.createCell(3).setCellValue(hospital.getLevel());
			Address address = hospital.getAddress();
			if(address != null){
				hssfrow.createCell(4).setCellValue(hospital.getAddress().getBriefAddress());
				hssfrow.createCell(5).setCellValue(hospital.getAddress().getAddress());
				hssfrow.createCell(6).setCellValue(hospital.getAddress().getLongitude() 
						+ "  " + hospital.getAddress().getLatitude());
			}
			hssfrow.createCell(7).setCellValue(hospital.getTrafficInfo());
			OrganizationManager orgManager = (OrganizationManager) hospitalInfoMap.get("manager");
			if(orgManager != null){
				hssfrow.createCell(8).setCellValue(orgManager.getManagerName());
				hssfrow.createCell(9).setCellValue(orgManager.getMobile());
				hssfrow.createCell(10).setCellValue(orgManager.getUsername());
			}
			OrganizationMediatorInfo mediatorInfo = (OrganizationMediatorInfo) hospitalInfoMap.get("mediatorInfo");
			if(mediatorInfo != null){
				hssfrow.createCell(11).setCellValue(mediatorInfo.getName());
				hssfrow.createCell(12).setCellValue(mediatorInfo.getMobile());
				hssfrow.createCell(13).setCellValue(mediatorInfo.getMail());
			}
			Employee employee = (Employee) hospitalInfoMap.get("opsManager");
			if(employee != null){
				hssfrow.createCell(14).setCellValue(employee.getEmployeeName());
				hssfrow.createCell(15).setCellValue(employee.getMobile());
				hssfrow.createCell(16).setCellValue(employee.getDepartName());
			}
			HospitalSettings setting = hospitalService.getHospitalSettingsById(hospitalId);
			hospital.setSettings(setting);;
			if(setting != null){
				hssfrow.createCell(17).setCellValue(setting.getBusinessAmount() + "");
				hssfrow.createCell(18).setCellValue(setting.getOpsRemark() + "");
			}
			hssfrow.createCell(19).setCellValue(hospital.getPhone());
			hssfrow.createCell(20).setCellValue(setting.getGroupExamTel());
			hssfrow.createCell(21).setCellValue(setting.getServiceTel());
			hssfrow.createCell(22).setCellValue(setting.getTechnicalTel());
			hssfrow.createCell(23).setCellValue(hospital.getKeywords());
			hssfrow.createCell(24).setCellValue(hospital.getExamNotice());
			hssfrow.createCell(25).setCellValue((setting.getIsBreakfast() == null || !setting.getIsBreakfast())?"不提供":"提供");
			hssfrow.createCell(26).setCellValue(hospital.getBriefIntro());
			hssfrow.createCell(27).setCellValue(hospital.getDetailIntro());
		}
	}
		
	@RequestMapping(value= "/initHospitalPinYin", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void initHospitalPinYin(){
		hospitalService.initHospitalPinYin();
	}
	/**
	 * 保存体检中心消息配置列表
	 * 
	 * @param smsList
	 **/
	@RequestMapping(value = "/hospitalsms", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void saveHospitalSms(@RequestBody List<SmsTemplate> smsList,HttpSession session) {
		Employee employee = (Employee)SecurityUtils.getSubject().getPrincipal();
		List<SmsTemplateUpdatePriorityReq> smsTemplateUpdatePriorityReqs = new ArrayList<>();
		if (AssertUtil.isNotEmpty(smsList)) {
			for (SmsTemplate sms : smsList) {
				SmsTemplateUpdatePriorityReq updateReq = new SmsTemplateUpdatePriorityReq();
				updateReq.setHospitalId(sms.getHospitalId());
				updateReq.setTemplateCode(sms.getCode());
				updateReq.setPriority(sms.getPriority());
				smsTemplateUpdatePriorityReqs.add(updateReq);
			}
		}
		SmsTemplateUpdateBatchResp resp = smsTemplateService.updatePriorityBatch(smsTemplateUpdatePriorityReqs);
		logger.info("event={},bizType={},operatorId={},operatorName={},time={}, reqJson={}","医院消息发送方式设置"+ resp.isSuccess(),
				"医院消息发送方式设置",(employee != null ? employee.getId() : ""),
				(employee != null ? employee.getLoginName() : ""),LocalDateTime.now(), JSON.toJSONString(smsList));
	}
	
	
	/**
	 * 获取体检中心消息配置列表
	 * 
	 * @param hospitalId
	 **/
	@RequestMapping(value = "/hospitalsms", method = RequestMethod.GET)
	@ResponseBody
	public List<SmsTemplate> getHospitalSms(int hospitalId) {
		SmsTemplateReq smsTemplateReq = new SmsTemplateReq();
		smsTemplateReq.setPageNo(1);
		smsTemplateReq.setPageSize(1000);
		smsTemplateReq.setHospitalId(hospitalId);
		List<SmsTemplate> smsTemplateList = smsTemplateService.query(smsTemplateReq).getSmsTemplateList();
		List<SmsTemplate> smsTemplates = new ArrayList<>();
		// 根据InitTemplateEnum初始化短信模板，结合数据库短信模板，生成显示消息设置
		for (InitTemplateEnum template : InitTemplateEnum.values()) {
			SmsTemplate smsTemplate = new SmsTemplate();
			for (SmsTemplate st : smsTemplateList) {
				boolean flag = false;
				if (StringUtils.isNotBlank(st.getCode())) {
					if (template.getCode().equalsIgnoreCase(st.getCode())) {
						flag = true;
					// 特殊处理重置密码短信模板，这个code比较乱，有resetpwd.vm还有updatePwd.vm
					} else if (template.equals(InitTemplateEnum.RESET_PWD)
							&& "resetpwd.vm".equalsIgnoreCase(st.getCode())) {
						flag = true;
					}
				}
				if (flag) {
					BeanUtils.copyProperties(st, smsTemplate);
					smsTemplate.setName(template.getName());
					smsTemplates.add(smsTemplate);
					// 只取第一条，因为所有相同短信模板的priority都是一致的
					break;
				}
			}
		}
		return smsTemplates;
	}
	
	/**
	 * 获取所有问卷
	 * @param hospitalId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getAllSurvey", method = RequestMethod.GET)
    @ResponseBody
    public List<SurveyDTO> getAllSurvey(Integer hospitalId) throws Exception{
        List<SurveyDTO> surveyVOList = surveyService.getAllSurvey(hospitalId);
        return surveyVOList;
    }
	
	/**
	 * 编辑问卷
	 * @param hospitalSurveyRelationDTO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/allocateSurvey", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
    public void allocateSurveyToHospital(@RequestBody List<SurveyDTO> surveyDtoList,
    		@RequestParam("hospitalId") Integer hospitalId) throws Exception{
		if(AssertUtil.isEmpty(surveyDtoList)){
			return;
		}
		for(SurveyDTO dto : surveyDtoList){
			HospitalSurveyRelationDTO relation = new HospitalSurveyRelationDTO();
			relation.setId(dto.getHospitalSurveyRelationId());
			relation.setSurveyId(dto.getId());
			relation.setShowType(dto.getShowType());
			relation.setPageType(dto.getPageType());
			relation.setIsAllocatingToHospital(dto.getIsAllocatingToHospital());
			relation.setHospitalId(hospitalId);
			surveyService.allocateSurveyToHospital(relation);
		}
        Employee employee = SessionUtil.getEmployee();
        logger.info("event={},hospitalId:{},operatorId={},operatorName={},time={},reqJson={}","更新体检中心问卷", 
                hospitalId,(employee != null ? employee.getId() : ""),(employee != null ? employee.getLoginName() : ""),
                LocalDateTime.now(), JSON.toJSONString(surveyDtoList));
    }
	
	/**
	 * 重置平台客户经理密码
	 * 
	 * @param accountId
	 */
	@RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void resetPwd(@RequestParam(value = "hospitalId") Integer hospitalId) {
		Account manager = accountService.getHospitalDefaultManager(hospitalId);
		//随机生成6位数字
		String pwd = "11111111a";
		userService.changePassword(manager.getId(), pwd);
		Employee employee = SessionUtil.getEmployee();
		logger.info("ops更改密码, 操作人：{}, hospitalId:{}, 新密码：{}, 操作时间：{}", 
				employee != null ? employee.getLoginName():null, hospitalId,  "11111111a", LocalDateTime.now());
	}

	/**
	 * 更新结算设置
	 */
	@RequestMapping(value = "/settlemantOpen", method = RequestMethod.POST)
	@ResponseBody
	public void settlemant(@RequestParam("hospitalId") Integer hospitalId,
						   @RequestParam("settlementOpen") Integer settlemantOpen,
						   @RequestParam(value = "settlementTime", required = false)String settlemantTime) throws ParseException {
		HospitalSettings settings = new HospitalSettings();

		if (StringUtils.isNotEmpty(settlemantTime)) {
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				settings.setSettlementTime(format.parse(settlemantTime));
			} catch (Exception e) {
				logger.error("日期格式解析失败 :格式={},日期={}","yyyy-MM-dd ", settlemantTime);
				throw e;
			}
		}
		logger.info("更新结算: hospitalId={},操作时间={},更新结算状态={}", hospitalId,LocalDateTime.now(), settlemantOpen);
		settings.setHospitalId(hospitalId);
		settings.setSettlementOpen(settlemantOpen);
		hospitalService.updateOrganizationSetting(settings);
	}
	/**
	 * 
	 * update examItem job
	 * 
	 **/
	@RequestMapping(value = "/updateitemstatus", method = RequestMethod.POST)
	@ResponseBody
	public ExamItemJobResult<Integer, String> updateExamItemStatus(Integer hospitalId) {
		ExamitemUpdateResult result = examitemUpdateClient.get(hospitalId);
		if (result == null) {
			return new ExamItemJobResult<Integer, String>(UpdateItemStatusEnum.NONE.getCode(), 
					UpdateItemStatusEnum.NONE.getName());
		} else {
			return new ExamItemJobResult<Integer, String>(result.getStatus().getCode(),
					result.getMessage()); 
		}
	}
	
	class ExamItemJobResult <A,B> {
	    public final A status;
	    public final B message;
	    public ExamItemJobResult(A a, B b) {
	        this.status = a;
	        this.message = b;
	    }
	}
	
	/**
	 * 
	 * update examItem job
	 * 
	 * @throws HospitalException
	 * 
	 **/
	@RequestMapping(value = "/updateitemjob", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateExamItemJob(Integer hospitalId) throws HospitalException {
		Employee employee = SessionUtil.getEmployee();
		logger.info("ops执行单项任务, 操作人：{}, hospitalId:{}, 操作时间：{}", 
				employee != null ? employee.getLoginName() : null, hospitalId, LocalDateTime.now());
		if (examitemUpdateClient.get(hospitalId) != null
                && examitemUpdateClient.get(hospitalId).getStatus().equals(UpdateItemStatusEnum.EXECUTING)) {
            throw new HospitalException(HospitalException.UPDATE_ITEM_EXCUTING, "更新单项的任务正在执行中");
        }
        
        ExamitemUpdateResult result = new ExamitemUpdateResult();
		result.setStatus(UpdateItemStatusEnum.EXECUTING);
		result.setMessage("执行中");
		examitemUpdateClient.put(hospitalId, result);

		Runnable sendThread = () -> {
			try {
				mealManageService.effectExamItemChanges(hospitalId);
				result.setStatus(UpdateItemStatusEnum.SUCCESS);
				result.setMessage("执行成功");
				examitemUpdateClient.put(hospitalId, result);
			} catch (Exception e) {
				result.setStatus(UpdateItemStatusEnum.FAIL);
				String message = "";
				Throwable throwable = e.getCause();
				if (throwable != null) {
					message = throwable.getMessage();
					if (StringUtils.isNotBlank(message)) {
						message = ",原因:" + message;
					}
				}
				result.setMessage("更新单项任务失败" + message);
				examitemUpdateClient.put(hospitalId, result);
				logger.error("更新单项任务失败", e);
			}
		};

        executorService.submit(sendThread);
	}
	
	/**
	 * 是否在平台显示
	 * @param showInList
	 */
	@RequestMapping(value = "/showInList", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void showInList(@RequestParam("hospitalId")Integer hospitalId,
			@RequestParam("showInList")Boolean showInList){
		Employee employee = SessionUtil.getEmployee();
		logger.info("ops更新体检中心是否在平台显示, 操作人：{}, hospitalId:{}, 是否开启：{}, 操作时间：{}", 
				employee != null ? employee.getLoginName():null, hospitalId, showInList, LocalDateTime.now());
		if(showInList!=null){
			hospitalService.updateHospitalShowInListColumn(hospitalId, showInList);
		}
	}
	
	/**
	 * 深浅对接切换
	 * @param exportWithXls
	 */
	@RequestMapping(value = "/exportWithXls", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void exportWithXls(@RequestParam("exportWithXls")Boolean exportWithXls,
			@RequestParam("hospitalId")Integer hospitalId){
		Employee employee = SessionUtil.getEmployee();
		logger.info("ops切换深浅对接, 操作人：{}, hospitalId:{}, 切换深浅：{}, 操作时间：{}", 
				employee != null ? employee.getLoginName(): null, hospitalId, exportWithXls, 
						LocalDateTime.now());
		HospitalSettings settings=new HospitalSettings();
		settings.setHospitalId(hospitalId);
		settings.setExportWithXls(exportWithXls);
		settings.setMobileFieldOrder(null);
		organizationSettingsService.update(settings);
	}
	
	/**
	 * 开启单位同步
	 * @param hospitalId
	 */
	@RequestMapping(value = "/openSyncCompany", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void openSyncCompany(Integer hospitalId) throws Exception{
		Employee employee = SessionUtil.getEmployee();
		logger.info("ops开启单位同步, 操作人：{}, hospitalId:{}, 操作时间：{}", 
				employee != null ? employee.getLoginName() : null, hospitalId, LocalDateTime.now());
		HospitalSettings oriSettings = hospitalService.getHospitalSettingsById(hospitalId);
		if(oriSettings.getOpenSyncCompany()){
			return;
		}
		HospitalSettings settings=new HospitalSettings();
		settings.setMobileFieldOrder(null);
		settings.setOpenSyncCompany(true);
		settings.setOpenSyncMeal(true);
		settings.setHospitalId(hospitalId);
		hospitalService.updateOrganizationSetting(settings);
		try{
			syncCrmHisCompanyService.initCompanyHisRelationByNewHospitalId(hospitalId);
		} catch (Exception ex){
			settings.setOpenSyncCompany(false);
			hospitalService.updateOrganizationSetting(settings);
			logger.error("开启单位同步失败，hospitalId:{}, operator:{}, employeeId:{}, time:{}", 
					hospitalId, employee != null? employee.getEmployeeName():null, 
					employee != null ? employee.getId(): null, LocalDateTime.now(),ex);
			throw ex;
		}
	}
	
	
	@RequestMapping(value = "/initHospitalWorkDay", method = RequestMethod.GET)
	@ResponseBody
	public String initHospitalWorkDay(){
		String msg = "";
		List<Integer> hospitalIds = hospitalService.getHospitalIds();
		for(Integer hospitalId : hospitalIds){
			try{
				HospitalSettings setting = hospitalService.getHospitalSettingsById(hospitalId);
				if(setting != null && AssertUtil.isNotEmpty(setting.getWorkDay())){
					String[] workDayArr = setting.getWorkDay().split(",");
					String newWorkDay = "";
					for(String workDay : workDayArr){
						if(workDay.contains("一")){
							newWorkDay += "2,";
						}
						if(workDay.contains("二")){
							newWorkDay += "3,";
						}
						if(workDay.contains("三")){
							newWorkDay += "4,";
						}
						if(workDay.contains("四")){
							newWorkDay += "5,";
						}
						if(workDay.contains("五")){
							newWorkDay += "6,";
						}
						if(workDay.contains("六")){
							newWorkDay += "7,";
						}
						if(workDay.contains("日")){
							newWorkDay += "1,";
						}
					}
					if(AssertUtil.isNotEmpty(newWorkDay)){
						setting.setWorkDay(newWorkDay.substring(0, newWorkDay.length() - 1));
						hospitalService.updateOrganizationSetting(setting);
					}
				}
			} catch (Exception ex){
				msg += ex.toString();
			}
		}
		if(AssertUtil.isNotEmpty(msg)){
			return msg;
		} else {
			return "success";
		}
	}
	
	/**
	 * 获取体检中心初始化列表
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value = "/listHospitalInitialization", method = RequestMethod.GET)
	@ResponseBody
	public List<HospitalInitialization> getHospitalInitializationList(int hospitalId){
		return hospitalInitializationService.listHospitalInitialization(hospitalId);
	}
	
	/**
	 * 初始化操作
	 * @param initializations
	 * @throws Exception 
	 */
	@RequestMapping(value = "/initializeHospitalProject", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void initializeHospitalProject(@RequestBody List<HospitalInitialization> initializations)
			throws Exception{
		if(AssertUtil.isNotEmpty(initializations)){
			Employee employee = SessionUtil.getEmployee();
			for(HospitalInitialization init : initializations){
				init.setOperator(employee.getEmployeeName());
			}
		}
		hospitalInitializationService.initializeHospitalProject(initializations);
	}
}
