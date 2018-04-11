package com.mytijian.mediator.company.migrate.constant;

/**
 * Created by Administrator on 2017/5/10.
 */
public class UserConstant {
    /**
     * 需要记录迁移日志的表名
     */
    public static final String TB_ACCOUNT_RELATIONSHIP = "tb_account_relationship";
    public static final String TB_ACCOUNT_RELATIONSHIP_FAIL = "tb_account_relationship_fail";
    public static final String TB_IMPORT_GROUP_SEQ = "tb_import_group_seq";
    /**
     * 体检中心散客单位对应的tb_exam_company_id
     */
    public static final Integer HOSPITAL_TB_EXAM_COMPANY_ID_WSGRYY = -100;       //网上预约个人
    public static final Integer HOSPITAL_TB_EXAM_COMPANY_ID_GR = -101;           //个人
    public static final Integer HOSPITAL_TB_EXAM_COMPANY_ID_MTJK = -102;         //每天健康
    /**
     * 渠道商散客单位对应的tb_exam_company_id
     */
    public static final Integer CHANNEL_TB_EXAM_COMPANY_ID_MTJK = -103;         //个人网上预约
    public static final Integer CHANNEL_TB_EXAM_COMPANY_ID_SKDW = -104;         //散客单位

    public static final Integer GUEST_COMPANY_ID = 1585;         //原来的散客单位
}
