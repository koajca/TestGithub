/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util;

import java.util.ResourceBundle;

public class Constants {

    /**
     *
     * @author cuongdv3@viettel.com.vn
     * @since Mar 11, 2014
     * @version 1.0
     */
    static final ResourceBundle resourceConfig = ResourceBundle.getBundle("config");

    public static final String SESS_ATTR_USER_LOGIN_INFO = "userLoginInfo";

    public static final String NOT_VALIDATE_URL = resourceConfig.getString("not_validate_url");

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Constants.class);
//
//    static {
//        try {
//            Constants.setNOT_VALIDATE_URL(resourceConfig.getString("not_validate_url"));
//        } catch (Exception ex) {
//            log.error(ex, ex);
//        }
//    }
}
