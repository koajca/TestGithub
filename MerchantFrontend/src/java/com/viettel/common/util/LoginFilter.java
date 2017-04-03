/*
 * Copyright (C) 2013 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util;

import java.io.IOException;
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

    public void init(FilterConfig fc) throws ServletException {
        log.info("========= Login Filter Init =========");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        log.info("LoginFilter.doFilter begin........................................");
        HttpServletRequest req = null;
        HttpServletResponse res = null;

        if ((request instanceof HttpServletRequest)) {
            req = (HttpServletRequest) request;
        }
        if ((response instanceof HttpServletResponse)) {
            res = (HttpServletResponse) response;
        }
        String allowURL = req.getContextPath() + "/Index.do";
        String allowURL2 = req.getContextPath() + "/Index!onLogin.do";
        String allowURL3 = req.getContextPath() + "/Index.do?";
        String allowURL4 = req.getContextPath() + "/Index!onLogin.do?";
        String allowURL5 = req.getContextPath() + "/VBSRegistration.do?";
        String allowURL6 = req.getContextPath() + "/VBSRegistration!doRegistration.do?";

        log.info("request:" + req.getRequestURI());

        if (req.getRequestURI().equalsIgnoreCase(allowURL) || req.getRequestURI().equalsIgnoreCase(allowURL2)
                || req.getRequestURI().equalsIgnoreCase(allowURL3) || req.getRequestURI().equalsIgnoreCase(allowURL4)
                || req.getRequestURI().equalsIgnoreCase(allowURL5) || req.getRequestURI().equalsIgnoreCase(allowURL6)) {
            fc.doFilter(req, res);
        } else {
            String mobileNumber = req.getSession().getAttribute("mobileBPNumber") == null ? "" : req.getSession().getAttribute("mobileBPNumber").toString();
            if (mobileNumber.equals("")) {
                res.sendRedirect(req.getContextPath() + "/Index.do?");
            } else {
                fc.doFilter(req, res);
            }
        }
        log.info("LoginFilter.doFilter end........................................");
    }

    public void destroy() {
        log.info("========= Login Filter Destroy =========");
    }
}
