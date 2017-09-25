package com.tuotiansudai.paywrapper.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.tuotiansudai.dto.BaseDto;
import com.tuotiansudai.dto.HuiZuRepayDto;
import com.tuotiansudai.dto.PayFormDataDto;
import com.tuotiansudai.enums.UserBillBusinessType;
import com.tuotiansudai.exception.AmountTransferException;
import com.tuotiansudai.paywrapper.repository.mapper.HuiZuRepayMapper;
import com.tuotiansudai.paywrapper.repository.mapper.HuiZuRepayNotifyRequestMapper;
import com.tuotiansudai.paywrapper.repository.model.async.callback.BaseCallbackRequestModel;
import com.tuotiansudai.paywrapper.repository.model.async.callback.HuiZuRepayNotifyRequestModel;
import com.tuotiansudai.paywrapper.repository.model.sync.request.SyncRequestStatus;
import com.tuotiansudai.paywrapper.service.impl.HuizuRepayServiceImpl;
import com.tuotiansudai.repository.mapper.AccountMapper;
import com.tuotiansudai.repository.mapper.UserBillMapper;
import com.tuotiansudai.repository.mapper.UserMapper;
import com.tuotiansudai.repository.model.*;
import com.tuotiansudai.util.AmountConverter;
import com.tuotiansudai.util.RedisWrapperClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@Transactional
public class HuiZuRepayServiceTest {
    private static final String loginName = "testHuiZuRepay";

    @Autowired
    private HuiZuRepayService huiZuRepayService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserBillMapper userBillMapper;
    @Autowired
    private HuiZuRepayNotifyRequestMapper huiZuRepayNotifyRequestMapper;

    private final RedisWrapperClient redisWrapperClient = RedisWrapperClient.getInstance();


    @Before
    public void setUp() {
        this.createFakeUser(loginName, 300);
    }

    @After
    public void tearDown() {
        redisWrapperClient.del(String.format("REPAY_PLAN_ID:%s", 111), String.format("REPAY_PLAN_ID:%s", 222));
    }

    @Test
    public void shouldPasswordRepayIsSuccess() {
        HuiZuRepayDto huiZuRepayDto = createFakeHuiZuRepayDto("1.00", "111");
        BaseDto<PayFormDataDto> baseDto = huiZuRepayService.passwordRepay(huiZuRepayDto);
        AccountModel accountModel = accountMapper.findByLoginName(loginName);

        assertEquals(true, redisWrapperClient.exists(String.format("REPAY_PLAN_ID:%s", huiZuRepayDto.getRepayPlanId())));
        assertEquals(huiZuRepayDto.getMobile(), redisWrapperClient.hget(String.format("REPAY_PLAN_ID:%s", huiZuRepayDto.getRepayPlanId()), "mobile"));
        assertEquals(AmountConverter.convertStringToCent(huiZuRepayDto.getAmount()), Long.parseLong(redisWrapperClient.hget(String.format("REPAY_PLAN_ID:%s", huiZuRepayDto.getRepayPlanId()), "amount")));
        assertEquals(String.valueOf(huiZuRepayDto.getPeriod()), redisWrapperClient.hget(String.format("REPAY_PLAN_ID:%s", huiZuRepayDto.getRepayPlanId()), "period"));
        assertEquals(SyncRequestStatus.SENT.name(), redisWrapperClient.hget(String.format("REPAY_PLAN_ID:%s", huiZuRepayDto.getRepayPlanId()), "status"));

        assertEquals(true, baseDto.isSuccess());
        assertEquals(String.valueOf(AmountConverter.convertStringToCent(huiZuRepayDto.getAmount())), baseDto.getData().getFields().get("amount"));
        assertEquals(huiZuRepayDto.getRepayPlanId(), baseDto.getData().getFields().get("order_id").split(HuizuRepayServiceImpl.REPAY_ORDER_ID_SEPARATOR)[0]);
        assertEquals(accountModel.getPayUserId(), baseDto.getData().getFields().get("partic_user_id"));
        redisWrapperClient.del(String.format("REPAY_PLAN_ID:%s", 111), String.format("REPAY_PLAN_ID:%s", 222));

    }

