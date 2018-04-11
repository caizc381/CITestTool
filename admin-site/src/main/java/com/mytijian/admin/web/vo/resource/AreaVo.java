package com.mytijian.admin.web.vo.resource;

import java.io.Serializable;
import java.util.List;

public class AreaVo implements Serializable{

    private List<ProvinceVo> provinces;

    public List<ProvinceVo> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<ProvinceVo> provinces) {
        this.provinces = provinces;
    }
}
