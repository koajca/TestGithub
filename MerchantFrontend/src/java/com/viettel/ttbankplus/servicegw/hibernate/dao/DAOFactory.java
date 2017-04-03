/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao;

import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.BillingService;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.RequestBankplus;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.HoaDonNuoc;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.SessionManager;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransBankplus;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransCp;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransferMerchant;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Users;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Warehouse;
import com.viettel.ttbankplus.servicegw.hibernate.dao.provider.ContentProviderDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.provider.SessionManagerDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.transaction.BillingServiceDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.transaction.HoaDonNuocDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.transaction.RequestBankplusDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.transaction.TransBankplusDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.transaction.TransCPDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.transaction.TransferMerchantDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.users.UsersDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.warehouse.WarehouseDAO;
import com.viettel.ttbankplus.servicegw.hibernate.utils.HibernateSessionFactory;
import org.hibernate.Session;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since 21-10-2011
 * @version 1.0
 */
public class DAOFactory {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DAOFactory.class);

    public static Session getCurrentSession() {
        Session session = HibernateSessionFactory.getSession();
        return session;
    }

    public static Session getNewSession() {
        return HibernateSessionFactory.getSessionFactory().openSession();
    }

    public static Session getCurrentSessionAndBeginTransaction() {

        try {
//            session.beginTransaction();
            HibernateSessionFactory.beginTransaction();
        } catch (Exception ex) {
            log.error("getCurrentSessionAndBeginTransaction", ex);
        }
        Session session = getCurrentSession();
        return session;
    }

    public static void refreshObj(Object obj) {
        if (obj != null) {
            try {
                getCurrentSession().refresh(obj);
            } catch (Exception ex) {
            } finally {
                commitCurrentSessions();
            }
        }
    }

    public static void commitCurrentSessions() {
        HibernateSessionFactory.commitTransaction();
    }

    public static void closeCurrentSessions() {
        Session session = getCurrentSession();
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (Exception ex) {
            log.error("Lỗi khi đóng session: ", ex);
        }
    }

    public static ContentProviderDAO getContentProviderDAO() {
        return ContentProviderDAO.getInstance(ContentProvider.class, null);
    }

    public static SessionManagerDAO getSessionManagerDAO() {
        return SessionManagerDAO.getInstance(SessionManager.class, null);
    }

    public static TransCPDAO getTransCPDAO() {
        return TransCPDAO.getInstance(TransCp.class, null);
    }

    public static RequestBankplusDAO getRequestBankplusDAO() {
        return RequestBankplusDAO.getInstance(RequestBankplus.class, null);
    }

    public static TransBankplusDAO getTransBankplusDAO() {
        return TransBankplusDAO.getInstance(TransBankplus.class, null);
    }

    public static BillingServiceDAO getBillingServiceDAO() {
        return BillingServiceDAO.getInstance(BillingService.class, null);
    }

    public static HoaDonNuocDAO getHoaDonNuocDAO() {
        return HoaDonNuocDAO.getInstance(HoaDonNuoc.class, null);
    }

    public static UsersDAO getUsersDAO() {
        return UsersDAO.getInstance(Users.class, null);
    }

    public static WarehouseDAO getWarehouseDAO() {
        return WarehouseDAO.getInstance(Warehouse.class, null);
    }

    public static TransferMerchantDAO getTransferMerchantDAO() {
        return TransferMerchantDAO.getInstance(TransferMerchant.class, null);
    }
}
