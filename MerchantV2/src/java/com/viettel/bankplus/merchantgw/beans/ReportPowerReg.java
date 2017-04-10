/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bankplus.merchantgw.beans;

import com.viettel.bankplus.merchantgw.beans.entities.UserLoginInfo;
import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.Constants;
import com.viettel.common.util.DateTimeUtils;
import com.viettel.common.util.ExportExcelUtils;
import com.viettel.common.util.StringProcess;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author ducnt28
 */
@ManagedBean
@ViewScoped
public class ReportPowerReg extends BaseBean implements Serializable {

    private Date fromDate;
    private Date toDate;
    private String chooseCp;
    private static final Logger log = Logger.getLogger(ReportPowerReg.class);
    List<HashMap<String, String>> listContentProvider = new ArrayList<HashMap<String, String>>();

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

    private List<HashMap<String, String>> getListCpNew(Long startWith) {
        return listContentProvider = dbProcessor.getProviderProcessor().getTreeProvider(startWith);
    }

    private StreamedContent file;

    public StreamedContent getFile() {
        this.file = this.exportExcel();
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
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

    public StreamedContent exportExcel() {

        List<HashMap> lstReport = new ArrayList<HashMap>();
        log.info("ReportPowerReg.onReportEvnReg()");
        log.info("ReportPowerTransaction.onReprotTranfer():");
        log.info(fromDate + " - " + toDate);
        FacesMessage msg = new FacesMessage(message.getString("search.error.full"));
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        if (fromDate == null || "".equalsIgnoreCase(fromDate.toString())) {
            log.info("ReportPowerTransaction.onReprotTranfer():fromDate is null");
            msg.setSummary(message.getString("error.frmDateNull"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        if (toDate == null || "".equalsIgnoreCase(toDate.toString())) {
            log.info("ReportPowerTransaction.onReprotTranfer():toDate is null");
            msg.setSummary(message.getString("error.toDateNull"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        if (fromDate.after(toDate)) {
            log.info("ReportPowerTransaction.onReprotTranfer():fromDate > toDate");
            msg.setSummary(message.getString("error.frDatetoDate"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        if (CommonUtils.isNullOrEmpty(chooseCp)) {
            log.info("ReportPowerTransaction.onReprotTranfer():chooseCp Error");
            msg.setSummary(message.getString("error.infoCpError"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        getListCp();
        ArrayList<String> listCpCode = new ArrayList();
        if (!CommonUtils.isNullOrEmpty(this.chooseCp) && !"-1".equals(this.chooseCp)) {
            boolean isOK = false;
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                Long cpIdInput = StringProcess.convertToLong(chooseCp);
                Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                if (cpIdInput.compareTo(cpIdStore) == 0) {
                    isOK = true;
                    break;
                }
            }
            if (isOK) {
                List lstCpNew = getListCpNew(Long.parseLong(this.chooseCp));
                for (int i = 0; i < lstCpNew.size(); i++) {
                    HashMap<String, String> hashInfo = (HashMap<String, String>) lstCpNew.get(i);
                    listCpCode.add(hashInfo.get("CP_CODE"));
                }
            }
        } else {
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                listCpCode.add(hashInfo.get("CP_CODE"));
            }
        }

        if (listCpCode.isEmpty()) {
            log.info("ReportPowerTransaction.onReprotTranfer():listCpCode is Empty");
            msg.setSummary(message.getString("error.infoCpError"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

//        Date dFromDate = DateTimeUtils.addDate(DateTimeUtils.convertStringToDate(fromDate.toString(), DateTimeUtils.patternDateVN), Calendar.MONTH, -1);
//        Date dToDate = DateTimeUtils.convertStringToDate(toDate + " 23:59:59", "dd/MM/yyyy HH:mm:ss");
        HashMap params = new HashMap();
        params.put("fromDate", fromDate);
        params.put("toDate", toDate);
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
                if (toDate.toString().contains(hashMap.get("REGISTER_MON").toString())) {
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

        StreamedContent result = null;
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            UserLoginInfo loginInfo = (UserLoginInfo) session.getAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO);
            Map<String, String> mapData = new HashMap<String, String>();
            ResourceBundle rb = ResourceBundle.getBundle("config");
            String templateFolder = rb.getString("template_excel");
            String reportFolder = rb.getString("export_output");

            String reportFileName = loginInfo.getCpCode() + DateTimeUtils.convertDateToString(new Date(), "yyMMddHHmm") + ".xls";
            String reportFilePath = reportFolder + reportFileName;
            String templateFilePath = templateFolder + "evnreg.xls";

            String fromDateTemp = DateTimeUtils.convertDateToString(fromDate, "dd-MM-yyyy");
            String toDateTemp = DateTimeUtils.convertDateToString(toDate, "dd-MM-yyyy");
            mapData.put("fromDate", fromDateTemp);
            mapData.put("toDate", toDateTemp);

            if (lstReport != null) {
                ContentProvider cp = new DBProcessor().getProviderProcessor().getProviderById(loginInfo.getUserInfo().getContentProviderId());
                if (cp != null) {
                    mapData.put("cpCode", cp.getCpCode());
                    mapData.put("cpName", cp.getCpName());
                }
            }

            InputStream is = ExportExcelUtils.getInstance().generateMultiSheet(lstReport, mapData, templateFilePath, reportFilePath);
            if (is != null) {
                result = new DefaultStreamedContent(is, "application/vnd.ms-excel", reportFileName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
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
