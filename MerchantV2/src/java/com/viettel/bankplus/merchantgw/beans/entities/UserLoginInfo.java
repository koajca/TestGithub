/*
 * Copyright (C) 2017 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.beans.entities;

import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Users;
import java.util.HashMap;
import java.util.List;

/**
 * Class thong tin user dang nhap
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since Feb 10, 2017
 */
public class UserLoginInfo {

    private String userName;
    private String cpCode;
    private Users userInfo;
    private boolean loggedIn;
    private List<HashMap> lstUserFunction;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCpCode() {
        return cpCode;
    }

    public void setCpCode(String cpCode) {
        this.cpCode = cpCode;
    }

    public Users getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Users userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isLoggedIn() {
        if (!this.loggedIn) {
            return false;
        }
        if (this.userInfo == null || "".equals(this.userName)
                || this.cpCode == null || "".equals(this.cpCode)) {
            return false;
        }
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public List<HashMap> getLstUserFunction() {
        return lstUserFunction;
    }

    public void setLstUserFunction(List<HashMap> lstUserFunction) {
        this.lstUserFunction = lstUserFunction;
    }

    public boolean isValidAction(String url) {
        if (this.lstUserFunction == null) {
            return false;
        }
        for (HashMap role : lstUserFunction) {
            if (role.get("OBJECT_URL") != null && url.equals(role.get("OBJECT_URL").toString())) {
                return true;
            }
        }
        return false;
    }

}
