/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bankplus.merchantgw.dao;

import com.viettel.common.util.DateTimeUtils;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jxl.format.CellFormat;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

/**
 *
 * @author longth1
 */
public class Data2Excel {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Data2Excel.class);
    private int startColIdx = 1;
    private int totalCol = 12;
    WritableWorkbook writableWorkbook;
    WritableSheet writableSheet;
    private static String header = "BÁO CÁO KHÁCH HÀNG ĐÃ THANH TOÁN TIỀN ĐIỆN TỪ HỆ THỐNG BANKPLUS";
    private static String merchantName = "NPC - ĐIỆN LỰC MIỀN BẮC";

    /**
     * ghi du lieu vao 1 cell
     *
     * @param data - du lieu
     * @param sheet
     * @param type
     * @param cellFormat
     * @param rowno - so dong
     * @param colno - sp cot
     */
    public void writeData2Cell(String data, WritableSheet sheet, String type, CellFormat cellFormat, int rowno, int colno) {
        try {
            // cellFormat.setWrap(true);
            if (type.equals("1")) {
                // Number
                sheet.addCell(new Number(colno, rowno, Double.parseDouble(data.isEmpty() ? "0" : data), cellFormat));
            } else {
                // log.info("colno=" + colno + "&rowno=" + rowno + "&data=" + data);
                // String
                sheet.addCell(new Label(colno, rowno, data, cellFormat));
            }

        } catch (Exception ex) {
            log.error("Error at writeData2Cell", ex);
        }

    }

    /**
     * ghi du lieu vao 1 dong excel
     *
     * @param data - du lieu
     * @param sheet
     * @param rowno - so dong
     */
    public void writeDatas2Row(List<Object> data, WritableSheet sheet, int rowno, String orderId) {
        try {
            WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 9);
            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
            cellFormat.setAlignment(Alignment.LEFT);
            cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            //cellFormat.setWrap(true);
            cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            writeData2Cell(orderId, sheet,
                    "2", cellFormat, rowno, 0);
            for (int i = 0; i < totalCol; i++) {

                if (i == 6) {

                    WritableCellFormat cellFormatTemp = new WritableCellFormat(cellFont);
                    cellFormatTemp.setAlignment(Alignment.RIGHT);
                    cellFormatTemp.setVerticalAlignment(VerticalAlignment.CENTRE);
                    cellFormatTemp.setBorder(Border.ALL, BorderLineStyle.THIN);
                    writeData2Cell(data.get(i) == null ? "" : data.get(i).toString(), sheet,
                            "2", cellFormatTemp, rowno, i + startColIdx);
                } else {
                    writeData2Cell(data.get(i) == null ? "" : data.get(i).toString(), sheet,
                            "2", cellFormat, rowno, i + startColIdx);
                }

            }
            // write cot cuoi (trang thai xac nhan)
            writeData2Cell("", sheet, "2", cellFormat, rowno, totalCol + startColIdx);
        } catch (Exception ex) {
            log.error("Error at writeDatas2Row", ex);
        }
    }

    public void writeTotal(String amount, WritableSheet sheet, int rowno) {
        try {

            WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 9);
            cellFont.setBoldStyle(WritableFont.BOLD);
            cellFont.setItalic(true);
            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
            cellFormat.setAlignment(Alignment.LEFT);
            cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            Label lableTemp = new Label(0, rowno,
                    "", cellFormat);
            sheet.addCell(lableTemp);

            //Merge col[0-3] and row[1]
            sheet.mergeCells(1, rowno, 6, rowno);

            Label lable = new Label(1, rowno,
                    "Tổng cộng ", cellFormat);
            sheet.addCell(lable);

//            lable = new Label(7, rowno,
//                    convertMoney(amount), cellFormat);
            WritableCellFormat cellFormatTemp = new WritableCellFormat(cellFont);
            cellFormatTemp.setAlignment(Alignment.RIGHT);
            cellFormatTemp.setVerticalAlignment(VerticalAlignment.CENTRE);
            cellFormatTemp.setBorder(Border.ALL, BorderLineStyle.THIN);
            DecimalFormat format = new DecimalFormat("0.#");
            lable = new Label(7, rowno,
                    format.format(Double.parseDouble(amount)), cellFormatTemp);
            sheet.addCell(lable);
            sheet.mergeCells(8, rowno, 13, rowno);
            lableTemp = new Label(8, rowno,
                    "", cellFormat);
            sheet.addCell(lableTemp);
        } catch (Exception ex) {
            log.error("Error at writeData2Cell", ex);
        }
    }

    public void writeAmountTotal(String numberGcs, String amount, WritableSheet sheet, int rowno) {
        try {
            log.info("numberGcs = " + numberGcs);
            WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 9);
            cellFont.setBoldStyle(WritableFont.BOLD);
            cellFont.setItalic(true);
            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
            cellFormat.setAlignment(Alignment.LEFT);
            cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            Label lableTemp = new Label(0, rowno,
                    "", cellFormat);
            sheet.addCell(lableTemp);

            //Merge col[0-3] and row[1]
            sheet.mergeCells(1, rowno, 6, rowno);

            Label lable = new Label(1, rowno,
                    "Tổng quyển " + numberGcs, cellFormat);
            sheet.addCell(lable);
            WritableCellFormat cellFormatTemp = new WritableCellFormat(cellFont);
            cellFormatTemp.setAlignment(Alignment.RIGHT);
            cellFormatTemp.setVerticalAlignment(VerticalAlignment.CENTRE);
            cellFormatTemp.setBorder(Border.ALL, BorderLineStyle.THIN);
            log.info("convertMoney(amount) = " + convertMoney(amount));
            DecimalFormat format = new DecimalFormat("0.#");
            lable = new Label(7, rowno,
                    format.format(Double.parseDouble(amount)), cellFormatTemp);
            sheet.addCell(lable);

            sheet.mergeCells(8, rowno, 13, rowno);
            lableTemp = new Label(8, rowno,
                    "", cellFormat);
            sheet.addCell(lableTemp);
        } catch (Exception ex) {
            log.error("Error at writeData2Cell", ex);
        }
    }

    public void writeHeader(WritableSheet sheet, String fromDate, String toDate) {
        try {
            WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 12);
            cellFont.setBoldStyle(WritableFont.BOLD);
            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
            cellFormat.setAlignment(Alignment.CENTRE);
            cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            sheet.mergeCells(0, 0, 8, 0);
            //Merge col[0-3] and row[1]
            sheet.mergeCells(0, 1, 14, 1);
            Label lable = new Label(0, 1, header, cellFormat);
            sheet.addCell(lable);
            sheet.mergeCells(0, 2, 14, 2);
            lable = new Label(0, 2, merchantName, cellFormat);

            sheet.addCell(lable);
            sheet.mergeCells(0, 3, 14, 3);
            lable = new Label(0, 3, "Từ ngày: " + fromDate + " Đến ngày: " + toDate, cellFormat);

            sheet.addCell(lable);

        } catch (Exception ex) {
            log.error("Error at writeData2Cell", ex);
        }
    }

    public void writeLabel(WritableSheet sheet, CellFormat cellFormat) {
        try {

            WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 9);
            cellFont.setBoldStyle(WritableFont.BOLD);
            WritableCellFormat cellFormatTemp = new WritableCellFormat(cellFont);

            cellFormatTemp.setAlignment(Alignment.CENTRE);
            cellFormatTemp.setVerticalAlignment(VerticalAlignment.CENTRE);
            cellFormatTemp.setWrap(true);
            cellFormatTemp.setBorder(Border.ALL, BorderLineStyle.THIN);

            sheet.setColumnView(0, 5);
            writeData2Cell("STT", sheet, "2", cellFormatTemp, 4, 0);
            sheet.setColumnView(1, 10);
            writeData2Cell("Ngày GD", sheet, "2", cellFormatTemp, 4, 1);
            sheet.setColumnView(2, 10);
            writeData2Cell("Số hóa đơn", sheet, "2", cellFormatTemp, 4, 2);
            sheet.setColumnView(3, 15);
            writeData2Cell("Mã KH EVN", sheet, "2", cellFormatTemp, 4, 3);
            sheet.setColumnView(4, 15);
            writeData2Cell("Tên KH", sheet, "2", cellFormatTemp, 4, 4);
            sheet.setColumnView(5, 15);
            writeData2Cell("Địa chỉ KH", sheet, "2", cellFormatTemp, 4, 5);
            sheet.setColumnView(6, 8);
            writeData2Cell("Số quyển", sheet, "2", cellFormatTemp, 4, 6);
            sheet.setColumnView(7, 10);
            writeData2Cell("Số tiền", sheet, "2", cellFormatTemp, 4, 7);
            sheet.setColumnView(8, 8);
            writeData2Cell("Mã CN", sheet, "2", cellFormatTemp, 4, 8);
//            WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 13);
//            WritableCellFormat cellFormatTemp = new WritableCellFormat(cellFont);
//            cellFormatTemp.setAlignment(Alignment.LEFT);
//            cellFormatTemp.setVerticalAlignment(VerticalAlignment.CENTRE);
//            cellFormatTemp.setWrap(true);
            sheet.setColumnView(9, 19);
            writeData2Cell("Mã giao dịch Bankplus", sheet, "2", cellFormatTemp, 4, 9);
            sheet.setColumnView(10, 15);
            writeData2Cell("Số điện thoại thanh toán", sheet, "2", cellFormatTemp, 4, 10);
            sheet.setColumnView(11, 10);
            writeData2Cell("Ngân hàng", sheet, "2", cellFormatTemp, 4, 11);
            sheet.setColumnView(12, 10);
            writeData2Cell("Trạng thái", sheet, "2", cellFormatTemp, 4, 12);
            sheet.setColumnView(13, 10);
            writeData2Cell("Trạng thái xác nhận", sheet, "2", cellFormatTemp, 4, 13);

        } catch (Exception ex) {
            log.error("Error at writeData2Cell", ex);
        }
    }

    public void writeDatatoExcel(List<Map<String, Object>> lstData, String file, String fromDate, String toDate) {
        try {
            File exlFile = new File(file);
            writableWorkbook = Workbook.createWorkbook(exlFile);
            writableSheet = writableWorkbook.createSheet("Sheet1", 1);
            writeHeader(writableSheet, fromDate, toDate);
            log.info("Export excel voi so ban ghi " + lstData.size());

            WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 13);

            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
            cellFormat.setAlignment(Alignment.CENTRE);
            cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            writeLabel(writableSheet, cellFormat);
            String oldNumberGcs = "oldNumberGcs";
            String currenNumberGcs = "";
            int rowno = 4;
            Double amount = 0.0;
            Double amountTotal = 0.0;
            for (int i = 0; i < lstData.size(); i++) {
                rowno++;
                List<Object> data = getListFromMap(lstData.get(i));
                currenNumberGcs = data.get(5) == null ? "" : data.get(5).toString();
                log.info("currenNumberGcs = " + currenNumberGcs);
                log.info("oldNumberGcs = " + oldNumberGcs);
                if (i != 0 && !oldNumberGcs.equals("") && !currenNumberGcs.equals(oldNumberGcs)) {
                    // neu gia tri currenNumber != oldNumber, insert dong tong tien cua oldNumber
                    writeAmountTotal(oldNumberGcs, amount.toString(), writableSheet, rowno);
                    rowno++;
                    amount = 0.0;
                }
                int orderId = i + 1;
                writeDatas2Row(data, writableSheet, rowno, "" + orderId);
                oldNumberGcs = currenNumberGcs;
                String amountTemp = data.get(6) == null ? "0.0" : data.get(6).toString();
                amount += Double.parseDouble(amountTemp);
                amountTotal += Double.parseDouble(amountTemp);
//                PHUCPT edit Jan, 04th 2017
                if (i % 65000 != 0) {
                    writableSheet = writableWorkbook.createSheet("Sheet" + (i / 65000), (i / 65000));
                    writeDatas2Row(data, writableSheet, rowno, "" + orderId);
//                    i = 0;
                    rowno = 1;
                }
//                end edit
            }
            if (lstData.size() > 0) {
                writeAmountTotal(oldNumberGcs, amount.toString(), writableSheet, rowno + 1);
                rowno++;
            }
            writeTotal(amountTotal.toString(), writableSheet, rowno + 1);
            writableWorkbook.write();
            writableWorkbook.close();
            //  OutputStream out = new FileOutputStream(new File("F://testt.xls"));
//            outputStream.writeTo(out);
//            outputStream.close();

        } catch (Exception ex) {
            log.error("Error at writeDatatoExcel", ex);
        }
    }

    public List<Object> getListFromMap(Map<String, Object> map) {

        List<Object> list = new ArrayList<Object>();
        list.add(map.get("REQDATE"));
        list.add(map.get("ORDERID"));
        list.add(map.get("BILLINGCODE"));
        list.add(map.get("CUSTOMERNAME"));
        list.add(map.get("CUSTADDRESS"));
        list.add(map.get("NUMBERGCS"));
        list.add(map.get("AMOUNT"));
        list.add(map.get("MACN"));
        list.add(map.get("TRANSID"));
        list.add(map.get("MSISDN"));
        list.add(map.get("BANKCODE"));

        list.add(getStatusDesc(map.get("CPTRANSTATUS").toString()));
        return list;

    }

    public String getStatusDesc(String status) {
        if (status.equals("0")) {
            return "Chưa xử lý";
        }
        if (status.equals("1")) {
            return "Đang xử lý";
        }
        if (status.equals("2")) {
            return "Thành công";
        }
        if (status.equals("3")) {
            return "Đã hủy";
        }
        if (status.equals("4")) {
            return "Thất bại";
        }
        return "";
    }

    public static String convertMoney(String source) {

        Double src = Double.valueOf(source);

        try {

            if (src != null && src.toString().equals("0.0")) {
                return "0";
            }
            if (source != null && source.length() > 2) {

                int indexofDot = source.indexOf(".");
                String temp = "";
                String t = "";
                if (indexofDot == -1) {
                    temp = source.substring(0, source.length());
                } else {

                    temp = source.substring(0, indexofDot);

                    t = source.substring(indexofDot, source.length());
                    if (t.length() > 3) {
                        t = t.substring(0, 2);
                    }
                }
                int indexofZero = temp.indexOf("0");
                int c = -1;
                for (int i = 0; i < temp.length(); i++) {
                    if (!temp.substring(i, i + 1).equals("0")) {
                        c = i;
                        break;
                    }
                }
                if (indexofZero < c && c != -1) {
                    temp = temp.substring(c);
                }
                String dest = "";
                int j = 0;
                for (int i = temp.length() - 1; i >= 0; i--) {
                    dest += temp.charAt(i);
                    j++;
                    if (j % 3 == 0) {
                        dest += ",";
                    }
                }
                String desc = "";
                if (dest.charAt(dest.length() - 1) != ',') {
                    dest += dest.charAt(dest.length() - 1);
                }
                if (dest.length() > 1) {
                    for (int i = dest.length() - 2; i >= 0; i--) {
                        desc += dest.charAt(i);
                    }
                }
                return desc + t;
            } else {
                return source;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        try {
//            File exlFile = new File("F:/write_test1.xls");
//            Data2Excel ex = new Data2Excel();
//
//            WritableWorkbook writableWorkbook = Workbook.createWorkbook(exlFile);
//            WritableSheet writableSheet = writableWorkbook.createSheet("Sheet1", 1);
//            WritableFont cellFont = new WritableFont(WritableFont.COURIER, 16);
//            cellFont.setBoldStyle(WritableFont.BOLD);
//
//            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
//            Label label = new Label(0, 0, "Label (String)", cellFormat);
//            DateTime date = new DateTime(1, 0, new Date(), cellFormat);
//            // Boolean bool = new Boolean(2, 0, true);
//            Number num = new Number(3, 0, 9.99, cellFormat);
//            //Add the created Cells to the sheet 
//            writableSheet.addCell(label);
//            writableSheet.addCell(date);
//            // writableSheet.addCell(bool);
//           // writableSheet.addCell(num);
//            writableSheet.addCell(num);
//            //new Label //Write and close the workbook
//
//            writableWorkbook.write();
//            writableWorkbook.close();

//            Properties logProperties = new Properties();
//            logProperties.put("log4j.rootLogger", "INFO, CONSOLE");
//            logProperties.put("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
//            logProperties.put("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
//            logProperties.put("log4j.appender.CONSOLE.layout.ConversionPattern", "%d{HH:mm:ss} [%12.12t] %5.5p %40.40c: %m%n");
//            PropertyConfigurator.configure(logProperties);
            DBProcessor dbProcessor = new DBProcessor();
            Date fromDate = DateTimeUtils.convertStringToDate("01/01/2015", "dd/MM/yyyy");
            Date toDate = DateTimeUtils.convertStringToDate("22/01/2015", "dd/MM/yyyy");
            //List<Map<String, Object>> listTrans = dbProcessor.getTransactionProcessor().getReportEVNNPC(fromDate, toDate, "NONE", "782");
            // new Data2Excel().writeDatatoExcel(listTrans, "F://template.xls", "01/01/2015", "17/01/2015");
            // writeDatatoExcel(listTrans)        
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
