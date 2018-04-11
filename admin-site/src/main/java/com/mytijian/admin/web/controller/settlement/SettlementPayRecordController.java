package com.mytijian.admin.web.controller.settlement;

import com.mytijian.admin.web.dto.HospitalQueryDto;
import com.mytijian.admin.web.facade.hospital.HospitalFacade;
import com.mytijian.admin.web.vo.resource.OrganizationVO;
import com.mytijian.exportexcel.ExportDTO;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.resource.enums.OrganizationTypeEnum;
import com.mytijian.trade.settlement.dto.OSSConfigDTO;
import com.mytijian.trade.settlement.dto.SettlementPayRecordQueryDTO;
import com.mytijian.trade.settlement.model.DownloadBillDTO;
import com.mytijian.trade.settlement.model.TradeSettlementPayRecord;
import com.mytijian.trade.settlement.service.HospitalSettlementService;
import com.mytijian.trade.settlement.service.TradeSettlementPayRecordService;
import com.mytijian.trade.settlement.util.OSSUtil;
import com.mytijian.util.AssertUtil;
import com.mytijian.util.ExportExcelUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/settlement")
public class SettlementPayRecordController {

    private final static Logger logger = LoggerFactory.getLogger(SettlementPayRecordController.class);

    @Value("${temp.folder}")
    private String tempFolder;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucket}")
    private String bucket;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Resource(name = "hospitalFacade")
    private HospitalFacade hospitalFacade;

    @Resource(name = "hospitalSettlementService")
    private HospitalSettlementService hospitalSettlementService;

    @Resource(name = "tradeSettlementPayRecordService")
    private TradeSettlementPayRecordService tradeSettlementPayRecordService;

    /**
     * 获取结算支付记录
     *
     * @param settlementPayRecordQueryDTO
     * @return
     */
    @RequestMapping(value = "/getSettlementPayRecordList", method = RequestMethod.POST)
    @ResponseBody
    public PageView<TradeSettlementPayRecord> getSettlementPayRecordList(@RequestBody SettlementPayRecordQueryDTO settlementPayRecordQueryDTO) {
        HospitalQueryDto hospitalQueryDto = new HospitalQueryDto();
        BeanUtils.copyProperties(settlementPayRecordQueryDTO, hospitalQueryDto);
        hospitalQueryDto.setOrgType(OrganizationTypeEnum.HOSPITAL.getCode());
        List<OrganizationVO> organizationVOList = hospitalFacade.listHospital(hospitalQueryDto);
        List<Integer> organizationIds = new ArrayList<>();
        for (OrganizationVO organizationVO : organizationVOList) {
            organizationIds.add(organizationVO.getId());
        }
        if (AssertUtil.isEmpty(organizationIds)) {
            organizationIds.add(-1);
        }
        settlementPayRecordQueryDTO.setOrganizationIds(organizationIds);
        PageView<TradeSettlementPayRecord> tradeSettlementPayRecordPageView = tradeSettlementPayRecordService.getTradeSettlementPayRecordList(settlementPayRecordQueryDTO);
        return tradeSettlementPayRecordPageView;
    }

    /**
     * 上传付款凭证
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    @ResponseBody
    public String uploadImage(MultipartFile file) throws IOException {
        OSSConfigDTO ossConfigDTO = new OSSConfigDTO();
        ossConfigDTO.setEndpoint(endpoint);
        ossConfigDTO.setBucket(bucket);
        ossConfigDTO.setAccessKeyId(accessKeyId);
        ossConfigDTO.setAccessKeySecret(accessKeySecret);
        File tempFile = new File("/tmp/" +file.getOriginalFilename());
        file.transferTo(tempFile);
        String str = OSSUtil.uploadFile(ossConfigDTO, tempFile, "settlement/certificate/");
        return str;
    }

    /**
     * 下载对账单
     * 
     * @param downloadBillDTO
     * @param response
     */
    @RequestMapping(value = "/downloadSettlementBill", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void downloadSettlementBill(DownloadBillDTO downloadBillDTO, HttpServletResponse response){
        ExportDTO exportDTO = hospitalSettlementService.downloadSettlementBill(downloadBillDTO);

        Date now = new Date();
        String filePath = tempFolder  + downloadBillDTO.getHospitalId() + "_" + now.getTime() + File.separator;
        String fileName = "checkbook" + now.getTime() + ".xls";

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            ExportExcelUtil.exportExcel(exportDTO, filePath, fileName);

            inputStream = new BufferedInputStream(new FileInputStream(filePath + fileName));
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            String origin = response.getHeader("Access-Control-Allow-Origin");

            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Access-Control-Allow-Credentials","true");
            response.addHeader("Access-Control-Allow-Origin",origin);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");

            outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(buffer);
            outputStream.flush();
        } catch (Exception e) {
            logger.error("create settlement bill file failed!, exception: {}", e);
        } finally {
            try {
                inputStream.close();
                outputStream.close();
                FileUtils.forceDelete(new File((filePath)));
            } catch (Exception e) {
                logger.error("delete settlement bill file failed!, exception: {}", e);
            }
        }
    }
}
