/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.provider;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.SessionManager;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since Oct 28, 2011
 */
public class SessionManagerDAO extends GenericDAO<SessionManager, Long> {

    public SessionManagerDAO(Class<SessionManager> persistentClass, Session session) {
        super(persistentClass, session);
    }
    public static SessionManagerDAO getInstance(Class<SessionManager> persistentClass,
            Session session) {
        return new SessionManagerDAO(persistentClass, session);
    }

    public SessionManager getSessionBySessionId(String sessionId) {
        SessionManager result = (SessionManager) getSession().createCriteria(SessionManager.class)
                .add(Expression.eq("sessionId", sessionId))
                .uniqueResult();
//        PHUCPT edit Jan, 05th 2017
        DAOFactory.commitCurrentSessions();
//        end edit
        return result;
    }

    public List<SessionManager> getActiveSessionByCP(Long cpId) {
        List<SessionManager> result = getSession().createCriteria(SessionManager.class)
                .add(Restrictions.eq("contentProviderId", cpId))
                .add(Restrictions.eq("status", new Long(1)))
                .list();
        //        PHUCPT edit Jan, 05th 2017
        DAOFactory.commitCurrentSessions();
//        end edit
        return result;
    }

}
