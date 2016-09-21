package com.tuotiansudai.membership.repository.model;

import java.io.Serializable;
import java.util.Date;

public class UserMembershipModel implements Serializable{

    private long id;
    private String loginName;
    private long membershipId;
    private Date expiredTime;
    private Date createdTime;
    private UserMembershipType type;
    private Long membershipGiveId;

    public UserMembershipModel(){
    }

    public UserMembershipModel(String loginName, long membershipId, Date expiredTime, UserMembershipType type) {
        this.loginName = loginName;
        this.membershipId = membershipId;
        this.expiredTime = expiredTime;
        this.createdTime = new Date();
        this.type = type;
    }

    public UserMembershipModel(String loginName, long membershipId, Date expiredTime, UserMembershipType type, long membershipGiveId) {
        this.loginName = loginName;
        this.membershipId = membershipId;
        this.expiredTime = expiredTime;
        this.createdTime = new Date();
        this.type = type;
        this.membershipGiveId = membershipGiveId;
    }

    public UserMembershipModel(String loginName, long membershipId, Date expiredTime, Date createdTime, UserMembershipType type) {
        this.loginName = loginName;
        this.membershipId = membershipId;
        this.expiredTime = expiredTime;
        this.createdTime = createdTime;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(long membershipId) {
        this.membershipId = membershipId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public UserMembershipType getType() {
        return type;
    }

    public void setType(UserMembershipType type) {
        this.type = type;
    }

    public Long getMembershipGiveId() {
        return membershipGiveId;
    }

    public void setMembershipGiveId(Long membershipGiveId) {
        this.membershipGiveId = membershipGiveId;
    }
}
