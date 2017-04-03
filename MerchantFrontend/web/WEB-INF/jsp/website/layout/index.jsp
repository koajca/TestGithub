<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
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
            <title>He thong bao cao doi soat</title>
            <tiles:insertTemplate template="/WEB-INF/jsp/website/layout/external_CSS_JS_wap.jsp" />
        </head>
        <body>
            <div data-role="page" id="mainpage" data-theme="c" data-cache="never" data-enhance="false">
                <div data-role="header">
                    <div class="header">
                        <img src="images/logo.png" alt="Viettel Bankplus" title="Viettel Bankplus" onclick="getHompage()" />
                        <% if (request.getSession().getAttribute("USERNAME") != null) {
                                String name = request.getSession().getAttribute("FULLNAME") == null ? "" : request.getSession().getAttribute("FULLNAME").toString();
                                String username = request.getSession().getAttribute("USERNAME") == null ? "" : request.getSession().getAttribute("USERNAME").toString();
                                String cp = request.getSession().getAttribute("CP_CODE") == null ? "" : request.getSession().getAttribute("CP_CODE").toString();

                        %>
                        <div style="float: right; margin-right: 10px; color: #ffffff">
                            Họ tên: <%=StringEscapeUtils.escapeHtml4(name)%><br/>
                            Tên đăng nhập: <%=StringEscapeUtils.escapeHtml4(username)%><br/>
                            Mã đối tác: <%=StringEscapeUtils.escapeHtml4(cp)%>
                        </div>
                        <%                         }
                        %>
                    </div>

                </div>
                <div id="cssmenu">
                    <tiles:insertAttribute name="menu"/>
                </div>

                <div data-role="content" style="float: left">
                    <tiles:insertAttribute name="body"/>


                </div>
                <br/><br/><br/>
                <div data-role="footer">
                    <div class="footer">
                        &copy; Bản quyền <%=StringEscapeUtils.escapeHtml3(Calendar.getInstance().get(Calendar.YEAR) + "")%> Viettel Telecom
                    </div>
                </div>
            </div>
        </body>
    </html>
</s:i18n>
