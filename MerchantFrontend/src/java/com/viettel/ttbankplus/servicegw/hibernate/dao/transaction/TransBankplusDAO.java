/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.transaction;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransBankplus;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since Nov 3, 2011
 */
public class TransBankplusDAO extends GenericDAO<TransBankplus, Long> {

    public TransBankplusDAO(Class<TransBankplus> persistentClass, Session session) {
        super(persistentClass, session);
    }

    public TransBankplus getTransBankplusByVtRequestId(String requestId) {
        TransBankplus result = (TransBankplus) getSession().createCriteria(TransBankplus.class)
                .add(Expression.eq("requestId", requestId).ignoreCase())
                .uniqueResult();
        //        PHUCPT edit Jan, 05th 2017
        DAOFactory.commitCurrentSessions();
//        end edit
        return result;
    }

    public TransBankplus getPaymentTransBankplusByTransCpId(Long transCpId) {
        TransBankplus result = (TransBankplus) getSession().createCriteria(TransBankplus.class)
                .add(Restrictions.eq("transCpId", transCpId))
                .add(Expression.eq("requestMti", "0510"))
                .uniqueResult();
        //        PHUCPT edit Jan, 05th 2017
        DAOFactory.commitCurrentSessions();
         //        end edit
        return result;
    }

    public TransBankplus getTransBankplusByTransCpId(Long transCpId) {
        TransBankplus result = (TransBankplus) getSession().createCriteria(TransBankplus.class)
                .add(Restrictions.eq("transCpId", transCpId))
                .uniqueResult();
//        PHUCPT edit Jan, 05th 2017
        DAOFactory.commitCurrentSessions();
//        end edit
        return result;
    }

    public static TransBankplusDAO getInstance(Class<TransBankplus> persistentClass,
            Session session) {
        return new TransBankplusDAO(persistentClass, session);
    }
}
