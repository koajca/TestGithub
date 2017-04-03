/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bankplus.merchantgw.dao;

import com.viettel.action.BaseAction;
import com.viettel.action.JsonData;
import com.viettel.bankplus.merchantgw.form.TransactionForm;
import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.DateTimeUtils;
import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransBankplus;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author anhld4
 */
public class ConfirmTransFixAction extends BaseAction {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ConfirmTransFixAction.class);
    String INDEX_PAGE = "indexSuccess";
    String SEARCH_RESULT_PAGE = "searchResult";
    String TRANSACTION_DETAIL_PAGE = "transactionDetail";
    DBProcessor dbProcessor = new DBProcessor();
    TransactionForm transactionConfirmForm = new TransactionForm();
    public JsonData json = new JsonData();
    List<HashMap<String, String>> listContentProvider = new ArrayList<HashMap<String, String>>();

    public String display() {
        log.info("TransactionAction.display");
        if (getUserRole().equals(ROLE_ADMIN)) {
            return INDEX_PAGE;
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

        HashMap param = new HashMap();
        String sqlQuery = "select tb.trans_bankplus_id, tb.request_id, tb.original_request_id, to_char(tb.request_date, 'dd/MM/yyyy hh24:mi:ss') request_Date, to_char(otb.request_date, 'dd/MM/yyyy hh24:mi:ss') original_request_Date,"
                + " tb.billing_code, otb.billing_code original_billing_code, tb.amount, otb.amount original_amount, tb.trans_status "
                + "from trans_bankplus tb left join trans_bankplus otb on tb.original_request_id = otb.request_id ";
        String where = "";
        where += "where tb.process_code = '300011' and tb.tid_number in ('FIX','CANCELEXT')";
        where += " and tb.request_date > to_date(:fromDate,'dd/MM/yyyy hh24:mi:ss')";
        where += " and tb.request_date < to_date(:toDate,'dd/MM/yyyy hh24:mi:ss')";
//        param.put("fromDate", fromDate);
//        param.put("toDate", toDate);
        param.put("fromDate", transactionConfirmForm.getFromDate());
        param.put("toDate", transactionConfirmForm.getToDate().trim() + " 23:59:59");
        if (!transactionConfirmForm.getTransStatus().equals("NONE")) {
            where += " and tb.trans_status = :status";
            param.put("status", transactionConfirmForm.getTransStatus());
        }
        getListCp();
        ArrayList<String> listCpId = new ArrayList();
        for (int i = 0; i < listContentProvider.size(); i++) {
            HashMap<String, String> hashInfo = listContentProvider.get(i);
            listCpId.add(hashInfo.get("CP_CODE"));
        }
        where += " and tb.cp_code in (:cpId) ";
        param.put("cpId", listCpId);
        sqlQuery += where;
        log.debug(sqlQuery);
        SQLQuery query = DAOFactory.getCurrentSessionAndBeginTransaction().createSQLQuery(sqlQuery);
        query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        for (Object object : param.keySet()) {
            String key = object.toString();
            Object val = param.get(key);
            if (val instanceof ArrayList) { //For select in
                query.setParameterList(key, (ArrayList) val);
            } else {
                query.setParameter(key, param.get(key));
            }
        }
        List listTrans = query.list();
        DAOFactory.commitCurrentSessions();
        getRequest().getSession().setAttribute("listTransactionConfirm.fromDate", transactionConfirmForm.getFromDate());
        getRequest().getSession().setAttribute("listTransactionConfirm.toDate", transactionConfirmForm.getToDate());
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
                    TransBankplus transBp = getTransBankplusByTransBpId(Long.parseLong(transId));
                    if (transBp != null && transBp.getTransStatus().equals("1")
                            && (value.equals("2") || value.equals("3"))) {
                        boolean isOK = false;
                        for (int i = 0; i < listContentProvider.size(); i++) {
                            HashMap<String, String> hashInfo = listContentProvider.get(i);
                            String cpCode = hashInfo.get("CP_CODE");
                            if (transBp.getCpCode().compareTo(cpCode) == 0) {
                                isOK = true;
                                break;
                            }
                        }
                        if (isOK) {
                            transBp.setTransStatus(value);
                            if (value.equals("3")) {
                                transBp.setErrorCode("23");
                                transBp.setCorrectCode("23");
                            }
                            dbProcessor.getTransactionProcessor().doSaveTransBank(transBp);
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

    public TransBankplus getTransBankplusByTransBpId(Long transBpId) {
        TransBankplus trans = (TransBankplus) DAOFactory.getCurrentSessionAndBeginTransaction().createCriteria(TransBankplus.class)
                .add(Restrictions.eq("transBankplusId", transBpId))
                .uniqueResult();
        DAOFactory.commitCurrentSessions();
        return trans;
    }

    private void getListCp() {
        getListCp(getCpId());
    }

    private void getListCp(Long startWith) {
        //Long cpId = getCpId();
        listContentProvider = dbProcessor.getProviderProcessor().getTreeProvider(startWith);
        log.info(listContentProvider);
    }

    public TransactionForm getTransactionConfirmForm() {
        return transactionConfirmForm;
    }

    public void setTransactionConfirmForm(TransactionForm transactionConfirmForm) {
        this.transactionConfirmForm = transactionConfirmForm;
    }

    public List<HashMap<String, String>> getListContentProvider() {
        return listContentProvider;
    }

    public void setListContentProvider(List<HashMap<String, String>> listContentProvider) {
        this.listContentProvider = listContentProvider;
    }
}
