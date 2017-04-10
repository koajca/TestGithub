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
public enum TransactionStatus {
    
    PENDING ("pending", 0L, "Chua xu ly"),
    PROCESSING ("processing", 1L, "Dang xu ly"),
    COMPLETED ("completed", 2L, "Da hoan thanh"),
    CANCELED ("canceled", 3L, "Da huy"),
    FAILED ("failed", 4L, "Da that bai");

    private String strVal;
    private Long longVal;
    private String desc;

    private TransactionStatus(String strVal, Long longVal,
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
