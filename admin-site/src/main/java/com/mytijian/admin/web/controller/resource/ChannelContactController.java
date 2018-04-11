package com.mytijian.admin.web.controller.resource;

import com.google.common.collect.Lists;
import com.mytijian.admin.web.vo.resource.ChannelContactVo;
import com.mytijian.uic.annotation.LoginRequired;
import com.mytijian.gotone.api.HospitalContactMessageConfigService;
import com.mytijian.gotone.api.HospitalContactService;
import com.mytijian.gotone.api.model.HospitalContactAddReq;
import com.mytijian.gotone.api.model.HospitalContactMessageConfigQueryReq;
import com.mytijian.gotone.api.model.HospitalContactReq;
import com.mytijian.gotone.api.model.beans.HospitalContact;
import com.mytijian.gotone.api.model.beans.HospitalContactMessageConfig;
import com.mytijian.gotone.api.model.enums.NotifyTypeEnum;
import com.mytijian.gotone.api.model.enums.SendTypeEnum;
import com.mytijian.util.AssertUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ChannelContactController {
	
	@Resource(name="gotoneContactMessageConfigService")
    private HospitalContactMessageConfigService hospitalContactMessageConfigService;

    @Resource(name="gotoneContactService")
    private HospitalContactService hospitalContactService;

    /**
     * 添加医院联系人以及消息配置
     * @param channelContactVos
     * @param hospitalId
     */
    @RequestMapping(value= "/addHospitalContactConfigs",method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @Transactional(rollbackFor = Exception.class)
    public void addHospitalContactConfigs(@RequestBody List<ChannelContactVo> channelContactVos, @RequestParam Integer hospitalId){

        List<HospitalContactAddReq> hospitalContactAddReqList = new ArrayList<>();
        for (ChannelContactVo vo : channelContactVos) {
            HospitalContactAddReq addReq = new HospitalContactAddReq();
            addReq.setContactId(vo.getContactId());
            addReq.setHospitalId(hospitalId);
            if (StringUtils.isNotBlank(vo.getEmail())){
                addReq.setEmail(vo.getEmail());
                addReq.setSendTypeEnum(SendTypeEnum.email);
            }
            if (vo.getItemMessage() && StringUtils.isNotBlank(vo.getEmail())) {
                Set notifySet = new HashSet<>();
                notifySet.add(NotifyTypeEnum.MEAL_CHANGE_NOTIFY);
                addReq.setNotifyTypeSet(notifySet);
            }
            hospitalContactAddReqList.add(addReq);
        }
        hospitalContactService.removeAndAddContactList(hospitalContactAddReqList);
    }

    
    
	 /**
     * 获取医院联系人配置
     * @param hospitalId 医院id
     * @return
     */
    @RequestMapping(value = "/hospitalContact",method = RequestMethod.GET)
    @ResponseBody
    @LoginRequired
    public List<ChannelContactVo> getHospitalContactConfigs(Integer hospitalId){

        List<ChannelContactVo> channelContactVo = Lists.newArrayList();
        //获取医院所有联系人消息配置

        HospitalContactReq contactReq = new HospitalContactReq();
        contactReq.setHospitalId(hospitalId);
        List<HospitalContact> hospitalContacts = hospitalContactService.queryByHospitalAndType(contactReq)
                .getHospitalContactList()
                .stream()
                .filter(
                        hospitalContact -> AssertUtil.isNotEmpty(hospitalContact.getEmail())).collect(Collectors.toList()
                );
        //遍历
        hospitalContacts.forEach((HospitalContact contact) -> {
            HospitalContactMessageConfigQueryReq queryReq = new HospitalContactMessageConfigQueryReq();
            queryReq.setContactId(contact.getContactId());

            List<HospitalContactMessageConfig> configs = hospitalContactMessageConfigService.queryByIdAndType(queryReq)
                    .getHospitalContactMessageConfigs()
                    .stream()
                    .filter(
                            config -> config.getSendType() == SendTypeEnum.email
                    )
                    .collect(Collectors.toList());
            //配制对象（存在只有联系人，无配置）
            ChannelContactVo hospitalContactVo = new ChannelContactVo();
            hospitalContactVo.setEmail(contact.getEmail());
            hospitalContactVo.setContactId(contact.getContactId());
            if (AssertUtil.isNotEmpty(configs)){
                hospitalContactVo.setItemMessage(true);
            }
            channelContactVo.add(hospitalContactVo);
        });

        return channelContactVo;
    }
}
