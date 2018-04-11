package com.mytijian.admin.web.controller.resource;

import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.dto.FeedbackDTO;
import com.mytijian.resource.model.Feedback;
import com.mytijian.resource.model.FeedbackDeal;
import com.mytijian.resource.service.FeedbackQueryService;
import com.mytijian.resource.service.FeedbackService;
import com.mytijian.resource.service.param.FeedbackQuery;
import com.mytijian.resource.service.param.FeedbackSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private FeedbackQueryService feedbackQueryService;

    @PostMapping("/dealFeedback")
    public void dealFeedback(@RequestBody FeedbackDeal feedbackDeal) {
        Employee employee = SessionUtil.getEmployee();
        if (employee != null) {
            feedbackDeal.setOperator(employee.getEmployeeName());
        }
        feedbackService.dealFeedback(feedbackDeal);
    }

    @PostMapping("/saveFeedback")
    public void saveFeedback(@RequestBody Feedback feedback) {
        feedback.setSaveTime(new Date());
        feedbackService.saveFeedback(feedback);
    }

    @GetMapping("/queryFeedBackById")
    public FeedbackDTO queryFeedBackById(Integer feedbackId) {
        return feedbackQueryService.queryFeedBackById(feedbackId, new FeedbackSelector().selectFeedbackDeal());
    }

    @GetMapping("/listFeedbackByPage")
    public PageView<FeedbackDTO> listFeedbackByPage(Page page, FeedbackQuery feedbackQuery) {
        return feedbackQueryService.listFeedbackByPage(page, feedbackQuery, new FeedbackSelector());
    }

}
