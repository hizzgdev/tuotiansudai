package com.tuotiansudai.paywrapper.service;

import com.tuotiansudai.dto.BaseDto;
import com.tuotiansudai.dto.InvestDto;
import com.tuotiansudai.dto.PayDataDto;
import com.tuotiansudai.dto.PayFormDataDto;
import com.tuotiansudai.paywrapper.repository.model.async.callback.InvestNotifyRequestModel;
import com.tuotiansudai.repository.model.AutoInvestPlanModel;
import com.tuotiansudai.repository.model.InvestModel;
import com.tuotiansudai.util.AutoInvestMonthPeriod;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface InvestService {

    String INVEST_CHECK_QUEUE_KEY = "invest:check:queue";

    BaseDto<PayFormDataDto> invest(InvestDto dto);

    String investCallback(Map<String, String> paramsMap, String queryString);

    BaseDto<PayDataDto> asyncInvestCallback(long orderId);

    String overInvestPaybackCallback(Map<String, String> paramsMap, String queryString);

    void autoInvest(long loanId);

    List<AutoInvestPlanModel> findValidPlanByPeriod(AutoInvestMonthPeriod period);

    void investSuccess(InvestModel investModel);

    BaseDto<PayDataDto> noPasswordInvest(InvestDto dto);
}
