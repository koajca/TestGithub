/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.dao;

import com.viettel.action.BaseAction;
import com.viettel.action.JsonData;
import com.viettel.bankplus.merchantgw.form.LoginForm;
import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.captcha.CaptchaServlet;
import com.viettel.common.util.security.EncryptUtils;
import com.viettel.security.PassTranformer;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Users;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.util.Calendar;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 11, 2014
 * @version 1.0
 */
public class Authentication extends BaseAction {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Authentication.class);
    public JsonData json = new JsonData();
    private LoginForm loginForm = new LoginForm();
    String MENU_PAGE = "getMenu";
    String INDEX_PAGE = "indexSuccess";
    String MAIN_PAGE = "main.page";
    DBProcessor dbProcessor = new DBProcessor();

    public String getIndexPage() {
        return INDEX_PAGE;
    }

    public String getChangePass() {
        return "changePassIndex";
    }

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public String onLogin() {
        log.debug("Begin: onLogin");
        String[] customInfo = new String[2];
        clearSession();
        customInfo = validateLogin();
        json.setCustomInfo(customInfo);
        return SUCCESS;
    }

    public String onLogout() {
        log.debug("Begin: onLogout");
        clearSession();
        getRequest().getSession().invalidate();
        return INDEX_PAGE;
    }

    public String[] validateLogin() {
        String[] customInfo = new String[2];
        customInfo[1] = "";
        if (CommonUtils.isNullOrEmpty(loginForm.getMerchantId())
                || CommonUtils.isNullOrEmpty(loginForm.getUsername())
                || CommonUtils.isNullOrEmpty(loginForm.getPassword())) {
            customInfo[0] = "failure";
            return customInfo;
        }

        if (loginForm.getMerchantId().length() > 15
                || loginForm.getUsername().length() > 50
                || loginForm.getPassword().length() > 50) {
            customInfo[0] = "failure";
            CaptchaServlet.generateToken(getRequest().getSession());
            return customInfo;
        }
        if (!CaptchaServlet.isTokenOk(getRequest().getSession(), loginForm.getSecurityCode())) {
            customInfo[0] = "failure";
            CaptchaServlet.generateToken(getRequest().getSession());
            return customInfo;
        }
        CaptchaServlet.generateToken(getRequest().getSession());
        ContentProvider cp = dbProcessor.getProviderProcessor().getProviderByCPCode(loginForm.getMerchantId());
        if (cp == null) {
            customInfo[0] = "failure";
            return customInfo;
        }
        Users user = dbProcessor.getUsersProcessor().getUser(loginForm.getUsername(), cp.getContentProviderId());
        if (user == null || user.getStatus() != 1) {
            customInfo[0] = "failure";
            return customInfo;
        }

        if (!EncryptUtils.isHashedPasswordOK(loginForm.getPassword(), user.getPassword())) {
            customInfo[0] = "failure";
            return customInfo;
        }
        if (user.getRoles() == null || (!user.getRoles().equalsIgnoreCase(ROLE_ADMIN) && !user.getRoles().equalsIgnoreCase(ROLE_MEMBER))) {
            customInfo[0] = "failure";
            return customInfo;
        }
        setUsername(loginForm.getUsername());
        setCpCode(cp.getCpCode());
        setCpId(cp.getContentProviderId());
        setUserRole(user.getRoles());
        setUserId(user.getUserId());
        setUserFullname(user.getFullName());
        if (cp.getParent() != null) {
            setParenCP(dbProcessor.getProviderProcessor().getProviderById(cp.getParent()).getCpCode());
        } else {
            setParenCP("");
        }

        log.info("parent = " + cp.getParent());
        customInfo[0] = "success";
        return customInfo;
    }

    public String doChangePass() {
        String[] customInfo = new String[2];
        customInfo[1] = "";
        String oldPass = getRequest().getParameter("oldPassword");
        String newPass = getRequest().getParameter("newPassword");
        String reNewPass = getRequest().getParameter("reNewPassword");
        boolean flag = true;

        if (!newPass.equals(reNewPass)) {
            customInfo[0] = "failure";
            flag = false;
        }

        //Password phai it nhat 8 ky tu gom chu thuong, chu hoa, so va ky tu dac biet
        String regex = "^(?=.{8,})(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W).*$";
        if (!newPass.matches(regex)) {
            customInfo[0] = "failure";
            flag = false;
        }

        Users user = dbProcessor.getUsersProcessor().getUser(getUsername(), getCpId());
        if (flag && (user == null || user.getStatus() != 1)) {
            customInfo[0] = "failure";
            flag = false;
        }

        if (flag && !EncryptUtils.isHashedPasswordOK(oldPass, user.getPassword())) {
            customInfo[0] = "failure";
            flag = false;
        }
        if (flag) {
            user.setPassword(EncryptUtils.hashPassword(newPass));
            user.setUpdatedTime(Calendar.getInstance().getTime());
            if (dbProcessor.getUsersProcessor().doSave(user) != null) {
                customInfo[0] = "success";
            }
        }
        json.setCustomInfo(customInfo);
        return SUCCESS;
    }

    private void clearSession() {
        if (getRequest().getSession().getAttribute(SESS_CP_CODE) != null) {
            getRequest().getSession().removeAttribute(SESS_CP_CODE);
        }
        if (getRequest().getSession().getAttribute(SESS_ROLE) != null) {
            getRequest().getSession().removeAttribute(SESS_ROLE);
        }
        if (getRequest().getSession().getAttribute(SESS_USERNAME) != null) {
            getRequest().getSession().removeAttribute(SESS_USERNAME);
        }
        if (getRequest().getSession().getAttribute(SESS_CP_CODE) != null) {
            getRequest().getSession().removeAttribute(SESS_CP_CODE);
        }
        if (getRequest().getSession().getAttribute(SESS_CP_ID) != null) {
            getRequest().getSession().removeAttribute(SESS_CP_ID);
        }
        if (getRequest().getSession().getAttribute(SESS_USER_ID) != null) {
            getRequest().getSession().removeAttribute(SESS_USER_ID);
        }
        if (getRequest().getSession().getAttribute(SESS_SESSION_MANAGER_ID) != null) {
            getRequest().getSession().removeAttribute(SESS_SESSION_MANAGER_ID);
        }
    }

    public static void main(String[] args) throws Exception {
        String where = "where cp.request_date > :fromDate and cp.request_date < :toDate and bp.request_date > :fromDate and bp.request_date < :toDate and cp.content_provider_id in (:cpId) and bp.process_code = '300001'";
        int numparam = StringUtils.countMatches(where, ":");
        System.out.println(numparam);

    }
}
