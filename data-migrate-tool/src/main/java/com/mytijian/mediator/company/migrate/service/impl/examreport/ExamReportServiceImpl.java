package com.mytijian.mediator.company.migrate.service.impl.examreport;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.company.migrate.dao.HospitalCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.examreport.ConditionDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.examreport.OrderConditionDO;
import com.mytijian.mediator.company.migrate.dao.examreport.ConditionMapper;
import com.mytijian.mediator.company.migrate.dao.examreport.ExamReportMapper;
import com.mytijian.mediator.company.migrate.dao.examreport.ExamReportSystemParamMapper;
import com.mytijian.mediator.company.migrate.dao.user.UserHelperMapper;
import com.mytijian.mediator.company.migrate.service.examreport.ExamReportService;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.report.model.ExamReportSystemParam;
import com.mytijian.util.AssertUtil;

@Service("examReportService")
public class ExamReportServiceImpl implements ExamReportService {

	private Logger logger = LoggerFactory.getLogger(ExamReportServiceImpl.class);

	@Resource(name = "examReportMapper")
	private ExamReportMapper examReportMapper;

	@Resource(name = "hospitalCompanyMapper")
	private HospitalCompanyMapper hospitalCompanyMapper;

	@Resource(name = "examReportSystemParamMapper")
	private ExamReportSystemParamMapper examReportSystemParamMapper;

	@Resource(name = "userHelperMapper")
	private UserHelperMapper userHelperMapper;

	@Resource(name = "conditionMapper")
	private ConditionMapper conditionMapper;

	@Override
	public void updateReportNewCompanyId(String tableName, Page page) {
		while (true) {
			ExamReportSystemParam param = examReportSystemParamMapper.selectSysParams("companyId:update:" + tableName,
					null);
			if (param == null) {
				logger.info("{}表没有设置起始值，无法更新", tableName);
				break;
			}
			int startIndex = Integer.valueOf(param.getParamValue());
			List<ConditionDO> reportDOList = examReportMapper.selectWithOrderNumByPage(tableName, startIndex, page);
			if (AssertUtil.isEmpty(reportDOList)) {
				break;
			}
			for (ConditionDO report : reportDOList) {
				updateOneExamReport(tableName, report.getOrderNum(), report);
			}
			int index = reportDOList.get(reportDOList.size() - 1).getId();
			param.setParamValue(String.valueOf(index));
			examReportSystemParamMapper.updateSysParam(param);
		}
	}

	
	private void updateOneExamReport(String tableName, String orderNum, ConditionDO report){
		try{
			OrderConditionDO order = conditionMapper.selectOrderByOrderNum(orderNum);
			if (order == null) {
				return;
			}
			examReportMapper.updateNewCompanyIdById(tableName, report.getId(), order.getNewCompanyId(), order.getOldCompanyId());
		} catch (Exception ex){
			logger.error("体检报告更新单位失败，reportId:{}, tableName:{}, orderNum:{}", report.getId(), tableName, orderNum, ex);
		}
	}

	private void updateOne(String tableName, String orderNum, ConditionDO report) {
		Integer examCompanyId = report.getCompanyId();
		if(examCompanyId == null){
			return;
		}
		if (examCompanyId.equals(1585)) {
			OrderConditionDO order = conditionMapper.selectOrderByOrderNum(orderNum);
			if (order == null) {
				return;
			}
			Integer managerId = order.getOwnerId() == null ? order.getOperatorId() : order.getOwnerId();
			if (managerId == null) {
				return;
			}
			// 单位是1585
			// HOSPITAL_GUEST_ONLINE(1, "个人网上预约",-100),
			// HOSPITAL_GUEST_OFFLINE(2, "现场散客",-101),
			// HOSPITAL_MTJK(3, "每天健康",-102);
			List<Integer> platformManagerIds = conditionMapper.selectAllPlatformManager();
			Integer mt = userHelperMapper.selectHospitalIdmt();
			if (platformManagerIds.contains(managerId) || mt.equals(order.getFromSite())) {
				examCompanyId = -102;
			} else {
				if (order.getSource() == 3) {
					examCompanyId = -101;
				} else {
					examCompanyId = -100;
				}
			}

		} else {
			// m 单位
			List<Integer> mCompanyIds = conditionMapper.selectAllMCompanyId();
			if (mCompanyIds.contains(examCompanyId)) {
				examCompanyId = -102;
			}
		}
		updateCompanyId(tableName, report, examCompanyId);
	}


