/*
 * Copyright (C) 2013 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util;

import com.viettel.bankplus.merchantgw.beans.BaseBean;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Cell;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Feb 20, 2013
 * @version 1.0
 */
public class CommonUtils extends BaseBean {

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

    private static CommonUtils instance = new CommonUtils();

    public static CommonUtils getInstance() {
        return instance;
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
        String agent = req.getHeader("user-agent");
        return agent;
    }

    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null || "".equals(obj.toString().trim())) {
            return true;
        } else {
            return false;
        }
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

    public static String formatDecima(String regexFormat, Object data) throws Exception {
        String result = "";
        try {
            NumberFormat formatData = new DecimalFormat(regexFormat);
            result = data == null ? "" : formatData.format(data);
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
        return result;
    }

    public static String getValueOfCellExtend(org.apache.poi.ss.usermodel.Cell cell) {
        String result = null;
        try {
            if (cell != null) {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        result = cell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        BigDecimal tempOriginal = BigDecimal.valueOf(cell.getNumericCellValue());
                        String tempConvert = CommonUtils.formatDecima("###", tempOriginal);
                        result = tempConvert.equals(tempOriginal.toString()) ? tempOriginal.toString() : tempConvert;
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        result = cell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_ERROR:
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return result;
    }

    public static String getValueOfCellOriginal(org.apache.poi.hssf.usermodel.HSSFCell cell) {
        String result = null;
        try {
            if (cell != null) {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        result = cell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        BigDecimal tempOriginal = BigDecimal.valueOf(cell.getNumericCellValue());
                        String tempConvert = CommonUtils.formatDecima("###", tempOriginal);
                        result = tempConvert.equals(tempOriginal.toString()) ? tempOriginal.toString() : tempConvert;
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        result = cell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_ERROR:
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return result;
    }

    public static boolean validateByRegex(String content, String regex) {
        if (content != null && !"".equals(content)) {
            Pattern pattern = Pattern.compile(regex);
            return (pattern.matcher(content).matches());
        }
        return true;
    }

    public static String formatAmount(Object amount) {
        try {
            NumberFormat formatter = new DecimalFormat(ResourceBundleUtils.getConfig("format.amount"));
            return (amount == null ? "" : formatter.format(amount));
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
        return "";
    }

    public String standardizedISDN(String isdn) throws Exception {
        String result = "";
        try {
            if (isdn != null && !"".equals(isdn)) {
                String countryCode = ResourceBundleUtils.getConfig("country.code");
                if (isdn.startsWith(countryCode)) {
                    result = isdn;
                } else if (isdn.startsWith("0")) {
                    result = countryCode + isdn.substring(1);
                } else {
                    result = countryCode + isdn;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    public String hideMSISDN(String isdn) throws Exception {
        String result = "";
        try {
            if (isdn != null && !"".equals(isdn)) {
                isdn = this.standardizedISDN(isdn);
                String replaceDigit = ResourceBundleUtils.getConfig("character.replace.digit.msisdn");
                if (isdn.length() > 6) {
                    result = isdn.substring(0, 3);
                    for (int i = 3; i < isdn.length() - 3; i++) {
                        result += replaceDigit;
                    }
                    result += isdn.substring(isdn.length() - 3, isdn.length());
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    public static void main(String[] args) {
//        System.out.println("result: "+validateByRegex("Lỗi tại ngân hàng", "\\."));
        Pattern pattern = Pattern.compile(".");
        Matcher matcher = pattern.matcher("Lỗi tại ngân hàng");
        boolean matchFound = matcher.matches();
        System.out.println("result: " + matchFound);
    }
}
