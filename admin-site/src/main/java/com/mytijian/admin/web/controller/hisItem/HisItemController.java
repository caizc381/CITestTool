package com.mytijian.admin.web.controller.hisItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.admin.web.vo.hisItem.HisItemMappingVO;
import com.mytijian.common.util.DateUtils;
import com.mytijian.report.dto.HisItemQueryDto;
import com.mytijian.report.model.hisitem.HisItem;
import com.mytijian.report.model.hisitem.HisItemResult;
import com.mytijian.report.model.hisitem.HisItemStandard;
import com.mytijian.report.model.hisitem.HisItemStandardResult;
import com.mytijian.report.model.hisitem.ItemResult;
import com.mytijian.report.model.hisitem.Unit;
import com.mytijian.report.service.hisitem.HisItemService;
import com.mytijian.report.service.hisitem.HisItemStandardService;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.util.AssertUtil;

@Controller
public class HisItemController {

	private final static Logger logger = LoggerFactory.getLogger(HisItemController.class);
	
	@Resource
	private HisItemService hisItemService;

	@Resource
	private HisItemStandardService hisItemStandardService;
	
	@Resource
	private HospitalService hospitalService;
	
    @Value("${temp.folder}")
    private String tempFolder;

	/**
	 * 刷新页面，获取数据
	 **/
	@GetMapping("/reloadHisitemPage")
	@ResponseBody
	public Map<String, Object> reloadHisitemPage(int hospitalId, String keywords, Boolean isRelevance) {
		Map<String, Object> resultMap = Maps.newHashMap();
		//获取标准项列表
		List<HisItemStandard> itemStandardList = hisItemStandardService.listHisItemStandard(keywords);
		if (AssertUtil.isEmpty(itemStandardList)) {
			return resultMap;
		}
		List<Integer> standardIds = Lists.newArrayList();
		Map<Integer, HisItem> hisItemMap = getHisItemMap(hospitalId, itemStandardList, standardIds);
		if(isRelevance == null || isRelevance){
			resultMap.put("hisItemMap", hisItemMap);
		}
		if(isRelevance != null && isRelevance){
			itemStandardList = itemStandardList.stream().filter(standard -> {
				return standardIds.contains(standard.getId());
			}).collect(Collectors.toList());
		}
		if(isRelevance != null && !isRelevance){
			itemStandardList = itemStandardList.stream().filter(standard -> {
				return !standardIds.contains(standard.getId());
			}).collect(Collectors.toList());
		}
		resultMap.put("standardList", itemStandardList);
		return resultMap;
	}


	private Map<Integer, HisItem> getHisItemMap(int hospitalId, List<HisItemStandard> itemStandardList, List<Integer> standardIds) {
		Map<Integer, HisItem> hisItemMap = null;
		List<Integer> hisItemStandardIds = itemStandardList.stream().map(standard -> standard.getId())
				.collect(Collectors.toList());
		HisItemQueryDto query = new HisItemQueryDto();
		query.setHisItemStandardIds(hisItemStandardIds);
		query.setHospitalId(hospitalId);
		query.setIsRelevance(true);
		//获取映射的体检小项列表
		List<HisItem> hisItemList = hisItemService.listHisItems(query);
		if (AssertUtil.isNotEmpty(hisItemList)) {
			hisItemMap = hisItemList.stream()
					.map(item -> {
						standardIds.add(item.getHisItemStandardId());
						return item;
					})
					.collect(Collectors.toMap(HisItem::getHisItemStandardId, 
							val -> val, (oldV, newV) -> oldV));
		}
		return hisItemMap;
	}
	
	
	/**
	 * 获取标准项详情
	 * @param hisItemStandardId
	 * @return
	 */
	@GetMapping("/hisItemStandardInfo")
	@ResponseBody
	public HisItemStandard hisItemStandardInfo(int hisItemStandardId){
		return hisItemStandardService.getHisItemStandard(hisItemStandardId);
	}
	
	/**
	 * 获取医院体检小项详情
	 * @param hisItemId
	 * @return
	 */
	@GetMapping("/hisItemInfo")
	@ResponseBody
	public HisItem hisItemInfo(int hisItemId){
		return hisItemService.getHisItemByHisItemId(hisItemId);
	}
	
	/**
	 * 搜索体检中心小项
	 * @param hospitalId
	 * @param keywords
	 * @return
	 */
	@GetMapping("/listHisItem")
	@ResponseBody
	public List<HisItem> getHisItemList(int hospitalId, String keywords){
		HisItemQueryDto query = new HisItemQueryDto();
		query.setHospitalId(hospitalId);
		query.setKeywords(keywords);
		return hisItemService.listHisItems(query);
	}
	
