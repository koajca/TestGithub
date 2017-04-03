/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.common.util;

import com.viettel.security.PassTranformer;
import java.math.BigDecimal;

/**
 *
 * @author Admin
 */
public class StringProcess {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StringProcess.class);

    public static int convertToInt(Object obj) {
        int iResult = 0;
        if (!CommonUtils.isNullOrEmpty(obj)) {
            try {
                iResult = Integer.parseInt(obj.toString().trim());
            } catch (Exception ex) {
                log.error("convertToInt: ", ex);
            }
        }
        return iResult;
    }

    public static Long convertToLong(Object obj) {
        Long iResult = 0L;
        if (!CommonUtils.isNullOrEmpty(obj)) {
            try {
                iResult = Long.parseLong(obj.toString().trim());
            } catch (Exception ex) {
                log.error("convertToLong: ", ex);
            }
        }
        return iResult;
    }

    public static Double convertToDouble(Object obj) {
        Double iResult = 0.0;
        if (!CommonUtils.isNullOrEmpty(obj)) {
            try {
                iResult = Double.parseDouble(obj.toString().trim());
            } catch (Exception ex) {
                log.error("convertToDouble: ", ex);
            }
        }
        return iResult;
    }

    public static String convertToString(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }
    }

    public static BigDecimal convertToBigDecimal(Object obj) {
        BigDecimal result = null;
        if (!CommonUtils.isNullOrEmpty(obj)) {
            try {
                result = new BigDecimal(convertToString(obj));
            } catch (Exception ex) {
                log.error("convertToBigDecimal: ", ex);
            }
        }
        return result;
    }

    public static String padLeft(String text, int totalWidth,
            Character paddingChar) {
        String strReturn = text;
        if (text.length() < totalWidth) {
            int numAdd = totalWidth - text.length();
            for (int i = 0; i < numAdd; i++) {
                strReturn = paddingChar.toString() + strReturn;
            }
        }
        return strReturn;
    }

    public static String padRight(String text, int totalWidth,
            Character paddingChar) {
        String strReturn = text;
        if (text.length() < totalWidth) {
            int numAdd = totalWidth - text.length();
            for (int i = 0; i < numAdd; i++) {
                strReturn += paddingChar.toString();
            }
        }
        return strReturn;
    }

    public static String padRight(String text, int totalWidth,
            String paddingChar) {
        String strReturn = text;
        if (text.length() < totalWidth) {
            int numAdd = totalWidth - text.length();
            for (int i = 0; i < numAdd; i++) {
                strReturn += paddingChar;
            }
        }
        return strReturn;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(PassTranformer.decrypt("39036ae60c13db43ec55ebde022569b62d39b0f695440a2b710f9027d20dce884b74ae915e503786340594ae81bb50d4"));
        System.out.println(PassTranformer.decrypt("6a85a151dc8c45e71d4c6087ffaa67d8"));
        System.out.println(PassTranformer.decrypt("268102cf73390eb84687ccba21e07165"));
        
        System.out.println(PassTranformer.encrypt("jdbc:oracle:thin:@10.58.62.226:3873:merchant"));
        System.out.println(PassTranformer.encrypt("vp_ftp"));
        System.out.println(PassTranformer.encrypt("payment#2012"));
    }
}
