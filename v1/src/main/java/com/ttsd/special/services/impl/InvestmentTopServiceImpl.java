package com.ttsd.special.services.impl;

import com.ttsd.redis.RedisClient;
import com.ttsd.special.dao.InvestmentTopDao;
import com.ttsd.special.dto.InvestTopItem;
import com.ttsd.special.dto.InvestTopResponse;
import com.ttsd.special.dto.InvestTopStatPeriod;
import com.ttsd.special.services.CachedMobileLocationService;
import com.ttsd.special.services.InvestmentTopService;
import com.ttsd.util.ChinaArea;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class InvestmentTopServiceImpl implements InvestmentTopService {
    /**
     * redis 里的投资排行榜的 key
     */
    private static final String INVEST_TOP_SPECIAL_CACHE_KEY = "invest_top_special_cache";
    /**
     * 缓存有效期（毫秒）  默认1小时
     */
    private static final int INVEST_TOP_SPECIAL_CACHE_EXPIRE = 1000*60;//*60;
    /**
     * 排行榜的最大容量
     */
    private static final int TopLimit = 100;

    @Autowired
    private RedisClient redis;

    @Autowired
    private InvestmentTopDao dao;

    @Autowired
    private CachedMobileLocationService mobileLocationService;

    @Override
    public InvestTopResponse queryInvestTopResponse(InvestTopStatPeriod period) {
        InvestTopResponse resp = getInvestTopResponseFromCache(period);
        // 如果缓存存在
        if(resp != null){
            Date cacheTime = resp.getUpdateTime();
            Date newTime = new Date();
            // 且没有过期
            if(newTime.getTime() - cacheTime.getTime() < INVEST_TOP_SPECIAL_CACHE_EXPIRE){
                // 则直接使用缓存的数据
                return resp;
            }
        }
        // 除此之外，重新查询数据 (此处可以异步处理，不过暂时可以先看看效果)
        resp = queryInvestTopResponseNoCache(period);
        // 并缓存
        cacheInvestTopResponse(resp, period);
        return resp;
    }

    @Override
    public InvestTopResponse queryInvestTopResponseNoCache(InvestTopStatPeriod period) {
        // 计算起止时间
        Date[] beginEndTime = buildStartAndEndTime(period);
        Date beginTime = beginEndTime[0];
        Date endTime = beginEndTime[1];

        // 查询全部投资排行
        List<InvestTopItem> d = dao.StatInvestmentTop(beginTime, endTime);

        // 分发到全国及六个区域
        Map<ChinaArea, List<InvestTopItem>> investMap = distributeTopList(d, TopLimit);
        InvestTopResponse resp = new InvestTopResponse();

        // 数据填充
        resp.setBeginTime(beginTime);
        resp.setEndTime(DateUtils.addDays(endTime,-1));//endTime实际上是次日的零点，所以在输出时应该减一天
        resp.setUpdateTime(new Date());
        resp.setAreaInvestments(investMap);
        return resp;
    }

    private InvestTopResponse getInvestTopResponseFromCache(InvestTopStatPeriod period){
        InvestTopResponse cachedResponse = null;
        String cachedResponseString = redis.hget(INVEST_TOP_SPECIAL_CACHE_KEY, period.name());
        if(StringUtils.isNotBlank(cachedResponseString)) {
            try {
                byte[] bytes = Base64.decodeBase64(cachedResponseString);
                cachedResponse = SerializationUtils.deserialize(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachedResponse;
    }

    private void cacheInvestTopResponse(InvestTopResponse response, InvestTopStatPeriod period){
        String cachedResponseString = null;
        try {
            byte[] bytes = SerializationUtils.serialize(response);
            cachedResponseString = Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        redis.hset(INVEST_TOP_SPECIAL_CACHE_KEY, period.name(), cachedResponseString);
    }

    private Map<ChinaArea, List<InvestTopItem>> distributeTopList(List<InvestTopItem> itemList, int limit){
        Map<ChinaArea, List<InvestTopItem>> areaInvestTopMap = new HashMap<>();
        Map<ChinaArea, Integer> areaInvestUserCountMap=new HashMap<>();
        for(ChinaArea area:ChinaArea.values()){
            areaInvestTopMap.put(area, new ArrayList<InvestTopItem>());
            areaInvestUserCountMap.put(area, 0);
        }

        for(InvestTopItem item:itemList){
            if(areaInvestUserCountMap.get(ChinaArea.CHINA) < limit){
                areaInvestTopMap.get(ChinaArea.CHINA).add(item);
            }
            ChinaArea area = mobileLocationService.getAreaByPhoneNumber(item.getPhoneNumber());
            if(area != ChinaArea.CHINA){
                if(areaInvestUserCountMap.get(area) < limit){
                    areaInvestTopMap.get(area).add(item);
                }
            }
        }
        return areaInvestTopMap;
    }

    private static Date[] buildStartAndEndTime(InvestTopStatPeriod period){
        Date[] dates = new Date[2];

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int weekofyear = cal.get(Calendar.WEEK_OF_YEAR);

        if(period == InvestTopStatPeriod.Week){
            cal.setWeekDate(year,weekofyear, 2);
            dates[0] = cal.getTime();
            cal.setWeekDate(year,weekofyear+1, 2);
            dates[1] = cal.getTime();
        }

        if(period == InvestTopStatPeriod.Month){
            cal.set(year, month, 1);
            dates[0] = cal.getTime();
            cal.set(year, month + 1, 1);
            dates[1] = cal.getTime();
        }

        if(period == InvestTopStatPeriod.Quarter){
            cal.set(year, (month/3)*3, 1);
            dates[0] = cal.getTime();
            cal.add(Calendar.MONTH, 3);
            dates[1] = cal.getTime();
        }

        if(period == InvestTopStatPeriod.Year){
            cal.set(year, 0, 1);
            dates[0] = cal.getTime();
            cal.set(year+1, 0, 1);
            dates[1] = cal.getTime();
        }

        return dates;
    }
}
