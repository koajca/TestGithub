/*
 * Copyright (C) 2017 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.beans;

import com.viettel.bankplus.merchantgw.beans.entities.Transaction;
import java.io.Serializable;
import com.viettel.bankplus.merchantgw.client.form.ErrorCodeForm;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Feb 11, 2017
 * @version 1.0
 */
@ManagedBean(name = "dtPaginatorView")
@ViewScoped
public class TestDataBean implements Serializable {

    private List<HashMap<String, String>> cars;
    private Date fromDate;
    HashMap transDetail;

    public HashMap getTransDetail() {
        return transDetail;
    }

    public void setTransDetail(HashMap transDetail) {
        this.transDetail = transDetail;
    }

    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getFromDate() {
        return fromDate;
    }
    private ErrorCodeForm errorCodeForm;

    public ErrorCodeForm getErrorCodeForm() {
        return errorCodeForm;
    }

    public void setErrorCodeForm(ErrorCodeForm errorCodeForm) {
        this.errorCodeForm = errorCodeForm;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @PostConstruct
    public void init() {
        errorCodeForm = errorCodeForm == null ? (new ErrorCodeForm()) : errorCodeForm;
        errorCodeForm.setErrorCodeId("123456789");
        errorCodeForm.setErrorCode("init data");
    }

    private void initData() {
        cars = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 50; i++) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("id", i + "");
            data.put("year", "nam" + i);
            data.put("brand", "brand" + i);
            data.put("color", "color" + i);
            cars.add(data);
        }
    }

    public List<HashMap<String, String>> getCars() {
        return cars;
    }

    public void search() {
        initData();
    }

    public void viewDetail(HashMap data) {
        this.transDetail = data;
    }
}
