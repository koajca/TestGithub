/*
 * Copyright (C) 2013 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util;

import com.ocpsoft.pretty.PrettyContext;
import com.viettel.bankplus.merchantgw.beans.LoginBean;
import com.viettel.bankplus.merchantgw.beans.entities.UserLoginInfo;
import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Feb 7, 2013
 * @version 1.0
 */
public class LoginFilter implements Filter {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LoginFilter.class);

    private static final String FACES_REQUEST_HEADER = "faces-request";

    public void init(FilterConfig fc) throws ServletException {
        log.info("========= Login Filter Init =========");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest httpReq = ((HttpServletRequest) request);
        HttpServletResponse httpRes = ((HttpServletResponse) response);

        UserLoginInfo loginInfo = (UserLoginInfo) ((HttpServletRequest) request).getSession().getAttribute("userLoginInfo");
        if (loginInfo == null || !loginInfo.isLoggedIn()) {

            String contextPath = httpReq.getContextPath();

            boolean ajaxRedirect = "partial/ajax".equals(httpReq.getHeader(FACES_REQUEST_HEADER));
            if (ajaxRedirect) {
                String redirectUrl = contextPath + "/login.xhtml";
                log.debug("Session expired due to ajax request, redirecting to " + redirectUrl);

                String ajaxRedirectXml = createAjaxRedirectXml(redirectUrl);
                httpRes.setContentType("text/xml");
                httpRes.getWriter().write(ajaxRedirectXml);
            } else {
                httpRes.sendRedirect(contextPath + "/login.xhtml");
            }
        } else {
            fc.doFilter(request, response);
        }
    }

    public void destroy() {
        log.info("========= Login Filter Destroy =========");
    }

    private String createAjaxRedirectXml(String redirectUrl) {
        return new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<partial-response><redirect url=\"")
                .append(redirectUrl)
                .append("\"></redirect></partial-response>")
                .toString();
    }
}
