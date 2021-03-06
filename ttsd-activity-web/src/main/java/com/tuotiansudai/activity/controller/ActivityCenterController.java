package com.tuotiansudai.activity.controller;

import com.tuotiansudai.activity.repository.dto.ActivityDto;
import com.tuotiansudai.activity.service.ActivityService;
import com.tuotiansudai.repository.model.Source;
import com.tuotiansudai.spring.LoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/activity/activity-center")
public class ActivityCenterController {

    @Autowired
    private ActivityService activityService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getAllOperatingActivities() {
        ModelAndView modelAndView = new ModelAndView("/activities/activity-center");
        String loginName = LoginUserInfo.getLoginName();
        List<ActivityDto> activityDtos = activityService.getAllActiveActivities(loginName, Source.WEB);
        modelAndView.addObject("data", activityDtos);
        modelAndView.addObject("responsive", true);
        return modelAndView;
    }
}
