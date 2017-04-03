/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bankplus.merchantgw.form;

/**
 *
 * @author LongTH1
 */
public class LoginForm extends BaseForm {

    private String username;
    private String password;
    private String merchantId;
    private String securityCode;

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

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
}
