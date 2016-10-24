package com.tuotiansudai.paywrapper.service;

import com.tuotiansudai.paywrapper.exception.PayException;

public interface RepayGeneratorService {

    void generateRepay(long loanId) throws PayException;

}
