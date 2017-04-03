/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.transaction;

import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.BillingService;
import org.hibernate.Session;

/**
 *
 * @author LongTH1
 */
public class BillingServiceDAO extends GenericDAO<BillingService, Long> {

    public BillingServiceDAO(Class<BillingService> persistentClass, Session session) {
        super(persistentClass, session);
    }

    public static BillingServiceDAO getInstance(Class<BillingService> persistentClass,
            Session session) {
        return new BillingServiceDAO(persistentClass, session);
    }
    
    
}
