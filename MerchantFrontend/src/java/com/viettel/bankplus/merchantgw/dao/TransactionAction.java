/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.dao;

import com.viettel.action.BaseAction;
import com.viettel.action.JsonData;
import com.viettel.bankplus.merchantgw.dao.entities.Transaction;
import com.viettel.bankplus.merchantgw.form.TransactionForm;
import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.DateTimeUtils;
import com.viettel.common.util.StringProcess;
import com.viettel.common.util.TransactionStatus;
import com.viettel.common.util.TransactionType;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.SessionManager;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransBankplus;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransCp;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransferMerchant;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 11, 2014
 * @version 1.0
 */
public class TransactionAction extends BaseAction {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TransactionAction.class);
    TransactionForm transactionForm = new TransactionForm();
    TransactionForm transactionConfirmForm = new TransactionForm();
    DBProcessor dbProcessor = new DBProcessor();
    public JsonData json = new JsonData();
    Transaction trans = new Transaction();
    List<TransCp> listTransHis = new ArrayList<TransCp>();
    TransactionForm transferForm = new TransactionForm();

    List<HashMap<String, String>> listContentProvider = new ArrayList<HashMap<String, String>>();

    public List<HashMap<String, String>> getListContentProvider() {
        return listContentProvider;
    }

    public void setListContentProvider(List<HashMap<String, String>> listContentProvider) {
        this.listContentProvider = listContentProvider;
    }

    public TransactionForm getTransferForm() {
        return transferForm;
    }

    public void setTransferForm(TransactionForm transferForm) {
        this.transferForm = transferForm;
    }

    public Transaction getTrans() {
        return trans;
    }

    public void setTrans(Transaction trans) {
        this.trans = trans;
    }

    public List<TransCp> getListTransHis() {
        return listTransHis;
    }

    public void setListTransHis(List<TransCp> listTransHis) {
        this.listTransHis = listTransHis;
    }

    public TransactionForm getTransactionConfirmForm() {
        return transactionConfirmForm;
    }

    public void setTransactionConfirmForm(TransactionForm transactionConfirmForm) {
        this.transactionConfirmForm = transactionConfirmForm;
    }

    public TransactionForm getTransactionForm() {
        return transactionForm;
    }

    public void setTransactionForm(TransactionForm transactionForm) {
        this.transactionForm = transactionForm;
    }
    String INDEX_PAGE = "indexSuccess";
    String SEARCH_RESULT_PAGE = "searchResult";
    String TRANSACTION_DETAIL_PAGE = "transactionDetail";

    public String getIndexPage() {
        log.info("TransactionAction.getIndexPage");
        if (isLoggedIn()) {
            getListCp();
            return INDEX_PAGE;
        } else {
            return "loginIndex";
        }
    }

    private void getListCp() {
        //Long cpId = getCpId();
        getListCp(getCpId());
    }

    private void getListCp(Long startWith) {
        //Long cpId = getCpId();
        listContentProvider = dbProcessor.getProviderProcessor().getTreeProvider(startWith);
        log.info(listContentProvider);
    }

    public String onSearch() {
        log.info("TransactionAction.onSearch");
        String[] customInfo = new String[2];
        customInfo[0] = "success";
        customInfo[1] = "";

        Date fromDate = DateTimeUtils.convertStringToDate(transactionForm.getFromDate().trim(), DateTimeUtils.patternDateVN);
        Date toDate = DateTimeUtils.convertStringToDate(transactionForm.getToDate().trim() + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
        if (fromDate == null || toDate == null) {
            customInfo[0] = "failure";
            customInfo[1] = "Ngày tháng nhập không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;

        }
        String where = "";
        where += "where cp.request_date > :fromDate";
        where += " and cp.request_date < :toDate";
        where += " and bp.request_date > :fromDate";
        where += " and bp.request_date < :toDate";
        where += " and cp.content_provider_id in (:cpId)";
//        where += " and cp.trans_type = 0";
        where += " and bp.process_code = '300001'";
        HashMap hash = new HashMap();
        if (!CommonUtils.isNullOrEmpty(transactionForm.getBankCode()) && !transactionForm.getBankCode().equalsIgnoreCase("NONE")) {
            where += " and upper(bp.bank_code) = :bankCode";
            hash.put("bankCode", transactionForm.getBankCode().toUpperCase());
        }
        if (!CommonUtils.isNullOrEmpty(transactionForm.getBillingCode())) {
            where += " and lower(bp.billing_code) = :billingCode";
            hash.put("billingCode", transactionForm.getBillingCode().toLowerCase());
        }
        if (!CommonUtils.isNullOrEmpty(transactionForm.getOrderId())) {
            where += " and lower(cp.order_id) = :orderId";
            hash.put("orderId", transactionForm.getOrderId().toLowerCase());
        }
        if (!CommonUtils.isNullOrEmpty(transactionForm.getTransId())) {
            where += " and cp.trans_id = :transId";
            hash.put("transId", transactionForm.getTransId());
        }
        if (!CommonUtils.isNullOrEmpty(transactionForm.getTransStatus()) && !transactionForm.getTransStatus().equalsIgnoreCase("NONE")) {

//            if (value == '0') {
//                        value = 'Chưa xử lý';
//                    } else if (value == '1') {
//                        value = 'Đang xử lý';
//                    } else if (value == '2') {
//                        value = 'Thành công';
//                    } else if (value == '3') {
//                        value = 'Đã hủy';
//                    } else if (value == '4') {
//                        value = 'Thất bại';
//                    } else {
//                        value = '';
//                    }
            if (transactionForm.getTransStatus().equals("1")) {//Timeout
                where += " and (bp.error_code is null or bp.error_code = '32')"
                        + " and (bp.correct_code is null or bp.correct_code = '32')";
            } else if (transactionForm.getTransStatus().equals("2")) {//Thanh cong
                where += " and (bp.error_code = '00' or bp.correct_code = '00')";
            } else {
                where += " and cp.trans_status = :transStatus";
                hash.put("transStatus", transactionForm.getTransStatus());
            }
        }

        getListCp();
        if (!CommonUtils.isNullOrEmpty(transactionForm.getContentProviderId()) && !transactionForm.getContentProviderId().equals("-1")) {
            ArrayList<Long> listCpId = new ArrayList();
            boolean isOK = false;
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                Long cpIdInput = StringProcess.convertToLong(transactionForm.getContentProviderId());
                Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                if (cpIdInput.compareTo(cpIdStore) == 0) {
                    isOK = true;
                    break;
                }
            }
            if (isOK) {
                getListCp(StringProcess.convertToLong(transactionForm.getContentProviderId()));
                for (int i = 0; i < listContentProvider.size(); i++) {
                    HashMap<String, String> hashInfo = listContentProvider.get(i);
                    listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
                }
            }
            if (listCpId.isEmpty()) {
                customInfo[0] = "failure";
                customInfo[1] = "Thông tin CP không đúng!";
                json.setCustomInfo(customInfo);
                return SUCCESS;
            } else {
                hash.put("cpId", listCpId);
            }
        } else {
            ArrayList<Long> listCpId = new ArrayList();
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
            }
            hash.put("cpId", listCpId);
        }
        log.info("WHERE CLAUSE: " + where);

