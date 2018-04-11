package com.mytijian.admin.web.vo.resource;

import java.io.Serializable;
import java.util.List;

public class CityVo implements Serializable{

    private Integer value;

    private String label;

    private List<DistrictVo> children;

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

    public List<DistrictVo> getChildren() {
        return children;
    }

    public void setChildren(List<DistrictVo> children) {
        this.children = children;
    }
}
