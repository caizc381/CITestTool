package com.mytijian.admin.web.controller.survey;

import com.mytijian.uic.annotation.LoginRequired;
import com.mytijian.survey.dto.HospitalSurveyRelationDTO;
import com.mytijian.survey.service.SurveyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wangzhongxing on 2017/2/21.
 */
@Controller
public class SurveyController {

    private final Logger logger = LoggerFactory.getLogger(SurveyController.class);

    @Resource(name = "surveyService")
    private SurveyService surveyService;

/*
    @RequestMapping(value = "/getAllSurvey", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public List<SurveyDTO> getAllSurvey(Integer hospitalId) throws Exception{
        List<SurveyDTO> surveyVOList = surveyService.getAllSurvey(hospitalId);

        return surveyVOList;
    }
*/

    @RequestMapping(value = "/managerAllocateSurvey", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public boolean managerAllocateSurveyToHospital(@RequestBody HospitalSurveyRelationDTO hospitalSurveyRelationDTO) throws Exception{
        return surveyService.allocateSurveyToHospital(hospitalSurveyRelationDTO);
    }


}
