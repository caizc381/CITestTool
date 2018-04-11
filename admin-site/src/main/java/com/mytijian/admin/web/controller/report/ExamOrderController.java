package com.mytijian.admin.web.controller.report;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.plugins.Page;
import com.mytijian.mediator.report.api.common.QureyExamOrderValue;
import com.mytijian.mediator.report.service.ExamOrderService;
import com.mytijian.mediator.report.vo.ExamOrderVo;

@RestController
@RequestMapping("/report/examOrder")
public class ExamOrderController {
	
	private static final Integer DEFAULT_SIZE = 100;
	private static final Integer DEFAULT_CURRENT_PAGE = 1;
	
	@Resource(name = "examOrderService")
    private ExamOrderService examOrderService;
	
	@RequestMapping(value="/queryTotalExamOrder")
	@ResponseBody
	public ExamOrderVo queryExamOrderTotalData(@RequestBody(required = true)QureyExamOrderValue qureyExamOrderValue){
		
		transformOrderType(qureyExamOrderValue);
		ExamOrderVo examOrderVo = examOrderService.queryExamOrderTotalData(qureyExamOrderValue);
		
		return examOrderVo;
	}
	
	@RequestMapping(value="/queryExamOrderByTimeDimensionalityData")
	@ResponseBody
	public Page<ExamOrderVo> queryExamOrderByTimeDimensionality(@RequestBody(required = true)QureyExamOrderValue qureyExamOrderValue){
		
		transformOrderType(qureyExamOrderValue);
		Page<ExamOrderVo> page = new Page<ExamOrderVo>();
		Map<String,Object> dynamicAttr= qureyExamOrderValue.getDynamicAttr();
		String countCycle = (String)dynamicAttr.get("countCycle");
		
		if(qureyExamOrderValue.getPageSize() != null && qureyExamOrderValue.getCurrent() != null){
			if("week".equals(countCycle) ){
				page.setSize(qureyExamOrderValue.getPageSize()*7);
				page.setCurrent(qureyExamOrderValue.getCurrent());
			}else if("month".equals(countCycle)){
				page.setSize(qureyExamOrderValue.getPageSize()*30);
				page.setCurrent(qureyExamOrderValue.getCurrent());
			}else{
				page.setSize(qureyExamOrderValue.getPageSize());
				page.setCurrent(qureyExamOrderValue.getCurrent());
			}
		}else{
			page.setSize(DEFAULT_SIZE);
			page.setCurrent(DEFAULT_CURRENT_PAGE);
		}
		
		return examOrderService.queryExamOrderByTimeDimensionality(qureyExamOrderValue, page);
	}
	
	@RequestMapping(value="/queryExamOrderDateByOragDimensionality")
	@ResponseBody
	public Page<ExamOrderVo> queryExamOrderDateByOragDimensionality(@RequestBody(required = true)QureyExamOrderValue qureyExamOrderValue){
		
		transformOrderType(qureyExamOrderValue);
		Page<ExamOrderVo> page = new Page<ExamOrderVo>();
		if(qureyExamOrderValue.getPageSize() != null && qureyExamOrderValue.getCurrent() != null){
			page.setSize(qureyExamOrderValue.getPageSize());
			page.setCurrent(qureyExamOrderValue.getCurrent());
		}else{
			page.setSize(DEFAULT_SIZE);
			page.setCurrent(DEFAULT_CURRENT_PAGE);
		}
		
		return examOrderService.queryExamOrderByOragDimensionality(qureyExamOrderValue, page);
	}
	
	@RequestMapping(value="/queryExamOrderDateByAreaDimensionality")
	@ResponseBody
	public Page<ExamOrderVo> queryExamOrderDateByAreaDimensionality(@RequestBody(required = true)QureyExamOrderValue qureyExamOrderValue){
		
		transformOrderType(qureyExamOrderValue);
		Page<ExamOrderVo> page = new Page<ExamOrderVo>();
		if(qureyExamOrderValue.getPageSize() != null && qureyExamOrderValue.getCurrent() != null){
			page.setSize(qureyExamOrderValue.getPageSize());
			page.setCurrent(qureyExamOrderValue.getCurrent());
		}else{
			page.setSize(DEFAULT_SIZE);
			page.setCurrent(DEFAULT_CURRENT_PAGE);
		}
		
		return examOrderService.queryExamOrderByAreaDimensionality(qureyExamOrderValue, page);
	}
	
	@RequestMapping(value="/queryExamOrderByTypeOfOrderDimensionality")
	@ResponseBody
    public List<ExamOrderVo>  queryExamOrderByTypeOfOrderDimensionality(@RequestBody(required = true)QureyExamOrderValue qureyExamOrderValue){
		transformOrderType(qureyExamOrderValue);
	    return examOrderService.queryExamOrderByTypeOfOrderDimensionality(qureyExamOrderValue);
    }
	
	@RequestMapping(value="/queryExamOrderByOrderModeDimensionality")
	@ResponseBody
	public List<ExamOrderVo> queryExamOrderByOrderModeDimensionality(@RequestBody(required = true)QureyExamOrderValue qureyExamOrderValue){
		transformOrderType(qureyExamOrderValue);
		return examOrderService.queryExamOrderByOrderModeDimensionality(qureyExamOrderValue);
	}
	
	/**
	 * TypeOfOrder 值为零代表全部订单,转换为null，代表不对该类型进行过滤
	 * @param qureyExamOrderValue
	 */
	private void transformOrderType(QureyExamOrderValue qureyExamOrderValue){
		Long tyep = qureyExamOrderValue.getTypeOfOrder();
		if(tyep != null && tyep == 0){
			qureyExamOrderValue.setTypeOfOrder(null);
		}
	}
}
