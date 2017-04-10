/*
 * Copyright (C) 2017 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.users;

import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.UsersPassHis;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Feb 20, 2017
 * @version 1.0
 */
public class UsersPassHisDAO extends GenericDAO<UsersPassHis, Long> {

    public UsersPassHisDAO(Class<UsersPassHis> persistentClass, Session session) {
        super(persistentClass, session);
    }

    public static UsersPassHisDAO getInstance(Class<UsersPassHis> persistentClass,
            Session session) {
        return new UsersPassHisDAO(persistentClass, session);
    }

    public List<UsersPassHis> getFiveRecentPassHis(Long userId) {
        Criteria crit = getSession().createCriteria(UsersPassHis.class)
                .add(Restrictions.eq("userId", userId))
                .addOrder(Order.desc("createdTime"))
                .setFirstResult(0)
                .setFetchSize(5);
        List<UsersPassHis> lst = crit.list();
        return lst;
    }
}
