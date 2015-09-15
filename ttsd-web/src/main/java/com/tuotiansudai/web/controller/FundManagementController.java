package com.tuotiansudai.web.controller;

import com.tuotiansudai.dto.UserBillDto;
import com.tuotiansudai.repository.model.AccountModel;
import com.tuotiansudai.repository.model.UserBillBusinessType;
import com.tuotiansudai.service.AccountService;
import com.tuotiansudai.service.RechargeService;
import com.tuotiansudai.service.UserBillService;
import com.tuotiansudai.service.WithdrawService;
import com.tuotiansudai.utils.LoginUserInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/9/10.
 */
@Controller
@RequestMapping(value = "/fund")
public class FundManagementController {

    static Logger logger = Logger.getLogger(FundManagementController.class);

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private WithdrawService withdrawService;

    @RequestMapping(value = "/management", method = RequestMethod.GET)
    public ModelAndView fundManagement(@RequestParam("startTime") @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime,@RequestParam("endTime") @DateTimeFormat(pattern="yyyy-MM-dd") Date endTime,
                                       @RequestParam("currentPage") int currentPage,@RequestParam("userBillBusinessType") List<UserBillBusinessType> userBillBusinessTypes) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        ModelAndView modelAndView = new ModelAndView("/fund");
        List<UserBillDto> userBillDtos = userBillService.findUserBills(userBillBusinessTypes,currentPage,startTime,endTime);
        modelAndView.addObject("userBillDtos",userBillDtos);
        AccountModel accountModel = accountService.findByLoginName(LoginUserInfo.getLoginName());
        modelAndView.addObject("accountModelBalance",decimalFormat.format(accountModel!=null?accountModel.getBalance():0L/100D));
        int countNum = userBillService.findUserBillsCount(userBillBusinessTypes,startTime,endTime);
        modelAndView.addObject("countNum",countNum);
        String sumRecharge = rechargeService.findSumRechargeByLoginName(LoginUserInfo.getLoginName());
        modelAndView.addObject("sumRecharge",sumRecharge);
        String sumWithdraw = withdrawService.findSumWithdrawByLoginName(LoginUserInfo.getLoginName());
        modelAndView.addObject("sumWithdraw",sumWithdraw);
        modelAndView.addObject("currentPage",currentPage);
        return modelAndView;
    }

}
