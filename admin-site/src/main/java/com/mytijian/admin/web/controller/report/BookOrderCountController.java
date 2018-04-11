package com.mytijian.admin.web.controller.report;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.plugins.Page;
import com.mytijian.mediator.report.api.common.QueryBaseValue;
import com.mytijian.mediator.report.service.OrderSourceReportService;
import com.mytijian.mediator.report.vo.OrderSourceVo;

@RestController
@RequestMapping("/report/order")
public class BookOrderCountController {
	
	private static final Integer DEFAULT_SIZE = 100;
	private static final Integer DEFAULT_CURRENT_PAGE = 1;
	
	@Resource(name = "orderSourceReportService")
	private OrderSourceReportService orderSourceReportService;
	
	@RequestMapping(value="/queryTotalorder")
	@ResponseBody
	public OrderSourceVo queryTotalCountData(@RequestBody(required = true)QueryBaseValue queryBaseValue){
		
		return orderSourceReportService.queryTotalCountData(queryBaseValue);
	}
	
	@RequestMapping(value="/queryTimeDimensionalityData")
	@ResponseBody
	public Page<OrderSourceVo> queryTimeDimensionalityData(@RequestBody(required = true)QueryBaseValue queryBaseValue){
		
		Page<OrderSourceVo> page = new Page<OrderSourceVo>();
		Map<String,Object> dynamicAttr= queryBaseValue.getDynamicAttr();
		String countCycle = (String)dynamicAttr.get("countCycle");
		if(countCycle == null || "hours".equals(countCycle)){
			page.setSize(24);
			page.setCurrent(1);
		}else{
			if(queryBaseValue.getPageSize() != null && queryBaseValue.getCurrent() != null){
				if("week".equals(countCycle) ){
					page.setSize(queryBaseValue.getPageSize()*7);
					page.setCurrent(queryBaseValue.getCurrent());
				}else if("month".equals(countCycle)){
					page.setSize(queryBaseValue.getPageSize()*30);
					page.setCurrent(queryBaseValue.getCurrent());
				}else{
					page.setSize(queryBaseValue.getPageSize());
					page.setCurrent(queryBaseValue.getCurrent());
				}
			}else{
				page.setSize(DEFAULT_SIZE);
				page.setCurrent(DEFAULT_CURRENT_PAGE);
			}
		}
		
		return orderSourceReportService.queryOrderDateByTimeDimensionality(queryBaseValue,page);
	}
	
	@RequestMapping(value="/queryOrderDateByOragDimensionality")
	@ResponseBody
	public Page<OrderSourceVo> queryOrderDateByOragDimensionality(@RequestBody(required = true)QueryBaseValue queryBaseValue){
		
		Page<OrderSourceVo> page = new Page<OrderSourceVo>();
		if(queryBaseValue.getPageSize() != null && queryBaseValue.getCurrent() != null){
			page.setSize(queryBaseValue.getPageSize());
			page.setCurrent(queryBaseValue.getCurrent());
		}else{
			page.setSize(DEFAULT_SIZE);
			page.setCurrent(DEFAULT_CURRENT_PAGE);
		}
		
		return orderSourceReportService.queryOrderDateByOragDimensionality(queryBaseValue, page);
	}
	
	@RequestMapping(value="/queryOrderDateByAreaDimensionality")
	@ResponseBody
	public Page<OrderSourceVo> queryOrderDateByAreaDimensionality(@RequestBody(required = true)QueryBaseValue queryBaseValue){
		
		Page<OrderSourceVo> page = new Page<OrderSourceVo>();
		if(queryBaseValue.getPageSize() != null && queryBaseValue.getCurrent() != null){
			page.setSize(queryBaseValue.getPageSize());
			page.setCurrent(queryBaseValue.getCurrent());
		}else{
			page.setSize(DEFAULT_SIZE);
			page.setCurrent(DEFAULT_CURRENT_PAGE);
		}
		
		return orderSourceReportService.queryOrderDateByAreaDimensionality(queryBaseValue, page);
	}
}
