package com.tuotiansudai.web.controller;

import com.tuotiansudai.repository.model.Role;
import com.tuotiansudai.service.*;
import com.tuotiansudai.utils.DateUtil;
import com.tuotiansudai.utils.LoginUserInfo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Controller
@RequestMapping(value = "/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private LoanRepayService loanRepayService;

    @Autowired
    private InvestRepayService investRepayService;

    @Autowired
    private UserBillService userBillService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView account() {
        ModelAndView modelAndView = new ModelAndView("/account");
        Date startTime = DateUtil.getFirstDayOfMonth(new Date());
        Date endTime = DateUtils.addMonths(startTime, 1);
        String loginName = LoginUserInfo.getLoginName();
        modelAndView.addObject("loginName",loginName);
        modelAndView.addObject("balance",accountService.getBalance(loginName));
        modelAndView.addObject("collectedReward", userBillService.findSumRewardByLoginName(loginName));
        modelAndView.addObject("collectingPrincipal",investRepayService.findSumRepayingCorpusByLoginName(loginName));
        modelAndView.addObject("collectingInterest",investRepayService.findSumRepayingInterestByLoginName(loginName));
        modelAndView.addObject("collectedInterest",investRepayService.findSumRepaidInterestByLoginName(loginName));
        modelAndView.addObject("freeze",accountService.getFreeze(loginName));
        if (userRoleService.judgeUserRoleExist(loginName, Role.LOANER)){
            modelAndView.addObject("successSumRepay",loanRepayService.findByLoginNameAndTimeSuccessRepay(loginName,startTime,endTime));
            modelAndView.addObject("repayList", loanRepayService.findLoanRepayInAccount(loginName, startTime, endTime, 0, 6));
        }
        modelAndView.addObject("successSumInvestRepay", investRepayService.findByLoginNameAndTimeAndSuccessInvestRepay(loginName, startTime, endTime));
        modelAndView.addObject("successSumInvestRepayList", investRepayService.findByLoginNameAndTimeSuccessInvestRepayList(loginName, startTime, endTime, 0, 5));
        modelAndView.addObject("notSuccessSumInvestRepay", investRepayService.findByLoginNameAndTimeAndNotSuccessInvestRepay(loginName, startTime, endTime));
        modelAndView.addObject("notSuccessSumInvestRepayList", investRepayService.findByLoginNameAndTimeNotSuccessInvestRepayList(loginName, startTime, endTime, 0, 5));
        modelAndView.addObject("latestInvestList", investRepayService.findLatestInvestByLoginName(loginName, 0, 4));
        return modelAndView;
    }

}
