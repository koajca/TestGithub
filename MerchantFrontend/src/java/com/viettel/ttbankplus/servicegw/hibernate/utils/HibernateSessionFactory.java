/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ttbankplus.servicegw.hibernate.utils;

import com.viettel.security.PassTranformer;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since 22-06-2011
 * @version 1.0
 */
public class HibernateSessionFactory {
//	private static Log log = LogFactory.getLog(HibernateSessionFactory.class);

    static Logger log = Logger.getLogger(HibernateSessionFactory.class);
    private static String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml";
    private static String configFile = CONFIG_FILE_LOCATION;
//	private static Configuration configuration = new Configuration();
    private static AnnotationConfiguration configuration = new AnnotationConfiguration();
    private static SessionFactory sessionFactory;
    private static final ThreadLocal threadSession = new ThreadLocal();
//    private static final ThreadLocal threadTransaction = new ThreadLocal();
    private static final ThreadLocal threadInterceptor = new ThreadLocal();

    private HibernateSessionFactory() {
    }

    /**
     * Returns the ThreadLocal Session instance. Lazy initialize the
     * <code>SessionFactory</code> if needed.
     *
     * @return Session
     * @throws HibernateException
     */
    static {
        try {
            configuration.configure(configFile);

            String url = PassTranformer.decrypt(configuration.getProperty("hibernate.connection.url"));
            configuration.setProperty("hibernate.connection.url", url);
            String userName = PassTranformer.decrypt(configuration.getProperty("hibernate.connection.username"));
            configuration.setProperty("hibernate.connection.username", userName);
            String password = PassTranformer.decrypt(configuration.getProperty("hibernate.connection.password"));
            configuration.setProperty("hibernate.connection.password", password);
            sessionFactory = configuration.buildSessionFactory();

            sessionFactory = configuration.buildSessionFactory();
            // We could also let Hibernate bind it to JNDI:
            // configuration.configure().buildSessionFactory()
        } catch (Throwable ex) {
            // We have to catch Throwable, otherwise we will miss
            // NoClassDefFoundError and other subclasses of Error
            log.error("Building SessionFactory failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Returns the SessionFactory used for this static class.
     *
     * @return SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        /**//*
         * Instead of a static variable, use JNDI: SessionFactory sessions =
         * null; try { Context ctx = new InitialContext(); String jndiName =
         * "java:hibernate/HibernateFactory"; sessions =
         * (SessionFactory)ctx.lookup(jndiName); } catch (NamingException
         * ex) { throw new InfrastructureException(ex); } return sessions;
         */

        log.warn("new sessionFactory");
        if (sessionFactory == null) {
            rebuildSessionFactory();
        }
        return sessionFactory;
    }

    /**/
    /**
     * Returns the original Hibernate configuration.
     *
     * @return Configuration
     */
    public static Configuration getConfiguration() {
        return configuration;
    }

    /**/
    /**
     * Rebuild the SessionFactory with the static Configuration.
     *
     */
    public static void rebuildSessionFactory() {
        synchronized (sessionFactory) {
            try {
                log.warn("%%%% Creating SessionFactory %%%%");
                sessionFactory = getConfiguration().buildSessionFactory();
            } catch (Exception ex) {
                log.error("%%%% Error Creating SessionFactory %%%%");
                System.err.println("%%%% Error Creating SessionFactory %%%%" + ex.getMessage());
            }
        }
    }

    /**/
    /**
     * Rebuild the SessionFactory with the given Hibernate Configuration.
     *
     * @param cfg
     */
    public static void rebuildSessionFactory(AnnotationConfiguration cfg) {
        synchronized (sessionFactory) {
            try {
                sessionFactory = cfg.buildSessionFactory();
                configuration = cfg;
            } catch (Exception ex) {
                log.error("%%%% Error Creating SessionFactory %%%% ==" + ex.getMessage());
                System.err.println("%%%% Error Creating SessionFactory %%%%" + ex.getMessage());
            }
        }
    }

    /**/
    /**
     * Retrieves the current Session local to the thread.
     * <p/>
     * If no Session is open, opens a new Session for the running thread.
     *
     * @return Session
     */
    public static Session getSession() {
        Session s = (Session) threadSession.get();
        try {
            if (s == null || !s.isOpen()) {
                log.info("Get current Session from hibernate pool.");
                s = sessionFactory.getCurrentSession();
                try {
                    if (s == null || !s.isOpen()) {
                        log.info("Opening new Session for this thread.");
                        s = sessionFactory.openSession();
                    }
                } catch (Exception ex) {
                    log.error("", ex);
                    s = sessionFactory.openSession();
                }
                threadSession.set(s);
//                endTransaction();
            } else {
                log.info("Using current session in this thread.");
            }
        } catch (HibernateException ex) {
            log.error("unable to open hibernate session", ex);
        }
        return s;
    }

//    public static void endTransaction() {
//        log.info("End old Transaction in this thread.");
////        threadTransaction.set(null);
////        final Transaction tx = (Transaction) threadTransaction.get();
////        try {
////            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
//////                Session s = getCurrentSession();
//////                s.flush();
////                log.info("Committing transaction of this thread before end.");
////                tx.commit();
////            }
////        } catch (HibernateException ex) {
////            rollbackTransaction();
////            log.error("commitTransaction", ex);
////        } finally {
////            threadTransaction.set(null);
////        }
//        final Transaction tx = (Transaction) threadTransaction.get();
//        threadTransaction.set(null);
//        if (tx != null) {
//            new Thread() {
//                public void run() {
//                    try {
//                        if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
//                            log.info("Committing transaction of this thread before end.");
//                            tx.commit();
//                        }
//                    } catch (HibernateException ex) {
//                        log.error("endTransaction", ex);
//                    } finally {
//
//                    }
//                }
//            }.start();
//        }
//    }
//	public static Session getSession() {
//		Session session = (Session) threadSession.get();
//
//		if (session == null || !session.isOpen()) {
//			if (sessionFactory == null) {
//				rebuildSessionFactory();
//			}
//			session = (sessionFactory != null) ? sessionFactory.openSession()
//					: null;
//			threadSession.set(session);
//		}
//
//		return session;
//	}

    /**/
    /**
     * Closes the Session local to the thread.
     */
    public static void closeSession() {
        try {
            Session s = (Session) threadSession.get();
            threadSession.set(null);
            if (s != null && s.isOpen()) {
                log.debug("Closing Session of this thread.");
                s.close();
            }
        } catch (HibernateException ex) {
            log.error("%%%% closeSession  %%%% ==" + ex.getMessage());
            System.err.println("%%%% Error closeSession %%%%");
        }
    }

    public static void removeSession() {
        log.info("remove invalid session in this thread");
        threadSession.set(null);
    }

    /**/
    /**
     * Start a new database transaction.
     */
    public static void beginTransaction() {
//        Transaction tx = (Transaction) threadTransaction.get();
        try {
//            if (tx != null && !tx.isActive()) {
//                tx = null;
//                threadTransaction.set(null);
//            }
//            if (tx == null) {
//                log.debug("Starting new database transaction in this thread.");
//                tx = getSession().beginTransaction();
//                threadTransaction.set(tx);
//            }
//            if (tx != null && !tx.isActive()) {
//                tx = null;
//                threadTransaction.set(null);
//            }
//            if (tx == null) {
//                log.info("Starting new database transaction in this thread.");
////                Session s = (Session) threadSession.get();
////                if (s != null && s.isOpen()) {
////                    s.close();
////                    threadSession.set(null);
////                }
//                tx = getSession().beginTransaction();
//                threadTransaction.set(tx);
//            } else {
//                log.info("Using current database transaction in this thread.");
//            }
            getSession().beginTransaction();
        } catch (HibernateException ex) {
            log.error("%%%% beginTransaction  %%%% ==" + ex.getMessage());
            System.err.println("%%%% Error beginTransaction %%%%");
        }
    }

    /**/
    /**
     * Commit the database transaction.
     */
    public static boolean commitTransaction() {
        Session s = (Session) threadSession.get();
        try {
            if (s != null && s.isOpen()) {
                Transaction tx = s.getTransaction();
                try {
                    if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                        log.info("Committing database transaction of this thread.");
                        tx.commit();
                    } else {
                        log.info("Can't committing database transaction of this thread");
                    }
                } catch (Exception ex) {

                }
            } else {
                log.info("Can't committing database transaction of this thread");
            }
        } catch (HibernateException ex) {
            log.error("unable to open hibernate session", ex);
        } finally {
//            threadTransaction.set(null);
            closeSession();
        }

//        Transaction tx = (Transaction) threadTransaction.get();
//        try {
//            //log.info("Begin commitTransaction");
//            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
//                //  log.info("Committing database transaction of this thread.");
//                tx.commit();
//            } else {
//                log.info("Can't committing database transaction of this thread");
//            }
//            threadTransaction.set(null);
//        } catch (HibernateException ex) {
//            log.error("%%%% commitTransaction  %%%% ==", ex);
//            rollbackTransaction();
//
//            System.err.println("%%%% Error commitTransaction %%%%");
//            return false;
//        } finally {
//            threadTransaction.set(null);
//            closeSession();
//        }
        return true;
    }

    /**/
    /**
     * Commit the database transaction.
     */
    public static void rollbackTransaction() {
//        Transaction tx = (Transaction) threadTransaction.get();
//        try {
//            threadTransaction.set(null);
//            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
//                log.warn("Tyring to rollback database transaction of this thread.");
//                tx.rollback();
//            }
//        } catch (HibernateException ex) {
//            log.error("%%%% rollbackTransaction  %%%% ==" + ex.getMessage());
//            System.err.println("%%%% Error rollbackTransaction %%%%");
//        } finally {
//            closeSession();
//        }
    }

    public static void rollbackTransaction(Transaction tx) {
        try {
            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                log.warn("Tyring to rollback database transaction of this thread.");
                tx.rollback();
            }
        } catch (HibernateException ex) {
            log.error("%%%% rollbackTransaction  %%%% ==", ex);
//            System.err.println("%%%% Error rollbackTransaction %%%%");
        } finally {
            closeSession();
        }
    }

