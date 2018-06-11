package com.tuotiansudai.activity.controller;

import com.google.common.base.Strings;
import com.tuotiansudai.activity.repository.dto.BaseResponse;
import com.tuotiansudai.activity.service.ThirdAnniversaryActivityService;
import com.tuotiansudai.etcd.ETCDConfigReader;
import com.tuotiansudai.spring.LoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping(value = "/activity/third-anniversary")
public class ThirdAnniversaryActivityController {

    @Autowired
    private ThirdAnniversaryActivityService thirdAnniversaryActivityService;

    private String startTime = ETCDConfigReader.getReader().getValue("activity.third.anniversary.startTime");

    private String endTime = ETCDConfigReader.getReader().getValue("activity.third.anniversary.endTime");

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView thirdAnniversary() {
        ModelAndView modelAndView = new ModelAndView("/activities/2018/july-activity", "responsive", true);
        modelAndView.addAllObjects(thirdAnniversaryActivityService.getTopFourTeam(LoginUserInfo.getLoginName()));
        return modelAndView;
    }

    @RequestMapping(value = "/draw", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse draw() {
        return thirdAnniversaryActivityService.draw(LoginUserInfo.getLoginName());
    }

    @RequestMapping(value = "/prizes", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse prizes() {
        if (Strings.isNullOrEmpty(LoginUserInfo.getLoginName())) {
            return new BaseResponse(false);
        }
        return thirdAnniversaryActivityService.getTeamLogos(LoginUserInfo.getLoginName());
    }

    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse select(@RequestParam(value = "isRed") boolean isRed) {
        return thirdAnniversaryActivityService.selectRedOrBlue(LoginUserInfo.getLoginName(), isRed);
    }

    @RequestMapping(value = "/select-result", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse selectResult() {
        return new BaseResponse<Map<String, Object>>(thirdAnniversaryActivityService.selectResult(LoginUserInfo.getLoginName()));
    }

    @RequestMapping(value = "/invite-page", method = RequestMethod.GET)
    public ModelAndView invite(){
        ModelAndView modelAndView = new ModelAndView();
        if (Strings.isNullOrEmpty(LoginUserInfo.getLoginName())){
            return new ModelAndView("redirect:/we-chat/entry-point?redirect=/activity/third-anniversary/invite");
        }
        modelAndView.addAllObjects(thirdAnniversaryActivityService.invite(LoginUserInfo.getLoginName()));
        modelAndView.addObject("activityStartTime", startTime);
        modelAndView.addObject("activityEndTime", endTime);
        return modelAndView;
    }

    @RequestMapping(value = "/share-invite", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse shareInvite(){
        thirdAnniversaryActivityService.shareInvite(LoginUserInfo.getLoginName());
        return new BaseResponse(true);
    }

    @RequestMapping(value = "/share-page", method = RequestMethod.GET)
    public ModelAndView sharePage(@RequestParam(value = "originator") String originator){
        ModelAndView modelAndView = new ModelAndView();
        Map<String, Object> map = thirdAnniversaryActivityService.sharePage(LoginUserInfo.getLoginName(), originator);
        if(map.isEmpty()){
            modelAndView.setViewName("/error/error-info-page");
            modelAndView.addObject("errorInfo", "无效推荐链接");
            return modelAndView;
        }
        modelAndView.setViewName("/error/error-info-page");
        modelAndView.addAllObjects(map);
        return modelAndView;
    }

    @RequestMapping(value = "/open-red-envelope", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView openRedEnvelope(@RequestParam(value = "originator") String originator, HttpServletRequest request){
        String loginName = LoginUserInfo.getLoginName();
        if (Strings.isNullOrEmpty(loginName)){
            request.getSession().setAttribute("channel", "thirdAnniversary");
            return new ModelAndView(String.format("redirect:/we-chat/entry-point?redirect=/activity/third-anniversary/open-red-envelope?originator=%s", originator));
        }

        if (!thirdAnniversaryActivityService.isActivityRegister(loginName)){
            return new ModelAndView("redirect:/activity/third-anniversary");
        }

        if (!thirdAnniversaryActivityService.isAccount(loginName)){
            return new ModelAndView("redirect:/m/account");
        }
        thirdAnniversaryActivityService.openRedEnvelope(loginName, LoginUserInfo.getMobile(), originator);
        return new ModelAndView(String.format("redirect:/activity/third-anniversary/share-page?originator=%s", originator));
    }

}