/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.wsservice.business;

import com.viettel.common.util.StringProcess;
import com.viettel.common.util.security.EncryptManager;
import com.viettel.security.PassTranformer;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.SessionManager;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransCp;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

/**
 * @author cuongdv3@viettel.com.vn
 * @since Nov 13, 2014
 * @version 1.0
 */
public class BaseBusiness extends Thread {
    
    protected LinkedBlockingQueue<RequestData> QUEUE_REQUEST = new LinkedBlockingQueue<RequestData>();
    Logger log = Logger.getLogger(BaseBusiness.class);
    protected HashMap<String, String> hashClearData = new HashMap<String, String>();
    protected HashMap<String, String> responseData = new HashMap<String, String>();
    protected static HashMap<String, SessionManager> HASH_CP_SESSION = new HashMap<String, SessionManager>();
    public DBProcessor dbProcessor = new DBProcessor();
    private String workername = "";
    
    public String getWorkerName() {
        return this.workername;
    }
    
    public void setWorkerName(String workername) {
        this.workername = workername;
    }
    
    public int getSize() {
        synchronized (this.QUEUE_REQUEST) {
            return this.QUEUE_REQUEST.size();
        }
    }
    
    public void process(RequestData request) {
        throw new UnsupportedOperationException("");
    }
    
    public void onRequest(RequestData req) {
        try {
            QUEUE_REQUEST.put(req);
        } catch (InterruptedException ex) {
            log.error("onRequest", ex);
        }
    }
    
    public void onResponse(RequestData req) {
        BusinessManager.HASH_RESPONSE.put(req.getUuid(), req.getResponseJsonData());
    }
    
