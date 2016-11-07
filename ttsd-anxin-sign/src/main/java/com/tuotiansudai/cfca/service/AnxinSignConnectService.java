package com.tuotiansudai.cfca.service;

import cfca.sadk.algorithm.common.PKIException;
import cfca.trustsign.common.vo.cs.CreateContractVO;
import cfca.trustsign.common.vo.response.tx3.*;
import com.tuotiansudai.repository.model.AccountModel;
import com.tuotiansudai.repository.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AnxinSignConnectService {

    Tx3ResVO createAccount3001(AccountModel accountModel, UserModel userModel) throws PKIException;

    Tx3ResVO sendCaptcha3101(String userId, String projectCode, boolean isVoice) throws PKIException;

    Tx3ResVO verifyCaptcha3102(String userId, String projectCode, String checkCode) throws PKIException;

    Tx3202ResVO generateContractBatch3202(long loanId,String batchNo, List<CreateContractVO> createContractlist) throws PKIException;

}
