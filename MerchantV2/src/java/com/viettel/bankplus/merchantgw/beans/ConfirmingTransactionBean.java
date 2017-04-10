/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bankplus.merchantgw.beans;

import com.viettel.bankplus.merchantgw.beans.entities.Transaction;
import com.viettel.bankplus.merchantgw.beans.entities.UserLoginInfo;
import com.viettel.bankplus.merchantgw.client.form.TransForm;
import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.Constants;
import com.viettel.common.util.DateTimeUtils;
//import com.viettel.common.util.ResourceBundleUtils;
import com.viettel.common.util.StringProcess;
import com.viettel.common.util.TransactionStatus;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransBankplus;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransCp;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author os_phucpt1
 */
@ManagedBean(name = "confirmingTransactionBean")
@ViewScoped
public class ConfirmingTransactionBean extends BaseBean implements Serializable {

    private final Logger log = Logger.getLogger(ConfirmingTransactionBean.class);
    private final DBProcessor dbProcessor = new DBProcessor();
    private final String SESS_ATTR_LIST_CP = "listCp";
    private final String SESS_ATTR_LIST_TRANS_CONFIRM = "listTransConfirm";

    private TransForm transForm;
    private List<Transaction> listTrans;

    private String[] confirmStatus;

    public String[] getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(String[] confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public List<Transaction> getListTrans() {
        return listTrans;
    }

    public void setListTrans(List<Transaction> listTrans) {
        this.listTrans = listTrans;
    }

    public TransForm getTransForm() {
        return transForm;
    }

    public void setTransForm(TransForm transForm) {
        this.transForm = transForm;
    }

    @PostConstruct
    public void init() {
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            session.removeAttribute(SESS_ATTR_LIST_TRANS_CONFIRM);

            UserLoginInfo userInfo = (UserLoginInfo) session.getAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO);
            transForm = transForm == null ? (new TransForm()) : transForm;
            transForm.setFromDate(DateTimeUtils.getFirstDate());
            transForm.setToDate(new Date());

//            Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
//            String defaultConfirm = ResourceBundleUtils.getString("confirming.transaction.cmb.value.0", locale);
//            String successConfirm = ResourceBundleUtils.getString("confirming.transaction.cmb.value.1", locale);
//            String failedConfirm = ResourceBundleUtils.getString("confirming.transaction.cmb.value.-1", locale);
            String defaultConfirm = lang.getString("confirming.transaction.cmb.value.0");
            String successConfirm = lang.getString("confirming.transaction.cmb.value.1");
            String failedConfirm = lang.getString("confirming.transaction.cmb.value.-1");

            String[] tempConfirm = {defaultConfirm, failedConfirm, successConfirm};
            confirmStatus = tempConfirm;

            session.removeAttribute(this.SESS_ATTR_LIST_CP);
            List<Map<String, String>> listCP = getListCp(userInfo.getUserInfo().getContentProviderId());
            session.setAttribute(this.SESS_ATTR_LIST_CP, listCP);

        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public List completeText(String cpName) {
        List<String> results = new ArrayList<String>();
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
//            UserLoginInfo userInfo = (UserLoginInfo) session.getAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO);
            List l = (List) session.getAttribute(this.SESS_ATTR_LIST_CP);
            String temp;
            for (int i = 0; i < l.size(); i++) {
                Map m = (Map) l.get(i);
//                    temp = m.get("CONTENT_PROVIDER_ID")==null?"": m.get("CONTENT_PROVIDER_ID").toString();
                temp = m.get("CP_NAME") == null ? "" : m.get("CP_NAME").toString().trim();
                if (cpName != null && !"".equals(cpName) && temp.toUpperCase().contains(cpName.toUpperCase())) {
                    results.add(temp);
                }
                if (cpName == null || "".equals(cpName)) {
                    results.add(temp);
                }
            }
        } catch (Exception e) {
            log.error("error: ", e);
        }
        return results;
    }

