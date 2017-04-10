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
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransferMerchant;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
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
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author ducnt28
 */
@ManagedBean
@ViewScoped
public class ReportPowerTransaction extends BaseBean implements Serializable {

    private Date fromDate;
    private Date toDate;
    private String chooseCp;
    private static final Logger log = Logger.getLogger(ReportPowerTransaction.class);
    List<HashMap<String, String>> listContentProvider = new ArrayList<HashMap<String, String>>();
    List<Map<String, Object>> listTrans = new ArrayList<Map<String, Object>>();
    private StreamedContent file;

    public StreamedContent getFile() {
        this.file = this.exportExcel();
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public List<Map<String, Object>> getListTrans() {
        return listTrans;
    }

    public void setListTrans(List<Map<String, Object>> listTrans) {
        this.listTrans = listTrans;
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

    public void onReprotPower() {

        log.info("ReportPowerTransaction.onReprotTranfer():");
        log.info(fromDate + " - " + toDate);
        FacesMessage msg = new FacesMessage(message.getString("search.error.full"));
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        if (fromDate == null || "".equalsIgnoreCase(fromDate.toString())) {
            log.info("ReportPowerTransaction.onReprotTranfer():fromDate is null");
            msg.setSummary(message.getString("error.frmDateNull"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (toDate == null || "".equalsIgnoreCase(toDate.toString())) {
            log.info("ReportPowerTransaction.onReprotTranfer():toDate is null");
            msg.setSummary(message.getString("error.toDateNull"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        if (fromDate.after(toDate)) {
            log.info("ReportPowerTransaction.onReprotTranfer():fromDate > toDate");
            msg.setSummary(message.getString("error.frDatetoDate"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        getListCp();
        ArrayList<Long> listCpId = new ArrayList();
        if (!CommonUtils.isNullOrEmpty(chooseCp) && !"-1".equals(chooseCp)) {
            log.info("cpId = " + chooseCp);
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                Long cpIdInput = StringProcess.convertToLong(chooseCp);
                Long cpIdStore = StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID"));
                log.info("cpIdInput = " + cpIdInput + "&cpIdStore=" + cpIdStore);
                if (cpIdInput.compareTo(cpIdStore) == 0) {
                    listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
                }
            }

        } else {
            for (int i = 0; i < listContentProvider.size(); i++) {
                HashMap<String, String> hashInfo = listContentProvider.get(i);
                listCpId.add(StringProcess.convertToLong(hashInfo.get("CONTENT_PROVIDER_ID")));
            }
        }

        if (listCpId.isEmpty()) {
            log.info("ReportPowerTransaction.onReprotTranfer():listCpId is Empty");
            msg.setSummary(message.getString("error.infoCpError"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        String status = "NONE";
        listTrans = dbProcessor.getTransactionProcessor().getReportEVNNPC(fromDate, toDate, status, listCpId);
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

            String reportFileName = "TransactionEVNNPC_" + DateTimeUtils.convertDateToString(new Date(), "yyMMddHHmmss") + ".xls";
            String reportFilePath = reportFolder + reportFileName;
            String templateFilePath = templateFolder + "TransactionEVNNPC.xls";

            String fromDateTemp = DateTimeUtils.convertDateToString(fromDate, "dd-MM-yyyy");
            String toDateTemp = DateTimeUtils.convertDateToString(toDate, "dd-MM-yyyy");
            mapData.put("fromDate", fromDateTemp);
            mapData.put("toDate", toDateTemp);

            if (listTrans != null) {
                ContentProvider cp = new DBProcessor().getProviderProcessor().getProviderById(loginInfo.getUserInfo().getContentProviderId());
                if (cp != null) {
                    mapData.put("cpCode", cp.getCpCode());
                    mapData.put("cpName", cp.getCpName());
                }
            }

            InputStream is = ExportExcelUtils.getInstance().generateMultiSheet(listTrans, mapData, templateFilePath, reportFilePath);
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
