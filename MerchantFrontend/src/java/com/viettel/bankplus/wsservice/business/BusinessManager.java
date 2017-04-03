/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.wsservice.business;

import com.viettel.common.util.DateTimeUtils;
import com.viettel.common.util.StringProcess;
import com.viettel.security.PassTranformer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author cuongdv3@viettel.com.vn
 * @since Nov 13, 2014
 * @version 1.0
 */
public class BusinessManager extends Thread {

    static ConcurrentHashMap<String, List<BaseBusiness>> LIST_BUSINESS;
    static ConcurrentHashMap<String, String> HASH_RESPONSE;
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BusinessManager.class);
    static LinkedBlockingQueue<RequestData> REQUEST_DATA;
    static BusinessManager instance = null;
    int time_loop = 1200;
    int wait_time = 100;

    public static BusinessManager getInstance() {
        if (instance == null) {
            instance = new BusinessManager();
            instance.startBusiness();
        }
        return instance;
    }

    public BusinessManager() {
        LIST_BUSINESS = new ConcurrentHashMap<String, List<BaseBusiness>>();
        REQUEST_DATA = new LinkedBlockingQueue<RequestData>();
        HASH_RESPONSE = new ConcurrentHashMap<String, String>();
    }

    private void initBusiness() {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("pincodebusiness");
            int num_bus = StringProcess.convertToInt(rb.getString("num_business"));
            time_loop = StringProcess.convertToInt(rb.getString("time_loop"));
            wait_time = StringProcess.convertToInt(rb.getString("wait_time"));
            DataUtils.VIETTEL_PUBLIC_KEY = PassTranformer.decrypt(rb.getString("VIETTEL_PUBLIC_KEY"));
            DataUtils.VIETTEL_PRIVATE_KEY = PassTranformer.decrypt(rb.getString("VIETTEL_PRIVATE_KEY"));
            DataUtils.MAX_QUANTITY = StringProcess.convertToInt(rb.getString("max_quantity"));
            for (int i = 0; i < num_bus; i++) {
                String clsName = rb.getString("bs." + i + ".classname");
                int workerSize = StringProcess.convertToInt(rb.getString("bs." + i + ".workersize"));
                String command = rb.getString("bs." + i + ".command");
                String workername = rb.getString("bs." + i + ".workername");
                List<BaseBusiness> lstBus = new ArrayList<BaseBusiness>();
                for (int j = 0; j < workerSize; j++) {
                    Class cls = Class.forName(clsName);
                    BaseBusiness bus = (BaseBusiness) cls.newInstance();
                    bus.setWorkerName(workername + "_" + j);
                    bus.start();
                    lstBus.add(bus);
                }
                LIST_BUSINESS.put(command, lstBus);
            }
            
            //Load PINCODE Amount list
            String pinAmount = rb.getString("pincode_amount");
            if(pinAmount != null && !pinAmount.equals("")){
                String[] arrTmp = pinAmount.split(",");
                if(arrTmp != null && arrTmp.length > 0){
                    for (int i = 0; i < arrTmp.length; i++) {
                        String string = arrTmp[i];
                        DataUtils.LIST_PINCODE_AMOUNT.add(string);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("initBusiness", ex);
        }
    }

    public void onRequest(RequestData req) {
        REQUEST_DATA.add(req);
    }

    private void startBusiness() {
        initBusiness();
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!REQUEST_DATA.isEmpty()) {
                    log.info("getting message from queue...");
                    RequestData req = REQUEST_DATA.take();
                    String command = req.getCmd();
                    if (LIST_BUSINESS.containsKey(command)) {
                        List<BaseBusiness> lstBus = LIST_BUSINESS.get(command);
                        BaseBusiness bus = getBestWorker(lstBus);
                        log.info("Using worker: " + bus.getWorkerName() + " - " + bus.getName());
                        bus.onRequest(req);
                    } else {
                        log.info(command + " - command not supported...do nothing");
                    }
                } else {
                    Thread.sleep(1);
                }
            } catch (Exception ex) {
                log.error("run", ex);
            }
        }
    }

    protected BaseBusiness getBestWorker(List<BaseBusiness> listBus) {
        BaseBusiness result = listBus.get(0);
        String info = "";
        int min = result.getSize();
        for (BaseBusiness baseBusiness : listBus) {
            info += "Worker: " + baseBusiness.getWorkerName() + " - Current Size: " + baseBusiness.getSize() + "\n";
            if (baseBusiness.getSize() < min) {
                min = baseBusiness.getSize();
                result = baseBusiness;
            }
        }
        log.info(info);
        return result;
    }
    
    public String createResponseTimeout(){
        HashMap<String, String> responseData = new HashMap<String, String>();
        responseData.put(DataUtils.JSON_KEY.error_code.toString(), BaseBusiness.ERROR_CODE.ERROR_32.getErrorCode());
        responseData.put(DataUtils.JSON_KEY.error_msg.toString(), BaseBusiness.ERROR_CODE.ERROR_32.getErrorDesc());
        responseData.put(DataUtils.JSON_KEY.trans_date.toString(), DateTimeUtils.convertDateToString(Calendar.getInstance().getTime(), "yyMMddHHmmss"));
        HashMap<String, Object> hashRes = new HashMap<String, Object>();
        hashRes.put(DataUtils.JSON_KEY.data.toString(), responseData);
        String sign = DataUtils.signData(DataUtils.toJsonObjectString(responseData));
        hashRes.put(DataUtils.JSON_KEY.signature.toString(), sign);
        String strJson = DataUtils.toJsonObjectString(hashRes);
        log.info(strJson);
        return strJson;
    }

    public String getResponse(String uuid) {
        int count = 0;
        while (true) {
            if (HASH_RESPONSE.containsKey(uuid)) {
                // get ResponseObject
                String obj = HASH_RESPONSE.get(uuid);
                HASH_RESPONSE.remove(uuid);
                return obj;
            }
            try {
                Thread.sleep(wait_time);
                count++;
            } catch (Exception e) {
                log.error("getResponse", e);
            }

            if (count > time_loop) {
                return createResponseTimeout();
            }
        }
    }

}
