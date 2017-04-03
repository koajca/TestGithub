/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.warehouse;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Warehouse;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 11, 2014
 * @version 1.0
 */
public class WarehouseDAO extends GenericDAO<Warehouse, Long> {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WarehouseDAO.class);

    public WarehouseDAO(Class<Warehouse> persistentClass, Session session) {
        super(persistentClass, session);
    }

    public static WarehouseDAO getInstance(Class<Warehouse> persistentClass,
            Session session) {
        return new WarehouseDAO(persistentClass, session);
    }

    public List<Warehouse> getPincode(Long amount, int quantity, String cpCode, String transId, String serviceCode) {
        List<Warehouse> lst = new ArrayList<Warehouse>();
        CallableStatement call = null;
        ResultSet res = null;
        try {
            call = getSession().connection().prepareCall("{call PINCODE.PROC_GET_PINCODE(?,?,?,?,?,?)}");
            call.setLong(1, amount);
            call.setInt(2, quantity);
            call.setString(3, cpCode);
            call.setString(4, transId);
            call.setString(5, serviceCode);
            call.registerOutParameter(6, OracleTypes.CURSOR);
            call.execute();
            res = (ResultSet) call.getObject(6);
            while (res.next()) {
                Warehouse wh = new Warehouse();
                wh.setWarehouseId(res.getLong("warehouse_id"));
                wh.setSerial(res.getString("serial"));
                wh.setPincode(res.getString("pincode"));
                lst.add(wh);
            }
        } catch (Exception ex) {
            log.error("getPincode", ex);
        } finally {
            if (res != null) {
                try {
                    res.close();

                } catch (Exception e) {
                    log.error("error:", e);
                }
            }
            if (call != null) {
                try {
                    call.close();
                } catch (Exception e) {
                    log.error("error:", e);
                }
            }
            DAOFactory.commitCurrentSessions();
            
        }
        return lst;
    }
}
