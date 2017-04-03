/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.processor.provider;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import java.util.List;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since 21-11-2011
 */
public class ContentProviderProcessor {

    private static ContentProviderProcessor instance;
    private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ContentProviderProcessor.class);

    public static ContentProviderProcessor getInstance() {
        if (instance == null) {
            instance = new ContentProviderProcessor();
        }
        return instance;
    }

    public ContentProvider getProviderById(Long cpId) {
        ContentProvider cp = null;
        try {
        if (cpId != null && cpId > 0) {
            cp = DAOFactory.getContentProviderDAO().findById(cpId, false);
            if(cp != null){
                DAOFactory.refreshObj(cp);
            }
        }
        } catch (Exception e) {
            log.error("error: ",e);
        }finally{
            DAOFactory.commitCurrentSessions();
        }
        return cp;
    }

    public ContentProvider getProviderByUsername(String username) {
        ContentProvider cp = null;
        if (username != null && !username.equals("")) {
            try {
                cp = DAOFactory.getContentProviderDAO().getProviderByUsername(username);
                log.debug(cp);
                if(cp != null){
                    DAOFactory.refreshObj(cp);
                }
            } catch (Exception ex) {
                log.error(ex);
            }
        }
        return cp;
    }

    public ContentProvider getProviderByCPCode(String telCo){
        ContentProvider cp = null;
        if (telCo != null && !telCo.equals("")) {
            try {
                cp = DAOFactory.getContentProviderDAO().getProviderByCPCode(telCo);
                log.debug(cp);
                if(cp != null){
                    DAOFactory.refreshObj(cp);
                }
            } catch (Exception ex) {
                log.error(ex);
            }
        }
        return cp;
    }

    public ContentProvider doSave(ContentProvider cp) {
        ContentProvider cpUpdated = null;
        try {
            cpUpdated = DAOFactory.getContentProviderDAO().makePersistent(cp);
            DAOFactory.commitCurrentSessions();
        } catch (Exception ex) {
            log.error("Update CP Failure: ", ex);
        }
        return cpUpdated;
    }
    public List getTreeProvider(Long startWith){
        try {
            return DAOFactory.getContentProviderDAO().getTreeProvider(startWith);
        } catch (Exception e) {
            log.error("getTreeProvider: ", e);
            return null;
        }
    }
}
