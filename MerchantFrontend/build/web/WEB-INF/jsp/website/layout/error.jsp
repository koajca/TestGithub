<%-- 
    Document   : result
    Created on : Jan 28, 2013, 2:29:46 PM
    Author     : LongTH1
--%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="sd" uri="struts-dojo-tags" %>
<div>
    <div id="divListBank" style="text-align: left; width: 100%" align="center">
        <%
            String errorDesc = request.getAttribute("wapErrorDesc") == null ? "" : request.getAttribute("wapErrorDesc").toString();
            if (!"".equals(errorDesc)) {                
        %>
        <div  class="errorMessage"><sd:Property>error_common</sd:Property></div>
        <%=  StringEscapeUtils.escapeHtml3(errorDesc)%>
        <%        }
        %>
    </div>
</div>