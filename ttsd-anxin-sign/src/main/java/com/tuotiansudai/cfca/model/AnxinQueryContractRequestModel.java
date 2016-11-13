package com.tuotiansudai.cfca.model;

import java.util.Date;

public class AnxinQueryContractRequestModel {

    private long id;
    private long businessId;
    private String batchNo;
    private String txTime;
    private Date createdTime;

    public AnxinQueryContractRequestModel() {
    }

    public AnxinQueryContractRequestModel(long businessId, String batchNo, String txTime, Date createdTime) {
        this.businessId = businessId;
        this.batchNo = batchNo;
        this.txTime = txTime;
        this.createdTime = createdTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(long businessId) {
        this.businessId = businessId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getTxTime() {
        return txTime;
    }

    public void setTxTime(String txTime) {
        this.txTime = txTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
