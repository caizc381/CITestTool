package com.mytijian.admin.web.controller.syncmsg;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.api.rbac.service.EmployeeService;
import com.mytijian.admin.web.util.ShiroUtils;
import com.mytijian.admin.web.vo.syncmsg.SyncMessageVO;
import com.mytijian.common.dto.SyncMessageDTO;
import com.mytijian.common.enums.CommonExceptionEnum;
import com.mytijian.common.model.SyncMsgProcessRequest;
import com.mytijian.common.model.SyncMsgProcessResponse;
import com.mytijian.common.service.SyncMessageService;
import com.mytijian.common.service.SyncMsgProcessor;
import com.mytijian.exception.BizException;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.util.AssertUtil;

@RestController
@RequestMapping("/syncMsg")
public class SyncMessageController implements ApplicationContextAware {

    // Spring应用上下文环境
    private ApplicationContext applicationContext;
	
	@Resource(name = "syncMessageService")
	private SyncMessageService syncMessageService;
	@Resource(name = "employeeService")
	private EmployeeService employeeService;

	/**
	 * 获取有效同步数据消息
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "/allValidSyncMessages", method = RequestMethod.POST)
	public PageView<SyncMessageVO> getAllValidSyncMessages(Page page){
		//获取消息
		PageView<SyncMessageDTO> pageVievTemp = syncMessageService.getAllValidSyncMessages(page);
		//获取员工Map
		Map<Integer, Employee> employeeMap = getEmployeeMap(pageVievTemp.getRecords());
		//设置操作人名称
		List<SyncMessageVO> msgVOList=Lists.newArrayList();
		pageVievTemp.getRecords().forEach(msgDTO->{
			SyncMessageVO msgVO = new SyncMessageVO();
			BeanUtils.copyProperties(msgDTO, msgVO);
			if(AssertUtil.isNotNull(employeeMap.get(msgDTO.getOperator()))){
				msgVO.setOperatorName(employeeMap.get(msgDTO.getOperator()).getEmployeeName());
			}
			msgVOList.add(msgVO);
		});
		//构建展示消息PageView对象
		PageView<SyncMessageVO> pageViev = new PageView<SyncMessageVO>();
		pageViev.setRecords(msgVOList);
		pageViev.setPage(pageVievTemp.getPage());
		return pageViev;
	}

	private Map<Integer, Employee> getEmployeeMap(List<SyncMessageDTO> records) {
		Map<Integer, Employee> employeeMap = Maps.newHashMap();
		if (AssertUtil.isEmpty(records)) {
			return employeeMap;
		}
		List<Integer> employeeIds = Lists.newArrayList();
		records.forEach(msgDTO -> {
			if (AssertUtil.isNotNull(msgDTO.getOperator())) {
				employeeIds.add(msgDTO.getOperator());
			}
		});
		List<Employee> employees = employeeService.listEmployeesByIds(employeeIds);
		employees.forEach(employee -> {
			employeeMap.put(employee.getId(), employee);
		});
		return employeeMap;
	}
	
	/**
	 * 同步数据
	 * @param msgKey
	 * @param msgType
	 */
	@RequestMapping(value = "/syncData", method=RequestMethod.GET)
	public void syncData(String messageKey, String messageType){
		//获取同步消息
		SyncMessageDTO syncMsgDTO = syncMessageService.getSyncMessageByMsgkey(messageKey, messageType);
		if(AssertUtil.isNull(syncMsgDTO)){
			throw new BizException(CommonExceptionEnum.SYNC_MSG_NULL);
		}
		//获取同步消息处理器
		SyncMsgProcessor processor = applicationContext.getBean(syncMsgDTO.getMsgTypeModel().getMessageProcessor(), SyncMsgProcessor.class);
		if(AssertUtil.isNull(processor)){
			throw new BizException(CommonExceptionEnum.SYNC_MSG_NULL);
		}
		//同步消息处理
		SyncMsgProcessRequest request = new SyncMsgProcessRequest();
		request.setMessageKey(syncMsgDTO.getMessageKey());
		request.setMessageType(syncMsgDTO.getMessageType());
		request.setMessageContent(syncMsgDTO.getMessageContent());
		SyncMsgProcessResponse response = processor.process(request);
		response.setOperator(ShiroUtils.getUserId());
		syncMessageService.saveProcessResult(response, messageKey, messageType);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}
