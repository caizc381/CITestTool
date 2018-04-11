package com.mytijian.mediator.company.migrate.service.impl.user.helpser;

import com.mytijian.company.enums.ExamCompanyTypeEnum;
import com.mytijian.mediator.company.migrate.constant.UserConstant;
import com.mytijian.mediator.company.migrate.dao.*;
import com.mytijian.mediator.company.migrate.dao.dataobj.*;
import com.mytijian.mediator.company.migrate.dao.dataobj.card.CardBase;
import com.mytijian.mediator.company.migrate.dao.dataobj.user.AccountRelationSuper;
import com.mytijian.mediator.company.migrate.dao.dataobj.user.ImportGroupId;
import com.mytijian.mediator.company.migrate.dao.dataobj.user.Role;
import com.mytijian.mediator.company.migrate.dao.user.RoleMapper;
import com.mytijian.mediator.company.migrate.dao.user.UserHelperMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2017/5/11.
 */
@Service
public class UserHelper {
    @Autowired
    private UserHelperMapper userHelperMapper;
    @Autowired
    private HospitalCompanyMapper hospitalCompanyMapper;
    @Autowired
    private ExamCompanyMapper examCompanyMapper;
    @Autowired
    private ChannelCompanyMapper channelCompanyMapper;
    @Autowired
    private PlatformCompanyMapper platformCompanyMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private HospitalMapper hospitalMapper;

    private volatile Map<Integer, Boolean> accountRoles = new HashMap<>();
    private volatile Map<Integer, List<Role>> account_Roles = new HashMap<>();

    private Object obj = new Object();
    private Object obj1 = new Object();

    private Map<Integer, Integer> map = new HashMap<>();

    /**
     * 通过客户经理查询体检中心Id
     *
     * @return
     */
    public Integer getHospitalIdByManagerId(Integer managerId) {
        if (!map.containsKey(managerId)) {
            synchronized (obj1) {
                if (!map.containsKey(managerId)) {
                    map.put(managerId, userHelperMapper.selectHospitalIdByManagerId(managerId));
                }
            }
        }
        return map.get(managerId);
    }

    public void setExamCompanyIdAndOrganizationId(List<? extends AccountRelationSuper> accountRelationSupers) {
        Integer mt = userHelperMapper.selectHospitalIdmt();
        accountRelationSupers.parallelStream().forEach(accountRelationSuper -> {
            CompanyDOHelper companyDOHelper = new CompanyDOHelper();
            getNewCompanyDO(accountRelationSuper.getCompanyId(), accountRelationSuper.getManagerId(), companyDOHelper, mt);

            if (companyDOHelper.getHospitalCompanyDO() != null) {
                accountRelationSuper.setNewCompanyId(companyDOHelper.getHospitalCompanyDO().getId());
                accountRelationSuper.setOrganizationId(companyDOHelper.getHospitalCompanyDO().getOrganizationId());
            }
            if (companyDOHelper.getChannelCompanyDO() != null) {
                accountRelationSuper.setNewCompanyId(companyDOHelper.getChannelCompanyDO().getId());
                accountRelationSuper.setOrganizationId(companyDOHelper.getChannelCompanyDO().getOrganizationId());
            }
        });
    }

    public void setExamCompanyIdAndOrganizationType(List<ImportGroupId> importGroupIds) {
        Integer mt = userHelperMapper.selectHospitalIdmt();
        importGroupIds.parallelStream().forEach(importGroupId -> {
            CompanyDOHelper companyDOHelper = new CompanyDOHelper();
            getNewCompanyDO(importGroupId.getCompanyId(), importGroupId.getManagerId(), companyDOHelper, mt);

            if (companyDOHelper.getHospitalCompanyDO() != null) {
                HospitalDO hospitalDO = hospitalMapper.selectBaseInfoByHospId(companyDOHelper.getHospitalCompanyDO().getOrganizationId());
                if (hospitalDO != null) {
                    importGroupId.setOrganizationType(hospitalDO.getOrganizationType());
                }
                importGroupId.setNewCompanyId(companyDOHelper.getHospitalCompanyDO().getId());
            }
            if (companyDOHelper.getChannelCompanyDO() != null) {
                HospitalDO hospitalDO = hospitalMapper.selectBaseInfoByHospId(companyDOHelper.getChannelCompanyDO().getOrganizationId());
                if (hospitalDO != null) {
                    importGroupId.setOrganizationType(hospitalDO.getOrganizationType());
                }
                importGroupId.setNewCompanyId(companyDOHelper.getChannelCompanyDO().getId());
            }
        });
    }

    public void setCardBaseNewCompanyIdAndOrganizationType(List<CardBase> cardBases) {
        Integer mt = userHelperMapper.selectHospitalIdmt();
        cardBases.parallelStream().forEach(cardBase -> {
            CompanyDOHelper companyDOHelper = new CompanyDOHelper();
            getNewCompanyDO(cardBase.getCompanyId(), cardBase.getManagerId(), companyDOHelper, mt);

            if (companyDOHelper.getHospitalCompanyDO() != null) {
                HospitalDO hospitalDO = hospitalMapper.selectBaseInfoByHospId(companyDOHelper.getHospitalCompanyDO().getOrganizationId());
                if (hospitalDO != null) {
                    cardBase.setOrganizationType(hospitalDO.getOrganizationType());
                }
                cardBase.setNewCompanyId(companyDOHelper.getHospitalCompanyDO().getId());
            }
            if (companyDOHelper.getChannelCompanyDO() != null) {
                HospitalDO hospitalDO = hospitalMapper.selectBaseInfoByHospId(companyDOHelper.getChannelCompanyDO().getOrganizationId());
                if (hospitalDO != null) {
                    cardBase.setOrganizationType(hospitalDO.getOrganizationType());
                }
                cardBase.setNewCompanyId(companyDOHelper.getChannelCompanyDO().getId());
            }
        });
    }


