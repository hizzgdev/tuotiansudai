package com.tuotiansudai.enums;

public enum ExperienceType {
    REGISTER("新手注册"),
    INVEST_LOAN("投资体验金项目"),
    MONEY_TREE("摇钱树活动");

    private final String description;

    ExperienceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
