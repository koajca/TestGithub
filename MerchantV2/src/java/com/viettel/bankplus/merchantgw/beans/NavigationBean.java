/*
 * Copyright (C) 2017 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Navigation Bean
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since Jan 16, 2017
 */
@ManagedBean
@SessionScoped
public class NavigationBean implements Serializable {

    /**
     * Redirect to login page.
     *
     * @return Login page name.
     */
    public String redirectToLogin() {
        return "/login.xhtml?faces-redirect=true";
    }

    /**
     * Go to login page.
     *
     * @return Login page name.
     */
    public String toLogin() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String loginUrl = request.getContextPath() + "/login.xhtml";
        return loginUrl;
    }

    /**
     * Go to Home page when login success
     *
     * @return Welcome page name.
     */
    public String toHome() {
        return "home";
    }

}
