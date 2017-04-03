/*
 * Copyright (C) 2013 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.StrutsStatics;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 26, 2013
 * @version 1.0
 */
public class AuthenticationInterceptor extends AbstractInterceptor {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AuthenticationInterceptor.class);

    @Override
    public String intercept(ActionInvocation ai) throws Exception {
        log.info("========= Interceptor Begin =========");
        final ActionContext context = ai.getInvocationContext();
        try {
            HttpServletRequest request = (HttpServletRequest) context.get(StrutsStatics.HTTP_REQUEST);
            HttpServletResponse response = (HttpServletResponse) context.get(StrutsStatics.HTTP_RESPONSE);

            String servletPath = request.getServletPath();
            if (servletPath != null && !servletPath.startsWith("/")) {
                servletPath = "/" + servletPath;
            }

            log.debug("[INTERCEPTER] request action " + servletPath);

//            if (request.getSession() == null || request.getSession().isNew()) {
//                log.info("session timeout, redirect ve trang login");
//                response.sendRedirect(request.getContextPath() + "/Index.do?request_locale=vi_VN");
//                return "loginPage";
//            }

            String[] actionsValidate = ResourceBundleUtil.getString(
                    "not_validate_action").split(";");
            for (int i = 0; i < actionsValidate.length; i++) {
                if (servletPath.startsWith(actionsValidate[i].trim())) {
                    log.debug(servletPath + " ton tai trong danh sach khong can check, roles pass.");
                    return ai.invoke();
                }
            }

            if (request.getSession().getAttribute("USERNAME") != null
                    && !"".contains(request.getSession().getAttribute("USERNAME").toString().trim())) {
                String username = request.getSession().getAttribute("USERNAME").toString().trim();
                log.info("User Loged In: " + username);
                return ai.invoke();
            }

            return "loginPage";
        } catch (Exception e) {
            log.error("intercept", e);
            return "Exception";
        }
    }
}
