
package com.mytijian.admin.web.controller.resource;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.account.enums.SystemTypeEnum;
import com.mytijian.account.exceptions.AccountException;
import com.mytijian.account.model.Account;
import com.mytijian.account.model.AccountInfo;
import com.mytijian.account.model.User;
import com.mytijian.account.param.AccountQueryParam;
import com.mytijian.account.param.UserQueryParam;
import com.mytijian.account.service.AccountInfoService;
import com.mytijian.account.service.AccountService;
import com.mytijian.account.service.UserService;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.web.vo.account.ManagerVo;
import com.mytijian.admin.web.vo.resource.OrganizationListVo;
import com.mytijian.admin.web.vo.resource.OrganizationManagerVo;
import com.mytijian.admin.web.vo.resource.SiteResourceVo;
import com.mytijian.uic.annotation.LoginRequired;
import com.mytijian.calculate.CalculatorServiceEnum;
import com.mytijian.gotone.api.SmsTemplateService;
import com.mytijian.mongodb.MongoException;
import com.mytijian.resource.dto.GeneralManagerDto;
import com.mytijian.resource.dto.HospitalSettingDto;
import com.mytijian.resource.dto.OrganizationDto;
import com.mytijian.resource.dto.OrganizationHospitalRelationDto;
import com.mytijian.resource.enums.OrganizationAllocationStatusEnum;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.exceptions.HospitalException;
import com.mytijian.resource.model.*;
import com.mytijian.resource.service.AddressService;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.site.enums.ResourceTypeEnum;
import com.mytijian.site.enums.TemplateTypeEnum;
import com.mytijian.site.exception.SiteException;
import com.mytijian.site.model.*;
import com.mytijian.site.service.*;
import com.mytijian.util.AssertUtil;
import com.mytijian.web.intercepter.Token;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ManagerHospitalController {

	private final static Logger logger = LoggerFactory.getLogger(ManagerHospitalController.class);

	@Value("${aliyun.oss.endpoint}")
	private String endpoint;

	@Value("${aliyun.oss.bucket}")
	private String bucket;

	@Value("${aliyun.oss.accessKeyId}")
	private String accessKeyId;

	@Value("${aliyun.oss.accessKeySecret}")
	private String accessKeySecret;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;

	@Resource(name = "hospitalExamItemManager")
	private HospitalExamItemManager hospitalExamItemManager;

	@Resource(name = "accountService")
	private AccountService accountService;

	@Resource(name = "accountInfoService")
	private AccountInfoService accountInfoService;

	@Resource(name = "siteService")
	private SiteService siteService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "addressService")
	private AddressService addressService;

	@Resource(name = "siteResourceService")
	private SiteResourceService siteResourceService;

	@Resource(name = "cssService")
	private CssService cssService;

	@Resource(name = "siteTemplateService")
	private SiteTemplateService siteTemplateService;

	@Resource(name = "siteSubWebCssService")
	private SiteSubWebCssService siteSubWebCssService;

	@Resource(name = "smsTemplateService")
	private SmsTemplateService smsTemplateService;

	private OSSClient ossClient = null;

	@RequestMapping(value = "/hospital", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public List<Hospital> getHospital() throws Exception {
		return hospitalService.getHospitals(null, null);
	}

	@RequestMapping(value = "/orderOrganizationList", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public OrganizationListVo getOrganizationList(){
		OrganizationListVo organizationListVo = new OrganizationListVo();
		List<Hospital> hospitals = hospitalService.getOrganizationList(OrganizationTypeEnum.HOSPITAL.getCode()).stream().filter(hospital -> hospital.getEnable().equals(Hospital.STATUS_ENABLE)).collect(Collectors.toList());
		organizationListVo.setHospitals(hospitals);
		List<Hospital> organizationList = hospitalService.getOrganizationList(OrganizationTypeEnum.CHANNEL.getCode()).stream().filter(hospital -> hospital.getEnable().equals(Hospital.STATUS_ENABLE)).collect(Collectors.toList());
		organizationListVo.setChannels(organizationList);
		Map<Integer, String> map = organizationList.stream().collect(Collectors.toMap(Hospital::getId, Hospital::getName));
		organizationListVo.setChannelMap(map);
		return organizationListVo;
	}


	@RequestMapping(value = "/getHospitalList", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public List<Hospital> getHospitalList() throws Exception {
		return hospitalService.getOrganizationList(OrganizationTypeEnum.HOSPITAL.getCode());
	}

	@RequestMapping(value = "/hospitalsetting", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public HospitalSettings getHospitalSetting(Integer id) throws Exception {
		return hospitalService.getHospitalSettingsById(id);
	}

	/**
	 * 
	 * update体检中心设置
	 * 
	 * @throws HospitalException
	 * 
	 **/
	@RequestMapping(value = "/hospitalsetting", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	@LoginRequired
	public void saveExamParameter(@RequestBody HospitalSettingDto hospitalSettingDto)
			throws MongoException, HospitalException {
		hospitalService.addOrUpdateHospitalSetting(hospitalSettingDto);
	}

	/**
	 * 
	 * update examItem job
	 * 
	 * @throws HospitalException
	 * 
	 **/
	@RequestMapping(value = "/updateitemjob", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	@LoginRequired
	public void updateExamItemJob(Integer hospitalId) throws HospitalException {

		hospitalExamItemManager.exeUpdateItemJob(hospitalId);
	}

	/**
	 * 
	 * update examItem job
	 * 
	 **/
	@RequestMapping(value = "/updateitemstatus", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public String updateExamItemStatus(Integer hospitalId) {

		return hospitalExamItemManager.getUpdateItemStatus(hospitalId);
	}

	/**
	 * 获取机构列表
	 * 
	 * @param orgType
	 * @return
	 */
	@RequestMapping(value = "/organizationList", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public List<Map<String, Object>> organizationList(int orgType) {
		//step 1 获取医院列表
		List<Hospital> hospitalList = hospitalService.getOrganizationList(orgType);
		if(hospitalList == null || hospitalList.isEmpty() ){
			return Lists.newArrayList();
		}
		
		List<Integer> managerList = hospitalList.stream()
				.map(item -> item.getDefaultManagerId())
				.collect(Collectors.toList());
		
		// step 2 获取account列表以及map
		Map<Integer,Account> acMap = getAccountMap(managerList);
		
		// step 3 获取user列表以及map
		Map<Integer,User> usMap = getUserMap(managerList);
		
		// step 4 组装返回数据
		List<Map<String, Object>> resultList = buildResult(hospitalList,acMap,usMap);
		
		return resultList;
	}
	/**
	 * 构造返回数据
	 * @param hospitalList
	 * @param acMap
	 * @param usMap
	 * @return
	 */
	private List<Map<String, Object>> buildResult(List<Hospital> hospitalList, Map<Integer,Account> acMap, Map<Integer,User> usMap){
		List<Map<String, Object>> resultList = Lists.newArrayList();
		for (Hospital hospital : hospitalList) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("hospital", hospital);
			Account account = acMap.get(hospital.getDefaultManagerId());
			ManagerVo managerVo = new ManagerVo();
			if (account != null) {
				BeanUtils.copyProperties(account, managerVo);
				User user = usMap.get(hospital.getDefaultManagerId());
				if(user != null){
					managerVo.setUsername(user.getUsername());
				}
			}
			map.put("manager", managerVo);
			resultList.add(map);
		}
		return resultList;
	}
	/**
	 * 获取usermap
	 * key：accountId
	 * value：user
	 * @param accountIdList
	 * @return
	 */
	private Map<Integer,User> getUserMap(List<Integer> accountIdList){
		if(accountIdList == null || accountIdList.isEmpty()){
			return Maps.newHashMap();
		}
		UserQueryParam userQueryParam = new UserQueryParam();
		userQueryParam.setAccountIds(accountIdList);
		List<User> userList = userService.listUser(userQueryParam);
		Map<Integer,User> usMap = Maps.newHashMap();
		if(userList == null || userList.isEmpty()){
			return Maps.newHashMap();
		}
		userList.stream().forEach(item -> {
			if(usMap.get(item.getAccountId()) == null){
				usMap.put(item.getAccountId(), item);
			}
		});
		return usMap;
	}
	/**
	 * 获取用户信息map
	 * key：accountId
	 * value:account
	 * @param accountIdList
	 * @return
	 */
	private Map<Integer,Account> getAccountMap(List<Integer> accountIdList){
		if(accountIdList == null || accountIdList.isEmpty()){
			return Maps.newHashMap();
		}
		AccountQueryParam queryParam = new AccountQueryParam();
		queryParam.setIds(accountIdList);
		List<Account> accountList = accountService.listAccount(queryParam);
		
		Map<Integer,Account> acMap = Maps.newHashMap();
		if(accountList == null || accountList.isEmpty()){
			return Maps.newHashMap();
		}
		accountList.stream().forEach(item -> acMap.put(item.getId(), item));
		return acMap;
	}
	
	
	

	/**
	 * 添加机构
	 * 
	 * @param orgManagerVo
	 * @throws Exception
	 */
	@RequestMapping(value = "/organization/add", method = RequestMethod.POST)
	@LoginRequired
	@ResponseStatus(value = HttpStatus.OK)
	public void managerAddOrganization(@RequestBody OrganizationManagerVo orgManagerVo) throws Exception {

		if (AssertUtil.isNull(orgManagerVo.getOrganization()) || (OrganizationTypeEnum.HOSPITAL
				.getCode() != orgManagerVo.getOrganization().getOrganizationType()
				&& OrganizationTypeEnum.CHANNEL.getCode() != orgManagerVo.getOrganization().getOrganizationType())) {
			throw new HospitalException("站点类型不能为空");
		}

		validateUserExist(orgManagerVo);

		GeneralManagerDto manager = orgManagerVo.getManager();
		OrganizationDto organization = orgManagerVo.getOrganization();
		if (organization.getId() != null) {
			hospitalService.updateOrganization(organization, manager);
		} else {
			if (AssertUtil.isEmpty(organization.getUrl())) {
				throw new SiteException(SiteException.URL_NULL, "网址为空");
			}
			boolean isExist = siteService.checkSiteUrl(null, organization.getUrl());

			if (isExist) {
				throw new SiteException(SiteException.URL_EXIST, "网址已经存在" + organization.getUrl());
			}
			hospitalService.addOrganization(organization, manager);
		}
	}

	private void validateUserExist(OrganizationManagerVo orgManagerVo) throws AccountException {
		GeneralManagerDto manager = orgManagerVo.getManager();
		if (manager.getId() == null) {
			int system = 0;
			if (OrganizationTypeEnum.HOSPITAL.getCode() == orgManagerVo.getOrganization().getOrganizationType()) {
				system = SystemTypeEnum.CRM_LOGIN.getCode();
			} else if (OrganizationTypeEnum.CHANNEL.getCode() == orgManagerVo.getOrganization().getOrganizationType()) {
				system = SystemTypeEnum.CHANNEL_LOGIN.getCode();
			}
			User user = userService.getUserBySystemType(manager.getUsername(), system);
			if (user != null) {
				throw new AccountException(AccountException.USER_NAME_EXIST, manager.getUsername() + "　账号已被使用");
			}
		}
	}

	/**
	 * 获取体检中心基础信息
	 * 
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value = "/organization", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public Map<String, Object> organizationBaseInfo(@RequestParam("hospitalId") Integer hospitalId) {
		OrganizationDto dto = hospitalService.getOrganizationBaseInfo(hospitalId);
		Account account = accountService.getHospitalDefaultManager(hospitalId);
		ManagerVo manager = new ManagerVo();
		if (account != null) {
			BeanUtils.copyProperties(account, manager);
			AccountInfo info = accountInfoService.getAccountInfo(account.getId());
			if (info != null) {
				manager.setGender(info.getGender());
			}
			List<User> userList = userService.getUserListByAccountId(account.getId());
			if (AssertUtil.isNotEmpty(userList)) {
				manager.setUsername(userList.get(0).getUsername());
			}
		}

		Map<String, Object> map = Maps.newHashMap();
		if(dto.getAddress() != null && dto.getAddress().getId() != null){
			Integer[] addressIds = getAddressIds(dto.getAddress().getId());
			map.put("addressIds", addressIds);
		}

		map.put("organizationVo", dto);
		map.put("managerVo", manager);
		return map;
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
	 * 更新机构状态
	 * 
	 * @param hospitalId
	 * @param state
	 */
	@RequestMapping(value = "/siteOpt", method = RequestMethod.POST)
	@LoginRequired
	@ResponseStatus(value = HttpStatus.OK)
	public void updateOrganization(@RequestParam("hospitalId") Integer hospitalId, @RequestParam("state") int state) {
		hospitalService.updateOrganizationState(hospitalId, state);
	}

	/**
	 * 验证url
	 * 
	 * @param url
	 * @param siteId
	 * @return
	 */
	@RequestMapping(value = "/validateSite", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public int checkUrl(@RequestParam("url") String url,
			@RequestParam(value = "siteId", required = false) Integer siteId) {
		boolean isExist = siteService.checkSiteUrl(siteId, url);
		if (isExist) {
			return 0;
		}
		return 1;
	}

	/**
	 * 验证账户重复性
	 * 
	 * @param userName
	 * @throws AccountException
	 */
		/*

	@RequestMapping(value = "/validateUserName", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public int checkUserName(@RequestParam("userName") String userName,
			@RequestParam("organizationType") Integer organizationType) throws AccountException {

		Integer sysTem;
		if (OrganizationTypeEnum.HOSPITAL.getCode().equals(organizationType)) {
			sysTem = SystemTypeEnum.CRM_LOGIN.getCode();
		} else if (OrganizationTypeEnum.CHANNEL.getCode().equals(organizationType)) {
			sysTem = SystemTypeEnum.CHANNEL_LOGIN.getCode();
		} else {
			throw new AccountException(AccountException.SYSTEM_LOGIN_TYPE_EMPTY, "站点类型不能为空");
		}

		User user = userService.getUserBySystemType(userName, sysTem);
		if (user != null) {
			return 0;
		}
		return 1;
	}
	*/

	/**
	 * 获取体检中心设置信息
	 * 
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value = "/organizationSetting", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public Map<String, Object> getSettings(@RequestParam("hospitalId") Integer hospitalId) {
		HospitalSettings setting = hospitalService.getHospitalSettingsById(hospitalId);
		setting.setCalculatorService(
				String.valueOf(CalculatorServiceEnum.getCodeByName(setting.getCalculatorService())));
		Hospital hospital = hospitalService.getHospitalById(hospitalId); // 进入页面查询tb_hospital的show_in_list状态字段
		List<HospitalPeriodSetting> periodList = hospitalService.getHospitalPeriod(hospitalId);
		Map<String, Object> map = Maps.newHashMap();
		map.put("setting", setting);
		map.put("period", periodList);
		map.put("hospital", hospital);
		return map;
	}

	@RequestMapping(value = "/organizationSetting", method = RequestMethod.POST)
	@LoginRequired
	@ResponseBody
	public void saveSetting(@RequestBody HospitalSettingDto settingDto, HttpSession session) {
		Employee employee = (Employee) SecurityUtils.getSubject().getPrincipal();
		HospitalSettings setting = new HospitalSettings();
		settingDto.setCalculatorService(
				CalculatorServiceEnum.getNameByCode(Integer.valueOf(settingDto.getCalculatorService())));
		BeanUtils.copyProperties(settingDto, setting, "periodList");
		hospitalService.updateOrganizationSetting(setting);
		hospitalService.updateHospitalPeriod(settingDto.getHospitalId(), settingDto.getPeriodList());
		hospitalService.updateHospitalShowInListColumn(settingDto.getHospitalId(), settingDto.getShowInList()); // 更改在平台显示状态
		logger.info("event={},bizType={},hospitalId={},operatorId={},operatorName={},reqJson={}",
				"体检中心参数设置成功","体检中心参数设置",settingDto.getHospitalId(),
				(employee != null ? employee.getId() : ""),(employee != null ? employee.getLoginName() : ""),JSON.toJSONString(settingDto));
	}

	/**
	 * 获取省集合
	 * 
	 * @return
	 */
	@RequestMapping(value = "/province", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public List<Address> getProvince() {
		return addressService.getProvince();
	}

	/**
	 * 获取城市集合
	 * 
	 * @param provinceId
	 * @return
	 */
	@RequestMapping(value = "/city", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public List<Address> getCity(@RequestParam("provinceId") Integer provinceId) {
		return addressService.getCitys(provinceId);
	}

	/**
	 * 获取区集合
	 * 
	 * @param cityId
	 * @return
	 */
	@RequestMapping(value = "/district", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public List<Address> getDistrict(@RequestParam("cityId") Integer cityId) {
		return addressService.getDistricts(cityId);
	}

	/**
	 * 获取站点部分资源信息
	 * 
	 * @param hospitalId
	 * @param orgType
	 *            机构类型1：hospital，2：channel
	 * @return
	 */
	@RequestMapping(value = "/siteResource", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public Map<String, Object> siteResource(@RequestParam("hospitalId") Integer hospitalId,
			@RequestParam("orgType") Integer orgType) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<SiteTemplate> mobileTemList = siteTemplateService
				.getGlobalSiteTemplateListByType(TemplateTypeEnum.MOBILE.getCode(), orgType);
		// List<SiteTemplate> pcTemList =
		// siteTemplateService.getGlobalSiteTemplateListByType(TemplateTypeEnum.PC
		// .getCode());
		List<SiteSubWebCss> cssList = siteSubWebCssService
				.getSiteSubWebCssListByType(TemplateTypeEnum.MOBILE.getCode());
		Map<Integer, List<SiteResource>> siteResourceMap = siteResourceService.getSortResourceByHspId(hospitalId);
		resultMap.put("mobileTemList", mobileTemList);
		// resultMap.put("pcTemList", pcTemList);
		resultMap.put("cssList", cssList);
		resultMap.put("mobileMainBanner", siteResourceMap.get(ResourceTypeEnum.MOBILE_MAIN_BANNER.getCode()));
		resultMap.put("mobileDeputyBanner", siteResourceMap.get(ResourceTypeEnum.MOBILE_DEPUTY_BANNER.getCode()));
		List<SiteResource> logos = siteResourceMap.get(ResourceTypeEnum.MOBILE_LOGO.getCode());
		if (AssertUtil.isNotEmpty(logos)) {
			resultMap.put("logo", logos.get(0));
		}
		List<SiteResource> qrCodes = siteResourceMap.get(ResourceTypeEnum.QR_CODE.getCode());
		if (AssertUtil.isNotEmpty(qrCodes)) {
			resultMap.put("qrCode", qrCodes.get(0));
		}
		List<SiteResource> covers = siteResourceMap.get(ResourceTypeEnum.COVER.getCode());
		if (AssertUtil.isNotEmpty(covers)) {
			resultMap.put("cover", covers.get(0));
		}
		resultMap.put("environment", siteResourceMap.get(ResourceTypeEnum.ENVIRONMENT_BIG_IMAGE.getCode()));
		Site site = siteService.getSiteByHospitalId(hospitalId);
		SiteTemplate mobileTem = siteTemplateService.getSiteTemplateById(site.getMobileTemplateId());
		if (mobileTem.getIsCustom()) {
			mobileTemList.add(mobileTem);
		}
		resultMap.put("mobileTem", mobileTem);
		HashMap<Integer, SiteSubWebCss> cssMap = covertListToMap(cssList);
		resultMap.put("css", mobileTem != null ? cssMap.get(mobileTem.getDefaultCssId()) : null);
		// resultMap.put("pcTem",
		// siteTemplateService.getSiteTemplateById(site.getTemplateId()));
		return resultMap;
	}

	/**
	 * 上传多张图片
	 * 
	 * @param siteResourceVo
	 * @param session
	 * @throws Exception
	 */
//	@RequestMapping(value = "/siteResource", method = RequestMethod.POST)
//	@LoginRequired
//	@ResponseStatus(value = HttpStatus.OK)
//	@Token
//	public void siteResource(@ModelAttribute(value = "siteResourceVo") SiteResourceVo siteResourceVo,
//			HttpSession session) throws Exception {
//		String tmpFileDir = session.getServletContext().getRealPath("/");
//		List<SiteResource> siteResourceList = JSON.parseArray(siteResourceVo.getImageList(), SiteResource.class);
//		Map<Integer, List<File>> fileMap = getFileList(tmpFileDir, siteResourceVo);
//		siteService.updateSiteResource(siteResourceVo.getHospitalId(), siteResourceVo.getMobileTemplateId(),
//				siteResourceVo.getCssId(), fileMap, siteResourceList);
//	}

	/**
	 * 上传多张图片
	 *
	 * @param siteResourceVo
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping(value = "/siteResource", method = RequestMethod.POST)
	@LoginRequired
	@ResponseStatus(value = HttpStatus.OK)
	@Token
	public void siteResource(@ModelAttribute(value = "siteResourceVo") SiteResourceVo siteResourceVo,
							 HttpSession session) throws Exception {
		String tmpFileDir = session.getServletContext().getRealPath("/");
		List<SiteResource> siteResourceList = JSON.parseArray(siteResourceVo.getImageList(), SiteResource.class);
		Map<Integer, List<File>> fileMap = getFileList(tmpFileDir, siteResourceVo);

		addSiteResource(fileMap, siteResourceList);

		siteService.updateSiteResource(siteResourceVo.getHospitalId(), siteResourceVo.getMobileTemplateId(),
				siteResourceVo.getCssId(), siteResourceList);
	}

	private void addSiteResource(Map<Integer, List<File>> fileMap, List<SiteResource> siteResList) {
		try {
			ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
			Map<Integer, List<SiteResource>> classResourceMap = siteResList.stream().collect(Collectors.groupingBy(SiteResource::getType));
			classResourceMap.forEach((key, valueList) -> {
				Map<String, File> addFileMap = null;
				if(fileMap != null){
					List<File> fileList = fileMap.get(key);
					if(fileList != null && fileList.size() > 0){
						addFileMap = fileList.stream().collect(Collectors.toMap(File::getName, val -> val));
					}
				}
				for(int i=0; i < valueList.size(); i++){
					SiteResource sr = valueList.get(i);
					File file = addFileMap == null ? null : addFileMap.get(sr.getName());

					addSiteResource(file, sr);

				}

			});
		} finally {
			ossClient.shutdown();
		}
	}

	private void addSiteResource(File file, SiteResource sr){
		if(sr.getHospitalId() == null){
			return;
		}
		String imgPath = "hospital/" + sr.getHospitalId();
		if(sr.getType().equals(ResourceTypeEnum.MOBILE_MAIN_BANNER.getCode())){
			imgPath += ImageService.MOBILE_MAIN_BANNER_DIR + sr.getName();
		}
		if(sr.getType().equals(ResourceTypeEnum.MOBILE_DEPUTY_BANNER.getCode())){
			imgPath += ImageService.MOBILE_DEPUTY_BANNER_DIR + sr.getName();
		}
		if(sr.getType().equals(ResourceTypeEnum.PC_LOGO.getCode())){
			imgPath += "/" + sr.getName();
		}
		if(sr.getType().equals(ResourceTypeEnum.PC_BANNER.getCode())){
			imgPath += ImageService.PC_BANNER + sr.getName();
		}
		if(sr.getType().equals(ResourceTypeEnum.MOBILE_LOGO.getCode())){
			imgPath += "/" + sr.getName();
		}
		if(sr.getType().equals(ResourceTypeEnum.QR_CODE.getCode())){
			imgPath += "/" + sr.getName();
		}
		if(sr.getType().equals(ResourceTypeEnum.ENVIRONMENT_BIG_IMAGE.getCode())){
			imgPath += ImageService.BIG_DIR + sr.getName();
		}
		if(sr.getType().equals(ResourceTypeEnum.COVER.getCode())){
			imgPath += ImageService.COVER + sr.getName();
		}
		sr.setValue(imgPath);
		if (file != null) {
			ossClient.putObject(bucket, sr.getValue(), file);
		}
	}

	private Map<Integer, List<File>> getFileList(String tmpFileDir, SiteResourceVo vo) throws Exception {

		Map<Integer, List<File>> resultMap = Maps.newHashMap();
		resultMap.put(ResourceTypeEnum.MOBILE_MAIN_BANNER.getCode(),
				transferToFile(tmpFileDir, vo.getMobileMainBannerList()));
		resultMap.put(ResourceTypeEnum.MOBILE_DEPUTY_BANNER.getCode(),
				transferToFile(tmpFileDir, vo.getMobileDeputyBannerList()));
		resultMap.put(ResourceTypeEnum.MOBILE_LOGO.getCode(),
				transferToFile(tmpFileDir, vo.getLogo() != null ? Collections.singletonList(vo.getLogo()) : null));
		resultMap.put(ResourceTypeEnum.QR_CODE.getCode(),
				transferToFile(tmpFileDir, vo.getQrCode() != null ? Collections.singletonList(vo.getQrCode()) : null));
		resultMap.put(ResourceTypeEnum.COVER.getCode(),
				transferToFile(tmpFileDir, vo.getCover() != null ? Collections.singletonList(vo.getCover()) : null));
		resultMap.put(ResourceTypeEnum.ENVIRONMENT_BIG_IMAGE.getCode(),
				transferToFile(tmpFileDir, vo.getEnvironmentList()));
		return resultMap;
	}

	private List<File> transferToFile(String tmpFileDir, List<MultipartFile> list) throws Exception {
		List<File> fileList = Lists.newArrayList();
		if (AssertUtil.isEmpty(list)) {
			return fileList;
		}
		for (MultipartFile file : list) {
			String filePath = file.getOriginalFilename();
			if (filePath.length() > 20) {
				throw new Exception(filePath + "名称不能超过20个字符");
			}
			File newFile = new File(tmpFileDir + filePath);
			file.transferTo(newFile);
			fileList.add(newFile);
		}

		return fileList;
	}

	@RequestMapping(value = "/colorRule", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public Map<String, Object> getCss() {
		Map<String, Object> map = Maps.newHashMap();
		String cssVal = cssService.getColorRule();
		List<SiteColor> colorList = cssService.getSiteColor();
		map.put("rule", cssVal);
		map.put("color", colorList);
		return map;
	}

	/**
	 * 获取色调模板
	 * 
	 * @return
	 */
	@RequestMapping(value = "/webCss", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public Map<String, Object> webCss() {
		List<SiteSubWebCss> list = cssService.getWebCss();
		Map<String, SiteColor> colorList = cssService.getSiteColor().stream()
				.collect(Collectors.toMap(SiteColor::getName, val -> val, (oldVal, newVal) -> oldVal));
		Map<String, Object> map = Maps.newHashMap();
		map.put("css", list);
		map.put("color", colorList);
		return map;
	}

	/**
	 * 保存色调模板
	 * 
	 * @param css
	 * @throws Exception
	 */
	@RequestMapping(value = "/webCss", method = RequestMethod.POST)
	@LoginRequired
	@ResponseStatus(value = HttpStatus.OK)
	public void addWebCss(@RequestBody SiteSubWebCss css) throws Exception {
		if (AssertUtil.isEmpty(css.getName())) {
			throw new Exception("模板名称不能为空");
		}
		if (css.getId() != null) {
			boolean result = cssService.updateCssTemplate(css);
			if (!result) {
				throw new Exception("模板名称已经存在");
			}
		} else {
			css.setType(TemplateTypeEnum.MOBILE.getCode());
			boolean result = cssService.addCssTemplate(css);
			if (!result) {
				throw new Exception("模板名称已经存在");
			}
		}
	}

	/**
	 * 删除模板
	 * 
	 * @param cssId
	 * @throws Exception
	 */
	@RequestMapping(value = "/delColorTem/{cssId}", method = RequestMethod.DELETE)
	@LoginRequired
	@ResponseStatus(value = HttpStatus.OK)
	public void deleteWebCss(@PathVariable Integer cssId) throws Exception {
		boolean result = cssService.deleteCssTemplate(cssId);
		if (!result) {
			throw new Exception("该模板已经被引用，无法删除");
		}
	}

	/**
	 * 保存色调规则
	 * 
	 * @param cssRule
	 * @throws Exception
	 */
	@RequestMapping(value = "/colorRule", method = RequestMethod.POST)
	@LoginRequired
	@ResponseStatus(value = HttpStatus.OK)
	public void saveColorRule(@RequestParam("cssRule") String cssRule) throws Exception {
		if (AssertUtil.isEmpty(cssRule)) {
			throw new Exception("规则不能为空");
		}
		cssService.saveColorRule(cssRule);
	}

	private HashMap<Integer, SiteSubWebCss> covertListToMap(List<SiteSubWebCss> list) {
		HashMap<Integer, SiteSubWebCss> map = Maps.newHashMap();
		list.forEach(x -> map.put(x.getId(), x));
		return map;
	}

	/**
	 * 进入给渠道商分配医院界面
	 * 
	 * @param organizationId
	 * @return
	 */
	@RequestMapping(value = "/allocateHospital", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public Object allocateHospitalToMediator(
			@RequestParam(value = "organizationId", required = true) Integer organizationId) throws Exception {

		List<OrganizationHospitalRelation> list = hospitalService.getRelationByOrganizationId(organizationId);
		if (list.size() == 0 || (list.size() > 0 && list.get(0).getStatus() == 2)) {// 渠道商现在支持所有体检中心
			return "all";
		}
		if (list.size() == 1 && list.get(0).getHospitalId() == -1) {// 渠道商现在受控制，且不支持任何体检中心
			return null;
		}
		if (list.size() > 0 && list.get(0).getStatus() == 1) {// 渠道商现在支持部分体检中心
			List<Integer> list2 = list.stream().map(item -> item.getHospitalId()).collect(Collectors.toList());
			return hospitalService.getOrganizationRelationByHospitalIds(list2);
		}
		return null;
	}

	/**
	 * 切换渠道商支持体检中心的状态
	 * 
	 * @param status
	 * @param organizationId
	 * @return
	 */
	@RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
	@LoginRequired
	@ResponseBody
	public Object changeOrganizationControlStatus(@RequestParam(value = "status") Integer status,
			@RequestParam(value = "organizationId") Integer organizationId) {
		hospitalService.changeHospitalAllocationStatus(organizationId, status);
		if (status == OrganizationAllocationStatusEnum.ALL.getStatus()) {
			return "all";
		}
		if (status == OrganizationAllocationStatusEnum.ALLOCATION.getStatus()) {
			List<OrganizationHospitalRelation> list = hospitalService.getRelationByOrganizationId(organizationId);
			List<Integer> hospitalIds = list.stream().filter(relation -> relation.getHospitalId() > 0)
					.map(item -> item.getHospitalId()).collect(Collectors.toList());
			if (AssertUtil.isNotEmpty(hospitalIds)) {
				return hospitalService.getOrganizationRelationByHospitalIds(hospitalIds);
			}
		}
		return null;
	}

	/**
	 * 按地区和对接方式查询体检中心
	 * 
	 * @param addressId
	 * @param exportWithXls
	 * @return
	 */
	@RequestMapping(value = "/queryHospitalByAddressAndExportWithXls", method = RequestMethod.GET)
	@LoginRequired
	@ResponseBody
	public List<OrganizationHospitalRelationDto> queryHospitalByAddressAndExportWithXls(
			@RequestParam(value = "addressId", required = false) Integer addressId,
			@RequestParam(value = "exportWithXls", required = false) Integer exportWithXls,
			@RequestParam(value = "organizationId", required = true) Integer organizationId) {

		List<OrganizationHospitalRelation> list = hospitalService.getRelationByOrganizationId(organizationId);

		List<Integer> list2 = list.stream().map(item -> item.getHospitalId()).collect(Collectors.toList());

		return hospitalService.getHospitalsByAddressAndExportWithXls(organizationId, addressId, exportWithXls, list2);
	}

	/**
	 * 添加渠道商支持的体检中心
	 * 
	 * @param organizationId
	 * @param hospitalIds
	 */
	@RequestMapping(value = "/addAssociate", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	@LoginRequired
	public void addAssociate(@RequestParam("organizationId") Integer organizationId,
			@RequestParam("hospitalId[]") Integer[] hospitalIds) {

		List<OrganizationHospitalRelation> list = hospitalService.getRelationByOrganizationId(organizationId);

		if (list.size() == 1 && list.get(0).getHospitalId() == -1) {// 渠道商以前受控制，且不支持任何体检中心
			hospitalService.removeAllRelation(organizationId);
		}

		for (Integer hospitalId : hospitalIds) {
			List<OrganizationHospitalRelation> relationlist = hospitalService
					.getRelationsByOrganizationIdAndHospitalId(organizationId, hospitalId);
			if (relationlist.size() == 0) {// 判断关系表中是否有相同记录，防止添加重复记录
				hospitalService.addAllocationRelation(organizationId, hospitalId);
			}
		}
	}

	/**
	 * 删除渠道商和体检中心的关联关系
	 * 
	 * @param organizationId
	 * @param hospitalId
	 */
	@RequestMapping(value = "/removeAssociate", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	@LoginRequired
	public void removeAssociate(@RequestParam("organizationId") Integer organizationId,
			@RequestParam("hospitalId") Integer hospitalId) {

		hospitalService.removeRelation(organizationId, hospitalId);
		// 如果删除了某个渠道商关联的的所有体检中心,则添加一条受控制记录
		List<OrganizationHospitalRelation> list = hospitalService.getRelationByOrganizationId(organizationId);
		if (list.size() == 0) {
			hospitalService.addAllocationRelation(organizationId, -1);
		}
	}

	/**
	 * 删除渠道商和体检中心的关联关系
	 * 
	 * @param organizationId
	 */
	@RequestMapping(value = "/removeAllAssociate", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	@LoginRequired
	public void removeAllAssociate(@RequestParam("organizationId") Integer organizationId) {

		hospitalService.removeAllRelation(organizationId);
		// insert one -1 hospital
		hospitalService.addAllocationRelation(organizationId, -1);
	}

	/**
	 * 获取体检中心消息配置列表
	 * 
	 * @param hospitalId
	 **/
	/*
	@RequestMapping(value = "/hospitalsms", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
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
*/
	/**
	 * 保存体检中心消息配置列表
	 * 
	 * @param smsList
	 **/
	/*
	@RequestMapping(value = "/hospitalsms", method = RequestMethod.POST)
	@ResponseBody
	@LoginRequired
	public void saveHospitalSms(@RequestBody List<SmsTemplate> smsList, HttpSession session) {
		Employee employee = (Employee) SecurityUtils.getSubject().getPrincipal();
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
		logger.info("event={},bizType={},operatorId={},operatorName={},reqJson={}","医院消息发送方式设置"+ resp.isSuccess(),"医院消息发送方式设置",
				(employee != null ? employee.getId() : ""),(employee != null ? employee.getLoginName() : ""),JSON.toJSONString(smsList));
	}
	*/
	/**
	 * 获取所有的渠道商列表
	 * @return
	 */
	@RequestMapping(value = "/channelList", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public List<Hospital> getChannelList(){
		List<Hospital> resultList = hospitalService.getOrganizationList(OrganizationTypeEnum.CHANNEL.getCode());
		return resultList;	
	}
}
