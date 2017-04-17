package com.tuotiansudai.activity.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.tuotiansudai.repository.mapper.InvestMapper;
import com.tuotiansudai.repository.mapper.UserMapper;
import com.tuotiansudai.repository.model.UserModel;
import com.tuotiansudai.util.AmountConverter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GrantWelfareActivityService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InvestMapper investMapper;

    @Value("#{'${activity.grant.welfare.period}'.split('\\~')}")
    private List<String> grantWelfarePeriod = Lists.newArrayList();

    public int findReferrerCountByLoginName(String loginName) {
        int ReferrerCount = 0;
        if (Strings.isNullOrEmpty(loginName)) {
            return 0;
        }

        Date startTime = DateTime.parse(grantWelfarePeriod.get(0), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        Date endTime = DateTime.parse(grantWelfarePeriod.get(1), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        List<UserModel> userModels = userMapper.findUsersByRegisterTimeOrReferrer(startTime, endTime, loginName);
        for (UserModel referrerUserModel : userModels) {
            if (referrerUserModel.getRegisterTime().before(endTime) && referrerUserModel.getRegisterTime().after(startTime)) {
                long investAmount = investMapper.findFirstInvestAmountByLoginName(referrerUserModel.getLoginName(), startTime, endTime);
                if (investAmount >= 500000) {
                    ReferrerCount++;
                }
            }
        }
        return ReferrerCount;
    }

    public String findReferrerSumInvestAmountByLoginName(String loginName) {
        long ReferrerSumInvestAmount = 0L;
        if (Strings.isNullOrEmpty(loginName)) {
            return "0";
        }

        Date startTime = DateTime.parse(grantWelfarePeriod.get(0), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        Date endTime = DateTime.parse(grantWelfarePeriod.get(1), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        List<UserModel> userModels = userMapper.findUsersByRegisterTimeOrReferrer(startTime, endTime, loginName);
        for (UserModel referrerUserModel : userModels) {
            if (referrerUserModel.getRegisterTime().before(endTime) && referrerUserModel.getRegisterTime().after(startTime)) {
                ReferrerSumInvestAmount += investMapper.sumSuccessActivityInvestAmount(referrerUserModel.getLoginName(), null, startTime, endTime);
            }
        }
        return AmountConverter.convertCentToString(ReferrerSumInvestAmount);
    }

}
