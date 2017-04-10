/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.processor.warehouse;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Warehouse;
import java.util.List;

/**
 * @author cuongdv3@viettel.com.vn
 * @since Nov 20, 2014
 * @version 1.0
 */
public class WarehouseProcessor {

    private static volatile WarehouseProcessor instance;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WarehouseProcessor.class);

    public static WarehouseProcessor getInstance() {
        if (instance == null) {
            instance = new WarehouseProcessor();
        }
        return instance;
    }

    public List<Warehouse> getPincode(Long amount, int quantity, String cpCode, String transId, String serviceCode) {
        return DAOFactory.getWarehouseDAO().getPincode(amount, quantity, cpCode, transId, serviceCode);
    }
}
