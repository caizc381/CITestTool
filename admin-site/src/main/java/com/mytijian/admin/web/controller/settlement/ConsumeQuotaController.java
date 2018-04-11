package com.mytijian.admin.web.controller.settlement;

import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.web.dto.HospitalQueryDto;
import com.mytijian.admin.web.facade.hospital.HospitalFacade;
import com.mytijian.admin.web.vo.resource.OrganizationVO;
import com.mytijian.exception.BizException;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.trade.common.constant.OperatorTypeEnum;
import com.mytijian.trade.common.dto.TradeCommonLogAddDTO;
import com.mytijian.trade.settlement.dto.AuditConsumeQuotaDetailDTO;
import com.mytijian.trade.settlement.dto.TradeConsumeQuotaDetailQueryDTO;
import com.mytijian.trade.settlement.enums.ConsumeQuotaDetailSceneEnum;
import com.mytijian.trade.settlement.enums.ConsumeQuotaDetailStatusEnum;
import com.mytijian.trade.settlement.model.TradeConsumeQuotaDetail;
import com.mytijian.trade.settlement.model.TradeConsumeQuotaStatistics;
import com.mytijian.trade.settlement.service.TradeConsumeQuotaService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by wangzhongxing on 2017/11/30.
 */
@Controller
@RequestMapping("/settlement")
public class ConsumeQuotaController {

    private final static Logger logger = LoggerFactory.getLogger(ConsumeQuotaController.class);

    @Resource(name = "hospitalFacade")
    private HospitalFacade hospitalFacade;

    @Resource(name = "tradeConsumeQuotaService")
    private TradeConsumeQuotaService tradeConsumeQuotaService;

    /**
     * 消费额度统计页面
     * @param tradeConsumeQuotaDetailQueryDTO
     * @return
     */
    @RequestMapping(value = "/consumeQuotaManage", method = RequestMethod.POST)
    @ResponseBody
    public PageView<TradeConsumeQuotaStatistics> consumeQuotaManage(@RequestBody TradeConsumeQuotaDetailQueryDTO tradeConsumeQuotaDetailQueryDTO){
        List<Integer> organizationIds = getOrgnizationIds(tradeConsumeQuotaDetailQueryDTO);
        if (CollectionUtils.isEmpty(organizationIds)){
            Page page = new Page(1, 20);
            page.setRowCount(0);
            return new PageView<>(Collections.emptyList(), page);
        }
        tradeConsumeQuotaDetailQueryDTO.setOrganizationIds(organizationIds);

        return tradeConsumeQuotaService.listConsumeQuotaStatistics(tradeConsumeQuotaDetailQueryDTO);
    }

    private List<Integer> getOrgnizationIds(TradeConsumeQuotaDetailQueryDTO tradeConsumeQuotaDetailQueryDTO){
        List<Integer> organizationIds = new ArrayList<>();
        if (null == tradeConsumeQuotaDetailQueryDTO.getOrganizationId()){
            HospitalQueryDto hospitalQueryDto = new HospitalQueryDto();
            hospitalQueryDto.setProvinceId(tradeConsumeQuotaDetailQueryDTO.getProvinceId());
            hospitalQueryDto.setCityId(tradeConsumeQuotaDetailQueryDTO.getCityId());
            hospitalQueryDto.setDistrictId(tradeConsumeQuotaDetailQueryDTO.getDistrictId());
            hospitalQueryDto.setOrgType(OrganizationTypeEnum.HOSPITAL.getCode());
            List<OrganizationVO> organizationVOList = hospitalFacade.listHospital(hospitalQueryDto);
            if (CollectionUtils.isEmpty(organizationVOList)){
                Page page = new Page(1, 20);
                page.setRowCount(0);
                return Collections.emptyList();
            }
            for (OrganizationVO organizationVO : organizationVOList) {
                organizationIds.add(organizationVO.getId());
            }
        } else {
            organizationIds.add(tradeConsumeQuotaDetailQueryDTO.getOrganizationId());
        }

        return organizationIds;
    }

    @RequestMapping(value = "/getPlatformConsumeQuotaStatistics", method = RequestMethod.POST)
    @ResponseBody
    public TradeConsumeQuotaStatistics getPlatformConsumeQuotaStatistics(@RequestBody TradeConsumeQuotaDetailQueryDTO tradeConsumeQuotaDetailQueryDTO){
        List<Integer> organizationIds = getOrgnizationIds(tradeConsumeQuotaDetailQueryDTO);
        if (CollectionUtils.isEmpty(organizationIds)){
            TradeConsumeQuotaStatistics consumeQuotaStatistics = new TradeConsumeQuotaStatistics();
            consumeQuotaStatistics.setTotalAmount(0l);
            consumeQuotaStatistics.setPresentMounthAmont(0l);
            consumeQuotaStatistics.setForwardMounthAmont(0l);
            return consumeQuotaStatistics;
        }

        return tradeConsumeQuotaService.getPlatformConsumeQuotaStatistics(organizationIds);
    }

