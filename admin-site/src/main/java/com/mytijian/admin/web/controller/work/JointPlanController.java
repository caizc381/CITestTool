package com.mytijian.admin.web.controller.work;

import com.mytijian.work.service.jointplan.model.JointPlanDetailDTO;
import com.mytijian.work.service.jointplan.service.JointPlanService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhaosiqi 2018/1/31 16:35
 */
@Controller
@RequestMapping("/work")
public class JointPlanController {

    @Resource(name="jointPlanService")
    private JointPlanService jointPlanService;

    @RequestMapping(value = "/listJointPlan",method = RequestMethod.POST)
    @ResponseBody
    public List<JointPlanDetailDTO> getJointPlanList(Integer hospitalId){
        List<JointPlanDetailDTO> list = jointPlanService.getJointPlanByHospitalId(hospitalId);
        return list;
    }

    @RequestMapping(value = "/updateJointPlan",method = RequestMethod.POST)
    @ResponseBody
    public boolean updateJointPlan(JointPlanDetailDTO jointPlanDTO){
        return jointPlanService.updateJointPlan(jointPlanDTO);
    }

    @RequestMapping(value = "/getJointPlan",method = RequestMethod.POST)
    @ResponseBody
    public JointPlanDetailDTO getJointPlan(Integer id){
        JointPlanDetailDTO dto = jointPlanService.getJointPlanById(id);
        return dto;
    }
}
