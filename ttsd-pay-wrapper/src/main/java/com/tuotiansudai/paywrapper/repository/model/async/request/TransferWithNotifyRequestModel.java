package com.tuotiansudai.paywrapper.repository.model.async.request;

import com.tuotiansudai.paywrapper.repository.model.UmPayParticAccType;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class TransferWithNotifyRequestModel extends BaseAsyncRequestModel {

    private String orderId;

    private String particUserId;

    private String particAccType = "01";

    private String merDate;

    private String amount;

    public TransferWithNotifyRequestModel() {

    }

    public static TransferWithNotifyRequestModel newCouponRepayRequest(String orderId, String payUserId, String amount) {
        return new TransferWithNotifyRequestModel(orderId,
                payUserId,
                amount,
                "coupon_repay_notify");
    }

    public static TransferWithNotifyRequestModel newExtraRateRequest(String orderId, String payUserId, String amount){
        return new TransferWithNotifyRequestModel(orderId, payUserId, amount, "extra_rate_notify");
    }

    private TransferWithNotifyRequestModel(String orderId, String payUserId, String amount, String notifyUrl) {
        this.service = "transfer";
        this.orderId = orderId;
        this.merDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.particUserId = payUserId;
        this.particAccType = UmPayParticAccType.INDIVIDUAL.getCode();
        this.amount = amount;
        this.notifyUrl = MessageFormat.format("{0}/{1}", getCallbackBackHost(), notifyUrl);
    }

    @Override
    public Map<String, String> generatePayRequestData() {
        Map<String, String> payRequestData = super.generatePayRequestData();
        payRequestData.put("notify_url", this.notifyUrl);
        payRequestData.put("order_id", this.orderId);
        payRequestData.put("mer_date", this.merDate);
        payRequestData.put("partic_user_id", this.particUserId);
        payRequestData.put("partic_acc_type", this.particAccType);
        payRequestData.put("amount", this.amount);
        return payRequestData;
    }
}
