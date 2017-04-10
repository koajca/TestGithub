/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.processor.transbankplusprocessor;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.HoaDonNuoc;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransBillingSuccess;
import com.viettel.ttbankplus.servicegw.hibernate.dao.transaction.HoaDonNuocDAO;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 04, 2014
 * @version 1.0
 */
public class HoaDonNuocProcessor {

    Logger log = Logger.getLogger(HoaDonNuocProcessor.class);
    private static volatile HoaDonNuocProcessor instance;

    public static HoaDonNuocProcessor getInstance() {
        if (instance == null) {
            instance = new HoaDonNuocProcessor();
        }
        return instance;
    }

    public HoaDonNuoc doSave(HoaDonNuoc hoadon) {
        HoaDonNuoc hoadonUpdated = null;
        try {
            hoadonUpdated = DAOFactory.getHoaDonNuocDAO().makePersistent(hoadon);
            DAOFactory.commitCurrentSessions();
        } catch (Exception ex) {
            log.error("doSave: ", ex);
        }
        return hoadonUpdated;
    }

    public int doSave(List<HoaDonNuoc> hoadons) {
        int countSucess = 0;
        if (hoadons == null || hoadons.size() == 0) {
            log.info("HoaDonNuoc is null or empty");
            return 0;
        }
        try {
//            HoaDonNuocDAO hdnDAO = DAOFactory.getHoaDonNuocDAO();
            for (HoaDonNuoc item : hoadons) {
                if (DAOFactory.getHoaDonNuocDAO().makePersistent(item) != null) {
                    countSucess++;
                }
            }
//            Thread.sleep(5000);
            DAOFactory.commitCurrentSessions();
        } catch (Exception ex) {
            log.error("doSave: ", ex);
        }
        return countSucess;

    }
}
