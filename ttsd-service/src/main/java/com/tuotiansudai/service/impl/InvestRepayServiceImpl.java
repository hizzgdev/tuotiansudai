package com.tuotiansudai.service.impl;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.tuotiansudai.coupon.repository.mapper.CouponMapper;
import com.tuotiansudai.coupon.repository.mapper.UserCouponMapper;
import com.tuotiansudai.coupon.repository.model.CouponModel;
import com.tuotiansudai.coupon.repository.model.UserCouponModel;
import com.tuotiansudai.coupon.repository.model.UserCouponView;
import com.tuotiansudai.dto.InvestRepayDataItemDto;
import com.tuotiansudai.repository.mapper.InvestRepayMapper;
import com.tuotiansudai.repository.model.CouponType;
import com.tuotiansudai.repository.model.InvestRepayModel;
import com.tuotiansudai.repository.model.LatestInvestView;
import com.tuotiansudai.service.InvestRepayService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InvestRepayServiceImpl implements InvestRepayService{

    @Autowired
    private InvestRepayMapper investRepayMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public long findByLoginNameAndTimeAndSuccessInvestRepay(String loginName,Date startTime,Date endTime) {
        return investRepayMapper.findByLoginNameAndTimeAndSuccessInvestRepay(loginName, startTime, endTime);
    }

    @Override
    public long findByLoginNameAndTimeAndNotSuccessInvestRepay(String loginName,Date startTime,Date endTime) {
        return investRepayMapper.findByLoginNameAndTimeAndNotSuccessInvestRepay(loginName, startTime, endTime);
    }

    @Override
    public List<InvestRepayDataItemDto> findByLoginNameAndTimeSuccessInvestRepayList(String loginName, Date startTime, Date endTime, int startLimit, int endLimit) {
        List<InvestRepayModel> investRepayModels = investRepayMapper.findByLoginNameAndTimeSuccessInvestRepayList(loginName, startTime, endTime, startLimit, endLimit);
        return investRepayCouponAll(investRepayModels, loginName);
    }

    @Override
    public List<InvestRepayDataItemDto> findByLoginNameAndTimeNotSuccessInvestRepayList(String loginName, Date startTime, Date endTime, int startLimit, int endLimit) {
        List<InvestRepayModel> investRepayModels = investRepayMapper.findByLoginNameAndTimeNotSuccessInvestRepayList(loginName, startTime, endTime, startLimit, endLimit);
        return investRepayCouponAll(investRepayModels, loginName);
    }

    private List<InvestRepayDataItemDto> investRepayCouponAll(List<InvestRepayModel> investRepayModels, String loginName) {

        List<InvestRepayDataItemDto> investRepayDataItemDtoList = Lists.transform(investRepayModels, new Function<InvestRepayModel, InvestRepayDataItemDto>() {
            @Override
            public InvestRepayDataItemDto apply(InvestRepayModel input) {
                InvestRepayDataItemDto investRepayDataItemDto =  new InvestRepayDataItemDto().generateInvestRepayDataItemDto(input);
                List<UserCouponModel> userCouponList = userCouponMapper.findByInvestId(investRepayDataItemDto.getInvestId());
                List<CouponType> couponTypeList;
                if (CollectionUtils.isNotEmpty(userCouponList)){
                    couponTypeList = new ArrayList<>();
                    for(UserCouponModel userCouponView : userCouponList){
                        CouponModel couponModel = couponMapper.findById(userCouponView.getCouponId());
                        if(couponModel != null && couponModel.getCouponType() != null){
                            couponTypeList.add(couponModel.getCouponType());
                        }
                    }
                    investRepayDataItemDto.setCouponTypeList(couponTypeList);
                }
                return investRepayDataItemDto;
            }
        });

        return investRepayDataItemDtoList;
    }

    @Override
    public List<LatestInvestView> findLatestInvestByLoginName(String loginName, int startLimit, int endLimit) {
        List<LatestInvestView> latestInvestViews = investRepayMapper.findLatestInvestByLoginName(loginName, startLimit, endLimit);
        for (LatestInvestView latestInvestView : latestInvestViews) {
            List<UserCouponModel> userCouponModels = userCouponMapper.findBirthdaySuccessByLoginNameAndInvestId(loginName, latestInvestView.getInvestId());
            latestInvestView.setBirthdayCoupon(CollectionUtils.isNotEmpty(userCouponModels));
            if (CollectionUtils.isNotEmpty(userCouponModels)) {
                latestInvestView.setBirthdayBenefit(couponMapper.findById(userCouponModels.get(0).getCouponId()).getBirthdayBenefit());
            }
        }
        return latestInvestViews;
    }

    @Override
    public long findSumRepaidInterestByLoginName(String loginName) {
        return investRepayMapper.findSumRepaidInterestByLoginName(loginName);
    }

    @Override
    public long findSumRepayingInterestByLoginName(String loginName) {
        return investRepayMapper.findSumRepayingInterestByLoginName(loginName);
    }

    @Override
    public long findSumRepayingCorpusByLoginName(String loginName) {
        return investRepayMapper.findSumRepayingCorpusByLoginName(loginName);
    }

    @Override
    public long findSumRepaidCorpusByLoginName(String loginName) {
        return investRepayMapper.findSumRepaidCorpusByLoginName(loginName);
    }


}
