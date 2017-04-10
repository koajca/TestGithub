/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bankplus.merchantgw.beans;

import com.viettel.bankplus.merchantgw.beans.entities.Transaction;
import com.viettel.bankplus.merchantgw.beans.entities.UserLoginInfo;
import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.Constants;
import com.viettel.common.util.DateTimeUtils;
import com.viettel.common.util.ExportExcelUtils;
import com.viettel.common.util.StringProcess;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransCp;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author ducnt28
 */
@ManagedBean
@ViewScoped
public class SearchTransactionBank extends BaseBean implements Serializable {

    private Date fromDate;
    private Date toDate;
    private String transIdBank;
    private String chooseCp;
    private String chooseStatus;
    private String requestId;
    private String requestIdCp;
    private String billingCode;

    private static final Logger log = Logger.getLogger(SearchTransactionBank.class);
    List<HashMap<String, String>> listContentProvider;
    private List<Transaction> lstTransaction;
    private List<ContentProvider> lstContentProvider;
    List<TransCp> listTransHis = new ArrayList<TransCp>();
    Transaction trans = new Transaction();

//    private String refunAmount;
//    private String refundReason;
//    public String getRefundReason() {
//        return refundReason;
//    }
//
//    public void setRefundReason(String refundReason) {
//        this.refundReason = refundReason;
//    }
//
//    public String getRefunAmount() {
//        return refunAmount;
//    }
//
//    public void setRefunAmount(String refunAmount) {
//        this.refunAmount = refunAmount;
//    }
    private StreamedContent file;

    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }

    public void click() {
        RequestContext requestContext = RequestContext.getCurrentInstance();

        requestContext.update("form:display");
        requestContext.execute("PF('dlg').show()");
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

    private void getListCp(Long startWith) {
        listContentProvider = dbProcessor.getProviderProcessor().getTreeProvider(startWith);
        log.info("SearchTransactionBank.getListCp(Long startWith): " + listContentProvider);
    }

    private List getListCpNew(Long startWith) {
        return dbProcessor.getProviderProcessor().getTreeProvider(startWith);
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

    public List<Transaction> getLstTransaction() {
        return lstTransaction;
    }

    public void doSearch() {

        FacesMessage msg = new FacesMessage(message.getString("search.error.full"));
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        if (fromDate == null || "".equalsIgnoreCase(fromDate.toString())) {
            log.info("SearchTransactionBank.doSearch():fromDate is NULL");
            msg.setSummary(message.getString("error.frmDateNull"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (toDate == null || "".equalsIgnoreCase(toDate.toString())) {
            log.info("SearchTransactionBank.doSearch():toDate is NULL");
            msg.setSummary(message.getString("error.toDateNull"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (fromDate.after(toDate)) {
            log.info("SearchTransactionBank.doSearch():fromDate > toDate");
            msg.setSummary(message.getString("error.frDatetoDate"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        try {
            String where = "";
            where += "where cp.request_date > :fromDate";
            where += " and cp.request_date < :toDate";
            where += " and bp.request_date > :fromDate";
            where += " and bp.request_date < :toDate";
            where += " and cp.content_provider_id in (:cpId)";
            where += " and bp.process_code = '300001'";
            HashMap hash = new HashMap();

            if (!CommonUtils.isNullOrEmpty(this.billingCode)) {
                where += " and lower(bp.billing_code) = :billingCode";
                hash.put("billingCode", this.billingCode.toLowerCase());
            }
            if (!CommonUtils.isNullOrEmpty(this.requestId)) {
                where += " and cp.trans_id = :transId";
                hash.put("transId", this.requestId);
            }
            if (!CommonUtils.isNullOrEmpty(this.requestIdCp)) {
                where += " and lower(cp.order_id) = :orderId";
                hash.put("orderId", this.requestIdCp.toLowerCase());
            }
            if (!CommonUtils.isNullOrEmpty(this.transIdBank)) {
                where += " and bp.request_id = :bankRequestId";
                hash.put("bankRequestId", this.transIdBank);
            }
            if (!CommonUtils.isNullOrEmpty(this.chooseStatus) && !"NONE".equalsIgnoreCase(this.chooseStatus)) {
                if ("1".equals(this.chooseStatus)) {
                    where += " and (bp.error_code is null or bp.error_code = '32')"
                            + " and (bp.correct_code is null or bp.correct_code = '32')";
                } else if ("2".equals(this.chooseStatus)) {
                    where += " and (bp.error_code = '00' or bp.correct_code = '00')";
                } else {
                    where += " and cp.trans_status = :transStatus";
                    hash.put("transStatus", this.chooseStatus);
                }
            }

            if (!CommonUtils.isNullOrEmpty(this.chooseCp) && !"-1".equals(this.chooseCp)) {
                ArrayList<Long> listCpId = new ArrayList();
                boolean isOK = false;
                for (int i = 0; i < listContentProvider.size(); i++) {
                    HashMap<String, String> hashInfo = listContentProvider.get(i);
                    Long cpIdInput = StringProcess.convertToLong(this.chooseCp);
                    Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                    if (cpIdInput.compareTo(cpIdStore) == 0) {
                        isOK = true;
                        break;
                    }
                }
                if (isOK) {
                    List lstCpNew = getListCpNew(Long.parseLong(this.chooseCp));
//                    getListCp(StringProcess.convertToLong(this.chooseCp));
                    for (int i = 0; i < lstCpNew.size(); i++) {
                        HashMap<String, String> hashInfo = (HashMap<String, String>) lstCpNew.get(i);
                        listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
                    }
                    if (!listCpId.isEmpty()) {
                        hash.put("cpId", listCpId);
                    }
                }
            } else {
                ArrayList<Long> listCpId = new ArrayList();
                for (int i = 0; i < listContentProvider.size(); i++) {
                    HashMap<String, String> hashInfo = listContentProvider.get(i);
                    listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
                }
                hash.put("cpId", listCpId);
            }

            log.info("SearchTransactionBank.doSearch():WHERE CLAUSE: " + where);
            hash.put("fromDate", fromDate);
            hash.put("toDate", toDate);
            lstTransaction = dbProcessor.getTransactionProcessor().getTrans(where, hash);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void viewDetailHistory(String requestId) {

        log.info("SearchTransactionBank.viewDetailHistory(String requestId)");
        if (lstTransaction != null && lstTransaction.size() > 0) {
            for (Transaction transaction : lstTransaction) {
                if (transaction.getTRANSID().equalsIgnoreCase(requestId)) {
                    trans = transaction;
                    break;
                }
            }
        }
        if (trans != null) {
//            this.refunAmount = trans.getAMOUNT().toString();
            //Lay danh sach giao dich lien quan
            listTransHis = dbProcessor.getTransactionProcessor().getTransByOriginTransId(requestId);
            TransCp originTrans = dbProcessor.getTransactionProcessor().getTransByTransId(requestId);
            listTransHis.add(0, originTrans);
        }
    }

    public StreamedContent exportExcel() {
        StreamedContent result = null;
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            UserLoginInfo loginInfo = (UserLoginInfo) session.getAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO);
            Map<String, String> mapData = new HashMap<String, String>();
            ResourceBundle rb = ResourceBundle.getBundle("config");
            String templateFolder = rb.getString("template_excel");
            String reportFolder = rb.getString("export_output");

            String reportFileName = "transaction_" + DateTimeUtils.convertDateToString(new Date(), "yyMMddHHmmss") + ".xls";
            String reportFilePath = reportFolder + reportFileName;
            String templateFilePath = templateFolder + "transaction.xls";

            String fromDateTemp = DateTimeUtils.convertDateToString(fromDate, "dd-MM-yyyy");
            String toDateTemp = DateTimeUtils.convertDateToString(toDate, "dd-MM-yyyy");
            mapData.put("fromDate", fromDateTemp);
            mapData.put("toDate", toDateTemp);

            List<Transaction> listData = lstTransaction;
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
                            transaction.setTEXTTRANSSTATUS("Chưa xử lý");
                        } else if ("1".equals(transaction.getTRANSSTATUS())) {
                            transaction.setTEXTTRANSSTATUS("Đang xử lý");
                        } else if ("2".equals(transaction.getTRANSSTATUS())) {
                            transaction.setTEXTTRANSSTATUS("Thành công");
                        } else if ("3".equals(transaction.getTRANSSTATUS())) {
                            transaction.setTEXTTRANSSTATUS("Đã hủy");
                        } else if ("4".equals(transaction.getTRANSSTATUS())) {
                            transaction.setTEXTTRANSSTATUS("Thất bại");
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

//    public void doRefund() {
//
//        log.info("SearchTransactionBank.doRefund()");
//        FacesMessage msg = new FacesMessage(message.getString("search.error.full"));
//        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
//        if (refundReason == null || "".equalsIgnoreCase(refundReason)) {
//            log.info("SearchTransactionBank.doRefund():refundReason is NULL");
//            msg.setSummary("Lý do không thể bỏ trống");
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//            return;
//        }
//
//        long userId = 0L;
//        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
//        UserLoginInfo loginInfo = (UserLoginInfo) session.getAttribute("userLoginInfo");
//        if (loginInfo != null && loginInfo.getCpCode() != null) {
//            userId = loginInfo.getUserInfo().getUserId();
//        } else {
//            log.info("SearchTransactionBank.doRefund():loginInfo is NULL");
//            msg.setSummary("Thông tin giao dịch không đúng!");
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//            return;
//        }
//
//        if (getUserRole().equals(ROLE_ADMIN)) {
//            log.info("SearchTransactionBank.doRefund():transId: " + requestId);
//            if (!CommonUtils.isNullOrEmpty(requestId)) {
//                //Check giao dich da duoc hoan tien hay chua
//                List<TransCp> list = dbProcessor.getTransactionProcessor().getTransByOriginTransId(requestId);
//                if (list != null && list.size() > 0) {
//                    for (TransCp transCp : list) {
//                        if (transCp.getTransType().compareTo(TransactionType.CANCEL_PAYMENT.getLongVal()) == 0) {
//                            log.info("SearchTransactionBank.doRefund():Transaction is REFUNDED");
//                            msg.setSummary("Lỗi, giao dịch đã được gửi yêu cầu hoàn tiền trước đó");
//                            FacesContext.getCurrentInstance().addMessage(null, msg);
//                            return;
//                        }
//                    }
//                }
//
//                TransCp transCp = dbProcessor.getTransactionProcessor().getTransByTransId(requestId);
//                if (transCp != null) {
//                    getListCp();
//                    boolean isOK = false;
//                    for (int i = 0; i < listContentProvider.size(); i++) {
//                        HashMap<String, String> hashInfo = listContentProvider.get(i);
//                        Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
//                        if (transCp.getContentProviderId().compareTo(cpIdStore) == 0) {
//                            isOK = true;
//                            break;
//                        }
//                    }
//                    if (isOK) {
//                        //Thuc hien tao ban tin refund
//                        SessionManager sm = getSessionManagerId();
//                        if (sm == null) {
//                            sm = createSessionManager();
//                        }
//                        String id = CommonUtils.generateTransId();
//                        TransCp cancelTransCp = new TransCp();
//                        cancelTransCp.setAmount(transCp.getAmount());
//                        cancelTransCp.setBillingCode(transCp.getBillingCode());
//                        cancelTransCp.setContentProviderId(transCp.getContentProviderId());
//                        cancelTransCp.setMsisdn(transCp.getMsisdn());
//                        cancelTransCp.setOrderId(transCp.getOrderId());
//                        cancelTransCp.setOrderInfo(transCp.getOrderInfo());
//                        cancelTransCp.setTransId(id);
//                        cancelTransCp.setRequestDate(Calendar.getInstance().getTime());
//                        cancelTransCp.setUpdatedDate(Calendar.getInstance().getTime());
//                        cancelTransCp.setSessionManagerId(sm.getSessionManagerId());
//                        cancelTransCp.setTransStatus(TransactionStatus.PENDING.getLongVal().toString());
//                        cancelTransCp.setTransType(TransactionType.CANCEL_PAYMENT.getLongVal());
//                        cancelTransCp.setSecondCpId(transCp.getSecondCpId());
//                        cancelTransCp.setOriginalTransId(requestId);
//                        cancelTransCp.setNote(refundReason);
//                        cancelTransCp.setUserId(userId);
//
//                        cancelTransCp = dbProcessor.getTransactionProcessor().doSave(cancelTransCp);
//                        if (cancelTransCp != null) {
//                            log.info("SearchTransactionBank.doRefund() cancelTransCp is not NULL");
//                            msg.setSummary("Xác nhận giao dịch thành công");
//                            FacesContext.getCurrentInstance().addMessage(null, msg);
//                        }
//
//                        String genRequestId = generateRquestId();
//                        TransBankplus transBankOld = dbProcessor.getTransBankplusProcessor().getTransBankplusByTransCpId(requestId);
//                        TransBankplus transbank = new TransBankplus();
//                        transbank.setAmount(transCp.getAmount());
//                        transbank.setBankCode(transBankOld.getBankCode());
//                        transbank.setTransCpId(cancelTransCp.getTransCpId());
//                        transbank.setRequestId(genRequestId);
//                        transbank.setBillingCode(transCp.getBillingCode());
//                        ContentProvider cp = dbProcessor.getProviderProcessor().getProviderById(getCpId());
//                        transbank.setCpCode(transBankOld.getCpCode());
//                        transbank.setRequestDate(Calendar.getInstance().getTime());
//                        transbank.setOriginalRequestId(transBankOld.getRequestId());
//                        transbank.setTransStatus(TransactionStatus.PENDING.getLongVal().toString());
//
//                        transbank = dbProcessor.getTransBankplusProcessor().doSave(transbank);
//                        if (transbank != null) {
//                            log.info("SearchTransactionBank.doRefund() transbank is not NULL");
//                            msg.setSummary("Xác nhận giao dịch thành công");
//                            FacesContext.getCurrentInstance().addMessage(null, msg);
//                        }
//                    } else {
//                        log.info("SearchTransactionBank.doRefund():isOK is NOK @@");
//                        msg.setSummary("Thông tin giao dịch không đúng!");
//                        FacesContext.getCurrentInstance().addMessage(null, msg);
//                    }
//                } else {
//                    log.info("SearchTransactionBank.doRefund():tranCp is NULL");
//                    msg.setSummary("Thông tin giao dịch không đúng!");
//                    FacesContext.getCurrentInstance().addMessage(null, msg);
//                }
//            } else {
//                log.info("SearchTransactionBank.doRefund():RequestId is NULL");
//                msg.setSummary("Thông tin giao dịch không đúng!");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//            }
//        }
//    }
//    public static String generateRquestId() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
//        String id = sdf.format(Calendar.getInstance().getTime());
//        id += "24";
//
//        String strSeq;
//        Random rand = new Random();
//        strSeq = rand.nextInt(9999999) + "";
//        strSeq = StringProcess.padLeft(strSeq, 7, '0');
//        id += strSeq;
//        return id;
//    }
    public StreamedContent getFile() {
        this.file = this.exportExcel();
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    Transaction transDetail;

    public Transaction getTransDetail() {
        return transDetail;
    }

    public void setTransDetail(Transaction transDetail) {
        this.transDetail = transDetail;
    }

    public void viewDetail(Transaction transDetail) {
        this.transDetail = transDetail;
        viewDetailHistory(transDetail.getTRANSID());
    }

    public List<ContentProvider> getLstContentProvider() {
        return lstContentProvider;
    }

    public void setLstContentProvider(List<ContentProvider> lstContentProvider) {
        this.lstContentProvider = lstContentProvider;
    }

    public List<HashMap<String, String>> getListContentProvider() {
        return listContentProvider;
    }

    public void setListContentProvider(List<HashMap<String, String>> listContentProvider) {
        this.listContentProvider = listContentProvider;
    }

    public void setLstTransaction(List<Transaction> lstTransaction) {
        this.lstTransaction = lstTransaction;
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

    public String getTransIdBank() {
        return transIdBank;
    }

    public void setTransIdBank(String transIdBank) {
        this.transIdBank = transIdBank;
    }

    public String getChooseCp() {
        return chooseCp;
    }

    public void setChooseCp(String chooseCp) {
        this.chooseCp = chooseCp;
    }

    public String getChooseStatus() {
        return chooseStatus;
    }

    public void setChooseStatus(String chooseStatus) {
        this.chooseStatus = chooseStatus;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestIdCp() {
        return requestIdCp;
    }

    public void setRequestIdCp(String requestIdCp) {
        this.requestIdCp = requestIdCp;
    }

    public String getBillingCode() {
        return billingCode;
    }

    public void setBillingCode(String billingCode) {
        this.billingCode = billingCode;
    }

    public DBProcessor getDbProcessor() {
        return dbProcessor;
    }

    public void setDbProcessor(DBProcessor dbProcessor) {
        this.dbProcessor = dbProcessor;
    }

    public List<TransCp> getListTransHis() {
        return listTransHis;
    }

    public void setListTransHis(List<TransCp> listTransHis) {
        this.listTransHis = listTransHis;
    }

    public Transaction getTrans() {
        return trans;
    }

    public void setTrans(Transaction trans) {
        this.trans = trans;
    }

}
