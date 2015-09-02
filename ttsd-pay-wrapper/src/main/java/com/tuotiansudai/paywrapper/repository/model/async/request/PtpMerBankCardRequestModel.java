package com.tuotiansudai.paywrapper.repository.model.async.request;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PtpMerBankCardRequestModel extends BaseAsyncModel {

    private String orderId;

    private String merDate;

    private String userId;

    private String cardId;

    private String accountName;

    private String identityType = "IDENTITY_CARD";

    private String identityCode;

    private String isOpenFastPayment = "0";

    public PtpMerBankCardRequestModel() {
    }

    public PtpMerBankCardRequestModel(String orderId, String cardNumber, String payUserId, String userName, String identityNumber) {
        super();
        this.service = "ptp_mer_bind_card";
        this.setNotifyUrl("http://121.43.71.173:13003/trusteeship_return_s2s/ptp_mer_bind_card");
        this.setRetUrl("http://121.43.71.173:13003/trusteeship_return_web/ptp_mer_bind_card");
        this.orderId = orderId;
        this.merDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.cardId = cardNumber;
        this.userId = payUserId;
        this.accountName = userName;
        this.identityCode = identityNumber;

    }

    @Override
    public Map<String, String> generatePayRequestData() {
        Map<String, String> payRequestData = super.generatePayRequestData();
        payRequestData.put("ret_url",this.getRetUrl());
        payRequestData.put("notify_url", this.getNotifyUrl());
        payRequestData.put("order_id", this.orderId);
        payRequestData.put("mer_date", this.merDate);
        payRequestData.put("user_id", this.userId);
        payRequestData.put("card_id", this.cardId);
        payRequestData.put("account_name", this.accountName);
        payRequestData.put("identity_type", this.identityType);
        payRequestData.put("identity_code", this.identityCode);
        payRequestData.put("is_open_fastPayment", this.isOpenFastPayment);
        return payRequestData;
    }
}
