package com.tuotiansudai.diagnosis.bill.diagnoses;

import com.tuotiansudai.repository.mapper.CouponRepayMapper;
import com.tuotiansudai.repository.mapper.UserCouponMapper;
import com.tuotiansudai.enums.UserBillBusinessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BirthdayCouponDiagnosis extends UserCouponDiagnosis {
    private static Logger logger = LoggerFactory.getLogger(BirthdayCouponDiagnosis.class);

    @Autowired
    public BirthdayCouponDiagnosis(UserCouponMapper userCouponMapper, CouponRepayMapper couponRepayMapper) {
        super(userCouponMapper, couponRepayMapper);
    }

    @Override
    public UserBillBusinessType getSupportedBusinessType() {
        return UserBillBusinessType.BIRTHDAY_COUPON;
    }
}
