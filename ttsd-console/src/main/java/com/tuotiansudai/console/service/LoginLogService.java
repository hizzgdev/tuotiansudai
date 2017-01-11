package com.tuotiansudai.console.service;

import com.google.common.collect.Lists;
import com.tuotiansudai.dto.BasePaginationDataDto;
import com.tuotiansudai.dto.LoginLogPaginationItemDataDto;
import com.tuotiansudai.log.repository.mapper.LoginLogMapper;
import com.tuotiansudai.log.repository.model.LoginLogModel;
import com.tuotiansudai.repository.mapper.UserMapper;
import com.tuotiansudai.repository.model.UserModel;
import com.tuotiansudai.util.PaginationUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Service
public class LoginLogService {

    private final static String LOGIN_LOG_TABLE_TEMPLATE = "login_log_{0}";

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private UserMapper userMapper;

    public BasePaginationDataDto<LoginLogPaginationItemDataDto> getLoginLogPaginationData(String mobile, Boolean success, int index, int pageSize, int year, int month) {
        String loginLogTableName = this.getLoginLogTableName(new DateTime(year, month, 1, 0, 0).toDate());

        UserModel userModel = userMapper.findByLoginNameOrMobile(mobile);

        long count = loginLogMapper.count(userModel.getLoginName(), userModel.getMobile(), success, loginLogTableName);

        List<LoginLogModel> data = loginLogMapper.getPaginationData(userModel.getLoginName(), userModel.getMobile(), success, PaginationUtil.calculateOffset(index, pageSize, count), pageSize, loginLogTableName);

        List<LoginLogPaginationItemDataDto> records = Lists.transform(data, input -> new LoginLogPaginationItemDataDto(input.getLoginName(), input.getSource(), input.getIp(), input.getDevice(), input.getLoginTime(), input.isSuccess()));

        return new BasePaginationDataDto<>(PaginationUtil.validateIndex(index, pageSize, count), pageSize, count, records);
    }

    private String getLoginLogTableName(Date date) {
        return MessageFormat.format(LOGIN_LOG_TABLE_TEMPLATE, new DateTime(date).toString("yyyyMM"));
    }
}
