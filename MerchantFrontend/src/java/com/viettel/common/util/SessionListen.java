/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.common.util;

import java.util.Date;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author congth2
 */
public class SessionListen implements HttpSessionListener {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SessionListen.class);
    private int sessionCount;

    public SessionListen() {
        this.sessionCount = 0;
    }

    public void sessionCreated(HttpSessionEvent hse) {
        log.info("[SessionListen] sessionCreated......");
        HttpSession session = hse.getSession();
        synchronized (this) {
            sessionCount++;
        }
        String id = session.getId();
        Date now = new Date();        
        String message = new StringBuffer("[SessionListen] Session moi da duoc tao.").append(
                now.toString()).append("\nSession ID: ").append(id).append("\n")
                .append("So luong session dang active trong he thong: ").append("" + sessionCount).toString();

        log.info(message);
    }

    public void sessionDestroyed(HttpSessionEvent hse) {
        log.info("[SessionListen] sessionDestroyed......");
        HttpSession session = hse.getSession();
        String id = session.getId();
        synchronized (this) {
            --sessionCount;
        }
        String message = new StringBuffer("[SessionListen] Session da bi huy."
                + "\nSession ID = ").append("" + id).append(
                "\n").append("So luong session dang active trong he thong: ").append("" + sessionCount).toString();
        log.info(message);
    }
}