    public void getNewCompanyDO(Integer companyId, Integer managerId, CompanyDOHelper companyDOHelper, Integer mt) {
        if (companyId.equals(UserConstant.GUEST_COMPANY_ID)) {
            if (belongsToRole(managerId, 4)) {
                companyDOHelper.setChannelCompanyDO(channelCompanyMapper.selectByExamCompanyIdAndOrganizationId(UserConstant.CHANNEL_TB_EXAM_COMPANY_ID_SKDW, mt));
            } else if (belongsToRole(managerId, 9)) {
                companyDOHelper.setChannelCompanyDO(channelCompanyMapper.selectByExamCompanyIdAndOrganizationId(UserConstant.CHANNEL_TB_EXAM_COMPANY_ID_MTJK, mt));
            } else {
                Integer hospitalId = getHospitalIdByManagerId(managerId);
                if (hospitalId != null) {
                    companyDOHelper.setHospitalCompanyDO(hospitalCompanyMapper.selectByExamCompanyIdAndOrganizationId(UserConstant.HOSPITAL_TB_EXAM_COMPANY_ID_GR, hospitalId));
                }
            }
        } else {
            ExamCompanyDO examCompanyDO = examCompanyMapper.selectById(companyId);
            if (examCompanyDO != null) {
                if (examCompanyDO.getType() == ExamCompanyTypeEnum.P.getCode()) {
                    PlatformCompanyDO platformCompanyDO = platformCompanyMapper.selectByExamCompanyId(companyId);
                    if (platformCompanyDO != null) {
                        companyDOHelper.setChannelCompanyDO(channelCompanyMapper.selectByPlatformCompanyIdAndOrganizationId(platformCompanyDO.getId(), mt));
                    }
                } else if (examCompanyDO.getType() == ExamCompanyTypeEnum.M.getCode()) {
                    companyDOHelper.setChannelCompanyDO(channelCompanyMapper.selectByExamCompanyId(companyId));
                } else {
                    Integer hospitalId = getHospitalIdByManagerId(managerId);
                    HospitalCompanyDO hospitalCompanyDO = hospitalCompanyMapper.selectByExamCompanyIdAndOrganizationId(companyId, hospitalId);
                    companyDOHelper.setHospitalCompanyDO(hospitalCompanyDO);
                }
            }
        }
    }

    public void getOrderChannelNewCompanyDO(Integer companyId, Integer managerId, CompanyDOHelper companyDOHelper,Integer mt, Integer fromSite) {
        if (companyId.equals(UserConstant.GUEST_COMPANY_ID)) {
            if ((belongsToRole(managerId, 4) ||belongsToRole(managerId, 13)) && !Objects.equals(fromSite, mt)) {
                companyDOHelper.setChannelCompanyDO(channelCompanyMapper.selectByExamCompanyIdAndOrganizationId(UserConstant.CHANNEL_TB_EXAM_COMPANY_ID_SKDW, fromSite));
            } else {
                companyDOHelper.setChannelCompanyDO(channelCompanyMapper.selectByExamCompanyIdAndOrganizationId(UserConstant.CHANNEL_TB_EXAM_COMPANY_ID_MTJK, fromSite));
            }
        }else {
            companyDOHelper.setChannelCompanyDO(channelCompanyMapper.selectByExamCompanyId(companyId));
        }

    }

    /**
     * 账户是否具有此角色
     *
     * @param accountId
     * @param roleIds
     * @return
     */
    public boolean belongsToRoleCache(int accountId, int... roleIds) {
        if (!accountRoles.containsKey(accountId)) {
            synchronized (obj) {
                if (!accountRoles.containsKey(accountId)) {
                    accountRoles.put(accountId, belongsToRole(accountId, roleIds));
                }
            }
        }
        return accountRoles.get(accountId);
    }

    /**
     * 账户是否具有此角色
     *
     * @param accountId
     * @param roleIds
     * @return
     */
    public boolean belongsToRole(int accountId, int... roleIds) {
        List<Role> roles = getAccountRoles(accountId);
        for (Role role : roles) {
            for (int roleId : roleIds) {
                if (role.getId() == roleId) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Role> getAccountRoles(int accountId) {
        if (!account_Roles.containsKey(accountId)) {
            synchronized (obj) {
                if (!account_Roles.containsKey(accountId)) {
                    account_Roles.put(accountId, roleMapper.getAccountRoles(accountId));
                }
            }
        }
        return account_Roles.get(accountId);
    }

  public  static class CompanyDOHelper {
        HospitalCompanyDO hospitalCompanyDO;
        ChannelCompanyDO channelCompanyDO;

        public HospitalCompanyDO getHospitalCompanyDO() {
            return hospitalCompanyDO;
        }

        public void setHospitalCompanyDO(HospitalCompanyDO hospitalCompanyDO) {
            this.hospitalCompanyDO = hospitalCompanyDO;
        }

        public ChannelCompanyDO getChannelCompanyDO() {
            return channelCompanyDO;
        }

        public void setChannelCompanyDO(ChannelCompanyDO channelCompanyDO) {
            this.channelCompanyDO = channelCompanyDO;
        }
    }
}