    /**/
    /**
     * Reconnects a Hibernate Session to the current Thread.
     *
     * @param session The Hibernate Session to be reconnected.
     */
    public static void reconnect(Session session) {
        try {
            session.reconnect();
            threadSession.set(session);
        } catch (HibernateException ex) {
            log.error("%%%% reconnect  %%%% ==" + ex.getMessage());
            System.err.println("%%%% Error reconnect %%%%");
        }
    }

    /**/
    /**
     * Disconnect and return Session from current Thread.
     *
     * @return Session the disconnected Session
     */
    public static Session disconnectSession() {

        Session session = getSession();
        try {
            threadSession.set(null);
            if (session.isConnected() && session.isOpen()) {
                session.disconnect();
            }
        } catch (HibernateException ex) {
            log.error("%%%% disconnectSession  %%%% ==" + ex.getMessage());
            System.err.println("%%%% Error disconnectSession %%%%");
        }
        return session;
    }

    /**/
    /**
     * Register a Hibernate interceptor with the current thread.
     * <p>
     * Every Session opened is opened with this interceptor after registration.
     * Has no effect if the current Session of the thread is already open,
     * effective on next close()/getSession().
     */
    public static void registerInterceptor(Interceptor interceptor) {
        threadInterceptor.set(interceptor);
    }

    private static Interceptor getInterceptor() {
        Interceptor interceptor = (Interceptor) threadInterceptor.get();
        return interceptor;
    }
}
