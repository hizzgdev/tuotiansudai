package com.tuotiansudai.service;

import com.tuotiansudai.enums.riskestimation.Estimate;

import java.util.List;

public interface RiskEstimateService {

    Estimate estimate(String loginName, List<Integer> answers);

    boolean alertEstimate(String loginName);
}
