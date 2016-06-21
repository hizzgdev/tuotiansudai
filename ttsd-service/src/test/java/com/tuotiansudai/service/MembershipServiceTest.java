package com.tuotiansudai.service;


import com.google.common.collect.Lists;
import com.tuotiansudai.dto.LoanDto;
import com.tuotiansudai.membership.repository.mapper.MembershipMapper;
import com.tuotiansudai.membership.repository.mapper.UserMembershipMapper;
import com.tuotiansudai.membership.repository.model.GivenMembership;
import com.tuotiansudai.membership.repository.model.MembershipModel;
import com.tuotiansudai.membership.repository.model.UserMembershipModel;
import com.tuotiansudai.membership.repository.model.UserMembershipType;
import com.tuotiansudai.repository.mapper.AccountMapper;
import com.tuotiansudai.repository.mapper.InvestMapper;
import com.tuotiansudai.repository.mapper.LoanMapper;
import com.tuotiansudai.repository.mapper.UserMapper;
import com.tuotiansudai.repository.model.*;
import com.tuotiansudai.util.IdGenerator;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@Transactional
public class MembershipServiceTest {

    @Autowired
    private MembershipMapper membershipMapper;

    @Autowired
    private UserMembershipMapper userMembershipMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private LoanMapper loanMapper;

    @Autowired
    private InvestMapper investMapper;

    @Autowired
    private IdGenerator idGenerator;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void shouldReceiveMembershipIsEqualsNoTime() throws ParseException {
        List<String> date = Lists.newArrayList();
        date.add(sdf.format(DateUtils.addDays(new Date(),1)));
        date.add(sdf.format(DateUtils.addMonths(new Date(),1)));
        ReflectionTestUtils.setField(membershipService, "heroRankingActivityPeriod" ,date);
        UserModel fakeUser = getFakeUser("testReceive");
        GivenMembership GivenMembership = membershipService.receiveMembership(fakeUser.getLoginName());
        assertThat(com.tuotiansudai.membership.repository.model.GivenMembership.NO_TIME,is(GivenMembership));
    }

    @Test
    public void shouldReceiveMembershipIsEqualsNoLogin(){
        List<String> date = Lists.newArrayList();
        date.add(sdf.format(new Date()));
        date.add(sdf.format(DateUtils.addMonths(new Date(),1)));
        ReflectionTestUtils.setField(membershipService, "heroRankingActivityPeriod" ,date);
        GivenMembership GivenMembership = membershipService.receiveMembership("");
        assertThat(com.tuotiansudai.membership.repository.model.GivenMembership.NO_LOGIN,is(GivenMembership));
    }

    @Test
    public void shouldReceiveMembershipIsEqualsNoRegister() throws ParseException {
        List<String> date = Lists.newArrayList();
        date.add(sdf.format(new Date()));
        date.add(sdf.format(DateUtils.addMonths(new Date(),1)));
        ReflectionTestUtils.setField(membershipService, "heroRankingActivityPeriod" ,date);
        UserModel fakeUser = getFakeUser("testReceive");
        GivenMembership GivenMembership = membershipService.receiveMembership(fakeUser.getLoginName());
        assertThat(com.tuotiansudai.membership.repository.model.GivenMembership.NO_REGISTER,is(GivenMembership));
    }

    @Test
    public void shouldReceiveMembershipIsEqualsAlreadyReceived(){
        List<String> date = Lists.newArrayList();
        date.add(sdf.format(new Date()));
        date.add(sdf.format(DateUtils.addMonths(new Date(),1)));
        ReflectionTestUtils.setField(membershipService, "heroRankingActivityPeriod" ,date);
        UserModel fakeUser = getFakeUser("testReceive");
        accountMapper.create(new AccountModel(fakeUser.getLoginName(), "username", "11234", "", "", new Date()));
        UserMembershipModel userMembershipModel = new UserMembershipModel(fakeUser.getLoginName(), createMembership(1).getId(), new DateTime().plusDays(130).toDate() , UserMembershipType.GIVEN);
        userMembershipMapper.create(userMembershipModel);
        GivenMembership GivenMembership = membershipService.receiveMembership(fakeUser.getLoginName());
        assertThat(com.tuotiansudai.membership.repository.model.GivenMembership.ALREADY_RECEIVED,is(GivenMembership));
    }

    @Test
    public void shouldReceiveMembershipIsEqualsAlreadyRegisterNotInvest1000(){
        List<String> date = Lists.newArrayList();
        date.add(sdf.format(new Date()));
        date.add(sdf.format(DateUtils.addMonths(new Date(),1)));
        ReflectionTestUtils.setField(membershipService, "heroRankingActivityPeriod" ,date);
        UserModel fakeUser = getFakeUser("testReceive");
        long loanId = idGenerator.generate();
        createLoanByUserId(fakeUser.getLoginName(),loanId);
        InvestModel model = new InvestModel(idGenerator.generate(), loanId, null, 100, fakeUser.getLoginName(), new Date(), Source.WEB, null,0);
        model.setStatus(InvestStatus.SUCCESS);
        investMapper.create(model);
        accountMapper.create(new AccountModel(fakeUser.getLoginName(), "username", "11234", "", "", DateUtils.addDays(new Date(),-1)));
        GivenMembership GivenMembership = membershipService.receiveMembership(fakeUser.getLoginName());
        assertThat(com.tuotiansudai.membership.repository.model.GivenMembership.ALREADY_REGISTER_NOT_INVEST_1000,is(GivenMembership));
    }

