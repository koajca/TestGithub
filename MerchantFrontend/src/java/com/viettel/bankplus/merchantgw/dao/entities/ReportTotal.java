/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.dao.entities;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 19, 2014
 * @version 1.0
 */
public class ReportTotal implements java.io.Serializable{
    private String BANKCODE;
    private String MONDAY;
    private String TOTALPAY;
    private String TOTALREFUND;
    private String PAYAMOUNT;
    private String REFUNDAMOUNT;

    public String getBANKCODE() {
        return BANKCODE;
    }

    public void setBANKCODE(String BANKCODE) {
        this.BANKCODE = BANKCODE;
    }

    public String getMONDAY() {
        return MONDAY;
    }

    public void setMONDAY(String MONDAY) {
        this.MONDAY = MONDAY;
    }

    public String getTOTALPAY() {
        return TOTALPAY;
    }

    public void setTOTALPAY(String TOTALPAY) {
        this.TOTALPAY = TOTALPAY;
    }

    public String getTOTALREFUND() {
        return TOTALREFUND;
    }

    public void setTOTALREFUND(String TOTALREFUND) {
        this.TOTALREFUND = TOTALREFUND;
    }

    public String getPAYAMOUNT() {
        return PAYAMOUNT;
    }

    public void setPAYAMOUNT(String PAYAMOUNT) {
        this.PAYAMOUNT = PAYAMOUNT;
    }

    public String getREFUNDAMOUNT() {
        return REFUNDAMOUNT;
    }

    public void setREFUNDAMOUNT(String REFUNDAMOUNT) {
        this.REFUNDAMOUNT = REFUNDAMOUNT;
    }

}
