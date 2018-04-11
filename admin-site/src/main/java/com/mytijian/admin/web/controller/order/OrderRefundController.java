package com.mytijian.admin.web.controller.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.account.model.Account;
import com.mytijian.account.param.AccountQueryParam;
import com.mytijian.account.service.AccountService;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.web.dto.HospitalQueryDto;
import com.mytijian.admin.web.facade.hospital.HospitalFacade;
import com.mytijian.admin.web.util.SessionUtil;
import com.mytijian.admin.web.vo.orderrefund.BatchOrderRefundAuditVO;
import com.mytijian.admin.web.vo.orderrefund.OrderRefundApplyQueryVO;
import com.mytijian.admin.web.vo.orderrefund.OrderRefundApplyRecordQueryVO;
import com.mytijian.admin.web.vo.orderrefund.OrderRefundApplyRecordResultVO;
import com.mytijian.admin.web.vo.orderrefund.OrderRefundApplyRecordVO;
import com.mytijian.admin.web.vo.orderrefund.OrderRefundApplyResultVO;
import com.mytijian.admin.web.vo.orderrefund.OrderRefundApplyVO;
import com.mytijian.admin.web.vo.resource.OrganizationVO;
import com.mytijian.company.hospital.service.HospitalCompanyService;
import com.mytijian.company.hospital.service.model.HospitalCompany;
import com.mytijian.order.base.service.OrderRefundApplyService;
import com.mytijian.order.base.service.dto.BatchOrderRefundAuditDTO;
import com.mytijian.order.base.service.dto.OrderRefundApplyQueryDTO;
import com.mytijian.order.base.service.dto.OrderRefundApplyRecordQueryDTO;
import com.mytijian.order.base.service.dto.OrderRefundApplyRecordResultDTO;
import com.mytijian.order.base.service.dto.OrderRefundApplyResultDTO;
import com.mytijian.order.base.service.model.OrderRefundApply;
import com.mytijian.order.base.service.model.OrderRefundApplyRecord;
import com.mytijian.order.exception.OrderException;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;

/**
 * @author weifeng
 * @date 2017/8/9
 */
@RestController
@RequestMapping("/orderrefund")
public class OrderRefundController {
    @Resource
    private OrderRefundApplyService orderRefundApplyService;

    @Resource
    private AccountService accountService;

    @Resource
    private HospitalCompanyService hospitalCompanyService;

    @Resource
    private HospitalService hospitalService;

    @Resource(name = "hospitalFacade")
    private HospitalFacade hospitalFacade;

    @RequestMapping(value = "/listRefundApply")
    @ResponseBody
    public OrderRefundApplyResultVO listApplyRefund(@RequestBody(required = true)OrderRefundApplyQueryVO orderRefundApplyQueryVO, HttpSession session) throws OrderException {
        OrderRefundApplyResultVO orderRefundApplyResultVO = null;

        OrderRefundApplyQueryDTO orderRefundApplyQueryDTO = convertToOrderRefundApplyQueryDTO(orderRefundApplyQueryVO);

        OrderRefundApplyResultDTO orderRefundApplyResultDTO = null;
        //null为全部，大于0为部分查
        if(!((orderRefundApplyQueryDTO.getAccountIdList() != null  && orderRefundApplyQueryDTO.getAccountIdList().size() == 0)
                || (orderRefundApplyQueryDTO.getFromSiteList() != null && orderRefundApplyQueryDTO.getFromSiteList().size() == 0))){
            orderRefundApplyResultDTO = orderRefundApplyService.getOrderRefundApplyList(orderRefundApplyQueryDTO);
        }

        if(orderRefundApplyResultDTO == null || orderRefundApplyResultDTO.getOrderRefundApplyList() == null || orderRefundApplyResultDTO.getOrderRefundApplyList().size() == 0){
            orderRefundApplyResultVO = new OrderRefundApplyResultVO();

            Page page = generateEmptyPage(orderRefundApplyQueryDTO.getCurrentPage(), orderRefundApplyQueryDTO.getPageSize());
            orderRefundApplyResultVO.setPage(page);
            orderRefundApplyResultVO.setRecords(Collections.EMPTY_LIST);
            return orderRefundApplyResultVO;
        }
        List<Integer> accountIdList = new ArrayList<>();
        List<Integer> companyIdList = new ArrayList<>();
        List<Integer> fromSiteIdList = new ArrayList<>();

        orderRefundApplyResultDTO.getOrderRefundApplyList().stream().forEach(orderRefundApplyRecord -> {
            accountIdList.add(orderRefundApplyRecord.getAccountId());
            companyIdList.add(orderRefundApplyRecord.getCompanyId());
            fromSiteIdList.add(orderRefundApplyRecord.getFromSite());
        });

        AccountQueryParam accountQueryParam = new AccountQueryParam();
        accountQueryParam.setIds(accountIdList);
        List<Account> accountList = accountService.listAccount(accountQueryParam);
        Map<Integer, Account> accountMap = accountList.stream().collect(Collectors.toMap(Account::getId, Function.identity()));

        List<HospitalCompany> hospitalCompanyList = hospitalCompanyService.listHospitalCompanyByIds(companyIdList);
        Map<Integer, HospitalCompany> companyMap = hospitalCompanyList.stream().collect(Collectors.toMap(HospitalCompany::getId, Function.identity()));

        List<Hospital> fromSiteList = hospitalService.getHospitalsByIds(fromSiteIdList);
        Map<Integer, Hospital> fromSiteMap = fromSiteList.stream().collect(Collectors.toMap(Hospital::getId, Function.identity()));

        orderRefundApplyResultVO = convertToOrderRefundApplyResultVO(accountMap, companyMap, fromSiteMap, orderRefundApplyResultDTO);

        return orderRefundApplyResultVO;
    }

