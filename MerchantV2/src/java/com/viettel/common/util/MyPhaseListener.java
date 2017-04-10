/*
 * Copyright (C) 2017 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util;

import com.ocpsoft.pretty.PrettyContext;
import com.viettel.bankplus.merchantgw.beans.entities.UserLoginInfo;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Feb 11, 2017
 * @version 1.0
 */
public class MyPhaseListener implements PhaseListener {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MyPhaseListener.class);
    private static final String FACES_REQUEST_HEADER = "faces-request";

    public void afterPhase(PhaseEvent event) {

        log.info("afterPhase: " + event.getPhaseId());
//        FacesContext fc = event.getFacesContext();
//        String prettyUrl = PrettyContext.getCurrentInstance().getRequestURL().toURL();
//        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
//        log.info("Pretty: " + prettyUrl);
//        log.info("Real: " + request.getRequestURL());
//        log.info("Real URI: " + request.getRequestURI());
    }

    public void beforePhase(PhaseEvent event) {
        log.debug("beforePhase: " + event.getPhaseId());

        if (event.getPhaseId() == PhaseId.RESTORE_VIEW || event.getPhaseId() == PhaseId.INVOKE_APPLICATION) {
            FacesContext fc = event.getFacesContext();
            HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
            HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
            String prettyUrl = PrettyContext.getCurrentInstance().getRequestURL().toURL();
            String servletUrl = request.getRequestURI();
            String contextPath = request.getContextPath();
            if (!"/".equals(contextPath)) {
                prettyUrl = prettyUrl.replace(contextPath, "");
                servletUrl = servletUrl.replace(contextPath, "");
            }
            if (Constants.NOT_VALIDATE_URL.contains(prettyUrl) || Constants.NOT_VALIDATE_URL.contains(servletUrl)) {
                return;
            }
            log.info("validate action url");
            UserLoginInfo loginInfo = (UserLoginInfo) ((HttpServletRequest) request).getSession().getAttribute("userLoginInfo");
            if (loginInfo == null || !loginInfo.isLoggedIn()) {
                log.info("user not logged in => redirect to login");
                try {

                    boolean ajaxRedirect = "partial/ajax".equals(request.getHeader(FACES_REQUEST_HEADER));
                    if (ajaxRedirect) {
                        String redirectUrl = contextPath + "/login.xhtml";
                        log.debug("Session expired due to ajax request, redirecting to " + redirectUrl);

                        String ajaxRedirectXml = createAjaxRedirectXml(redirectUrl);
                        response.setContentType("text/xml");
                        response.getWriter().write(ajaxRedirectXml);
                    } else {
                        response.sendRedirect(contextPath + "/login.xhtml");
                    }
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            } else if (!isValidUrl(loginInfo, prettyUrl, servletUrl)) {
                log.info("user not permision => redirect to login");
                try {

                    boolean ajaxRedirect = "partial/ajax".equals(request.getHeader(FACES_REQUEST_HEADER));
                    if (ajaxRedirect) {
                        String redirectUrl = contextPath + "/login.xhtml";
                        log.debug("Session expired due to ajax request, redirecting to " + redirectUrl);

                        String ajaxRedirectXml = createAjaxRedirectXml(redirectUrl);
                        response.setContentType("text/xml");
                        response.getWriter().write(ajaxRedirectXml);
                    } else {
                        response.sendRedirect(contextPath + "/login.xhtml");
                    }
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            }
        }
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    private String createAjaxRedirectXml(String redirectUrl) {
        return new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<partial-response><redirect url=\"")
                .append(redirectUrl)
                .append("\"></redirect></partial-response>")
                .toString();
    }

    private boolean isValidUrl(UserLoginInfo user, String prettyUrl, String servletUrl) {
        if (user == null) {
            return false;
        }
        if (user.getLstUserFunction() == null || user.getLstUserFunction().isEmpty()) {
            return false;
        }
        for (HashMap object : user.getLstUserFunction()) {
            if (prettyUrl.equalsIgnoreCase(object.get("OBJECT_URL").toString()) || servletUrl.equalsIgnoreCase(object.get("OBJECT_URL").toString())) {
                return true;
            }
        }
        return false;
    }

}
