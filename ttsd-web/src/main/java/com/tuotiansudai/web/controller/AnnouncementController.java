package com.tuotiansudai.web.controller;


import com.tuotiansudai.dto.AnnouncementManagementDto;
import com.tuotiansudai.dto.BaseDto;
import com.tuotiansudai.dto.BasePaginationDataDto;
import com.tuotiansudai.dto.LoanDto;
import com.tuotiansudai.service.AnnouncementManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping(value = "/announce")
public class AnnouncementController {

    @Autowired
    private AnnouncementManagementService announcementManagementService;

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public BaseDto<BasePaginationDataDto> getAnnounceList(@RequestParam(value = "index", defaultValue = "1", required = false) int index,
                                                          @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return announcementManagementService.getAnnouncementList(index, pageSize);
    }

    @RequestMapping(value = "/{announceId:^\\d+$}", method = RequestMethod.GET)
    public ModelAndView getAnnounceDetail(@PathVariable long announceId) {
        AnnouncementManagementDto dto = announcementManagementService.getDtoById(announceId);
        return new ModelAndView("/about/notice-detail", "announcement", dto);
    }
}
