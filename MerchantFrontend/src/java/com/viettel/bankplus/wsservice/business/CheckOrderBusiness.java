/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.wsservice.business;

import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.DateTimeUtils;
import com.viettel.common.util.StringProcess;
import com.viettel.common.util.TransactionStatus;
import com.viettel.common.util.TransactionType;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.SessionManager;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransCp;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @author cuongdv3@viettel.com.vn
 * @since Nov 14, 2014
 * @version 1.0
 */
public class CheckOrderBusiness extends BaseBusiness {

    public CheckOrderBusiness() {
        super();
        log = Logger.getLogger(CheckOrderBusiness.class);
    }

    @Override
    public void process(RequestData request) {
        if (isValidCheckOrder(request)) {
            this.responseData.put(DataUtils.JSON_KEY.order_id.toString(), this.hashClearData.get(DataUtils.JSON_KEY.order_id.toString()));
            this.responseData.put(DataUtils.JSON_KEY.username.toString(), this.hashClearData.get(DataUtils.JSON_KEY.username.toString()));

            //Luu thong tin vao CSDL
            ContentProvider cp = dbProcessor.getProviderProcessor().getProviderByUsername(this.hashClearData.get(DataUtils.JSON_KEY.username.toString()));
            SessionManager sm = doOpenSession(this.hashClearData.get(DataUtils.JSON_KEY.username.toString()), cp.getContentProviderId());
//            TransCp oldTransCp = dbProcessor.getTransactionProcessor().getTransByOrderId(cp.getContentProviderId(), this.hashClearData.get(DataUtils.JSON_KEY.order_id.toString())).get(0);
            List<TransCp> lstOldTransCp = dbProcessor.getTransactionProcessor().getTransByOrderId(cp.getContentProviderId(), this.hashClearData.get(DataUtils.JSON_KEY.order_id.toString()));
            TransCp oldTransCp = null;
            for (TransCp transCp : lstOldTransCp) {
                if (transCp.getTransType().compareTo(TransactionType.CHECKOUT_PINCODE.getLongVal()) == 0) {
                    oldTransCp = transCp;
                    break;
                }
            }
            if (oldTransCp == null) {
                log.debug("order_id khong ton tai");
                this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_02.getErrorCode());
                this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_02.getErrorDesc());
            } else {
                //<editor-fold defaultstate="collapsed" desc="Tao ra transCp va luu vao co so du lieu">
                String id = CommonUtils.generateTransId();
                TransCp transCp = new TransCp();
//                BigDecimal amount = StringProcess.convertToBigDecimal(this.hashClearData.get(DataUtils.JSON_KEY.amount.toString()));
//                transCp.setAmount(amount);
//
//                transCp.setBillingCode(DataUtils.getCodeId(amount.doubleValue()));
//        transCp.setContentProvider(cp);
                transCp.setContentProviderId(cp.getContentProviderId());
                transCp.setMsisdn("");
                transCp.setOrderId(this.hashClearData.get(DataUtils.JSON_KEY.order_id.toString()));
                transCp.setOrderInfo("");
                transCp.setTransId(id);
                transCp.setRequestDate(Calendar.getInstance().getTime());
                transCp.setServiceCode("");
//        transCp.setSessionManager(sm);
                transCp.setSessionManagerId(sm.getSessionManagerId());
                transCp.setTransStatus(TransactionStatus.PENDING.getLongVal().toString());
                transCp.setTransType(TransactionType.QUERY_TRANS_RESULT.getLongVal());
                HashMap mapReqContent = (HashMap) this.hashClearData.clone();
                mapReqContent.put(DataUtils.JSON_KEY.password.toString(), "******");
                transCp.setRequestContent(mapReqContent.toString());

                transCp = dbProcessor.getTransactionProcessor().doSave(transCp);
                //</editor-fold>

                if (transCp != null) {
                    this.responseData.put(DataUtils.JSON_KEY.trans_id.toString(), oldTransCp.getTransId());
                    this.responseData.put(DataUtils.JSON_KEY.amount.toString(), StringProcess.convertToString(oldTransCp.getAmount().longValue()));
                    if (oldTransCp.getErrorCode() == null) {
                        this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_32.getErrorCode());
                        this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_32.getErrorDesc());
                    } else if (oldTransCp.getErrorCode().equals(ERROR_CODE.ERROR_00.getErrorCode())) {
                        //Giao dich goc da thanh cong => Tra ket qua luon
                        this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_00.getErrorCode());
                        this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_00.getErrorDesc());
                    } else {
                        this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), oldTransCp.getErrorCode());
                        this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), oldTransCp.getErrorMsg());
                    }
                    transCp.setErrorCode(this.responseData.get(DataUtils.JSON_KEY.error_code.toString()));
                    transCp.setErrorMsg(this.responseData.get(DataUtils.JSON_KEY.error_msg.toString()));
                    if (this.responseData.get(DataUtils.JSON_KEY.error_code.toString()).equals(ERROR_CODE.ERROR_00.getErrorCode())) {
                        transCp.setTransStatus(TransactionStatus.COMPLETED.getLongVal().toString());
                    } else if (this.responseData.get(DataUtils.JSON_KEY.error_code.toString()).equals(ERROR_CODE.ERROR_32.getErrorCode())) {
                        transCp.setTransStatus(TransactionStatus.PROCESSING.getLongVal().toString());
                    } else {
                        transCp.setTransStatus(TransactionStatus.FAILED.getLongVal().toString());
                    }
                    transCp.setResponseContent(this.responseData.toString());
                    transCp = dbProcessor.getTransactionProcessor().doSave(transCp);
                } else {
                    log.error("Insert TransCP Failed: " + id);
                    this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_32.getErrorCode());
                    this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_32.getErrorDesc());
                }
            }
        }
        responseData.put(DataUtils.JSON_KEY.trans_date.toString(), DateTimeUtils.convertDateToString(Calendar.getInstance().getTime(), "yyMMddHHmmss"));
        request.setResponseJsonData(createResponseJson());
        onResponse(request);
    }

    private boolean isValidCheckOrder(RequestData req) {
        Map validateKey = new HashMap();
        validateKey.put(DataUtils.JSON_KEY.username.toString(), "");
        validateKey.put(DataUtils.JSON_KEY.password.toString(), "");
        validateKey.put(DataUtils.JSON_KEY.order_id.toString(), DataUtils.VALIDATOR.exist.toString());
        return isValidData(req, validateKey);
    }
}