    @Test
    public void shouldHzRepayModifyIsSuccess() throws AmountTransferException {
        HuiZuRepayDto huiZuRepayDto = createFakeHuiZuRepayDto("1.10", "222");
        redisWrapperClient.hmset(String.format("REPAY_PLAN_ID:%s", String.valueOf(huiZuRepayDto.getRepayPlanId())),
                Maps.newHashMap(ImmutableMap.builder()
                        .put("mobile", huiZuRepayDto.getMobile())
                        .put("amount", String.valueOf(AmountConverter.convertStringToCent(huiZuRepayDto.getAmount())))
                        .put("period", String.valueOf(huiZuRepayDto.getPeriod()))
                        .put("status", SyncRequestStatus.SENT.name())
                        .build()),
                30 * 30);
        huiZuRepayService.postRepay(String.valueOf(huiZuRepayDto.getRepayPlanId()));
        AccountModel accountModel = accountMapper.findByLoginName(huiZuRepayDto.getMobile());
        List<UserBillModel> userBillModels = userBillMapper.findByLoginName(huiZuRepayDto.getMobile());

        assertEquals(190, accountModel.getBalance());
        assertEquals(110, userBillModels.get(0).getAmount());
        assertEquals(UserBillBusinessType.HUI_ZU_REPAY_IN, userBillModels.get(0).getBusinessType());
        assertEquals(huiZuRepayDto.getRepayPlanId(), String.valueOf(userBillModels.get(0).getOrderId()));
        assertEquals(UserBillOperationType.TO_BALANCE, userBillModels.get(0).getOperationType());
        redisWrapperClient.del(String.format("REPAY_PLAN_ID:%s", 111), String.format("REPAY_PLAN_ID:%s", 222));

    }

    @Test
    public void shouldPasswordRepayReturnBalanceInsufficient() {
        HuiZuRepayDto huiZuRepayDto = createFakeHuiZuRepayDto("3.10", "001");
        BaseDto<PayFormDataDto> baseDto = huiZuRepayService.passwordRepay(huiZuRepayDto);
        assertEquals("余额不足，请充值", baseDto.getData().getMessage());
        redisWrapperClient.del(String.format("REPAY_PLAN_ID:%s", 111), String.format("REPAY_PLAN_ID:%s", 222));

    }

    @Test
    public void shouldPasswordRepayReturnRepayIdPaySuccess() {
        HuiZuRepayDto huiZuRepayDto = createFakeHuiZuRepayDto("1.10", "111");
        redisWrapperClient.hmset(String.format("REPAY_PLAN_ID:%s", String.valueOf(huiZuRepayDto.getRepayPlanId())),
                Maps.newHashMap(ImmutableMap.builder()
                        .put("loginName", huiZuRepayDto.getMobile())
                        .put("amount", String.valueOf(huiZuRepayDto.getAmount()))
                        .put("period", String.valueOf(huiZuRepayDto.getPeriod()))
                        .put("status", SyncRequestStatus.SUCCESS.name())
                        .build()),
                30 * 30);
        BaseDto<PayFormDataDto> baseDto = huiZuRepayService.passwordRepay(huiZuRepayDto);

        assertEquals(String.format("用户:%s-第%s期-已经还款成功",
                huiZuRepayDto.getMobile(),
                String.valueOf(huiZuRepayDto.getPeriod())), baseDto.getData().getMessage());
        redisWrapperClient.del(String.format("REPAY_PLAN_ID:%s", 111), String.format("REPAY_PLAN_ID:%s", 222));
    }

    private HuiZuRepayDto createFakeHuiZuRepayDto(String amount, String repayPlanId) {
        HuiZuRepayDto huiZuRepayDto = new HuiZuRepayDto();
        huiZuRepayDto.setMobile(loginName);
        huiZuRepayDto.setPeriod(2);
        huiZuRepayDto.setAmount(amount);
        huiZuRepayDto.setRepayPlanId(repayPlanId);
        return huiZuRepayDto;
    }

    private void createFakeUser(String loginName, long balance) {
        UserModel fakeUserModel = new UserModel();
        fakeUserModel.setLoginName(loginName);
        fakeUserModel.setPassword("password");
        fakeUserModel.setMobile(loginName);
        fakeUserModel.setRegisterTime(new Date());
        fakeUserModel.setStatus(UserStatus.ACTIVE);
        fakeUserModel.setSalt(UUID.randomUUID().toString().replaceAll("-", ""));
        userMapper.create(fakeUserModel);
        AccountModel accountModel = new AccountModel(loginName, "payUserId", "payAccountId", new Date());
        accountModel.setBalance(balance);
        accountModel.setFreeze(0l);
        accountMapper.create(accountModel);
    }
}
