/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.transaction;

import com.viettel.bankplus.merchantgw.dao.entities.Transaction;
import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransCp;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.transform.Transformers;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since Oct 28, 2011
 */
public class TransCPDAO extends GenericDAO<TransCp, Long> {

    Logger log = Logger.getLogger(TransCPDAO.class);

    public TransCPDAO(Class<TransCp> persistentClass, Session session) {
        super(persistentClass, session);
    }

    public static TransCPDAO getInstance(Class<TransCp> persistentClass,
            Session session) {
        return new TransCPDAO(persistentClass, session);
    }

    public List<TransCp> getTransCp(Long cpId, String billingCode) {
        List<TransCp> lst = getSession().createCriteria(TransCp.class)
                .add(Expression.eq("contentProviderId", cpId))
                .add(Expression.eq("billingCode", billingCode))
                .list();
        DAOFactory.commitCurrentSessions();
        return lst;
    }

    public List<TransCp> getTransCpByOrderId(long cp_id, String orderId) {
        List<TransCp> lst = getSession().createCriteria(TransCp.class)
                .add(Expression.eq("secondCpId", cp_id))
                .add(Expression.eq("orderId", orderId).ignoreCase()).list();
        DAOFactory.commitCurrentSessions();
        return lst;
    }

    public TransCp getTransBy(Long cpId, String transId) {
//        DBProcessor dBProcessor = new DBProcessor();
//        ContentProvider cp = dBProcessor.getProviderProcessor().getProviderById(cpId);
        TransCp trans = (TransCp) getSession().createCriteria(TransCp.class)
                .add(Expression.eq("contentProviderId", cpId))
                .add(Expression.eq("transId", transId))
                .uniqueResult();
        DAOFactory.commitCurrentSessions();
        return trans;
    }

    public List<TransCp> getTransByOrderId(Long cpId, String orderId) {
//        DBProcessor dBProcessor = new DBProcessor();
//        ContentProvider cp = dBProcessor.getProviderProcessor().getProviderById(cpId);
        List<TransCp> lst = getSession().createCriteria(TransCp.class)
                .add(Expression.eq("contentProviderId", cpId))
                .add(Expression.eq("orderId", orderId).ignoreCase())
                .list();
        DAOFactory.commitCurrentSessions();
        return lst;
    }

    public TransCp getTransByTransId(String transId) {
        TransCp trans = (TransCp) getSession().createCriteria(TransCp.class)
                .add(Expression.eq("transId", transId).ignoreCase())
                .uniqueResult();
        DAOFactory.commitCurrentSessions();
        return trans;
    }

    public List<TransCp> getListTransBySecondCpIdAndOrderId(Long cpId, String orderId) {
        List<TransCp> lst = getSession().createCriteria(TransCp.class)
                .add(Expression.eq("secondCpId", cpId))
                .add(Expression.eq("orderId", orderId).ignoreCase())
                .list();
        DAOFactory.commitCurrentSessions();
        return lst;
    }

