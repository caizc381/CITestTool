package com.mytijian.admin.web.controller.work;

import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.api.rbac.service.EmployeeService;
import com.mytijian.admin.web.vo.work.JointCostVo;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.work.service.jointcost.model.JointCostDetailDTO;
import com.mytijian.work.service.jointcost.model.JointCostQuery;
import com.mytijian.work.service.jointcost.model.JointCostSumDTO;
import com.mytijian.work.service.jointcost.model.JointCostTypeDTO;
import com.mytijian.work.service.jointcost.service.JointCostService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaosiqi 2018/2/5 16:31
 */
@Controller
@RequestMapping("/work")
public class JointCostController {
    private final static Logger logger = LoggerFactory.getLogger(JointCostController.class);

    @Resource(name= "jointCostService" )
    private JointCostService jointCostService;

    @Value("${temp.folder}")
    private String tempFolder;

    @Resource(name = "employeeService")
    private EmployeeService employeeService;

    @RequestMapping(value = "/listCostType",method = RequestMethod.POST)
    @ResponseBody
    public List<JointCostTypeDTO> listCostType(){
        return jointCostService.listAllCostType();
    }

    @RequestMapping(value = "/addCostType",method = RequestMethod.POST)
    @ResponseBody
    public boolean addCostType(String name){
        return jointCostService.addCostType(name);
    }

    @RequestMapping(value = "/listCostDetail",method = RequestMethod.POST)
    @ResponseBody
    public JointCostVo listCostDetail(@RequestBody(required = true) JointCostQuery query){
        PageView<JointCostDetailDTO> detailPage = jointCostService.listJointCostDetail(query);
        List<JointCostSumDTO> sumList = jointCostService.listSum(query);
        JointCostVo jointCostVo = new JointCostVo();
        jointCostVo.setJointCostDetailDTO(detailPage);
        jointCostVo.setJointCostSumDTO(sumList);
        return jointCostVo;
    }

    @RequestMapping(value = "/addCostDetail",method = RequestMethod.POST)
    @ResponseBody
    public boolean addCostDetail(@RequestBody(required = true) JointCostDetailDTO dto){
        /*List<Integer> ids = new ArrayList<>();
        ids.add(dto.getRecordUserId());
        ids.add(dto.getApplyUserId());
        List<Employee> list = employeeService.listEmployeesByIds(ids);*/
        jointCostService.addCostDetail(dto);
        return true;
    }

    @RequestMapping(value = "/getManager",method = RequestMethod.GET)
    @ResponseBody
    public Employee getManager(Integer hospitalId){
        Employee employee = employeeService.getOperationByHospitalId(hospitalId);
        return employee;
    }

    @RequestMapping(value = "/jointCostInfoExport" , method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void exportJointCostInfo(JointCostQuery query,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("jointCostTemplate/jointCostExport_template.xls");
        File targetFile = new File("jointCostExport_template.xls");
        FileUtils.copyInputStreamToFile(stream, targetFile);
        FileInputStream fileInputStream = new FileInputStream(targetFile);
        HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
        createInfoSheet(workbook,query);
        String fileName = tempFolder + LocalDate.now() + "对接费用.xls";
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        String origin = response.getHeader("Access-Control-Allow-Origin");
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=" +
                new String((LocalDate.now() + "对接费用.xls").getBytes("GBK"), "ISO-8859-1"));
        response.addHeader("Access-Control-Allow-Credentials","true");
        response.addHeader("Access-Control-Allow-Origin",origin);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        try {
            OutputStream outputStreamo = response.getOutputStream();
            FileInputStream fInputStream = new FileInputStream(new File(fileName));
            IOUtils.copy(fInputStream, outputStreamo);
            outputStreamo.flush();
            outputStreamo.close();
            fInputStream.close();
            stream.close();
        } catch (Exception e) {
            logger.error("jointCost export error", e);
        }

    }

    private void createInfoSheet( HSSFWorkbook workbook, JointCostQuery query)
            throws IllegalAccessException, InvocationTargetException {
        HSSFSheet sheet = workbook.getSheetAt(0);
        List<JointCostDetailDTO> list = jointCostService.listCostDetailWithExcel(query);
        int row = 0;
        SimpleDateFormat sdf =  new SimpleDateFormat( " yyyy-MM-dd " );
        for(JointCostDetailDTO detailDTO : list){
            row += 1;
            HSSFRow hssfrow = sheet.createRow(row);
            hssfrow.createCell(0).setCellValue(detailDTO.getId());
            hssfrow.createCell(1).setCellValue(sdf.format(detailDTO.getProduceTime()));
            hssfrow.createCell(2).setCellValue(detailDTO.getHospitalSimple().getName());
            hssfrow.createCell(3).setCellValue(detailDTO.getJointCostTypeDTO().getName());
            hssfrow.createCell(4).setCellValue(detailDTO.getMoney());
            hssfrow.createCell(5).setCellValue(detailDTO.getRemark());
            hssfrow.createCell(6).setCellValue(detailDTO.getApplyUser());
            hssfrow.createCell(7).setCellValue(sdf.format(detailDTO.getCreateTime()));
            hssfrow.createCell(8).setCellValue(detailDTO.getRecordUser());
        }
    }
}
