package com.mytijian.admin.web.controller.meal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mytijian.admin.dao.meal.param.MealExport;
import com.mytijian.admin.dao.meal.param.MealsBaseQuery;
import com.mytijian.admin.web.vo.meal.MealBaseVO;
import com.mytijian.admin.web.vo.meal.MealVO;
import com.mytijian.calculate.HospitalCaculateUtil;
import com.mytijian.company.hospital.service.HospitalCompanyService;
import com.mytijian.company.hospital.service.constant.HospitalGuestCompanyEnum;
import com.mytijian.company.hospital.service.model.HospitalCompany;
import com.mytijian.offer.examitem.dto.ExamItemandStandardItemDto;
import com.mytijian.offer.examitem.model.ExamItem;
import com.mytijian.offer.examitem.service.ExamItemService;
import com.mytijian.offer.meal.constant.enums.MealStateEnum;
import com.mytijian.offer.meal.constant.enums.MealTypeEnum;
import com.mytijian.offer.meal.model.Meal;
import com.mytijian.offer.meal.service.MealService;
import com.mytijian.product.item.constant.enums.ExamItemStandardRelationStatusEnum;
import com.mytijian.product.item.model.ExamItemStandardRelation;
import com.mytijian.product.item.service.ExamItemStandardRelationService;
import com.mytijian.product.item.service.ExamItemStandardService;
import com.mytijian.product.meal.model.StandardMeal;
import com.mytijian.product.meal.model.StandardMealExamitem;
import com.mytijian.product.meal.model.StandardMealMapping;
import com.mytijian.product.meal.param.StandardMealQuery;
import com.mytijian.product.meal.param.StandardMealSelector;
import com.mytijian.product.meal.service.StandardMealMappingManagerService;
import com.mytijian.product.meal.service.StandardMealReadService;
import com.mytijian.product.meal.service.StandardMealWriteService;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.model.Address;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.service.AddressService;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.web.intercepter.Token;

@RestController
@RequestMapping("/meal/platform/")
public class PlatformMealController {
	
	private final static Logger log = LoggerFactory.getLogger(PlatformMealController.class);
	
	@Resource(name = "mealService")
	private MealService mealService;
	
	@Resource(name = "examItemService")
    ExamItemService examItemService;

    @Resource(name = "hospitalService")
    private HospitalService hospitalService;
	
	@Resource(name = "hospitalCompanyService")
	private HospitalCompanyService hospitalCompanyService;
	
	@Resource(name = "addressService")
	private AddressService addressService;
	
	@Value("${temp.folder}")
	private String tempFolder;
	
	@Resource(name = "standardMealMappingManagerService")
	private StandardMealMappingManagerService standardMealMappingManagerService;
	
	@Resource(name = "standardMealReadService")
	private StandardMealReadService standardMealReadService;
	
	@Resource(name = "standardMealWriteService")
	private StandardMealWriteService standardMealWriteService;
	
	@Resource(name = "examItemStandardService")
	private ExamItemStandardService examItemStandardService;
	
	@Resource(name = "examItemStandardRelationService")
	private ExamItemStandardRelationService examItemStandardRelationService;
	
	@Resource(name = "mealAssembler")
	private MealAssembler mealAssembler;