//        hash.put("cpId", getCpId());
        hash.put("fromDate", fromDate);
        hash.put("toDate", toDate);
        List<Transaction> listTrans = dbProcessor.getTransactionProcessor().getTrans(where, hash);
        getRequest().getSession().setAttribute("listTransaction", listTrans);
        getRequest().getSession().setAttribute("listTransaction.fromDate", transactionForm.getFromDate());
        getRequest().getSession().setAttribute("listTransaction.toDate", transactionForm.getToDate());
        json.setCustomInfo(customInfo);
        json.setItems(listTrans);
        return SUCCESS;
    }

    public String viewDetail() {
        log.info("TransactionAction.viewDetail");
        String transId = getRequest().getParameter("transId");
        List<Transaction> listTrans = (List<Transaction>) getRequest().getSession().getAttribute("listTransaction");
        if (listTrans != null && listTrans.size() > 0) {
            for (Transaction transaction : listTrans) {
                if (transaction.getTRANSID().equalsIgnoreCase(transId)) {
                    trans = transaction;
                    break;
                }
            }
        }
        if (trans != null) {
            //Lay danh sach giao dich lien quan
            listTransHis = dbProcessor.getTransactionProcessor().getTransByOriginTransId(transId);
            TransCp originTrans = dbProcessor.getTransactionProcessor().getTransByTransId(transId);
            listTransHis.add(0, originTrans);
        }
        return TRANSACTION_DETAIL_PAGE;
    }

    public String doConfirm() {
        log.info("TransactionAction.doConfirm");
        String[] customInfo = new String[2];
        customInfo[0] = "failure";
        customInfo[1] = "";
        if (getUserRole().equals(ROLE_ADMIN)) {
            String transId = getRequest().getParameter("transId");
            String confirmStatus = getRequest().getParameter("confirmStatus");
            if (!CommonUtils.isNullOrEmpty(transId) && !CommonUtils.isNullOrEmpty(confirmStatus) && CommonUtils.doCheckDigit(confirmStatus)) {
                TransCp transcp = dbProcessor.getTransactionProcessor().getTransByTransId(transId);
                if (transcp != null && transcp.getConfirmStatus() == null) {
                    getListCp();
                    boolean isOK = false;
                    for (int i = 0; i < listContentProvider.size(); i++) {
                        HashMap<String, String> hashInfo = listContentProvider.get(i);
                        Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                        if (transcp.getContentProviderId().compareTo(cpIdStore) == 0) {
                            isOK = true;
                            break;
                        }
                    }
                    if (isOK) {
                        //Thuc hien cap nhat trang thai
                        transcp.setConfirmDate(Calendar.getInstance().getTime());
                        transcp.setConfirmStatus(StringProcess.convertToInt(confirmStatus));
                        transcp.setUserId(getUserId());
                        if (dbProcessor.getTransactionProcessor().doSave(transcp) != null) {
                            //Cap nhat xac nhan giao dich trong bang trans_bankplus
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
                            customInfo[0] = "success";
                        } else {
                            customInfo[0] = "failure";
                            customInfo[1] = "Lỗi kết nối CSDL!";
                        }
                    } else {
                        customInfo[0] = "failure";
                        customInfo[1] = "Thông tin giao dịch không đúng!";
                    }
                } else {
                    customInfo[0] = "failure";
                    customInfo[1] = "Giao dịch không đúng hoặc đã được xác nhận trạng thái!";
                }
            }
        }
        json.setCustomInfo(customInfo);
        return SUCCESS;
    }

    public String doRefund() {
        log.info("TransactionAction.doRefund");
        String[] customInfo = new String[2];
        customInfo[0] = "failure";
        customInfo[1] = "";
        if (getUserRole().equals(ROLE_ADMIN)) {
            String transId = getRequest().getParameter("transId");
            log.info("transId: " + transId);
            String note = getRequest().getParameter("note");
            if (!CommonUtils.isNullOrEmpty(transId)) {
                //Check giao dich da duoc hoan tien hay chua
                List<TransCp> list = dbProcessor.getTransactionProcessor().getTransByOriginTransId(transId);
                if (list != null && list.size() > 0) {
                    for (TransCp transCp : list) {
                        if (transCp.getTransType().compareTo(TransactionType.CANCEL_PAYMENT.getLongVal()) == 0) {
                            customInfo[0] = "failure";
                            customInfo[1] = "Lỗi, giao dịch đã được gửi yêu cầu hoàn tiền trước đó";
                            json.setCustomInfo(customInfo);
                            return SUCCESS;
                        }
                    }
                }

                TransCp transCp = dbProcessor.getTransactionProcessor().getTransByTransId(transId);
//                TransCp transCp = dbProcessor.getTransactionProcessor().getTransBy(getCpId(), transId);
                if (transCp != null) {
                    getListCp();
                    boolean isOK = false;
                    for (int i = 0; i < listContentProvider.size(); i++) {
                        HashMap<String, String> hashInfo = listContentProvider.get(i);
                        Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                        if (transCp.getContentProviderId().compareTo(cpIdStore) == 0) {
                            isOK = true;
                            break;
                        }
                    }
                    if (isOK) {
                        //Thuc hien tao ban tin refund
                        SessionManager sm = getSessionManagerId();
                        if (sm == null) {
                            sm = createSessionManager();
                        }
                        String id = CommonUtils.generateTransId();
                        TransCp cancelTransCp = new TransCp();
                        cancelTransCp.setAmount(transCp.getAmount());
                        cancelTransCp.setBillingCode(transCp.getBillingCode());
//            cancelTransCp.setContentProvider(cp);
                        cancelTransCp.setContentProviderId(transCp.getContentProviderId());
                        cancelTransCp.setMsisdn(transCp.getMsisdn());
                        cancelTransCp.setOrderId(transCp.getOrderId());
                        cancelTransCp.setOrderInfo(transCp.getOrderInfo());
                        cancelTransCp.setTransId(id);
                        cancelTransCp.setRequestDate(Calendar.getInstance().getTime());
                        cancelTransCp.setUpdatedDate(Calendar.getInstance().getTime());
//            cancelTransCp.setSessionManager(sm);
                        cancelTransCp.setSessionManagerId(sm.getSessionManagerId());
                        cancelTransCp.setTransStatus(TransactionStatus.PENDING.getLongVal().toString());
                        cancelTransCp.setTransType(TransactionType.CANCEL_PAYMENT.getLongVal());
                        cancelTransCp.setSecondCpId(transCp.getSecondCpId());
                        cancelTransCp.setOriginalTransId(transId);
                        cancelTransCp.setNote(note);
                        cancelTransCp.setUserId(getUserId());

                        cancelTransCp = dbProcessor.getTransactionProcessor().doSave(cancelTransCp);
                        if (cancelTransCp != null) {
                            customInfo[0] = "success";
                        }

                        String requestId = generateRquestId();
                        TransBankplus transBankOld = dbProcessor.getTransBankplusProcessor().getTransBankplusByTransCpId(transId);
                        TransBankplus transbank = new TransBankplus();
                        transbank.setAmount(transCp.getAmount());
                        transbank.setBankCode(transBankOld.getBankCode());
                        transbank.setTransCpId(cancelTransCp.getTransCpId());
                        transbank.setRequestId(requestId);
                        transbank.setBillingCode(transCp.getBillingCode());
                        ContentProvider cp = dbProcessor.getProviderProcessor().getProviderById(getCpId());
                        transbank.setCpCode(transBankOld.getCpCode());
                        transbank.setRequestDate(Calendar.getInstance().getTime());
                        transbank.setOriginalRequestId(transBankOld.getRequestId());
                        transbank.setTransStatus(TransactionStatus.PENDING.getLongVal().toString());

                        transbank = dbProcessor.getTransBankplusProcessor().doSave(transbank);
                        if (transbank != null) {
                            customInfo[0] = "success";
                        }
                    } else {
                        customInfo[0] = "failure";
                        customInfo[1] = "Thông tin giao dịch không đúng!";
                    }
                } else {
                    customInfo[0] = "failure";
                    customInfo[1] = "Thông tin giao dịch không đúng!";
                }
            } else {
                customInfo[0] = "failure";
                customInfo[1] = "Thông tin giao dịch không đúng!";
            }
        }
        json.setCustomInfo(customInfo);
        return SUCCESS;
    }

    public static String generateRquestId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String id = sdf.format(Calendar.getInstance().getTime());
        id += "24";

        String strSeq;
        Random rand = new Random();
        strSeq = rand.nextInt(9999999) + "";
        strSeq = StringProcess.padLeft(strSeq, 7, '0');
        id += strSeq;
        return id;
    }

    public String initTransConfirm() {
        if (getUserRole().equals(ROLE_ADMIN)) {
            getListCp();
            return "transactionConfirm";
        }
        return "loginIndex";
    }

    public String searchTransConfirm() {
        log.info("TransactionAction.searchTransConfirm");
        String[] customInfo = new String[2];
        customInfo[0] = "success";
        customInfo[1] = "";

        Date fromDate = DateTimeUtils.convertStringToDate(transactionConfirmForm.getFromDate(), DateTimeUtils.patternDateVN);
        Date toDate = DateTimeUtils.convertStringToDate(transactionConfirmForm.getToDate().trim() + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
        if (fromDate == null || toDate == null) {
            customInfo[0] = "failure";
            customInfo[1] = "Ngày tháng nhập không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;

        }
        String where = "";
        HashMap hash = new HashMap();
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
//        hash.put("transStatus", TransactionStatus.PROCESSING.getLongVal());
//        hash.put("transType", TransactionType.PAYMENT.getLongVal());
        if (!CommonUtils.isNullOrEmpty(transactionConfirmForm.getBankCode())
                && !transactionConfirmForm.getBankCode().equalsIgnoreCase("NONE")) {
            where += " and (bp.bank_code) = :bankCode";
            hash.put("bankCode", transactionConfirmForm.getBankCode().toUpperCase());
        }
        if (!CommonUtils.isNullOrEmpty(transactionConfirmForm.getBillingCode())) {
            where += " and lower(bp.billing_code) = :billingCode";
            hash.put("billingCode", transactionConfirmForm.getBillingCode().toUpperCase());
        }
        if (!CommonUtils.isNullOrEmpty(transactionConfirmForm.getOrderId())) {
            where += " and lower(cp.order_id) = :orderId";
            hash.put("orderId", transactionConfirmForm.getOrderId().toLowerCase());
        }
        if (!CommonUtils.isNullOrEmpty(transactionConfirmForm.getTransId())) {
            where += " and cp.trans_id = :transId";
            hash.put("transId", transactionConfirmForm.getTransId());
        }

        getListCp();
        if (!CommonUtils.isNullOrEmpty(transactionConfirmForm.getContentProviderId()) && !transactionConfirmForm.getContentProviderId().equals("-1")) {
            ArrayList<Long> listCpId = new ArrayList();
            boolean isOK = false;
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                Long cpIdInput = StringProcess.convertToLong(transactionConfirmForm.getContentProviderId());
                Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                if (cpIdInput.compareTo(cpIdStore) == 0) {
                    isOK = true;
                    break;
                }
            }
            if (isOK) {
                getListCp(StringProcess.convertToLong(transactionConfirmForm.getContentProviderId()));
                for (int i = 0; i < listContentProvider.size(); i++) {
                    HashMap<String, String> hashInfo = listContentProvider.get(i);
                    listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
                }
            }
            if (listCpId.isEmpty()) {
                customInfo[0] = "failure";
                customInfo[1] = "Thông tin CP không đúng!";
                json.setCustomInfo(customInfo);
                return SUCCESS;
            } else {
                hash.put("cpId", listCpId);
            }
        } else {
            ArrayList<Long> listCpId = new ArrayList();
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
            }
            hash.put("cpId", listCpId);
        }
        log.info("WHERE CLAUSE: " + where);

        hash.put("fromDate", fromDate);
        hash.put("toDate", toDate);
        List<Transaction> listTrans = dbProcessor.getTransactionProcessor().getTrans(where, hash);
        getRequest().getSession().setAttribute("listTransactionConfirm", listTrans);
        json.setCustomInfo(customInfo);
        json.setItems(listTrans);
        json.setTotalRow(listTrans.size());
        return SUCCESS;
    }

    public String multiConfirm() {
        log.info("TransactionAction.multiConfirm");
        String[] customInfo = new String[2];
        customInfo[0] = "failure";
        customInfo[1] = "";
        if (getUserRole().equals(ROLE_ADMIN)) {
            getListCp();
            Map mapParams = getRequest().getParameterMap();
            log.info("ParameterMap: " + mapParams);
            Iterator entries = mapParams.entrySet().iterator();
            while (entries.hasNext()) {
                Entry thisEntry = (Entry) entries.next();
                String key = thisEntry.getKey().toString();
                String[] avalue = (String[]) thisEntry.getValue();
                String value = avalue[0];
                if (key.startsWith("txt") && CommonUtils.doCheckDigit(value)) {
                    String transId = key.substring(3);
                    TransCp transcp = dbProcessor.getTransactionProcessor().getTransByTransId(transId);
                    if (transcp != null && transcp.getConfirmStatus() == null) {
                        boolean isOK = false;
                        for (int i = 0; i < listContentProvider.size(); i++) {
                            HashMap<String, String> hashInfo = listContentProvider.get(i);
                            Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                            if (transcp.getContentProviderId().compareTo(cpIdStore) == 0) {
                                isOK = true;
                                break;
                            }
                        }
                        if (isOK) {
                            //Thuc hien cap nhat trang thai
                            transcp.setConfirmDate(Calendar.getInstance().getTime());
                            transcp.setConfirmStatus(StringProcess.convertToInt(value));
                            transcp.setUserId(getUserId());
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
                            customInfo[0] = "success";
                        } else {
                            customInfo[0] = "failure";
                            customInfo[1] = "Thông tin giao dịch không đúng";
                            break;
                        }
                    }
                }
            }
        }
        json.setCustomInfo(customInfo);
        return SUCCESS;
    }

    String TRANSFER_PAGE = "transfer.page";

    public String transfer() {
        log.info("TransactionAction.getIndexPage");
        if (isLoggedIn()) {
            getListCp();
            return TRANSFER_PAGE;
        } else {
            return "loginIndex";
        }
    }

    public String searchTransfer() {
        log.info("TransactionAction.searchTransfer");
        String[] customInfo = new String[2];
        customInfo[0] = "success";
        customInfo[1] = "";

        Date fromDate = DateTimeUtils.convertStringToDate(transferForm.getFromDate(), DateTimeUtils.patternDateVN);
        Date toDate = DateTimeUtils.convertStringToDate(transferForm.getToDate().trim() + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
        if (fromDate == null || toDate == null) {
            customInfo[0] = "failure";
            customInfo[1] = "Ngày tháng nhập không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;

        }

        getListCp();
        ArrayList<String> listCpCode = new ArrayList();
        if (!CommonUtils.isNullOrEmpty(transferForm.getContentProviderId()) && !transferForm.getContentProviderId().equals("-1")) {

            boolean isOK = false;
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                Long cpIdInput = StringProcess.convertToLong(transferForm.getContentProviderId());
                Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                if (cpIdInput.compareTo(cpIdStore) == 0) {
                    isOK = true;
                    break;
                }
            }
            if (isOK) {
                getListCp(StringProcess.convertToLong(transferForm.getContentProviderId()));
                for (int i = 0; i < listContentProvider.size(); i++) {
                    HashMap<String, String> hashInfo = listContentProvider.get(i);
                    listCpCode.add(hashInfo.get("CP_CODE"));
                }
            }
            if (listCpCode.isEmpty()) {
                customInfo[0] = "failure";
                customInfo[1] = "Thông tin CP không đúng!";
                json.setCustomInfo(customInfo);
                return SUCCESS;
            }
        } else {
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                listCpCode.add(hashInfo.get("CP_CODE"));
            }
        }
        List<TransferMerchant> listTrans = dbProcessor.getTransactionProcessor().getListTransfer(fromDate, toDate, listCpCode);
//        getRequest().getSession().setAttribute("listTransferMerchant", listTrans);
        json.setItems(listTrans);
        json.setTotalRow(listTrans.size());
        json.setCustomInfo(customInfo);
        return SUCCESS;
    }
}
