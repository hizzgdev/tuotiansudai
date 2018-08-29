package com.tuotiansudai.service.impl;

import com.google.common.base.*;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.tuotiansudai.client.BankWrapperClient;
import com.tuotiansudai.coupon.service.CouponService;
import com.tuotiansudai.coupon.service.UserCouponService;
import com.tuotiansudai.dto.*;
import com.tuotiansudai.enums.CouponType;
import com.tuotiansudai.enums.Role;
import com.tuotiansudai.enums.UserOpType;
import com.tuotiansudai.etcd.ETCDConfigReader;
import com.tuotiansudai.exception.InvestException;
import com.tuotiansudai.exception.InvestExceptionType;
import com.tuotiansudai.fudian.message.BankAsyncMessage;
import com.tuotiansudai.fudian.message.BankReturnCallbackMessage;
import com.tuotiansudai.log.service.UserOpLogService;
import com.tuotiansudai.membership.repository.model.MembershipModel;
import com.tuotiansudai.membership.service.UserMembershipEvaluator;
import com.tuotiansudai.membership.service.UserMembershipService;
import com.tuotiansudai.repository.mapper.*;
import com.tuotiansudai.repository.model.*;
import com.tuotiansudai.rest.client.mapper.UserMapper;
import com.tuotiansudai.service.InvestService;
import com.tuotiansudai.transfer.service.InvestTransferService;
import com.tuotiansudai.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestServiceImpl implements InvestService {

    static Logger logger = Logger.getLogger(InvestServiceImpl.class);

    private final static String INVEST_NO_PASSWORD_REMIND_MAP = "invest_no_password_remind_map";

    private final RedisWrapperClient redisWrapperClient = RedisWrapperClient.getInstance();

    private final BankWrapperClient bankWrapperClient = new BankWrapperClient();

    private final static int COUPON_LOCK_SECONDS = Integer.parseInt(ETCDConfigReader.getReader().getValue("web.coupon.lock.seconds"));

    private final static int NEWBIE_INVEST_LIMIT = Integer.parseInt(ETCDConfigReader.getReader().getValue("web.newbie.invest.limit"));

    private final static double DEFAULT_FEE = Double.parseDouble(ETCDConfigReader.getReader().getValue("pay.interest.fee"));

    @Value("#{'${web.random.investor.list}'.split('\\|')}")
    private List<String> showRandomLoginNameList;

    @Autowired
    private BankAccountMapper bankAccountMapper;

    @Autowired
    private LoanMapper loanMapper;

    @Autowired
    private InvestMapper investMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private InvestRepayMapper investRepayMapper;

    @Autowired
    private InvestExtraRateMapper investExtraRateMapper;

    @Autowired
    private ExtraLoanRateMapper extraLoanRateMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoanDetailsMapper loanDetailsMapper;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private UserOpLogService userOpLogService;

    @Autowired
    private TransferApplicationMapper transferApplicationMapper;

    @Autowired
    private CouponRepayMapper couponRepayMapper;

    @Autowired
    private CouponService couponService;

    @Autowired
    private InvestTransferService investTransferService;

    @Autowired
    private UserMembershipEvaluator userMembershipEvaluator;

    @Autowired
    private UserMembershipService userMembershipService;

    @Override
    public BankAsyncMessage invest(InvestDto investDto) throws InvestException {
        investDto.setNoPassword(false);
        BankAccountModel bankAccountModel = bankAccountMapper.findByLoginNameAndRole(investDto.getLoginName(), Role.INVESTOR);
        LoanModel loanModel = loanMapper.findById(Long.parseLong(investDto.getLoanId()));
        UserModel userModel = userMapper.findByLoginName(investDto.getLoginName());
        InvestModel investModel = this.generateInvest(investDto);
        return bankWrapperClient.invest(investModel.getId(),
                investDto.getSource(),
                investModel.getLoginName(),
                userModel.getMobile(),
                bankAccountModel.getBankUserName(),
                bankAccountModel.getBankAccountNo(),
                investModel.getAmount(),
                loanModel.getLoanTxNo(),
                loanModel.getId(),
                loanModel.getName());
    }

    @Override
    public BankReturnCallbackMessage noPasswordInvest(InvestDto investDto) throws InvestException {
        investDto.setNoPassword(true);
        BankAccountModel bankAccountModel = bankAccountMapper.findByLoginNameAndRole(investDto.getLoginName(), Role.INVESTOR);
        LoanModel loanModel = loanMapper.findById(Long.parseLong(investDto.getLoanId()));
        UserModel userModel = userMapper.findByLoginName(investDto.getLoginName());
        InvestModel investModel = this.generateInvest(investDto);
        return bankWrapperClient.fastInvest(investModel.getId(),
                investDto.getSource(),
                investModel.getLoginName(),
                userModel.getMobile(),
                bankAccountModel.getBankUserName(),
                bankAccountModel.getBankAccountNo(),
                investModel.getAmount(),
                loanModel.getLoanTxNo(),
                loanModel.getId(),
                loanModel.getName());
    }

    private InvestModel generateInvest(InvestDto investDto) throws InvestException {
        this.checkInvestAvailable(investDto);
        double rate = userMembershipService.obtainServiceFee(investDto.getLoginName());

        InvestModel investModel = new InvestModel(IdGenerator.generate(),
                Long.parseLong(investDto.getLoanId()),
                null,
                investDto.getLoginName(), AmountConverter.convertStringToCent(investDto.getAmount()),
                rate,
                investDto.isNoPassword(),
                new Date(),
                investDto.getSource(),
                investDto.getChannel()
        );
        investMapper.create(investModel);

        return investModel;
    }

    private void checkInvestAvailable(InvestDto investDto) throws InvestException {
        BankAccountModel bankAccountModel = bankAccountMapper.findByLoginNameAndRole(investDto.getLoginName(), Role.INVESTOR);

        long loanId = Long.parseLong(investDto.getLoanId());
        LoanModel loan = loanMapper.findById(loanId);

        if (loan == null) {
            throw new InvestException(InvestExceptionType.NOT_ENOUGH_BALANCE);
        }

        if (investDto.getLoginName().equalsIgnoreCase(loan.getAgentLoginName())) {
            throw new InvestException(InvestExceptionType.INVESTOR_IS_LOANER);
        }

        long userInvestMinAmount = loan.getMinInvestAmount();
        long investAmount = AmountConverter.convertStringToCent(investDto.getAmount());
        long userInvestIncreasingAmount = loan.getInvestIncreasingAmount();

        if (bankAccountModel.getBalance() < investAmount) {
            throw new InvestException(InvestExceptionType.NOT_ENOUGH_BALANCE);
        }

        // 尚未开启免密投资
        if (investDto.isNoPassword() && !bankAccountModel.isAutoInvest()) {
            throw new InvestException(InvestExceptionType.PASSWORD_INVEST_OFF);
        }

        // 标的状态不对
        if (LoanStatus.RAISING != loan.getStatus()) {
            throw new InvestException(InvestExceptionType.ILLEGAL_LOAN_STATUS);
        }

        if (ActivityType.NEWBIE == loan.getActivityType() && !canInvestNewbieLoan(investDto.getLoginName())) {
            throw new InvestException(InvestExceptionType.OUT_OF_NOVICE_INVEST_LIMIT);
        }

        // 不满足最小投资限制
        if (investAmount < userInvestMinAmount) {
            throw new InvestException(InvestExceptionType.LESS_THAN_MIN_INVEST_AMOUNT);
        }

        // 不满足递增规则
        if ((investAmount - userInvestMinAmount) % userInvestIncreasingAmount > 0) {
            throw new InvestException(InvestExceptionType.ILLEGAL_INVEST_AMOUNT);
        }

        long userInvestMaxAmount = loan.getMaxInvestAmount();
        long successInvestAmount = investMapper.sumSuccessInvestAmount(loanId);
        long loanNeedAmount = loan.getLoanAmount() - successInvestAmount;

        // 标已满
        if (loanNeedAmount <= 0) {
            throw new InvestException(InvestExceptionType.LOAN_IS_FULL);
        }

        // 超投
        if (loanNeedAmount < investAmount) {
            throw new InvestException(InvestExceptionType.EXCEED_MONEY_NEED_RAISED);
        }

        long userInvestAmount = investMapper.sumSuccessInvestAmountByLoginName(loanId, investDto.getLoginName(), true);

        // 不满足单用户投资限额
        if (investAmount > userInvestMaxAmount - userInvestAmount) {
            throw new InvestException(InvestExceptionType.MORE_THAN_MAX_INVEST_AMOUNT);
        }

        LoanDetailsModel loanDetailsModel = loanDetailsMapper.getByLoanId(loanId);
        if (CollectionUtils.isNotEmpty(investDto.getUserCouponIds()) && loanDetailsModel.getDisableCoupon()) {
            throw new InvestException(InvestExceptionType.NOT_USE_COUPON);
        }
    }

    private boolean canInvestNewbieLoan(String loginName) {
        int newbieInvestCount = investMapper.countSuccessNewbieInvestByLoginName(loginName);
        return showRandomLoginNameList.contains(loginName.toLowerCase()) || NEWBIE_INVEST_LIMIT == 0 || newbieInvestCount < NEWBIE_INVEST_LIMIT;
    }

    @Override
    public long estimateInvestIncome(long loanId, double investFeeRate, String loginName, long amount, Date investTime) {
        LoanModel loanModel = loanMapper.findById(loanId);

        //根据loginName查询出会员的相关信息
        List<Long> expectedInterestList = InterestCalculator.estimateExpectedInterest(loanModel, amount, investTime);

        long expectedInterest = 0L;
        for (Long expectedInterestOfPerPeriod : expectedInterestList) {
            long expectedOfPerPeriodFee = new BigDecimal(expectedInterestOfPerPeriod).multiply(new BigDecimal(investFeeRate)).setScale(0, BigDecimal.ROUND_DOWN).longValue();
            expectedInterest += (expectedInterestOfPerPeriod - expectedOfPerPeriodFee);
        }

        LoanDetailsModel loanDetailsModel = loanDetailsMapper.getByLoanId(loanId);

        long extraRateInterest = 0;
        long extraRateFee = 0;
        if (loanDetailsModel != null && !CollectionUtils.isEmpty(loanDetailsModel.getExtraSource()) && loanDetailsModel.getExtraSource().contains(Source.WEB)) {
            //根据不同的标的状态显示不同的periodDuration
            int periodDuration = LoanPeriodCalculator.calculateDuration(investTime, loanModel.getDeadline());
            extraRateInterest = getExtraRate(loanId, amount, periodDuration);
            extraRateFee = new BigDecimal(extraRateInterest).multiply(new BigDecimal(investFeeRate)).setScale(0, BigDecimal.ROUND_DOWN).longValue();
        }

        return loanModel.getProductType() == ProductType.EXPERIENCE ? expectedInterest : expectedInterest + (extraRateInterest - extraRateFee);
    }

    @Override
    public BasePaginationDataDto<InvestorInvestPaginationItemDataDto> getInvestPagination(String loginName, int index, int pageSize, Date startTime, Date endTime, LoanStatus loanStatus) {
        startTime = new DateTime(startTime == null ? 0L : startTime).withTimeAtStartOfDay().toDate();
        if (endTime == null) {
            endTime = CalculateUtil.calculateMaxDate();
        } else {
            endTime = new DateTime(endTime).withTimeAtStartOfDay().plusDays(1).minusMillis(1).toDate();
        }

        long count = investMapper.countInvestorInvestPagination(loginName, loanStatus, startTime, endTime);
        int totalPages = PaginationUtil.calculateMaxPage(count, pageSize);
        index = index > totalPages ? totalPages : index;

        List<InvestModel> investModels = investMapper.findInvestorInvestPagination(loginName, loanStatus, (index - 1) * pageSize, pageSize, startTime, endTime);
        List<InvestorInvestPaginationItemDataDto> items = Lists.newArrayList();

        for (InvestModel investModel : investModels) {
            List<InvestRepayModel> investRepayModels = investRepayMapper.findByInvestIdAndPeriodAsc(investModel.getId());
            Optional<InvestRepayModel> nextInvestRepayOptional = Iterators.tryFind(investRepayModels.iterator(), new Predicate<InvestRepayModel>() {
                @Override
                public boolean apply(InvestRepayModel input) {
                    return Lists.newArrayList(RepayStatus.REPAYING, RepayStatus.OVERDUE).contains(input.getStatus());
                }
            });

            List<UserCouponDto> userCouponDtoList = Lists.newArrayList();
            if (investModel.getStatus() == InvestStatus.SUCCESS) {
                List<UserCouponModel> userCouponModels = userCouponMapper.findByInvestId(investModel.getId());
                for (UserCouponModel userCouponModel : userCouponModels) {
                    userCouponDtoList.add(new UserCouponDto(couponMapper.findById(userCouponModel.getCouponId()), userCouponModel, 0));
                }
            }

            LoanModel loanModel = loanMapper.findById(investModel.getLoanId());
            if (loanModel.getProductType().equals(ProductType.EXPERIENCE)) {
                List<UserCouponModel> userCouponModelList = userCouponMapper.findByInvestId(investModel.getId());
                for (UserCouponModel userCouponModel : userCouponModelList) {
                    if (userCouponModel.getStatus().equals(InvestStatus.SUCCESS)) {
                        investModel.setAmount(couponMapper.findById(userCouponModel.getCouponId()).getAmount());
                        break;
                    }
                }
            }

            InvestExtraRateModel investExtraRateModel = investExtraRateMapper.findByInvestId(investModel.getId());

            items.add(new InvestorInvestPaginationItemDataDto(loanModel, investModel,
                    nextInvestRepayOptional.isPresent() ? nextInvestRepayOptional.get() : null,
                    userCouponDtoList, CollectionUtils.isNotEmpty(investRepayModels), investExtraRateModel));
        }

        BasePaginationDataDto<InvestorInvestPaginationItemDataDto> dto = new BasePaginationDataDto<>(index, pageSize, count, items);
        dto.setStatus(true);

        return dto;
    }

    @Override
    public InvestModel findById(long investId) {
        return investMapper.findById(investId);
    }

    @Override
    @Transactional
    public boolean switchNoPasswordInvest(String loginName, boolean isTurnOn, String ip) {
        BankAccountModel bankAccountModel = bankAccountMapper.findByLoginNameAndRole(loginName, Role.INVESTOR);
        bankAccountModel.setAutoInvest(isTurnOn);
        bankAccountMapper.updateAutoInvest(loginName, isTurnOn);
        // 发送用户行为日志MQ
        userOpLogService.sendUserOpLogMQ(loginName, ip, Source.WEB.name(), "", UserOpType.INVEST_NO_PASSWORD,
                isTurnOn ? "Turn On" : "Turn Off");
        return true;
    }

    @Override
    public void markNoPasswordRemind(String loginName) {
        redisWrapperClient.hsetSeri(INVEST_NO_PASSWORD_REMIND_MAP, loginName, "1");
    }

    private long getExtraRate(long loanId, long amount, int duration) {
        double rate = 0;
        List<ExtraLoanRateModel> extraLoanRateModelList = extraLoanRateMapper.findByLoanIdOrderByRate(loanId);
        for (ExtraLoanRateModel extraLoanRateModel : extraLoanRateModelList) {
            if (extraLoanRateModel.getMinInvestAmount() <= amount && extraLoanRateModel.getMaxInvestAmount() == 0) {
                rate = extraLoanRateModel.getRate();
                break;
            }
            if (extraLoanRateModel.getMinInvestAmount() <= amount && amount < extraLoanRateModel.getMaxInvestAmount()) {
                rate = extraLoanRateModel.getRate();
                break;
            }
        }

        return rate == 0 ? 0 : new BigDecimal(duration * amount).multiply(new BigDecimal(rate)).divide(new BigDecimal(InterestCalculator.DAYS_OF_YEAR), 0, BigDecimal.ROUND_DOWN).longValue();
    }

    public long calculateMembershipPreference(String loginName, long loanId, List<Long> couponIds, long investAmount, Source source) {
        long preference;
        double investFeeRate = userMembershipService.obtainServiceFee(loginName);
        LoanModel loanModel = loanMapper.findById(loanId);

        List<ExtraLoanRateModel> extraLoanRateModels = extraLoanRateMapper.findByLoanId(loanId);
        LoanDetailsModel loanDetailsModel = loanDetailsMapper.getByLoanId(loanId);
        long extraLoanRateExpectedInterest = 0L;
        int periodDuration = LoanPeriodCalculator.calculateDuration(new Date(), loanModel.getDeadline());
        if (CollectionUtils.isNotEmpty(extraLoanRateModels) && !StringUtils.isEmpty(loanDetailsModel) && loanDetailsModel.getExtraSource().contains(source)) {
            for (ExtraLoanRateModel extraLoanRateModel : extraLoanRateModels) {
                if ((extraLoanRateModel.getMinInvestAmount() <= investAmount && investAmount < extraLoanRateModel.getMaxInvestAmount()) ||
                        (extraLoanRateModel.getMaxInvestAmount() == 0 && extraLoanRateModel.getMinInvestAmount() <= investAmount)) {
                    extraLoanRateExpectedInterest = InterestCalculator.calculateExtraLoanRateExpectedInterest(extraLoanRateModel.getRate(), investAmount, periodDuration, investFeeRate);
                }
            }
        }

        long expectedInterest = 0;
        //红包和投资体验券不计算在内
        for (Long couponId : couponIds) {
            CouponModel couponModel = couponMapper.findById(couponId);
            if (couponModel != null) {
                int couponPeriodDuration = couponModel.getPeriod() == null ? periodDuration : couponModel.getPeriod() * InterestCalculator.DAYS_OF_MONTH;
                expectedInterest = (couponModel.getCouponType() == CouponType.INTEREST_COUPON || couponModel.getCouponType() == CouponType.BIRTHDAY_COUPON) ?
                        InterestCalculator.getCouponExpectedInterest(loanModel, couponModel, investAmount, couponPeriodDuration) : 0;
            }
        }

        long interest = 0L;
        List<Long> perPeriodInterestList = InterestCalculator.estimateExpectedInterest(loanModel, investAmount, new Date());
        for (Long perPeriodInterest : perPeriodInterestList) {
            interest += perPeriodInterest;
        }

        long originFee = new BigDecimal(interest).multiply(new BigDecimal(DEFAULT_FEE)).longValue();
        long membershipFee = new BigDecimal(interest).multiply(new BigDecimal(investFeeRate)).longValue();
        long originCouponFee = new BigDecimal(expectedInterest).multiply(new BigDecimal(DEFAULT_FEE)).longValue();
        long membershipCouponFee = new BigDecimal(expectedInterest).multiply(new BigDecimal(investFeeRate)).longValue();
        long originExtraLoanRateExpectedInterest = new BigDecimal(extraLoanRateExpectedInterest).multiply(new BigDecimal(DEFAULT_FEE)).longValue();
        long membershipExtraLoanRateExpectedInterest = new BigDecimal(extraLoanRateExpectedInterest).multiply(new BigDecimal(investFeeRate)).longValue();
        preference = originFee - membershipFee + originCouponFee - membershipCouponFee + originExtraLoanRateExpectedInterest - membershipExtraLoanRateExpectedInterest;
        return preference;
    }

    @Override
    public List<InvestModel> findContractFailInvest(long loanId) {
        return investMapper.findNoContractNoInvest(loanId);
    }

    @Override
    public BasePaginationDataDto<UserInvestRecordDataDto> generateUserInvestList(String loginName, int index, int pageSize, LoanStatus loanStatus) {
        long count = investMapper.countInvestorInvestAndTransferPagination(loginName, loanStatus);

        BasePaginationDataDto<UserInvestRecordDataDto> dto = new BasePaginationDataDto<>(index,
                pageSize,
                count,
                convertResponseData(loanStatus,
                        investMapper.findInvestorInvestAndTransferPagination(loginName,
                                loanStatus,
                                (index - 1) * pageSize,
                                pageSize)));
        dto.setStatus(true);
        return dto;
    }

    @Override
    public InvestorInvestDetailDto getInvestDetailById(long investId) {
        InvestModel investModel = investMapper.findById(investId);
        if (investModel == null) {
            return null;
        }
        if (investModel.getTransferInvestId() != null) {
            return userTransferInvestDetail(investModel);
        }
        return userInvestDetail(investModel);
    }

    @Override
    public boolean isUserContractNo(String loginName, String contractNo) {
        List<InvestModel> investorModels = investMapper.findPaginationByLoginName(loginName, 0, Integer.MAX_VALUE);
        List<InvestModel> transferModels = investMapper.findTransfeeInvestByTransfer(loginName);
        List<InvestModel> models = new ArrayList<>();
        models.addAll(investorModels);
        models.addAll(transferModels);
        return models.stream().filter(model -> model.getContractNo() != null).anyMatch(investModel -> investModel.getContractNo().equals(contractNo));
    }

    private List<UserInvestRecordDataDto> convertResponseData(LoanStatus loanStatus, List<InvestModel> investModels) {
        List<UserInvestRecordDataDto> list = Lists.newArrayList();

        for (InvestModel investModel : investModels) {
            LoanModel loanModel = loanMapper.findById(investModel.getLoanId());
            UserInvestRecordDataDto dto = new UserInvestRecordDataDto(investModel, loanModel);

            TransferApplicationModel transferApplicationModel = null;
            List<TransferApplicationModel> transferApplicationModels = transferApplicationMapper.findByTransferInvestId(investModel.getId(), Lists.newArrayList(TransferStatus.TRANSFERRING));
            if (investModel.getTransferInvestId() != null) {
                // 有转让已承接
                transferApplicationModel = transferApplicationMapper.findByInvestId(investModel.getId());
            } else if (CollectionUtils.isNotEmpty(transferApplicationModels)) {
                // 有转让未承接
                transferApplicationModel = transferApplicationModels.get(0);
            }
            if (transferApplicationModel != null) {
                // 有转让情况
                dto.setLoanName(transferApplicationModel.getName());
                dto.setTransferApplicationId(String.valueOf(transferApplicationModel.getId()));
                dto.setInvestAmount(AmountConverter.convertCentToString(transferApplicationModel.getInvestAmount()));
                dto.setTransferInvest(true);
            }

            long actualInterest = 0;
            long expectedInterest = 0;

            List<InvestRepayModel> investRepayModels = investRepayMapper.findByInvestIdAndPeriodAsc(investModel.getId());
            for (InvestRepayModel investRepayModel : investRepayModels) {
                expectedInterest += investRepayModel.getExpectedInterest() - investRepayModel.getExpectedFee();
                actualInterest += investRepayModel.getActualInterest() - investRepayModel.getActualFee();
                CouponRepayModel couponRepayModel = couponRepayMapper.findCouponRepayByInvestIdAndPeriod(investRepayModel.getInvestId(), investRepayModel.getPeriod());
                if (couponRepayModel != null) {
                    actualInterest += couponRepayModel.getActualInterest() - couponRepayModel.getActualFee();
                    expectedInterest += couponRepayModel.getExpectedInterest() - couponRepayModel.getExpectedFee();
                }
            }

            //阶梯加息的利息计算
            InvestExtraRateModel investExtraRateModel = investExtraRateMapper.findByInvestId(investModel.getId());
            if (investExtraRateModel != null && !investExtraRateModel.isTransfer()) {
                long expectedExtraInterest = investExtraRateModel.getExpectedInterest() - investExtraRateModel.getExpectedFee();
                expectedInterest += expectedExtraInterest;
                actualInterest += investExtraRateModel.getStatus() == RepayStatus.COMPLETE ? investExtraRateModel.getActualInterest() - investExtraRateModel.getActualFee() : 0;
            }

            //债权转让折扣计算
            if (transferApplicationModel != null) {
                long discount = transferApplicationModel.getInvestAmount() - transferApplicationModel.getTransferAmount();
                expectedInterest += discount;
                actualInterest += loanModel.getStatus() == LoanStatus.COMPLETE ? discount : 0;
            }

            dto.setActualInterest(AmountConverter.convertCentToString(actualInterest));
            dto.setExpectedInterest(AmountConverter.convertCentToString(expectedInterest));
            if (Lists.newArrayList(LoanStatus.RAISING, LoanStatus.RECHECK).contains(loanModel.getStatus())) {
                List<Long> couponIds = userCouponMapper.findUserCouponSuccessByInvestId(investModel.getId()).stream().filter(userCouponModel -> couponMapper.findById(userCouponModel.getCouponId()).getCouponType() == CouponType.INTEREST_COUPON).map(UserCouponModel::getCouponId).collect(Collectors.toList());
                long couponExpectedInterest = couponService.estimateCouponExpectedInterest(investModel.getLoginName(), investModel.getInvestFeeRate(), loanModel.getId(), couponIds, investModel.getAmount(), investModel.getCreatedTime());
                long investIncome = estimateInvestIncome(loanModel.getId(), investModel.getInvestFeeRate(), investModel.getLoginName(), investModel.getAmount(), investModel.getCreatedTime());
                dto.setExpectedInterest(AmountConverter.convertCentToString(couponExpectedInterest + investIncome));
            }

            dto.setTransferStatus(loanModel.getIsBankPlatform() ? investTransferService.isTransferable(investModel.getId()) ? TransferStatus.TRANSFERABLE.name() : (investModel.getTransferStatus().equals(TransferStatus.TRANSFERABLE) ? TransferStatus.NONTRANSFERABLE.name() : investModel.getTransferStatus().name()) : TransferStatus.NONTRANSFERABLE.name());

            List<UserCouponModel> userCouponModels = userCouponMapper.findUserCouponSuccessByInvestId(investModel.getId());
            List<CouponType> couponTypes = Lists.newArrayList();
            for (UserCouponModel userCouponModel : userCouponModels) {
                CouponModel couponModel = couponMapper.findById(userCouponModel.getCouponId());
                couponTypes.add(couponModel.getCouponType());
            }

            dto.setLastRepayDate(org.apache.commons.lang3.StringUtils.trimToEmpty(new DateTime(loanModel.getStatus() == LoanStatus.COMPLETE ? investRepayModels.get(investRepayModels.size() - 1).getActualRepayDate() : loanModel.getDeadline()).toString("yyyy-MM-dd")));
            dto.setUsedCoupon(CollectionUtils.isNotEmpty(couponTypes) && !couponTypes.contains(CouponType.RED_ENVELOPE));
            dto.setUsedRedEnvelope(couponTypes.contains(CouponType.RED_ENVELOPE));
            dto.setProductNewType(loanModel.getProductType().name());

            dto.setRepayProgress(generateRepayProgress(loanStatus, loanModel));

            list.add(dto);
        }

        return list;
    }

    private int generateRepayProgress(LoanStatus loanStatus, LoanModel loanModel) {
        if (loanStatus == LoanStatus.COMPLETE) {
            return 100;
        }
        if (Lists.newArrayList(LoanStatus.REPAYING, LoanStatus.OVERDUE).contains(loanModel.getStatus())) {
            int passedDays = Days.daysBetween(new DateTime(loanModel.getRecheckTime()).withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay()).getDays() + 1;
            int totalRepayDays = Days.daysBetween(new DateTime(loanModel.getRecheckTime()).withTimeAtStartOfDay(), new DateTime(loanModel.getDeadline()).withTimeAtStartOfDay()).getDays() + 1;
            int progress = passedDays * 100 / totalRepayDays;
            return progress > 100 ? 100 : (progress == 0 ? 1 : progress);
        }

        return 0;
    }


    private InvestorInvestDetailDto userTransferInvestDetail(InvestModel investModel) {
        TransferApplicationModel transferApplicationModel = transferApplicationMapper.findByInvestId(investModel.getId());
        InvestModel originInvestModel = investMapper.findById(investModel.getTransferInvestId());
        LoanModel loanModel = loanMapper.findById(transferApplicationModel.getLoanId());
        long userInvestAmountTotal = investMapper.sumSuccessInvestAmountByLoginName(null, investModel.getLoginName(), false);
        InvestorInvestDetailDto investorInvestDetailDto = new InvestorInvestDetailDto(loanModel, transferApplicationModel, userInvestAmountTotal);

        long totalExpectedInterest = 0;
        long totalActualInterest = 0;
        long corpus = 0;
        List<InvestRepayModel> investRepayModels = investRepayMapper.findByLoginNameAndInvestId(investModel.getLoginName(), investModel.getId());
        for (InvestRepayModel investRepayModel : investRepayModels) {
            long expectedInterest = investRepayModel.getExpectedInterest() + investRepayModel.getDefaultInterest() - investRepayModel.getExpectedFee();
            totalExpectedInterest += expectedInterest;
            totalActualInterest += investRepayModel.getRepayAmount();
            corpus += investRepayModel.getCorpus();
            CouponRepayModel couponRepayModel = couponRepayMapper.findByUserCouponByInvestIdAndPeriod(investRepayModel.getInvestId(), investRepayModel.getPeriod());
            InvestorInvestRepayDto investRepayDataDto = new InvestorInvestRepayDto(investRepayModel, couponRepayModel);
            investorInvestDetailDto.getInvestRepays().add(investRepayDataDto);
            if (investRepayModel.getPeriod() == loanModel.getPeriods()) {
                investorInvestDetailDto.setLastRepayDate(investRepayModel.getRepayDate());
            }
        }

        investorInvestDetailDto.setExpectedInterest(totalExpectedInterest);
        investorInvestDetailDto.setActualInterest(totalActualInterest);
        investorInvestDetailDto.setUnPaidRepay(totalExpectedInterest + corpus - totalActualInterest);

        MembershipModel membershipModel = userMembershipEvaluator.evaluateSpecifiedDate(investModel.getLoginName(), transferApplicationModel.getTransferTime());
        investorInvestDetailDto.setMembershipLevel(membershipModel.getLevel());
        investorInvestDetailDto.setServiceFeeDesc(getDescriptionOfRate(originInvestModel.getInvestFeeRate()));
        investorInvestDetailDto.setContractUrl(generateContractUrl(originInvestModel));
        return investorInvestDetailDto;
    }


    private InvestorInvestDetailDto userInvestDetail(InvestModel investModel) {
        long totalExpectedInterest = 0;
        long completeTotalActualInterest = 0;
        long unPaidTotalRepay = 0;
        LoanModel loanModel = loanMapper.findById(investModel.getLoanId());
        //未放款时按照预计利息计算(拓天体验项目没有本金，所以不需要计算)
        if (loanModel.getRecheckTime() == null && loanModel.getProductType() != ProductType.EXPERIENCE) {
            List<Long> couponIds = userCouponMapper.findUserCouponSuccessByInvestId(investModel.getId()).stream().filter(userCouponModel -> couponMapper.findById(userCouponModel.getCouponId()).getCouponType() == CouponType.INTEREST_COUPON).map(UserCouponModel::getCouponId).collect(Collectors.toList());
            long estimateInvestIncome = estimateInvestIncome(loanModel.getId(), investModel.getInvestFeeRate(), investModel.getLoginName(), investModel.getAmount(), investModel.getCreatedTime());
            long couponExpectedInterest = couponService.estimateCouponExpectedInterest(investModel.getLoginName(), investModel.getInvestFeeRate(), loanModel.getId(), couponIds, investModel.getAmount(), investModel.getCreatedTime());
            totalExpectedInterest = estimateInvestIncome + couponExpectedInterest;
        }
        long userInvestAmountTotal = investMapper.sumSuccessInvestAmountByLoginName(null, investModel.getLoginName(), false);
        InvestorInvestDetailDto investorInvestDetailDto = new InvestorInvestDetailDto(loanModel, investModel, userInvestAmountTotal);
        if (investModel.getTransferInvestId() != null) {
            TransferApplicationModel transferApplicationModel = transferApplicationMapper.findByInvestId(investModel.getId());
            investorInvestDetailDto.setLoanName(transferApplicationModel != null ? transferApplicationModel.getName() : loanModel.getName());
            investorInvestDetailDto.setInvestTime(transferApplicationModel != null ? transferApplicationModel.getTransferTime() : investorInvestDetailDto.getInvestTime());
        }
        List<InvestRepayModel> investRepayModels = investRepayMapper.findByInvestIdAndPeriodAsc(investModel.getId());
        List<InvestorInvestRepayDto> investRepayList = new ArrayList<>();

        investorInvestDetailDto.setInterestBeginDate(getInterestBeginDate(investModel, loanModel, investRepayModels));
        if (loanModel.getProductType() == ProductType.EXPERIENCE) {
            investorInvestDetailDto.setLastRepayDate(investRepayModels.get(investRepayModels.size() - 1).getRepayDate());
        } else if (loanModel.getStatus() == LoanStatus.COMPLETE) {
            investorInvestDetailDto.setLastRepayDate(investRepayModels.get(investRepayModels.size() - 1).getActualRepayDate());
        } else {
            investorInvestDetailDto.setLastRepayDate(loanModel.getDeadline());
        }
        List<TransferApplicationModel> transferApplicationModels;
        for (InvestRepayModel investRepayModel : investRepayModels) {
            if (investRepayModel.isTransferred()) {
                transferApplicationModels = transferApplicationMapper.findByTransferInvestId(investModel.getId(), Lists.newArrayList(TransferStatus.SUCCESS));
                if (CollectionUtils.isNotEmpty(transferApplicationModels) && transferApplicationModels.get(0).getPeriod() != investRepayModel.getPeriod()) {
                    continue;
                }
            }
            CouponRepayModel couponRepayModel = couponRepayMapper.findCouponRepayByInvestIdAndPeriod(investRepayModel.getInvestId(), investRepayModel.getPeriod());
            long expectedInterest = investRepayModel.getExpectedInterest() + investRepayModel.getDefaultInterest() - investRepayModel.getExpectedFee();
            long actualInterest = investRepayModel.getRepayAmount();
            if (couponRepayModel != null) {
                expectedInterest += couponRepayModel.getExpectedInterest() - couponRepayModel.getExpectedFee();
                actualInterest += couponRepayModel.getRepayAmount();
            }
            int periods = loanMapper.findById(investModel.getLoanId()).getPeriods();
            if (periods == investRepayModel.getPeriod()) {
                InvestExtraRateModel investExtraRateModel = investExtraRateMapper.findByInvestId(investRepayModel.getInvestId());
                if (investExtraRateModel != null && !investExtraRateModel.isTransfer()) {
                    expectedInterest += investExtraRateModel.getExpectedInterest() - investExtraRateModel.getExpectedFee();
                    actualInterest += investExtraRateModel.getRepayAmount();
                }
            }

            InvestorInvestRepayDto investRepayDataDto = new InvestorInvestRepayDto(investRepayModel, couponRepayModel);
            investRepayList.add(investRepayDataDto);
            if (investRepayModel.getStatus() == RepayStatus.COMPLETE) {
                completeTotalActualInterest += actualInterest;
            } else {
                unPaidTotalRepay += expectedInterest + investRepayModel.getCorpus();
            }
            totalExpectedInterest += expectedInterest;
        }

        investorInvestDetailDto.setExpectedInterest(totalExpectedInterest);
        investorInvestDetailDto.setActualInterest(completeTotalActualInterest);
        investorInvestDetailDto.setUnPaidRepay(unPaidTotalRepay);

        investorInvestDetailDto.setInvestRepays(investRepayList);
        MembershipModel membershipModel = userMembershipEvaluator.evaluateSpecifiedDate(investModel.getLoginName(), investModel.getInvestTime());
        investorInvestDetailDto.setMembershipLevel(membershipModel.getLevel());
        investorInvestDetailDto.setServiceFeeDesc(getDescriptionOfRate(investModel.getInvestFeeRate()));
        List<UserCouponModel> userCouponModels = userCouponMapper.findByInvestId(investModel.getId());

        List<String> usedCoupons = userCouponModels.stream().map(input -> generateUsedCouponName(couponMapper.findById(input.getCouponId()))).collect(Collectors.toList());
        investorInvestDetailDto.setUsedCoupons(usedCoupons);
        investorInvestDetailDto.setContractUrl(generateContractUrl(investModel));
        return investorInvestDetailDto;
    }

    private String generateContractUrl(InvestModel investModel) {
        if (Strings.isNullOrEmpty(investModel.getContractNo())) {
            return null;
        }

        if (investModel.getContractNo().equals("OLD")) {
            if (investModel.getTransferInvestId() != null) {
                long transferApplicationId = transferApplicationMapper.findByInvestId(investModel.getId()).getId();
                return String.format("/contract/transfer/transferApplicationId/%d", transferApplicationId);
            } else {
                return String.format("/contract/investor/loanId/%d/investId/%d", investModel.getLoanId(), investModel.getId());
            }
        } else {
            return String.format("/contract/invest/contractNo/%s", investModel.getContractNo());
        }
    }

    private String generateUsedCouponName(CouponModel couponModel) {
        if (couponModel == null) {
            return "";
        }
        if (CouponType.INTEREST_COUPON.equals(couponModel.getCouponType())) {
            return String.format("%s%s加息券", new DecimalFormat("#.##").format(couponModel.getRate() * 100), "%");
        }
        if (CouponType.BIRTHDAY_COUPON.equals(couponModel.getCouponType())) {
            return CouponType.BIRTHDAY_COUPON.getName();
        }
        return String.format("%s元%s", AmountConverter.convertCentToString(couponModel.getAmount()), couponModel.getCouponType().getName());
    }

    private Date getInterestBeginDate(InvestModel investModel, LoanModel loanModel, List<InvestRepayModel> investRepayModels) {
        if (InterestInitiateType.INTEREST_START_AT_INVEST.equals(loanModel.getType().getInterestInitiateType())) {
            return investModel.getInvestTime();
        }
        if (investRepayModels.size() == 0) {
            return null;
        }
        int minPeriod = investRepayModels.get(0).getPeriod();
        if (investModel.getTransferInvestId() != null && minPeriod > 1) {
            return new DateTime(investRepayMapper.findByInvestIdAndPeriod(investModel.getTransferInvestId(), minPeriod - 1).getRepayDate()).plusDays(1).toDate();
        }

        if (Lists.newArrayList(LoanType.LOAN_INTEREST_LUMP_SUM_REPAY, LoanType.LOAN_INTEREST_MONTHLY_REPAY).contains(loanModel.getType())) {
            return loanModel.getRecheckTime();
        }
        if (investModel.getTransferInvestId() != null && minPeriod == 1) {
            investModel = investMapper.findById(investModel.getTransferInvestId());
        }
        return investModel.getInvestTime();
    }

    private String getDescriptionOfRate(double rate) {
        if (rate == 0.07) {
            return "服务费七折";
        }
        if (rate == 0.08) {
            return "服务费八折";
        }
        if (rate == 0.09) {
            return "服务费九折";
        }
        return "暂无优惠";
    }
}
