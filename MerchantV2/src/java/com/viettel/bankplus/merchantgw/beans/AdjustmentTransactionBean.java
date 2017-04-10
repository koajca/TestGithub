/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bankplus.merchantgw.beans;

import com.viettel.bankplus.merchantgw.beans.entities.UserLoginInfo;
import com.viettel.bankplus.merchantgw.client.form.TransForm;
import com.viettel.common.util.CommonUtils;
import com.viettel.common.util.Constants;
import com.viettel.common.util.DateTimeUtils;
import com.viettel.common.util.ExportExcelUtils;
import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransBankplus;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author os_phucpt1
 */
@ManagedBean(name = "adjustmentTransactionBean")
@ViewScoped
public class AdjustmentTransactionBean extends BaseBean implements Serializable {

    private final Logger log = Logger.getLogger(AdjustmentTransactionBean.class);
    private TransForm transForm;
    private final String SESS_ATTR_LIST_CP = "listCp";
    private final String SESS_ATTR_LIST_TRANS_CONFIRM = "listTransConfirm";
    private final String SESS_ATTR_LIST_TRANS_IMPORTED = "listTransImported";
    private List<Map> listTrans;
    private List<Map> listTransImported;
    private String[] listStatus;
    private StreamedContent file;
    private UploadedFile fileUpload;

    public UploadedFile getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(UploadedFile fileUpload) {
        this.fileUpload = fileUpload;
    }

    public List<Map> getListTransImported() {
        return listTransImported;
    }

    public void setListTransImported(List<Map> listTransImported) {
        this.listTransImported = listTransImported;
    }

    public List<Map> getListTrans() {
        return listTrans;
    }

    public void setListTrans(List<Map> listTrans) {
        this.listTrans = listTrans;
    }

