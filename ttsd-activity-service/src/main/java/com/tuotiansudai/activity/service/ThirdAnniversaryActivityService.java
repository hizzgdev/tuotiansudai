package com.tuotiansudai.activity.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.tuotiansudai.activity.repository.dto.BaseResponse;
import com.tuotiansudai.activity.repository.dto.DrawLotteryResultDto;
import com.tuotiansudai.activity.repository.mapper.*;
import com.tuotiansudai.activity.repository.model.*;
import com.tuotiansudai.repository.mapper.AccountMapper;
import com.tuotiansudai.repository.model.UserModel;
import com.tuotiansudai.rest.client.mapper.UserMapper;
import com.tuotiansudai.util.AmountConverter;
import com.tuotiansudai.util.RedisWrapperClient;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThirdAnniversaryActivityService {

    @Value(value = "#{new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").parse(\"${activity.third.anniversary.startTime}\")}")
    private Date activityStartTime;

    @Value(value = "#{new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").parse(\"${activity.third.anniversary.draw.endTime}\")}")
    private Date activityDrawEndTime;

    @Value(value = "#{new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").parse(\"${activity.third.anniversary.endTime}\")}")
    private Date activityEndTime;

    private final Map<Integer, String> FOOTBALL_TEAMS = Maps.newHashMap(ImmutableMap.<Integer, String>builder()
            .put(1, "agenting")
            .put(2, "aiji")
            .put(3, "aodaliya")
            .put(4, "banama")
            .put(5, "baxi")
            .put(6, "bilishi")
            .put(7, "bilu")
            .put(8, "bingdao")
            .put(9, "bolan")
            .put(10, "danmai")
            .put(11, "deguo")
            .put(12, "eluosi")
            .put(13, "faguo")
            .put(14, "gelunbiya")
            .put(15, "gesidani")
            .put(16, "hanguo")
            .put(17, "keluodiya")
            .put(18, "moluoge")
            .put(19, "moxige")
            .put(20, "niriliya")
            .put(21, "putaoya")
            .put(22, "riben")
            .put(23, "ruidian")
            .put(24, "ruishi")
            .put(25, "saierweiya")
            .put(26, "saineijiaer")
            .put(27, "shatealabo")
            .put(28, "tunisi")
            .put(29, "wulagui")
            .put(30, "xibanya")
            .put(31, "yilang")
            .put(32, "yinggelan")
            .build());

    private final Map<Integer, Double> rates = Maps.newHashMap(ImmutableMap.<Integer, Double>builder()
            .put(0, 0D)
            .put(1, 0.001D)
            .put(2, 0.002D)
            .put(3, 0.005D)
            .build());

    private final RedisWrapperClient redisWrapperClient = RedisWrapperClient.getInstance();

    private final String THIRD_ANNIVERSARY_TOP_FOUR_TEAM = "THIRD_ANNIVERSARY_TOP_FOUR_TEAM";

    private final String THIRD_ANNIVERSARY_TOP_FOUR_TEAM_CHINESE = "THIRD_ANNIVERSARY_TOP_FOUR_TEAM_CHINESE";

    private final String THIRD_ANNIVERSARY_EACH_EVERY_DAY_DRAW = "THIRD_ANNIVERSARY_EACH_EVERY_DAY_DRAW:{0}";

    private final String THIRD_ANNIVERSARY_SELECT_RED_OR_BLUE = "THIRD_ANNIVERSARY_SELECT_RED_OR_BLUE";

    private final String THIRD_ANNIVERSARY_WAIT_SEND_REWARD = "THIRD_ANNIVERSARY_WAIT_SEND_REWARD";

    @Autowired
    private ThirdAnniversaryDrawMapper thirdAnniversaryDrawMapper;

    @Autowired
    private ActivityInvestAnnualizedMapper activityInvestAnnualizedMapper;

    @Autowired
    private ActivityInvestMapper activityInvestMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private WeChatHelpMapper weChatHelpMapper;

    @Autowired
    private WeChatHelpInfoMapper weChatHelpInfoMapper;

    @Autowired
    private UserMapper userMapper;

    private final long EACH_INVEST_AMOUNT_50000 = 50000L;

    private final int lifeSecond = 180 * 24 * 60 * 60;

    public Map<String, Object> selectResult(String loginName) {

        List<ActivityInvestAnnualizedView> annualizedViews = activityInvestAnnualizedMapper.findByActivityAndMobile(ActivityInvestAnnualized.THIRD_ANNIVERSARY_ACTIVITY, null);
        Map<String, String> supportMaps = redisWrapperClient.hgetAll(THIRD_ANNIVERSARY_SELECT_RED_OR_BLUE);
        List<String> redSupportLoginName = supportMaps.entrySet().stream().filter(entry -> entry.getValue().equals("RED")).map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> blueSupportLoginName = supportMaps.entrySet().stream().filter(entry -> entry.getValue().equals("BLUE")).map(Map.Entry::getKey).collect(Collectors.toList());

        long redSupportAmount = annualizedViews.stream().filter(view -> redSupportLoginName.contains(view.getLoginName())).mapToLong(ActivityInvestAnnualizedView::getSumAnnualizedAmount).sum();
        long blueSupportAmount = annualizedViews.stream().filter(view -> blueSupportLoginName.contains(view.getLoginName())).mapToLong(ActivityInvestAnnualizedView::getSumAnnualizedAmount).sum();

        if (Strings.isNullOrEmpty(loginName)) {
            return Maps.newHashMap(ImmutableMap.<String, Object>builder()
                    .put("redAmount", AmountConverter.convertCentToString(redSupportAmount))
                    .put("blueAmount", AmountConverter.convertCentToString(blueSupportAmount))
                    .put("redCount", redSupportLoginName.size())
                    .put("blueCount", blueSupportLoginName.size())
                    .build());
        }

        ActivityInvestAnnualizedModel model = activityInvestAnnualizedMapper.findByActivityAndLoginName(ActivityInvestAnnualized.THIRD_ANNIVERSARY_ACTIVITY, loginName);
        boolean isSelect = redisWrapperClient.hexists(THIRD_ANNIVERSARY_SELECT_RED_OR_BLUE, loginName);
        String currentRate = "0";
        String currentAward = "0";
        String selectResult = "";
        if (isSelect) {
            boolean isSelectRed = redisWrapperClient.hget(THIRD_ANNIVERSARY_SELECT_RED_OR_BLUE, loginName).equals("RED");
            long myAmount = model == null ? 0 : model.getSumAnnualizedAmount();
            if (redSupportAmount > blueSupportAmount) {
                currentRate = isSelectRed ? "0.8%" : "0.5%";
                currentAward = AmountConverter.convertCentToString((long) (myAmount * (isSelectRed ? 0.008 : 0.005)));
            } else if (redSupportAmount < blueSupportAmount) {
                currentRate = isSelectRed ? "0.5%" : "0.8%";
                currentAward = AmountConverter.convertCentToString((long) (myAmount * (isSelectRed ? 0.005 : 0.008)));
            } else {
                currentRate = "0.5%";
                currentAward = AmountConverter.convertCentToString((long) (myAmount * 0.005));
            }
            selectResult = isSelectRed ? "RED" : "BLUE";
        }

        return Maps.newHashMap(ImmutableMap.<String, Object>builder()
                .put("redAmount", AmountConverter.convertCentToString(redSupportAmount))
                .put("blueAmount", AmountConverter.convertCentToString(blueSupportAmount))
                .put("redCount", redSupportLoginName.size())
                .put("blueCount", blueSupportLoginName.size())
                .put("isSelect", isSelect)
                .put("selectResult", selectResult)
                .put("myAmount", model == null ? "0" : AmountConverter.convertCentToString(model.getSumAnnualizedAmount()))
                .put("currentRate", currentRate)
                .put("currentAward", currentAward)
                .build());
    }

    public Map<String, Object> getTopFourTeam(String loginName) {
        Map<String, Object> map = new HashMap<>();
        if (!redisWrapperClient.exists(THIRD_ANNIVERSARY_TOP_FOUR_TEAM)) {
            map.put("isSuccess", false);
            return map;
        }
        List<String> teams = Arrays.asList(redisWrapperClient.get(THIRD_ANNIVERSARY_TOP_FOUR_TEAM).split(","));
        map.put("topFour", redisWrapperClient.get(THIRD_ANNIVERSARY_TOP_FOUR_TEAM_CHINESE));
        map.put("collectSuccess", thirdAnniversaryDrawMapper.findLoginNameByCollectTopFour(teams).size());
        map.put("isSuccess", Strings.isNullOrEmpty(loginName) ? false : teams.stream().filter(name->name.equals(loginName)).findAny());
        return map;
    }

    public BaseResponse<Map<String, Object>> getTeamLogos(String loginName) {
        Map<String, Object> map = this.getTopFourTeam(loginName);
        map.put("prizes", thirdAnniversaryDrawMapper.findByLoginName(loginName));
        return new BaseResponse<Map<String, Object>>(map);
    }

    public BaseResponse draw(String loginName) {

        if (StringUtils.isEmpty(loginName)) {
            return new BaseResponse("您还未登陆，请登陆后再来抽奖吧！");
        }

        if (!duringDrawActivities()) {
            return new BaseResponse("不再活动范围内");
        }

        if (accountMapper.findByLoginName(loginName) == null) {
            return new BaseResponse("您还未实名认证，请实名认证后再来抽奖吧！");
        }

        int usedDrawCount = thirdAnniversaryDrawMapper.countDrawByLoginName(loginName);

        ActivityInvestAnnualizedModel activityInvestAnnualizedModel = activityInvestAnnualizedMapper.findByActivityAndLoginName(ActivityInvestAnnualized.THIRD_ANNIVERSARY_ACTIVITY, loginName);
        int investDrawCount = activityInvestAnnualizedModel == null ? 0 : (int) (activityInvestAnnualizedModel.getSumInvestAmount() / EACH_INVEST_AMOUNT_50000);

        Map<String, String> eachEveryDayDraws = redisWrapperClient.hgetAll(MessageFormat.format(THIRD_ANNIVERSARY_EACH_EVERY_DAY_DRAW, loginName));
        int usedInvestDrawCount = usedDrawCount - eachEveryDayDraws.size();

        int isTodayDraw = eachEveryDayDraws.entrySet().stream().filter(entry -> entry.getKey().equals(DateTime.now().toString("yyyy-MM-dd"))).count() > 0 ? 0 : 1;
        int unUseDrawCount = investDrawCount - usedInvestDrawCount + isTodayDraw;

        if (unUseDrawCount <= 0) {
            return new BaseResponse("您暂无抽奖机会，赢取机会后再来抽奖吧!");
        }

        List<ThirdAnniversaryDrawModel> models = new ArrayList<>();
        for (int i = 0; i < unUseDrawCount; i++) {
            int random = (int) (Math.random() * 32 + 1);
            models.add(new ThirdAnniversaryDrawModel(loginName, FOOTBALL_TEAMS.get(random)));
        }

        redisWrapperClient.hset(MessageFormat.format(THIRD_ANNIVERSARY_EACH_EVERY_DAY_DRAW, loginName), DateTime.now().toString("yyyy-MM-dd"), "success", lifeSecond);
        thirdAnniversaryDrawMapper.create(models);

        return new BaseResponse<List<ThirdAnniversaryDrawModel>>(models.stream().collect(Collectors.groupingBy(ThirdAnniversaryDrawModel::getTeamName, Collectors.summingInt(ThirdAnniversaryDrawModel::getTeamCount)))
                .entrySet().stream().map(entry -> new ThirdAnniversaryDrawModel(loginName, entry.getKey(), entry.getValue())).collect(Collectors.toList()));

    }

    public BaseResponse selectRedOrBlue(String loginName, boolean isRed) {
        if (Strings.isNullOrEmpty(loginName) || redisWrapperClient.hexists(THIRD_ANNIVERSARY_SELECT_RED_OR_BLUE, loginName)) {
            return new BaseResponse(false);
        }
        redisWrapperClient.hset(THIRD_ANNIVERSARY_SELECT_RED_OR_BLUE, loginName, isRed ? "RED" : "BLUE");
        return new BaseResponse<Map<String, Object>>(this.selectResult(loginName));
    }

    public Map<String, Object> invite(String loginName) {
        List<WeChatHelpModel> models = weChatHelpMapper.findByUserAndHelpType(loginName, null, WeChatHelpType.THIRD_ANNIVERSARY_HELP);
        if (models.size() == 0) {
            List<ActivityInvestModel> investModels = activityInvestMapper.findAllByActivityLoginNameAndTime(loginName, ActivityCategory.THIRD_ANNIVERSARY_ACTIVITY.name(), activityStartTime, new Date());
            long sumAnnualizedAmount = investModels.stream().mapToLong(ActivityInvestModel::getAnnualizedAmount).sum();
            return Maps.newHashMap(ImmutableMap.<String, Object>builder()
                    .put("annualizedAmount", AmountConverter.convertCentToString(sumAnnualizedAmount))
                    .put("rewardRate", "0")
                    .build());
        }
        WeChatHelpModel weChatHelpModel = models.get(0);
        List<ActivityInvestModel> investModels = activityInvestMapper.findAllByActivityLoginNameAndTime(loginName, ActivityCategory.THIRD_ANNIVERSARY_ACTIVITY.name(), activityStartTime, weChatHelpModel.getEndTime());
        long sumAnnualizedAmount = investModels.stream().mapToLong(ActivityInvestModel::getAnnualizedAmount).sum();
        List<WeChatHelpInfoModel> helpInfoModels = weChatHelpInfoMapper.findByHelpId(weChatHelpModel.getId());
        return Maps.newHashMap(ImmutableMap.<String, Object>builder()
                .put("annualizedAmount", AmountConverter.convertCentToString(sumAnnualizedAmount))
                .put("rewardRate", AmountConverter.convertCentToString((long) (sumAnnualizedAmount * rates.get(helpInfoModels.size()))))
                .put("endTime", weChatHelpModel.getEndTime())
                .put("helpFriend", helpInfoModels)
                .build());
    }

    public void shareInvite(String loginName) {
        List<WeChatHelpModel> models = weChatHelpMapper.findByUserAndHelpType(loginName, null, WeChatHelpType.THIRD_ANNIVERSARY_HELP);
        if (models.size() == 0) {
            return;
        }
        UserModel userModel = userMapper.findByLoginName(loginName);
        WeChatHelpModel weChatHelpModel = new WeChatHelpModel(loginName, userModel.getUserName(), userModel.getMobile(), WeChatHelpType.THIRD_ANNIVERSARY_HELP, new Date(), DateTime.now().plusDays(3).toDate());
        weChatHelpMapper.create(weChatHelpModel);
        redisWrapperClient.hset(THIRD_ANNIVERSARY_WAIT_SEND_REWARD, String.valueOf(weChatHelpModel.getId()), DateTime.now().plusDays(3).toString("yyyy-MM-dd HH:mm:ss"));
    }

    public Map<String, Object> sharePage(String loginName, String originator) {
        List<WeChatHelpModel> models = weChatHelpMapper.findByUserAndHelpType(originator, null, WeChatHelpType.THIRD_ANNIVERSARY_HELP);
        if (models.size() == 0) {
            return null;
        }
        List<WeChatHelpInfoModel> helpInfoModels = weChatHelpInfoMapper.findByHelpId(models.get(0).getId());
        return Maps.newHashMap(ImmutableMap.<String, Object>builder()
                .put("originator", models.get(0).getUserName())
                .put("endTime", models.get(0).getEndTime())
                .put("isHelp", helpInfoModels.stream().map(model -> model.getLoginName().equals(loginName)).findAny().isPresent())
                .put("helpFriend", helpInfoModels)
                .build());
    }

    public void openRedEnvelope(String loginName, String mobile, String originator) {
        List<WeChatHelpModel> models = weChatHelpMapper.findByUserAndHelpType(originator, null, WeChatHelpType.THIRD_ANNIVERSARY_HELP);
        List<WeChatHelpInfoModel> helpInfoModels = weChatHelpInfoMapper.findByHelpId(models.get(0).getId());
        if (new Date().after(models.get(0).getEndTime())){
            return;
        }
        if (helpInfoModels.size() >= 3) {
            return;
        }
        if (helpInfoModels.stream().map(model -> model.getLoginName().equals(loginName)).findAny().isPresent()) {
            return;
        }
        weChatHelpInfoMapper.create(new WeChatHelpInfoModel(loginName, mobile, models.get(0).getId(), WeChatHelpUserStatus.WAITING));
    }

    private boolean duringDrawActivities() {
        return activityStartTime.before(new Date()) && activityDrawEndTime.after(new Date());
    }

    public boolean isActivityRegister(String loginName){
        return "thirdAnniversary".equals(userMapper.findByLoginName(loginName).getChannel());
    }

    public boolean isAccount(String loginName) {
        return accountMapper.findByLoginName(loginName) != null;
    }
}