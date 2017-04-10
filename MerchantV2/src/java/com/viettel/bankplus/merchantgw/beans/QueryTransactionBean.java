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
import com.viettel.common.util.ExportExcelUtils;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransCp;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author os_phucpt1
 */
@ManagedBean(name = "queryTransactionBean")
@ViewScoped
public class QueryTransactionBean extends BaseBean implements Serializable {

    private Logger log = Logger.getLogger(QueryTransactionBean.class);
    private DBProcessor dbProcessor = new DBProcessor();
    private TransForm transForm;
    private String SESS_ATTR_LIST_CP = "listCp";
    private List<Transaction> listTrans;
    private StreamedContent file;
    private Transaction transDetail;
    List<TransCp> listTransHis = new ArrayList<TransCp>();

    public List<TransCp> getListTransHis() {
        return listTransHis;
    }

    public void setListTransHis(List<TransCp> listTransHis) {
        this.listTransHis = listTransHis;
    }

    public Transaction getTransDetail() {
        return transDetail;
    }

    public void setTransDetail(Transaction transDetail) {
        this.transDetail = transDetail;
    }

    public StreamedContent getFile() {
        this.file = this.exportExcel();
        return file;
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
            UserLoginInfo userInfo = (UserLoginInfo) session.getAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO);
            transForm = transForm == null ? (new TransForm()) : transForm;
            transForm.setFromDate(DateTimeUtils.getFirstDate());
            transForm.setToDate(new Date());
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
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        String msgError = "";
        try {
            if (this.transForm != null) {
                Date now = DateTimeUtils.getStartDate(new Date());
                if (now.compareTo(this.transForm.getFromDate()) < 0) {
                    msgError = message.getString("query.transaction.from.date.not.larger.than.sys.date");
                } else if (now.compareTo(this.transForm.getToDate()) < 0) {
                    msgError = message.getString("query.transaction.to.date.not.larger.than.sys.date");
                } else if (this.transForm.getFromDate() != null && this.transForm.getFromDate().compareTo(this.transForm.getToDate()) > 0) {
                    msgError = message.getString("query.transaction.from.date.not.larger.than.to.date");
                } else {
                    msgError = this.validateData(transForm);
                    if ("".equals(msgError)) {
                        listTrans = this.getListTransaction(this.transForm);
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

    private String validateData(TransForm form) throws Exception {
        String regexTransId = config.getString("query.trans.regex.transId");
        String regexCpTransId = config.getString("query.trans.regex.cpTransId");
        String regexPaymentId = config.getString("query.trans.regex.paymentId");

        String msgTransId = message.getString("query.transaction.transId.invalid");
        String msgCpTransId = message.getString("query.transaction.cpTransId.invalid");
        String msgPaymentId = message.getString("query.transaction.paymentId.invalid");
        try {
            if (!CommonUtils.validateByRegex(form.getTransId(), regexTransId)) {
                return msgTransId;
            }
            if (!CommonUtils.validateByRegex(form.getCpTransId(), regexCpTransId)) {
                return msgCpTransId;
            }
            if (!CommonUtils.validateByRegex(form.getPaymentId(), regexPaymentId)) {
                return msgPaymentId;
            }
        } catch (Exception e) {
            throw e;
        }
        return "";
    }

    private List<Transaction> getListTransaction(TransForm form) throws Exception {
        List<Transaction> result;
        try {
            String where = "";
            where += "where cp.request_date > :fromDate";
            where += " and cp.request_date < :toDate";
            where += " and bp.request_date > :fromDate";
            where += " and bp.request_date < :toDate";
            where += " and bp.process_code = '300001'";
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            HashMap hash = new HashMap();
            String cpId;
//            if (!CommonUtils.isNullOrEmpty(form.getCpName())) {
//                List l = (List) session.getAttribute(this.SESS_ATTR_LIST_CP);
//                for (int i = 0; i < l.size(); i++) {
//                    Map obj = (Map) l.get(i);
//                    temp = obj.get("CP_NAME") == null ? "" : obj.get("CP_NAME").toString().trim();
//                    if (temp.equalsIgnoreCase(form.getCpName().trim())) {
//                        where += " and cp.content_provider_id in (:cpId)";
//                        hash.put("cpId", obj.get("CONTENT_PROVIDER_ID") == null ? "" : obj.get("CONTENT_PROVIDER_ID").toString().trim());
//                    }
//                }
//            }
            if (!CommonUtils.isNullOrEmpty(form.getCpId())) {
                List l = (List) session.getAttribute(this.SESS_ATTR_LIST_CP);
                List listTempCpId = new ArrayList();
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
            if (!CommonUtils.isNullOrEmpty(form.getBankCode())) {
                where += " and upper(bp.bank_code) = :bankCode";
                hash.put("bankCode", form.getBankCode().toUpperCase());
            }
            if (!CommonUtils.isNullOrEmpty(form.getPaymentId())) {
                where += " and lower(bp.billing_code) = :billingCode";
                hash.put("billingCode", form.getPaymentId().toLowerCase());
            }
            if (!CommonUtils.isNullOrEmpty(form.getCpTransId())) {
                where += " and lower(cp.order_id) = :orderId";
                hash.put("orderId", form.getCpTransId().toLowerCase());
            }
            if (!CommonUtils.isNullOrEmpty(form.getTransId())) {
                where += " and cp.trans_id = :transId";
                hash.put("transId", form.getTransId());
            }
            if (!CommonUtils.isNullOrEmpty(form.getStatus())) {
                if ("1".equals(form.getStatus())) {//Timeout
                    where += " and (bp.error_code is null or bp.error_code = '32')"
                            + " and (bp.correct_code is null or bp.correct_code = '32')";
                } else if ("2".equals(form.getStatus())) {//Thanh cong
                    where += " and (bp.error_code = '00' or bp.correct_code = '00')";
                } else {
                    where += " and cp.trans_status = :transStatus";
                    hash.put("transStatus", form.getStatus());
                }
            }
            hash.put("fromDate", form.getFromDate());
            hash.put("toDate", DateTimeUtils.getNextDay(form.getToDate()));
            result = dbProcessor.getTransactionProcessor().getTrans(where, hash);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    public StreamedContent exportExcel() {
        StreamedContent result = null;
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
//        Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        String msgError = "";
        try {
            if (this.transForm != null) {
                Date now = DateTimeUtils.getStartDate(new Date());
                if (now.compareTo(this.transForm.getFromDate()) < 0) {
                    msgError = message.getString("query.transaction.from.date.not.larger.than.sys.date");
                } else if (now.compareTo(this.transForm.getToDate()) < 0) {
                    msgError = message.getString("query.transaction.to.date.not.larger.than.sys.date");
                } else if (this.transForm.getFromDate() != null && this.transForm.getFromDate().compareTo(this.transForm.getToDate()) > 0) {
                    msgError = message.getString("query.transaction.from.date.not.larger.than.to.date");
                } else {
                    msgError = this.validateData(transForm);
                    if ("".equals(msgError)) {
                        List<Transaction> listData = this.getListTransaction(this.transForm);
                        UserLoginInfo loginInfo = (UserLoginInfo) session.getAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO);
                        Map<String, String> mapData = new HashMap<String, String>();
                        ResourceBundle rb = ResourceBundle.getBundle("config");
                        String templateFolder = rb.getString("template_excel");
                        String reportFolder = rb.getString("export_output");

                        String reportFileName = "transaction_" + DateTimeUtils.convertDateToString(new Date(), "yyMMddHHmmss") + ".xls";
                        String reportFilePath = reportFolder + reportFileName;
                        String templateFilePath = templateFolder + "transaction.xls";

                        String fromDateTemp = DateTimeUtils.convertDateToString(transForm.getFromDate(), "dd-MM-yyyy");
                        String toDateTemp = DateTimeUtils.convertDateToString(transForm.getToDate(), "dd-MM-yyyy");
                        mapData.put("fromDate", fromDateTemp);
                        mapData.put("toDate", toDateTemp);
                        if (listData != null) {
                            for (Transaction transaction : listData) {
                                if (transaction.getCONFIRMSTATUS() != null) {
                                    if (transaction.getCONFIRMSTATUS().compareTo(new BigDecimal("2")) == 0) {
                                        transaction.setTEXTCONFIRMSTATUS("Thành công");
                                    }
                                    if (transaction.getCONFIRMSTATUS().compareTo(new BigDecimal("4")) == 0) {
                                        transaction.setTEXTCONFIRMSTATUS("Thất bại");
                                    }
                                } else {
                                    transaction.setTEXTCONFIRMSTATUS("");
                                }
                                if (transaction.getTRANSSTATUS() != null) {
                                    if ("0".equals(transaction.getTRANSSTATUS())) {
//                            transaction.setTEXTTRANSSTATUS("Chưa xử lý");
                                        transaction.setTEXTTRANSSTATUS(lang.getString("searchTransactionBank.lbl.trans.status.suspending"));
                                    } else if ("1".equals(transaction.getTRANSSTATUS())) {
//                            transaction.setTEXTTRANSSTATUS("Đang xử lý");
                                        transaction.setTEXTTRANSSTATUS(lang.getString("searchTransactionBank.lbl.trans.status.processing"));
                                    } else if ("2".equals(transaction.getTRANSSTATUS())) {
//                            transaction.setTEXTTRANSSTATUS("Thành công");
                                        transaction.setTEXTTRANSSTATUS(lang.getString("searchTransactionBank.lbl.trans.status.success"));
                                    } else if ("3".equals(transaction.getTRANSSTATUS())) {
//                            transaction.setTEXTTRANSSTATUS("Đã hủy");
                                        transaction.setTEXTTRANSSTATUS(lang.getString("searchTransactionBank.lbl.trans.status.cancelled"));
                                    } else if ("4".equals(transaction.getTRANSSTATUS())) {
//                            transaction.setTEXTTRANSSTATUS("Thất bại");
                                        transaction.setTEXTTRANSSTATUS(lang.getString("searchTransactionBank.lbl.trans.status.failed"));
                                    }
                                } else {
                                    transaction.setTEXTTRANSSTATUS("");
                                }
                            }
                            ContentProvider cp = new DBProcessor().getProviderProcessor().getProviderById(loginInfo.getUserInfo().getContentProviderId());
                            if (cp != null) {
                                mapData.put("cpCode", cp.getCpCode());
                                mapData.put("cpName", cp.getCpName());
                            }

                        }

                        InputStream is = ExportExcelUtils.getInstance().generateMultiSheet(listData, mapData, templateFilePath, reportFilePath);
                        if (is != null) {
                            result = new DefaultStreamedContent(is, "application/vnd.ms-excel", reportFileName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            msgError = e.getMessage();
            log.error("Error: ", e);
        }
        if (!"".equals(msgError)) {
            msg.setSummary(msgError);
            FacesContext.getCurrentInstance().addMessage(null, msg);
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

    public void viewDetail(Transaction transDetail) {
        try {
            this.transDetail = getTransInfo(transDetail.getTRANSID());
            String tempMSISDN = CommonUtils.getInstance().hideMSISDN(transDetail.getMSISDN());
            this.transDetail.setMSISDN(tempMSISDN);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public Transaction getTransInfo(String transId) {
        Transaction result = null;
        log.info("TransactionAction.viewDetail");
        if (listTrans != null && !listTrans.isEmpty()) {
            for (Transaction transaction : listTrans) {
                if (transaction.getTRANSID().equalsIgnoreCase(transId)) {
                    result = transaction;
                    break;
                }
            }
            if (result != null) {
                //Lay danh sach giao dich lien quan
                listTransHis = dbProcessor.getTransactionProcessor().getTransByOriginTransId(transId);
                TransCp originTrans = dbProcessor.getTransactionProcessor().getTransByTransId(transId);
                listTransHis.add(0, originTrans);
            }
        }
        return result;
    }
    public static void main(String[] args) {
        Date d = new Date();
        System.out.println("date: "+ d);
        System.out.println("next day: "+ DateTimeUtils.getNextDay(d));
    }
}
