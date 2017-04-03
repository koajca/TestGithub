/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.processor.provider;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.SessionManager;
import java.util.List;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since 21-11-2011
 */
public class SessionProcessor {

    private static SessionProcessor instance;
    private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SessionProcessor.class);
    public static SessionProcessor getInstance() {
        if (instance == null) {
            instance = new SessionProcessor();
        }
        return instance;
    }

    public SessionManager findById(Long sessionManagerId){
        SessionManager sm = null;
        try{
            sm = DAOFactory.getSessionManagerDAO().findById(sessionManagerId, false);
        }catch(Exception ex){
            log.error("findById: ", ex);
        }finally{
            DAOFactory.commitCurrentSessions();
        }
        return sm;
    }

    public SessionManager getSessionBySessionId(String sessionId){
        SessionManager sm = null;
        try{
            sm = DAOFactory.getSessionManagerDAO().getSessionBySessionId(sessionId);
        }catch(Exception ex){
            log.error("getSessionBySessionId: ", ex);
        }
        return sm;
    }
    public List<SessionManager> getActiveSessionByCP(Long cpId){
        return DAOFactory.getSessionManagerDAO().getActiveSessionByCP(cpId);
    }

    public SessionManager doSave(SessionManager sm){
        SessionManager smUpdated = null;
        try{
            smUpdated = DAOFactory.getSessionManagerDAO().makePersistent(sm);
            DAOFactory.commitCurrentSessions();
        }catch(Exception ex){
            log.error("doSave: ", ex);
        }
        return smUpdated;
    }
}
