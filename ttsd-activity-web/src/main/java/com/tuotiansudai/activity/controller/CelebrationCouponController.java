package com.tuotiansudai.activity.controller;

import com.google.common.base.Strings;
import com.tuotiansudai.activity.service.CelebrationCouponService;
import com.tuotiansudai.service.WeChatService;
import com.tuotiansudai.spring.LoginUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(path = "/activity/celebration-coupon")
public class CelebrationCouponController {

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private CelebrationCouponService celebrationCouponService;

    @RequestMapping(path = "/wechat", method = RequestMethod.GET)
    public ModelAndView celebrationCouponWechatHome(HttpServletRequest request) {
        String openId = (String) request.getSession().getAttribute("weChatUserOpenid");
        if (Strings.isNullOrEmpty(openId)) {
            return new ModelAndView("redirect:/activity/celebration-coupon");
        }
        boolean duringActivities = celebrationCouponService.duringActivities();
        ModelAndView modelAndView = new ModelAndView("/wechat/coupon-special-receive");

        modelAndView.addObject("duringActivities", duringActivities);

        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView celebrationCouponHome() {

        return new ModelAndView("/activities/2017/coupon-special");
    }


    @RequestMapping(path = "/draw", method = RequestMethod.GET)
    public ModelAndView celebrationDrawCouponHome(HttpServletRequest request) {
        String openId = (String) request.getSession().getAttribute("weChatUserOpenid");
        if (Strings.isNullOrEmpty(openId)) {
            return new ModelAndView("redirect:/activity/celebration-coupon");
        }
        boolean duringActivities = celebrationCouponService.duringActivities();
        if (!duringActivities) {
            return new ModelAndView("redirect:/activity/celebration-coupon/wechat");
        }
        ModelAndView mv = new ModelAndView();
        String loginName = LoginUserInfo.getLoginName();
        if (Strings.isNullOrEmpty(loginName)) {
            return new ModelAndView("redirect:/we-chat/entry-point?redirect=/activity/celebration-coupon/draw");
        }

        if (!weChatService.isBound(loginName)) {
            return new ModelAndView("/error/404");
        }
        boolean drewCoupon = celebrationCouponService.drewCoupon(loginName);
        if (drewCoupon) {
            mv.addObject("duringActivities", duringActivities);
            mv.addObject("drewCoupon", drewCoupon);
            mv.setViewName("/wechat/coupon-special-receive");
        } else {
            celebrationCouponService.sendDrawCouponMessage(loginName);
            mv.setViewName("/wechat/coupon-special-success");
        }

        return mv;
    }


}