    public StreamedContent getFile() {
        this.file = this.exportExcel();
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public String[] getListStatus() {
        return listStatus;
    }

    public void setListStatus(String[] listStatus) {
        this.listStatus = listStatus;
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
//            Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
//            String defaultValue = ResourceBundleUtils.getString("adjustment.transaction.data.table.cmb.value.default", locale);
//            String rejectValue = ResourceBundleUtils.getString("adjustment.transaction.data.table.cmb.value.reject", locale);
//            String acceptValue = ResourceBundleUtils.getString("adjustment.transaction.data.table.cmb.value.accept", locale);
            String defaultValue = lang.getString("adjustment.transaction.data.table.cmb.value.default");
            String rejectValue = lang.getString("adjustment.transaction.data.table.cmb.value.reject");
            String acceptValue = lang.getString("adjustment.transaction.data.table.cmb.value.accept");

            String[] tempConfirm = {defaultValue, rejectValue, acceptValue};
            listStatus = tempConfirm;

            List<Map<String, String>> listCP = getListCp(userInfo.getUserInfo().getContentProviderId());
            session.setAttribute(this.SESS_ATTR_LIST_CP, listCP);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void searchTrans() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.removeAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);
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
                    List listContentProvider = (List) session.getAttribute(this.SESS_ATTR_LIST_CP);
                    listTrans = this.getListTrans(transForm, listContentProvider);
                    DAOFactory.commitCurrentSessions();
                }
            }
        } catch (Exception e) {
            log.error("error: ", e);
        }
        if (!"".equals(msgError)) {
            msg.setSummary(msgError);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    private List getListTrans(TransForm dataForm, List listCP) {
        List result = new ArrayList();
        try {
            log.info("searchTransConfirm");
            HashMap param = new HashMap();
            String sqlQuery = "select tb.trans_bankplus_id, tb.request_id, tb.original_request_id, to_char(tb.request_date, 'dd/MM/yyyy hh24:mi:ss') request_Date, to_char(otb.request_date, 'dd/MM/yyyy hh24:mi:ss') original_request_Date,"
                    + " tb.billing_code, otb.billing_code original_billing_code, tb.amount, otb.amount original_amount, tb.trans_status"
                    + ", :confirmStatus as confirm_status "
                    + "from trans_bankplus tb left join trans_bankplus otb on tb.original_request_id = otb.request_id ";
            String where = "";
            where += "where tb.process_code = '300011' and tb.tid_number in ('FIX','CANCELEXT')";
            where += " and tb.request_date > to_date(:fromDate,'dd/MM/yyyy hh24:mi:ss')";
            where += " and tb.request_date < to_date(:toDate,'dd/MM/yyyy hh24:mi:ss')";
//        param.put("fromDate", fromDate);
//        param.put("toDate", toDate);
//            String defaultValue = ResourceBundleUtils.getString("adjustment.transaction.data.table.cmb.value.default", locale);
            String defaultValue = lang.getString("adjustment.transaction.data.table.cmb.value.default");
            param.put("confirmStatus", defaultValue);
            param.put("fromDate", DateTimeUtils.convertDateToString(dataForm.getFromDate(), "dd/MM/yyyy hh:mm:ss"));
            param.put("toDate", DateTimeUtils.convertDateToString(DateTimeUtils.addDate(dataForm.getToDate(), Calendar.MONTH, 1), "dd/MM/yyyy hh:mm:ss"));
//            param.put("fromDate", dataForm.getFromDate());
//            param.put("toDate", transactionConfirmForm.getToDate().trim() + " 23:59:59");
            if (!"".equals(dataForm.getStatus())) {
                where += " and tb.trans_status = :status";
                param.put("status", dataForm.getStatus());
            }
            ArrayList<String> listCpId = new ArrayList();
            for (int i = 0; i < listCP.size(); i++) {
                HashMap<String, String> hashInfo = (HashMap<String, String>) listCP.get(i);
                listCpId.add(hashInfo.get("CP_CODE"));
            }
            where += " and tb.cp_code in (:cpId) ";
            param.put("cpId", listCpId);
            sqlQuery += where;
            log.info("sqlQuery: " + sqlQuery);
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
            result = query.list();
            DAOFactory.commitCurrentSessions();
        } catch (Exception e) {
            log.error("error: ", e);
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
            Map mapTrans = context.getApplication().evaluateExpressionGet(context, "#{trans}", Map.class);

            Map<String, Map> mapTransConfirm = (Map<String, Map>) session.getAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);

//            String defaultValue = ResourceBundleUtils.getString("adjustment.transaction.data.table.cmb.value.default", locale);
//            String rejectValue = ResourceBundleUtils.getString("adjustment.transaction.data.table.cmb.value.reject", locale);
//            String acceptValue = ResourceBundleUtils.getString("adjustment.transaction.data.table.cmb.value.accept", locale);
            String defaultValue = lang.getString("adjustment.transaction.data.table.cmb.value.default");
            String rejectValue = lang.getString("adjustment.transaction.data.table.cmb.value.reject");
            String acceptValue = lang.getString("adjustment.transaction.data.table.cmb.value.accept");

            if (newValue != null && !newValue.equals(oldValue)) {
                mapTransConfirm = mapTransConfirm == null ? (new HashMap()) : mapTransConfirm;
                if (newValue.equals(defaultValue)) {
                    if (mapTransConfirm.containsKey(mapTrans.get("TRANS_BANKPLUS_ID"))) {
                        mapTransConfirm.remove(mapTrans.get("TRANS_BANKPLUS_ID"));
                    }
                } else {
                    if (newValue.equals(acceptValue)) {
                        mapTrans.put("CONFIRM_ID", "2");
                    } else if (newValue.equals(rejectValue)) {
                        mapTrans.put("CONFIRM_ID", "3");
                    }
                    mapTransConfirm.put(String.valueOf(mapTrans.get("TRANS_BANKPLUS_ID")), mapTrans);
                }
                session.removeAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);
                session.setAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM, mapTransConfirm);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public List<Map> getAllTransConfirm() {
        List<Map> result = new ArrayList<Map>();
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            Map<String, Map> mapTransConfirm = (Map<String, Map>) session.getAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);
            if (mapTransConfirm != null && !mapTransConfirm.isEmpty()) {
                for (Map.Entry<String, Map> entry : mapTransConfirm.entrySet()) {
                    result.add(entry.getValue());
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return result;
    }

    public void multiConfirm() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        RequestContext reqContext = RequestContext.getCurrentInstance();
        String msgError = "";
        try {
            log.info("TransactionAction.multiConfirm");
            List listContentProvider = (List) session.getAttribute(this.SESS_ATTR_LIST_CP);
            Map<String, Map> mapTransConfirm = (Map<String, Map>) session.getAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);
            for (Map.Entry<String, Map> entry : mapTransConfirm.entrySet()) {
                String transId = entry.getKey();
                Map transMap = entry.getValue();
                String confirmId = transMap.get("CONFIRM_ID") == null ? "" : transMap.get("CONFIRM_ID").toString();
                TransBankplus transBp = getTransBankplusByTransBpId(Long.parseLong(transId));
                if (transBp != null && "1".equals(transBp.getTransStatus())
                        && ("2".equals(confirmId) || "3".equals(confirmId))) {
                    boolean isOK = false;
                    for (int i = 0; i < listContentProvider.size(); i++) {
                        HashMap<String, String> hashInfo = (HashMap<String, String>) listContentProvider.get(i);
                        String cpCode = hashInfo.get("CP_CODE");
                        if (transBp.getCpCode().compareTo(cpCode) == 0) {
                            isOK = true;
                            break;
                        }
                    }
                    if (isOK) {
                        transBp.setTransStatus(confirmId);
                        if ("3".equals(confirmId)) {
                            transBp.setErrorCode("23");
                            transBp.setCorrectCode("23");
                        }
                        dbProcessor.getTransactionProcessor().doSaveTransBank(transBp);
                        msgError = "success";
                    } else {
                        msgError = "failed";
                        break;
                    }
                }
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

    public TransBankplus getTransBankplusByTransBpId(Long transBpId) {
        TransBankplus trans = (TransBankplus) DAOFactory.getCurrentSessionAndBeginTransaction().createCriteria(TransBankplus.class)
                .add(Restrictions.eq("transBankplusId", transBpId))
                .uniqueResult();
        DAOFactory.commitCurrentSessions();
        return trans;
    }

    public TransBankplus getTransBankplusByRequestId(String requestId) {
        TransBankplus trans = (TransBankplus) DAOFactory.getCurrentSessionAndBeginTransaction().createCriteria(TransBankplus.class)
                .add(Restrictions.eq("requestId", requestId))
                .uniqueResult();
        DAOFactory.commitCurrentSessions();
        return trans;
    }

    public void showDialogTransConfirm() {
        RequestContext reqContext = RequestContext.getCurrentInstance();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        try {
            Map<String, Map> mapTransConfirm = (Map<String, Map>) session.getAttribute(this.SESS_ATTR_LIST_TRANS_CONFIRM);
            if (mapTransConfirm != null && !mapTransConfirm.isEmpty()) {
                Map<String, Object> options = new HashMap<String, Object>();
                options.put("resizable", false);
                options.put("draggable", false);
                options.put("modal", true);
                reqContext.execute("PF('wdgTransConfirm').show();");
            } else {
                reqContext.showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, message.getString("common.severity.warn"), message.getString("confirming.transaction.show.dialog.trans.confirm")));
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public StreamedContent exportExcel() {
        StreamedContent result = null;
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        try {
//            UserLoginInfo loginInfo = (UserLoginInfo) session.getAttribute(Constants.SESS_ATTR_USER_LOGIN_INFO);
            Map<String, String> mapData = new HashMap<String, String>();
            ResourceBundle rb = ResourceBundle.getBundle("config");
            String templateFolder = rb.getString("template_excel");
            String reportFolder = rb.getString("export_output");

            String reportFileName = "transaction_fix_request_" + DateTimeUtils.convertDateToString(new Date(), "yyMMddHHmmss") + ".xls";
            String reportFilePath = reportFolder + reportFileName;
            String templateFilePath = templateFolder + "TransFixRequest.xls";

            String fromDateTemp = DateTimeUtils.convertDateToString(transForm.getFromDate(), "dd-MM-yyyy");
            String toDateTemp = DateTimeUtils.convertDateToString(transForm.getToDate(), "dd-MM-yyyy");
            mapData.put("fromDate", fromDateTemp);
            mapData.put("toDate", toDateTemp);
            List listContentProvider = (List) session.getAttribute(this.SESS_ATTR_LIST_CP);

            List<Map> listData = this.getListTrans(this.transForm, listContentProvider);

            InputStream is = ExportExcelUtils.getInstance().generateMultiSheet(listData, mapData, templateFilePath, reportFilePath);
            if (is != null) {
                result = new DefaultStreamedContent(is, "application/vnd.ms-excel", reportFileName);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return result;
    }

    public void showImportDialog() {
        try {
            RequestContext reqContext = RequestContext.getCurrentInstance();
            listTransImported = null;
            reqContext.execute("PF('wdgImportDialog').show();");
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        RequestContext reqContext = RequestContext.getCurrentInstance();
        String msg = "";
        try {
            UploadedFile fileUpload = event.getFile();
            if (fileUpload != null) {
                String fileName = fileUpload.getFileName() == null ? "" : fileUpload.getFileName().toLowerCase();
                InputStream is = fileUpload.getInputstream();
                if (fileName.endsWith(".xls")) {
                    listTransImported = this.getDataFromExcelOriginal(is);
                } else if (fileName.endsWith(".xlsx")) {
                    listTransImported = this.getDataFromExcelExtend(is);
                } else {
                    msg = message.getString("adjustment.transaction.import.file.wrong.allow.type");
                }
            } else {
                msg = message.getString("adjustment.transaction.show.dialog.cannot.read.file");
            }
        } catch (Exception e) {
            msg = message.getString("adjustment.transaction.show.dialog.cannot.read.file");
            log.error("Error: ", e);
        }
        if (!"".equals(msg)) {
            reqContext.showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR, message.getString("common.severity.error"), msg));
        }
    }

    private List getDataFromExcelOriginal(InputStream inputStream) throws Exception {
        List result = null;
        try {
            result = new ArrayList<Map>();
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            int i = 9;
            while (i < sheet.getLastRowNum()) {
                HSSFRow row = sheet.getRow(i);
                //For each row, iterate through all the columns
                String requestId = CommonUtils.getValueOfCellOriginal(row.getCell((short) 2));
//                String id = CommonUtils.getValueOfCellOriginal(row.getCell((short) 1));
//                String account = CommonUtils.getValueOfCellOriginal(row.getCell((short) 2));
//                String name = CommonUtils.getValueOfCellOriginal(row.getCell((short) 3));
//                String msisdn = CommonUtils.getValueOfCellOriginal(row.getCell((short) 4));
                String confirmStatus = CommonUtils.getValueOfCellOriginal(row.getCell((short) 12));
                String description = CommonUtils.getValueOfCellOriginal(row.getCell((short) 13));

                if (requestId != null) {
                    Map mapData = new HashMap();
                    mapData.put("REQUEST_ID", requestId);
                    mapData.put("CONFIRM_STATUS", confirmStatus);
                    mapData.put("DESCRIPTION", description);

                    standardizedData(mapData);
                    result.add(mapData);
                } else {
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    private void standardizedData(Map map) {
        try {
            String requestId = map.get("REQUEST_ID") == null ? "" : map.get("REQUEST_ID").toString();
            String confirmStatus = map.get("CONFIRM_STATUS") == null ? "" : map.get("CONFIRM_STATUS").toString();
            String description = map.get("DESCRIPTION") == null ? "" : map.get("DESCRIPTION").toString();

            String regexRequestId = config.getString("adjust.trans.regex.request.id");
            String regexDesc = config.getString("adjust.trans.regex.description");

            String accept = lang.getString("adjustment.transaction.data.table.cmb.value.accept");
            String reject = lang.getString("adjustment.transaction.data.table.cmb.value.reject");
            if (!CommonUtils.validateByRegex(requestId, regexRequestId)) {
                map.put("NOTE", message.getString("adjustment.transaction.import.wrong.request.id"));
                return;
            }
            if (accept.equalsIgnoreCase(confirmStatus)) {
                map.put("CONFIRM_ID", "2");
                map.put("DESCRIPTION", "");
            } else if (reject.equalsIgnoreCase(confirmStatus)) {
                map.put("CONFIRM_ID", "3");
            } else {
                map.put("NOTE", message.getString("adjustment.transaction.import.wrong.confirm.status"));
                return;
            }
            if (reject.equalsIgnoreCase(confirmStatus)) {
                if (!CommonUtils.validateByRegex(description, regexDesc)) {
                    map.put("NOTE", message.getString("adjustment.transaction.import.wrong.description"));
                    return;
                }
            }
            map.put("NOTE", "OK");
        } catch (Exception e) {
            map.put("NOTE", e.getMessage());
            log.error("Error: ", e);
        }
    }

    private List getDataFromExcelExtend(InputStream excelFile) throws Exception {
        List result = null;
        try {
            result = new ArrayList<Map>();

            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.rowIterator();
            long i = 9;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                String requestId = CommonUtils.getValueOfCellExtend(row.getCell(2));
                String confirmStatus = CommonUtils.getValueOfCellExtend(row.getCell(12));
                String description = CommonUtils.getValueOfCellExtend(row.getCell(13));
//                String account = CommonUtils.getValueOfCellExtend(row.getCell(2));
//                String name = CommonUtils.getValueOfCellExtend(row.getCell(3));
//                String msisdn = CommonUtils.getValueOfCellExtend(row.getCell(4));
                if (requestId != null) {
                    Map bean = new HashMap();
                    bean.put("REQUEST_ID", requestId);
                    bean.put("CONFIRM_STATUS", confirmStatus);
                    bean.put("DESCRIPTION", description);

                    result.add(bean);
                } else {
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    public void confirmTransImported() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        RequestContext reqContext = RequestContext.getCurrentInstance();
        FacesContext context = FacesContext.getCurrentInstance();
//        Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        String msgError = "";
        try {
            log.info("TransactionAction.multiConfirm");
            List listContentProvider = (List) session.getAttribute(this.SESS_ATTR_LIST_CP);
//            List<Map> mapTransConfirm = (List<Map>) session.getAttribute(this.SESS_ATTR_LIST_TRANS_IMPORTED);
            String validData;
            String confirmId;
            String transId;
            String description;
            if (listTransImported != null) {
                for (Map transMap : listTransImported) {
                    validData = transMap.get("NOTE") == null ? "" : transMap.get("NOTE").toString();
                    if ("OK".equals(validData)) {
                        transId = transMap.get("REQUEST_ID") == null ? "" : transMap.get("REQUEST_ID").toString();
                        confirmId = transMap.get("CONFIRM_ID") == null ? "" : transMap.get("CONFIRM_ID").toString();
                        description = transMap.get("DESCRIPTION") == null ? "" : transMap.get("DESCRIPTION").toString();
                        TransBankplus transBp = getTransBankplusByRequestId(transId);
                        if (transBp != null && "1".equals(transBp.getTransStatus())
                                && ("2".equals(confirmId) || "3".equals(confirmId))) {
                            boolean isOK = false;
                            for (int i = 0; i < listContentProvider.size(); i++) {
                                HashMap<String, String> hashInfo = (HashMap<String, String>) listContentProvider.get(i);
                                String cpCode = hashInfo.get("CP_CODE");
                                if (transBp.getCpCode().compareTo(cpCode) == 0) {
                                    isOK = true;
                                    break;
                                }
                            }
                            if (isOK) {
                                transBp.setTransStatus(confirmId);
                                if ("3".equals(confirmId)) {
                                    transBp.setErrorCode("23");
                                    transBp.setCorrectCode("23");
                                    transBp.setNote(description);
                                }
                                dbProcessor.getTransactionProcessor().doSaveTransBank(transBp);
                            } else {
                                msgError = message.getString("confirming.transaction.multi.confirm.msg.failed");
                                break;
                            }
                        }
                    }
                }
                listTransImported = null;

            } else {
                msgError = message.getString("adjustment.transaction.confirm.must.be.upload.file");
            }
            if ("".equals(msgError)) {
//                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message.getString("common.severity.info"), message.getString("confirming.transaction.multi.confirm.msg.success")));
                reqContext.showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, message.getString("common.severity.info"), message.getString("confirming.transaction.multi.confirm.msg.success")));
                reqContext.execute("PF('wdgImportDialog').hide();");
            } else {
//                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message.getString("common.severity.error"), msgError));
                reqContext.showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR, message.getString("common.severity.error"), msgError));
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

}
