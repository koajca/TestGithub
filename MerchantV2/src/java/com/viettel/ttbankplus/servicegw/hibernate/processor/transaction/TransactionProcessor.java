/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.processor.transaction;

import com.viettel.bankplus.merchantgw.beans.entities.Transaction;
import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransBankplus;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransCp;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransferMerchant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since 21-11-2011
 */
public class TransactionProcessor {

    private static volatile TransactionProcessor instance;
    private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TransactionProcessor.class);

    public static TransactionProcessor getInstance() {
        if (instance == null) {
            instance = new TransactionProcessor();
        }
        return instance;
    }

    public TransCp findById(Long transCpId) {
        TransCp transCp = null;
        try {
            transCp = DAOFactory.getTransCPDAO().findById(transCpId, false);
            if (transCp != null) {
                DAOFactory.refreshObj(transCp);
            }
        } catch (Exception ex) {
            log.error("findById: ", ex);
        }
        return transCp;
    }

    public List<TransCp> getTransCp(Long cpId, String billingCode) {
        List<TransCp> lst = null;
        try {
            lst = DAOFactory.getTransCPDAO().getTransCp(cpId, billingCode);
        } catch (Exception ex) {
            log.error("Error when get TransCp by cp and billingCode: " + ex);
        }
        return lst;
    }

    public boolean isExistOrderId(long cp_id, String orderId) {
        List<TransCp> lst = null;
        try {
            lst = DAOFactory.getTransCPDAO().getTransCpByOrderId(cp_id, orderId);
        } catch (Exception ex) {
            log.error("Error when get TransCp by cp and order id: " + ex);
        }
        if (lst == null) {
            return false;
        } else {
            return !lst.isEmpty();
        }
    }

    public TransCp doSave(TransCp trans) {
        TransCp transUpdated = null;
        try {
            transUpdated = DAOFactory.getTransCPDAO().makePersistent(trans);
            DAOFactory.commitCurrentSessions();
        } catch (Exception ex) {
            transUpdated = null;
            log.error("doSave: ", ex);
        }
        return transUpdated;
    }

    public TransBankplus doSaveTransBank(TransBankplus trans) {
        TransBankplus transUpdated = null;
        try {
            transUpdated = DAOFactory.getTransBankplusDAO().makePersistent(trans);
            DAOFactory.commitCurrentSessions();
        } catch (Exception ex) {
            log.error("doSave: ", ex);
        }
        return transUpdated;
    }

    public TransCp getTransBy(Long cpId, String transId) {
        TransCp transCp = null;
        try {
            transCp = DAOFactory.getTransCPDAO().getTransBy(cpId, transId);
            if (transCp != null) {
                DAOFactory.refreshObj(transCp);
            }
        } catch (Exception ex) {
            log.error("getTransBy: ", ex);
        }
        return transCp;
    }

    public TransCp getTransByTransId(String transId) {
        TransCp transCp = null;
        try {
            transCp = DAOFactory.getTransCPDAO().getTransByTransId(transId);
            if (transCp != null) {
                DAOFactory.refreshObj(transCp);
            }
        } catch (Exception ex) {
            log.error("Get trans by transid", ex);
        }
        return transCp;
    }

    public List<TransCp> getTransByOrderId(Long cpId, String orderId) {
        List<TransCp> lst = null;
        try {
            lst = DAOFactory.getTransCPDAO().getTransByOrderId(cpId, orderId);
        } catch (Exception ex) {
            log.error("Error when get TransCp by cpId and orderID: " + ex);
        }
        return lst;
    }

    public List<TransCp> getListTransBySecondCpIdAndOrderId(Long cpId, String orderId) {
        List<TransCp> lst = null;
        try {
            lst = DAOFactory.getTransCPDAO().getListTransBySecondCpIdAndOrderId(cpId, orderId);
        } catch (Exception ex) {
            log.error("Error when get TransCp by cpId and orderID: " + ex);
        }
        return lst;
    }

    public List<Transaction> getTrans(String where, HashMap param) {
        return DAOFactory.getTransCPDAO().getTrans(where, param);
    }

//    public List<Transaction> getTransEVN( Date toDate, Date fromDate, String bankCode) {
//        return DAOFactory.getTransCPDAO().getTransEVN(toDate,fromDate,bankCode);
//    }
    public List<Map<String, Object>> getTransEVN(Date toDate, Date fromDate, String bankCode) {
        return DAOFactory.getTransCPDAO().getTransEVN2(toDate, fromDate, bankCode);
    }

    public List<TransCp> getTransByOriginTransId(String transId) {
        return DAOFactory.getTransCPDAO().getTransByOriginTransId(transId);
    }

    public List<Map<String, Object>> getReportTotal(Date fromDate, Date toDate, String by, String bankCode, ArrayList<Long> lstCpId) {
        return DAOFactory.getTransCPDAO().getReportTotal(fromDate, toDate, by, bankCode, lstCpId);
    }

    public List<Map<String, Object>> getReportTotalEVN(Date fromDate, Date toDate, String by, String bankCode, Long cpId) {
        return DAOFactory.getTransCPDAO().getReportTotalEVN(fromDate, toDate, by, bankCode, cpId);
    }

    public List<Map<String, Object>> getReportRefund(Date fromDate, Date toDate, String by, String bankCode, ArrayList<Long> lstCpId) {
        return DAOFactory.getTransCPDAO().getReportRefund(fromDate, toDate, by, bankCode, lstCpId);
    }

    public TransBankplus getTransBankplusByTransCpId(Long transCpId) {
        return DAOFactory.getTransBankplusDAO().getTransBankplusByTransCpId(transCpId);
    }

    public List<Map<String, Object>> getReportEVNNPC(Date fromDate, Date toDate, String status, ArrayList<Long> lstCpId) {
        return DAOFactory.getTransCPDAO().getReportEVNNPC(fromDate, toDate, status, lstCpId);
    }

    public List<TransferMerchant> getListTransfer(Date fromDate, Date toDate, ArrayList<String> lstCpCode) {
        return DAOFactory.getTransferMerchantDAO().getListTransfer(fromDate, toDate, lstCpCode);
    }

    public List<HashMap> getReport(String sql, HashMap param) {
        return DAOFactory.getTransCPDAO().getReport(sql, param);
    }
}
