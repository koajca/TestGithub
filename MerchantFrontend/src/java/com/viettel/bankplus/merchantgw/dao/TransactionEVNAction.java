/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bankplus.merchantgw.dao;

import com.viettel.action.BaseAction;
import com.viettel.action.JsonData;
import com.viettel.bankplus.merchantgw.dao.entities.Transaction;
import com.viettel.bankplus.merchantgw.form.TransactionForm;
import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.DateTimeUtils;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pham
 */
public class TransactionEVNAction extends BaseAction {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TransactionEVNAction.class);
    TransactionForm transactionEVNForm = new TransactionForm();
    DBProcessor dbProcessor = new DBProcessor();
    public JsonData json = new JsonData();

    public TransactionForm getTransactionEVNForm() {
        return transactionEVNForm;
    }

    public void setTransactionEVNForm(TransactionForm transactionEVNForm) {
        this.transactionEVNForm = transactionEVNForm;
    }
    String INDEX_PAGE = "indexSuccess";
    String SEARCH_RESULT_PAGE = "searchResult";
    String TRANSACTION_DETAIL_PAGE = "transactionDetail";

    public String getIndexPage() {
        log.info("TransactionEVNAction.getIndexPage");
        if (isLoggedIn()) {

            if (!getCpCode().equals("EVNHCM")) {

                return "notRules";
            } else {
                return INDEX_PAGE;
            }

        } else {
            return "loginIndex";
        }
    }

    public String onSearch() {
        log.info("TransactionEVNAction.onSearch");
        String[] customInfo = new String[2];
        customInfo[0] = "success";
        customInfo[1] = "";

        Date fromDate = DateTimeUtils.convertStringToDate(transactionEVNForm.getFromDate().trim() + " 00:00:00", "dd/MM/yyyy HH:mm:ss");
        Date toDate = DateTimeUtils.convertStringToDate(transactionEVNForm.getToDate().trim() + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
        if (fromDate == null || toDate == null) {
            customInfo[0] = "failure";
            customInfo[1] = "Ngày tháng nhập không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;

        }

        String bankCode = "";
        if (!CommonUtils.isNullOrEmpty(transactionEVNForm.getBankCode()) && !transactionEVNForm.getBankCode().equalsIgnoreCase("NONE")) {
            
            bankCode = transactionEVNForm.getBankCode().toUpperCase();
            
        }
       
        List<Map<String, Object>> listTrans = dbProcessor.getTransactionProcessor().getTransEVN(toDate,fromDate,bankCode);
        getRequest().getSession().setAttribute("listTransactionEVN", listTrans);
        getRequest().getSession().setAttribute("listTransactionEVN.fromDate", transactionEVNForm.getFromDate());
        getRequest().getSession().setAttribute("listTransactionEVN.toDate", transactionEVNForm.getToDate());
        json.setCustomInfo(customInfo);
        json.setItems(listTrans);
        return SUCCESS;
    }
}
