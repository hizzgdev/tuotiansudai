package com.tuotiansudai.point.service.impl;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.tuotiansudai.coupon.dto.ExchangeCouponDto;
import com.tuotiansudai.coupon.repository.mapper.CouponExchangeMapper;
import com.tuotiansudai.coupon.repository.mapper.CouponMapper;
import com.tuotiansudai.coupon.repository.model.CouponModel;
import com.tuotiansudai.coupon.repository.model.UserGroup;
import com.tuotiansudai.coupon.service.CouponActivationService;
import com.tuotiansudai.point.repository.mapper.PointBillMapper;
import com.tuotiansudai.point.repository.model.PointBillModel;
import com.tuotiansudai.point.repository.model.PointBusinessType;
import com.tuotiansudai.point.service.PointExchangeService;
import com.tuotiansudai.repository.mapper.AccountMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class PointExchangeServiceImpl implements PointExchangeService {
    static Logger logger = Logger.getLogger(PointExchangeServiceImpl.class);

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private PointBillMapper pointBillMapper;

    @Autowired
    private CouponExchangeMapper couponExchangeMapper;

    @Autowired
    private CouponActivationService couponActivationService;

    @Override
    public List<ExchangeCouponDto> findExchangeableCouponList(){
        List<CouponModel> couponModels = couponMapper.findCouponExchangeableList(0,100);
        return Lists.transform(couponModels, new Function<CouponModel, ExchangeCouponDto>() {
            @Override
            public ExchangeCouponDto apply(CouponModel input) {
                ExchangeCouponDto exchangeCouponDto = new ExchangeCouponDto(input);
                exchangeCouponDto.setExchangePoint(couponExchangeMapper.findByCouponId(input.getId()).getExchangePoint());
                return exchangeCouponDto;
            }
        });
    }

    @Override
    public boolean exchangeableCoupon(long couponId, String loginName){
        long exchange_point = couponExchangeMapper.findByCouponId(couponId).getExchangePoint();
        long availablePoint = accountMapper.findUsersAccountAvailablePoint(loginName);
        return availablePoint >= exchange_point;
    }

    @Override
    @Transactional
    public void exchangeCoupon(long couponId, String loginName, long exchangePoint){
        couponActivationService.assignUserCoupon(loginName, Lists.newArrayList(UserGroup.EXCHANGER));
        PointBillModel pointBillModel = new PointBillModel(loginName, couponId, exchangePoint, PointBusinessType.EXCHANGE, PointBusinessType.EXCHANGE.name());
        pointBillMapper.create(pointBillModel);
        accountMapper.updateByLoginName(loginName, exchangePoint);
        couponMapper.updateByLoginName(loginName);
    }
}
