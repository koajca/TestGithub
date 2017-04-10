/*
 * Copyright (C) 2017 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.beans;

import com.ocpsoft.pretty.PrettyContext;
import com.viettel.bankplus.merchantgw.beans.entities.UserLoginInfo;
import com.viettel.common.util.Constants;
import com.viettel.common.util.captcha.CaptchaServlet;
import com.viettel.common.util.security.EncryptUtils;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Users;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.UsersPassHis;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

/**
 * Class xu ly dang nhap
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since Jan 16, 2017
 */
@ManagedBean
public class LoginBean extends BaseBean implements Serializable {

    private String username;
    private String password;
    private String cpcode;
    private String securityCode;
    private String newpassword;
    private String renewpassword;
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LoginBean.class);
    private List<HashMap> lstUserFunction;
    private Users userInfo;

    /**
     * Login operation.
     *
     * @return
     */
    public void doLogin() {
        boolean loggedIn = false;
        log.info("Login Action");

        String prettyUrl = PrettyContext.getCurrentInstance().getRequestURL().toURL();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        log.info("Pretty: " + prettyUrl);
        log.info("Real: " + request.getRequestURL());
        log.info("Real URI: " + request.getRequestURI());

        FacesMessage msg = new FacesMessage(message.getString("login.error.full"));
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        RequestContext context = RequestContext.getCurrentInstance();
        if (!CaptchaServlet.isTokenOk((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true), this.securityCode)) {
            log.debug("Captcha not correct");
            msg.setSummary(message.getString("login.error"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("loggedIn", loggedIn);
            return;
            // To to login page
        }

        //Invalidate Old Session
//        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        CaptchaServlet.generateToken(session);
        ContentProvider cp = dbProcessor.getProviderProcessor().getProviderByCPCode(cpcode);
        if (cp == null) {
            log.debug("CP not found");
            msg.setSummary(message.getString("login.error"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            // To to login page
            context.addCallbackParam("loggedIn", loggedIn);
            return;
        }
        userInfo = dbProcessor.getUsersProcessor().getUser(username, cp.getContentProviderId());
        if (userInfo == null || userInfo.getStatus() != 1) {
            log.debug("User not found or inactive");
            msg.setSummary(message.getString("login.error"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            // To to login page
            context.addCallbackParam("loggedIn", loggedIn);
            return;
        }

        if (!EncryptUtils.isHashedPasswordOK(password, userInfo.getPassword())) {
            log.debug("Password incorrect");
            msg.setSummary(message.getString("login.error"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            // To to login page
            context.addCallbackParam("loggedIn", loggedIn);
            return;
        }
        lstUserFunction = dbProcessor.getUsersProcessor().getUserFunction(userInfo.getUserId());
        loggedIn = true;
        UserLoginInfo userLoggedIn = new UserLoginInfo();
        userLoggedIn.setCpCode(cpcode);
        userLoggedIn.setLoggedIn(loggedIn);
        userLoggedIn.setLstUserFunction(lstUserFunction);
        userLoggedIn.setUserInfo(userInfo);
        userLoggedIn.setUserName(username);
        session.setAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO, userLoggedIn);
        log.debug("login success");
        // To to login page
        context.addCallbackParam("loggedIn", loggedIn);

    }

    public void doChangePass() {
        FacesMessage msg = new FacesMessage(message.getString("changepass.error"));
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        RequestContext context = RequestContext.getCurrentInstance();
        if (this.password == null || "".equals(this.password)
                || this.newpassword == null || "".equals(this.newpassword)
                || this.renewpassword == null || "".equals(this.renewpassword)) {
            msg.setSummary(message.getString("changepass.error"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("changedPass", false);
            return;
        }

        if (!CaptchaServlet.isTokenOk((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true), this.securityCode)) {
            log.debug("Captcha not correct");
            msg.setSummary(message.getString("change.pass.security.code.invalid"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("changedPass", false);
            return;
        }

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        CaptchaServlet.generateToken(session);

        if (this.password.equals(this.newpassword)) {
            log.debug("Mat khau cu trung mat khau moi");
            msg.setSummary(message.getString("changepass.error.sameoldpass"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("changedPass", false);
            return;
        }

        //Password phai it nhat 8 ky tu gom chu thuong, chu hoa, so va ky tu dac biet
        String regex = "^(?=.{8,})(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W).*$";
        if (!this.newpassword.matches(regex)) {
            log.debug("Password phai it nhat 8 ky tu gom chu thuong, chu hoa, so va ky tu dac biet");
            msg.setSummary(message.getString("changepass.error.passsecure"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("changedPass", false);
            return;
        }

        if (!this.newpassword.equals(this.renewpassword)) {
            log.debug("Mat khau moi khong giong nhau");
            msg.setSummary(message.getString("changepass.error.notsamenewpass"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("changedPass", false);
            return;
        }

        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO);

        Users user = dbProcessor.getUsersProcessor().getUser(userLoginInfo.getUserName(), userLoginInfo.getUserInfo().getContentProviderId());
        if (!EncryptUtils.isHashedPasswordOK(this.password, user.getPassword())) {
            //Trung mat khau cu
            log.debug("Mat khau cu khong dung");
            msg.setSummary(message.getString("changepass.error"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("changedPass", false);
            return;
        }

        //Lay 5 giao dich doi pass gan nhat
        List<UsersPassHis> lstPassHis = dbProcessor.getUsersProcessor().getFiveRecentPassHis(userLoginInfo.getUserInfo().getUserId());
        if (lstPassHis != null && !lstPassHis.isEmpty()) {
            for (UsersPassHis userPassHis : lstPassHis) {
                if (EncryptUtils.isHashedPasswordOK(this.newpassword, userPassHis.getPassword())) {
                    //Trung mat khau cu
                    log.debug("Trung mat khau cu");
                    msg.setSummary(message.getString("changepass.error.samerecent"));
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    context.addCallbackParam("changedPass", false);
                    return;
                }
            }
        }

        String hashedPass = EncryptUtils.hashPassword(this.newpassword);
        user.setPassword(hashedPass);
        dbProcessor.getUsersProcessor().doSave(user);
        UsersPassHis passHis = new UsersPassHis();
        passHis.setCreatedTime(Calendar.getInstance().getTime());
        passHis.setPassword(hashedPass);
        passHis.setUserId(user.getUserId());
        passHis.setUsername(user.getUsername());
        dbProcessor.getUsersProcessor().doSavePassHis(passHis);
        context.addCallbackParam("changedPass", true);
        session.setAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO, null);
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }

    /**
     * Logout operation.
     *
     * @return
     */
    public void doLogout() {
        // Set the paremeter indicating that user is logged in to false
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.setAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO, null);
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        // Set logout message
        FacesMessage msg = new FacesMessage("Logout success!", "INFO MSG");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(navigationBean.toLogin());
        } catch (Exception ex) {
            log.error(ex, ex);
        }
    }

    // ------------------------------
    // Getters & Setters 
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpcode() {
        return cpcode;
    }

    public void setCpcode(String cpcode) {
        this.cpcode = cpcode;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getRenewpassword() {
        return renewpassword;
    }

    public void setRenewpassword(String renewpassword) {
        this.renewpassword = renewpassword;
    }

}
