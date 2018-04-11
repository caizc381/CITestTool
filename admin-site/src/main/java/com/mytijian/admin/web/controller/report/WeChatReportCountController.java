package com.mytijian.admin.web.controller.report;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.plugins.Page;
import com.mytijian.mediator.report.api.common.QueryBaseValue;
import com.mytijian.mediator.report.api.common.QueryOrganizationAddressValue;
import com.mytijian.mediator.report.api.common.QueryWechatDateByTimeDimensionalityValue;
import com.mytijian.mediator.report.service.AreaService;
import com.mytijian.mediator.report.service.OrganizationAddressService;
import com.mytijian.mediator.report.service.WeChatReportCountService;
import com.mytijian.mediator.report.vo.AreaVo;
import com.mytijian.mediator.report.vo.OrganizationAddressVo;
import com.mytijian.mediator.report.vo.WeChatAreaDimensionalityVo;
import com.mytijian.mediator.report.vo.WeChatOrgaDimensionalityVo;
import com.mytijian.mediator.report.vo.WeChatTimeDimensionalityVo;


@RestController
@RequestMapping("/report/wechat")
public class WeChatReportCountController {
	
	private static final Integer DEFAULT_SIZE = 100;
	private static final Integer DEFAULT_CURRENT_PAGE = 1;
	
	@Resource(name = "weChatReportCountService")
    private WeChatReportCountService weChatReportCountService;
	
	@Resource(name = "reportAreaService")
	private AreaService areaService;
	
	@Resource(name = "organizationAddressService")
	private OrganizationAddressService organizationAddressService;
	
	@RequestMapping(value="/queryArea")
	@ResponseBody
	public AreaVo queryArea(){
		return areaService.queryAreaInfo();
	}
	
	@RequestMapping(value="/queryOrganizationAddressByArea")
	@ResponseBody
	public List<OrganizationAddressVo> queryOrganizationAddressByArea(@RequestBody(required = false)QueryOrganizationAddressValue queryOrganizationAddressVal){
		return organizationAddressService.queryOrganizationAddressByArea(queryOrganizationAddressVal);
	}
	
	
	@RequestMapping(value="/queryTimeDimensionalityData")
	@ResponseBody
	public Page<WeChatTimeDimensionalityVo> queryTimeDimensionalityData(@RequestBody(required = false)QueryWechatDateByTimeDimensionalityValue timeDemensionality){
		
		Page<WeChatTimeDimensionalityVo> page = new Page<WeChatTimeDimensionalityVo>();
		if(timeDemensionality == null){
			page.setSize(DEFAULT_SIZE);
			page.setCurrent(DEFAULT_CURRENT_PAGE);
		}else{
			if(timeDemensionality.getCountCycle() != null && timeDemensionality.getPageSize() != null && timeDemensionality.getCurrent() != null){
				if("week".equals(timeDemensionality.getCountCycle()) ){
					page.setSize(timeDemensionality.getPageSize()*7);
					page.setCurrent(timeDemensionality.getCurrent());
				}else if("month".equals(timeDemensionality.getCountCycle())){
					page.setSize(timeDemensionality.getPageSize()*30);
					page.setCurrent(timeDemensionality.getCurrent());
				}else{
					page.setSize(timeDemensionality.getPageSize());
					page.setCurrent(timeDemensionality.getCurrent());
				}
			}else{
				page.setSize(DEFAULT_SIZE);
				page.setCurrent(DEFAULT_CURRENT_PAGE);
			}
			
		}
		return weChatReportCountService.queryReportDataByTimeDimensionality(timeDemensionality, page);
	}
	
	@RequestMapping(value="/queryOrgaDimensionalityDate")
	@ResponseBody
	public Page<WeChatOrgaDimensionalityVo> queryOrgaDimensionalityDate(@RequestBody(required = false)QueryBaseValue queryBaseValue){
		
		Page<WeChatOrgaDimensionalityVo> page = new Page<WeChatOrgaDimensionalityVo>();
		if(queryBaseValue == null){
			page.setSize(DEFAULT_SIZE);
			page.setCurrent(DEFAULT_CURRENT_PAGE);
		}else{
			if(queryBaseValue.getPageSize() != null && queryBaseValue.getCurrent() != null){
				page.setSize(queryBaseValue.getPageSize());
				page.setCurrent(queryBaseValue.getCurrent());
			}else{
				page.setSize(DEFAULT_SIZE);
				page.setCurrent(DEFAULT_CURRENT_PAGE);
			}
		}
		
		return weChatReportCountService.queryReportDataByOragDimensionality(queryBaseValue, page);
	}
	
	@RequestMapping(value="/queryAreaDimensionalityDate")
	@ResponseBody
	public Page<WeChatAreaDimensionalityVo> queryAreaDimensionalityDate(@RequestBody(required = false)QueryBaseValue queryBaseValue){
		
		Page<WeChatAreaDimensionalityVo> page = new Page<WeChatAreaDimensionalityVo>();
		if(queryBaseValue == null){
			page.setSize(DEFAULT_SIZE);
			page.setCurrent(DEFAULT_CURRENT_PAGE);
		}else{
			if(queryBaseValue.getPageSize() != null && queryBaseValue.getCurrent() != null){
				page.setSize(queryBaseValue.getPageSize());
				page.setCurrent(queryBaseValue.getCurrent());
			}else{
				page.setSize(DEFAULT_SIZE);
				page.setCurrent(DEFAULT_CURRENT_PAGE);
			}
		}
		
		return weChatReportCountService.queryReportDataByAreaDimensionality(queryBaseValue, page);
	}
	
	@RequestMapping(value="/queryCollectData")
	@ResponseBody
	public WeChatAreaDimensionalityVo queryCollectData(@RequestBody(required = false)QueryBaseValue queryBaseValue){
		return weChatReportCountService.queryCollectData(queryBaseValue);
	}
}
