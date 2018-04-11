package com.mytijian.admin.web.controller.hospital;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.util.ExcelUtil;
import com.mytijian.admin.web.controller.hospital.param.ChannelHospitalOperateReq;
import com.mytijian.admin.web.controller.hospital.param.ChannelHospitalQuery;
import com.mytijian.admin.web.controller.hospital.util.PlatformChannelDiscountUtil;
import com.mytijian.admin.web.controller.hospital.vo.ChannelHospitalVO;
import com.mytijian.organization.param.OrganizationHosRelationQuery;
import com.mytijian.organization.param.OrganizationQuery;
import com.mytijian.organization.service.OrganizationHospitalRelationService;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.OrganizationHospitalRelation;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.util.DateUtils;

/**
 * 渠道管理controller
 *
 * @author king
 */
@RestController
public class OrganizationHospitalRelationController {

    @Resource
    private HospitalService hospitalService;


    @Resource
    private OrganizationHospitalRelationService organizationHospitalRelationService;

    /**
     * 渠道管理页面体检中心分配，获取渠道列表
     *
     * @return
     */
    @GetMapping("/listChannel")
    public List<Hospital> listChannel() {
        return hospitalService.getOrganizationList(OrganizationTypeEnum.CHANNEL.getCode());
    }


    /**
     * 渠道分配页面展示渠道医院关系
     *
     * @param channelHospitalQuery
     * @return
     */
    @PostMapping("/listChannelHospitals")
    PageView<ChannelHospitalVO> listChannelHospitals(@RequestBody ChannelHospitalQuery channelHospitalQuery) {

        List<ChannelHospitalVO> records = listChannelHospitalVO(channelHospitalQuery);

        //分页
        Page page = channelHospitalQuery.getPage();
        if (page != null && !CollectionUtils.isEmpty(records)) {
            page.setRowCount(records.size());
            records = records.stream().skip(page.getPageSize() * (page.getCurrentPage() - 1))
                    .limit(page.getPageSize())
                    .collect(Collectors.toList());
        }
        return new PageView<>(records, page);
    }

    private List<ChannelHospitalVO> listChannelHospitalVO(ChannelHospitalQuery channelHospitalQuery) {

        //查询数据
        OrganizationQuery organizationQuery = new OrganizationQuery();
        organizationQuery.setShowInList(channelHospitalQuery.getShowInList());
        organizationQuery.setAddressId(channelHospitalQuery.getAddressId());
        organizationQuery.setHospitalId(channelHospitalQuery.getHospitalId());
        organizationQuery.setOrderById(1);
        organizationQuery.setOrganizationType(OrganizationTypeEnum.HOSPITAL.getCode());
        List<Hospital> hospitalList = hospitalService.listHospitalInfo(organizationQuery).getRecords();

        //查询关系
        OrganizationHosRelationQuery organizationHosRelationQuery = new OrganizationHosRelationQuery();
        organizationHosRelationQuery.setOrganizationId(channelHospitalQuery.getChannelId());
//        organizationHosRelationQuery.setStatus(channelHospitalQuery.getStatus());
//        organizationHosRelationQuery.setIsCancel(channelHospitalQuery.getIsCancel());
        List<OrganizationHospitalRelation> relations = organizationHospitalRelationService.listOrganizationHospitalRelation(organizationHosRelationQuery);

        //组装数据
        List<ChannelHospitalVO> records = buildChannelHospitalVOS(hospitalList, relations);

        //过滤数据
        records.removeIf(record -> !filter(record, channelHospitalQuery));

        return records;
    }

    private List<ChannelHospitalVO> buildChannelHospitalVOS(List<Hospital> hospitalList, List<OrganizationHospitalRelation> relations) {
        Map<Integer, OrganizationHospitalRelation> relationMap = relations.stream().collect(Collectors.toMap(OrganizationHospitalRelation::getHospitalId, val -> val));
        List<ChannelHospitalVO> records = Lists.newArrayList();
        for (Hospital hospital : hospitalList) {
            ChannelHospitalVO channelHospitalVO = new ChannelHospitalVO();
            channelHospitalVO.setHospital(hospital);
            channelHospitalVO.setOrganizationHospitalRelation(relationMap.get(hospital.getId()));
            records.add(channelHospitalVO);
        }
        return records;
    }


