package com.tuotiansudai.activity.controller;

import com.tuotiansudai.activity.repository.model.ZeroShoppingPrize;
import com.tuotiansudai.activity.service.ZeroShoppingActivityService;
import com.tuotiansudai.repository.model.LoanModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(value = "/activity/zero-shopping")
public class ZeroShoppingActivityController {

    @Autowired
    private ZeroShoppingActivityService zeroShoppingActivityService;

    @RequestMapping(method = {RequestMethod.GET})
    public ModelAndView zeroShopping(){
        ModelAndView modelAndView = new ModelAndView("/activities/2017/zero-shopping", "responsive", true);
        modelAndView.addObject("prize", zeroShoppingActivityService.getAllPrize());
        return modelAndView;
    }

    @RequestMapping(value = "/activity-loan-exists", method = {RequestMethod.GET})
    public ModelAndView activityLoanExists(){
        List<LoanModel> loanModels = zeroShoppingActivityService.queryActivityLoan();

        ModelAndView modelAndView = new ModelAndView("activities/2017/zero-shopping-detail");

        Boolean exists = false;
        if (!CollectionUtils.isEmpty(loanModels)){
            exists = true;
            modelAndView.addObject("activityLoan", loanModels.get(0).getId());
        }
        modelAndView.addObject("exists", exists);
        return modelAndView;
    }

    @RequestMapping(value = "/activity-loan-detail/{loanId:^\\d+$}", method = {RequestMethod.GET})
    public ModelAndView redirectLoanDetail(@PathVariable long loanId, ZeroShoppingPrize zeroShoppingPrize, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("zeroShoppingPrize", zeroShoppingPrize);
        return new ModelAndView("redirect:/loan/" + loanId);
    }
}
