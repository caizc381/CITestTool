package com.mytijian.admin.web.vo.resource;

import java.io.Serializable;

public class DistrictVo implements Serializable{

    private Integer value;

    private String label;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