    public List<Transaction> getTrans(String where, HashMap param) {
        List lst = new ArrayList();
        try {

            String sql = "select cp.trans_id transId, cp.content_provider_id contentproviderid,  to_char(cp.request_date, 'dd/MM/yyyy hh24:mi:ss') requestDate, "
                    + "(case nvl(bp.error_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when '32' then \n"
                    + "(case nvl(bp.correct_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when 'null' then TO_CHAR(1)\n"
                    + "else TO_CHAR(4) end)\n"
                    + "when 'null' then\n"
                    + "(case nvl(bp.correct_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when 'null' then TO_CHAR(1)\n"
                    + "else TO_CHAR(4) end\n"
                    + ")\n"
                    + "when 'null' then\n"
                    + "(case nvl(bp.correct_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when 'null' then TO_CHAR(1)\n"
                    + "else TO_CHAR(4) end\n"
                    + ")\n"
                    + "else TO_CHAR(4)\n"
                    + "end\n"
                    + ")"
                    + " transStatus, "
                    + "cp.confirm_status confirmStatus, cp.billing_code billingCode, cp.order_id orderId, cp.amount, "
                    + "cp.order_info orderInfo, cp.trans_type transType, bp.bank_code bankCode, bp.cp_code cpCode, bp.msisdn, p.cp_name cpName, bp.customer_name customerName, bp.error_code || bp.correct_code errorCode from trans_cp cp join trans_bankplus bp on cp.trans_cp_id = bp.trans_cp_id "
                    + " join content_provider p on p.content_provider_id = cp.content_provider_id ";
//            sql += where;
//            sql += " order by cp.request_date desc";
//            log.debug(sql);
            //Rebuild where param
            int numparam = StringUtils.countMatches(where, ":");
//            log.info("NUM PARAM: " + numparam);
            ArrayList<Object> arrParam = new ArrayList<Object>();
            arrParam.add(0, "");
            for (int i = 0; i < numparam; i++) {
                for (Object object : param.keySet()) {
                    String key = object.toString();
                    Object val = param.get(key);
                    int index = where.indexOf(":");
                    int indexCheck = where.indexOf(":" + key);
                    if (index == indexCheck) {
                        if (val instanceof ArrayList) {
                            ArrayList arr = (ArrayList) val;
                            String add = "";
                            for (int j = 0; j < arr.size(); j++) {
                                arrParam.add(arr.get(j));
                                add += ",?";
                            }
                            add = add.substring(1);
                            where = where.substring(0, index) + add + where.substring(index + (":" + key).length());
                        } else if (val instanceof Date) {
                            Date d = (Date) val;
                            String date = new SimpleDateFormat("dd/MM/yyyy HH-mm-ss").format(d);
                            arrParam.add(date);
                            where = where.substring(0, index) + "to_date(?,'dd/MM/yyyy hh24-mi-ss')" + where.substring(index + (":" + key).length());
                        } else {
                            arrParam.add(val);
                            where = where.substring(0, index) + "?" + where.substring(index + (":" + key).length());
                        }
//                        arrParam.add(val);
//                        where = where.substring(0, index) + "?" + where.substring(index + (":" + key).length());
                        break;
                    }
                }
            }
            numparam = arrParam.size() - 1;
            sql += where;
            sql += " order by cp.request_date desc";
//            log.debug(sql);
            log.info("WHERE CLAUSE: " + where);
            log.info("LIST PARAM VALUE: " + arrParam);

            Session sess = DAOFactory.getNewSession();
//            SQLQuery query = sess.createSQLQuery(sql);

            Connection conn = sess.connection();

            PreparedStatement pstm = null;
            ResultSet rs = null;
            try {
                pstm = conn.prepareStatement(sql);
                for (int i = 0; i < numparam; i++) {
                    Object objVal = arrParam.get(i + 1);
                    if (objVal instanceof ArrayList) {
                        ArrayList arrlist = (ArrayList) objVal;
                        java.sql.Array sqlArray = null;
//                        oracle.jdbc.OracleConnection oracleConnection = conn.unwrap(OracleConnection.class);
                        if (arrlist.get(0) instanceof String) {

//                            sqlArray = oracleConnection.createArrayOf("VARCHAR", arrlist.toArray());
//                            sqlArray = conn.createArrayOf("VARCHAR", arrlist.toArray());
                        } else {
//                            sqlArray = conn.createArrayOf("NUMERIC", arrlist.toArray());
//                            sqlArray = oracleConnection.createArrayOf("INTEGER", arrlist.toArray());
                        }
                        pstm.setArray(i + 1, sqlArray);
                    } else if (objVal instanceof String) {
                        pstm.setString(i + 1, objVal.toString());
//                        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
//                        log.info("set string: " + (i + 1) + " - " + objVal.toString());
                    } else if (objVal instanceof Date) {
                        Date d = (Date) objVal;
//                        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(d);
                        java.sql.Timestamp sqlDate = new java.sql.Timestamp(d.getTime());
//                        log.info("set date: " + (i + 1) + " - " + sqlDate);
                        pstm.setTimestamp(i + 1, sqlDate);
//                        pstm.setString(i + 1, date);
                    } else {
                        pstm.setLong(i + 1, Long.parseLong(objVal.toString()));
//                        log.info("set long: " + (i + 1) + " - " + Long.parseLong(objVal.toString()));
                    }
                }
//                log.info("PREP: " + pstm.toString());
                rs = pstm.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int numCol = rsmd.getColumnCount();
                String[] arrCol = new String[numCol];
                String coltemp = "";
                for (int i = 0; i < numCol; i++) {
                    arrCol[i] = rsmd.getColumnName(i + 1);
                    coltemp += rsmd.getColumnName(i + 1) + "#";
                }
//                log.info("CCCCCC:" + coltemp);
                while (rs.next()) {
//                    log.info("AAAAAAAAAAAAA:" + rs.getString(1));
                    Transaction trans = new Transaction();
                    for (int i = 0; i < numCol; i++) {
//                        String data = rs.getString(arrCol[i]);
                        Object data = rs.getObject(arrCol[i]);
                        if (data != null) {
                            callSetFunction(trans, "set" + arrCol[i], data);
                        }
                    }
                    lst.add(trans);
                }

            } catch (Exception ex) {
                log.error("", ex);
            } finally {
//                log.info("================>finally");
                if (rs != null) {
                    rs.close();
                }
                if (pstm != null) {
                    pstm.close();
                }

                if (sess != null) {
                    sess.close();
                }
            }
//            query.setResultTransformer(Transformers.aliasToBean(Transaction.class));
//            for (Object object : param.keySet()) {
//                String key = object.toString();
//                Object val = param.get(key);
//                if (val instanceof ArrayList) { //For select in
//                    query.setParameterList(key, (ArrayList) val);
//                } else {
//                    query.setParameter(key, param.get(key));
//                }
//            }
//            log.info(query.toString());
//            lst = query.list();
//            ScrollableResults resultset = query.scroll(ScrollMode.FORWARD_ONLY);
//            resultset.beforeFirst();
//            while (resultset.next()) {
//                Object[] objres = resultset.get();
//                log.info(objres);
//            }
//            resultset.close();
        } catch (Exception ex) {
            log.error("getTrans: ", ex);
        } finally {
//            DAOFactory.commitCurrentSessions();
        }

        return lst;
    }

