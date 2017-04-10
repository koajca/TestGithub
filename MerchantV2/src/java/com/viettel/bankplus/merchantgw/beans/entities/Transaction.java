/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.beans.entities;

import java.math.BigDecimal;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 11, 2014
 * @version 1.0
 */
public class Transaction implements java.io.Serializable {

    private String TRANSID;
    private String ORIGINALTRANSID;
    private BigDecimal CONTENTPROVIDERID;
    private String REQUESTDATE;
    private String TRANSSTATUS;
    private BigDecimal CONFIRMSTATUS;
    private String BILLINGCODE;
    private String ORDERID;
    private BigDecimal AMOUNT;
    private String ORDERINFO;
    private BigDecimal TRANSTYPE;
    private String BANKCODE;
    private String CPCODE;
    private String MSISDN;
    private String TEXTCONFIRMSTATUS;
    private String TEXTTRANSSTATUS;
    private String CPNAME;
    private String CUSTOMERNAME;
    private String ERRORCODE;

    public String getORIGINALTRANSID() {
        return ORIGINALTRANSID;
    }

    public void setORIGINALTRANSID(String ORIGINALTRANSID) {
        this.ORIGINALTRANSID = ORIGINALTRANSID;
    }

    public String getERRORCODE() {
        return ERRORCODE;
    }

    public void setERRORCODE(String ERRORCODE) {
        this.ERRORCODE = ERRORCODE;
    }

    public String getCUSTOMERNAME() {
        return CUSTOMERNAME;
    }

    public void setCUSTOMERNAME(String CUSTOMERNAME) {
        this.CUSTOMERNAME = CUSTOMERNAME;
    }

    public String getCPNAME() {
        return CPNAME;
    }

    public void setCPNAME(String CPNAME) {
        this.CPNAME = CPNAME;
    }

    public String getTEXTTRANSSTATUS() {
        return TEXTTRANSSTATUS;
    }

    public void setTEXTTRANSSTATUS(String TEXTTRANSSTATUS) {
        this.TEXTTRANSSTATUS = TEXTTRANSSTATUS;
    }

    public String getTEXTCONFIRMSTATUS() {
        return TEXTCONFIRMSTATUS;
    }

    public void setTEXTCONFIRMSTATUS(String TEXTCONFIRMSTATUS) {
        this.TEXTCONFIRMSTATUS = TEXTCONFIRMSTATUS;
    }

    public BigDecimal getCONTENTPROVIDERID() {
        return CONTENTPROVIDERID;
    }

    public void setCONTENTPROVIDERID(BigDecimal CONTENTPROVIDERID) {
        this.CONTENTPROVIDERID = CONTENTPROVIDERID;
    }

    public String getTRANSID() {
        return TRANSID;
    }

    public void setTRANSID(String TRANSID) {
        this.TRANSID = TRANSID;
    }

    public String getREQUESTDATE() {
        return REQUESTDATE;
    }

    public void setREQUESTDATE(String REQUESTDATE) {
        this.REQUESTDATE = REQUESTDATE;
    }

    public String getTRANSSTATUS() {
        return TRANSSTATUS;
    }

    public void setTRANSSTATUS(String TRANSSTATUS) {
        this.TRANSSTATUS = TRANSSTATUS;
    }

    public BigDecimal getCONFIRMSTATUS() {
        return CONFIRMSTATUS;
    }

    public void setCONFIRMSTATUS(BigDecimal CONFIRMSTATUS) {
        this.CONFIRMSTATUS = CONFIRMSTATUS;
    }

    public String getBILLINGCODE() {
        return BILLINGCODE;
    }

    public void setBILLINGCODE(String BILLINGCODE) {
        this.BILLINGCODE = BILLINGCODE;
    }

    public String getORDERID() {
        return ORDERID;
    }

    public void setORDERID(String ORDERID) {
        this.ORDERID = ORDERID;
    }

    public BigDecimal getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(BigDecimal AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public String getORDERINFO() {
        return ORDERINFO;
    }

    public void setORDERINFO(String ORDERINFO) {
        this.ORDERINFO = ORDERINFO;
    }

    public BigDecimal getTRANSTYPE() {
        return TRANSTYPE;
    }

    public void setTRANSTYPE(BigDecimal TRANSTYPE) {
        this.TRANSTYPE = TRANSTYPE;
    }

    public String getBANKCODE() {
        return BANKCODE;
    }

    public void setBANKCODE(String BANKCODE) {
        this.BANKCODE = BANKCODE;
    }

    public String getCPCODE() {
        return CPCODE;
    }

    public void setCPCODE(String CPCODE) {
        this.CPCODE = CPCODE;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String MSISDN) {
        this.MSISDN = MSISDN;
    }

}
