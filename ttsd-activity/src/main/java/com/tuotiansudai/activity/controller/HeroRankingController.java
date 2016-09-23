package com.tuotiansudai.activity.controller;

import com.tuotiansudai.dto.BasePaginationDataDto;
import com.tuotiansudai.repository.mapper.LoanMapper;
import com.tuotiansudai.repository.model.ActivityCategory;
import com.tuotiansudai.repository.model.HeroRankingView;
import com.tuotiansudai.repository.model.LoanModel;
import com.tuotiansudai.repository.model.LoanStatus;
import com.tuotiansudai.service.HeroRankingService;
import com.tuotiansudai.util.RandomUtils;
import com.tuotiansudai.spring.LoginUserInfo;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/activity/hero-ranking")
public class HeroRankingController {

    @Autowired
    private HeroRankingService heroRankingService;

    @Autowired
    private RandomUtils randomUtils;

    @Autowired
    private LoanMapper loanMapper;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView loadPageData() {
        String loginName = LoginUserInfo.getLoginName();
        Map param = heroRankingService.obtainHeroRankingAndInvestAmountByLoginName(ActivityCategory.HERO_RANKING, new Date(), loginName);
        Integer investRanking = Integer.parseInt(String.valueOf(param.get("investRanking")));
        String investAmount = param.get("investAmount").toString();
        ModelAndView modelAndView = new ModelAndView("/activities/hero-ranking", "responsive", true);
        modelAndView.addObject("currentTime", new DateTime().withTimeAtStartOfDay().toDate());
        modelAndView.addObject("yesterdayTime", DateUtils.addDays(new DateTime().withTimeAtStartOfDay().toDate(), -1));
        Integer referRanking = heroRankingService.findHeroRankingByReferrerLoginName(loginName);
        modelAndView.addObject("investRanking", investRanking);
        modelAndView.addObject("investAmount", investAmount);
        modelAndView.addObject("referRanking", referRanking);
        modelAndView.addObject("mysteriousPrizeDto", heroRankingService.obtainMysteriousPrizeDto(new DateTime().toString("yyyy-MM-dd")));
        return modelAndView;
    }

    @RequestMapping(value = "/new" ,method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView loadNewPageData() {
        String loginName = LoginUserInfo.getLoginName();
        Map param = heroRankingService.obtainHeroRankingAndInvestAmountByLoginName(ActivityCategory.HERO_RANKING, new Date(), loginName);
        Integer investRanking = Integer.parseInt(String.valueOf(param.get("investRanking")));
        String investAmount = param.get("investAmount").toString();
        ModelAndView modelAndView = new ModelAndView("/activities/hero-standings", "responsive", true);
        modelAndView.addObject("currentTime", new DateTime().withTimeAtStartOfDay().toDate());
        modelAndView.addObject("yesterdayTime", DateUtils.addDays(new DateTime().withTimeAtStartOfDay().toDate(), -1));
        Integer referRanking = heroRankingService.findHeroRankingByReferrerLoginName(loginName);
        modelAndView.addObject("investRanking", investRanking);
        modelAndView.addObject("investAmount", investAmount);
        modelAndView.addObject("referRanking", referRanking);
        modelAndView.addObject("mysteriousPrizeDto", heroRankingService.obtainMysteriousPrizeDto(new DateTime().toString("yyyy-MM-dd")));
        List<String> activityTime = heroRankingService.getActivityTime();
        modelAndView.addObject("activityStartTime",activityTime.get(0));
        modelAndView.addObject("activityEndTime",activityTime.get(1));
        return modelAndView;
    }



    @RequestMapping(value = "/referrer-invest/{tradingTime}", method = RequestMethod.GET)
    @ResponseBody
    public BasePaginationDataDto<HeroRankingView> obtainHeroRankingByReferrer(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date tradingTime) {
        return heroRankingService.findHeroRankingByReferrer(tradingTime, LoginUserInfo.getLoginName(), 1, 3);
    }

    @RequestMapping(value = "/invest/{tradingTime}", method = RequestMethod.GET)
    @ResponseBody
    public BasePaginationDataDto<HeroRankingView> obtainHeroRanking(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date tradingTime) {
        final String loginName = LoginUserInfo.getLoginName();
        BasePaginationDataDto<HeroRankingView> baseListDataDto = new BasePaginationDataDto<>();
        List<HeroRankingView> heroRankingViews = heroRankingService.obtainHeroRanking(tradingTime);

        if (heroRankingViews != null) {
            for (HeroRankingView heroRankingView : heroRankingViews) {
                heroRankingView.setLoginName(randomUtils.encryptMobile(loginName, heroRankingView.getLoginName()));
            }

            //TODO:fake
            LoanModel loanModel = loanMapper.findById(41650602422768L);
            if (loanModel != null && loanModel.getStatus() == LoanStatus.REPAYING && new DateTime(tradingTime).withTimeAtStartOfDay().isEqual(new DateTime(2016, 7, 29, 15, 33, 45).withTimeAtStartOfDay())) {
                HeroRankingView element = new HeroRankingView();
                element.setLoginName("186**67");
                element.setSumAmount(loanModel.getLoanAmount());
                heroRankingViews.add(0, element);
            }

            baseListDataDto.setRecords(heroRankingViews.size() > 10 ? heroRankingViews.subList(0, 10) : heroRankingViews);
        }
        baseListDataDto.setStatus(true);
        return baseListDataDto;
    }

}
