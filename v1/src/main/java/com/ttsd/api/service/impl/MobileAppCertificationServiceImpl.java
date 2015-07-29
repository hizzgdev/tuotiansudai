package com.ttsd.api.service.impl;

import com.esoft.archer.user.exception.UserNotFoundException;
import com.esoft.archer.user.model.User;
import com.esoft.archer.user.service.UserService;
import com.esoft.core.annotations.Logger;
import com.esoft.umpay.trusteeship.exception.UmPayOperationException;
import com.esoft.umpay.user.service.impl.UmPayUserOperation;
import com.google.common.base.Strings;
import com.ttsd.api.dto.*;
import com.ttsd.api.service.MobileAppCertificationService;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by tuotian on 15/7/27.
 */
@Service(value = "MobileAppCertificationServiceImpl")
public class MobileAppCertificationServiceImpl implements MobileAppCertificationService{

    @Logger
    private Log log;

    @Autowired
    private UmPayUserOperation umPayUserOperation;

    @Autowired
    private UserService userService;

    /**
     * @function 移动端实名认证接口
     * @param certificationRequestDto 移动端用户实名认证请求参数包装类
     * @return CertificationResponseDto
     */
    @Override
    public CertificationResponseDto validateUserCertificationInfo(CertificationRequestDto certificationRequestDto) {
        BaseParam baseParam = certificationRequestDto.getBaseParam();//TODO 将此参数信息持久化到数据库中
        String userId = baseParam.getUserId();
        String userRealName = certificationRequestDto.getUserRealName();
        String idCardNumber = certificationRequestDto.getUserIdCardNumber();

        if (Strings.isNullOrEmpty(userId)){
            return assembleResult(ReturnMessage.USER_ID_IS_NULL.getCode(), ReturnMessage.USER_ID_IS_NULL.getMsg(), userRealName, idCardNumber);
        }
        if (Strings.isNullOrEmpty(userRealName)){
            return assembleResult(ReturnMessage.REAL_NAME_IS_NULL.getCode(), ReturnMessage.REAL_NAME_IS_NULL.getMsg(), userRealName, idCardNumber);
        }
        if (Strings.isNullOrEmpty(idCardNumber)){
            return assembleResult(ReturnMessage.ID_CARD_IS_NULL.getCode(), ReturnMessage.ID_CARD_IS_NULL.getMsg(), userRealName, idCardNumber);
        }
        if (userService.idCardIsExists(idCardNumber)){
            return assembleResult(ReturnMessage.ID_CARD_IS_EXIST.getCode(), ReturnMessage.ID_CARD_IS_EXIST.getMsg(), userRealName, idCardNumber);
        }
        User user = null;
        try {
            user = userService.getUserById(userId);
        } catch (UserNotFoundException e) {
            log.error("获取用户ID为："+userId+" 的用户信息异常！");
            log.error(e.getLocalizedMessage(),e);
            return assembleResult(ReturnMessage.USER_ID_NOT_EXIST.getCode(), ReturnMessage.USER_ID_NOT_EXIST.getMsg(), userRealName, idCardNumber);
        }
        if (user != null){
            user.setRealname(userRealName);
            user.setIdCard(idCardNumber);
            try {
                umPayUserOperation.createOperation(user, null);
                return assembleResult(ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMsg(), userRealName, idCardNumber);
            } catch (IOException e) {
                log.error("由于网络异常，ID为：" + userId + " 的用户实名认证失败！");
                log.error(e.getLocalizedMessage());
                return assembleResult(ReturnMessage.CERTIFICATION_FAIL.getCode(), ReturnMessage.CERTIFICATION_FAIL.getMsg(), userRealName, idCardNumber);
            } catch (UmPayOperationException e){
                log.error("用户ID为："+userId+" 的用户使用真实姓名为："+userRealName+"，身份证号为："+idCardNumber+"进行实名认证未通过！");
                log.error(e.getLocalizedMessage());
                return assembleResult(ReturnMessage.CERTIFICATION_FAIL.getCode(), ReturnMessage.CERTIFICATION_FAIL.getMsg(), userRealName, idCardNumber);
            }
        }
        return null;
    }


    /**
     * @function 封装结果集
     * @param code 消息代码
     * @param message 消息描述
     * @param userRealName 用户真实姓名
     * @param idCardNumber 用户身份证号码
     * @return CertificationResponseDto
     */
    public CertificationResponseDto assembleResult(String code,String message,String userRealName,String idCardNumber){
        CertificationDataDto certificationDataDto = new CertificationDataDto();
        certificationDataDto.setUserRealName(userRealName);
        certificationDataDto.setUserIdCardNumber(idCardNumber);
        CertificationResponseDto certificationResponseDto = new CertificationResponseDto();
        certificationResponseDto.setCode(code);
        certificationResponseDto.setMessage(message);
        certificationResponseDto.setData(certificationDataDto);
        return certificationResponseDto;
    }

    /*************************************set方法**************************************/
    public void setLog(Log log) {
        this.log = log;
    }

    public void setUmPayUserOperation(UmPayUserOperation umPayUserOperation) {
        this.umPayUserOperation = umPayUserOperation;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