    /**
     * getter call reflection
     *
     * @param obj
     * @param getFuncName
     * @return
     * @throws Exception
     */
    private static Object callGetFunction(Object obj, String getFuncName) throws Exception {
        Method m = obj.getClass().getMethod(getFuncName, new Class[]{});
        Object ret = m.invoke(obj, new Object[]{});
        return ret;
    }

    /**
     * setter call reflection
     *
     * @param obj
     * @param setFuncName
     * @param param
     * @throws Exception
     */
    private static void callSetFunction(Object obj, String setFuncName, Object param) {
        try {
            Method m = obj.getClass().getMethod(setFuncName, param.getClass());
            m.invoke(obj, param);
        } catch (Exception ex) {

        }
    }

    public List<Transaction> getTransEVN(Date toDate, Date fromDate, String bankCode) {
        List lst = new ArrayList();
        try {
            String sql = " bp.BANK_CODE bankCode, bp.billing_code billingCode, to_char(bp.request_date, 'dd/MM/yyyy hh24:mi:ss')  requestDate, bs.order_id orderId, bs.order_amount amount";
            sql += " from trans_bankplus bp ";
            sql += " left join (select aa.*, bb.order_id, bb.amount order_amount, cc.num_order from trans_bankplus aa ";
            sql += " inner join billing_service bb on aa.trans_bankplus_id = bb.trans_bankplus_id";
            sql += " inner join (select trans_bankplus_id, count(*) num_order from billing_service group by trans_bankplus_id) cc";
            sql += " on aa.trans_bankplus_id = cc.trans_bankplus_id where aa.error_code = '00') bs";
            sql += " on bp.original_request_id = bs.request_id where bp.process_code = '300001' and bp.original_request_id is not null";
            sql += " and bp.error_code = '00' and bp.cp_code = 'EVNHCM' ";
            sql += " and bp.request_date >= to_date(:fromDate, 'dd/MM/yyyy hh24:mi:ss') and bp.request_date <= to_date(:toDate, 'dd/MM/yyyy hh24:mi:ss')";

            if (bankCode != null && !bankCode.equals("")) {
                sql += " and upper(bp.bank_code) = :bankCode";
            }
            sql += " order by bp.request_date asc";

            SQLQuery query = getSession().createSQLQuery(sql);

            query.setResultTransformer(Transformers.aliasToBean(Transaction.class));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            query.setParameter("fromDate", sdf.format(fromDate));
            query.setParameter("toDate", sdf.format(toDate));
            if (bankCode != null && !bankCode.equals("")) {
                query.setParameter("bankCode", bankCode);
            }
            log.info(query.toString());
            lst = query.list();
        } catch (Exception ex) {
            log.error("getTrans: ", ex);
        } finally {
            DAOFactory.commitCurrentSessions();
        }
        return lst;
    }

