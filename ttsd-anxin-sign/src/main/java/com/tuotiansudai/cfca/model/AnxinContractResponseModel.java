package com.tuotiansudai.cfca.model;


import java.io.Serializable;
import java.util.Date;

public class AnxinContractResponseModel implements Serializable {

    private long id;
    private long requestId;
    private String batchNo;
    private String txTime;
    private String locale;
    private String retCode;
    private String retMessage;
    private Date createdTime;

    public AnxinContractResponseModel() {
    }

    public AnxinContractResponseModel(long requestId, String batchNo, String txTime, String locale, String retCode, String retMessage, Date createdTime) {
        this.requestId = requestId;
        this.batchNo = batchNo;
        this.txTime = txTime;
        this.locale = locale;
        this.retCode = retCode;
        this.retMessage = retMessage;
        this.createdTime = createdTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
