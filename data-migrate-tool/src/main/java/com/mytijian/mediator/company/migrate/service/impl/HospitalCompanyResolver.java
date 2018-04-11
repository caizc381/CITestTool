package com.mytijian.mediator.company.migrate.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.mytijian.company.enums.ExamCompanyTypeEnum;
import com.mytijian.mediator.company.migrate.dao.ExamCompanyHospitalRelMapper;
import com.mytijian.mediator.company.migrate.dao.HospitalCompanyMapper;
import com.mytijian.mediator.company.migrate.dao.HospitalMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.ExamCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ExamCompanyHospitalRelDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalCompanyDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalDO;
import com.mytijian.util.PinYinUtil;

@Service("hospitalCompanyResolver")
public class HospitalCompanyResolver {
	@Resource(name = "examCompanyHospitalRelMapper")
	private ExamCompanyHospitalRelMapper examCompanyHospitalRelMapper;

	@Resource(name = "hospitalMapper")
	private HospitalMapper hospitalMapper;
	
	@Resource(name = "hospitalCompanyMapper")
	private HospitalCompanyMapper hospitalCompanyMapper;

	// 医院缓存
	private Map<Integer, HospitalDO> hospMap = new HashMap<Integer, HospitalDO>();

	/**
	 * 处理医院单位
	 * 
	 * @param companyList
	 * @return
	 */
	public List<HospitalCompanyDO> handleHospComp(
			List<ExamCompanyDO> companyList) {
		List<ExamCompanyDO> normalCompList = companyList
				.stream()
				.filter(p -> p.getType().intValue() == ExamCompanyTypeEnum.normal
						.getCode()).collect(Collectors.toList());

		List<HospitalCompanyDO> hospCompList = new ArrayList<HospitalCompanyDO>();
		for (ExamCompanyDO comp : normalCompList) {
			hospCompList.addAll(this.getHospCompany(comp));
		}

		return hospCompList;

	}

	/**
	 * 查询医院单位关系表数据
	 * @param company
	 * @return
	 */
	public List<HospitalCompanyDO> getHospCompany(ExamCompanyDO company) {
		List<ExamCompanyHospitalRelDO> companyRelList = examCompanyHospitalRelMapper
				.selectByCompanyId(company.getId());
		if (CollectionUtils.isNotEmpty(companyRelList)) {

			List<HospitalCompanyDO> list = new ArrayList<HospitalCompanyDO>();
			for (ExamCompanyHospitalRelDO rel : companyRelList) {
				HospitalCompanyDO hospCompany = new HospitalCompanyDO();
				hospCompany.setDiscount(rel.getDiscount());
				hospCompany.setPlatformCompanyId(null);
				// status=1可用
				hospCompany.setDeleted((rel.getStatus() != null && rel
						.getStatus().intValue() == 1) ? false : true);
				hospCompany.setEmployeeImport(rel.getEmployeeImport());
				hospCompany.setOrganizationId(rel.getHospitalId());
				HospitalDO hospitalDO = this.getHospital(rel.getHospitalId());
				hospCompany.setOrganizationName(hospitalDO != null ? hospitalDO
						.getName() : null);

				hospCompany.setShowReport(rel.getShowReport());
				hospCompany.setSettlementMode(rel.getSettlementMode());
				hospCompany.setSendExamSms(rel.getSendExamSms());
				hospCompany.setSendExamSmsDays(rel.getSendExamSmsDays());
				hospCompany.setName(rel.getCompanyAlias()); // 单位名称
				hospCompany.setPinyin(PinYinUtil.getFirstSpell(hospCompany
						.getName()));
				hospCompany.setTbExamCompanyId(rel.getCompanyId());

				hospCompany.setHisName(rel.getHisName()); // 设置his name
				// 设置提前导出订单
				hospCompany.setAdvanceExportOrder(rel.getAdvanceExportOrder());
				hospCompany.setEmployeePrefix(company.getPrefix());
				// 设置地址
				hospCompany.setExaminationAddress(rel.getExaminationAddress());
				hospCompany.setExamreportIntervalTime(rel.getExamreportIntervalTime());
				list.add(hospCompany);
			}
			return list;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * 医院本地缓存
	 * @param hospId
	 * @return
	 */
	public HospitalDO getHospital(Integer hospId) {
		if (hospMap.size() == 0) {
			List<HospitalDO> allList = hospitalMapper.selectAll();
			for (HospitalDO hosp : allList) {
				hospMap.put(hosp.getId(), hosp);
			}
		}

		HospitalDO hospitalDO = hospMap.get(hospId);
		if (hospitalDO != null) {
			return hospitalDO;
		} else {
			return hospitalMapper.selectBaseInfoByHospId(hospId);
		}

	}
	
	/**
	 * tb_hospital_company数据的插入和更新
	 * 
	 * @param examCompanyDO
	 */
	public void changeHospitalCompanyData(ExamCompanyDO examCompanyDO) {
		List<HospitalCompanyDO> hospitalCompanyDOList = hospitalCompanyMapper
				.selectByExamCompanyId(examCompanyDO.getId());
		List<HospitalCompanyDO> hospitalCompanyList = this
				.getHospCompany(examCompanyDO);

		// tb_hospital_company表中不存在,直接插入
		if (CollectionUtils.isEmpty(hospitalCompanyDOList)) 
		{
			if (CollectionUtils.isNotEmpty(hospitalCompanyList)) {
				hospitalCompanyMapper.insertList(hospitalCompanyList);
			}
		} else // tb_hospital_company表中存在数据则进行更新
		{
			this.updateHospCompany(hospitalCompanyList);
		}
	}

	/**
	 * 更新医院单位
	 * @param hospitalCompanyList
	 */
	public void updateHospCompany(List<HospitalCompanyDO> hospitalCompanyList) {
		for (HospitalCompanyDO hisCompanyDO : hospitalCompanyList) {
			HospitalCompanyDO temp = hospitalCompanyMapper
					.selectByExamCompanyIdAndOrganizationId(
							hisCompanyDO.getTbExamCompanyId(),
							hisCompanyDO.getOrganizationId());
			if (temp != null) {
				hisCompanyDO.setId(temp.getId()); // 设置医院单位id，更新
				hospitalCompanyMapper.updateExamCompany(hisCompanyDO);
			} else {
				// 给新的体检中心分配了这个平台单位
				hospitalCompanyMapper.insert(hisCompanyDO); 
			}
		}
	}
}
