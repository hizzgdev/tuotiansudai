package com.tuotiansudai.api.dto.v1_0;

import com.tuotiansudai.enums.SmsCaptchaType;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class SendSmsCompositeRequestDto extends BaseParamDto{

    @NotNull(message = "0022")
    @ApiModelProperty(value = "验证码类型", example = "RETRIEVE_PASSWORD_CAPTCHA")
    private SmsCaptchaType type;

    @NotEmpty(message = "0001")
    @Pattern(regexp = "^1\\d{10}$", message = "0002")
    @ApiModelProperty(value = "手机号码", example = "15900000001")
    private String phoneNum;

    public SmsCaptchaType getType() {
        return type;
    }

    public void setType(SmsCaptchaType type) {
        this.type = type;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

}
