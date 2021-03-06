package com.tuotiansudai.activity.repository.dto;

import com.tuotiansudai.activity.repository.model.NotWorkModel;
import com.tuotiansudai.util.AmountConverter;

public class NotWorkDto {
    private long id;
    private String loginName;
    private String userName;
    private String mobile;
    private String investAmount;
    private String rewards;
    private String recommendedRegisterAmount;
    private String recommendedIdentifyAmount;
    private String recommendedInvestAmount;

    public NotWorkDto() {
    }

    public NotWorkDto(NotWorkModel notWorkModel) {
        this.id = notWorkModel.getId();
        this.loginName = notWorkModel.getLoginName();
        this.userName = notWorkModel.getUserName();
        this.mobile = notWorkModel.getMobile();
        this.investAmount = AmountConverter.convertCentToString(notWorkModel.getInvestAmount());
        this.recommendedRegisterAmount = "";
        this.recommendedIdentifyAmount = "";
        this.recommendedInvestAmount = String.valueOf(AmountConverter.convertCentToString(notWorkModel.getRecommendedInvestAmount()));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getInvestAmount() {
        return investAmount;
    }

    public void setInvestAmount(String investAmount) {
        this.investAmount = investAmount;
    }

    public String getRecommendedRegisterAmount() {
        return recommendedRegisterAmount;
    }

    public void setRecommendedRegisterAmount(String recommendedRegisterAmount) {
        this.recommendedRegisterAmount = recommendedRegisterAmount;
    }

    public String getRecommendedIdentifyAmount() {
        return recommendedIdentifyAmount;
    }

    public void setRecommendedIdentifyAmount(String recommendedIdentifyAmount) {
        this.recommendedIdentifyAmount = recommendedIdentifyAmount;
    }

    public String getRecommendedInvestAmount() {
        return recommendedInvestAmount;
    }

    public void setRecommendedInvestAmount(String recommendedInvestAmount) {
        this.recommendedInvestAmount = recommendedInvestAmount;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }
}