    @Test
    public void shouldReceiveMembershipIsEqualsAlreadyRegisterAlreadyInvest1000(){
        List<String> date = Lists.newArrayList();
        date.add(sdf.format(new Date()));
        date.add(sdf.format(DateUtils.addMonths(new Date(),1)));
        ReflectionTestUtils.setField(membershipService, "heroRankingActivityPeriod" ,date);
        UserModel fakeUser = getFakeUser("testReceive");
        long loanId = idGenerator.generate();
        createLoanByUserId(fakeUser.getLoginName(),loanId);
        InvestModel model = new InvestModel(idGenerator.generate(), loanId, null, 1000000, fakeUser.getLoginName(), new Date(), Source.WEB, null,0);
        model.setStatus(InvestStatus.SUCCESS);
        investMapper.create(model);
        accountMapper.create(new AccountModel(fakeUser.getLoginName(), "username", "11234", "", "", DateUtils.addDays(new Date(),-1)));
        GivenMembership GivenMembership = membershipService.receiveMembership(fakeUser.getLoginName());
        UserMembershipModel userMembershipModel = userMembershipMapper.findActiveByLoginName(fakeUser.getLoginName());
        assertThat(com.tuotiansudai.membership.repository.model.GivenMembership.ALREADY_REGISTER_ALREADY_INVEST_1000,is(GivenMembership));
        assertNotNull(userMembershipModel);
    }

    @Test
    public void shouldReceiveMembershipIsEqualsAlreadyStartActivityRegister(){
        List<String> date = Lists.newArrayList();
        date.add(sdf.format(new Date()));
        date.add(sdf.format(DateUtils.addMonths(new Date(),1)));
        ReflectionTestUtils.setField(membershipService, "heroRankingActivityPeriod" ,date);
        UserModel fakeUser = getFakeUser("testReceive");
        long loanId = idGenerator.generate();
        createLoanByUserId(fakeUser.getLoginName(),loanId);
        accountMapper.create(new AccountModel(fakeUser.getLoginName(), "username", "11234", "", "", DateUtils.addDays(new Date(),+1)));
        GivenMembership GivenMembership = membershipService.receiveMembership(fakeUser.getLoginName());
        List<UserMembershipModel> userMembershipModel = userMembershipMapper.findByLoginName(fakeUser.getLoginName());
        assertThat(com.tuotiansudai.membership.repository.model.GivenMembership.AFTER_START_ACTIVITY_REGISTER,is(GivenMembership));
        assertNotNull(userMembershipModel);
    }

    private void createLoanByUserId(String userId, long loanId) {
        LoanDto loanDto = new LoanDto();
        loanDto.setLoanerLoginName(userId);
        loanDto.setLoanerUserName("借款人");
        loanDto.setLoanerIdentityNumber("111111111111111111");
        loanDto.setAgentLoginName(userId);
        loanDto.setBasicRate("16.00");
        loanDto.setId(loanId);
        loanDto.setProjectName("店铺资金周转");
        loanDto.setActivityRate("12");
        loanDto.setShowOnHome(true);
        loanDto.setPeriods(30);
        loanDto.setActivityType(ActivityType.NORMAL);
        loanDto.setContractId(123);
        loanDto.setDescriptionHtml("asdfasdf");
        loanDto.setDescriptionText("asdfasd");
        loanDto.setFundraisingEndTime(new Date());
        loanDto.setFundraisingStartTime(new Date());
        loanDto.setInvestIncreasingAmount("1");
        loanDto.setLoanAmount("10000");
        loanDto.setType(LoanType.INVEST_INTEREST_MONTHLY_REPAY);
        loanDto.setMaxInvestAmount("100000000000");
        loanDto.setMinInvestAmount("0");
        loanDto.setCreatedTime(new Date());
        loanDto.setLoanStatus(LoanStatus.WAITING_VERIFY);
        loanDto.setProductType(ProductType._30);
        LoanModel loanModel = new LoanModel(loanDto);
        loanMapper.create(loanModel);
    }

    private UserModel getFakeUser(String loginName) {
        UserModel fakeUser = new UserModel();
        fakeUser.setLoginName(loginName);
        fakeUser.setPassword("password");
        fakeUser.setEmail("fakeUsr@tuotiansudai.com");
        fakeUser.setMobile("11900000000");
        fakeUser.setRegisterTime(new Date());
        fakeUser.setStatus(UserStatus.ACTIVE);
        fakeUser.setSalt(UUID.randomUUID().toString().replaceAll("-", ""));
        userMapper.create(fakeUser);
        return fakeUser;
    }

    private MembershipModel createMembership(int id){
        MembershipModel membershipModel = membershipMapper.findById(id);
        return membershipModel;
    }
}