    private boolean filter(ChannelHospitalVO channelHospitalVO, ChannelHospitalQuery channelHospitalQuery) {

        //关系表有数据
        OrganizationHospitalRelation relation = channelHospitalVO.getOrganizationHospitalRelation();
        boolean companyDiscount = true;
        boolean guestDiscount = true;
        boolean assign = true;
        boolean status = true;
        if (relation != null) {

            //查询了单位折扣
            if (channelHospitalQuery.getCompanyDiscount() != null) {

                if (channelHospitalQuery.getCompanyDiscount()) {
                    companyDiscount = relation.getPlatformChannelCompDiscount() != null;
                } else {
                    companyDiscount = relation.getPlatformChannelCompDiscount() == null;
                }
            }

            //查询了散客折扣
            if (channelHospitalQuery.getGuestDiscount() != null) {
                if (channelHospitalQuery.getGuestDiscount()) {
                    guestDiscount = relation.getPlatformChannelGuestDiscount() != null;
                } else {
                    guestDiscount = relation.getPlatformChannelGuestDiscount() == null;
                }
            }

            //查询是否分配
            if (channelHospitalQuery.getIsCancel() != null) {
                assign = channelHospitalQuery.getIsCancel().equals(relation.getIsCancel());
            }

            //查询了状态
            if (channelHospitalQuery.getStatus() != null) {
                status = channelHospitalQuery.getStatus().equals(relation.getStatus());
            }
        } else {
            //没有relation
            //查询了单位折扣
            if (channelHospitalQuery.getCompanyDiscount() != null) {
                companyDiscount = !channelHospitalQuery.getCompanyDiscount();
            }

            //查询了散客折扣
            if (channelHospitalQuery.getGuestDiscount() != null) {
                guestDiscount = !channelHospitalQuery.getGuestDiscount();
            }

            //查询是否分配
            if (channelHospitalQuery.getIsCancel() != null) {
                assign = channelHospitalQuery.getIsCancel();
            }

            //查询了状态
            if (channelHospitalQuery.getStatus() != null) {
                status = channelHospitalQuery.getStatus() != 1;
            }

        }

        return companyDiscount && guestDiscount && assign && status;
    }


    /**
     * 分配医院给体检中心
     *
     * @param channelHospitalOperateReq 参数对象
     */
    @PostMapping("/allocateHospitals")
    public void allocateHospitals(@RequestBody ChannelHospitalOperateReq channelHospitalOperateReq, HttpSession session) {
        Employee employee = (Employee) session.getAttribute("employee");
        organizationHospitalRelationService.allocateHospitals(channelHospitalOperateReq.getChannelId(), channelHospitalOperateReq.getHospitalIds(), employee.getId());
    }

    /**
     * 取消分配医院给体检中心
     *
     * @param channelHospitalOperateReq 参数对象
     */
    @PostMapping("/cancelAllocateHospitals")
    public void cancelAllocatedHospitals(@RequestBody ChannelHospitalOperateReq channelHospitalOperateReq, HttpSession session) {
        Employee employee = (Employee) session.getAttribute("employee");
        organizationHospitalRelationService.cancelAllocatedHospitals(channelHospitalOperateReq.getChannelId(), channelHospitalOperateReq.getHospitalIds(), employee.getId());
    }


    /**
     * 重置医院折扣
     *
     * @param channelHospitalOperateReq 参数对象
     */
    @PostMapping("/resetPlatformChannelDisCount")
    public void resetPlatformChannelDisCount(@RequestBody ChannelHospitalOperateReq channelHospitalOperateReq, HttpSession session) {
        Employee employee = (Employee) session.getAttribute("employee");
        organizationHospitalRelationService.resetPlatformChannelDisCount(channelHospitalOperateReq.getChannelId(), channelHospitalOperateReq.getHospitalIds(), employee.getId());
    }

    /**
     * 编辑平台折扣
     *
     * @param relation
     * @param session
     */
    @PostMapping(("/editPlatformChannelDisCount"))
    public void editPlatformChannelDisCount(@RequestBody OrganizationHospitalRelation relation, HttpSession session) {
        Employee employee = (Employee) session.getAttribute("employee");
        organizationHospitalRelationService.editPlatformChannelDiscount(relation, employee.getId());

    }


    /**
     * 导出查看
     */
    @GetMapping(value = "/exportChannelHospitals")
    @ResponseStatus(value = HttpStatus.OK)
    public void exportChannelHospitals(@RequestParam("channelId")Integer channelId,@RequestParam("hospitalIds") List<Integer> hospitalIds, HttpServletResponse response) throws IOException {

        if (channelId == null || CollectionUtils.isEmpty(hospitalIds)) {
            return;
        }

        List<ChannelHospitalVO> channelHospitalVOs = getChannelHospitalVOs(channelId, hospitalIds);



        InputStream tplInputStream = getClass().getClassLoader().getResourceAsStream(
                "qdDiscountTemplate/qd_hospitalrelation_template.xls");
        HSSFWorkbook workbook = ExcelUtil.createHSSFWorkbookByTemplate(PlatformChannelDiscountUtil.convertExportOrganizationChannelRelation(channelHospitalVOs), tplInputStream,hospitalService.getHospitalById(channelId).getName());

        // 输出excel到客户端
        String origin = response.getHeader("Access-Control-Allow-Origin");
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + "qd_discount_" + DateUtils.format(DateUtils.YYYYMMDDSS, new Date())
                        + ".xls");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Origin", origin);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        OutputStream outputStream = response.getOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        IOUtils.copy(bais, outputStream);
        outputStream.flush();
        outputStream.close();
        tplInputStream.close();
        baos.close();
        bais.close();

    }

    private List<ChannelHospitalVO> getChannelHospitalVOs(Integer channelId, List<Integer> hospitalIds) {
        ChannelHospitalQuery channelHospitalQuery = new ChannelHospitalQuery();
        channelHospitalQuery.setChannelId(channelId);
        List<ChannelHospitalVO> channelHospitalVOS = listChannelHospitalVO(channelHospitalQuery);

        channelHospitalVOS.removeIf(channelHospitalVO -> !hospitalIds.contains(channelHospitalVO.getHospital().getId()));

        return channelHospitalVOS;
    }

}