	@RequestMapping(value="/mealsBasicInfo",method = { RequestMethod.POST })
	@ResponseBody
	public MealBaseVO listMealsBasicInfo(@RequestBody(required = true) MealsBaseQuery mealsBaseQuery) {

		MealBaseVO mealBaseVO = new MealBaseVO();
		Page page = new Page();
		com.mytijian.base.page.Page sourcePage = mealsBaseQuery.getPage();
		BeanUtils.copyProperties(sourcePage, page);
		List<MealVO> mealVOList = Lists.newArrayList();
		mealBaseVO.setMealVOList(mealVOList);
		if (mealsBaseQuery.getIsSelectedAddress() && CollectionUtils.isEmpty(mealsBaseQuery.getHospitals())) {
			mealBaseVO.setPage(page);
			return mealBaseVO;
		}
		PageView<Meal> mealList = mealService.listMealsWithOrderConditionByPage(mealsBaseQuery.getHospitals(),
				mealsBaseQuery.getTypes(),mealsBaseQuery.getStandardMealId(),page);
		mealBaseVO.setPage(mealList.getPage());
		if (CollectionUtils.isEmpty(mealList.getRecords())) {
			return mealBaseVO;
		}

		for (Meal meal : mealList.getRecords()) {
			MealVO mealVO = new MealVO();
			StandardMealMapping mealMapping=standardMealMappingManagerService.getMealMappingByPlatformMealId(meal.getId(), false, true);
			String standardMealName = null;
			if(mealMapping != null){
				if(mealMapping.getStandardMeal() != null){
					standardMealName = mealMapping.getStandardMeal().getName();
				}
				warpMealToVO(meal, mealVO,mealMapping.getStandardMealId(),standardMealName);
			}else{
				warpMealToVO(meal, mealVO,null,standardMealName);
			}
			mealVOList.add(mealVO);
		}
		return mealBaseVO;
	}
	
	
	private void warpMealToVO(Meal meal, MealVO mealVO,Integer standardMealId, String standardMealName) {
		mealVO.setId(meal.getId());
		mealVO.setName(meal.getName());
		mealVO.setGender(meal.getGender());
		Hospital hospital = hospitalService.getHospitalById(meal.getHospitalId());
		mealVO.setHospitalId(meal.getHospitalId());
		mealVO.setHospitalName(hospital.getName());
		Address address = addressService.getAddressById(hospital.getAddressId());
		if (address != null) {
			mealVO.setAddress(address.getProvince() + address.getCity());
		}
		mealVO.setDisable(meal.getDisable());

		HospitalSettings settings = (HospitalSettings) hospital.getSettings();
		if (settings == null) {
			mealVO.setPurchasePrice(getPirceString(meal.getPurchasePrice()));
			mealVO.setSupplyPrice(getPirceString(meal.getSupplyPrice()));
			mealVO.setSalePrice(getPirceString(meal.getPrice()));
		} else {
			String calculatorService = settings.getCalculatorService();
			Integer purchasePrice = HospitalCaculateUtil.caculateDiscountPrice(calculatorService, 1.0, meal.getPurchasePrice());
			Integer supplyPrice = HospitalCaculateUtil.caculateDiscountPrice(calculatorService, 1.0, meal.getSupplyPrice());
			Integer salePrice = HospitalCaculateUtil.caculateDiscountPrice(calculatorService, 1.0, meal.getPrice());
			
			mealVO.setPurchasePrice(getPirceString(HospitalCaculateUtil.caculateRoundPrice(calculatorService, purchasePrice)));
			mealVO.setSupplyPrice(getPirceString(HospitalCaculateUtil.caculateRoundPrice(calculatorService, supplyPrice)));
			mealVO.setSalePrice(getPirceString(HospitalCaculateUtil.caculateRoundPrice(calculatorService, salePrice)));
		}

		Meal mealwithSetting = mealService.getMealById(meal.getId());
		mealVO.setMealSetting(mealwithSetting.getMealSetting());
		mealVO.setStandardMealId(standardMealId);
		mealVO.setStandardMealName(standardMealName);
	}
	
	private String getPirceString(Integer price) {
		BigDecimal div1 = new BigDecimal(price.toString());
		BigDecimal div2 = new BigDecimal(100);
		return div1.divide(div2, 2, RoundingMode.HALF_UP).toString();
	}
	
