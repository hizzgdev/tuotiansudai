package com.tuotiansudai.paywrapper.repository.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DailyValidationMapper {

    List<Map<String, String>> findInvestRepayTransactions();
}
