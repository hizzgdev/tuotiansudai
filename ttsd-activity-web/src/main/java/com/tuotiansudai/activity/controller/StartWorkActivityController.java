package com.tuotiansudai.activity.controller;

import com.google.common.base.Strings;
import com.tuotiansudai.activity.repository.dto.StartWorkPrizeDto;
import com.tuotiansudai.activity.repository.model.ExchangePrize;
import com.tuotiansudai.activity.service.StartWorkActivityService;
import com.tuotiansudai.etcd.ETCDConfigReader;
import com.tuotiansudai.service.WeChatService;
import com.tuotiansudai.spring.LoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/activity/start-work")
public class StartWorkActivityController {

    @Autowired
    private StartWorkActivityService startWorkActivityService;

    @Autowired
    private WeChatService weChatService;

    private String startTime = ETCDConfigReader.getReader().getValue("activity.start.work.startTime");

    private String endTime = ETCDConfigReader.getReader().getValue("activity.start.work.endTime");

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView newYearActivity() {
        ModelAndView modelAndView = new ModelAndView("/activities/2018/start-work");
        modelAndView.addObject("count", LoginUserInfo.getMobile() == null ? null : startWorkActivityService.getCount(LoginUserInfo.getMobile()));
        modelAndView.addObject("activityStartTime", startTime);
        modelAndView.addObject("activityEndTime", endTime);
        return modelAndView;
    }

    @RequestMapping(path = "/exchange", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> exchange(@RequestParam(value = "exchangePrize") ExchangePrize exchangePrize) {
        return LoginUserInfo.getMobile() == null ? null : startWorkActivityService.exchangePrize(LoginUserInfo.getMobile(), exchangePrize);
    }

    @RequestMapping(path = "/prize", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<StartWorkPrizeDto>> exchangePrizeList() {
        Map<String, List<StartWorkPrizeDto>> map = new HashMap<>();
        map.put("prize", LoginUserInfo.getMobile() == null ? null : startWorkActivityService.getUserPrizeByMobile(LoginUserInfo.getMobile()).stream().map(StartWorkPrizeDto::new).collect(Collectors.toList()));
        return map;
    }

    @RequestMapping(path = "/wechat", method = RequestMethod.GET)
    public ModelAndView startWorkActivityWechat(HttpServletRequest request) {
        String openId = (String) request.getSession().getAttribute("weChatUserOpenid");
        if (Strings.isNullOrEmpty(openId)) {
            return new ModelAndView("redirect:/activity/start-work");
        }
        ModelAndView modelAndView = new ModelAndView("/wechat/start-work");
        modelAndView.addObject("activityStartTime", startTime);
        modelAndView.addObject("activityEndTime", endTime);
        String loginName = LoginUserInfo.getLoginName();
        if (loginName != null) {
            modelAndView.addObject("drewCoupon", startWorkActivityService.drewCoupon(loginName));
        }
        return modelAndView;
    }

    @RequestMapping(path = "/draw", method = RequestMethod.GET)
    public ModelAndView newYearActivityDrawCoupon(HttpServletRequest request) {
        String openId = (String) request.getSession().getAttribute("weChatUserOpenid");
        if (Strings.isNullOrEmpty(openId)) {
            return new ModelAndView("redirect:/activity/start-work");
        }
        boolean duringActivities = startWorkActivityService.duringActivities();
        if (!duringActivities) {
            return new ModelAndView("redirect:/activity/start-work/wechat");
        }

        String loginName = LoginUserInfo.getLoginName();
        if (Strings.isNullOrEmpty(loginName)) {
            return new ModelAndView("redirect:/we-chat/entry-point?redirect=/activity/start-work/draw");
        }
        if (!weChatService.isBound(loginName)) {
            return new ModelAndView("/error/404");
        }

        ModelAndView modelAndView = new ModelAndView("/wechat/start-work");
        boolean drewCoupon = startWorkActivityService.drewCoupon(loginName);
        modelAndView.addObject("activityStartTime", startTime);
        modelAndView.addObject("activityEndTime", endTime);
        if (!drewCoupon) {
            startWorkActivityService.sendDrawCouponMessage(loginName);
            modelAndView.addObject("drawSuccess", true);
        }
        modelAndView.addObject("drewCoupon", true);
        return modelAndView;
    }
}
