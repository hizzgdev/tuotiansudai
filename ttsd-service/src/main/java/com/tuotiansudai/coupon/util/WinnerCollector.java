package com.tuotiansudai.coupon.util;

import com.tuotiansudai.repository.model.CouponModel;
import com.tuotiansudai.repository.model.UserModel;
import org.springframework.stereotype.Service;

@Service
public class WinnerCollector implements UserCollector {

    @Override
    public boolean contains(CouponModel couponModel, UserModel userModel) {
        return true;
    }

}
