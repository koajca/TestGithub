/*
 * Copyright (C) 2017 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.beans;

import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 * Base Class
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since Jan 16, 2017
 */
@ManagedBean
public class BaseBean implements Serializable{

    @ManagedProperty("#{msg}")
    protected ResourceBundle message;

    @ManagedProperty("#{lang}")
    protected ResourceBundle lang;
    @ManagedProperty("#{config}")
    protected ResourceBundle config;

    @ManagedProperty(value = "#{navigationBean}")
    protected NavigationBean navigationBean;

    protected DBProcessor dbProcessor = new DBProcessor();

    public ResourceBundle getConfig() {
        return config;
    }

    public void setConfig(ResourceBundle config) {
        this.config = config;
    }

    public ResourceBundle getMessage() {
        return message;
    }

    public void setMessage(ResourceBundle message) {
        this.message = message;
    }

    public ResourceBundle getLang() {
        return lang;
    }

    public void setLang(ResourceBundle lang) {
        this.lang = lang;
    }

    public NavigationBean getNavigationBean() {
        return navigationBean;
    }

    public void setNavigationBean(NavigationBean navigationBean) {
        this.navigationBean = navigationBean;
    }
    
    
}
