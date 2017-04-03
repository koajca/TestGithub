/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.dao;

import com.viettel.action.BaseAction;
import com.viettel.action.JsonData;
import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.DateTimeUtils;
import com.viettel.common.util.StringProcess;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 19, 2014
 * @version 1.0
 */
public class ReportAction extends BaseAction {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ReportAction.class);
    String INDEX_PAGE = "indexSuccess";
    String REPORT_TOTAL_PAGE = "reportTotal";
    String REPORT_REFUND_PAGE = "reportRefund";
    String REPORT_TOTAL_EVN_PAGE = "reportTotalEVN";
    DBProcessor dbProcessor = new DBProcessor();
    public JsonData json = new JsonData();

    List<HashMap<String, String>> listContentProvider = new ArrayList<HashMap<String, String>>();

    public List<HashMap<String, String>> getListContentProvider() {
        return listContentProvider;
    }

    public void setListContentProvider(List<HashMap<String, String>> listContentProvider) {
        this.listContentProvider = listContentProvider;
    }

    public String getIndexPage() {
        log.info("ReportAction.getIndexPage");
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
//        listContentProvider = dbProcessor.getProviderProcessor().getTreeProvider(getCpId());
//        log.info(listContentProvider);
    }

    private void getListCp(Long startWith) {
        //Long cpId = getCpId();
        listContentProvider = dbProcessor.getProviderProcessor().getTreeProvider(startWith);
        log.info("listContentProvider = " + listContentProvider);
    }

    public String onReprotTotal() {
        log.info("ReportAction.onReprotTotal");
        String[] customInfo = new String[2];
        customInfo[0] = "success";
        customInfo[1] = "";

        Date fromDate = DateTimeUtils.convertStringToDate(getRequest().getParameter("fromDate"), DateTimeUtils.patternDateVN);
        Date toDate = DateTimeUtils.convertStringToDate(getRequest().getParameter("toDate") + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
        if (fromDate == null || toDate == null) {
            customInfo[0] = "failure";
            customInfo[1] = "Ngày tháng nhập không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;

        }

        log.debug(fromDate + " - " + toDate);
        String by = getRequest().getParameter("reportType");
        String bankCode = getRequest().getParameter("bankCode");
        if (bankCode == null || bankCode.equals("NONE")) {
            bankCode = "";
        }
        String cpId = getRequest().getParameter("contentProviderId");
        getListCp();
        ArrayList<Long> listCpId;
        if (!CommonUtils.isNullOrEmpty(cpId) && !cpId.equals("-1")) {
            listCpId = new ArrayList();
            boolean isOK = false;
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                Long cpIdInput = StringProcess.convertToLong(cpId);
                Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                if (cpIdInput.compareTo(cpIdStore) == 0) {
                    isOK = true;
                    break;
                }

            }
            if (isOK) {
                getListCp(StringProcess.convertToLong(cpId));
                for (int i = 0; i < listContentProvider.size(); i++) {
                    HashMap<String, String> hashInfo = listContentProvider.get(i);
                    listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
                }
            }
        } else {
            listCpId = new ArrayList();
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
            }
        }
        if (listCpId == null || listCpId.isEmpty()) {
            customInfo[0] = "failure";
            customInfo[1] = "Thông tin CP không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;
        }
        List<Map<String, Object>> listTrans = dbProcessor.getTransactionProcessor().getReportTotal(fromDate, toDate, by, bankCode, listCpId);
        getRequest().getSession().setAttribute("listReportTotal", listTrans);
        getRequest().getSession().setAttribute("listReportTotal.fromDate", getRequest().getParameter("fromDate"));
        getRequest().getSession().setAttribute("listReportTotal.toDate", getRequest().getParameter("toDate"));
        json.setCustomInfo(customInfo);
        json.setItems(listTrans);
        return SUCCESS;
    }

    public String getReportTotalEVN() {
        log.info("ReportAction.getReportTotalEVN");
        if (isLoggedIn()) {

            if (!getCpCode().equals("EVNHCM")) {

                return "notRules";
            } else {

                return "reportTotalEVN";
            }

        } else {
            return "loginIndex";
        }
    }
    //hoapt12

    public String onReportTotalEVN() {
        log.info("ReportAction.onReprotTotalEVN");
        String[] customInfo = new String[2];
        customInfo[0] = "success";
        customInfo[1] = "";

        Date fromDate = DateTimeUtils.convertStringToDate(getRequest().getParameter("fromDate") + " 00:00:00", "dd/MM/yyyy HH:mm:ss");
        Date toDate = DateTimeUtils.convertStringToDate(getRequest().getParameter("toDate") + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
        if (fromDate == null || toDate == null) {
            customInfo[0] = "failure";
            customInfo[1] = "Ngày tháng nhập không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;

        }
        log.debug(fromDate + " - " + toDate);
        String by = getRequest().getParameter("reportType");
        String bankCode = getRequest().getParameter("bankCode");
        if (bankCode == null || bankCode.equals("NONE")) {
            bankCode = "";
        }
        List<Map<String, Object>> listTrans = dbProcessor.getTransactionProcessor().getReportTotalEVN(fromDate, toDate, by, bankCode, getCpId());
        //List<Map<String, Object>> listTrans = dbProcessor.getTransactionProcessor().getReportTotalEVN(fromDate, toDate, by, bankCode, (long) 264);
        getRequest().getSession().setAttribute("listReportTotal", listTrans);
        getRequest().getSession().setAttribute("listReportTotal.fromDate", getRequest().getParameter("fromDate"));
        getRequest().getSession().setAttribute("listReportTotal.toDate", getRequest().getParameter("toDate"));
        json.setCustomInfo(customInfo);
        json.setItems(listTrans);
        return SUCCESS;
    }

    public String getReportRefund() {

        log.info("ReportAction.getReportRefund");
        if (isLoggedIn()) {
            getListCp();
            return "reportRefund";
        } else {

            return "loginIndex";
        }
    }

    public String getReportEVNNPC() {
        log.info("ReportAction.getReportEVNNPC");
        if (isLoggedIn()) {
            getListCp();
            return "reportEVNPC";
        } else {

            return "loginIndex";
        }
    }

    public String onReprotEVNNPC() {
        log.info("ReportAction.onReprotEVNNPC");
        String[] customInfo = new String[2];
        customInfo[0] = "success";
        customInfo[1] = "";

        Date fromDate = DateTimeUtils.convertStringToDate(getRequest().getParameter("fromDate"), DateTimeUtils.patternDateVN);
        Date toDate = DateTimeUtils.convertStringToDate(getRequest().getParameter("toDate") + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
        if (fromDate == null || toDate == null) {
            customInfo[0] = "failure";
            customInfo[1] = "Ngày tháng nhập không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;
        }
        log.debug(fromDate + " - " + toDate);
        String status = getRequest().getParameter("status");
        String cpId = getRequest().getParameter("contentProviderId");
        getListCp();
        ArrayList<Long> listCpId;
        if (!CommonUtils.isNullOrEmpty(cpId) && !cpId.equals("-1")) {
            listCpId = new ArrayList();
            boolean isOK = false;
            log.info("cpId = " + cpId);
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                Long cpIdInput = StringProcess.convertToLong(cpId);
                Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                log.info("cpIdInput = " + cpIdInput + "&cpIdStore=" + cpIdStore);
                if (cpIdInput.compareTo(cpIdStore) == 0) {
                    listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
                }
//                else{
//                    
//                }
//                if (isOK) {
//                    listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
//                }
            }

        } else {
            listCpId = new ArrayList();
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
            }
        }
        if (listCpId == null || listCpId.isEmpty()) {
            customInfo[0] = "failure";
            customInfo[1] = "Thông tin CP không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;
        }
        List<Map<String, Object>> listTrans = dbProcessor.getTransactionProcessor().getReportEVNNPC(fromDate, toDate, status, listCpId);
        getRequest().getSession().setAttribute("listReportEVNNPC", listTrans);
        getRequest().getSession().setAttribute("listReportEVNNPC.fromDate", getRequest().getParameter("fromDate"));
        getRequest().getSession().setAttribute("listReportEVNNPC.toDate", getRequest().getParameter("toDate"));
        json.setCustomInfo(customInfo);
        json.setItems(listTrans);
        return SUCCESS;
    }

    public String onReprotRefund() {
        log.info("ReportAction.onReprotRefund");
        String[] customInfo = new String[2];
        customInfo[0] = "success";
        customInfo[1] = "";

        Date fromDate = DateTimeUtils.convertStringToDate(getRequest().getParameter("fromDate"), DateTimeUtils.patternDateVN);
        Date toDate = DateTimeUtils.convertStringToDate(getRequest().getParameter("toDate") + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
        if (fromDate == null || toDate == null) {
            customInfo[0] = "failure";
            customInfo[1] = "Ngày tháng nhập không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;
        }
        log.debug(fromDate + " - " + toDate);
        String by = getRequest().getParameter("reportType");
        String bankCode = getRequest().getParameter("bankCode");
        if (bankCode == null || bankCode.equals("NONE")) {
            bankCode = "";
        }
        String cpId = getRequest().getParameter("contentProviderId");
        getListCp();
        ArrayList<Long> listCpId;
        if (!CommonUtils.isNullOrEmpty(cpId) && !cpId.equals("-1")) {
            listCpId = new ArrayList();
            boolean isOK = false;
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                Long cpIdInput = StringProcess.convertToLong(cpId);
                Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                if (cpIdInput.compareTo(cpIdStore) == 0) {
                    isOK = true;
                }
                if (isOK) {
                    listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
                }
            }

        } else {
            listCpId = new ArrayList();
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
            }
        }
        if (listCpId == null || listCpId.isEmpty()) {
            customInfo[0] = "failure";
            customInfo[1] = "Thông tin CP không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;
        }
        List<Map<String, Object>> listTrans = dbProcessor.getTransactionProcessor().getReportRefund(fromDate, toDate, by, bankCode, listCpId);
        getRequest().getSession().setAttribute("listReportRefund", listTrans);
        getRequest().getSession().setAttribute("listReportRefund.fromDate", getRequest().getParameter("fromDate"));
        getRequest().getSession().setAttribute("listReportRefund.toDate", getRequest().getParameter("toDate"));
        json.setCustomInfo(customInfo);
        json.setItems(listTrans);
        return SUCCESS;
    }

    public String EVN_REG_PAGE = "evnregister.page";

    public String onEvnReg() {
        if (isLoggedIn()) {
            getListCp();
            return EVN_REG_PAGE;
        } else {
            return "loginIndex";
        }
    }

    public String onReportEvnReg() {
        log.info("ReportAction.onReportEvnReg");
        String[] customInfo = new String[2];
        String fromDate = getRequest().getParameter("fromDate");
        String toDate = getRequest().getParameter("toDate");
        String contentProviderId = getRequest().getParameter("contentProviderId");
        customInfo[0] = "success";
        customInfo[1] = "success";
        if (CommonUtils.isNullOrEmpty(fromDate) || CommonUtils.isNullOrEmpty(toDate)) {
            customInfo[0] = "failure";
            customInfo[1] = "Ngày tháng nhập không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;
        }

        if (CommonUtils.isNullOrEmpty(contentProviderId)) {
            customInfo[0] = "failure";
            customInfo[1] = "Thông tin CP không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;
        }

        getListCp();
        ArrayList<String> listCpCode = new ArrayList();
        if (!contentProviderId.equals("-1")) {

            boolean isOK = false;
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                Long cpIdInput = StringProcess.convertToLong(contentProviderId);
                Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                if (cpIdInput.compareTo(cpIdStore) == 0) {
                    isOK = true;
                    break;
                }
            }
            if (isOK) {
                getListCp(StringProcess.convertToLong(contentProviderId));
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

        if (listCpCode == null || listCpCode.isEmpty()) {
            customInfo[0] = "failure";
            customInfo[1] = "Thông tin CP không đúng!";
            json.setCustomInfo(customInfo);
            return SUCCESS;
        }
        List<HashMap> lstReport = new ArrayList<HashMap>();
        //cp_code, cp_name, prev_mon,curr_mon,increment, reduction, total_order, total_amount

        Date dFromDate = DateTimeUtils.addDate(DateTimeUtils.convertStringToDate(fromDate, DateTimeUtils.patternDateVN), Calendar.MONTH, -1);
        Date dToDate = DateTimeUtils.convertStringToDate(toDate + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
        HashMap params = new HashMap();
        params.put("fromDate", dFromDate);
        params.put("toDate", dToDate);
        params.put("cpCode", listCpCode);
        //Lay danh sach khach hang dang ky thanh toan
        String sqlReg = "select cp_code, to_char(REGISTER_DATE, 'MM/yyyy') register_mon, count(*) total from evn_register "
                + " where REGISTER_DATE >= :fromDate"
                + " and REGISTER_DATE <= :toDate"
                + " and CP_CODE in (:cpCode)"
                + " group by cp_code, to_char(REGISTER_DATE, 'MM/yyyy')"
                + " order by to_char(REGISTER_DATE, 'MM/yyyy') desc";

        List<HashMap> lstReg = dbProcessor.getTransactionProcessor().getReport(sqlReg, params);
        HashMap currMon = new HashMap();
        HashMap prevMon = new HashMap();
        if (lstReg != null && lstReg.size() > 0) {
            for (HashMap hashMap : lstReg) {
                if (toDate.contains(hashMap.get("REGISTER_MON").toString())) {
                    currMon.put(hashMap.get("CP_CODE").toString(), hashMap.get("TOTAL").toString());
                } else {
                    prevMon.put(hashMap.get("CP_CODE").toString(), hashMap.get("TOTAL").toString());
                }
            }
        }

        //Lay tong so hoa don, so tien thanh toan theo tung cp
        String sqlReportBill = " select bp.cp_code, sum(bp.amount) total_amount, sum(bs.num_order) total_order from trans_bankplus bp,\n"
                + " (select trans_bankplus_id, count(*) num_order from billing_service where trans_bankplus_id in (\n"
                + " select trans_bankplus_id from trans_bankplus bp2 where request_id in ( \n"
                + " select original_request_id from trans_bankplus bp1 where\n"
                + " bp1.process_code = '300001'\n"
                + " and (bp1.error_code = '00' or bp1.correct_code = '00')\n"
                + " and bp1.request_date >= :fromDate and bp1.request_date <= :toDate "
                + " and cp_code in (:cpCode) ) "
                + " and bp2.request_date >= :fromDate and bp2.request_date <= :toDate "
                + " and bp2.error_code = '00'\n"
                + " and bp2.process_code = '300000'\n"
                + ") group by trans_bankplus_id\n"
                + ") bs \n"
                + " where bp.request_date >= :fromDate and bp.request_date <= :toDate "
                + " and bp.trans_bankplus_id = bs.trans_bankplus_id"
                + " group by bp.cp_code";
        List<HashMap> lstReportBill = dbProcessor.getTransactionProcessor().getReport(sqlReportBill, params);
        HashMap hashTotal = new HashMap();
        if (lstReportBill != null && lstReportBill.size() > 0) {
            for (HashMap hashMap : lstReportBill) {
                String cpCode = hashMap.get("CP_CODE").toString();
                String totalAmount = hashMap.get("TOTAL_AMOUNT").toString();
                String numOrder = hashMap.get("TOTAL_ORDER").toString();
                hashTotal.put(cpCode, totalAmount + "#" + numOrder);
            }
        }
//cp_code, cp_name, prev_mon,curr_mon,increment, reduction, total_order, total_amount
        for (int i = 0; i < listContentProvider.size(); i++) {
            HashMap<String, String> hashInfo = listContentProvider.get(i);
            HashMap hashData = new HashMap();
            String cpCode = hashInfo.get("CP_CODE");
            hashData.put("CP_CODE", cpCode);
            hashData.put("CP_NAME", hashInfo.get("CP_NAME"));
            int iCurrMon = 0;
            int iPrevMon = 0;
            if (currMon.containsKey(cpCode)) {
                hashData.put("CURR_MON", currMon.get(cpCode));
                iCurrMon = StringProcess.convertToInt(currMon.get(cpCode));
            }
            if (prevMon.containsKey(cpCode)) {
                hashData.put("PREV_MON", prevMon.get(cpCode));
                iPrevMon = StringProcess.convertToInt(prevMon.get(cpCode));
            }
            if (iCurrMon > iPrevMon) {
                hashData.put("INCREMENT", iCurrMon - iPrevMon);
                hashData.put("REDUCTION", 0);
            } else {
                hashData.put("REDUCTION", iCurrMon - iPrevMon);
                hashData.put("INCREMENT", 0);
            }
            if (hashTotal.containsKey(cpCode)) {
                String[] temp = hashTotal.get(cpCode).toString().split("#");
                hashData.put("TOTAL_AMOUNT", temp[0]);
                hashData.put("TOTAL_ORDER", temp[1]);
            }
            lstReport.add(hashData);
        }
        getRequest().getSession().setAttribute("listReportEvnReg", lstReport);
        getRequest().getSession().setAttribute("listReportEvnReg.fromDate", fromDate);
        getRequest().getSession().setAttribute("listReportEvnReg.toDate", toDate);
        json.setCustomInfo(customInfo);
        return SUCCESS;
    }

}