    public void searchTrans() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.removeAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        String msgError = "";
        try {
            Date now = DateTimeUtils.getStartDate(new Date());
            if (now.compareTo(this.transForm.getFromDate()) < 0) {
                msgError = message.getString("query.transaction.from.date.not.larger.than.sys.date");
            } else if (now.compareTo(this.transForm.getToDate()) < 0) {
                msgError = message.getString("query.transaction.to.date.not.larger.than.sys.date");
            } else if (this.transForm.getFromDate() != null && this.transForm.getFromDate().compareTo(this.transForm.getToDate()) > 0) {
                msgError = message.getString("query.transaction.from.date.not.larger.than.to.date");
            } else {
//                Date fromDateTemp = DateTimeUtils.getDateOriginal(this.transForm.getFromDate());
//                Date toDateTemp = DateTimeUtils.getDateOriginal(this.transForm.getToDate());
//                this.transForm.setFromDate(fromDateTemp);
//                this.transForm.setToDate(toDateTemp);
                listTrans = this.getListTransaction(this.transForm);
                String defaultConfirm = lang.getString("confirming.transaction.cmb.value.0");
                String successConfirm = lang.getString("confirming.transaction.cmb.value.1");
                String failedConfirm = lang.getString("confirming.transaction.cmb.value.-1");

                for (int i = 0; i < listTrans.size(); i++) {
                    Transaction obj = listTrans.get(i);
                    if ("-1".equals(String.valueOf(obj.getCONFIRMSTATUS()))) {
                        obj.setTEXTCONFIRMSTATUS(failedConfirm);
                    } else if ("0".equals(String.valueOf(obj.getCONFIRMSTATUS()))) {
                        obj.setTEXTCONFIRMSTATUS(defaultConfirm);
                    } else if ("1".equals(String.valueOf(obj.getCONFIRMSTATUS()))) {
                        obj.setTEXTCONFIRMSTATUS(successConfirm);
                    }
                }
            }
        } catch (Exception e) {
            msgError = e.getMessage();
            log.error("error: ", e);
        }
        if (!"".equals(msgError)) {
            msg.setSummary(msgError);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    private List<Transaction> getListTransaction(TransForm form) throws Exception {
        List<Transaction> result;
        try {
            String where = "";
            where += "where cp.request_date > :fromDate";
            where += " and cp.request_date < :toDate";
            where += " and bp.request_date > :fromDate";
            where += " and bp.request_date < :toDate";
            where += " and cp.content_provider_id in (:cpId) ";
//        where += " and cp.trans_status = :transStatus";
//        where += " and cp.trans_type = :transType";
            where += " and bp.process_code = '300001'";
            where += " and cp.confirm_status is null";
            where += " and (bp.error_code is null or bp.error_code = '32') and (bp.correct_code is null or bp.correct_code ='32')";
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            HashMap hash = new HashMap();
            String temp;
//            if (!CommonUtils.isNullOrEmpty(form.getCpName())) {
//                for (int i = 0; i < listContentProvider.size(); i++) {
//                    Map obj = (Map) listContentProvider.get(i);
//                    temp = obj.get("CP_NAME") == null ? "" : obj.get("CP_NAME").toString().trim();
//                    if (temp.equalsIgnoreCase(form.getCpName().trim())) {
//                        listCpId.add(Long.parseLong(obj.get("CONTENT_PROVIDER_ID") == null ? "" : obj.get("CONTENT_PROVIDER_ID").toString().trim()));
//                        break;
//                    }
//                }
//            } else {
//                for (int i = 0; i < listContentProvider.size(); i++) {
//                    Map<String, String> hashInfo = (Map<String, String>) listContentProvider.get(i);
//                    listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
//                }
//            }
            String cpId;
            List listTempCpId = new ArrayList();
            if (!CommonUtils.isNullOrEmpty(form.getCpId())) {
                List l = (List) session.getAttribute(this.SESS_ATTR_LIST_CP);
                if ("-1".equals(form.getCpId() + "")) {
                    for (int i = 0; i < l.size(); i++) {
                        Map obj = (Map) l.get(i);
                        cpId = obj.get("CONTENT_PROVIDER_ID") == null ? "" : obj.get("CONTENT_PROVIDER_ID").toString().trim();
                        listTempCpId.add(cpId);
                    }
                } else {
                    for (int i = 0; i < l.size(); i++) {
                        Map obj = (Map) l.get(i);
                        cpId = obj.get("CONTENT_PROVIDER_ID") == null ? "" : obj.get("CONTENT_PROVIDER_ID").toString().trim();
                        if (form.getCpId().equals(Long.parseLong(cpId))) {
//                            hash.put("cpId", obj.get("CONTENT_PROVIDER_ID") == null ? "" : obj.get("CONTENT_PROVIDER_ID").toString().trim());
                            listTempCpId.add(cpId);
                        }
                    }
                }
                where += " and cp.content_provider_id in (:cpId)";
                hash.put("cpId", listTempCpId);
            }
            hash.put("cpId", listTempCpId);
            if (!CommonUtils.isNullOrEmpty(form.getBankCode())) {
                where += " and upper(bp.bank_code) = :bankCode";
                hash.put("bankCode", form.getBankCode().toUpperCase());
            }
            if (!CommonUtils.isNullOrEmpty(form.getPaymentId())) {
                where += " and lower(bp.billing_code) = :billingCode";
                hash.put("billingCode", form.getPaymentId().toLowerCase());
            }
//            if (!CommonUtils.isNullOrEmpty(form.getCpTransId())) {
//                where += " and lower(cp.order_id) = :orderId";
//                hash.put("orderId", form.getCpTransId().toLowerCase());
//            }
            if (!CommonUtils.isNullOrEmpty(form.getTransId())) {
                where += " and cp.trans_id = :transId";
                hash.put("transId", form.getTransId());
            }
//            if (!CommonUtils.isNullOrEmpty(form.getStatus())) {
//                if ("1".equals(form.getStatus())) {//Timeout
//                    where += " and (bp.error_code is null or bp.error_code = '32')"
//                            + " and (bp.correct_code is null or bp.correct_code = '32')";
//                } else if ("2".equals(form.getStatus())) {//Thanh cong
//                    where += " and (bp.error_code = '00' or bp.correct_code = '00')";
//                } else {
//                    where += " and cp.trans_status = :transStatus";
//                    hash.put("transStatus", form.getStatus());
//                }
//            }
            hash.put("fromDate", form.getFromDate());
            hash.put("toDate", DateTimeUtils.addDate(form.getToDate(), Calendar.DATE, 1));
            result = dbProcessor.getTransactionProcessor().getTrans(where, hash);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    private List getListCp(Long startWith) throws Exception {
        try {
            return dbProcessor.getProviderProcessor().getTreeProvider(startWith);
        } catch (Exception e) {
            throw e;
        }
    }

    public void onCellEdit(CellEditEvent event) {
        try {
//            Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
            String oldValue = event.getOldValue() == null ? "" : event.getOldValue().toString();
            String newValue = event.getNewValue() == null ? "" : event.getNewValue().toString();
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            FacesContext context = FacesContext.getCurrentInstance();
            Transaction trans = context.getApplication().evaluateExpressionGet(context, "#{trans}", Transaction.class);

            Map<String, Transaction> mapTransConfirm = (Map<String, Transaction>) session.getAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);

//            String defaultConfirm = ResourceBundleUtils.getString("confirming.transaction.cmb.value.0", locale);
//            String successConfirm = ResourceBundleUtils.getString("confirming.transaction.cmb.value.1", locale);
//            String failedConfirm = ResourceBundleUtils.getString("confirming.transaction.cmb.value.-1", locale);
            String defaultConfirm = lang.getString("confirming.transaction.cmb.value.0");
            String successConfirm = lang.getString("confirming.transaction.cmb.value.1");
            String failedConfirm = lang.getString("confirming.transaction.cmb.value.-1");

            if (newValue != null && !newValue.equals(oldValue)) {
                mapTransConfirm = mapTransConfirm == null ? (new HashMap()) : mapTransConfirm;
                if (newValue.equals(defaultConfirm)) {
                    if (mapTransConfirm.containsKey(trans.getTRANSID())) {
                        mapTransConfirm.remove(trans.getTRANSID());
                    }
                } else {
                    if (newValue.equals(failedConfirm)) {
                        trans.setCONFIRMSTATUS(BigDecimal.valueOf(-1l));
                    } else if (newValue.equals(successConfirm)) {
                        trans.setCONFIRMSTATUS(BigDecimal.valueOf(1l));
                    }
                    mapTransConfirm.put(trans.getTRANSID(), trans);
                }
                session.removeAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);
                session.setAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM, mapTransConfirm);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public List<Transaction> getAllTransConfirm() {
        List<Transaction> result = new ArrayList<Transaction>();
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            Map<String, Transaction> mapTransConfirm = (Map<String, Transaction>) session.getAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);
            if (mapTransConfirm != null && !mapTransConfirm.isEmpty()) {
                for (Map.Entry<String, Transaction> entry : mapTransConfirm.entrySet()) {
                    result.add(entry.getValue());
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return result;
    }

    public void multiConfirm() {
        try {
            String msgError = "";
            RequestContext reqContext = RequestContext.getCurrentInstance();
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

            Map<String, Transaction> mapTransConfirm = (Map<String, Transaction>) session.getAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);
            UserLoginInfo userInfo = (UserLoginInfo) session.getAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO);
            List<Map<String, String>> listContentProvider = getListCp(userInfo.getUserInfo().getContentProviderId());

//            String successConfirm = ResourceBundleUtils.getString("confirming.transaction.cmb.value.1", locale);
            String successConfirm = lang.getString("confirming.transaction.cmb.value.1");
//            String failedConfirm = ResourceBundleUtils.getString("confirming.transaction.cmb.value.-1", locale);

            successConfirm = successConfirm == null ? "" : successConfirm.trim();
//            failedConfirm = failedConfirm == null ? "" : failedConfirm.trim();

//            int tempConfirmStatus = 0;
            Transaction transConfirm;
            try {
                for (Map.Entry<String, Transaction> entry : mapTransConfirm.entrySet()) {
                    String transId = entry.getKey();
                    transConfirm = entry.getValue();
                    TransCp transcp = dbProcessor.getTransactionProcessor().getTransByTransId(transId);
                    if (transcp != null && transcp.getConfirmStatus() == null) {
                        boolean isOK = false;
                        for (int i = 0; i < listContentProvider.size(); i++) {
                            Map<String, String> hashInfo = listContentProvider.get(i);
                            Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                            if (transcp.getContentProviderId().compareTo(cpIdStore) == 0) {
                                isOK = true;
                                break;
                            }
                        }
                        if (isOK) {
                            //Thuc hien cap nhat trang thai
                            transcp.setConfirmDate(Calendar.getInstance().getTime());
                            transcp.setUserId(userInfo.getUserInfo().getUserId());

                            if (successConfirm.equals(transConfirm.getTEXTCONFIRMSTATUS())) {
                                transcp.setConfirmStatus(2);
                            } else {
                                transcp.setConfirmStatus(4);
                            }
                            dbProcessor.getTransactionProcessor().doSave(transcp);
                            TransBankplus transBankplus = dbProcessor.getTransactionProcessor().getTransBankplusByTransCpId(transcp.getTransCpId());
                            if (transBankplus != null) {
                                if (TransactionStatus.COMPLETED.getLongVal().compareTo(transcp.getConfirmStatus().longValue()) == 0) {
                                    transBankplus.setCorrectCode("00");
                                    transBankplus.setCorrectDate(Calendar.getInstance().getTime());
                                } else if (TransactionStatus.FAILED.getLongVal().compareTo(transcp.getConfirmStatus().longValue()) == 0) {
                                    transBankplus.setCorrectCode("23");
                                    transBankplus.setCorrectDate(Calendar.getInstance().getTime());
                                }
                                dbProcessor.getTransactionProcessor().doSaveTransBank(transBankplus);
                            }
                            msgError = "success";
                        } else {
                            msgError = "failure";
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error: ", e);
                msgError = "failure";
            }
            if ("success".equals(msgError)) {
                reqContext.showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, message.getString("common.severity.info"), message.getString("confirming.transaction.multi.confirm.msg.success")));
            } else {
                reqContext.showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR, message.getString("common.severity.error"), message.getString("confirming.transaction.multi.confirm.msg.failed")));
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public void showDialogTransConfirm() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
//        Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        try {
            Map<String, Transaction> mapTransConfirm = (Map<String, Transaction>) session.getAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);
            if (mapTransConfirm != null && !mapTransConfirm.isEmpty()) {
                Map<String, Object> options = new HashMap<String, Object>();
                options.put("resizable", false);
                options.put("draggable", false);
                options.put("modal", true);
                RequestContext reqContext = RequestContext.getCurrentInstance();
                reqContext.execute("PF('wdgTransConfirm').show();");
            } else {
//                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleUtils.getMessage("common.severity.warn", locale), ResourceBundleUtils.getMessage("confirming.transaction.show.dialog.trans.confirm", locale)));
//                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message.getString("common.severity.warn"), message.getString("confirming.transaction.show.dialog.trans.confirm")));
                FacesMessage msgObj = new FacesMessage(FacesMessage.SEVERITY_WARN, message.getString("common.severity.warn"), message.getString("confirming.transaction.show.dialog.trans.confirm"));
                RequestContext.getCurrentInstance().showMessageInDialog(msgObj);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }
}
