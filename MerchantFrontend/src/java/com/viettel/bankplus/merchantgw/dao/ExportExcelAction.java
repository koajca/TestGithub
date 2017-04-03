/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.merchantgw.dao;

import com.viettel.action.BaseAction;
import com.viettel.bankplus.merchantgw.dao.entities.Transaction;
import com.viettel.common.util.DateTimeUtils;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 20, 2014
 * @version 1.0
 */
public class ExportExcelAction extends BaseAction {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TransactionAction.class);
    public InputStream fileInputStream = null;
    String fileName = "data.xls";

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String doExport() {
        String type = getRequest().getParameter("type");
        if (type.equalsIgnoreCase("REPORT_TOTAL")) {
            exportReportTotal();
        } else if (type.equalsIgnoreCase("REPORT_REFUND")) {
            exportReportRefund();
        } else if (type.equalsIgnoreCase("TRANSACTION")) {
            exportTransaction();
        } else if (type.equalsIgnoreCase("TRANSACTIONEVN")) {
            exportTransactionEvn();
        } else if (type.equalsIgnoreCase("REPORT_TOTALEVN")) {
            exportTotalTransactionEvn();
        } else if (type.equalsIgnoreCase("REPORT_EVNNPC")) {
            exportTransEVNNPC();
        } else if (type.equalsIgnoreCase("REPORT_EVNREG")) {
            exportReportEvnReg();
        } else if (type.equalsIgnoreCase("REPORT_TRANS_FIX")) {
            exportTransFix();
        }

        return "doDownload";
    }

    private void exportReportTotal() {
        try {
            Date now = Calendar.getInstance().getTime();
            Map beans = new HashMap();

            String templateFolder = "/exceltemplate/";
            String reportFolder = getConfig("export_output");

            String reportFileNameTmp = getCpCode() + "_" + DateTimeUtils.convertDateToString(now, "yyMMddHHmm");
            String reportFileNameExt = ".xls";

            String templateFileName = templateFolder + "reportTotal.xls";
            //<editor-fold defaultstate="collapsed" desc="fill cac thong tin chung">

            beans.put("fromDate", getRequest().getSession().getAttribute("listReportTotal.fromDate"));
            beans.put("toDate", getRequest().getSession().getAttribute("listReportTotal.toDate"));
            List lst = (List) getRequest().getSession().getAttribute("listReportTotal");
            if (lst != null) {
                ContentProvider cp = new DBProcessor().getProviderProcessor().getProviderById(getCpId());
                if (cp != null) {
                    beans.put("cpCode", cp.getCpCode());
                    beans.put("cpName", cp.getCpName());
                }
                beans.put("list", lst);
            } else {
                beans.put("list", new ArrayList());
            }

            String reportFileName = reportFolder + reportFileNameTmp + reportFileNameExt;
            fileName = reportFileName;
            XLSTransformer transformer = new XLSTransformer();
            transformer.transformXLS(getRequest().getRealPath(templateFileName), beans,
                    getRequest().getRealPath(reportFileName));

            fileInputStream = new FileInputStream(new File(getRequest().getRealPath(reportFileName)));

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
        }
    }

    private void exportTotalTransactionEvn() {
        try {
            Date now = Calendar.getInstance().getTime();
            Map beans = new HashMap();

            String templateFolder = "/exceltemplate/";
            String reportFolder = getConfig("export_output");

            String reportFileNameTmp = "reportTotalEVN" + "_" + DateTimeUtils.convertDateToString(now, "yyMMddHHmm");
            String reportFileNameExt = ".xls";

            String templateFileName = templateFolder + "reportTotalEVN.xls";

            beans.put("fromDate", getRequest().getSession().getAttribute("listReportTotal.fromDate"));
            beans.put("toDate", getRequest().getSession().getAttribute("listReportTotal.toDate"));
            List lst = (List) getRequest().getSession().getAttribute("listReportTotal");
            if (lst != null) {
                ContentProvider cp = new DBProcessor().getProviderProcessor().getProviderById(getCpId());
                if (cp != null) {
                    beans.put("cpCode", cp.getCpCode());
                    beans.put("cpName", cp.getCpName());
                }
                beans.put("list", lst);
            } else {
                beans.put("list", new ArrayList());
            }

            String reportFileName = reportFolder + reportFileNameTmp + reportFileNameExt;
            fileName = reportFileName;
            XLSTransformer transformer = new XLSTransformer();
            transformer.transformXLS(getRequest().getRealPath(templateFileName), beans,
                    getRequest().getRealPath(reportFileName));

            fileInputStream = new FileInputStream(new File(getRequest().getRealPath(reportFileName)));

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
        }
    }

    private void exportReportRefund() {
        try {
            Date now = Calendar.getInstance().getTime();
            Map beans = new HashMap();

            String templateFolder = "/exceltemplate/";
            String reportFolder = getConfig("export_output");

            String reportFileNameTmp = getCpCode() + "_" + DateTimeUtils.convertDateToString(now, "yyMMddHHmm");
            String reportFileNameExt = ".xls";

            String templateFileName = templateFolder + "reportRefund.xls";
            //<editor-fold defaultstate="collapsed" desc="fill cac thong tin chung">

            beans.put("fromDate", getRequest().getSession().getAttribute("listReportRefund.fromDate"));
            beans.put("toDate", getRequest().getSession().getAttribute("listReportRefund.toDate"));
            List lst = (List) getRequest().getSession().getAttribute("listReportRefund");
            if (lst != null) {
                ContentProvider cp = new DBProcessor().getProviderProcessor().getProviderById(getCpId());
                if (cp != null) {
                    beans.put("cpCode", cp.getCpCode());
                    beans.put("cpName", cp.getCpName());
                }
                beans.put("list", lst);
            } else {
                beans.put("list", new ArrayList());
            }

            String reportFileName = reportFolder + reportFileNameTmp + reportFileNameExt;
            fileName = reportFileName;
            XLSTransformer transformer = new XLSTransformer();
            transformer.transformXLS(getRequest().getRealPath(templateFileName), beans,
                    getRequest().getRealPath(reportFileName));

            fileInputStream = new FileInputStream(new File(getRequest().getRealPath(reportFileName)));

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
        }
    }

    private void exportTransaction() {
        try {
            Date now = Calendar.getInstance().getTime();
            Map map = new HashMap();

            String templateFolder = "/template/";
            String reportFolder = getConfig("export_output");

            String reportFileNameTmp = getCpCode() + "_" + DateTimeUtils.convertDateToString(now, "yyMMddHHmm");
            String reportFileNameExt = ".xls";

            String templateFileName = templateFolder + "transaction.xls";
            //<editor-fold defaultstate="collapsed" desc="fill cac thong tin chung">

            Object fromDateTemp = getRequest().getSession().getAttribute("listTransaction.fromDate");
            Object toDateTemp = getRequest().getSession().getAttribute("listTransaction.toDate");
            map.put("fromDate", fromDateTemp);
            map.put("toDate", toDateTemp);
            List<Transaction> lst = (List) getRequest().getSession().getAttribute("listTransaction");
            List<Transaction> lst2 = new ArrayList();
            String spCode = "";
            String spName = "";
            if (lst != null) {
                for (Transaction transaction : lst) {
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
                        if (transaction.getTRANSSTATUS().equals("0")) {
                            transaction.setTEXTTRANSSTATUS("Chưa xử lý");
                        } else if (transaction.getTRANSSTATUS().equals("1")) {
                            transaction.setTEXTTRANSSTATUS("Đang xử lý");
                        } else if (transaction.getTRANSSTATUS().equals("2")) {
                            transaction.setTEXTTRANSSTATUS("Thành công");
                        } else if (transaction.getTRANSSTATUS().equals("3")) {
                            transaction.setTEXTTRANSSTATUS("Đã hủy");
                        } else if (transaction.getTRANSSTATUS().equals("4")) {
                            transaction.setTEXTTRANSSTATUS("Thất bại");
                        }
                    } else {
                        transaction.setTEXTTRANSSTATUS("");
                    }
                    lst2.add(transaction);
                }
                ContentProvider cp = new DBProcessor().getProviderProcessor().getProviderById(getCpId());
                spCode = cp.getCpCode();
                spName = cp.getCpName();
                if (cp != null) {
                    map.put("cpCode", spCode);
                    map.put("cpName", spName);
                }
                map.put("list", lst2);
            } else {
                map.put("list", new ArrayList());
            }

            String reportFileName = reportFolder + reportFileNameTmp + reportFileNameExt;
            fileName = reportFileName;
            XLSTransformer transformer = new XLSTransformer();

            List sheetNames = new ArrayList();
            List maps = new ArrayList();
            List<Transaction> tempList = new ArrayList<Transaction>();
            int indexSheet = 0;
            for (int i = 0; i < lst2.size(); i++) {
                tempList.add(lst2.get(i));
                if ((i != 0) && ((i + 1) % 65000 == 0)) {
                    map.put("list", tempList);
                    map.put("name", "sheet_transaction_" + (indexSheet + 1));
                    sheetNames.add("Sheet_" + (indexSheet + 1));
                    maps.add(map);
                    //
                    tempList = new ArrayList<Transaction>();
                    map = new HashMap();
                    map.put("cpCode", spCode);
                    map.put("cpName", spName);
                    map.put("fromDate", fromDateTemp);
                    map.put("toDate", toDateTemp);
                    indexSheet++;
                }
            }

            if (tempList != null && !tempList.isEmpty()) {
                map = new HashMap();
                map.put("cpCode", spCode);
                map.put("cpName", spName);
                map.put("fromDate", fromDateTemp);
                map.put("toDate", toDateTemp);

                map.put("list", tempList);
                map.put("name", "sheet_transaction_" + (indexSheet + 1));
                sheetNames.add("Sheet_" + (indexSheet + 1));
                maps.add(map);
            }
            if (lst == null || lst.isEmpty()) {
                map = new HashMap();
                map.put("cpCode", spCode);
                map.put("cpName", spName);
                map.put("fromDate", fromDateTemp);
                map.put("toDate", toDateTemp);

                map.put("list", new ArrayList());
                map.put("name", "sheet_transaction_" + (indexSheet + 1));
                sheetNames.add("Sheet_" + (indexSheet + 1));
                maps.add(map);
            }

            InputStream inputStream = new BufferedInputStream(new FileInputStream(getRequest().getRealPath(templateFileName)));
//            transformMultipleSheetsList(InputStream is, List objects, List newSheetNames, String beanName, Map beanParams, int startSheetNum);
            HSSFWorkbook resultWorkbook = transformer.transformMultipleSheetsList(inputStream, maps, sheetNames, "map", new HashMap(), 0);
            OutputStream outputStream = new FileOutputStream(getRequest().getRealPath(reportFileName));
            resultWorkbook.write(outputStream);
            outputStream.close();

//            transformer.transformXLS(inputStream, sheetNames, sheetNames, maps);
//            transformer.transformXLS(getRequest().getRealPath(templateFileName), map,
//                    getRequest().getRealPath(reportFileName));
            fileInputStream = new FileInputStream(new File(getRequest().getRealPath(reportFileName)));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
        }
    }

    private void exportTransactionEvn() {
        try {
            Date now = Calendar.getInstance().getTime();
            Map beans = new HashMap();

            String templateFolder = "/exceltemplate/";
            String reportFolder = getConfig("export_output");

            String reportFileNameTmp = "TransactionEVN." + "_" + DateTimeUtils.convertDateToString(now, "yyMMddHHmm");
            String reportFileNameExt = ".xls";

            String templateFileName = templateFolder + "transactionEVN.xls";

            beans.put("fromDate", getRequest().getSession().getAttribute("listTransactionEVN.fromDate"));
            beans.put("toDate", getRequest().getSession().getAttribute("listTransactionEVN.toDate"));
            List<Transaction> lst = (List) getRequest().getSession().getAttribute("listTransactionEVN");

            if (lst != null) {

                beans.put("list", lst);
            } else {
                beans.put("list", new ArrayList());
            }

            String reportFileName = reportFolder + reportFileNameTmp + reportFileNameExt;
            fileName = reportFileName;
            XLSTransformer transformer = new XLSTransformer();
            transformer.transformXLS(getRequest().getRealPath(templateFileName), beans,
                    getRequest().getRealPath(reportFileName));

            fileInputStream = new FileInputStream(new File(getRequest().getRealPath(reportFileName)));

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
        }
    }

    private void exportTransEVNNPC() {
        try {
            Date now = Calendar.getInstance().getTime();
            Map beans = new HashMap();

            String templateFolder = "/exceltemplate/";
            String reportFolder = "";

            String reportFileNameTmp = "TransactionEVNNPC" + "_" + DateTimeUtils.convertDateToString(now, "yyMMddHHmm");
            String reportFileNameExt = ".xls";

            String templateFileName = templateFolder + "transactionEVN.xls";

            String fromDate = getRequest().getSession().getAttribute("listReportEVNNPC.fromDate") == null ? "" : getRequest().getSession().getAttribute("listReportEVNNPC.fromDate").toString();
            String toDate = getRequest().getSession().getAttribute("listReportEVNNPC.toDate") == null ? "" : getRequest().getSession().getAttribute("listReportEVNNPC.toDate").toString();
            List<Map<String, Object>> lst = (List) getRequest().getSession().getAttribute("listReportEVNNPC");
            String reportFileName = reportFolder + reportFileNameTmp + reportFileNameExt;
            fileName = reportFileName;
            new Data2Excel().writeDatatoExcel(lst, getRequest().getRealPath(reportFileName), fromDate, toDate);

//            XLSTransformer transformer = new XLSTransformer();
//            transformer.transformXLS(getRequest().getRealPath(templateFileName), beans,
//                    getRequest().getRealPath(reportFileName));
            fileInputStream = new FileInputStream(new File(getRequest().getRealPath(reportFileName)));
            //fileInputStream = new FileInputStream(new File(getRequest().getRealPath(reportFileName)));

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
        }
    }

    private void exportReportEvnReg() {
        try {
            Date now = Calendar.getInstance().getTime();
            Map beans = new HashMap();

            String templateFolder = "/exceltemplate/";
            String reportFolder = getConfig("export_output");

            String reportFileNameTmp = getCpCode() + "_" + DateTimeUtils.convertDateToString(now, "yyMMddHHmm");
            String reportFileNameExt = ".xls";

            String templateFileName = templateFolder + "evnreg.xls";
            //<editor-fold defaultstate="collapsed" desc="fill cac thong tin chung">

            beans.put("fromDate", getRequest().getSession().getAttribute("listReportEvnReg.fromDate"));
            beans.put("toDate", getRequest().getSession().getAttribute("listReportEvnReg.toDate"));
            List<HashMap> lst = (List) getRequest().getSession().getAttribute("listReportEvnReg");
            if (lst != null) {
                beans.put("list", lst);
            } else {
                beans.put("list", new ArrayList());
            }

            String reportFileName = reportFolder + reportFileNameTmp + reportFileNameExt;
            fileName = reportFileName;
            XLSTransformer transformer = new XLSTransformer();
            transformer.transformXLS(getRequest().getRealPath(templateFileName), beans,
                    getRequest().getRealPath(reportFileName));

            fileInputStream = new FileInputStream(new File(getRequest().getRealPath(reportFileName)));

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
        }
    }

    private void exportTransFix() {
        try {
            Date now = Calendar.getInstance().getTime();
            Map beans = new HashMap();

            String templateFolder = "/exceltemplate/";
            String reportFolder = getConfig("export_output");

            String reportFileNameTmp = getCpCode() + "_" + DateTimeUtils.convertDateToString(now, "yyMMddHHmm");
            String reportFileNameExt = ".xls";

            String templateFileName = templateFolder + "TransFixRequest.xls";
            //<editor-fold defaultstate="collapsed" desc="fill cac thong tin chung">

            beans.put("fromDate", getRequest().getSession().getAttribute("listTransactionConfirm.fromDate"));
            beans.put("toDate", getRequest().getSession().getAttribute("listTransactionConfirm.toDate"));
            List<HashMap> lst = (List) getRequest().getSession().getAttribute("listTransactionConfirm");
            if (lst != null) {
                beans.put("list", lst);
            } else {
                beans.put("list", new ArrayList());
            }

            String reportFileName = reportFolder + reportFileNameTmp + reportFileNameExt;
            fileName = reportFileName;
            XLSTransformer transformer = new XLSTransformer();
            transformer.transformXLS(getRequest().getRealPath(templateFileName), beans,
                    getRequest().getRealPath(reportFileName));

            fileInputStream = new FileInputStream(new File(getRequest().getRealPath(reportFileName)));

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
        }
    }
}
