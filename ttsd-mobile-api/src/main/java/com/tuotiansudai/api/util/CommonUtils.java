package com.tuotiansudai.api.util;


import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommonUtils {

    public static String encryptUserName(String userName) {

        if (userName.length() > 3) {

            userName = userName.substring(0, 3) + "***";

        } else {

            userName += "***";

        }
        return userName;
    }

    public static String encryptBankCardNo(String cardNo) {
        if (StringUtils.isEmpty(cardNo)) {
            return "";
        }

        if (cardNo.length() > 4) {

            cardNo = cardNo.substring(0, 4) + "***" + cardNo.substring(cardNo.length() - 4);

        }
        return cardNo;
    }

    public static String convertRealMoneyByType(double money, String type) {
        if ("ti_balance".equals(type)) {
            return "+" + money;
        } else if ("to_balance".equals(type) || "to_frozen".equals(type)) {
            return "-" + money;
        }
        return "" + money;
    }

    public static String mapToFormData(Map<String, String> map, boolean isURLEncoder) throws UnsupportedEncodingException {
        Map<String, String> mapCopy = null;
        if (isURLEncoder) {
            mapCopy = new HashMap<>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) iterator.next();
                String encodeValue = URLEncoder.encode(entry.getValue(), "UTF-8");
                mapCopy.put(entry.getKey(), encodeValue);

            }
        } else {
            mapCopy = map;
        }

        String formData = "";
        if (mapCopy != null && mapCopy.size() > 0) {
            formData = Joiner.on("&").withKeyValueSeparator("=").join(mapCopy);
        }
        return formData;
    }

    public static String calculatorInvestBeginSeconds(Date investBeginTime) {
        if (investBeginTime == null) {
            return "0";
        }
        Long time = (investBeginTime.getTime() - System
                .currentTimeMillis()) / 1000;
        if (time < 0) {
            return "0";
        }
        return time.toString();

    }

    public static String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

}
