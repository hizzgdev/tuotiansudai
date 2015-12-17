package com.tuotiansudai.service.impl;

import com.tuotiansudai.dto.Granularity;
import com.tuotiansudai.dto.UserStage;
import com.tuotiansudai.repository.mapper.BusinessIntelligenceMapper;
import com.tuotiansudai.repository.model.KeyValueModel;
import com.tuotiansudai.service.BusinessIntelligenceService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.Predicate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BusinessIntelligenceServiceImpl implements BusinessIntelligenceService {

    @Autowired
    private BusinessIntelligenceMapper businessIntelligenceMapper;

    @Override
    public List<KeyValueModel> queryUserRegisterTrend(Granularity granularity, Date startTime, Date endTime, String province,UserStage userStage) {
        Date queryStartTime = new DateTime(startTime).withTimeAtStartOfDay().toDate();
        Date queryEndTime = new DateTime(endTime).plusDays(1).withTimeAtStartOfDay().toDate();
        return businessIntelligenceMapper.queryUserRegisterTrend(queryStartTime, queryEndTime, granularity, province,userStage);
    }

    @Override
    public List<KeyValueModel> queryUserRechargeTrend(Granularity granularity, Date startTime, Date endTime, String province) {
        Date queryStartTime = new DateTime(startTime).withTimeAtStartOfDay().toDate();
        Date queryEndTime = new DateTime(endTime).plusDays(1).withTimeAtStartOfDay().toDate();
        return businessIntelligenceMapper.queryUserRechargeTrend(queryStartTime, queryEndTime, granularity, province);
    }

    @Override
    public List<KeyValueModel> queryUserWithdrawTrend(Granularity granularity, Date startTime, Date endTime, String province) {
        Date queryStartTime = new DateTime(startTime).withTimeAtStartOfDay().toDate();
        Date queryEndTime = new DateTime(endTime).plusDays(1).withTimeAtStartOfDay().toDate();
        return businessIntelligenceMapper.queryUserWithdrawTrend(queryStartTime, queryEndTime, granularity, province);
    }

    @Override
    public List<KeyValueModel> queryUserAccountTrend(Granularity granularity, Date startTime, Date endTime, String province) {
        Date queryStartTime = new DateTime(startTime).withTimeAtStartOfDay().toDate();
        Date queryEndTime = new DateTime(endTime).plusDays(1).withTimeAtStartOfDay().toDate();
        return businessIntelligenceMapper.queryUserAccountTrend(queryStartTime, queryEndTime, granularity, province);
    }

    @Override
    public List<KeyValueModel> queryInvestViscosity(Date startTime, Date endTime, final String province) {
        Date queryStartTime = new DateTime(startTime).withTimeAtStartOfDay().toDate();
        Date queryEndTime = new DateTime(endTime).plusDays(1).withTimeAtStartOfDay().toDate();
        List<KeyValueModel> keyValueModelList = businessIntelligenceMapper.queryInvestViscosity(queryStartTime, queryEndTime, province);
        final KeyValueModel keyValueModel = new KeyValueModel();
        List<KeyValueModel> top12List = ListUtils.select(keyValueModelList, new Predicate<KeyValueModel>() {
            @Override
            public boolean evaluate(KeyValueModel object) {
                int loan_count = Integer.valueOf(object.getName());
                if(loan_count<=12) {
                    return true;
                } else {
                    keyValueModel.setGroup(object.getGroup());
                    if (keyValueModel.getValue() == null) {
                        keyValueModel.setValue(object.getValue());
                    } else {
                        int otherCount = Integer.valueOf(keyValueModel.getValue()) + Integer.valueOf(object.getValue());
                        keyValueModel.setValue(String.valueOf(otherCount));
                    }
                    return false;
                }
            }
        });
        if(keyValueModel.getGroup() != null) {
            keyValueModel.setName("13+");
            top12List.add(keyValueModel);
        }
        return top12List;
    }

}
