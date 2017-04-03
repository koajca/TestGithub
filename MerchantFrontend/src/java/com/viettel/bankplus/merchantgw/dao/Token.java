/*
 * Copyright (C) 2013 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.dao;

import com.viettel.action.BaseAction;

/**
 * Struts token dao
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since 1.0
 */
public class Token extends BaseAction {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Token.class);
    /**
     * Reload struts token
     * @return token
     */
    public String reloadToken() {
        log.info("==== Reload Token ====");
        return "token";
    }
}
