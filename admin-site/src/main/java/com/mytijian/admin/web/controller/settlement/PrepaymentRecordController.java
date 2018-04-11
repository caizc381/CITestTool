package com.mytijian.admin.web.controller.settlement;

import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.trade.settlement.dto.PrepaymentRecordQueryDTO;
import com.mytijian.trade.settlement.enums.PrepaymentRecordTypeEnum;
import com.mytijian.trade.settlement.enums.SettlementHospitalConfirmEnum;
import com.mytijian.trade.settlement.model.TradePrepaymentRecord;
import com.mytijian.trade.settlement.service.TradePrepaymentRecordService;
import com.mytijian.util.AssertUtil;
import com.mytijian.web.intercepter.Token;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/settlement")
public class PrepaymentRecordController {

    @Resource(name = "tradePrepaymentRecordService")
    private TradePrepaymentRecordService tradePrepaymentRecordService;

    /**
     * 添加预付款
     *
     * @param tradePrepaymentRecord
     */
    @RequestMapping(value = "/addPrepaymentRecord", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @Token
    public void addPrepaymentRecord(@RequestBody TradePrepaymentRecord tradePrepaymentRecord,
                                    HttpSession session) {
        Employee employee = (Employee) session.getAttribute("employee");
        tradePrepaymentRecord.setOperatorId(employee.getId());
        tradePrepaymentRecord.setOperatorName(employee.getEmployeeName());
        tradePrepaymentRecord.setStatus(SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());
        tradePrepaymentRecord.setType(PrepaymentRecordTypeEnum.SPECIAL_REFUND.getCode());
        tradePrepaymentRecordService.addTradePrepaymentRecord(tradePrepaymentRecord);
    }

    /**
     * 获取预付款列表
     *
     * @param prepaymentRecordQueryDTO
     * @return
     */
    @RequestMapping(value = "/getPrepaymentRecordList", method = RequestMethod.POST)
    @ResponseBody
    public PageView<TradePrepaymentRecord> getPrepaymentRecordList(@RequestBody PrepaymentRecordQueryDTO prepaymentRecordQueryDTO) {

        PageView<TradePrepaymentRecord> tradePrepaymentRecordPageView = tradePrepaymentRecordService.getTradePrepaymentRecordByPage(prepaymentRecordQueryDTO);
        List<TradePrepaymentRecord> tradePrepaymentRecordLsit = tradePrepaymentRecordPageView.getRecords();
        if (AssertUtil.isNotEmpty(tradePrepaymentRecordLsit)) {
            tradePrepaymentRecordLsit.forEach(tradePrepaymentRecord -> {
                if (tradePrepaymentRecord.getIsDeleted() == 0) {
                    if (tradePrepaymentRecord.getStatus() == SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode() ||
                            tradePrepaymentRecord.getStatus() == SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()) {
                        tradePrepaymentRecord.setSettlementStatus(0);
                    } else {
                        tradePrepaymentRecord.setSettlementStatus(1);
                    }
                }
                if (tradePrepaymentRecord.getIsDeleted() == 1) {
                    tradePrepaymentRecord.setSettlementStatus(2);
                }
            });
        }
        tradePrepaymentRecordPageView.setRecords(tradePrepaymentRecordLsit);
        return tradePrepaymentRecordPageView;
    }

    /**
     * 更新预付款
     *
     * @param tradePrepaymentRecord
     */
    @RequestMapping(value = "/updatePrepaymentRecord", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @Token
    public void updatePrepaymentRecord(@RequestBody TradePrepaymentRecord tradePrepaymentRecord) {
        tradePrepaymentRecordService.updateTradePrepaymentRecord(tradePrepaymentRecord);
    }

    /**
     * 删除预付款
     *
     * @param prepaymentRecordId
     */
    @RequestMapping(value = "/deletePrepaymentRecord", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void deletePrepaymentRecord(@RequestParam(value = "prepaymentRecordId") Integer prepaymentRecordId) {
        tradePrepaymentRecordService.deleteTradePrepaymentRecord(prepaymentRecordId);
    }

}
