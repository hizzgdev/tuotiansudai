package com.tuotiansudai.activity.repository.model;

import java.io.Serializable;
import java.util.Date;

public class WeChatHelpInfoModel implements Serializable {

    private long id;
    private String openId;
    private long weChatHelpId;
    private WeChatHelpUserStatus status;
    private String remark;
    private Date cashBackTime;
    private Date createdTime;

    public WeChatHelpInfoModel() {
    }

    public WeChatHelpInfoModel(String openId, long weChatHelpId, WeChatHelpUserStatus status) {
        this.openId = openId;
        this.weChatHelpId = weChatHelpId;
        this.status = status;
        this.createdTime = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public long getWeChatHelpId() {
        return weChatHelpId;
    }

    public void setWeChatHelpId(long weChatHelpId) {
        this.weChatHelpId = weChatHelpId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public WeChatHelpUserStatus getStatus() {
        return status;
    }

    public void setStatus(WeChatHelpUserStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCashBackTime() {
        return cashBackTime;
    }

    public void setCashBackTime(Date cashBackTime) {
        this.cashBackTime = cashBackTime;
    }
}
