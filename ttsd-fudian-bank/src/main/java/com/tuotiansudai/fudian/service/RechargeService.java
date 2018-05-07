package com.tuotiansudai.fudian.service;

import com.google.common.base.Strings;
import com.tuotiansudai.fudian.config.ApiType;
import com.tuotiansudai.fudian.config.BankConfig;
import com.tuotiansudai.fudian.dto.request.RechargePayType;
import com.tuotiansudai.fudian.dto.request.RechargeRequestDto;
import com.tuotiansudai.fudian.dto.response.ResponseDto;
import com.tuotiansudai.fudian.mapper.InsertMapper;
import com.tuotiansudai.fudian.mapper.UpdateMapper;
import com.tuotiansudai.fudian.sign.SignatureHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RechargeService implements AsyncCallbackInterface {

    private static Logger logger = LoggerFactory.getLogger(RechargeService.class);

    private final BankConfig bankConfig;

    private final SignatureHelper signatureHelper;

    private final InsertMapper insertMapper;

    private final UpdateMapper updateMapper;

    @Autowired
    public RechargeService(BankConfig bankConfig, SignatureHelper signatureHelper, InsertMapper insertMapper, UpdateMapper updateMapper) {
        this.bankConfig = bankConfig;
        this.signatureHelper = signatureHelper;
        this.insertMapper = insertMapper;
        this.updateMapper = updateMapper;
    }

    public RechargeRequestDto recharge(String userName, String accountNo, String amount, RechargePayType payType, String loginName, String mobile) {
        RechargeRequestDto dto = new RechargeRequestDto(userName, accountNo, amount, payType, loginName, mobile);
        signatureHelper.sign(dto, ApiType.RECHARGE);

        if (Strings.isNullOrEmpty(dto.getRequestData())) {
            logger.error("[recharge] sign error, userName: {}, accountNo: {}, amount: {}, payType: {}", userName, accountNo, amount, payType);
            return null;
        }
        insertMapper.insertRecharge(dto);
        return dto;

    }

    public RechargeRequestDto merchantRecharge(String amount, String loginName, String mobile) {
        RechargeRequestDto dto = new RechargeRequestDto(bankConfig.getMerchantUserName(), bankConfig.getMerchantAccountNo(), amount, RechargePayType.GATE_PAY, loginName, mobile);

        signatureHelper.sign(dto, ApiType.RECHARGE);

        if (Strings.isNullOrEmpty(dto.getRequestData())) {
            logger.error("[merchant recharge sign] sign error, userName: {}, accountNo: {}, amount: {}, payType: {}",
                    bankConfig.getMerchantUserName(), bankConfig.getMerchantAccountNo(), amount, RechargePayType.GATE_PAY);
            return null;
        }

        insertMapper.insertRecharge(dto);
        return dto;
    }

    @Override
    public ResponseDto callback(String responseData) {
        logger.info("[recharge callback] data is {}", responseData);

        ResponseDto responseDto = ApiType.RECHARGE.getParser().parse(responseData);

        if (responseDto == null) {
            logger.error("[recharge callback] parse callback data error, data is {}", responseData);
            return null;
        }

        responseDto.setReqData(responseData);
        updateMapper.updateRecharge(responseDto);
        return responseDto;
    }
}