    public List<Map<String, Object>> getTransEVN2(Date toDate, Date fromDate, String bankCode) {
        // List lst = new ArrayList();
        List<Map<String, Object>> lst = new ArrayList();
        try {
            String sql = " select bp.BANK_CODE bankCode, bp.billing_code billingCode, to_char(bp.request_date, 'dd/MM/yyyy hh24:mi:ss')  requestDate, bs.order_id orderId, bs.order_amount amount";
            sql += " from trans_bankplus bp ";
            sql += " left join (select aa.*, bb.order_id, bb.amount order_amount, cc.num_order from trans_bankplus aa ";
            sql += " inner join billing_service bb on aa.trans_bankplus_id = bb.trans_bankplus_id";
            sql += " inner join (select trans_bankplus_id, count(*) num_order from billing_service group by trans_bankplus_id) cc";
            sql += " on aa.trans_bankplus_id = cc.trans_bankplus_id where aa.error_code = '00') bs";
            sql += " on bp.original_request_id = bs.request_id where bp.process_code = '300001' and bp.original_request_id is not null";
            sql += " and bp.error_code = '00' and bp.cp_code = 'EVNHCM' ";
            sql += " and bp.request_date >= to_date(:fromDate, 'dd/MM/yyyy hh24:mi:ss') and bp.request_date <= to_date(:toDate, 'dd/MM/yyyy hh24:mi:ss')";

            if (bankCode != null && !bankCode.equals("")) {
                sql += " and upper(bp.bank_code) = :bankCode";
            }
            sql += " order by bp.request_date asc";

            SQLQuery query = getSession().createSQLQuery(sql);
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            //query.setResultTransformer(Transformers.aliasToBean(Transaction.class));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            query.setParameter("fromDate", sdf.format(fromDate));
            query.setParameter("toDate", sdf.format(toDate));
//            
//            query.setParameter("fromDate", fromDate);
//            query.setParameter("toDate", toDate);
            if (bankCode != null && !bankCode.equals("")) {
                query.setParameter("bankCode", bankCode);
            }
            log.info(query.toString());
            lst = query.list();
        } catch (Exception ex) {
            log.error("getTrans: ", ex);
        } finally {
            DAOFactory.commitCurrentSessions();
        }
        return lst;
    }

//     public List<Transaction> getTransEVN(String where, HashMap param) {
//        List lst = new ArrayList();
//        try {
//            String sql = "select   to_char(cp.request_date, 'dd/MM/yyyy hh24:mi:ss') requestDate, "
//                         + " cp.billing_code billingCode, bs.order_id orderId, cp.amount,  bp.bank_code bankCode "
//                         + "  from trans_cp cp left join trans_bankplus bp on cp.trans_cp_id = bp.trans_cp_id "
//                         + "  left join billing_service bs on bp.trans_bankplus_id = bs.trans_bankplus_id";
//            sql += where;
//            sql += " order by cp.request_date desc";
//            SQLQuery query = getSession().createSQLQuery(sql);
//            query.setResultTransformer(Transformers.aliasToBean(Transaction.class));
//            for (Object object : param.keySet()) {
//                String key = object.toString();
//                query.setParameter(key, param.get(key));
//            }
//            log.info(query.toString());
//            lst = query.list();
//        } catch (Exception ex) {
//            log.error("getTrans: ", ex);
//        }
//        return lst;
//    }
    public List<Map<String, Object>> getReportTotal(Date fromDate, Date toDate, String by, String bankCode, ArrayList<Long> lstCpId) {
        List<Map<String, Object>> lst = new ArrayList();
        try {
//            String where = " where cp.trans_status = 2 and (cp.trans_type = 0 or cp.trans_type = 2) ";
            String where = " where (bp.error_code = '00' or bp.correct_code = '00') and (cp.trans_type = 0 or cp.trans_type = 2) ";
            where += " and cp.request_date >= to_date(:fromDate, 'dd/MM/yyyy hh24:mi:ss') and cp.request_date <= to_date(:toDate, 'dd/MM/yyyy hh24:mi:ss')";
            where += " and cp.content_provider_id in (:cpId) ";
            if (bankCode != null && !bankCode.equals("")) {
                where += " and upper(bp.bank_code) = :bankCode ";
            }
            String groupBy = "";
            String orderBy = "";
            String sql = "select bp.bank_code bankCode, to_char(cp.request_date, 'MM/yyyy') monDay, ";
            if (by.equals("MON")) {
                //Chuyen ngay thang thanh thang
                Calendar cal = Calendar.getInstance();
                cal.setTime(fromDate);
                Calendar calFrom = Calendar.getInstance();
                calFrom.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getMinimum(Calendar.DAY_OF_MONTH), 0, 0, 0);

                cal.setTime(toDate);
                Calendar calTo = Calendar.getInstance();
                calTo.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);

                fromDate = calFrom.getTime();
                toDate = calTo.getTime();

                sql = "select bp.bank_code bankCode, to_char(cp.request_date, 'MM/yyyy') monDay, ";
                groupBy = " group by  to_char(cp.request_date, 'MM/yyyy'), bp.bank_code, p.cp_code, p.cp_name ";
                orderBy = " order by to_date(monDay, 'MM/yyyy') desc";
            } else {
                sql = "select bp.bank_code bankCode, to_char(cp.request_date, 'dd/MM/yyyy') monDay, ";
                groupBy = " group by to_char(cp.request_date, 'dd/MM/yyyy'), bp.bank_code, p.cp_code, p.cp_name ";
                orderBy = " order by to_date(monDay, 'dd/MM/yyyy') desc";
            }
            sql += " SUM(CASE cp.trans_type WHEN 0 THEN 1 ELSE 0 END) totalPay,";
            sql += " SUM(CASE cp.trans_type WHEN 2 THEN 1 ELSE 0 END) totalRefund,";
            sql += " SUM(CASE cp.trans_type WHEN 0 THEN cp.AMOUNT ELSE 0 END) payAmount,";
            sql += " SUM(CASE cp.trans_type WHEN 2 THEN cp.AMOUNT ELSE 0 END) refundAmount,";
            sql += " SUM(nvl(bp.fee,0)) totalFee,";
            sql += " p.cp_code cpCode, p.cp_name cpName ";
            sql += " from trans_cp cp left join trans_bankplus bp on cp.trans_cp_id = bp.trans_cp_id";
            sql += " left join content_provider p on cp.content_provider_id = p.content_provider_id ";
            sql += where;
            sql += groupBy;
            sql += orderBy;
            log.debug(sql);
            SQLQuery query = getSession().createSQLQuery(sql);
//        query.setResultTransformer(Transformers.aliasToBean(ReportTotal.class));
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            query.setParameter("fromDate", sdf.format(fromDate));
            query.setParameter("toDate", sdf.format(toDate));
            
//            query.setParameter("fromDate", fromDate);
//            query.setParameter("toDate", toDate);
            query.setParameterList("cpId", lstCpId);
            if (bankCode != null && !bankCode.equals("")) {
                query.setParameter("bankCode", bankCode.toUpperCase());
            }
            lst = query.list();
        } catch (Exception ex) {
            log.error("getReportTotal: ", ex);
        } finally {
            DAOFactory.commitCurrentSessions();
        }
        return lst;
    }

    public List<Map<String, Object>> getReportTotalEVN(Date fromDate, Date toDate, String by, String bankCode, Long cpId) {
        List<Map<String, Object>> lst = new ArrayList();
        try {

            String where = " where cp.trans_status = 2 and (cp.trans_type = 0 or cp.trans_type = 2) ";
            where += " and cp.request_date >= to_date(:fromDate, 'dd/MM/yyyy hh24:mi:ss') and cp.request_date <= to_date(:toDate, 'dd/MM/yyyy hh24:mi:ss')";
//            where += " and cp.request_date >= :fromDate and cp.request_date <= :toDate";
            where += " and cp.content_provider_id = :cpId ";
            where += " and bp.process_code = '300001' ";
            where += " and bp.error_code = '00'  ";

            if (bankCode != null && !bankCode.equals("")) {
                where += " and upper(bp.bank_code) = :bankCode ";
            }
            String groupBy = " group by  to_char(cp.request_date, 'dd/MM/yyyy'), bp.bank_code";
            String orderBy = "";
            String sql = "select bp.bank_code bankCode, to_char(cp.request_date, 'MM/yyyy') monDay, sum(bs.num_order) totalBill, ";
            if (by.equals("MON")) {
                //Chuyen ngay thang thanh thang
                Calendar cal = Calendar.getInstance();
                cal.setTime(fromDate);
                Calendar calFrom = Calendar.getInstance();
                calFrom.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getMinimum(Calendar.DAY_OF_MONTH), 0, 0, 0);

                cal.setTime(toDate);
                Calendar calTo = Calendar.getInstance();
                calTo.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);

                fromDate = calFrom.getTime();
                toDate = calTo.getTime();

                sql = "select bp.bank_code bankCode, to_char(cp.request_date, 'MM/yyyy') monDay, sum(bs.num_order) totalBill, ";
                groupBy = " group by  to_char(cp.request_date, 'MM/yyyy'), bp.bank_code";
                orderBy = " order by to_date(monDay, 'MM/yyyy') desc";
            } else {
                sql = "select bp.bank_code bankCode, to_char(cp.request_date, 'dd/MM/yyyy') monDay, sum(bs.num_order) totalBill, ";
                groupBy = " group by  to_char(cp.request_date, 'dd/MM/yyyy'), bp.bank_code";
                orderBy = " order by to_date(monDay, 'dd/MM/yyyy') desc";
            }
            sql += " SUM(CASE cp.trans_type WHEN 0 THEN 1 ELSE 0 END) totalPay,";
            sql += " SUM(CASE cp.trans_type WHEN 2 THEN 1 ELSE 0 END) totalRefund,";
            sql += " SUM(CASE cp.trans_type WHEN 0 THEN bp.amount ELSE 0 END) payAmount,";
            sql += " SUM(CASE cp.trans_type WHEN 2 THEN bp.amount ELSE 0 END) refundAmount";

            sql += " from trans_cp cp left join trans_bankplus bp on cp.trans_cp_id = bp.trans_cp_id";
            sql += " left join (select aa.*, cc.num_order from trans_bankplus aa ";
            //sql += " inner join billing_service bb on aa.trans_bankplus_id = bb.trans_bankplus_id";
            sql += " inner join (select trans_bankplus_id, count(*) num_order from billing_service group by trans_bankplus_id) cc";
            sql += " on aa.trans_bankplus_id = cc.trans_bankplus_id where aa.error_code = '00') bs";
            sql += " on bp.original_request_id = bs.request_id ";

            sql += where;
            sql += groupBy;
            sql += orderBy;
            log.debug(sql);
            SQLQuery query = getSession().createSQLQuery(sql);
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            query.setParameter("fromDate", sdf.format(fromDate));
            query.setParameter("toDate", sdf.format(toDate));
