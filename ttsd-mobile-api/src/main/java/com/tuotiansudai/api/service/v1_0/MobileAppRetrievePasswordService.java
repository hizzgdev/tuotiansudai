package com.tuotiansudai.api.service.v1_0;

import com.tuotiansudai.api.dto.v1_0.BaseResponseDto;
import com.tuotiansudai.api.dto.v1_0.RetrievePasswordRequestDto;

public interface MobileAppRetrievePasswordService {
    /**
     * @function 修改密码
     * @param retrievePasswordRequestDto 修改密码参数封装类
     * @return BaseResponseDto
     */
    BaseResponseDto retrievePassword(RetrievePasswordRequestDto retrievePasswordRequestDto);

}
