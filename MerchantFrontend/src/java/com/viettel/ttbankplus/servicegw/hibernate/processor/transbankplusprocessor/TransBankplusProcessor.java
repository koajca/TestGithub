/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.processor.transbankplusprocessor;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransBankplus;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransCp;
import org.apache.log4j.Logger;

/**
 *
 * @author caotv@viettel.com.vn
 * @since Nov 28, 2011
 * @version 1.0
 */
public class TransBankplusProcessor {
    Logger log = Logger.getLogger(TransBankplusProcessor.class);
    private static TransBankplusProcessor instance;

    public static TransBankplusProcessor getInstance() {
        if (instance == null) {
            instance = new TransBankplusProcessor();
        }
        return instance;
    }

    public TransBankplus getTransBankplusByVtRequestId(String requestId){
        TransBankplus transBankplus = null;
        if(requestId != null && !requestId.equals("")){
            try{
                transBankplus = DAOFactory.getTransBankplusDAO().getTransBankplusByVtRequestId(requestId);
                log.debug(transBankplus);
                if(transBankplus != null){
                    DAOFactory.refreshObj(transBankplus);
                }
            }catch(Exception e){
                log.error("Cannot get transBankplus by request id", e);
            }
        }
        return  transBankplus;
    }

    public TransBankplus getPaymentTransBankplusByTransCpId(String transId){
        TransBankplus transBankplus = null;
        if(transId != null && !transId.equals("")){
            try{
                TransCp transCp = DAOFactory.getTransCPDAO().getTransByTransId(transId);
                Long transCpId = transCp.getTransCpId();
                transBankplus = DAOFactory.getTransBankplusDAO().getPaymentTransBankplusByTransCpId(transCpId);
                log.debug(transBankplus);
                if(transBankplus != null){
                    DAOFactory.refreshObj(transBankplus);
                }
            }catch(Exception e){
                log.error("Cannot get transBankplus by trans id", e);
            }
        }
        return  transBankplus;
    }
    
      public TransBankplus getTransBankplusByTransCpId(String transId){
        TransBankplus transBankplus = null;
        if(transId != null && !transId.equals("")){
            try{
                TransCp transCp = DAOFactory.getTransCPDAO().getTransByTransId(transId);
                Long transCpId = transCp.getTransCpId();
                transBankplus = DAOFactory.getTransBankplusDAO().getTransBankplusByTransCpId(transCpId);
                log.debug(transBankplus);
                if(transBankplus != null){
                    DAOFactory.refreshObj(transBankplus);
                }
            }catch(Exception e){
                log.error("Cannot get transBankplus by trans id", e);
            }
        }
        return  transBankplus;
    }

    public TransBankplus doSave(TransBankplus trans) {
        TransBankplus transBankplusUpdated = null;
        try {
            transBankplusUpdated = DAOFactory.getTransBankplusDAO().makePersistent(trans);
            DAOFactory.commitCurrentSessions();
        } catch (Exception ex) {
            log.error("doSave: ", ex);
        }
        return transBankplusUpdated;
    }
}