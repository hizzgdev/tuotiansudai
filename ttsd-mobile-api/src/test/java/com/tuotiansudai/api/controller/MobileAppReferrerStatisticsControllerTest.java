package com.tuotiansudai.api.controller;


import com.tuotiansudai.api.dto.BaseParam;
import com.tuotiansudai.api.dto.BaseParamDto;
import com.tuotiansudai.api.dto.BaseResponseDto;
import com.tuotiansudai.api.dto.ReturnMessage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class MobileAppReferrerStatisticsControllerTest extends ControllerTestBase {



    @Autowired
    private MobileAppReferrerStatisticsController controller;

    @Override
    protected Object getControllerObject() {
        return controller;
    }



    @Test
    public void getReferrerStatistics() throws Exception {
        BaseParamDto baseParamDto = new BaseParamDto();
        BaseParam baseParam = new BaseParam();
        baseParam.setUserId("baoxin");
        baseParamDto.setBaseParam(baseParam);

        BaseResponseDto referrerStatistics = controller.getReferrerStatistics(baseParamDto);
        assertEquals(ReturnMessage.SUCCESS.getCode(), referrerStatistics.getCode());
    }

}
