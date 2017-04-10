/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.common.util;

import java.util.List;

/**
 *
 * @author PHUCPT
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
