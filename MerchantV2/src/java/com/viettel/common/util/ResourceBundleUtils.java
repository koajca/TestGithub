/*
 * ResourceBundleUtils.java
 *
 * Created on September 18, 2007, 11:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.viettel.common.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author PHUCPT
 */
public class ResourceBundleUtils {

    private static ResourceBundle rbLanguage = null;
    private static ResourceBundle rbMessage = null;
    private static ResourceBundle rbConfig = ResourceBundle.getBundle("config");

    /**
     * Creates a new instance of ResourceBundleUtils
     */
    public ResourceBundleUtils() {
    }

    public static ResourceBundle getRbLanguage() {
        return rbLanguage;
    }

    public static void setRbLanguage(ResourceBundle rbLanguage) {
        ResourceBundleUtils.rbLanguage = rbLanguage;
    }

    public static ResourceBundle getRbMessage() {
        return rbMessage;
    }

    public static void setRbMessage(ResourceBundle rbMessage) {
        ResourceBundleUtils.rbMessage = rbMessage;
    }

    public static ResourceBundle getRbConfig() {
        return rbConfig;
    }

    public static void setRbConfig(ResourceBundle rbConfig) {
        ResourceBundleUtils.rbConfig = rbConfig;
    }

    public static String getString(String key, Locale locale) throws Exception {
        try {
            if (locale != null) {
                setRbLanguage(ResourceBundle.getBundle("language", locale));
            } else {
                setRbLanguage(ResourceBundle.getBundle("language"));
            }
        } catch (Exception e) {
            throw e;
        }
        return rbLanguage.getString(key);
    }

    public static String getMessage(String key, Locale locale) throws Exception {
        try {
            if (locale != null) {
                setRbMessage(ResourceBundle.getBundle("message", locale));
            } else {
                setRbMessage(ResourceBundle.getBundle("message"));
            }
        } catch (Exception e) {
            throw e;
        }
        return rbMessage.getString(key);
    }

    public static String getConfig(String key) throws Exception {
        try {
            return rbConfig.getString(key);
        } catch (Exception e) {
            throw e;
        }
    }

}
