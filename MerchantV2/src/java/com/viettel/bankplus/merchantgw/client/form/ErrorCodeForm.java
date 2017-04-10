/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bankplus.merchantgw.client.form;

import java.io.Serializable;

/**
 *
 * @author os_phucpt1
 */
public class ErrorCodeForm implements Serializable{
    private String errorCodeId;
    private String errorCode;

    public String getErrorCodeId() {
        return errorCodeId;
    }

    public void setErrorCodeId(String errorCodeId) {
        this.errorCodeId = errorCodeId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
