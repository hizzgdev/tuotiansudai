package com.tuotiansudai.api.dto.v1_0;

public class UserBillDetailListRequestDto extends BaseParamDto{
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
