/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.transaction;

import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.RequestBankplus;
import java.math.BigDecimal;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since Nov 3, 2011
 */
public class RequestBankplusDAO extends GenericDAO<RequestBankplus, Long> {

    public RequestBankplusDAO(Class<RequestBankplus> persistentClass, Session session) {
        super(persistentClass, session);
    }

    public static RequestBankplusDAO getInstance(Class<RequestBankplus> persistentClass,
            Session session) {
        return new RequestBankplusDAO(persistentClass, session);
    }

    public RequestBankplus getRequestBankplusByOrderId(String orderId){
        return (RequestBankplus) getSession().createCriteria(RequestBankplus.class)
                .add(Restrictions.eq("orderId", orderId.trim())).uniqueResult();
    }
}
