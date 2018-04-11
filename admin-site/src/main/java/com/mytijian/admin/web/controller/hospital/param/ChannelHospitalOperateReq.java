package com.mytijian.admin.web.controller.hospital.param;

import java.util.List;

/**
 * 渠道医院操作对象
 * @author king
 */
public class ChannelHospitalOperateReq {

    private Integer channelId;

    private List<Integer> hospitalIds;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public List<Integer> getHospitalIds() {
        return hospitalIds;
    }

    public void setHospitalIds(List<Integer> hospitalIds) {
        this.hospitalIds = hospitalIds;
    }
}
