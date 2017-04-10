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
public class RefundTransaction extends BaseBean implements Serializable {

    private Date fromDate;
    private Date toDate;
    private String chooseCp;
    private String typeSearch;
    List<Map<String, Object>> listRefund;
    private StreamedContent file;
    private static final Logger log = Logger.getLogger(RefundTransaction.class);
    List<HashMap<String, String>> listContentProvider = new ArrayList<HashMap<String, String>>();

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

    public void onReprotRefund() {

        log.info("RefundTransaction.onReprotRefund");
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

        String by = typeSearch;
        getListCp();
        ArrayList<Long> listCpId;
        if (!CommonUtils.isNullOrEmpty(chooseCp) && !"-1".equals(chooseCp)) {
            listCpId = new ArrayList();
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
                List listNewChoose = getListCpNew(StringProcess.convertToLong(chooseCp));
                for (int i = 0; i < listNewChoose.size(); i++) {
                    HashMap<String, String> hashInfo = (HashMap<String, String>) listNewChoose.get(i);
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

        String bankCode = null;
        listRefund = dbProcessor.getTransactionProcessor().getReportRefund(fromDate, toDate, by, bankCode, listCpId);
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

            String reportFileName = "reportRefund_" + DateTimeUtils.convertDateToString(new Date(), "yyMMddHHmmss") + ".xls";
            String reportFilePath = reportFolder + reportFileName;
            String templateFilePath = templateFolder + "reportRefund.xls";

            String fromDateTemp = DateTimeUtils.convertDateToString(fromDate, "dd-MM-yyyy");
            String toDateTemp = DateTimeUtils.convertDateToString(toDate, "dd-MM-yyyy");
            mapData.put("fromDate", fromDateTemp);
            mapData.put("toDate", toDateTemp);

            if (listRefund != null) {
                ContentProvider cp = new DBProcessor().getProviderProcessor().getProviderById(loginInfo.getUserInfo().getContentProviderId());
                if (cp != null) {
                    mapData.put("cpCode", cp.getCpCode());
                    mapData.put("cpName", cp.getCpName());
                }
            }

            InputStream is = ExportExcelUtils.getInstance().generateMultiSheet(listRefund, mapData, templateFilePath, reportFilePath);
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

    public StreamedContent getFile() {
        this.file = this.exportExcel();
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public List<Map<String, Object>> getListRefund() {
        return listRefund;
    }

    public void setListRefund(List<Map<String, Object>> listRefund) {
        this.listRefund = listRefund;
    }

    public String getTypeSearch() {
        return typeSearch;
    }

    public void setTypeSearch(String typeSearch) {
        this.typeSearch = typeSearch;
    }

    public List<HashMap<String, String>> getListContentProvider() {
        return listContentProvider;
    }

    public void setListContentProvider(List<HashMap<String, String>> listContentProvider) {
        this.listContentProvider = listContentProvider;
    }
}
