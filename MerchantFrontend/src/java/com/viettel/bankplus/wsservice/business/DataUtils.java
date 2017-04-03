/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.wsservice.business;

import com.viettel.common.util.CommonUtils;
import com.viettel.rsa.Crypto;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Warehouse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;

/**
 * @author cuongdv3@viettel.com.vn
 * @since Nov 14, 2014
 * @version 1.0
 */
public class DataUtils {

    static Logger log = Logger.getLogger(DataUtils.class);
    static final String[] LIST_JSON_KEY = new String[]{
        "encrypted",
        "signature",
        "pincode",
        "serial",
        "amount",
        "order_id",
        "trans_id",
        "error_code",
        "error_msg",
        "username",
        "info",
        "data",
        "quantity",
        "trans_date",
        "service_code",
        "password"};

    public static enum JSON_KEY {

        encrypted,
        signature,
        pincode,
        serial,
        amount,
        order_id,
        trans_id,
        error_code,
        error_msg,
        username,
        data,
        info,
        trans_date,
        quantity,
        service_code,
        password;
    }
    
    public static enum VALIDATOR {
        exist,
        duplicate,
        empty;
    }

    public static List<String> LIST_PINCODE_AMOUNT = new ArrayList<String>();
    public static String VIETTEL_PUBLIC_KEY = "";
    public static String VIETTEL_PRIVATE_KEY = "";
    public static int MAX_QUANTITY = 10;

    public static String decryptData(String encrypted) {
        return decryptData(encrypted, VIETTEL_PRIVATE_KEY);
    }

    public static String decryptData(String encrypted, String privateKey) {
        try {
            String data = Crypto.Decrypt(encrypted, privateKey, false);
            return data;
        } catch (Exception ex) {
            log.error("decryptData", ex);
        }
        return "";
    }

    public static String encryptData(String data) {
        return encryptData(data, VIETTEL_PUBLIC_KEY);
    }

    public static String encryptData(String data, String publicKey) {
        try {
            String encrypted = Crypto.Encrypt(data, publicKey, false);
            return encrypted;
        } catch (Exception ex) {
            log.error("decryptData", ex);
        }
        return "";
    }

    public static String signData(String data) {
        return signData(data, VIETTEL_PRIVATE_KEY);
    }

    public static String signData(String data, String privateKey) {
        try {
            String sign = Crypto.Sign(data, privateKey, false);
            sign = sign.replace("\r", "");
            sign = sign.replace("\n", "");
            return sign;
        } catch (Exception ex) {
            log.error("signData", ex);
        }
        return "";
    }

    public static boolean verifySign(String data, String sign, String publicKey) {
        try {
            return Crypto.Verify(data, sign, publicKey, false);
        } catch (Exception ex) {
            log.error("verifySign", ex);
        }
        return false;
    }

    public static boolean isJson(String data) {
        try {
            JSONSerializer.toJSON(data);
            return true;
        } catch (Exception ex) {
            log.error("Not Json Object");
        }
        return false;
    }

    public static HashMap<String, String> getHashData(String jsonData) {
        HashMap<String, String> hash = null;
        if (isJson(jsonData)) {
            JSONObject jObj = (JSONObject) JSONSerializer.toJSON(jsonData);
            hash = toHashMap(jObj);
        }
        return hash;
    }

    public static HashMap toHashMap(JSONObject object) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toHashMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List toList(JSONArray array) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toHashMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static JSONObject toJsonObject(HashMap map) {
        JSONObject jobj = (JSONObject) JSONSerializer.toJSON(map);
        return jobj;
    }

    public static String toJsonObjectString(HashMap map) {
        JSONObject jobj = toJsonObject(map);
        return jobj.toString();
    }

    public static boolean isValidAmount(String amount) {
        if (!CommonUtils.doCheckDigit(amount)) {
            return false;
        }
        if (!LIST_PINCODE_AMOUNT.contains(amount)) {
            return false;
        }
        return true;
    }

    public static enum PINCODE_AMOUNT {

        AMOUNT_5("PINCODE5", 5000.00),
        AMOUNT_10("PINCODE10", 10000.00),
        AMOUNT_20("PINCODE20", 20000.00),
        AMOUNT_30("PINCODE30", 30000.00),
        AMOUNT_50("PINCODE50", 50000.00),
        AMOUNT_100("PINCODE100", 100000.00),
        AMOUNT_200("PINCODE200", 200000.00),
        AMOUNT_300("PINCODE300", 300000.00),
        AMOUNT_500("PINCODE500", 500000.00);

        private String code;
        private Double amount;

        private PINCODE_AMOUNT(String code, Double amount) {
            this.code = code;
            this.amount = amount;

        }

        public String getCode() {
            return code;
        }

        public Double getAmount() {
            return amount;
        }
    }
    
    public static String getJsonArray(List<Warehouse> lstPincode){
        if(lstPincode == null || lstPincode.isEmpty()) return "";
        List<HashMap> lstHashData = new ArrayList<HashMap>();
        for (Warehouse wh : lstPincode) {
            HashMap<String, String> hash = new HashMap<String, String>();
            hash.put(JSON_KEY.serial.toString(), wh.getSerial());
            String pincodeClear = decryptData(wh.getPincode());
//            String pincodeClear = wh.getPincode();
            hash.put(JSON_KEY.pincode.toString(), pincodeClear);
            lstHashData.add(hash);
        }
        JSONArray jArr = (JSONArray) JSONSerializer.toJSON(lstHashData);
        return jArr.toString();
    }

    public static String getCodeId(Double amount) {
        String id = "";
        if (amount != null) {
            if (PINCODE_AMOUNT.AMOUNT_5.getAmount().compareTo(amount) == 0) {
                id = PINCODE_AMOUNT.AMOUNT_5.getCode();
            } else if (PINCODE_AMOUNT.AMOUNT_10.getAmount().compareTo(amount) == 0) {
                id = PINCODE_AMOUNT.AMOUNT_10.getCode();
            } else if (PINCODE_AMOUNT.AMOUNT_20.getAmount().compareTo(amount) == 0) {
                id = PINCODE_AMOUNT.AMOUNT_20.getCode();
            } else if (PINCODE_AMOUNT.AMOUNT_30.getAmount().compareTo(amount) == 0) {
                id = PINCODE_AMOUNT.AMOUNT_30.getCode();
            } else if (PINCODE_AMOUNT.AMOUNT_50.getAmount().compareTo(amount) == 0) {
                id = PINCODE_AMOUNT.AMOUNT_50.getCode();
            } else if (PINCODE_AMOUNT.AMOUNT_100.getAmount().compareTo(amount) == 0) {
                id = PINCODE_AMOUNT.AMOUNT_100.getCode();
            } else if (PINCODE_AMOUNT.AMOUNT_200.getAmount().compareTo(amount) == 0) {
                id = PINCODE_AMOUNT.AMOUNT_200.getCode();
            } else if (PINCODE_AMOUNT.AMOUNT_300.getAmount().compareTo(amount) == 0) {
                id = PINCODE_AMOUNT.AMOUNT_300.getCode();
            } else if (PINCODE_AMOUNT.AMOUNT_500.getAmount().compareTo(amount) == 0) {
                id = PINCODE_AMOUNT.AMOUNT_500.getCode();
            }
        }
        return id;
    }
}