    /**
     * 添加消费额度
     * @param tradeConsumeQuotaDetail
     * @return
     */
    @RequestMapping(value = "/addConsumeQuotaDetail", method = RequestMethod.POST)
    @ResponseBody
    public boolean addConsumeQuotaDetail(@RequestBody TradeConsumeQuotaDetail tradeConsumeQuotaDetail, HttpSession session){
        if (null == tradeConsumeQuotaDetail.getOrganizationId() || null == tradeConsumeQuotaDetail.getAmount() ||
                null == tradeConsumeQuotaDetail.getPayTime() || null == tradeConsumeQuotaDetail.getScene()){
            logger.info("addConsumeQuotaDetail interface params are illegal! tradeConsumeQuotaDetail: {}", tradeConsumeQuotaDetail);
            throw new BizException("ADD_CONSUME_QUOTA_DETAIL_1000", "参数错误！");
        }
        if (ConsumeQuotaDetailSceneEnum.HOSPITAL_INVOICE.getCode().equals(tradeConsumeQuotaDetail.getScene()) && tradeConsumeQuotaDetail.getAmount() <= 0){
            logger.info("addConsumeQuotaDetail interface params are illegal! tradeConsumeQuotaDetail: {}", tradeConsumeQuotaDetail);
            throw new BizException("ADD_CONSUME_QUOTA_DETAIL_1001", "医院开票金额不能小于等于零！");
        }
        if (ConsumeQuotaDetailSceneEnum.FINANCIAL_ADJUST.getCode().equals(tradeConsumeQuotaDetail.getScene()) && tradeConsumeQuotaDetail.getAmount() == 0){
            logger.info("addConsumeQuotaDetail interface params are illegal! tradeConsumeQuotaDetail: {}", tradeConsumeQuotaDetail);
            throw new BizException("ADD_CONSUME_QUOTA_DETAIL_1002", "账务调整金额不能等于零！");
        }

        if (ConsumeQuotaDetailSceneEnum.PLATFORM_SERVICE.getCode().equals(tradeConsumeQuotaDetail.getScene()) && tradeConsumeQuotaDetail.getAmount() == 0){
            logger.info("addConsumeQuotaDetail interface params are illegal! tradeConsumeQuotaDetail: {}", tradeConsumeQuotaDetail);
            throw new BizException("ADD_CONSUME_QUOTA_DETAIL_1002", "平台服务费不能等于零！");
        }

        // 设置流转日志操作人信息
        Employee employee = (Employee) session.getAttribute("employee");
        TradeCommonLogAddDTO commonLogAddDTO = new TradeCommonLogAddDTO();
        commonLogAddDTO.setOperatorId(employee.getId());
        commonLogAddDTO.setOperatorName(employee.getEmployeeName());
        commonLogAddDTO.setOperatorType(OperatorTypeEnum.OPERATOR_TYPE_OPS.getValue());
        tradeConsumeQuotaDetail.setCommonLogAddDTO(commonLogAddDTO);
        tradeConsumeQuotaDetail.setStatus(ConsumeQuotaDetailStatusEnum.HOSPITAL_TO_BE_CONFIRMED.getCode());

        tradeConsumeQuotaService.addConsumeQuotaDetail(tradeConsumeQuotaDetail);

        return true;
    }

    /**
     * 消费额度明细页面
     * @param tradeConsumeQuotaDetailQueryDTO
     * @return
     */
    @RequestMapping(value = "/listConsumeQuotaDetail", method = RequestMethod.POST)
    @ResponseBody
    public PageView<TradeConsumeQuotaDetail> listConsumeQuotaDetail(@RequestBody TradeConsumeQuotaDetailQueryDTO tradeConsumeQuotaDetailQueryDTO){
        List<Integer> organizationIds =getOrgnizationIds(tradeConsumeQuotaDetailQueryDTO);
        if (CollectionUtils.isEmpty(organizationIds)){
            Page page = new Page(1, 20);
            page.setRowCount(0);
            return new PageView<>(Collections.emptyList(), page);
        }

        tradeConsumeQuotaDetailQueryDTO.setOrganizationIds(organizationIds);

        // 前端传-1表示全部，status不作为筛选条件
        if (tradeConsumeQuotaDetailQueryDTO.getStatus().size() == 1 && tradeConsumeQuotaDetailQueryDTO.getStatus().get(0) == -1){
            tradeConsumeQuotaDetailQueryDTO.setStatus(null);
        }

        return tradeConsumeQuotaService.listConsumeQuotaDetailByPage(tradeConsumeQuotaDetailQueryDTO);
    }

    /**
     * 撤销消费额度
     * @param sn
     * @return
     */
    @RequestMapping(value = "/revokeConsumeQuotaDetail", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public boolean revokeConsumeQuotaDetail(@RequestParam(value = "sn") String sn, HttpSession session){
        AuditConsumeQuotaDetailDTO auditConsumeQuotaDetailDTO = new AuditConsumeQuotaDetailDTO();
        auditConsumeQuotaDetailDTO.setSn(sn);
        auditConsumeQuotaDetailDTO.setStatus(ConsumeQuotaDetailStatusEnum.PLATFORM_HAS_REVOKED.getCode());
        // 设置流转日志信息
        TradeCommonLogAddDTO commonLogAddDTO = new TradeCommonLogAddDTO();
        Employee employee = (Employee) session.getAttribute("employee");
        commonLogAddDTO.setOperatorId(employee.getId());
        commonLogAddDTO.setOperatorName(employee.getEmployeeName());
        commonLogAddDTO.setOperatorType(OperatorTypeEnum.OPERATOR_TYPE_OPS.getValue());
        commonLogAddDTO.setOperation("平台撤销");
        commonLogAddDTO.setIsDeleted(0);
        auditConsumeQuotaDetailDTO.setCommonLogAddDTO(commonLogAddDTO);

        return tradeConsumeQuotaService.auditConsumeQuotaDetail(auditConsumeQuotaDetailDTO);
    }

}
