package com.mytijian.admin.web.vo.order;

import com.mytijian.order.model.MongoOrder;

import java.io.Serializable;

/**
 * 订单列表视图对象
 * @author fanheyan
 * @date 2017/12/27.
 */
public class OrderListVO extends MongoOrder implements Serializable {

    private static final long serialVersionUID = 2451282029281096384L;

    /**
     * 医院是否开启了结算功能
     */
    private Boolean hasSettlementOpen;
    /**
     * 退款场景:5 表示手动退款（订单列表标示是否是手动退款使用）
     *  请查看com.mytijian.trade.refund.constant.RefundConstants.RefundScene
     */
    private Integer refundScene;


    public Boolean getHasSettlementOpen() {
        return hasSettlementOpen;
    }

    public void setHasSettlementOpen(Boolean hasSettlementOpen) {
        this.hasSettlementOpen = hasSettlementOpen;
    }

    public Integer getRefundScene() {
        return refundScene;
    }

    public void setRefundScene(Integer refundScene) {
        this.refundScene = refundScene;
    }
}
