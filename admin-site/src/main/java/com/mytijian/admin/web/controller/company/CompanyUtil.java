package com.mytijian.admin.web.controller.company;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.mytijian.admin.web.vo.company.HospitalVO;
import com.mytijian.resource.model.Hospital;
import com.mytijian.util.PinYinUtil;

public class CompanyUtil {

	public static List<HospitalVO> resolveHospitalVOList(
			List<Hospital> hospitalList) {
		List<HospitalVO> hospitalVOList = new ArrayList<HospitalVO>();
		for (Hospital hospital : hospitalList) {
			HospitalVO v = new HospitalVO();
			BeanUtils.copyProperties(hospital, v);
			v.setPinyin(PinYinUtil.getFirstSpell(hospital.getName()));
			hospitalVOList.add(v);
		}
		return hospitalVOList;
	}
}
