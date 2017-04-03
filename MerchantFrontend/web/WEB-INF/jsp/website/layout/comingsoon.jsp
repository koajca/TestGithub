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
<style type="text/css">
    .ui-loader-background {
        width:100%;
        height:100%;
        top:0;
        margin: 0;
        background: rgba(0, 0, 0, 0.3);
        display:none;
        position: fixed;
        z-index:100;
    }
    .footer {
        text-align: center;
        height: 22px;
        background: #CCCCCC;
        border: 1px solid #e5e2c4;

        margin-bottom: 0px;
        padding-top: 9px;
        width: 100%;
        vertical-align: middle;
        font-size: 80%;
        margin-top: 10px;
    }
    .successMessage {
        width: auto;
        min-height: 10px;
        margin: 10px 5px 10px 5px;
        border: 0px;
        padding: 5px 10px 5px 15px;
        color: #FF0000;
        text-align: center;
    }
</style>
<s:i18n name="com/viettel/config/Language">
    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
    <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <meta name="description" content="Bankplus Mobile Web" />
            <title><sd:Property>bankplus_mobile_web</sd:Property></title>
        </head>
        <body>
            <div data-role="page" id="mainpage" data-theme="a">
                <div data-role="content" >
                    <div align="center" class="successMessage">
                        <h3><sd:Property>comingsoon_title</sd:Property></h3>
                        </div>
                        <br/>
                        <div style="text-align: center; width: 100%" align="center">
                            <img src="images/comingsoon.png" width="40%" height="40%" alt="Coming Soon"/>
                        </div>
                    </div>

                    <div data-role="footer">
                        <div class="footer">
                            &copy; <sd:Property>index_copyright</sd:Property> <%=StringEscapeUtils.escapeHtml3(Calendar.getInstance().get(Calendar.YEAR) + "")%> <sd:Property>index_vtt</sd:Property>
                        </div>
                    </div>

                </div>
            </div>
            <div class="ui-loader-background"> </div>
        </body>
    </html>
</s:i18n>