	private void updateCompanyId(String tableName, ConditionDO report, Integer examCompanyId) {
		HospitalCompanyDO companyDo = null;
		try {
			if(examCompanyId < 4400000){
				companyDo = hospitalCompanyMapper.selectByExamCompanyIdAndOrganizationId(examCompanyId,
						report.getHospitalId());
			} else {
				companyDo = hospitalCompanyMapper.selectByOldExamCompanyIdAndOrganizationId(examCompanyId,
						report.getHospitalId());
			}
			if (companyDo != null) {
				logger.info("更新体检报告单位，tableName:{},reportId:{},hospitalId:{}, oldCompanyId:{}, newCompanyId:{}",
						tableName, report.getId(), report.getHospitalId(), report.getCompanyId(), companyDo.getId());
				if(examCompanyId < 4400000){
					examReportMapper.updateNewCompanyIdById(tableName, report.getId(), companyDo.getId(), examCompanyId);
				} else {
					examReportMapper.updateNewCompanyIdById(tableName, report.getId(), examCompanyId, companyDo.getId() < 0? 1585:companyDo.getId());
				}
			} else {
				logger.info("更新体检报告单位，tableName:{}, reportId:{},hospitalId:{}, oldCompanyId:{}, newCompanyId is null",
						tableName, report.getId(), report.getHospitalId(), report.getCompanyId());
			}
		} catch (Exception ex) {
			logger.error("更新新单位id失败， tableName：{}，hospitalId:{}, oldCompanyId:{}", tableName, report.getHospitalId(),
					report.getClass(), ex);
		}
	}

	@Override
	public void updateGroupBaseNewCompanyId(String tableName, Page page) {
		while (true) {
			ExamReportSystemParam param = examReportSystemParamMapper.selectSysParams("companyId:update:" + tableName,
					null);
			if (param == null) {
				logger.info("{}表没有设置起始值，无法更新", tableName);
				break;
			}
			int startIndex = Integer.valueOf(param.getParamValue());
			List<ConditionDO> reportDOList = examReportMapper.selectGroupDataWithCompanyIdByPage(tableName, startIndex,
					page);
			if (AssertUtil.isEmpty(reportDOList)) {
				break;
			}
			for (ConditionDO report : reportDOList) {
				String reportTableName = "tb_examreport_" + report.getHospitalId() % 4;
				String orderNum = examReportMapper.selectOrderNumByReportId(reportTableName, report.getReportId());
				updateOne(tableName, orderNum, report);
			}
			int index = reportDOList.get(reportDOList.size() - 1).getId();
			param.setParamValue(String.valueOf(index));
			examReportSystemParamMapper.updateSysParam(param);
		}

	}

	@Override
	public void updateGroupSummaryNewCompanyId(Page page) {
		String tableName = "tb_group_report_summary_base";
		while(true){
			ExamReportSystemParam param = examReportSystemParamMapper.selectSysParams("companyId:update:tb_group_report_summary_base",
					null);
			if (param == null) {
				logger.info("{}表没有设置起始值，无法更新", "tb_group_report_summary_base");
				break;
			}
			int startIndex = Integer.valueOf(param.getParamValue());
			List<ConditionDO> reportDOList = examReportMapper.selectGroupSummaryWithCompanyIdByPage(startIndex, page);
			if (AssertUtil.isEmpty(reportDOList)) {
				break;
			}
			for (ConditionDO report : reportDOList) {
				Integer companyId = report.getCompanyId();
				if(companyId == null){
					continue;
				}
				updateCompanyId(tableName, report, companyId);
			}
			int index = reportDOList.get(reportDOList.size() - 1).getId();
			param.setParamValue(String.valueOf(index));
			examReportSystemParamMapper.updateSysParam(param);
		}
	}
}
