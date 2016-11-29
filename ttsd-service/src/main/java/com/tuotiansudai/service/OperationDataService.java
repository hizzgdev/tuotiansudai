package com.tuotiansudai.service;

import com.tuotiansudai.dto.OperationDataDto;
import com.tuotiansudai.repository.model.InvestDataView;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OperationDataService {
    OperationDataDto getOperationDataFromRedis(Date endDate);

    List<InvestDataView> getInvestDetail(Date endDate);

    List<Integer> findScaleByGender(Date endDate);

    Map<String,String> findLatestSixMonthTradeAmount(Date endDate);

    Map<String, String> findAgeDistributionByAge(Date endDate);

    Map<String, String> findCountInvestCityScaleTop3(Date endDate);

    Map<String, String> findInvestAmountScaleTop3(Date endDate);

    long findUserSumInterest(Date endDate);

}
