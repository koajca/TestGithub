/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.dao.users;

import com.viettel.ttbankplus.servicegw.hibernate.dao.DAOFactory;
import com.viettel.ttbankplus.servicegw.hibernate.dao.GenericDAO;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Users;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 11, 2014
 * @version 1.0
 */
public class UsersDAO extends GenericDAO<Users, Long> {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(UsersDAO.class);

    public UsersDAO(Class<Users> persistentClass, Session session) {
        super(persistentClass, session);
    }

    public static UsersDAO getInstance(Class<Users> persistentClass,
            Session session) {
        return new UsersDAO(persistentClass, session);
    }

    public Users getUser(String username, Long contentProviderId) {
        Users user = null;
        try {
            user = (Users) getSession().createCriteria(Users.class)
                    .add(Expression.eq("username", username).ignoreCase())
                    .add(Expression.eq("contentProviderId", contentProviderId))
                    .uniqueResult();
        } catch (Exception ex) {
            log.error("getUserByUsername", ex);
        } finally {
            DAOFactory.commitCurrentSessions();
        }

        return user;
    }

    public List<HashMap> getUserFunction(Long userId) {
        List<HashMap> lst;
        String sql = "select distinct object_code, object_url, object_name from (\n"
                + "select object_code, object_url, object_name from list_objects where object_id in (\n"
                + "select object_id from user_object where user_id = :userId and status = 1\n"
                + ") and status = 1\n"
                + "union\n"
                + "select object_code, object_url, object_name from list_objects where object_id in (\n"
                + "select object_id from role_object where role_id in (\n"
                + "select role_id from user_role where user_id = :userId and status = 1\n"
                + ") and status = 1\n"
                + ") and status = 1 \n"
                + ") "
                + "order by object_name";
        try {
            SQLQuery query = getSession().createSQLQuery(sql);
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            query.setParameter("userId", userId);
            lst = query.list();
            return lst;
        } catch (Exception ex) {
            log.error("getUserFunction", ex);
            lst = null;
        } finally {
            DAOFactory.commitCurrentSessions();
        }
        return lst;
    }
}