	/**
	 * 体检小项映射
	 * @param hisItemMapping
	 */
	@PostMapping("/mappingHisItem")
	@ResponseStatus(value = HttpStatus.OK)
	public void mappingHisItem(@RequestBody HisItemMappingVO hisItemMapping){
		if(hisItemMapping.getIsMappingAdvance() || hisItemMapping.getIsMappingDetail() || 
			hisItemMapping.getIsMappingMaxResult() || hisItemMapping.getIsMappingMinResult() ||
			hisItemMapping.getIsMappingUnit()){
			HisItem hisItem = hisItemService.getHisItemByHisItemId(hisItemMapping.getHisItemId());
			HisItemStandard standard = hisItemStandardService.getHisItemStandard(hisItemMapping.getHisItemStandardId());
			List<HisItemResult> resultList = getItemResultList(hisItemMapping, hisItem, standard);
			hisItem.setResultList(resultList);
			hisItemService.updateHisItem(hisItem);
		}
		hisItemStandardService.mappingHisItemStandard(hisItemMapping.getHospitalId(), 
				hisItemMapping.getHisItemStandardId(), hisItemMapping.getHisItemId());
	}

	@SuppressWarnings("unchecked")
	private List<HisItemResult> getItemResultList(HisItemMappingVO hisItemMapping, HisItem hisItem, HisItemStandard standard) {
		List<HisItemResult> itemResultList = (List<HisItemResult>) hisItem.getResultList();
		List<HisItemStandardResult> itemStandardResultList = (List<HisItemStandardResult>) standard.getResultList();
		Map<Integer, HisItemResult> resultMap = Maps.newHashMap();
		List<HisItemResult> hisResultList = Lists.newArrayList();
		if(AssertUtil.isNotEmpty(itemResultList)){
			itemResultList.forEach(ir -> {
				resultMap.put(ir.getType(), ir);
			});
		} 
		if(hisItemMapping.getIsMappingAdvance()){
			hisItem.setBelowAdvice(standard.getBelowAdvice());
			hisItem.setOverAdvice(standard.getOverAdvice());
			hisItem.setStandardAdvice(standard.getStandardAdvice());
		}
		if(hisItemMapping.getIsMappingDetail()){
			hisItem.setDetail(standard.getDetail());
		}
		if(hisItemMapping.getIsMappingUnit()){
			hisItem.setUnitId(hisItemMapping.getUnitId());
		}
		if(AssertUtil.isNotEmpty(itemStandardResultList)){
			//获取单位转化后的值
			Map<Integer, ItemResult> unitConvertResultMap = getUnitConvert(hisItemMapping, standard);
			itemStandardResultList.forEach(sr -> {
				HisItemResult itemResult = getHiItemResult(hisItemMapping, hisItem.getId(), standard, 
						unitConvertResultMap, resultMap, sr);
                if(itemResult != null){
                    hisResultList.add(itemResult);
                }
			});
		}
		return hisResultList;
	}


	private Map<Integer, ItemResult> getUnitConvert(HisItemMappingVO hisItemMapping, HisItemStandard standard) {
		Map<Integer, ItemResult> itemResultMap = Maps.newHashMap();
		if(standard.getStandardUnit() == null){
			standard.getResultList().forEach(result -> {
				itemResultMap.put(result.getType(), result);
			});
			return itemResultMap;
		}
		Integer unitId = hisItemMapping.getUnitId();
		Integer sourceUnitId = standard.getStandardUnit().getId();
		if(sourceUnitId == null || (unitId != null && !unitId.equals(sourceUnitId))){
			hisItemStandardService.unitConvert(standard.getId(), 
					sourceUnitId, unitId).forEach(result -> {
						itemResultMap.put(result.getType(), result);
					});;
		}
		return itemResultMap;
	}


