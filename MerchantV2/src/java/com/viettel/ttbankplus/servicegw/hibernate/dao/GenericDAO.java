package com.viettel.ttbankplus.servicegw.hibernate.dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;

/**
 * @author cuongdv referenced from CaveatEmptor project tm JBoss Hibernate
 * version
 */
public abstract class GenericDAO<E, Id extends Serializable> {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GenericDAO.class);
    private Class<E> persistentClass;
//    public Session session;

    public GenericDAO(Class<E> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public GenericDAO(Class<E> persistentClass, Session session) {
        this.persistentClass = persistentClass;
    }

    protected Session getSession() {
        return DAOFactory.getCurrentSessionAndBeginTransaction();
    }

    public Class<E> getPersistentClass() {
        return persistentClass;
    }

    @SuppressWarnings("unchecked")
    public E findById(Id id, boolean lock) {
//        getSession().flush();
        E entity;
//        lock = false;
        if (lock) {
            entity = (E) getSession().get(getPersistentClass(), id,
                    LockMode.UPGRADE);
        } else {
            entity = (E) getSession().get(getPersistentClass(), id);
        }
        if (entity != null) {
            refreshObj(entity);
        }
        return entity;
    }

    public E findById(Id id) {
        E entity;

        entity = (E) getSession().get(getPersistentClass(), id);

//        if (entity != null) {
//            refreshObj(entity);
//        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    public List<E> findAll() {
        return getSession().createCriteria(getPersistentClass()).list();
    }

    @SuppressWarnings("unchecked")
    // exampleInstance la mau Object
    // excludeProperty la mot mang String chua ten cac property ma ta ko muon
    // dua vao tieu chi tim kiem
    public List<E> findByExample(E exampleInstance, String[] excludeProperty) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        crit.add(example);
        return crit.list();
    }

    // return number of row when Searching
    public int count(E exampleInstance, String[] excludeProperty, boolean isLike) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        if (isLike) {
            example.enableLike(MatchMode.ANYWHERE).ignoreCase();
        }
        return (Integer) crit.add(example).setProjection(Projections.rowCount()).uniqueResult();
    }

    public int count() {
        return (Integer) getSession().createCriteria(this.getPersistentClass()).setProjection(Projections.rowCount()).uniqueResult();
    }

    public int count(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return (Integer) crit.setProjection(Projections.rowCount()).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<E> findByCriteria(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public E makePersistent(E entity) {
        try {
            getSession().saveOrUpdate(entity);
//            getSession().flush();
            return entity;
        } catch (Exception e) {
            log.error(e, e);
            try {
                DAOFactory.closeCurrentSessions();
            } catch (Exception e1) {
                log.error("makePersistent:", e1);
            }
        }
        return null;
    }

    public void makeTransient(E entity) {
        try {
            getSession().delete(entity);
//            getSession().flush();
        } catch (Exception e) {
            log.error(e, e);
            try {
                DAOFactory.closeCurrentSessions();
            } catch (Exception e1) {
                log.error(e1, e1);
            }
        }
    }

    public void refreshObj(Object object) {
        getSession().refresh(object);
    }
}
