package com.tuotiansudai.activity.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.tuotiansudai.activity.repository.dto.DrawLotteryResultDto;
import com.tuotiansudai.activity.repository.model.LotteryPrize;
import com.tuotiansudai.activity.repository.model.UserLotteryPrizeView;
import com.tuotiansudai.activity.service.AutumnPrizeService;
import com.tuotiansudai.activity.service.LotteryActivityService;
import com.tuotiansudai.client.RedisWrapperClient;
import com.tuotiansudai.service.AccountService;
import com.tuotiansudai.service.BindBankCardService;
import com.tuotiansudai.spring.LoginUserInfo;
import com.tuotiansudai.util.AmountConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(path = "/activity/autumn")
public class AutumnPrizeController {

    @Value(value = "${activity.autumn.invest.channel}")
    private String activityAutumnInvestChannelKey;

    @Autowired
    private AutumnPrizeService autumnPrizeService;

    @Autowired
    private RedisWrapperClient redisWrapperClient;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BindBankCardService bindBankCardService;

    @Autowired
    private LotteryActivityService lotteryActivityService;

    @RequestMapping(path = "/travel", method = RequestMethod.GET)
    public ModelAndView travelPrize() {
        String loginName = LoginUserInfo.getLoginName();
        ModelAndView modelAndView = new ModelAndView("/activities/autumn-travel", "responsive", true);
        modelAndView.addObject("today", new Date());
        modelAndView.addObject("myInvestAmount", AmountConverter.convertCentToString(autumnPrizeService.getTodayInvestAmount(loginName, "travel")));
        modelAndView.addObject("travelPrize", autumnPrizeService.getTravelPrizeItems());
        modelAndView.addObject("userTravelPrize", autumnPrizeService.getTravelAwardItems(loginName));
        modelAndView.addObject("myTravelPrize", autumnPrizeService.getMyTravelAwardItems(LoginUserInfo.getMobile()));
        modelAndView.addObject("drawTime", lotteryActivityService.getDrawPrizeTime(LoginUserInfo.getMobile()));
        modelAndView.addObject("steps", generateSteps(loginName));
        modelAndView.addObject("activityType","travel");
        return modelAndView;
    }

    private List<Integer> generateSteps(String loginName) {
        List<Integer> steps = Lists.newArrayList(1, 0, 0, 0, 0);
        if (Strings.isNullOrEmpty(loginName)) {
            return steps;
        }
        steps.set(0, 2);
        if (accountService.findByLoginName(loginName) == null) {
            steps.set(1, 1);
            return steps;
        }
        steps.set(1, 2);
        steps.set(2, 1);
        steps.set(3, 1);
        steps.set(4, 1);
        if (bindBankCardService.getPassedBankCard(loginName) != null) {
            steps.set(2, 2);
        }
        return steps;
    }

    @RequestMapping(path = "/luxury", method = RequestMethod.GET)
    public ModelAndView luxuryPrize() {
        String loginName = LoginUserInfo.getLoginName();
        ModelAndView modelAndView = new ModelAndView("/activities/autumn-luxury", "responsive", true);
        modelAndView.addObject("today", new Date());
        modelAndView.addObject("myInvestAmount", AmountConverter.convertCentToString(autumnPrizeService.getTodayInvestAmount(loginName, "luxury")));
        modelAndView.addObject("luxuryPrize", autumnPrizeService.getLuxuryPrizeItems());
        modelAndView.addObject("userLuxuryPrize", autumnPrizeService.getLuxuryAwardItems(loginName));
        modelAndView.addObject("myLuxuryPrize", autumnPrizeService.getMyLuxuryAwardItems(LoginUserInfo.getMobile()));
        modelAndView.addObject("steps", generateSteps(loginName));
        modelAndView.addObject("drawTime", lotteryActivityService.getDrawPrizeTime(LoginUserInfo.getMobile()));
        modelAndView.addObject("activityType","luxury");
        return modelAndView;
    }