	private HisItemResult getHiItemResult(HisItemMappingVO hisItemMapping, Integer hisItemId, 
			HisItemStandard standard, Map<Integer, ItemResult> unitConvertResultMap,
			Map<Integer, HisItemResult> resultMap, HisItemStandardResult sr) {
		HisItemResult itemResult = resultMap.get(sr.getType());
		if(itemResult == null){
			itemResult = new HisItemResult();
		} 
		itemResult.setHisItemId(hisItemId);
		itemResult.setType(sr.getType());
		Unit standardUnit = standard.getStandardUnit();
		if(hisItemMapping.getIsMappingMaxResult()){
			if(standardUnit == null || (!standardUnit.getId().equals(hisItemMapping.getUnitId()))){
				itemResult.setMaxResult(unitConvertResultMap.get(sr.getType()).getMaxResult());
			} else {
				itemResult.setMaxResult(sr.getMaxResult());
			}
		}
		if(hisItemMapping.getIsMappingMinResult()){
			if(standardUnit == null || (!standardUnit.getId().equals(hisItemMapping.getUnitId()))){
				itemResult.setMinResult(unitConvertResultMap.get(sr.getType()).getMinResult());
			} else {
				itemResult.setMinResult(sr.getMinResult());
			}
		}
        if(hisItemMapping.getIsMappingMaxResult() || hisItemMapping.getIsMappingMinResult()){
            return itemResult;
        } else {
            return null;
        }
	}
	
	/**
	 * 取消标准小项映射
	 * @param hisItemStandardId
	 */
	@PostMapping("/cancelMappingHisItem")
	@ResponseStatus(value = HttpStatus.OK)
	public void cancelMappingHisItem(@RequestBody HisItemMappingVO hisItemMapping){
		hisItemStandardService.cancelMappingHisItemStandard(hisItemMapping.getHospitalId(), 
				hisItemMapping.getHisItemStandardId());
	}
	

	/**
	 * 导出体检中心存在多个单位的小项
	 * @param hospitalId
	 * @throws IOException 
	 */
	@GetMapping("/exportHisItemUnit")
	@ResponseBody
	public void exportHisItemUnit(@RequestParam("hospitalId")int hospitalId, 
			HttpServletRequest request, HttpServletResponse response) throws IOException{
		HisItemQueryDto query = new HisItemQueryDto();
		query.setHospitalId(hospitalId);
		List<HisItem> hisItemList = hisItemService.listHisItems(query);
		hisItemList = hisItemList.stream()
				.filter(item -> AssertUtil.isNotEmpty(item.getHisUnit()) && item.getHisUnit().contains(","))
				.collect(Collectors.toList());
		InputStream stream = getClass().getClassLoader()
				.getResourceAsStream("examreportTemplate/examreport_different_unit_template.xls");
		HSSFWorkbook workbook = new HSSFWorkbook(stream);
		HSSFSheet sheet = workbook.getSheetAt(0);
		int row = 0;
		if(AssertUtil.isNotEmpty(hisItemList)){
			for(HisItem item : hisItemList){
				row += 1;
				HSSFRow hssfrow = sheet.createRow(row);	
				hssfrow.createCell(0).setCellValue(item.getName());
				hssfrow.createCell(1).setCellValue(item.getHisItemCode());
				hssfrow.createCell(2).setCellValue(item.getHisUnit());
			}
		}
		Hospital hospital = hospitalService.getHospitalById(hospitalId);
		String fileName = hospital.getName() + "(" + hospitalId + ")" + "不同单位小项" + 
					DateUtils.format(DateUtils.YYYYMMDDSS, new Date()) + ".xls";
		FileOutputStream fileOutputStream = new FileOutputStream(tempFolder + fileName);
		workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        String origin = response.getHeader("Access-Control-Allow-Origin");
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=" + 
        		new String((fileName).getBytes("GBK"), "ISO-8859-1"));
        response.addHeader("Access-Control-Allow-Credentials","true");
        response.addHeader("Access-Control-Allow-Origin",origin);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        try {
            OutputStream outputStreamo = response.getOutputStream();
            FileInputStream fInputStream = new FileInputStream(new File(tempFolder + fileName));
            IOUtils.copy(fInputStream, outputStreamo);

            outputStreamo.flush();
            outputStreamo.close();
            fInputStream.close();
            stream.close();
        } catch (Exception e) {
            logger.error("体检中心多单位小项导出失败", e);
        } finally {
            FileUtils.forceDelete(new File(tempFolder + fileName));
        }
	}
	
	/**
	 * 单位转化
	 * @param standardId
	 * @param sourceUnitId
	 * @param targetUnitId
	 * @return
	 */
	@GetMapping("/unitConvert")
	@ResponseBody
	public List<ItemResult> unitConvert(int standardId, int sourceUnitId, int targetUnitId){
		return hisItemStandardService.unitConvert(standardId, sourceUnitId, targetUnitId);
	}
}
