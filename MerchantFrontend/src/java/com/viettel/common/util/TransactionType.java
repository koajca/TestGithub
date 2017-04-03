/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.common.util;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since Nov 3, 2011
 */
public enum TransactionType {
    
    PAYMENT ("", 0L, "Giao dich thanh toan"),
    CANCEL_PAYMENT ("", 2L, "Giao dich huy thanh toan"),
    CHECK_PAYMENT ("", 1L, "giao dich kiem tra ket qua thanh toan"),
    TOPUP ("", 3L, "giao dich nap tien tai khoan"),
    BALANCE ("", 4L , "giao dich truy van so du tai khoan"),
    QUERY_TRANS_RESULT ("", 5L, "truy van ket qua gd"),
    CHECK_CUSTOMER ("", 6L, "kiem tra thong tin khach hang"),
    CHECKOUT_PINCODE ("", 7L, "Ban tin mua ma the cao"),
    RETRIEVE_PINCODE ("", 8L, "Ban tin lay lai ma the cao");

    private String strVal;
    private Long longVal;
    private String desc;

    private TransactionType(String strVal, Long longVal,
            String desc) {
        this.strVal = strVal;
        this.longVal = longVal;
        this.desc = desc;
    }
    public String getDesc() {
        return desc;
    }

    public Long getLongVal() {
        return longVal;
    }

    public String getStrVal() {
        return strVal;
    }

}