    @RequestMapping(path = "/travel/{id:^\\d+$}/detail", method = RequestMethod.GET)
    public ModelAndView travelPrizeDetail(@PathVariable final long id) {
        ModelAndView modelAndView = new ModelAndView("/activities/autumn-travel-detail", "responsive", true);
        modelAndView.addObject("prizeId", id);
        return modelAndView;
    }

    @RequestMapping(path = "/travel/invest", method = RequestMethod.POST)
    @ResponseBody
    public String travelInvest(@RequestParam(value = "loginName", required = false) String loginName) {
        loginName = LoginUserInfo.getLoginName() != null ? LoginUserInfo.getLoginName() : loginName;
        if (!Strings.isNullOrEmpty(loginName)) {
            redisWrapperClient.hset(this.activityAutumnInvestChannelKey, loginName, "travel", 3600 * 24 * 60);
        }
        return "travel";
    }

    @RequestMapping(path = "/luxury/invest", method = RequestMethod.POST)
    @ResponseBody
    public String luxuryInvest(@RequestParam(value = "loginName", required = false) String loginName) {
        loginName = LoginUserInfo.getLoginName() != null ? LoginUserInfo.getLoginName() : loginName;
        if (!Strings.isNullOrEmpty(loginName)) {
            redisWrapperClient.hset(this.activityAutumnInvestChannelKey, loginName, "luxury", 3600 * 24 * 60);
        }
        return "luxury";
    }

    @RequestMapping(path = "/luxury/{id:^\\d+$}/detail", method = RequestMethod.GET)
    public ModelAndView luxuryPrizeDetail(@PathVariable final long id) {
        ModelAndView modelAndView = new ModelAndView("/activities/autumn-luxury-detail", "responsive", true);
        modelAndView.addObject("prizeId", id);

        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value = "/travel-draw", method = RequestMethod.POST)
    public DrawLotteryResultDto travelDrawPrize(@RequestParam(value = "mobile", required = false) String mobile) {
        return lotteryActivityService.drawLotteryPrize(Strings.isNullOrEmpty(LoginUserInfo.getMobile()) ? mobile : LoginUserInfo.getMobile() , LotteryPrize.TOURISM);
    }

    @ResponseBody
    @RequestMapping(value = "/luxury-draw", method = RequestMethod.POST)
    public DrawLotteryResultDto luxuryDrawPrize(@RequestParam(value = "mobile", required = false) String mobile) {
        return lotteryActivityService.drawLotteryPrize(Strings.isNullOrEmpty(LoginUserInfo.getMobile()) ? mobile : LoginUserInfo.getMobile(), LotteryPrize.LUXURY);
    }

    @ResponseBody
    @RequestMapping(value = "/travel-user-list", method = RequestMethod.GET)
    public List<UserLotteryPrizeView> getTravelRecordByLoginName(@RequestParam(value = "mobile", required = false) String mobile) {
        return lotteryActivityService.findDrawLotteryPrizeRecordByMobile(Strings.isNullOrEmpty(LoginUserInfo.getMobile()) ? mobile : LoginUserInfo.getMobile(), LotteryPrize.TOURISM);
    }

    @ResponseBody
    @RequestMapping(value = "/travel-all-list", method = RequestMethod.GET)
    public List<UserLotteryPrizeView> getTravelRecordByAll() {
        return lotteryActivityService.findDrawLotteryPrizeRecord(null, LotteryPrize.TOURISM);
    }

    @ResponseBody
    @RequestMapping(value = "/luxury-user-list", method = RequestMethod.GET)
    public List<UserLotteryPrizeView> getLuxuryRecordByLoginName(@RequestParam(value = "mobile", required = false) String mobile) {
        return lotteryActivityService.findDrawLotteryPrizeRecordByMobile(Strings.isNullOrEmpty(LoginUserInfo.getMobile()) ? mobile : LoginUserInfo.getMobile(), LotteryPrize.LUXURY);
    }

    @ResponseBody
    @RequestMapping(value = "/luxury-all-list", method = RequestMethod.GET)
    public List<UserLotteryPrizeView> getLuxuryRecordByAll() {
        return lotteryActivityService.findDrawLotteryPrizeRecord(null, LotteryPrize.LUXURY);
    }
}
