/*
 * Copyright (C) 2013 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Feb 20, 2013
 * @version 1.0
 */
public class CommonUtils {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CommonUtils.class);
    private static char[] token = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static Random generator = new Random();

    public static String genLoginOTP() {
        String OTP = "";
        int car = token.length - 1;
        final int numText = 8;
        for (int i = 0; i < numText; i++) {
            OTP += token[generator.nextInt(car) + 1];
        }
        return OTP;
    }

    private static boolean doCheckDigit(char digit) {
        boolean validate = true;
        if (!Character.isDigit(digit)) {
            validate = false;
        }
        return validate;
    }

    /**
     * Kiem tra chuoi co phai chi la chuoi so khong
     *
     * @param str Chuoi can kiem tra
     * @return true/false
     */
    public static boolean doCheckDigit(String str) {
        if (str == null) {
            return false;
        }
        str = str.trim();
        boolean validate = true;
        if (str.length() > 0) {
            int i = 0;
            while (validate && i < str.length()) {
                if (!doCheckDigit(str.charAt(i))) {
                    validate = false;
                }
                i++;
            }
        } else {
            validate = false;
        }
        return validate;
    }

    public static String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip;
    }

    public static String getUserAgent(HttpServletRequest req) {
        String agent = "";
        agent = req.getHeader("user-agent");
        return agent;
    }

    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null || obj.toString().trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isViettelNumber(String msisdn) {
        if (msisdn == null || "".equals(msisdn.trim())) {
            return false;
        }
        if (msisdn.startsWith("0")) {
            msisdn = "84" + msisdn.substring(1);
        } else {
            if (!msisdn.startsWith("84")) {
                msisdn = "84" + msisdn;
            }
        }

        String listViettelPrefix = ResourceBundleUtil.getString("list_viettel_prefix");
        String[] arr = listViettelPrefix.split(",");
        for (int i = 0; i < arr.length; i++) {
            if (msisdn.startsWith(arr[i])) {
                return true;
            }
        }
        return false;
    }

    public static String generateTransId() {
        String id = "";
        id += DateTimeUtils.convertDateToString(Calendar.getInstance().getTime(), "yyMMddHHmmss");
        Random rand = new Random();
        String i = rand.nextInt(999999) + "";
        i = StringProcess.padLeft(i, 6, '0');
        id += i;
        return id;
    }
    
    public static String generateTransBankId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String id = sdf.format(Calendar.getInstance().getTime());
        id += "24";

        String strSeq;
        Random rand = new Random();
        strSeq = rand.nextInt(9999999) + "";
        strSeq = StringProcess.padLeft(strSeq, 7, '0');
        id += strSeq;
        return id;
    }
}
