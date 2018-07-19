package com.tuotiansudai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tuotiansudai.enums.BillOperationType;
import com.tuotiansudai.repository.model.BankUserBillModel;
import com.tuotiansudai.util.AmountConverter;

import java.util.Date;

public class UserBillPaginationItemDataDto {

    private long id;

    private String balance = "0.00";

    private String income = "0.00";

    private String cost = "0.00";

    private String businessType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createdTime;

    public UserBillPaginationItemDataDto() {
    }

    public UserBillPaginationItemDataDto(BankUserBillModel bankUserBillModel) {
        this.id = bankUserBillModel.getId();
        this.balance = AmountConverter.convertCentToString(bankUserBillModel.getBalance());
        if (BillOperationType.IN == bankUserBillModel.getOperationType()) {
            this.income = AmountConverter.convertCentToString(bankUserBillModel.getAmount());
        }
        if (BillOperationType.OUT == bankUserBillModel.getOperationType()) {
            this.cost = AmountConverter.convertCentToString(bankUserBillModel.getAmount());
        }
        this.businessType = bankUserBillModel.getBusinessType().getDescription();
        this.createdTime = bankUserBillModel.getCreatedTime();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
