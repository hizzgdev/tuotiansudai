package com.tuotiansudai.api.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.tuotiansudai.api.dto.v1_0.BaseParam;
import com.tuotiansudai.api.dto.v1_0.BaseResponseDto;
import com.tuotiansudai.api.dto.v1_0.LoginResponseDataDto;
import com.tuotiansudai.api.dto.v1_0.ReturnMessage;
import com.tuotiansudai.api.dto.v2_0.BaseParamDto;
import com.tuotiansudai.client.AppTokenRedisWrapperClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.UUID;

@Service
public class MobileAppTokenProvider {

    private final static Logger log = Logger.getLogger(MobileAppTokenProvider.class);

    private int tokenExpiredSeconds = 3600;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final static String TOKEN_TEMPLATE = "app-token:{0}:{1}";

    @Autowired
    private AppTokenRedisWrapperClient appTokenRedisWrapperClient;

    public String refreshToken(String loginName) {
        appTokenRedisWrapperClient.delPattern(MessageFormat.format(TOKEN_TEMPLATE, loginName, "*"));

        String token = MessageFormat.format(TOKEN_TEMPLATE, loginName, UUID.randomUUID().toString());
        appTokenRedisWrapperClient.setex(token, this.tokenExpiredSeconds, loginName);
        log.debug(MessageFormat.format("[MobileAppTokenProvider][refreshToken] loginName: {0} newToken: {1}", loginName, token));
        return token;
    }

    public void deleteToken(HttpServletRequest httpServletRequest) {
        String loginName = this.getLoginName(httpServletRequest);
        if (!Strings.isNullOrEmpty(loginName)) {
            appTokenRedisWrapperClient.delPattern(MessageFormat.format(TOKEN_TEMPLATE, loginName, "*"));
        }
    }

    public String getLoginName(HttpServletRequest httpServletRequest) {
        String token = this.getToken(httpServletRequest);
        if (Strings.isNullOrEmpty(token)) {
            return null;
        }
        return appTokenRedisWrapperClient.get(token);
    }

    public BaseResponseDto generateResponseDto(String token) {
        LoginResponseDataDto loginResponseDataDto = new LoginResponseDataDto();
        loginResponseDataDto.setToken(token);

        BaseResponseDto<LoginResponseDataDto> dto = new BaseResponseDto<>();
        dto.setCode(ReturnMessage.SUCCESS.getCode());
        dto.setMessage(ReturnMessage.SUCCESS.getMsg());
        dto.setData(loginResponseDataDto);

        return dto;
    }

    public BaseResponseDto generateResponseDto(ReturnMessage returnMessage) {
        LoginResponseDataDto loginResponseDataDto = new LoginResponseDataDto();
        loginResponseDataDto.setToken("");

        BaseResponseDto<LoginResponseDataDto> dto = new BaseResponseDto<>();
        dto.setData(loginResponseDataDto);

        dto.setCode(returnMessage.getCode());
        dto.setMessage(returnMessage.getMsg());
        return dto;
    }

    public void setTokenExpiredSeconds(int tokenExpiredSeconds) {
        this.tokenExpiredSeconds = tokenExpiredSeconds;
    }

    private String getToken(HttpServletRequest httpServletRequest) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            // 访问H5页面时token在header里
            if (!Strings.isNullOrEmpty(httpServletRequest.getHeader("token"))) {
                return httpServletRequest.getHeader("token");
            }
            // logout时token在query里
            if (!Strings.isNullOrEmpty(httpServletRequest.getParameter("token"))) {
                return httpServletRequest.getParameter("token");
            }
            // API token在baseParam里
            if (httpServletRequest.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
                BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpServletRequest);
                BaseParamDto dto = objectMapper.readValue(bufferedRequest.getInputStream(), BaseParamDto.class);
                BaseParam baseParam = dto.getBaseParam();
                return baseParam.getToken();
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
