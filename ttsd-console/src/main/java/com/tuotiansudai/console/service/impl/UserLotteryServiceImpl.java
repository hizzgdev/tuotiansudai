package com.tuotiansudai.console.service.impl;


import com.tuotiansudai.console.service.UserLotteryService;
import com.tuotiansudai.repository.mapper.UserLotteryPrizeMapper;
import com.tuotiansudai.repository.mapper.UserLotteryTimeMapper;
import com.tuotiansudai.repository.model.LotteryPrize;
import com.tuotiansudai.repository.model.UserLotteryPrizeModel;
import com.tuotiansudai.repository.model.UserLotteryPrizeView;
import com.tuotiansudai.repository.model.UserLotteryTimeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserLotteryServiceImpl implements UserLotteryService {

    @Autowired
    private UserLotteryTimeMapper userLotteryTimeMapper;

    @Autowired
    private UserLotteryPrizeMapper userLotteryPrizeMapper;

    @Override
    public List<UserLotteryTimeView> findUserLotteryTimeViews(String mobile,Integer index,Integer pageSize) {
        return userLotteryTimeMapper.findUserLotteryTimeViews(mobile, index, pageSize);
    }

    @Override
    public int findUserLotteryTimeCountViews(String loginName){
        return userLotteryTimeMapper.findUserLotteryTimeCountModels(loginName);
    }

    @Override
    public List<UserLotteryPrizeView> findUserLotteryPrizeViews(String mobile,LotteryPrize selectPrize,Date startTime,Date endTime,Integer index,Integer pageSize){
        return userLotteryPrizeMapper.findUserLotteryPrizeViews(mobile, selectPrize, startTime, endTime, index, pageSize);
    }

    @Override
    public int findUserLotteryPrizeCountViews(String mobile,LotteryPrize selectPrize,Date startTime,Date endTime){
        return userLotteryPrizeMapper.findUserLotteryPrizeCountViews(mobile, selectPrize, startTime, endTime);
    }
}