    private Page generateEmptyPage(Integer currentPage, Integer pageSize) {
        if(currentPage == null && pageSize == null){
            return null;
        }
        Page page = new Page();
        page.setCurrentPage(currentPage);
        page.setPageSize(pageSize);
        page.setRowCount(0);
        return page;
    }

    @RequestMapping(value = "/listRefundApplyRecord")
    @ResponseBody
    public OrderRefundApplyRecordResultVO listRefundApplyRecord(@RequestBody(required = true)OrderRefundApplyRecordQueryVO orderRefundApplyRecordQueryVO, HttpSession session) throws OrderException {
        OrderRefundApplyRecordResultVO orderRefundApplyRecordResultVO = null;

        OrderRefundApplyRecordQueryDTO orderRefundApplyRecordQueryDTO = convertToOrderRefundApplyRecordQueryDTO(orderRefundApplyRecordQueryVO);
        OrderRefundApplyRecordResultDTO orderRefundApplyResultDTO = null;
        if(!((orderRefundApplyRecordQueryDTO.getAccountIdList() != null  && orderRefundApplyRecordQueryDTO.getAccountIdList().size() == 0)
                || (orderRefundApplyRecordQueryDTO.getFromSiteList() != null && orderRefundApplyRecordQueryDTO.getFromSiteList().size() == 0))){
            orderRefundApplyResultDTO = orderRefundApplyService.getOrderRefundApplyRecordList(orderRefundApplyRecordQueryDTO);
        }

        if(orderRefundApplyResultDTO == null || orderRefundApplyResultDTO.getOrderRefundApplyRecordList() == null || orderRefundApplyResultDTO.getOrderRefundApplyRecordList().size() == 0){
            orderRefundApplyRecordResultVO = new OrderRefundApplyRecordResultVO();
            Page page = generateEmptyPage(orderRefundApplyRecordQueryDTO.getCurrentPage(), orderRefundApplyRecordQueryDTO.getPageSize());
            orderRefundApplyRecordResultVO.setPage(page);
            orderRefundApplyRecordResultVO.setRecords(Collections.EMPTY_LIST);
            return orderRefundApplyRecordResultVO;
        }
        List<Integer> accountIdList = new ArrayList<>();
        List<Integer> companyIdList = new ArrayList<>();
        List<Integer> fromSiteIdList = new ArrayList<>();

        orderRefundApplyResultDTO.getOrderRefundApplyRecordList().stream().forEach(orderRefundApplyRecord -> {
            accountIdList.add(orderRefundApplyRecord.getAccountId());
            companyIdList.add(orderRefundApplyRecord.getCompanyId());
            fromSiteIdList.add(orderRefundApplyRecord.getFromSite());
        });

        AccountQueryParam accountQueryParam = new AccountQueryParam();
        accountQueryParam.setIds(accountIdList);
        List<Account> accountList = accountService.listAccount(accountQueryParam);
        Map<Integer, Account> accountMap = accountList.stream().collect(Collectors.toMap(Account::getId, Function.identity()));

        List<HospitalCompany> hospitalCompanyList = hospitalCompanyService.listHospitalCompanyByIds(companyIdList);
        Map<Integer, HospitalCompany> companyMap = hospitalCompanyList.stream().collect(Collectors.toMap(HospitalCompany::getId, Function.identity()));

        List<Hospital> fromSiteList = hospitalService.getHospitalsByIds(fromSiteIdList);
        Map<Integer, Hospital> fromSiteMap = fromSiteList.stream().collect(Collectors.toMap(Hospital::getId, Function.identity()));

        orderRefundApplyRecordResultVO = convertToOrderRefundApplyRecordResultVO(accountMap, companyMap, fromSiteMap, orderRefundApplyResultDTO);
        return orderRefundApplyRecordResultVO;
    }

