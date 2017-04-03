<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="java.util.Calendar"%>

<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="sd" uri="struts-dojo-tags"%>

<%
    request.setAttribute("vt_locale", request.getParameter("request_locale"));
    request.setAttribute("contextPath", request.getContextPath());
    request.setAttribute("CSS_JS_contextPath", request.getContextPath());
%>

<s:i18n name="com/viettel/config/Language">
    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
    <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <meta name="description" content="Bankplus Mobile Web" />
            <title>Bankplus Mobile Web</title>
            <tiles:insertTemplate template="/WEB-INF/jsp/website/layout/external_CSS_JS_wap.jsp" />
        </head>
        <body>
            <div data-role="page" id="contentDialog" data-theme="c">
                <div data-role="content" >
                    <tiles:insertAttribute name="body"/>
                </div>
            </div>
        </body>
    </html>
</s:i18n>