//            query.setParameter("fromDate", fromDate);
//            query.setParameter("toDate", toDate);
            query.setParameter("cpId", cpId);

            if (bankCode != null && !bankCode.equals("")) {
                query.setParameter("bankCode", bankCode.toUpperCase());
            }
            lst = query.list();
        } catch (Exception ex) {
            log.error("getReportTotal: ", ex);
        } finally {
            DAOFactory.commitCurrentSessions();
        }
        return lst;
    }

    public List<Map<String, Object>> getReportRefund(Date fromDate, Date toDate, String by, String bankCode, ArrayList<Long> lstCpId) {
        List<Map<String, Object>> lst = new ArrayList();
        try {
            String where = " where cp.trans_type = 2 ";
            where += " and cp.request_date >= to_date(:fromDate, 'dd/MM/yyyy hh24:mi:ss') and cp.request_date <= to_date(:toDate, 'dd/MM/yyyy hh24:mi:ss')";
//            where += " and cp.request_date >= :fromDate and cp.request_date <= :toDate";
            where += " and cp.content_provider_id in (:cpId) ";
            if (bankCode != null && !bankCode.equals("")) {
                where += " and upper(bp.bank_code) = :bankCode ";
            }
            String groupBy = "";
            String orderBy = "";
            String sql = "select bp.bank_code bankCode, to_char(cp.request_date, 'MM/yyyy') monDay, ";
            if (by.equals("MON")) {
                //Chuyen ngay thang thanh thang
                Calendar cal = Calendar.getInstance();
                cal.setTime(fromDate);
                Calendar calFrom = Calendar.getInstance();
                calFrom.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getMinimum(Calendar.DAY_OF_MONTH), 0, 0, 0);

                cal.setTime(toDate);
                Calendar calTo = Calendar.getInstance();
                calTo.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);

                fromDate = calFrom.getTime();
                toDate = calTo.getTime();

                sql = "select bp.bank_code bankCode, to_char(cp.request_date, 'MM/yyyy') monDay, ";
                groupBy = " group by  to_char(cp.request_date, 'MM/yyyy'), bp.bank_code, p.cp_code, p.cp_name";
                orderBy = " order by to_date(monDay, 'MM/yyyy') desc";
            } else {
                sql = "select bp.bank_code bankCode, to_char(cp.request_date, 'dd/MM/yyyy') monDay, ";
                groupBy = " group by  to_char(cp.request_date, 'dd/MM/yyyy'), bp.bank_code, p.cp_code, p.cp_name";
                orderBy = " order by to_date(monDay, 'dd/MM/yyyy') desc";
            }
            sql += " SUM(CASE cp.trans_status WHEN '0' THEN 1 ELSE 0 END) totalpending,";
            sql += " SUM(CASE cp.trans_status WHEN '2' THEN 1 ELSE 0 END) totalsuccess,";
            sql += " SUM(CASE cp.trans_status WHEN '4' THEN 1 ELSE 0 END) totalfailure,";
            sql += " SUM(CASE cp.trans_status WHEN '0' THEN cp.amount ELSE 0 END) amountpending,";
            sql += " SUM(CASE cp.trans_status WHEN '2' THEN cp.amount ELSE 0 END) amountsuccess,";
            sql += " SUM(CASE cp.trans_status WHEN '4' THEN cp.amount ELSE 0 END) amountfailure,";
            sql += " p.cp_code cpCode, p.cp_name cpName ";