    @RequestMapping(value = "/agreeRefund", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void agreeRefund(@RequestBody(required = true)BatchOrderRefundAuditVO batchOrderRefundAuditVO, HttpSession session) throws OrderException {
        BatchOrderRefundAuditDTO batchOrderRefundAuditDTO = new BatchOrderRefundAuditDTO();
        batchOrderRefundAuditDTO.setOrderNumList(batchOrderRefundAuditVO.getOrderNumList());
        batchOrderRefundAuditDTO.setReason(batchOrderRefundAuditVO.getReason());
        Employee employee = SessionUtil.getEmployee();
        batchOrderRefundAuditDTO.setOpertor(employee.getId());
        batchOrderRefundAuditDTO.setOperatorName(employee.getEmployeeName());
        orderRefundApplyService.agreeRefund(batchOrderRefundAuditDTO);
    }

    @RequestMapping(value = "/refuseRefund", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void refuseRefund(@RequestBody(required = true)BatchOrderRefundAuditVO batchOrderRefundAuditVO, HttpSession session) throws OrderException {
        BatchOrderRefundAuditDTO batchOrderRefundAuditDTO = new BatchOrderRefundAuditDTO();
        batchOrderRefundAuditDTO.setOrderNumList(batchOrderRefundAuditVO.getOrderNumList());
        batchOrderRefundAuditDTO.setReason(batchOrderRefundAuditVO.getReason());
        Employee employee = SessionUtil.getEmployee();
        batchOrderRefundAuditDTO.setOpertor(employee.getId());
        batchOrderRefundAuditDTO.setOperatorName(employee.getEmployeeName());
        orderRefundApplyService.refuseRefund(batchOrderRefundAuditDTO);
    }

    private OrderRefundApplyQueryDTO convertToOrderRefundApplyQueryDTO(OrderRefundApplyQueryVO orderRefundApplyQueryVO) {
        OrderRefundApplyQueryDTO orderRefundApplyQueryDTO = new OrderRefundApplyQueryDTO();
        List<Integer> accountIdList = getAccountId(orderRefundApplyQueryVO.getAccount());
        orderRefundApplyQueryDTO.setAccountIdList(accountIdList);
        orderRefundApplyQueryDTO.setFromSite(orderRefundApplyQueryVO.getFromSite());
        orderRefundApplyQueryDTO.setHospitalCompanyId(orderRefundApplyQueryVO.getHospitalCompanyId());
        orderRefundApplyQueryDTO.setApplyStartTime(orderRefundApplyQueryVO.getApplyStartTime());
        orderRefundApplyQueryDTO.setApplyEndTime(orderRefundApplyQueryVO.getApplyEndTime());
        orderRefundApplyQueryDTO.setAuditStartTime(orderRefundApplyQueryVO.getAuditStartTime());
        orderRefundApplyQueryDTO.setAuditEndTime(orderRefundApplyQueryVO.getAuditEndTime());
        orderRefundApplyQueryDTO.setMinOnlinePay(orderRefundApplyQueryVO.getMinOnlinePay());
        orderRefundApplyQueryDTO.setMaxOnlinePay(orderRefundApplyQueryVO.getMaxOnlinePay());
        orderRefundApplyQueryDTO.setRefundType(orderRefundApplyQueryVO.getRefundType());
        orderRefundApplyQueryDTO.setScene(orderRefundApplyQueryVO.getScene());
        orderRefundApplyQueryDTO.setPageSize(orderRefundApplyQueryVO.getPageSize());
        orderRefundApplyQueryDTO.setCurrentPage(orderRefundApplyQueryVO.getCurrentPage());
        orderRefundApplyQueryDTO.setStatusList(orderRefundApplyQueryVO.getStatusList());


        if(orderRefundApplyQueryVO.getFromSite() == null){
            HospitalQueryDto hospitalQueryDto = new HospitalQueryDto();
            hospitalQueryDto.setOrgType(OrganizationTypeEnum.HOSPITAL.getCode());
            hospitalQueryDto.setCityId(orderRefundApplyQueryVO.getCityId());
            hospitalQueryDto.setDistrictId(orderRefundApplyQueryVO.getDistrictId());
            hospitalQueryDto.setProvinceId(orderRefundApplyQueryVO.getProvinceId());
            List<OrganizationVO> organizationVOList = hospitalFacade.listHospital(hospitalQueryDto);

            List<Integer> organizationIds = new ArrayList<>();
            for (OrganizationVO organizationVO : organizationVOList) {
                organizationIds.add(organizationVO.getId());
            }

            orderRefundApplyQueryDTO.setFromSiteList(organizationIds);
        }

        return orderRefundApplyQueryDTO;
    }

    private List<Integer> getAccountId(String account) {
        if(StringUtils.isBlank(account)){
            return null;
        }
        AccountQueryParam accountQueryParam = new AccountQueryParam();
        accountQueryParam.setSearchKey(account);
        List<Account> accountList = accountService.listAccount(accountQueryParam);
        if(accountList == null){
            return Collections.EMPTY_LIST;
        }
        return accountList.stream().map(Account::getId).collect(Collectors.toList());
    }



    private OrderRefundApplyResultVO convertToOrderRefundApplyResultVO(Map<Integer, Account> accountMap, Map<Integer, HospitalCompany> companyMap, Map<Integer, Hospital> fromSiteMap, OrderRefundApplyResultDTO orderRefundApplyResultDTO) {
        OrderRefundApplyResultVO orderRefundApplyResultVO = new OrderRefundApplyResultVO();
        orderRefundApplyResultVO.setPage(orderRefundApplyResultDTO.getPage());

        List<OrderRefundApplyVO> orderRefundApplyVOList = new ArrayList<>();
        for(OrderRefundApply orderRefundApply : orderRefundApplyResultDTO.getOrderRefundApplyList()){
            OrderRefundApplyVO orderRefundApplyVO = new OrderRefundApplyVO();
            orderRefundApplyVO.setAccountInfo(accountMap.get(orderRefundApply.getAccountId()));
            orderRefundApplyVO.setAmount(orderRefundApply.getAmount());
            orderRefundApplyVO.setApplyTime(orderRefundApply.getApplyTime());
            orderRefundApplyVO.setFromSiteInfo(fromSiteMap.get(orderRefundApply.getFromSite()));
            orderRefundApplyVO.setHospitalCompanyInfo(companyMap.get(orderRefundApply.getCompanyId()));
            orderRefundApplyVO.setOrderNum(orderRefundApply.getOrderNum());
            orderRefundApplyVO.setPayDetail(orderRefundApply.getPayDetail());
            orderRefundApplyVO.setRefundType(orderRefundApply.getRefundType());
            orderRefundApplyVOList.add(orderRefundApplyVO);
        }
        orderRefundApplyResultVO.setRecords(orderRefundApplyVOList);
        return orderRefundApplyResultVO;
    }



    private OrderRefundApplyRecordQueryDTO convertToOrderRefundApplyRecordQueryDTO(OrderRefundApplyRecordQueryVO orderRefundApplyRecordQueryVO) {
        OrderRefundApplyRecordQueryDTO orderRefundApplyRecordQueryDTO = new OrderRefundApplyRecordQueryDTO();
        List<Integer> accountIdList = getAccountId(orderRefundApplyRecordQueryVO.getAccount());
        orderRefundApplyRecordQueryDTO.setAccountIdList(accountIdList);
        orderRefundApplyRecordQueryDTO.setFromSite(orderRefundApplyRecordQueryVO.getFromSite());
        orderRefundApplyRecordQueryDTO.setHospitalCompanyId(orderRefundApplyRecordQueryVO.getHospitalCompanyId());
        orderRefundApplyRecordQueryDTO.setApplyStartTime(orderRefundApplyRecordQueryVO.getApplyStartTime());
        orderRefundApplyRecordQueryDTO.setApplyEndTime(orderRefundApplyRecordQueryVO.getApplyEndTime());
        orderRefundApplyRecordQueryDTO.setAuditStartTime(orderRefundApplyRecordQueryVO.getAuditStartTime());
        orderRefundApplyRecordQueryDTO.setAuditEndTime(orderRefundApplyRecordQueryVO.getAuditEndTime());
        orderRefundApplyRecordQueryDTO.setMinOnlinePay(orderRefundApplyRecordQueryVO.getMinOnlinePay());
        orderRefundApplyRecordQueryDTO.setMaxOnlinePay(orderRefundApplyRecordQueryVO.getMaxOnlinePay());
        orderRefundApplyRecordQueryDTO.setScene(orderRefundApplyRecordQueryVO.getScene());
        orderRefundApplyRecordQueryDTO.setPageSize(orderRefundApplyRecordQueryVO.getPageSize());
        orderRefundApplyRecordQueryDTO.setCurrentPage(orderRefundApplyRecordQueryVO.getCurrentPage());
        orderRefundApplyRecordQueryDTO.setStatusList(orderRefundApplyRecordQueryVO.getStatusList());



        if(orderRefundApplyRecordQueryVO.getFromSite() == null){
            HospitalQueryDto hospitalQueryDto = new HospitalQueryDto();
            hospitalQueryDto.setOrgType(OrganizationTypeEnum.HOSPITAL.getCode());
            hospitalQueryDto.setCityId(orderRefundApplyRecordQueryVO.getCityId());
            hospitalQueryDto.setDistrictId(orderRefundApplyRecordQueryVO.getDistrictId());
            hospitalQueryDto.setProvinceId(orderRefundApplyRecordQueryVO.getProvinceId());
            List<OrganizationVO> organizationVOList = hospitalFacade.listHospital(hospitalQueryDto);
            List<Integer> organizationIds = new ArrayList<>();
            for (OrganizationVO organizationVO : organizationVOList) {
                organizationIds.add(organizationVO.getId());
            }

            orderRefundApplyRecordQueryDTO.setFromSiteList(organizationIds);
        }

        return orderRefundApplyRecordQueryDTO;
    }

    private OrderRefundApplyRecordResultVO convertToOrderRefundApplyRecordResultVO(Map<Integer, Account> accountMap, Map<Integer, HospitalCompany> companyMap, Map<Integer, Hospital> fromSiteMap, OrderRefundApplyRecordResultDTO orderRefundApplyResultDTO) {
        OrderRefundApplyRecordResultVO orderRefundApplyRecordResultVO = new OrderRefundApplyRecordResultVO();
        orderRefundApplyRecordResultVO.setPage(orderRefundApplyResultDTO.getPage());

        List<OrderRefundApplyRecordVO> orderRefundApplyRecordVOList = new ArrayList<>();
        for(OrderRefundApplyRecord orderRefundApplyRecord : orderRefundApplyResultDTO.getOrderRefundApplyRecordList()){
            OrderRefundApplyRecordVO orderRefundApplyRecordVO = new OrderRefundApplyRecordVO();
            orderRefundApplyRecordVO.setAccountInfo(accountMap.get(orderRefundApplyRecord.getAccountId()));
            orderRefundApplyRecordVO.setAmount(orderRefundApplyRecord.getAmount());
            orderRefundApplyRecordVO.setApplyTime(orderRefundApplyRecord.getApplyTime());
            orderRefundApplyRecordVO.setFromSiteInfo(fromSiteMap.get(orderRefundApplyRecord.getFromSite()));
            orderRefundApplyRecordVO.setHospitalCompanyInfo(companyMap.get(orderRefundApplyRecord.getCompanyId()));
            orderRefundApplyRecordVO.setPayDetail(orderRefundApplyRecord.getPayDetail());
            orderRefundApplyRecordVO.setAuditTime(orderRefundApplyRecord.getAuditTime());
            orderRefundApplyRecordVO.setOperator(orderRefundApplyRecord.getOperator());
            orderRefundApplyRecordVO.setOperatorName(orderRefundApplyRecord.getOperatorName());
            orderRefundApplyRecordVO.setStatus(orderRefundApplyRecord.getStatus());
            orderRefundApplyRecordVO.setReason(orderRefundApplyRecord.getReason());
            orderRefundApplyRecordVOList.add(orderRefundApplyRecordVO);
        }
        orderRefundApplyRecordResultVO.setRecords(orderRefundApplyRecordVOList);
        return orderRefundApplyRecordResultVO;
    }
}
