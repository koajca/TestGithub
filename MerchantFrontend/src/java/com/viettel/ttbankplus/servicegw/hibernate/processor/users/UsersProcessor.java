/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.processor.users;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Users;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since 11-03-2014
 */
public class UsersProcessor {

    private static UsersProcessor instance;
    private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(UsersProcessor.class);

    public static UsersProcessor getInstance() {
        if (instance == null) {
            instance = new UsersProcessor();
        }
        return instance;
    }

    public Users doSave(Users user) {
        Users users = null;
        try {
            users = DAOFactory.getUsersDAO().makePersistent(user);
        } catch (Exception ex) {
            log.error("doSave: ", ex);
        }finally{
            DAOFactory.commitCurrentSessions();
        }
        return users;
    }
    
    public Users getUser(String username, Long contentProviderId) {
        return DAOFactory.getUsersDAO().getUser(username, contentProviderId);
    }
}
