/**
 * 
 */
package com.mytijian.admin.web.controller.trade.counter;

import com.mytijian.counter.base.service.CounterQueryService;
import com.mytijian.offer.examitem.service.CountExamItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ren
 *
 */
@Controller
@RequestMapping("/hospitalcounter")
public class HospitalCounterController {

	@Resource(name = "countExamItemService")
	private CountExamItemService countExamItemService;

	@Resource
	private CounterQueryService counterQueryService;

	/**
	 * 获取体检中心时间段和受限项目设置
	 * 
	 * @param hospitalId
	 * @param session
	 * @return <dayRange|examItem, List<DayRange|ExamItemRefer>>
	 */
	@RequestMapping(value = "/headCatalog", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, List<? extends Object>> getTableHeadFields(@RequestParam(value = "hospitalId") Integer hospitalId, HttpSession session) {
		//LoginUtil.getCurrentUser(session);
		Map<String, List<? extends Object>> fields = new HashMap<String, List<? extends Object>>();
		fields.put("dayRange", counterQueryService.getDayRange(hospitalId));

		fields.put("examItem", countExamItemService.getLimitItemsByHospitalId(hospitalId));
		return fields;
	}

}
