package com.tuotiansudai.fudian.controller;

import com.tuotiansudai.fudian.config.ApiType;
import com.tuotiansudai.fudian.dto.request.*;
import com.tuotiansudai.fudian.dto.response.ResponseDto;
import com.tuotiansudai.fudian.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class PayController {

    private static Logger logger = LoggerFactory.getLogger(PayController.class);

    private final RechargeService rechargeService;

    private final WithdrawService withdrawService;

    private final LoanCreateService loanCreateService;

    private final LoanInvestService loanInvestService;

    private final LoanCreditInvestService loanCreditInvestService;

    private final LoanFullService loanFullService;

    private final LoanRepayService loanRepayService;

    private final MerchantTransferService merchantTransferService;

    @Autowired
    public PayController(RechargeService rechargeService, WithdrawService withdrawService, LoanCreateService loanCreateService, LoanInvestService loanInvestService, LoanCreditInvestService loanCreditInvestService, LoanFullService loanFullService, LoanRepayService loanRepayService, MerchantTransferService merchantTransferService) {
        this.rechargeService = rechargeService;
        this.withdrawService = withdrawService;
        this.loanCreateService = loanCreateService;
        this.loanInvestService = loanInvestService;
        this.loanCreditInvestService = loanCreditInvestService;
        this.loanFullService = loanFullService;
        this.loanRepayService = loanRepayService;
        this.merchantTransferService = merchantTransferService;
    }

    @RequestMapping(path = "/recharge", method = RequestMethod.GET)
    public String recharge(Map<String, Object> model) {
        logger.info("[Fudian] call recharge");

//        String data = rechargeService.recharge("UU02615960791461001", "UA02615960791501001", "10000.00", RechargePayType.GATE_PAY);
//        String data = rechargeService.recharge("UU02619471098561001", "UA02619471098591001", "10000.00", RechargePayType.GATE_PAY);
        RechargeRequestDto requestDto = rechargeService.recharge("UU02624634769241001", "UA02624634769281001", "1000000.00", RechargePayType.GATE_PAY);//商户
        model.put("message", requestDto.getRequestData());
        model.put("path", ApiType.RECHARGE.getPath());
        return "post";
    }

    @RequestMapping(path = "/merchant-recharge", method = RequestMethod.GET)
    public String merchantRecharge(Map<String, Object> model) {
        logger.info("[Fudian] call merchant recharge");

        RechargeRequestDto requestDto = rechargeService.merchantRecharge("1000000.00");
        model.put("message", requestDto.getRequestData());
        model.put("path", ApiType.RECHARGE.getPath());
        return "post";
    }

    @RequestMapping(path = "/withdraw", method = RequestMethod.GET)
    public String withdraw(Map<String, Object> model) {
        logger.info("[Fudian] call withdraw");

        WithdrawRequestDto requestDto = withdrawService.withdraw("UU02615960791461001", "UA02615960791501001", "1.00");
        model.put("message", requestDto.getRequestData());
        model.put("path", ApiType.WITHDRAW.getPath());
        return "post";
    }

    @RequestMapping(path = "/loan-create", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto> loanCreate(Map<String, Object> model) {
        logger.info("[Fudian] call loan create");

        ResponseDto responseDto = loanCreateService.create("UU02615960791461001", "UA02615960791501001", "10.00", "LOAN_CREDIT");

        return ResponseEntity.ok(responseDto);
    }

    @RequestMapping(path = "/loan-full", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto> loanFull(Map<String, Object> model) {
        logger.info("[Fudian] call loan full");

        ResponseDto responseDto = loanFullService.full("UU02615960791461001", "UA02615960791501001", "LU02625453517541001", "20180427000000000001", "20180427", "20180701");

        return ResponseEntity.ok(responseDto);
    }

    @RequestMapping(path = "/loan-invest", method = RequestMethod.GET)
    public String loanInvest(Map<String, Object> model) {
        logger.info("[Fudian] call loan invest");

//        loanInvestService.invest("UU02615960791461001", "UA02615960791501001", "1.00", "0.00", "LU02619459384521001");
        LoanInvestRequestDto requestDto = loanInvestService.invest("UU02619471098561001", "UA02619471098591001", "10.00", "0.00", "LU02625453517541001");
        model.put("message", requestDto.getRequestData());
        model.put("path", ApiType.LOAN_INVEST.getPath());
        return "post";
    }

    @RequestMapping(path = "/loan-credit-invest", method = RequestMethod.GET)
    public String loanCreditInvest(Map<String, Object> model) {
        logger.info("[Fudian] call loan credit invest");

        LoanCreditInvestRequestDto requestDto = loanCreditInvestService.invest("UU02624634769241001", "UA02624634769281001", "LU02625453517541001", "20180427000000000002", "20180427", "3", "1.00", "1.00", "100.00");
        model.put("message", requestDto.getRequestData());
        model.put("path", ApiType.LOAN_CREDIT_INVEST.getPath());
        return "post";
    }

    @RequestMapping(path = "/loan-fast-invest", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto> loanFastInvest(Map<String, Object> model) {
        logger.info("[Fudian] call loan fast invest");

//        String data = loanInvestService.invest("UU02615960791461001", "UA02615960791501001", "1.00", "0.00", "LU02619459384521001");
        ResponseDto responseDto = loanInvestService.fastInvest("UU02619471098561001", "UA02619471098591001", "1.00", "0.00", "LU02619459384521001");
        return ResponseEntity.ok(responseDto);
    }

    @RequestMapping(path = "/loan-repay", method = RequestMethod.GET)
    public String loanRepay(Map<String, Object> model) {
        logger.info("[Fudian] call loan repay");

        LoanRepayRequestDto requestDto = loanRepayService.repay("UU02615960791461001", "UA02615960791501001", "LU02625453517541001", "0.00", "1.00");
        model.put("message", requestDto.getRequestData());
        model.put("path", ApiType.LOAN_REPAY.getPath());
        return "post";
    }

    @RequestMapping(path = "/loan-fast-repay", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto> loanFastRepay(Map<String, Object> model) {
        logger.info("[Fudian] call loan fast repay");

        ResponseDto responseDto = loanRepayService.fastRepay("UU02615960791461001", "UA02615960791501001", "LU02619459384521001", "1.00", "0.00");

        return ResponseEntity.ok(responseDto);
    }

    @RequestMapping(path = "/merchant-transfer", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto> merchantTransfer(Map<String, Object> model) {
        logger.info("[Fudian] call merchant transfer");

        ResponseDto responseDto = merchantTransferService.transfer("UU02615960791461001", "UA02615960791501001", "0.01");

        return ResponseEntity.ok(responseDto);
    }
}