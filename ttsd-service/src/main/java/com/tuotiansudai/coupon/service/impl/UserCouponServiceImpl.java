package com.tuotiansudai.coupon.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.tuotiansudai.client.RedisWrapperClient;
import com.tuotiansudai.coupon.dto.UserCouponDto;
import com.tuotiansudai.coupon.repository.mapper.CouponMapper;
import com.tuotiansudai.coupon.repository.mapper.UserCouponMapper;
import com.tuotiansudai.coupon.repository.model.CouponModel;
import com.tuotiansudai.coupon.repository.model.CouponUseRecordView;
import com.tuotiansudai.coupon.repository.model.UserCouponModel;
import com.tuotiansudai.coupon.service.UserCouponService;
import com.tuotiansudai.dto.BaseDto;
import com.tuotiansudai.dto.BasePaginationDataDto;
import com.tuotiansudai.repository.mapper.LoanMapper;
import com.tuotiansudai.repository.model.CouponType;
import com.tuotiansudai.repository.model.InvestStatus;
import com.tuotiansudai.repository.model.LoanModel;
import com.tuotiansudai.service.InvestService;
import com.tuotiansudai.util.AmountConverter;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class UserCouponServiceImpl implements UserCouponService {

    static Logger logger = Logger.getLogger(UserCouponServiceImpl.class);

    @Autowired
    private LoanMapper loanMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private InvestService investService;

    @Autowired
    private RedisWrapperClient redisWrapperClient;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String NEWBIE_COUPON_ALERT_KEY = "web:newbiecoupon:alert";

    @Override
    public List<UserCouponDto> getUserAllCoupons(String loginName) {
        List<UserCouponModel> modelList = userCouponMapper.findByLoginName(loginName);
        List<UserCouponDto> userCouponDtoList = new ArrayList<>();
        for (UserCouponModel couponModel : modelList) {
            CouponModel coupon = couponMapper.findById(couponModel.getCouponId());
            UserCouponDto dto = new UserCouponDto(coupon, couponModel);
            userCouponDtoList.add(dto);
        }
        Collections.sort(userCouponDtoList);
        return userCouponDtoList;
    }


    public List<UserCouponDto> getUserMoneyCoupons(String loginName) {
        List<UserCouponDto> userCouponModelList = getUserAllCoupons(loginName);
        return Lists.newArrayList(Iterators.filter(userCouponModelList.iterator(), new Predicate<UserCouponDto>() {
            @Override
            public boolean apply(UserCouponDto input) {
                return input.getCouponType() == CouponType.NEWBIE_COUPON || input.getCouponType() == CouponType.INVEST_COUPON;
            }
        }));
    }

    public List<UserCouponDto> getUserInterestCoupons(String loginName) {
        List<UserCouponDto> userCouponModelList = getUserAllCoupons(loginName);
        return Lists.newArrayList(Iterators.filter(userCouponModelList.iterator(), new Predicate<UserCouponDto>() {
            @Override
            public boolean apply(UserCouponDto input) {
                return input.getCouponType() == CouponType.INTEREST_COUPON;
            }
        }));
    }

    @Override
    public UserCouponDto getUsableNewbieCoupon(String loginName) {
        try {
            if (!redisWrapperClient.exists(NEWBIE_COUPON_ALERT_KEY)) {
                redisWrapperClient.set(NEWBIE_COUPON_ALERT_KEY, objectMapper.writeValueAsString(Sets.newHashSet()));
            }
            String redisValue = redisWrapperClient.get(NEWBIE_COUPON_ALERT_KEY);
            Set<String> loginNames = objectMapper.readValue(redisValue, new TypeReference<Set<String>>() {
            });
            if (loginNames.contains(loginName)) {
                return null;
            }
            List<UserCouponModel> userCoupons = userCouponMapper.findByLoginName(loginName);
            Optional<UserCouponModel> found = Iterators.tryFind(userCoupons.iterator(), new Predicate<UserCouponModel>() {
                @Override
                public boolean apply(UserCouponModel userCouponModel) {
                    CouponModel couponModel = couponMapper.findById(userCouponModel.getCouponId());
                    return couponModel.getCouponType() == CouponType.NEWBIE_COUPON && InvestStatus.SUCCESS != userCouponModel.getStatus() && new DateTime(couponModel.getEndTime()).plusDays(1).withTimeAtStartOfDay().isAfterNow();
                }
            });

            if (found.isPresent()) {
                loginNames.add(loginName);
                redisWrapperClient.set(NEWBIE_COUPON_ALERT_KEY, objectMapper.writeValueAsString(loginNames));
                return new UserCouponDto(couponMapper.findById(found.get().getCouponId()), found.get());
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<UserCouponDto> getUsableCoupons(String loginName, final long loanId) {
        final LoanModel loanModel = loanMapper.findById(loanId);
        if (loanModel == null) {
            return Lists.newArrayList();
        }

        List<UserCouponModel> userCouponModels = userCouponMapper.findByLoginName(loginName);
        List<UserCouponDto> couponDtoList = Lists.transform(userCouponModels, new Function<UserCouponModel, UserCouponDto>() {
            @Override
            public UserCouponDto apply(UserCouponModel input) {
                return new UserCouponDto(couponMapper.findById(input.getInvestId()), input);
            }
        });

        final boolean hasUsedCoupons = Iterators.tryFind(couponDtoList.iterator(), new Predicate<UserCouponDto>() {
            @Override
            public boolean apply(UserCouponDto input) {
                return input.getCouponType() != CouponType.RED_ENVELOPE && input.isUsed() && input.getLoanId() == loanModel.getId();
            }
        }).isPresent();

        return Lists.newArrayList(Iterators.filter(couponDtoList.iterator(), new Predicate<UserCouponDto>() {
            @Override
            public boolean apply(UserCouponDto input) {
                return input.getProductTypeList().contains(loanModel.getProductType()) && input.isUnused() && (input.getCouponType() == CouponType.RED_ENVELOPE || !hasUsedCoupons);
            }
        }));
    }

    @Override
    public BaseDto<BasePaginationDataDto> findUseRecords(List<CouponType> couponTypeList, String loginName, int index, int pageSize) {
        int count = userCouponMapper.findUseRecordsCount(couponTypeList, loginName);
        List<CouponUseRecordView> couponUseRecordList = userCouponMapper.findUseRecords(couponTypeList, loginName, (index - 1) * pageSize, pageSize);

        for (CouponUseRecordView curm : couponUseRecordList) {
            curm.setExpectedIncomeStr(AmountConverter.convertCentToString(curm.getExpectedIncome()));
            curm.setCouponAmountStr(AmountConverter.convertCentToString(curm.getCouponAmount()));
        }

        BaseDto<BasePaginationDataDto> baseDto = new BaseDto<>();
        BasePaginationDataDto<CouponUseRecordView> dataDto = new BasePaginationDataDto<>(index, pageSize, count, couponUseRecordList);
        baseDto.setData(dataDto);
        dataDto.setStatus(true);
        return baseDto;
    }

}
