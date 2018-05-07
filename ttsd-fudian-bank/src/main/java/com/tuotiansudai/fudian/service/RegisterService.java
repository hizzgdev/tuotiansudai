package com.tuotiansudai.fudian.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuotiansudai.fudian.config.ApiType;
import com.tuotiansudai.fudian.dto.request.RegisterRequestDto;
import com.tuotiansudai.fudian.dto.response.RegisterContentDto;
import com.tuotiansudai.fudian.dto.response.ResponseDto;
import com.tuotiansudai.fudian.mapper.InsertMapper;
import com.tuotiansudai.fudian.mapper.UpdateMapper;
import com.tuotiansudai.fudian.sign.SignatureHelper;
import com.tuotiansudai.fudian.util.MessageQueueClient;
import com.tuotiansudai.mq.client.model.MessageTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class RegisterService implements AsyncCallbackInterface {

    private static Logger logger = LoggerFactory.getLogger(RegisterService.class);

    private final SignatureHelper signatureHelper;

    private final InsertMapper insertMapper;

    private final UpdateMapper updateMapper;

    private final MessageQueueClient messageQueueClient;

    private List<String> JSON_KEYS = Lists.newArrayList("apiType", "loginName", "mobile");

    @Autowired
    public RegisterService(SignatureHelper signatureHelper, InsertMapper insertMapper, UpdateMapper updateMapper, MessageQueueClient messageQueueClient) {
        this.signatureHelper = signatureHelper;
        this.insertMapper = insertMapper;
        this.updateMapper = updateMapper;
        this.messageQueueClient = messageQueueClient;
    }

    public RegisterRequestDto register(String loginName, String realName, String identityCode, String mobilePhone) {
        RegisterRequestDto dto = new RegisterRequestDto(loginName, realName, identityCode, mobilePhone);
        signatureHelper.sign(dto, ApiType.REGISTER);

        if (Strings.isNullOrEmpty(dto.getRequestData())) {
            logger.error("[register] sign error, realName: {}, identityCode: {}, mobilePhone: {}", realName, identityCode, mobilePhone);
            return null;
        }

        insertMapper.insertRegister(dto);
        return dto;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public ResponseDto callback(String responseData) {
        logger.info("[register callback] data is {}", responseData);

        ResponseDto responseDto = ApiType.REGISTER.getParser().parse(responseData);

        if (responseDto == null) {
            logger.error("[register callback] parse callback data error, data is {}", responseData);
            return null;
        }

        if (responseDto.isSuccess()) {
            RegisterContentDto registerContentDto = (RegisterContentDto) responseDto.getContent();
            HashMap<String, String> extMarkMap = new Gson().fromJson(registerContentDto.getExtMark(), new TypeToken<HashMap<String, String>>() {
            }.getType());

            if (!Sets.difference(extMarkMap.keySet(), Sets.newHashSet(JSON_KEYS)).isEmpty()) {
                logger.error("[register callback] formJson extMark error, extMark is {}", registerContentDto.getExtMark());
                return null;
            }

            this.messageQueueClient.publishMessage(MessageTopic.CertificationSuccess, Maps.newHashMap(ImmutableMap.<String, String>builder()
                    .put("loginName", extMarkMap.get("loginName"))
                    .put("mobilePhone", registerContentDto.getMobilePhone())
                    .put("identityCode", registerContentDto.getIdentityCode())
                    .put("realName", registerContentDto.getRealName())
                    .put("accountNo", registerContentDto.getAccountNo())
                    .put("userName", registerContentDto.getUserName())
                    .put("orderDate", registerContentDto.getRegDate())
                    .put("orderNo", registerContentDto.getOrderNo())
                    .build()));
        }

        responseDto.setReqData(responseData);
        updateMapper.updateRegister(responseDto);
        return responseDto;
    }
}
