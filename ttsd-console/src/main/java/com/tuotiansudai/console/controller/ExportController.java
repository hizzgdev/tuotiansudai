package com.tuotiansudai.console.controller;

import com.tuotiansudai.console.service.ExportService;
import com.tuotiansudai.coupon.dto.CouponDto;
import com.tuotiansudai.coupon.dto.ExchangeCouponDto;
import com.tuotiansudai.coupon.service.CouponService;
import com.tuotiansudai.dto.AccountItemDataDto;
import com.tuotiansudai.dto.BaseDto;
import com.tuotiansudai.dto.BasePaginationDataDto;
import com.tuotiansudai.dto.TransferApplicationPaginationItemDataDto;
import com.tuotiansudai.point.repository.mapper.UserPointPrizeMapper;
import com.tuotiansudai.point.repository.model.PointPrizeWinnerViewDto;
import com.tuotiansudai.repository.model.RepayStatus;
import com.tuotiansudai.repository.model.SystemBillBusinessType;
import com.tuotiansudai.repository.model.SystemBillOperationType;
import com.tuotiansudai.repository.model.TransferStatus;
import com.tuotiansudai.service.AccountService;
import com.tuotiansudai.service.LoanRepayService;
import com.tuotiansudai.service.SystemBillService;
import com.tuotiansudai.transfer.service.InvestTransferService;
import com.tuotiansudai.util.CsvHeaderType;
import com.tuotiansudai.util.ExportCsvUtil;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/export")
public class ExportController {

    private static Logger logger = Logger.getLogger(ExportController.class);

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserPointPrizeMapper userPointPrizeMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ExportService exportService;
    @Autowired
    private SystemBillService systemBillService;

    @Autowired
    private InvestTransferService investTransferService;

    @Autowired
    private LoanRepayService loanRepayService;