    public String createResponseJson() {
        HashMap<String, Object> hashRes = new HashMap<String, Object>();
        hashRes.put(DataUtils.JSON_KEY.data.toString(), this.responseData);
        String sign = DataUtils.signData(DataUtils.toJsonObjectString(this.responseData));
        hashRes.put(DataUtils.JSON_KEY.signature.toString(), sign);
        String strJson = DataUtils.toJsonObjectString(hashRes);
        log.info(strJson);
        return strJson;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                if (!QUEUE_REQUEST.isEmpty()) {
                    RequestData req = QUEUE_REQUEST.take();
                    initializeComponent();
                    process(req);
                } else {
                    Thread.sleep(1);
                }
            } catch (Exception ex) {
                log.error("run", ex);
            }
        }
    }
    
    private void initializeComponent() {
        if (hashClearData == null) {
            hashClearData = new HashMap<String, String>();
        } else {
            hashClearData.clear();
        }
        if (responseData == null) {
            responseData = new HashMap<String, String>();
        } else {
            responseData.clear();
        }
    }
    
    protected boolean isValidData(RequestData req, Map validateKey) {
        String clearData = DataUtils.decryptData(req.getEncrypted());
        if (clearData == null || clearData.equals("")) {
            log.debug("ClearData is null or empty");
            this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_01.getErrorCode());
            this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_01.getErrorDesc());
            return false;
        }
        this.hashClearData = DataUtils.getHashData(clearData);
        if (this.hashClearData == null || this.hashClearData.isEmpty()) {
            log.debug("HashData is empty");
            this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_01.getErrorCode());
            this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_01.getErrorDesc());
            return false;
        } else {
            HashMap<String, String> mapClone = (HashMap<String, String>) this.hashClearData.clone();
            if (mapClone.containsKey(DataUtils.JSON_KEY.password.toString())) {
                mapClone.put(DataUtils.JSON_KEY.password.toString(), "********");
            }
            log.info(req.getCmd() + " REQ: " + mapClone);
        }

        //Kiem tra day du thong tin hay khong
        for (Object object : validateKey.keySet()) {
            String key = object.toString();
            if (!this.hashClearData.containsKey(key)) {
                log.debug("Thong tin khong day du");
                this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_01.getErrorCode());
                this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_01.getErrorDesc());
                return false;
            }
        }

        //Kiem tra menh gia tien
        if (validateKey.containsKey(DataUtils.JSON_KEY.amount.toString()) && !DataUtils.isValidAmount(this.hashClearData.get(DataUtils.JSON_KEY.amount.toString()))) {
            log.debug("Menh gia tien khong dung");
            this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_04.getErrorCode());
            this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_04.getErrorDesc());
            return false;
        }

        //Kiem tra so luong
        if (validateKey.containsKey(DataUtils.JSON_KEY.quantity.toString())) {
            int quantity = StringProcess.convertToInt(this.hashClearData.get(DataUtils.JSON_KEY.quantity.toString()));
            if (quantity <= 0 || quantity > DataUtils.MAX_QUANTITY) {
                log.debug("So luong khong dung quy dinh: " + quantity);
                this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_06.getErrorCode());
                this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_06.getErrorDesc());
                return false;
            }
        }
        
        ContentProvider cp = null;
        if (validateKey.containsKey(DataUtils.JSON_KEY.username.toString())) {
            //Kiem tra thong tin CP
            String username = this.hashClearData.get(DataUtils.JSON_KEY.username.toString());
            String password = this.hashClearData.get(DataUtils.JSON_KEY.password.toString());
            cp = dbProcessor.getProviderProcessor().getProviderByUsername(username);
            if (cp == null || cp.getStatus() != 1L) {
                log.debug("CP khong ton tai hoac khong hoat dong");
                this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_01.getErrorCode());
                this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_01.getErrorDesc());
                return false;
            }

            //Kiem tra thong tin IP
            String dbIP = cp.getIp();
            String[] arrIP = dbIP.split(",");
            boolean isIpOK = false;
            for (int i = 0; i < arrIP.length; i++) {
                if (arrIP[i].equals(req.getIpAddress()) || arrIP[i].equals("0.0.0.0")) {
                    isIpOK = true;
                    break;
                }
            }
            if (!isIpOK) {
                log.debug("IP " + req.getIpAddress() + " khong dung. IP DB: " + dbIP);
                this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_01.getErrorCode());
                this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_01.getErrorDesc());
                return false;
            }

            //Kiem tra thong tin chu ky
            String cpPublicKey = "";
            try {
                cpPublicKey = PassTranformer.decrypt(cp.getCpPublicKey());
            } catch (Exception ex) {
            }
            if (!DataUtils.verifySign(clearData, req.getSignature(), cpPublicKey)) {
                log.debug("Chu ky khong dung");
                this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_01.getErrorCode());
                this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_01.getErrorDesc());
                return false;
            }
            
            String passwordDB = cp.getPassword();
            String passwordInput = "";
            try {
                passwordInput = EncryptManager.md5Encrypt(EncryptManager.SHA1(password));
            } catch (Exception ex) {
                
            }
            if (!passwordDB.equalsIgnoreCase(passwordInput)) {
                log.debug("password invalid");
                this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_01.getErrorCode());
                this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_01.getErrorDesc());
                return false;
            }
        }
        
        if (validateKey.containsKey(DataUtils.JSON_KEY.order_id.toString())) {
            
            if (this.hashClearData.get(DataUtils.JSON_KEY.order_id.toString()).length() > 50) {
                log.debug("order_id vuot qua quy dinh");
                this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_01.getErrorCode());
                this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_01.getErrorDesc());
                return false;
            }
            
            if (validateKey.get(DataUtils.JSON_KEY.order_id.toString()).equals(DataUtils.VALIDATOR.duplicate.toString())) {
                //Check trung giao dich
                List<TransCp> lstTransCp = dbProcessor.getTransactionProcessor().getTransByOrderId(cp.getContentProviderId(), this.hashClearData.get(DataUtils.JSON_KEY.order_id.toString()));
                if (lstTransCp != null && lstTransCp.size() > 0) {
                    log.debug("order_id trung lap");
                    this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_05.getErrorCode());
                    this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_05.getErrorDesc());
                    return false;
                }
            }
            
            if (validateKey.get(DataUtils.JSON_KEY.order_id.toString()).equals(DataUtils.VALIDATOR.exist.toString())) {
                //Check giao dich ton tai
                List<TransCp> lstTransCp = dbProcessor.getTransactionProcessor().getTransByOrderId(cp.getContentProviderId(), this.hashClearData.get(DataUtils.JSON_KEY.order_id.toString()));
                if (lstTransCp == null || lstTransCp.isEmpty()) {
                    log.debug("order_id khong ton tai");
                    this.responseData.put(DataUtils.JSON_KEY.error_code.toString(), ERROR_CODE.ERROR_02.getErrorCode());
                    this.responseData.put(DataUtils.JSON_KEY.error_msg.toString(), ERROR_CODE.ERROR_02.getErrorDesc());
                    return false;
                }
            }
        }
        return true;
    }
    
    public enum ERROR_CODE {
        
        ERROR_00("00", "Giao dich thanh cong", "SUCCESS"),
        ERROR_32("32", "Giao dich dang xu ly", "TIMEOUT"),
        ERROR_01("01", "Thong tin doi tac khong dung (IP, username, password, signature)", "FAILURE"),
        ERROR_02("02", "Giao dich khong ton tai", "FAILURE"),
        ERROR_03("03", "Het the", "FAILURE"),
        ERROR_04("04", "Menh gia tien khong ho tro", "FAILURE"),
        ERROR_05("05", "Giao dich trung lap", "FAILURE"),
        ERROR_06("06", "So luong vuot qua han muc cho phep", "FAILURE"),
        ERROR_07("07", "So luong trong kho khong du giao dich", "FAILURE"),
        ERROR_23("23", "Giao dich that bai", "FAILURE"),
        ERROR_98("98", "He thong dang nang cap, bao duong", "FAILURE");
        private String errorCode;
        private String errorDesc;
        private String errorType;
        
        private ERROR_CODE(String errorCode, String errorDesc, String errorType) {
            this.errorCode = errorCode;
            this.errorDesc = errorDesc;
            this.errorType = errorType;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
        
        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }
        
        public String getErrorDesc() {
            return errorDesc;
        }
        
        public void setErrorDesc(String errorDesc) {
            this.errorDesc = errorDesc;
        }
        
        public String getErrorType() {
            return errorType;
        }
        
        public void setErrorType(String errorType) {
            this.errorType = errorType;
        }
    }
    
    public SessionManager doOpenSession(String username, Long cpId) {
        SessionManager sm;
        if (HASH_CP_SESSION.containsKey(username)) {
            sm = HASH_CP_SESSION.get(username);
        } else {
            if (cpId == null || cpId.intValue() == 0) {
                ContentProvider cp = dbProcessor.getProviderProcessor().getProviderByUsername(username);
                cpId = cp.getContentProviderId();
            }
            
            UUID uid = UUID.randomUUID();
            String sId = uid.toString().replace("-", "").toUpperCase();
            sm = new SessionManager();
            sm.setContentProviderId(cpId);
            sm.setSessionId(sId);
            sm.setStartTime(Calendar.getInstance().getTime());
            sm.setUpdateTime(Calendar.getInstance().getTime());
            sm.setStatus(1L);
            sm = dbProcessor.getSessionProcessor().doSave(sm);
            HASH_CP_SESSION.put(username, sm);
        }
        
        return sm;
    }
}
