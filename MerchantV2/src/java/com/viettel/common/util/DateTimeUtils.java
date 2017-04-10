/*
 * DateTimeUtils.java
 *
 * Created on August 6, 2007, 3:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.viettel.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Vu Thi Thu Huong
 */
public class DateTimeUtils {

    public static final String patternDateVN = "dd/MM/yyyy";
    public static final String patternDateVNFull = "dd/MM/yyyy hh:mm:ss";
    public static final String patternDateEN = "dd-MM-yyyy";
    public static final String patternDateENFull = "yyyy-MM-dd hh:mm:ss";
    public static final String patternDateSQL = "yyyyMMdd";
    public static final String patternDateSQLFull = "yyyyMMddHHmmss";
    public static final String patternDateSQLFullOracle = "YYYYMMDDHH24MISS";
    public static final String pattern_ddMMyy = "ddMMyy";
    public static final String pattern_MMyyyy = "MM/yyyy";
    public static final String pattern_yyMMdd = "yyyy-MM-dd";

    /**
     * Creates a new instance of DateTimeUtils
     */
    public DateTimeUtils() {
    }
    private static final Logger log = Logger.getLogger(DateTimeUtils.class);

    public static String changeDatePattern(String fromPattern,
            String toPattern, String input) {
        SimpleDateFormat formatter = new SimpleDateFormat();
        String result = "";
        try {
            formatter.applyPattern(fromPattern);
            Date date = formatter.parse(input);
            formatter.applyPattern(toPattern);
            result = formatter.format(date);
        } catch (ParseException e) {
            log.error("Date ParseException:", e);
        }
        return result;

    }

    public static Date convertStringToDate(String date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            Date d = dateFormat.parse(date);
            return d;
        } catch (ParseException e) {
            log.error(e, e);
        }
        return null;
    }

    public static String convertDateToString(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        if (date == null) {
            return "";
        }
        try {
            return dateFormat.format(date);
        } catch (Exception e) {
            log.error("Date ParseException:", e);
        }
        return "";
    }

    /**
     * @author: ThangPV
     * @todo: convert from java.util.Date to java.sql.Date
     */
    public static java.sql.Date convertToSqlDate(java.util.Date utilDate) {
        return new java.sql.Date(utilDate.getTime());
    }

    /**
     * @anhlt - Get the first day on month selected.
     * @param dateInput
     * @return
     */
    public static String parseDate(int monthInput) {
        String dateReturn = "01/01/";
        Calendar cal = Calendar.getInstance();
        switch (monthInput) {
            case 1:
                dateReturn = "01/01/";
                break;
            case 2:
                dateReturn = "01/02/";
                break;
            case 3:
                dateReturn = "01/03/";
                break;
            case 4:
                dateReturn = "01/04/";
                break;
            case 5:
                dateReturn = "01/05/";
                break;
            case 6:
                dateReturn = "01/06/";
                break;
            case 7:
                dateReturn = "01/07/";
                break;
            case 8:
                dateReturn = "01/08/";
                break;
            case 9:
                dateReturn = "01/09/";
                break;
            case 10:
                dateReturn = "01/10/";
                break;
            case 11:
                dateReturn = "01/11/";
                break;
            case 12:
                dateReturn = "01/12/";
                break;
        }
        return dateReturn + cal.get(Calendar.YEAR);
    }

    /**
     * @cuongph - Get start date in day.
     * @param dateInput
     * @return
     */
    public static Date getStartDate(Date toDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);
        cal.set(cal.HOUR_OF_DAY, 0);
        cal.set(cal.MINUTE, 0);
        cal.set(cal.SECOND, 0);
        return cal.getTime();
    }

    /**
     * @cuongph - Get end date in day.
     * @param dateInput
     * @return
     */
    public static Date getEndDate(Date toDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);
        cal.set(cal.HOUR_OF_DAY, 23);
        cal.set(cal.MINUTE, 59);
        cal.set(cal.SECOND, 59);
        return cal.getTime();
    }

    public static Date getNextDay(Date toDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        return cal.getTime();
    }

    public static Date addDate(Date dateAdd, int field, int amount) {

        Calendar cal = Calendar.getInstance();

        cal.setTime(dateAdd);

        cal.add(field, amount);

        return cal.getTime();

    }

    public static int compareDate(Date firstDate, Date secondeDate) {
        return firstDate.compareTo(secondeDate);
    }

    public static String getCurrentDate() {
        Date now = Calendar.getInstance().getTime();
        String current = now.getYear() + "" + now.getMonth() + "" + now.getDate();
        return current;
    }

    public static Date getFirstDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getDateOriginal(Date date) throws Exception {
        Date result = null;
        try {
            if (date != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                result = cal.getTime();
            }
        } catch (Exception e) {
            throw e;
        }
        return result;

    }

}
