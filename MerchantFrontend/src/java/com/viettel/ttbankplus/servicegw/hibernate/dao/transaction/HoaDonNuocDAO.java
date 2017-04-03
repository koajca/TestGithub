/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.transaction;

import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.HoaDonNuoc;
import org.hibernate.Session;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 04, 2014
 * @version 1.0
 */
public class HoaDonNuocDAO extends GenericDAO<HoaDonNuoc, Long> {

    public HoaDonNuocDAO(Class<HoaDonNuoc> persistentClass, Session session) {
        super(persistentClass, session);
    }

    public static HoaDonNuocDAO getInstance(Class<HoaDonNuoc> persistentClass,
            Session session) {
        return new HoaDonNuocDAO(persistentClass, session);
    }
}
