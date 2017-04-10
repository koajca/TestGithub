/*
 * Copyright (C) 2015 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.transaction;

import com.viettel.security.PassTranformer;
import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.TransferMerchant;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * @author cuongdv3@viettel.com.vn
 * @since Jul 22, 2015
 * @version 1.0
 */
public class TransferMerchantDAO extends GenericDAO<TransferMerchant, BigDecimal> {

    Logger log = Logger.getLogger(TransferMerchantDAO.class);

    public TransferMerchantDAO(Class<TransferMerchant> persistentClass, Session session) {
        super(persistentClass, session);
    }

    public static TransferMerchantDAO getInstance(Class<TransferMerchant> persistentClass,
            Session session) {
        return new TransferMerchantDAO(persistentClass, session);
    }

    public List<TransferMerchant> getListTransfer(Date fromDate, Date toDate, ArrayList<String> lstCpCode) {
        List<TransferMerchant> lst;
        Criteria crt = getSession().createCriteria(TransferMerchant.class);
        crt.add(Restrictions.in("cpCode", lstCpCode))
                .add(Restrictions.ge("requestDate", fromDate))
                .add(Restrictions.le("requestDate", toDate));
        lst = crt.list();
        return lst;
    }
    
    public static void main(String[] args) throws Exception{
        System.out.println(PassTranformer.decrypt("39036ae60c13db43ec55ebde022569b62d39b0f695440a2bd0179cae736a82e800a924ed2fde56bbf571308b7f466941"));
        //jdbc:oracle:thin:@10.58.34.10:1521:sms8k03
        System.out.println(PassTranformer.encrypt("jdbc:oracle:thin:@192.168.152.15:8521:sms8k03"));
    }

}
