package com.tuotiansudai.api.service.v1_0;

import com.tuotiansudai.api.dto.v1_0.BaseResponseDto;
import com.tuotiansudai.api.dto.v1_0.NoPasswordInvestTurnOffRequestDto;

public interface MobileAppNoPasswordInvestTurnOffService {
    BaseResponseDto noPasswordInvestTurnOff(NoPasswordInvestTurnOffRequestDto noPasswordInvestTurnOffRequestDto, String ip);
}
