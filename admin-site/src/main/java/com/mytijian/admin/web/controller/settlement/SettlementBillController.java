package com.mytijian.admin.web.controller.settlement;

import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.api.rbac.model.Role;
import com.mytijian.admin.web.dto.FinancePaymentDto;
import com.mytijian.admin.web.dto.HospitalQueryDto;
import com.mytijian.admin.web.dto.ReviewBillDTO;
import com.mytijian.admin.web.facade.hospital.HospitalFacade;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.admin.web.vo.resource.OrganizationVO;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.trade.settlement.dto.AddSettlementPayRecordDTO;
import com.mytijian.trade.settlement.dto.SettlementBatchQueryDTO;
import com.mytijian.trade.settlement.dto.TradeReviewBillDTO;
import com.mytijian.trade.settlement.enums.HospitalPlatformBillStatusEnum;
import com.mytijian.trade.settlement.enums.SettlementPayRecordTypeEnum;
import com.mytijian.trade.settlement.model.HospitalPlatformSummaryBill;
import com.mytijian.trade.settlement.model.TradeHospitalPlatformBill;
import com.mytijian.trade.settlement.model.TradeSettlementPayRecord;
import com.mytijian.trade.settlement.service.TradeHospitalPlatformBillService;
import com.mytijian.trade.settlement.service.TradeSettlementBatchService;
import com.mytijian.trade.settlement.service.TradeSettlementPayRecordService;
import com.mytijian.util.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mytijian.web.intercepter.Token;

@Controller
@RequestMapping("/settlement")
public class SettlementBillController {

    @Resource(name = "tradeSettlementBatchService")
    private TradeSettlementBatchService tradeSettlementBatchService;

    @Resource(name = "tradeHospitalPlatformBillService")
    private TradeHospitalPlatformBillService tradeHospitalPlatformBillService;

    @Resource(name = "tradeSettlementPayRecordService")
    private TradeSettlementPayRecordService tradeSettlementPayRecordService;

    @Resource(name = "hospitalFacade")
    private HospitalFacade hospitalFacade;

    /**
     * 获取登录人角色
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "getRoleList", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getRoleList(HttpSession session){
        List<String> roleList = new ArrayList<>();
        Employee employee = (Employee) session.getAttribute("employee");
        List<Role> roles = employee.getRoles();
        if(AssertUtil.isNotEmpty(roles)){
            for (Role role : roles) {
                roleList.add(role.getRoleName());
            }
        }
        return roleList;
    }

    /**
     * 获取医院与平台账单
     *
     * @param settlementBatchQueryDTO
     * @return
     */
    @RequestMapping(value = "/getHospitalPlatformSummaryBillList", method = RequestMethod.POST)
    @ResponseBody
    public List<HospitalPlatformSummaryBill> getHospitalPlatformSummaryBillList(@RequestBody SettlementBatchQueryDTO settlementBatchQueryDTO) {
        HospitalQueryDto hospitalQueryDto = new HospitalQueryDto();
        BeanUtils.copyProperties(settlementBatchQueryDTO, hospitalQueryDto);
        hospitalQueryDto.setOrgType(OrganizationTypeEnum.HOSPITAL.getCode());
        List<OrganizationVO> organizationVOList = hospitalFacade.listHospital(hospitalQueryDto);
        List<Integer> organizationIds = new ArrayList<>();
        for (OrganizationVO organizationVO : organizationVOList) {
            organizationIds.add(organizationVO.getId());
        }
        if (AssertUtil.isEmpty(organizationIds)) {
            organizationIds.add(-1);
        }
        settlementBatchQueryDTO.setOrganizationIds(organizationIds);
        return tradeSettlementBatchService.getHospitalPlatformSummaryBill(settlementBatchQueryDTO);
    }

    /**
     * 运营经理审核账单
     *
     * @param reviewBillDTO
     */
    @RequestMapping(value = "/reviewBill", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void reviewBill(@RequestBody ReviewBillDTO reviewBillDTO) {
        Employee employee = SessionUtil.getEmployee();

        TradeReviewBillDTO tradeReviewBillDTO = new TradeReviewBillDTO();
        tradeReviewBillDTO.setConsumeQuotaAmount(reviewBillDTO.getConsumeQuotaAmount());
        tradeReviewBillDTO.setDiscountAmount(reviewBillDTO.getDiscountAmount());
        tradeReviewBillDTO.setPlatformActurallyPayAmount(reviewBillDTO.getPlatformActurallyPayAmount());
        tradeReviewBillDTO.setRemark(reviewBillDTO.getRemark());
        tradeReviewBillDTO.setOperatorId(employee.getId());
        tradeReviewBillDTO.setOperatorName(employee.getEmployeeName());
        tradeReviewBillDTO.setSn(reviewBillDTO.getSn());

        tradeHospitalPlatformBillService.reviewBill(tradeReviewBillDTO);
    }

    /**
     * 财务付款
     *
     * @param financePaymentDto
     */
    @RequestMapping(value = "/addSettlementPayRecord", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @Token
    public void addSettlementPayRecord(@RequestBody FinancePaymentDto financePaymentDto,
                                       HttpSession session) {

        Employee employee = (Employee) session.getAttribute("employee");
        AddSettlementPayRecordDTO addSettlementPayRecordDTO = new AddSettlementPayRecordDTO();
        addSettlementPayRecordDTO.setImageUrl(financePaymentDto.getImageUrl());
        addSettlementPayRecordDTO.setOrganizationId(financePaymentDto.getOrganizationId());
        addSettlementPayRecordDTO.setRemark(financePaymentDto.getRemark());
        addSettlementPayRecordDTO.setSnList(financePaymentDto.getSnList());
        addSettlementPayRecordDTO.setOperatorId(employee.getId());
        addSettlementPayRecordDTO.setOperatorName(employee.getEmployeeName());
        tradeHospitalPlatformBillService.addSettlementPayRecord(addSettlementPayRecordDTO);


    }

}