	@RequestMapping(value="/exportMeals" ,method = RequestMethod.GET)  // @RequestBody(required = true) ExportParam exportParam
	@ResponseStatus(HttpStatus.OK)
	public void exportMeals(@RequestParam(value = "isExportAll", required = true) boolean isExportAll,@RequestParam(value = "mealIds", required = false) List<Integer> mealIds,
			@RequestParam(value = "hospitalIdList", required = false) List<Integer> hospitalIdList,@RequestParam(value = "cancelMealIds", required = false) List<Integer> cancelMealIds,HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		List<Integer> mealIdList = Lists.newArrayList();
		
		if(CollectionUtils.isEmpty(mealIds)){
			listExportMealIds(hospitalIdList, mealIdList);
		}else{
			mealIdList.addAll(mealIds);
		}
		mealIdList.removeAll(cancelMealIds);
		List<MealExport> mealExportList = Lists.newArrayList();
		listExportMeals(mealIdList, mealExportList);
		
		String fileName = tempFolder +"PlatformMeal_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".xls";
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("platformMeal");
		int rowNum = 0; 
		Row row = sheet.createRow(rowNum++);
		addRowData(row,0,"地区");
		addRowData(row,1,"医院ID");
		addRowData(row,2,"医院名称");
		addRowData(row,3,"平台套餐ID");
		addRowData(row,4,"平台套餐名称");
		addRowData(row,5,"套餐性别");
		addRowData(row,6,"套餐关键词");
		addRowData(row,7,"套餐描述");
		addRowData(row,8,"套餐项");
		addRowData(row,9,"标准套餐ID");
		addRowData(row,10,"标准套餐名称");
		addRowData(row,11,"标准项目");
		addRowData(row,12,"折扣");
		addRowData(row,13,"单项合计");
		addRowData(row,14,"标价");
		addRowData(row,15,"进货价格");
		addRowData(row,16,"供货价格");
		addRowData(row,17,"销售价格");
		addRowData(row,18,"变更时间");
		for(MealExport mealExport : mealExportList){
			row = sheet.createRow(rowNum++);
			row.createCell(0);
			row.getCell(0).setCellValue(mealExport.getAddress());
			row.createCell(1);
			row.getCell(1).setCellValue(mealExport.getHospitalId());
			row.createCell(2);
			row.getCell(2).setCellValue(mealExport.getHospitalName());
			row.createCell(3);
			row.getCell(3).setCellValue(mealExport.getMealId());
			row.createCell(4);
			row.getCell(4).setCellValue(mealExport.getMealName());
			row.createCell(5);
			row.getCell(5).setCellValue(mealExport.getGender());
			row.createCell(6);
			row.getCell(6).setCellValue(mealExport.getKey());
			row.createCell(7);
			row.getCell(7).setCellValue(mealExport.getDescription());
			row.createCell(8);
			row.getCell(8).setCellValue(mealExport.getExamItemName());
			row.createCell(9);
			if(mealExport.getStandardMealId() == null){
				row.getCell(9).setCellValue("");
			}else{
				row.getCell(9).setCellValue(mealExport.getStandardMealId());
			}
			row.createCell(10);
			row.getCell(10).setCellValue(mealExport.getStandardMealName());
			row.createCell(11);
			row.getCell(11).setCellValue(mealExport.getStandardItamName());
			row.createCell(12);
			row.getCell(12).setCellValue(mealExport.getDiscount());
			row.createCell(13);
			row.getCell(13).setCellValue(mealExport.getItemSummation());
			row.createCell(14);
			row.getCell(14).setCellValue(mealExport.getDisplayPrice());
			row.createCell(15);
			row.getCell(15).setCellValue(mealExport.getPurchasePrice());
			row.createCell(16);
			row.getCell(16).setCellValue(mealExport.getSupplyPrice());
			row.createCell(17);
			row.getCell(17).setCellValue(mealExport.getSalePrice());
			row.createCell(18);
			row.getCell(18).setCellValue(mealExport.getUpdateDate());
		}
		workbook.write(fileOutputStream);
		fileOutputStream.flush();
        fileOutputStream.close();
        String name = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename="
                + new String(name.getBytes("GBK"), "ISO-8859-1"));
        
