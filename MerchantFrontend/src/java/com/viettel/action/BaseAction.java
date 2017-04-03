/*
 * Copyright (C) 2013 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.action;

import com.viettel.common.util.ResourceBundleUtil;
import com.viettel.common.util.StringProcess;
import com.viettel.database.DAO.BaseDAOMDBAction;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.SessionManager;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Feb,04,2013
 * @version 1.0
 */
public class BaseAction extends BaseDAOMDBAction {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BaseAction.class);
    public String request_locale = "vi_VN";
    public String SESS_CP_CODE = "CP_CODE";
    public String SESS_CP_ID = "CP_ID";
    public String SESS_USERNAME = "USERNAME";
    public String SESS_FULL_NAME = "FULLNAME";
    public String SESS_USER_ID = "USER_ID";
    public String SESS_ROLE = "ROLE";
    public String ROLE_ADMIN = "ADMIN";
    public String ROLE_MEMBER = "MEMBER";
    public String SESS_SESSION_MANAGER_ID = "SESSION_MANAGER_ID";
    public String SESS_CP_PARENT = "CP_PARENT";

    public String getUsername() {
        String username = "";
        if (getRequest().getSession().getAttribute(SESS_USERNAME) != null) {
            username = getRequest().getSession().getAttribute(SESS_USERNAME).toString();
        }
        return username;
    }

    public void setUsername(String username) {
        getRequest().getSession().setAttribute(SESS_USERNAME, username);
    }

    public String getUserRole() {
        String role = "";
        if (getRequest().getSession().getAttribute(SESS_ROLE) != null) {
            role = getRequest().getSession().getAttribute(SESS_ROLE).toString();
        }
        return role;
    }

    public void setUserRole(String role) {
        getRequest().getSession().setAttribute(SESS_ROLE, role);
    }

    public String getCpCode() {
        String cpCode = "";
        if (getRequest().getSession().getAttribute(SESS_CP_CODE) != null) {
            cpCode = getRequest().getSession().getAttribute(SESS_CP_CODE).toString();
        }
        return cpCode;
    }

    public void setCpCode(String cpCode) {
        getRequest().getSession().setAttribute(SESS_CP_CODE, cpCode);
    }

    public Long getCpId() {
        String id = "";
        if (getRequest().getSession().getAttribute(SESS_CP_ID) != null) {
            id = getRequest().getSession().getAttribute(SESS_CP_ID).toString();
        }
        Long lId = StringProcess.convertToLong(id);
        return lId;
    }

    public void setCpId(Long id) {
        getRequest().getSession().setAttribute(SESS_CP_ID, StringProcess.convertToString(id));
    }

    public Long getUserId() {
        String id = "";
        if (getRequest().getSession().getAttribute(SESS_USER_ID) != null) {
            id = getRequest().getSession().getAttribute(SESS_USER_ID).toString();
        }
        Long lId = StringProcess.convertToLong(id);
        return lId;
    }

    public void setUserId(Long id) {
        getRequest().getSession().setAttribute(SESS_USER_ID, StringProcess.convertToString(id));
    }

    public boolean isLoggedIn() {
        if (getUsername().equals("") || getUserRole().equals("") || getCpCode().equals("")) {
            return false;
        }
        return true;
    }

    public String getUserFullname() {
        String name = "";
        if (getRequest().getSession().getAttribute(SESS_FULL_NAME) != null) {
            name = getRequest().getSession().getAttribute(SESS_FULL_NAME).toString();
        }
        return name;
    }

    public void setUserFullname(String name) {
        getRequest().getSession().setAttribute(SESS_FULL_NAME, name);
    }

    public SessionManager getSessionManagerId() {
        SessionManager sm = null;
        if (getRequest().getSession().getAttribute(SESS_SESSION_MANAGER_ID) != null) {
            sm = (SessionManager) getRequest().getSession().getAttribute(SESS_SESSION_MANAGER_ID);
        }
        return sm;
    }

    public void setSessionManagerId(SessionManager id) {
        getRequest().getSession().setAttribute(SESS_SESSION_MANAGER_ID, id);
    }

    public String getConfig(String key) {
        return ResourceBundleUtil.getString(key);
    }
    
     public String getParentCP() {
        String id = "";
        if (getRequest().getSession().getAttribute(SESS_CP_PARENT) != null) {
            id = getRequest().getSession().getAttribute(SESS_CP_PARENT).toString();
        }
        //
        return id;
    }

    public void setParenCP(String parentCPCode) {
        getRequest().getSession().setAttribute(SESS_CP_PARENT, parentCPCode);
    }

    public SessionManager createSessionManager() {
        SessionManager sm = new SessionManager();
        UUID uid = UUID.randomUUID();
        String sId = uid.toString().replace("-", "").toUpperCase();
        sm.setContentProviderId(getCpId());
        sm.setSessionId(sId);
        sm.setStartTime(Calendar.getInstance().getTime());
        sm.setUpdateTime(Calendar.getInstance().getTime());
        sm.setStatus(1L);
        sm = (new DBProcessor()).getSessionProcessor().doSave(sm);
        setSessionManagerId(sm);
        return sm;
    }
}
