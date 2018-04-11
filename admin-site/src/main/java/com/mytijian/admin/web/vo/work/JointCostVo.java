package com.mytijian.admin.web.vo.work;

import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.work.service.jointcost.model.JointCostDetailDTO;
import com.mytijian.work.service.jointcost.model.JointCostSumDTO;

import java.util.List;

/**
 * @author zhaosiqi 2018/2/5 18:18
 */
public class JointCostVo {
    private PageView<JointCostDetailDTO> jointCostDetailDTO;

    private List<JointCostSumDTO> jointCostSumDTO;

    public JointCostVo() {
    }

    public PageView<JointCostDetailDTO> getJointCostDetailDTO() {
        return jointCostDetailDTO;
    }

    public List<JointCostSumDTO> getJointCostSumDTO() {
        return jointCostSumDTO;
    }

    public void setJointCostDetailDTO(PageView<JointCostDetailDTO> jointCostDetailDTO) {
        this.jointCostDetailDTO = jointCostDetailDTO;
    }

    public void setJointCostSumDTO(List<JointCostSumDTO> jointCostSumDTO) {
        this.jointCostSumDTO = jointCostSumDTO;
    }
}
