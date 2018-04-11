package com.mytijian.admin.web.controller.hospital.util;

import com.mytijian.admin.web.controller.hospital.vo.ChannelHospitalVO;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.HospitalSettings;
import com.mytijian.resource.model.OrganizationHospitalRelation;

import java.util.ArrayList;
import java.util.List;

public class PlatformChannelDiscountUtil {

    public static List<List<String>> convertExportPlatformChannelDiscountData(List<Hospital> hospitals) {

        List<List<String>> exportData = new ArrayList<List<String>>();
        for (Hospital hsp : hospitals) {
            // 顺序一定不能改动，必须和模板中的数据顺序一致
            List<String> rowData = new ArrayList<String>();
            rowData.add(String.valueOf(hsp.getId()));
            rowData.add(hsp.getName());
            rowData.add(hsp.getAddress().getBriefAddress());
            rowData.add(hsp.getShowInList()==1?"显示":"不显示");
            HospitalSettings settings = hsp.inferSettings();
            rowData.add(String.valueOf(settings.getPlatformGuestDiscount()));
            rowData.add(String.valueOf(settings.getPlatformChannelGuestDiscount()));
            rowData.add(String.valueOf(settings.getPlatformCompDiscount()));
            rowData.add(String.valueOf(settings.getPlatformChannelCompDiscount()));
            exportData.add(rowData);
        }
        return exportData;
    }


    public static List<List<String>> convertExportOrganizationChannelRelation(List<ChannelHospitalVO> channelHospitalVOS) {

        List<List<String>> exportData = new ArrayList<>();
        for (ChannelHospitalVO vo : channelHospitalVOS) {
            // 顺序一定不能改动，必须和模板中的数据顺序一致
            List<String> rowData = new ArrayList<>();
            Hospital hospital = vo.getHospital();
            OrganizationHospitalRelation relation = vo.getOrganizationHospitalRelation();
            rowData.add(String.valueOf(hospital.getId()));
            rowData.add(hospital.getName());
            rowData.add(hospital.getAddress().getBriefAddress());
            rowData.add(hospital.getShowInList()==1?"显示":"不显示");
            HospitalSettings settings = hospital.inferSettings();
            rowData.add(settings.getPlatformGuestDiscount() ==null?"":String.valueOf(settings.getPlatformGuestDiscount()));
            rowData.add(settings.getPlatformChannelGuestDiscount()==null?"":String.valueOf(settings.getPlatformChannelGuestDiscount()));
            rowData.add((relation != null&&relation.getPlatformChannelGuestDiscount()!=null) ? String.valueOf(relation.getPlatformChannelGuestDiscount()) : "");
            rowData.add(settings.getPlatformCompDiscount()==null?"":String.valueOf(settings.getPlatformCompDiscount()));
            rowData.add(settings.getPlatformChannelCompDiscount()==null?"":String.valueOf(settings.getPlatformChannelCompDiscount()));
            rowData.add((relation != null &&relation.getPlatformChannelCompDiscount() !=null) ? String.valueOf(relation.getPlatformChannelCompDiscount()) : "");
            exportData.add(rowData);
        }
        return exportData;
    }
}
