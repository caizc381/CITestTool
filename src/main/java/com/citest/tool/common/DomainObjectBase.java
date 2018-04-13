package com.citest.tool.common;

import java.io.Serializable;
import java.util.Date;

public class DomainObjectBase implements Serializable{
    private static final long serialVersionUID = -912196701008191537L;
    private Date createTime;
    private Date updateTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


}
