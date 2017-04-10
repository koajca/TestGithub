/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.processor;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.processor.provider.ContentProviderProcessor;
import com.viettel.ttbankplus.servicegw.hibernate.processor.provider.SessionProcessor;
import com.viettel.ttbankplus.servicegw.hibernate.processor.transaction.TransactionProcessor;
import com.viettel.ttbankplus.servicegw.hibernate.processor.transbankplusprocessor.BillingServiceProcessor;
import com.viettel.ttbankplus.servicegw.hibernate.processor.transbankplusprocessor.HoaDonNuocProcessor;
import com.viettel.ttbankplus.servicegw.hibernate.processor.transbankplusprocessor.TransBankplusProcessor;
import com.viettel.ttbankplus.servicegw.hibernate.processor.users.UsersProcessor;
import com.viettel.ttbankplus.servicegw.hibernate.processor.warehouse.WarehouseProcessor;
import java.io.Serializable;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since 14-07-2011
 */
public class DBProcessor implements Serializable {

    public void refreshObject(Object obj) {
        DAOFactory.refreshObj(obj);
    }

    public ContentProviderProcessor getProviderProcessor() {
        return ContentProviderProcessor.getInstance();
    }

    public TransactionProcessor getTransactionProcessor() {
        return TransactionProcessor.getInstance();
    }

    public TransBankplusProcessor getTransBankplusProcessor() {
        return TransBankplusProcessor.getInstance();
    }

    public String getNextSequence(String seqName) {
        String nextSeqName = null;
        try {
            Session session = DAOFactory.getCurrentSession();
            Query query = session.createSQLQuery("select " + seqName + ".nextval from dual");
            nextSeqName = query.uniqueResult().toString();
        } catch (Exception ex) {
            Logger.getLogger(DBProcessor.class).error("get next sequence error", ex);
        }
        return nextSeqName;
    }

    public BillingServiceProcessor getBillingServiceProcessor() {
        return BillingServiceProcessor.getInstance();
    }

    public HoaDonNuocProcessor getHoaDonNuocProcessor() {
        return HoaDonNuocProcessor.getInstance();
    }

    public UsersProcessor getUsersProcessor() {
        return UsersProcessor.getInstance();
    }

    public SessionProcessor getSessionProcessor() {
        return SessionProcessor.getInstance();
    }

    public WarehouseProcessor getWarehouceProcessor() {
        return WarehouseProcessor.getInstance();
    }
}
