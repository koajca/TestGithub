/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.form;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 11, 2014
 * @version 1.0
 */
public class TransactionForm extends BaseForm{
    String fromDate; //dd/MM/yyyy
    String toDate; //dd/MM/yyyy
    String transId;
    String orderId;
    String billingCode;
    String bankCode;
    String transStatus;
    String contentProviderId;
//    PHUCPT edit Jan, 06th 2017    
    String bankRequest;

    public String getBankRequest() {
        return bankRequest;
    }

    public void setBankRequest(String bankRequest) {
        this.bankRequest = bankRequest;
    }

    public String getContentProviderId() {
        return contentProviderId;
    }

    public void setContentProviderId(String contentProviderId) {
        this.contentProviderId = contentProviderId;
    }
    
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBillingCode() {
        return billingCode;
    }

    public void setBillingCode(String billingCode) {
        this.billingCode = billingCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }
    
}
