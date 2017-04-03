/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ttbankplus.servicegw.hibernate.processor.transbankplusprocessor;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.BillingService;
import com.viettel.ttbankplus.servicegw.hibernate.dao.transaction.BillingServiceDAO;
import org.apache.log4j.Logger;

/**
 *
 * @author LongTH1
 */
public class BillingServiceProcessor {

    Logger log = Logger.getLogger(BillingServiceProcessor.class);
    private static BillingServiceProcessor instance;

    public static BillingServiceProcessor getInstance() {
        if (instance == null) {
            instance = new BillingServiceProcessor();
        }
        return instance;
    }

    public  BillingService doSave(BillingService billingService) {
        BillingService billingServiceUpdated = null;
        try {
            billingServiceUpdated = DAOFactory.getBillingServiceDAO().makePersistent(billingService);
        } catch (Exception ex) {
            log.error("doSave: ", ex);
        }finally{
            DAOFactory.commitCurrentSessions();
        }
        return billingServiceUpdated;
    }
    
    public static void main(String[] args) {
        BillingService billService = new BillingService();
        billService.setTransBankplusId(Long.parseLong("400"));
        billService.setOrderId("test1");
        billService.setAmount("test1");
        
        billService = BillingServiceProcessor.getInstance().doSave(billService);
        System.out.println("billService.id = "+billService.getBillingServiceId());
        
    }
}
