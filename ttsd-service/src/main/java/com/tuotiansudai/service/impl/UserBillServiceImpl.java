package com.tuotiansudai.service.impl;

import com.tuotiansudai.dto.BaseDto;
import com.tuotiansudai.dto.BasePaginationDataDto;
import com.tuotiansudai.dto.UserBillPaginationItemDataDto;
import com.tuotiansudai.enums.BankUserBillBusinessType;
import com.tuotiansudai.enums.Role;
import com.tuotiansudai.repository.mapper.BankUserBillMapper;
import com.tuotiansudai.repository.model.BankUserBillModel;
import com.tuotiansudai.service.UserBillService;
import com.tuotiansudai.util.CalculateUtil;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBillServiceImpl implements UserBillService {

    static Logger logger = Logger.getLogger(UserBillServiceImpl.class);

    private final BankUserBillMapper bankUserBillMapper;

    @Autowired
    public UserBillServiceImpl(BankUserBillMapper bankUserBillMapper) {
        this.bankUserBillMapper = bankUserBillMapper;
    }

    @Override
    public BaseDto<BasePaginationDataDto> getUserBillData(String loginName, int index, int pageSize, Date startTime, Date endTime, List<BankUserBillBusinessType> businessTypes, Role role) {
        if (startTime == null) {
            startTime = new DateTime(0).withTimeAtStartOfDay().toDate();
        } else {
            startTime = new DateTime(startTime).withTimeAtStartOfDay().toDate();
        }

        if (endTime == null) {
            endTime = CalculateUtil.calculateMaxDate();
        } else {
            endTime = new DateTime(endTime).withTimeAtStartOfDay().plusDays(1).minusMillis(1).toDate();
        }

        List<BankUserBillModel> userBillModels = bankUserBillMapper.findUserBills(loginName, null, businessTypes, null, startTime, endTime, (index - 1) * pageSize, pageSize, role);

        long count = bankUserBillMapper.countBills(loginName, null, businessTypes, null, startTime, endTime, role);

        List<UserBillPaginationItemDataDto> records = userBillModels.stream().map(UserBillPaginationItemDataDto::new).collect(Collectors.toList());
        BasePaginationDataDto<UserBillPaginationItemDataDto> dataDto = new BasePaginationDataDto<>(index, pageSize, count, records);
        dataDto.setStatus(true);

        return new BaseDto<>(dataDto);
    }
}
