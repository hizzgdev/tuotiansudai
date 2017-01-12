package com.tuotiansudai.coupon.service;

import com.tuotiansudai.coupon.repository.model.CouponModel;
import com.tuotiansudai.coupon.repository.model.UserCouponModel;
import com.tuotiansudai.coupon.repository.model.UserGroup;

import java.util.List;

public interface CouponAssignmentService {

    boolean assignUserCoupon(String loginNameOrMobile, String exchangeCode);

    void assignUserCoupon(String loginNameOrMobile, long couponId);

    List<CouponModel> asyncAssignUserCoupon(String loginNameOrMobile, List<UserGroup> userGroups);

    UserCouponModel assign(String loginName, long couponId, String exchangeCode);

    boolean assignInvestAchievementUserCoupon(String loginNameOrMobile, long loanId, long couponId);
}
