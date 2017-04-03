/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.wsservice.business;

import java.util.UUID;

/**
 * @author cuongdv3@viettel.com.vn
 * @since Nov 14, 2014
 * @version 1.0
 */
public class RequestData {

    private String uuid;
    private String cmd;
    private String encrypted;
    private String Signature;
    private String ipAddress;
    private String responseJsonData;

    public RequestData() {
        UUID uid = UUID.randomUUID();
        this.uuid = uid.toString().replace("-", "");
    }
    
    public RequestData(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String Signature) {
        this.Signature = Signature;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getResponseJsonData() {
        return responseJsonData;
    }

    public void setResponseJsonData(String responseJsonData) {
        this.responseJsonData = responseJsonData;
    }

}