//            sql += " from trans_cp cp left join trans_bankplus bp on cp.trans_cp_id = bp.trans_cp_id";
            sql += " from trans_cp cp";
            sql += " join trans_cp cp2 on cp.original_trans_id = cp2.trans_id ";
            sql += " left join trans_bankplus bp on cp2.trans_cp_id = bp.trans_cp_id ";
            sql += " left join content_provider p on cp.content_provider_id = p.content_provider_id ";
            sql += where;
            sql += groupBy;
            sql += orderBy;
            log.debug(sql);
            SQLQuery query = getSession().createSQLQuery(sql);
//        query.setResultTransformer(Transformers.aliasToBean(ReportTotal.class));
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            query.setParameter("fromDate", sdf.format(fromDate));
            query.setParameter("toDate", sdf.format(toDate));
//            query.setParameter("fromDate", fromDate);
//            query.setParameter("toDate", toDate);
            query.setParameterList("cpId", lstCpId);
            if (bankCode != null && !bankCode.equals("")) {
                query.setParameter("bankCode", bankCode.toUpperCase());
            }
            lst = query.list();
        } catch (Exception ex) {
            log.error("getReportRefund: ", ex);
        } finally {
            DAOFactory.commitCurrentSessions();
        }
        return lst;
    }

    public List<TransCp> getTransByOriginTransId(String transId) {
        List<TransCp> lst = getSession().createCriteria(TransCp.class)
                .add(Expression.eq("originalTransId", transId))
                .addOrder(Order.desc("requestDate"))
                .list();
        DAOFactory.commitCurrentSessions();
        return lst;
    }

    public List<Map<String, Object>> getReportEVNNPCBackup(Date fromDate, Date toDate, String status, ArrayList<Long> lstCpId) {
        List<Map<String, Object>> lst = new ArrayList();
        log.debug("status = " + status);
        try {

            String where = "where bp.request_date >= :fromDate and bp.request_date <= :toDate";
            where += " and cp.content_provider_id in (:cpId) ";
            if (!status.equals("NONE")) {
                where += " and cp.TRANS_STATUS = :transStatus ";
            }
            where += " and bp.process_code='300001'";
            where += " and orgbp.process_code='300000'";

            String groupBy = "";
            String orderBy = "";
            String sql = "select to_char(cp.request_date, 'dd/MM/yyyy') reqDate,bs.order_id orderId,bp.BILLING_CODE billingCode,bp.CUSTOMER_NAME customerName, ";
            sql += "bs.CUST_ADDRESS custAddress,bs.NUMBER_GCS numberGCS,bp.AMOUNT amount,bs.maCN maCN,cp.TRANS_ID transId,bp.MSISDN msisdn,bp.BANK_CODE bankCode,bp.TRANS_STATUS transStatus,cp.TRANS_STATUS cpTranstatus";

            orderBy = " order by bs.NUMBER_GCS, to_date(reqDate, 'dd/MM/yyyy') desc";
            sql += " from trans_bankplus bp";
            sql += " join trans_bankplus orgbp on bp.original_request_id=orgbp.request_id";
            sql += " join trans_cp cp on cp.TRANS_CP_ID = bp.TRANS_CP_ID ";
            sql += " join billing_service bs on bs.TRANS_BANKPLUS_ID = orgbp.TRANS_BANKPLUS_ID ";
            sql += where;
            sql += orderBy;
            log.debug(sql);
            SQLQuery query = getSession().createSQLQuery(sql);
//        query.setResultTransformer(Transformers.aliasToBean(ReportTotal.class));
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            query.setParameterList("cpId", lstCpId);
            if (!status.equals("NONE")) {
                query.setParameter("transStatus", status);
            }

            lst = query.list();
        } catch (Exception ex) {
            log.error("getReportRefund: ", ex);
        } finally {
            DAOFactory.commitCurrentSessions();
        }
        return lst;
    }

    public List<Map<String, Object>> getReportEVNNPC(Date fromDate, Date toDate, String status, ArrayList<Long> lstCpId) {
        List<Map<String, Object>> lst = new ArrayList();
        log.debug("status = " + status);
        try {

            String sqlPart1 = "select xxxxxx.reqDate,xxxxxx.billingCode,\n"
                    + "yyyyyy.customerName, \n"
                    + "xxxxxx.amount,xxxxxx.transId,xxxxxx.msisdn,xxxxxx.bankCode,\n"
                    + "xxxxxx.transStatus,xxxxxx.cpTranstatus,yyyyyy.custAddress,yyyyyy.numberGCS,yyyyyy.maCN,yyyyyy.orderId,xxxxxx.original_request_id,xxxxxx.request_id\n"
                    + "from (select to_char(cp.request_date, 'dd/MM/yyyy') reqDate,UPPER(bp.BILLING_CODE) billingCode,\n"
                    + "bp.CUSTOMER_NAME customerName, \n"
                    + "bp.AMOUNT amount,cp.TRANS_ID transId,bp.MSISDN msisdn,bp.BANK_CODE bankCode,\n"
                    + ""
                    + "(case nvl(bp.error_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when '32' then \n"
                    + "(case nvl(bp.correct_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when 'null' then TO_CHAR(1)\n"
                    + "else TO_CHAR(4) end)\n"
                    + "when 'null' then\n"
                    + "(case nvl(bp.correct_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when 'null' then TO_CHAR(1)\n"
                    + "else TO_CHAR(4) end\n"
                    + ")\n"
                    + "when 'null' then\n"
                    + "(case nvl(bp.correct_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when 'null' then TO_CHAR(1)\n"
                    + "else TO_CHAR(4) end\n"
                    + ")\n"
                    + "else TO_CHAR(4)\n"
                    + "end\n"
                    + ")"
                    + " transStatus,"
                    + ""
                    + "(case nvl(bp.error_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when '32' then \n"
                    + "(case nvl(bp.correct_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when 'null' then TO_CHAR(1)\n"
                    + "else TO_CHAR(4) end)\n"
                    + "when 'null' then\n"
                    + "(case nvl(bp.correct_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when 'null' then TO_CHAR(1)\n"
                    + "else TO_CHAR(4) end\n"
                    + ")\n"
                    + "when 'null' then\n"
                    + "(case nvl(bp.correct_code,'null')\n"
                    + "when '00' then TO_CHAR(2)\n"
                    + "when 'null' then TO_CHAR(1)\n"
                    + "else TO_CHAR(4) end\n"
                    + ")\n"
                    + "else TO_CHAR(4)\n"
                    + "end\n"
                    + ")"
                    + " cpTranstatus,bp.original_request_id original_request_id, bp.request_id request_id\n"
                    + "from trans_bankplus bp \n"
                    + "join trans_cp cp on cp.TRANS_CP_ID = bp.TRANS_CP_ID \n"
                    + "where bp.request_date >= to_date(:fromDate, 'dd/MM/yyyy hh24:mi:ss') \n"
                    + "and bp.request_date <= to_date(:toDate, 'dd/MM/yyyy hh24:mi:ss') \n"
                    + " and cp.request_date >= to_date(:fromDate, 'dd/MM/yyyy hh24:mi:ss') \n"
                    + "and cp.request_date <= to_date(:toDate, 'dd/MM/yyyy hh24:mi:ss') \n"
                    + "and cp.content_provider_id in (:cpId) \n";

            String sqlPart2 = "and bp.process_code='300001'\n"
                    + ") xxxxxx\n"
                    + "join\n"
                    + "(select bs.CUSTOMER_NAME customerName, bs.CUST_ADDRESS custAddress,bs.NUMBER_GCS numberGCS ,bs.maCN maCN,bs.order_id orderId,orgbp.request_id request_id\n"
                    + "from trans_bankplus orgbp\n"
                    + "join  billing_service bs\n"
                    + "on bs.TRANS_BANKPLUS_ID = orgbp.TRANS_BANKPLUS_ID\n"
                    + "where orgbp.request_date >= to_date(:fromDate, 'dd/MM/yyyy hh24:mi:ss') \n"
                    + "and orgbp.request_date <=  to_date(:toDate, 'dd/MM/yyyy hh24:mi:ss') \n"
                    + "and orgbp.process_code='300000') yyyyyy\n"
                    + "on xxxxxx.original_request_id = yyyyyy.request_id"
                    + " order by yyyyyy.numberGCS, to_date(xxxxxx.reqDate, 'dd/MM/yyyy') desc";
            String sqlNew = sqlPart1;
            if (!status.equals("NONE")) {
                if (status.equals("2")) {
                    sqlNew += " and (bp.ERROR_CODE = '00' OR bp.CORRECT_CODE = '00') ";
                } else if (status.equals("1")) {
                    sqlNew += " and (bp.ERROR_CODE = '32' OR bp.ERROR_CODE is null) ";
                    sqlNew += " and bp.CORRECT_CODE is null ";
                } else if (status.equals("0")) {
                    sqlNew += " and cp.TRANS_STATUS = 0 ";
                } else if (status.equals("3")) {
                    sqlNew += " and cp.TRANS_STATUS = 3 ";
                } else if (status.equals("4")) {
                    sqlNew += " and ((bp.ERROR_CODE = '32' or bp.ERROR_CODE is null) and bp.CORRECT_CODE = '23')"
                            + " and bp.ERROR_CODE <> '00' "
                            + " and (bp.CORRECT_CODE is null or bp.CORRECT_CODE = '23' ";
                }
//                sqlNew += " and cp.TRANS_STATUS = :transStatus ";
            }
            sqlNew += sqlPart2;
//            String where = "where bp.request_date >= :fromDate and bp.request_date <= :toDate";
//            where += " and cp.content_provider_id in (:cpId) ";
//            if (!status.equals("NONE")) {
//                where += " and cp.TRANS_STATUS = :transStatus ";
//            }
//            where += " and bp.process_code='300001'";
//            where += " and orgbp.process_code='300000'";
//
//            String groupBy = "";
//            String orderBy = "";
//            String sql = "select to_char(cp.request_date, 'dd/MM/yyyy') reqDate,bs.order_id orderId,bp.BILLING_CODE billingCode,bp.CUSTOMER_NAME customerName, ";
//            sql += "bs.CUST_ADDRESS custAddress,bs.NUMBER_GCS numberGCS,bp.AMOUNT amount,bs.maCN maCN,cp.TRANS_ID transId,bp.MSISDN msisdn,bp.BANK_CODE bankCode,bp.TRANS_STATUS transStatus,cp.TRANS_STATUS cpTranstatus";
//
//            orderBy = " order by bs.NUMBER_GCS, to_date(reqDate, 'dd/MM/yyyy') desc";
//            sql += " from trans_bankplus bp";
//            sql += " join trans_bankplus orgbp on bp.original_request_id=orgbp.request_id";
//            sql += " join trans_cp cp on cp.TRANS_CP_ID = bp.TRANS_CP_ID ";
//            sql += " join billing_service bs on bs.TRANS_BANKPLUS_ID = orgbp.TRANS_BANKPLUS_ID ";
//            sql += where;
//            sql += orderBy;
            log.debug(sqlNew);
            SQLQuery query = getSession().createSQLQuery(sqlNew);
//        query.setResultTransformer(Transformers.aliasToBean(ReportTotal.class));
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            query.setParameterList("cpId", lstCpId);
//            if (!status.equals("NONE")) {
//                
//                query.setParameter("transStatus", status);
//            }

            lst = query.list();
        } catch (Exception ex) {
            log.error("getReportRefund: ", ex);
        } finally {
            DAOFactory.commitCurrentSessions();
        }
        return lst;
    }

    public List<HashMap> getReport(String sql, HashMap param) {
        List lst = new ArrayList();
        try {

            SQLQuery query = getSession().createSQLQuery(sql);

            query.setResultTransformer(Transformers.aliasToBean(Transaction.class));
            for (Object object : param.keySet()) {
                String key = object.toString();
                Object val = param.get(key);
                if (val instanceof ArrayList) { //For select in
                    query.setParameterList(key, (ArrayList) val);
                } else {
                    query.setParameter(key, param.get(key));
                }
            }
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            log.info(query.toString());
            lst = query.list();
        } catch (Exception ex) {
            log.error("getReport: ", ex);
        } finally {
            DAOFactory.commitCurrentSessions();
        }
        return lst;
    }

}
