package com.tuotiansudai.activity.service;

import com.tuotiansudai.activity.repository.mapper.ActivityInvestAnnualizedMapper;
import com.tuotiansudai.activity.repository.mapper.UserExchangePrizeMapper;
import com.tuotiansudai.activity.repository.model.*;
import com.tuotiansudai.client.MQWrapperClient;
import com.tuotiansudai.mq.client.model.MessageQueue;
import com.tuotiansudai.repository.model.UserModel;
import com.tuotiansudai.rest.client.mapper.UserMapper;
import com.tuotiansudai.util.RedisWrapperClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StartWorkActivityService {

    @Value(value = "#{new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").parse(\"${activity.start.work.startTime}\")}")
    private Date activityStartTime;

    @Value(value = "#{new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").parse(\"${activity.start.work.endTime}\")}")
    private Date activityEndTime;

    @Autowired
    private UserExchangePrizeMapper userExchangePrizeMapper;

    @Autowired
    private ActivityInvestAnnualizedMapper activityInvestAnnualizedMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MQWrapperClient mqWrapperClient;

    private RedisWrapperClient redisWrapperClient = RedisWrapperClient.getInstance();

    private static final String KEY = "START_WORK_ACTIVITY_DRAW_COUPON:{0}";

    private static final long PRICE = 5000000;

    public boolean duringActivities() {
        return activityStartTime.before(new Date()) && activityEndTime.after(new Date());
    }

    public boolean drewCoupon(String loginName) {
        return redisWrapperClient.exists(MessageFormat.format(KEY, loginName));
    }

    public void sendDrawCouponMessage(String loginName) {
    }

    public Map<String, Object> exchangePrize(String mobile, ExchangePrize exchangePrize) {
        Map<String, Object> map = new HashMap<>();
        int unUseCount = getCount(mobile);
        if (exchangePrize.getExchangeMoney() / PRICE > unUseCount) {
            map.put("success", false);
            return map;
        }
        UserModel userModel = userMapper.findByMobile(mobile);
        userExchangePrizeMapper.create(new UserExchangePrizeModel(mobile, userModel.getLoginName(), userModel.getUserName(), exchangePrize, new Date(), ActivityCategory.START_WORK_ACTIVITY));
        map.put("success", true);
        map.put("count", unUseCount - (exchangePrize.getExchangeMoney() / PRICE));
        return map;
    }

    public List<UserExchangePrizeModel> getUserPrizeByMobile(String mobile) {
        return userExchangePrizeMapper.findUserExchangePrizeViews(mobile,null, ActivityCategory.START_WORK_ACTIVITY, activityStartTime, activityEndTime);
    }

    public int getCount(String mobile) {
        if (!duringActivities()) {
            return 0;
        }
        List<UserExchangePrizeModel> exchangePrizes = getUserPrizeByMobile(mobile);
        List<ActivityInvestAnnualizedView> activityInvestAnnualizedViews = activityInvestAnnualizedMapper.findByActivityAndMobile(ActivityInvestAnnualized.START_WORK_ACTIVITY, mobile);
        long sumAnnualizedAmount = activityInvestAnnualizedViews.stream().mapToLong(ActivityInvestAnnualizedView::getSumAnnualizedAmount).sum();
        long sumUseAnnualizedAmount = exchangePrizes.stream().mapToLong(i -> i.getPrize().getExchangeMoney()).sum();
        return (int) ((sumAnnualizedAmount - sumUseAnnualizedAmount) / PRICE);
    }
}
