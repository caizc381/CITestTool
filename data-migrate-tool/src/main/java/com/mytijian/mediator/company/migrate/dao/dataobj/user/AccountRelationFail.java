/**
 *
 */
package com.mytijian.mediator.company.migrate.dao.dataobj.user;

/**
 * 账户批量导入文件记录信息.
 *
 * @author ren
 */
public class AccountRelationFail extends AccountRelationSuper {

//    private Integer id;
//
//    // 客户经理
//    private Integer managerId;

    // 用户标识
    private Integer accountId;

    // 体检单位(改造前老单位)
//    private Integer companyId;
//
//    // 体检单位
//    private Integer examCompanyId;
//
//    // 机构ID
//    private Integer organizationId;
//
//    // 机构类型
//    private Integer organizationType;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

}
