/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bankplus.merchantgw.beans;

import com.viettel.bankplus.merchantgw.beans.entities.UserLoginInfo;
import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.StringProcess;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransferMerchant;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author ducnt28
 */
@ManagedBean
@ViewScoped
public class TranferTransaction extends BaseBean implements Serializable {

    private Date fromDate;
    private Date toDate;
    private String chooseCp;
    List<TransferMerchant> listTranferMerchant;
    private List<ContentProvider> lstContentProvider;
    private static final Logger log = Logger.getLogger(TranferTransaction.class);
    List<HashMap<String, String>> listContentProvider = new ArrayList<HashMap<String, String>>();

    public List<TransferMerchant> getListTranferMerchant() {
        return listTranferMerchant;
    }

    public void setListTranferMerchant(List<TransferMerchant> listTranferMerchant) {
        this.listTranferMerchant = listTranferMerchant;
    }

    public List<HashMap<String, String>> getListContentProvider() {
        return listContentProvider;
    }

    public void setListContentProvider(List<HashMap<String, String>> listContentProvider) {
        this.listContentProvider = listContentProvider;
    }

    private void getListCp(Long startWith) {
        listContentProvider = dbProcessor.getProviderProcessor().getTreeProvider(startWith);
        log.info(listContentProvider);
    }

    private List getListCpNew(Long startWith) {
        return dbProcessor.getProviderProcessor().getTreeProvider(startWith);
    }

    public Long getCpId() {

        Long id = null;
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        UserLoginInfo loginInfo = (UserLoginInfo) session.getAttribute("userLoginInfo");
        if (loginInfo != null && loginInfo.getCpCode() != null) {
            id = loginInfo.getUserInfo().getContentProviderId();
        }
        return id;
    }

    private void getListCp() {
        getListCp(getCpId());
    }

    @PostConstruct
    public void init() {
        getListCp();
        Calendar calFromDate = Calendar.getInstance();
        calFromDate.setTime(new Date());
        calFromDate.set(Calendar.DAY_OF_MONTH, 1);
        calFromDate.set(Calendar.HOUR_OF_DAY, 0);
        calFromDate.set(Calendar.MINUTE, 0);
        calFromDate.set(Calendar.SECOND, 0);
        fromDate = calFromDate.getTime();

        Calendar calToDate = Calendar.getInstance();
        calToDate.setTime(new Date());
        calToDate.set(Calendar.HOUR_OF_DAY, 23);
        calToDate.set(Calendar.MINUTE, 59);
        calToDate.set(Calendar.SECOND, 59);
        toDate = calToDate.getTime();
    }

    public void onReprotTranfer() {

        log.info("TranferTransaction.onReprotTranfer");
        log.info(fromDate + " - " + toDate);
        FacesMessage msg = new FacesMessage(message.getString("search.error.full"));
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        if (fromDate == null || "".equalsIgnoreCase(fromDate.toString())) {
            log.info("fromDate is null");
            msg.setSummary(message.getString("error.frmDateNull"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (toDate == null || "".equalsIgnoreCase(toDate.toString())) {
            log.info("toDate is null");
            msg.setSummary(message.getString("error.toDateNull"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (fromDate.after(toDate)) {
            log.info("fromDate > toDate");
            msg.setSummary(message.getString("error.frDatetoDate"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        ArrayList<String> listCpCode = new ArrayList();
        if (!CommonUtils.isNullOrEmpty(chooseCp) && !"-1".equals(chooseCp)) {
            boolean isOK = false;
            for (HashMap<String, String> hashInfo : listContentProvider) {
                Long cpIdInput = StringProcess.convertToLong(chooseCp);
                Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                if (cpIdInput.compareTo(cpIdStore) == 0) {
                    isOK = true;
                    break;
                }
            }
            if (isOK) {
                List lstNewChoose = getListCpNew(StringProcess.convertToLong(chooseCp));
                for (Object hashInfoEtt : lstNewChoose) {
                    HashMap<String, String> hashInfo = (HashMap<String, String>) hashInfoEtt;
                    listCpCode.add(hashInfo.get("CP_CODE"));
                }
            }
        } else {
            for (HashMap<String, String> hashInfo : listContentProvider) {
                listCpCode.add(hashInfo.get("CP_CODE"));
            }
        }

        listTranferMerchant = dbProcessor.getTransactionProcessor().getListTransfer(fromDate, toDate, listCpCode);
    }

    public List<ContentProvider> getLstContentProvider() {
        return lstContentProvider;
    }

    public void setLstContentProvider(List<ContentProvider> lstContentProvider) {
        this.lstContentProvider = lstContentProvider;
    }

    public String getChooseCp() {
        return chooseCp;
    }

    public void setChooseCp(String chooseCp) {
        this.chooseCp = chooseCp;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

}
