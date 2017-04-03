/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.dao;

import com.viettel.action.BaseAction;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 10, 2014
 * @version 1.0
 */
public class MainAction extends BaseAction {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MainAction.class);
    String MENU_PAGE = "getMenu";
    String INDEX_PAGE = "indexSuccess";

    public String getIndexPage() {
        log.info("MainDAO.getIndexPage");
        if (isLoggedIn()) {
            return INDEX_PAGE;
        } else {
            return "loginIndex";
        }
    }
}