        try {
            OutputStream outputStreamo = response.getOutputStream();
            FileInputStream fInputStream = new FileInputStream(new File(fileName));
            IOUtils.copy(fInputStream, outputStreamo);

            outputStreamo.flush();
            outputStreamo.close();
            fInputStream.close();

        } catch (Exception e) {
            log.error("meal export error", e);
        }
	}
	
	
	private void addRowData(Row row,int index, String value){
		row.createCell(index);
		row.getCell(index).setCellValue(value);
	}
	
	/**
	 *  获取需要导出的套餐ID
	 * @param exportParam
	 * @param mealIdList
	 */
	private void listExportMealIds(List<Integer> hospitalIdList, List<Integer> mealIdList) {
		List<Integer> mealTypes = Lists.newArrayList();
		mealTypes.add(MealTypeEnum.PLATFORM_MEAL.getCode());
		PageView<Meal> pageMealList = mealService.listMealsWithOrderConditionByPage(hospitalIdList, mealTypes,null ,new Page());
		List<Meal> mealList = pageMealList.getRecords();
		if (CollectionUtils.isNotEmpty(mealList)) {
			for (Meal meal : mealList) {
				mealIdList.add(meal.getId());
			}
		}
	}
	
	/**
	 *  获取需要导出的平台套餐完整数据对象
	 * @param mealIdList
	 * @param mealExportList
	 */
	private void listExportMeals(List<Integer> mealIdList, List<MealExport> mealExportList) {
		if (CollectionUtils.isNotEmpty(mealIdList)) {
			for (Integer mealId : mealIdList) {
				Meal meal = mealService.getMealWithItemByMealId(mealId);
				Hospital hospital = hospitalService.getHospitalById(meal.getHospitalId());
				Address address = addressService.getAddressById(hospital.getAddressId());
				List<ExamItem> temexamItemList = meal.getMealExamItemList();
				List<ExamItem> examItemList = temexamItemList.stream().filter(item -> item.isSelected() == true)
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(examItemList)) {

					List<Integer> examItemIdList = Lists.newArrayList();
					StringBuilder examItemNameBuilder = new StringBuilder();
					for (ExamItem examItem : examItemList) {
						examItemIdList.add(examItem.getId());
						examItemNameBuilder.append(examItem.getName());
						examItemNameBuilder.append("\n");
					}
					List<ExamItemandStandardItemDto> examItemandStandardItemDtoList = examItemService
							.getExamItemAndStandardItemRelByExamItemId(examItemIdList);
					StringBuilder standardItemBuilder = new StringBuilder();

					for (Integer examItemId : examItemIdList) {
						for (ExamItemandStandardItemDto examItemandStandardItemDto : examItemandStandardItemDtoList) {
							if (examItemId.equals(examItemandStandardItemDto.getExamItemId())) {
								if (examItemandStandardItemDto.getStandardItemName() == null) {
									standardItemBuilder.append("");
								} else {
									standardItemBuilder.append(examItemandStandardItemDto.getStandardItemName());
								}

								standardItemBuilder.append("\n");
							}

						}
					}

					MealExport mealExport = new MealExport();
					StringBuilder addressBuilder = new StringBuilder();
					addressBuilder.append(address.getProvince());
					addressBuilder.append(address.getCity());
					addressBuilder.append(address.getDistrict());
					mealExport.setAddress(addressBuilder.toString());
					mealExport.setHospitalId(hospital.getId());
					mealExport.setHospitalName(hospital.getName());
					mealExport.setMealId(meal.getId());
					mealExport.setMealName(meal.getName());
					String genderStr = "";
					switch (meal.getGender()) {
					case 0:
						genderStr = "男";
						break;
					case 1:
						genderStr = "女";
						break;
					case 2:
						genderStr = "通用";
						break;
					}
					mealExport.setGender(genderStr);
					mealExport.setKey(meal.getKeyword());
					mealExport.setDescription(meal.getDescription());
					mealExport.setExamItemName(examItemNameBuilder.toString());
					mealExport.setStandardItamName(standardItemBuilder.toString());
					mealExport.setDiscount(meal.getDiscount());

					HospitalSettings settings = (HospitalSettings) hospital.getSettings();
					String calculatorService = settings.getCalculatorService();
					Integer initPrice = HospitalCaculateUtil.caculateDiscountPrice(calculatorService, 1.0,
							meal.getInitPrice());
					mealExport.setItemSummation(
							getPirceString(HospitalCaculateUtil.caculateRoundPrice(calculatorService, initPrice)));
					if (meal.getDisplayPrice() == null) {
						mealExport.setDisplayPrice("");
					} else {
						Integer displayPrice = HospitalCaculateUtil.caculateDiscountPrice(calculatorService, 1.0,
								meal.getDisplayPrice());
						mealExport.setDisplayPrice(getPirceString(
								HospitalCaculateUtil.caculateRoundPrice(calculatorService, displayPrice)));
					}

					Integer purchasePrice = HospitalCaculateUtil.caculateDiscountPrice(calculatorService, 1.0,
							meal.getPurchasePrice());
					mealExport.setPurchasePrice(
							getPirceString(HospitalCaculateUtil.caculateRoundPrice(calculatorService, purchasePrice)));
					Integer supplyPrice = HospitalCaculateUtil.caculateDiscountPrice(calculatorService, 1.0,
							meal.getSupplyPrice());
					mealExport.setSupplyPrice(
							getPirceString(HospitalCaculateUtil.caculateRoundPrice(calculatorService, supplyPrice)));
					Integer salePrice = HospitalCaculateUtil.caculateDiscountPrice(calculatorService, 1.0,
							meal.getPrice());
					mealExport.setSalePrice(
							getPirceString(HospitalCaculateUtil.caculateRoundPrice(calculatorService, salePrice)));
					mealExport.setUpdateDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(meal.getUpdateTime()));
					
					StandardMealMapping mealMapping = standardMealMappingManagerService.getMealMappingByPlatformMealId(mealId, false, true);
					if(mealMapping != null){
						mealExport.setStandardMealId(mealMapping.getStandardMealId());
						mealExport.setStandardMealName(mealMapping.getStandardMeal().getName());
					}
					
					mealExportList.add(mealExport);

				}
			}
		}
	}

	/**
	 * 套餐详情
	 * 
	 * @param mealId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/getMealById", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> getMealById(
			@RequestParam(value = "mealId", required = true) Integer mealId)
			throws JsonProcessingException {
		Meal meal = mealService.getMealById(mealId);
		return mealAssembler.getMealPageInfo(meal);
	}
	
	/**
	 * 根据标准套餐创建平台套餐
	 * 
	 * @param hospitalId
	 * @param standardMealId
	 * @return
	 */
	@RequestMapping(value = "/getByStandardMeal", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> addPlatformMealByStandardMeal(
			@RequestParam(value = "hospitalId", required = true) Integer hospitalId,
			@RequestParam(value = "standardMealId", required = true) Integer standardMealId) {
		StandardMealQuery query = new StandardMealQuery();
		query.setMealIdList(Arrays.asList(standardMealId));
		StandardMealSelector selector = new StandardMealSelector();
		selector.setNeedExamitem(true);
		List<StandardMeal> standardMeals = standardMealReadService
				.listStandardMeal(query, selector);
		if (CollectionUtils.isNotEmpty(standardMeals)) {
			StandardMeal standardMeal = standardMeals.get(0);
			List<StandardMealExamitem> standardMealExamitems = standardMeal
					.getStandardMealItemList();
			

			List<ExamItemStandardRelation> relations =new ArrayList<ExamItemStandardRelation>();
			
			for(StandardMealExamitem  item : standardMealExamitems){
				ExamItemStandardRelation relation = new ExamItemStandardRelation();
				relation.setHospitalId(hospitalId);
				relation.setStandardLibraryId(item.getId());
				relation.setIsRelevance(ExamItemStandardRelationStatusEnum.RELATED.getCode());
				relations.addAll(examItemStandardRelationService
						.listByExamItemStandardRelation(relation));
			}
			
			// key:单项id value:标准套餐的单项
			Map<Integer, StandardMealExamitem> standardMealExamitemmap = new HashMap<>();
			for (ExamItemStandardRelation relation : relations) {
				Optional<StandardMealExamitem> option = standardMealExamitems
						.stream()
						.filter(p -> Objects.equal(p.getId(),
								relation.getStandardLibraryId())).findFirst();

				if (option.isPresent()) {
					standardMealExamitemmap.put(relation.getExamItemId(), option.get());
				}
			}
			return mealAssembler.getMealPageInfo(standardMealExamitemmap, hospitalId);
		}
		return new HashMap<String, Object>();
	}

	/**
	 * 套餐列表
	 * @param hospitalId
	 * @param mealType @see com.mytijian.resource.enums.MealTypeEnum
	 * @return
	 */
	@RequestMapping(value = "/mealList", method = { RequestMethod.POST })
	@ResponseBody
	public PageView<Meal> getMealList(@RequestBody(required = true) MealsBaseQuery mealsBaseQuery) {
		Page page = new Page();
		com.mytijian.base.page.Page sourcePage = mealsBaseQuery.getPage();
		BeanUtils.copyProperties(sourcePage, page);
		if(CollectionUtils.isEmpty(mealsBaseQuery.getHospitals()) || CollectionUtils.isEmpty(mealsBaseQuery.getTypes())){
			return new PageView<Meal>();
		}
		List<Integer> mealDisables = Lists.newArrayList();
		mealDisables.add(MealStateEnum.NORMAL.getCode()); 
		return mealService.listMealByPage(mealsBaseQuery.getTypes(), mealsBaseQuery.getHospitals(),mealDisables, page);
	}


	/**
	 * 复制套餐
	 * @param orgnizationId
	 * @param sourceMealIdList
	 * @throws Exception
	 */
	@RequestMapping(value = "copyMealList", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void copyMealList(@RequestParam(value = "orgnizationId", required = true) Integer orgnizationId,
			@RequestParam(value = "sourceMealIdList", required = true) List<Integer> sourceMealIdList)
			throws Exception {

		// 每天健康单位
		HospitalCompany hospitalCompany = hospitalCompanyService.getHospitalGuestCompany(orgnizationId,
				HospitalGuestCompanyEnum.HOSPITAL_MTJK);
		
		mealService.copyMeal(sourceMealIdList, null,
				MealTypeEnum.PLATFORM_MEAL.getCode(), hospitalCompany.getId());
	}

	/**
	 * 
	 * @param standardMealId为空的时候，表示取消关联
	 * @param platformMealId
	 * @param hospitalId
	 */
	@Token
	@RequestMapping(value = "/addMealMapping", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void addMealMapping(
			@RequestParam(value = "standardMealId", required = false) Integer standardMealId,
			@RequestParam(value = "platformMealId", required = true) Integer platformMealId,
			@RequestParam(value = "hospitalId", required = true) Integer hospitalId) {
		
		//查询平台套餐关联的标准套餐
		StandardMealMapping mealMapping = standardMealMappingManagerService.getMealMappingByPlatformMealId(platformMealId, false, false);
		//若原先未进行关联且此次未传标准套餐ID,则不进行关联
		if(mealMapping== null && standardMealId == null){
			return;
		}
		
		//标准套餐ID不为空，则
		if(standardMealId != null){
			//判断该套餐是否已和该体检中心其他套现进行关联，若已关联，抛出错误
			List<StandardMealMapping> mappingList = standardMealMappingManagerService.listMealMappingByHospitalId(hospitalId, false, false);
			Map<Integer,Integer> mappingMap = new HashMap<Integer,Integer>();
			for(StandardMealMapping mapping : mappingList){
				mappingMap.put(mapping.getStandardMealId(), hospitalId);
			}
			if(mappingMap.get(standardMealId) != null){
				throw new IllegalArgumentException("该标准套餐已被体检中心平台套餐关联");
			}
			standardMealMappingManagerService.addMapping(standardMealId, platformMealId, hospitalId);
		}else{
			// 取消与标准套餐关系
			standardMealMappingManagerService.addMapping(standardMealId, platformMealId, hospitalId);
		}
		
	}
}