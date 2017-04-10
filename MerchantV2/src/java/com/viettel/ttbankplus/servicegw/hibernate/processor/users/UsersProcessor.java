/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.processor.users;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Users;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.UsersPassHis;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since 11-03-2014
 */
public class UsersProcessor {

    private static volatile UsersProcessor instance;
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
            DAOFactory.commitCurrentSessions();
        } catch (Exception ex) {
            log.error("doSave: ", ex);
        }
        return users;
    }

    public Users getUser(String username, Long contentProviderId) {
        return DAOFactory.getUsersDAO().getUser(username, contentProviderId);
    }

    public List<HashMap> getUserFunction(Long userId) {
        return DAOFactory.getUsersDAO().getUserFunction(userId);
    }

    public UsersPassHis doSavePassHis(UsersPassHis userPassHis) {
        UsersPassHis updated = null;
        try {
            updated = DAOFactory.getUsersPassHisDAO().makePersistent(userPassHis);
            DAOFactory.commitCurrentSessions();
        } catch (Exception ex) {
            log.error("doSavePassHis: ", ex);
        }
        return updated;
    }
    public List<UsersPassHis> getFiveRecentPassHis(Long userId) {
        return DAOFactory.getUsersPassHisDAO().getFiveRecentPassHis(userId);
    }
}