    @RequestMapping(value = "/coupons", method = RequestMethod.GET)
    public void exportCoupons(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(CsvHeaderType.CouponHeader.getDescription() + new DateTime().toString("yyyyMMdd") + ".csv", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        response.setContentType("application/csv");

        int index = 1;
        int pageSize = Integer.MAX_VALUE;
        List<CouponDto> records = couponService.findNewbieAndInvestCoupons(index, pageSize);
        List<List<String>> coupons = exportService.buildCoupons(records);
        ExportCsvUtil.createCsvOutputStream(CsvHeaderType.CouponHeader, coupons, response.getOutputStream());
    }

    @RequestMapping(value = "/interest-coupons", method = RequestMethod.GET)
    public void exportInterestCoupons(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(CsvHeaderType.InterestCouponsHeader.getDescription() + new DateTime().toString("yyyyMMdd") + ".csv", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        response.setContentType("application/csv");
        int index = 1;
        int pageSize = Integer.MAX_VALUE;
        List<CouponDto> records = couponService.findInterestCoupons(index, pageSize);
        List<List<String>> interestCoupons = exportService.buildInterestCoupons(records);
        ExportCsvUtil.createCsvOutputStream(CsvHeaderType.InterestCouponsHeader, interestCoupons, response.getOutputStream());
    }

    @RequestMapping(value = "/red-envelopes", method = RequestMethod.GET)
    public void exportRedEnvelopes(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(CsvHeaderType.RedEnvelopesHeader.getDescription() + new DateTime().toString("yyyyMMdd") + ".csv", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        response.setContentType("application/csv");
        int index = 1;
        int pageSize = Integer.MAX_VALUE;
        List<CouponDto> records = couponService.findRedEnvelopeCoupons(index, pageSize);
        List<List<String>> redEnvelopeCoupons = exportService.buildRedEnvelopeCoupons(records);
        ExportCsvUtil.createCsvOutputStream(CsvHeaderType.RedEnvelopesHeader, redEnvelopeCoupons, response.getOutputStream());
    }

    @RequestMapping(value = "/birthday-coupons", method = RequestMethod.GET)
    public void exportBirthdayCoupons(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(CsvHeaderType.BirthdayCouponsHeader.getDescription() + new DateTime().toString("yyyyMMdd") + ".csv", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        response.setContentType("application/csv");
        int index = 1;
        int pageSize = Integer.MAX_VALUE;
        List<CouponDto> records = couponService.findBirthdayCoupons(index, pageSize);
        List<List<String>> birthdayCoupons = exportService.buildBirthdayCoupons(records);
        ExportCsvUtil.createCsvOutputStream(CsvHeaderType.BirthdayCouponsHeader, birthdayCoupons, response.getOutputStream());
    }

    @RequestMapping(value = "/point-prize", method = RequestMethod.GET)
    public void exportPointPrize(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(CsvHeaderType.PointPrizeHeader.getDescription() + new DateTime().toString("yyyyMMdd") + ".csv", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        response.setContentType("application/csv");
        List<PointPrizeWinnerViewDto> records = userPointPrizeMapper.findAllPointPrizeGroupPrize();
        List<List<String>> pointPrize = exportService.buildPointPrize(records);
        ExportCsvUtil.createCsvOutputStream(CsvHeaderType.PointPrizeHeader, pointPrize, response.getOutputStream());
    }

    @RequestMapping(value = "/user-point", method = RequestMethod.GET)
    public void exportUserPoint(@RequestParam(value = "index", defaultValue = "1", required = false) int index,
                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                @RequestParam(value = "loginName", required = false) String loginName,
                                @RequestParam(value = "userName", required = false) String userName,
                                @RequestParam(value = "mobile", required = false) String mobile, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(CsvHeaderType.UserPointHeader.getDescription() + new DateTime().toString("yyyyMMdd") + ".csv", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        httpServletResponse.setContentType("application/csv");
        List<AccountItemDataDto> accountItemDataDtoList = accountService.findUsersAccountPoint(loginName, userName, mobile, 1, Integer.MAX_VALUE);

        List<List<String>> csvData = exportService.buildOriginListToCsvData(accountItemDataDtoList);
        ExportCsvUtil.createCsvOutputStream(CsvHeaderType.UserPointHeader, csvData, httpServletResponse.getOutputStream());

    }

    @RequestMapping(value = "/coupon-exchange", method = RequestMethod.GET)
    public void exportCouponExchange(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(CsvHeaderType.CouponExchangeHeader.getDescription() + new DateTime().toString("yyyyMMdd") + ".csv", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        httpServletResponse.setContentType("application/csv");
        List<ExchangeCouponDto> exchangeCouponDtos = couponService.findCouponExchanges(1, Integer.MAX_VALUE);

        List<List<String>> csvData = exportService.buildOriginListToCsvData(exchangeCouponDtos);
        ExportCsvUtil.createCsvOutputStream(CsvHeaderType.CouponExchangeHeader, csvData, httpServletResponse.getOutputStream());

    }

    @RequestMapping(value = "/system-bill", method = RequestMethod.GET)
    public void exportSystemBillList(@RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startTime,
                                     @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date endTime,
                                     @RequestParam(value = "operationType", required = false) SystemBillOperationType operationType,
                                     @RequestParam(value = "businessType", required = false) SystemBillBusinessType businessType, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(CsvHeaderType.SystemBillHeader.getDescription() + new DateTime().toString("yyyyMMdd") + ".csv", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        httpServletResponse.setContentType("application/csv");

        BaseDto<BasePaginationDataDto> baseDto = systemBillService.findSystemBillPagination(
                startTime,
                endTime,
                operationType,
                businessType,
                1,
                Integer.MAX_VALUE);

        List<List<String>> csvData = exportService.buildSystemBillCsvData(baseDto.getData().getRecords());
        ExportCsvUtil.createCsvOutputStream(CsvHeaderType.SystemBillHeader, csvData, httpServletResponse.getOutputStream());

    }

    @RequestMapping(value = "/transfer-list", method = RequestMethod.GET)
    public void exportTransferList(@RequestParam(name = "transferApplicationId", required = false) Long transferApplicationId,
                                   @RequestParam(name = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
                                   @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
                                   @RequestParam(name = "status", required = false) TransferStatus status,
                                   @RequestParam(name = "transferrerMobile", required = false) String transferrerMobile,
                                   @RequestParam(name = "transfereeMobile", required = false) String transfereeMobile,
                                   @RequestParam(name = "loanId", required = false) Long loanId, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(CsvHeaderType.TransferListHeader.getDescription() + new DateTime().toString("yyyyMMdd") + ".csv", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        httpServletResponse.setContentType("application/csv");
        BasePaginationDataDto<TransferApplicationPaginationItemDataDto> basePaginationDataDto = investTransferService.findTransferApplicationPaginationList(transferApplicationId, startTime, endTime, status, transferrerMobile, transfereeMobile, loanId, 1, Integer.MAX_VALUE);
        List<List<String>> csvData = exportService.buildOriginListToCsvData(basePaginationDataDto.getRecords());
        ExportCsvUtil.createCsvOutputStream(CsvHeaderType.TransferListHeader, csvData, httpServletResponse.getOutputStream());
    }

    @RequestMapping(value = "/loan-repay", method = RequestMethod.GET)
    public void exportLoanRepay(@RequestParam(value = "index", defaultValue = "1", required = false) int index,
                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                @RequestParam(value = "loanId", required = false) Long loanId,
                                @RequestParam(value = "loginName", required = false) String loginName,
                                @RequestParam(value = "repayStatus", required = false) RepayStatus repayStatus,
                                @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
                                @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(CsvHeaderType.LoanRepayHeader.getDescription() + new DateTime().toString("yyyyMMdd") + ".csv", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        httpServletResponse.setContentType("application/csv");
        BaseDto<BasePaginationDataDto> baseDto = loanRepayService.findLoanRepayPagination(1, Integer.MAX_VALUE,
                loanId, loginName, startTime, endTime, repayStatus);

        List<List<String>> csvData = exportService.buildLoanRepayCsvData(baseDto.getData().getRecords());
        ExportCsvUtil.createCsvOutputStream(CsvHeaderType.LoanRepayHeader, csvData, httpServletResponse.getOutputStream());

    }

}
