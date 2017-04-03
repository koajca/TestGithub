/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util;

import com.viettel.security.PassTranformer;
import java.util.ResourceBundle;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 11, 2014
 * @version 1.0
 */
public class ApplicationListener implements ServletContextListener {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ApplicationListener.class);
    private static ResourceBundle rb = ResourceBundle.getBundle("config");

    public void contextInitialized(ServletContextEvent sce) {
        try {
            log.info("Context Initialized");
        } catch (Exception ex) {
            log.error("Loi khi start ung dung", ex);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Context Destroyed");
    }
}
