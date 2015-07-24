package com.ttsd.api.controller;

import com.esoft.core.annotations.Logger;
import com.ttsd.api.dto.BaseResponseDto;
import com.ttsd.api.dto.RegisterRequestDto;
import com.ttsd.api.dto.RegisterResponseDto;
import com.ttsd.api.dto.SendSmsRequestDto;
import com.ttsd.api.service.MobileRegisterAppService;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/mobileAppRegister")
public class MobileRegisterAppController {

    @Logger
    static Log log;
    @Resource
    private MobileRegisterAppService mobileRegisterAppService;

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody
    public RegisterResponseDto registerUser(@RequestBody RegisterRequestDto registerRequestDto) {
        return mobileRegisterAppService.registerUser(registerRequestDto);
    }

    @RequestMapping(value = "/sendSms", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponseDto sendRegisterByMobileNumberSMS(@RequestBody SendSmsRequestDto sendSmsRequestDto) {
        String mobileNumber = sendSmsRequestDto.getPhoneNum();
        return mobileRegisterAppService.sendRegisterByMobileNumberSMS(mobileNumber);
    }

}
