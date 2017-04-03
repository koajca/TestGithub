/*
 * Copyright (C) 2013 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.action;

import java.util.List;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Feb,04,2013
 * @version 1.0
 */
public class JsonData {
    private String message;
    private List items;
    private int totalRow;
    private int numRow;
    private Object customInfo;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getNumRow() {
        return numRow;
    }

    public void setNumRow(int numRow) {
        this.numRow = numRow;
    }

    public Object getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(Object customInfo) {
        this.customInfo = customInfo;
    }
    
}
