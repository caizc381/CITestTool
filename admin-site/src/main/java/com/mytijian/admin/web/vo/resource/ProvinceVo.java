package com.mytijian.admin.web.vo.resource;

import java.io.Serializable;
import java.util.List;

public class ProvinceVo implements Serializable{

    private Integer value;

    private String label;

    private List<CityVo> children;

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

    public List<CityVo> getChildren() {
        return children;
    }

    public void setChildren(List<CityVo> children) {
        this.children = children;
    }
}
