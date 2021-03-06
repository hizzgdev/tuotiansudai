package com.tuotiansudai.activity.controller;

import com.google.common.collect.Iterators;
import com.tuotiansudai.activity.repository.model.NewmanTyrantView;
import com.tuotiansudai.activity.service.NewmanTyrantService;
import com.tuotiansudai.dto.BasePaginationDataDto;
import com.tuotiansudai.activity.repository.model.NewmanTyrantHistoryView;
import com.tuotiansudai.spring.LoginUserInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/activity/newman-tyrant")
public class NewmanTyrantController {
    @Autowired
    private NewmanTyrantService newmanTyrantService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView newmanTyrant() {
        ModelAndView modelAndView = new ModelAndView("/activities/newman-tyrant", "responsive", true);
        String loginName = LoginUserInfo.getLoginName();
        List<String> activityTime = newmanTyrantService.getActivityTime();
        List<NewmanTyrantView> newmanViews = newmanTyrantService.obtainNewman(new Date());
        List<NewmanTyrantView> tyrantViews = newmanTyrantService.obtainTyrant(new Date());
        List<NewmanTyrantView> newmanTyrantViews = newmanViews.stream().filter(n -> n.getLoginName().equalsIgnoreCase(loginName)).collect(Collectors.toList()).size() > 0 ? newmanViews : tyrantViews;
        int investRanking = CollectionUtils.isNotEmpty(newmanTyrantViews) ?
                Iterators.indexOf(newmanTyrantViews.iterator(), input -> input.getLoginName().equalsIgnoreCase(loginName)) + 1 : 0;
        long investAmount = investRanking > 0 ? newmanTyrantViews.get(investRanking - 1).getSumAmount() : 0;

        List<NewmanTyrantHistoryView> newmanTyrantHistoryViews = newmanTyrantService
                .obtainNewmanTyrantHistoryRanking(new Date())
                .stream()
                .filter(n -> n.getCurrentDate().compareTo(new DateTime(new Date()).withTimeAtStartOfDay().toDate()) == 0)
                .collect(Collectors.toList());


        modelAndView.addObject("prizeDto", newmanTyrantService.obtainPrizeDto(new DateTime().toString("yyyy-MM-dd")));
        modelAndView.addObject("investRanking", investRanking);
        modelAndView.addObject("investAmount", investAmount);
        modelAndView.addObject("activityStartTime", activityTime.get(0));
        modelAndView.addObject("activityEndTime", activityTime.get(1));
        modelAndView.addObject("currentTime", new DateTime().withTimeAtStartOfDay().toDate());
        modelAndView.addObject("yesterdayTime", DateUtils.addDays(new DateTime().withTimeAtStartOfDay().toDate(), -1));
        modelAndView.addObject("avgNewmanInvestAmount", newmanTyrantHistoryViews.size() > 0 ? newmanTyrantHistoryViews.get(0).getAvgNewmanInvestAmount() : 0);
        modelAndView.addObject("avgTyrantInvestAmount", newmanTyrantHistoryViews.size() > 0 ? newmanTyrantHistoryViews.get(0).getAvgTyrantInvestAmount() : 0);
        return modelAndView;
    }

    @RequestMapping(value = "/newman/{tradingTime}", method = RequestMethod.GET)
    @ResponseBody
    public BasePaginationDataDto<NewmanTyrantView> obtainNewman(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date tradingTime) {
        final String loginName = LoginUserInfo.getLoginName();
        BasePaginationDataDto<NewmanTyrantView> baseListDataDto = new BasePaginationDataDto<>();
        List<NewmanTyrantView> newmanTyrantViews = newmanTyrantService.obtainNewman(tradingTime);

        newmanTyrantViews.stream().forEach(newmanTyrantView -> newmanTyrantView.setLoginName(newmanTyrantService.encryptMobileForWeb(loginName, newmanTyrantView.getLoginName(), newmanTyrantView.getMobile())));
        baseListDataDto.setRecords(newmanTyrantViews);

        baseListDataDto.setStatus(true);
        return baseListDataDto;
    }

    @RequestMapping(value = "/tyrant/{tradingTime}", method = RequestMethod.GET)
    @ResponseBody
    public BasePaginationDataDto<NewmanTyrantView> obtainTyrant(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date tradingTime) {
        final String loginName = LoginUserInfo.getLoginName();
        BasePaginationDataDto<NewmanTyrantView> baseListDataDto = new BasePaginationDataDto<>();
        List<NewmanTyrantView> newmanTyrantViews = newmanTyrantService.obtainTyrant(tradingTime);

        newmanTyrantViews.stream().forEach(newmanTyrantView -> newmanTyrantView.setLoginName(newmanTyrantService.encryptMobileForWeb(loginName, newmanTyrantView.getLoginName(), newmanTyrantView.getMobile())));
        baseListDataDto.setRecords(newmanTyrantViews);

        baseListDataDto.setStatus(true);
        return baseListDataDto;
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @ResponseBody
    public BasePaginationDataDto<NewmanTyrantHistoryView> obtainNewmanTyrantHistory() {
        BasePaginationDataDto<NewmanTyrantHistoryView> baseListDataDto = new BasePaginationDataDto<>();
        List<NewmanTyrantHistoryView> newmanTyrantHistoryViews = newmanTyrantService.obtainNewmanTyrantHistoryRanking(new Date());

        baseListDataDto.setRecords(newmanTyrantHistoryViews);

        baseListDataDto.setStatus(true);
        return baseListDataDto;
    }


}
