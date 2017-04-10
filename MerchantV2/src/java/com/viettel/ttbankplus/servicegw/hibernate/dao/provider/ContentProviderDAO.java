/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.provider;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.ContentProvider;
import java.util.HashMap;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @version 1.0
 * @since Oct 28, 2011
 */
public class ContentProviderDAO extends GenericDAO<ContentProvider, Long> {

    public ContentProviderDAO(Class<ContentProvider> persistentClass, Session session) {
        super(persistentClass, session);
    }

    public static ContentProviderDAO getInstance(Class<ContentProvider> persistentClass,
            Session session) {
        return new ContentProviderDAO(persistentClass, session);
    }

    public ContentProvider getProviderByUsername(String username) {
        ContentProvider cp = (ContentProvider) getSession().createCriteria(ContentProvider.class)
                //.add(Restrictions.eq("userName", username.trim()))
                .add(Expression.eq("userName", username).ignoreCase())
                .uniqueResult();
        DAOFactory.commitCurrentSessions();
        return cp;
    }

    public ContentProvider getProviderByCPCode(String telCode) {
        ContentProvider cp = (ContentProvider) getSession().createCriteria(ContentProvider.class)
                .add(Expression.eq("cpCode", telCode.trim()).ignoreCase())
                .uniqueResult();
        DAOFactory.commitCurrentSessions();
        return cp;
    }

    public List<HashMap<String, String>> getTreeProvider(Long startWith) {
        List lst;
        SQLQuery sqlQuery = getSession().createSQLQuery("select content_provider_id, cp_code, cp_name from CONTENT_PROVIDER CONNECT BY PRIOR content_provider_id = parent start with content_provider_id = :cpId order siblings by cp_name");
        sqlQuery.setParameter("cpId", startWith);
        sqlQuery.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        lst = sqlQuery.list();
        DAOFactory.commitCurrentSessions();
        return lst;
    }

}
