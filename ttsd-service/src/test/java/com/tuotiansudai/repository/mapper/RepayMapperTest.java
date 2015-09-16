package com.tuotiansudai.repository.mapper;

import com.google.common.collect.Lists;
import com.tuotiansudai.dto.LoanDto;
import com.tuotiansudai.repository.model.*;
import com.tuotiansudai.utils.DateUtil;
import com.tuotiansudai.utils.IdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Administrator on 2015/9/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@Transactional
public class RepayMapperTest {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private LoanRepayMapper loanRepayMapper;

    @Autowired
    private InvestRepayMapper investRepayMapper;

    @Autowired
    private InvestMapper investMapper;

    @Autowired
    private LoanMapper loanMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void shouldCreateLoanRepayModel() throws Exception {
        UserModel userModel = this.getUserModelTest();
        userMapper.create(userModel);
        LoanDto loanDto = this.getLoanModel();
        LoanModel loanModel = new LoanModel(loanDto);
        loanMapper.create(loanModel);
        List<LoanRepayModel> loanRepayModels = Lists.newArrayList();
        LoanRepayModel loanRepayModel = new LoanRepayModel();
        loanRepayModel.setId(idGenerator.generate());
        loanRepayModel.setDefaultInterest(0);
        loanRepayModel.setActualInterest(0);
        loanRepayModel.setPeriod(1);
        loanRepayModel.setStatus(RepayStatus.REPAYING);
        loanRepayModel.setLoanId(loanModel.getId());
        loanRepayModel.setRepayDate(new Date());
        loanRepayModel.setCorpus(0);
        loanRepayModel.setExpectInterest(0);
        loanRepayModels.add(loanRepayModel);
        loanRepayMapper.insertLoanRepay(loanRepayModels);
    }

    @Test
    public void shouldLoanRepayPaginationIsOk(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        UserModel userModel = this.getUserModelTest();
        userMapper.create(userModel);
        LoanDto loanDto = this.getLoanModel();
        LoanModel loanModel = new LoanModel(loanDto);
        loanMapper.create(loanModel);
        List<LoanRepayModel> loanRepayModels = Lists.newArrayList();
        LoanRepayModel loanRepayModel = new LoanRepayModel();
        loanRepayModel.setId(idGenerator.generate());
        loanRepayModel.setDefaultInterest(0);
        loanRepayModel.setActualInterest(0);
        loanRepayModel.setPeriod(1);
        loanRepayModel.setStatus(RepayStatus.REPAYING);
        loanRepayModel.setLoanId(loanModel.getId());
        loanRepayModel.setRepayDate(new Date());
        loanRepayModel.setCorpus(0);
        loanRepayModel.setExpectInterest(0);
        loanRepayModels.add(loanRepayModel);
        LoanRepayModel loanRepayModel1 = new LoanRepayModel();
        loanRepayModel1.setId(idGenerator.generate());
        loanRepayModel1.setDefaultInterest(0);
        loanRepayModel1.setActualInterest(0);
        loanRepayModel1.setPeriod(1);
        loanRepayModel1.setStatus(RepayStatus.REPAYING);
        loanRepayModel1.setLoanId(loanModel.getId());
        loanRepayModel1.setRepayDate(new Date());
        loanRepayModel1.setCorpus(0);
        loanRepayModel1.setExpectInterest(0);
        loanRepayModels.add(loanRepayModel1);
        loanRepayMapper.insertLoanRepay(loanRepayModels);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.add(Calendar.DATE,-1);
        String startDate = sdf.format(cal.getTime());
        cal.add(Calendar.DATE,2);
        String endDate = sdf.format(cal.getTime());

        List<LoanRepayModel> models = loanRepayMapper.findLoanRepayPagination(0, 1, loanModel.getId(), "", null, startDate, endDate);
        assertNotNull(models);
        assertNotNull(models.get(0).getLoan().getLoanerLoginName());
    }

    private LoanDto getLoanModel(){
        LoanDto loanDto = new LoanDto();
        loanDto.setLoanerLoginName("helloworld");
        loanDto.setAgentLoginName("helloworld");
        loanDto.setBasicRate("16.00");
        long id = idGenerator.generate();
        loanDto.setId(id);
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
        loanDto.setInvestFeeRate("15");
        loanDto.setInvestIncreasingAmount("1");
        loanDto.setLoanAmount("10000");
        loanDto.setType(LoanType.LOAN_TYPE_1);
        loanDto.setMaxInvestAmount("100000000000");
        loanDto.setMinInvestAmount("0");
        loanDto.setCreatedTime(new Date());
        loanDto.setLoanStatus(LoanStatus.WAITING_VERIFY);
        List<LoanTitleRelationModel> loanTitleRelationModelList = new ArrayList<LoanTitleRelationModel>();
        loanDto.setLoanTitles(loanTitleRelationModelList);
        return loanDto;
    }

    private UserModel getUserModelTest() {
        UserModel userModelTest = new UserModel();
        userModelTest.setLoginName("helloworld");
        userModelTest.setPassword("123abc");
        userModelTest.setEmail("12345@abc.com");
        userModelTest.setMobile("13900000000");
        userModelTest.setRegisterTime(new Date());
        userModelTest.setStatus(UserStatus.ACTIVE);
        userModelTest.setSalt(UUID.randomUUID().toString().replaceAll("-", ""));
        return userModelTest;
    }

}
