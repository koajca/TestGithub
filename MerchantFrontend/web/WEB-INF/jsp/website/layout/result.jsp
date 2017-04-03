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
<%
    String mobileNumber = request.getSession().getAttribute("mobileBPNumber") == null ? "" : request.getSession().getAttribute("mobileBPNumber").toString();
    if (mobileNumber.equals("")) {
%>
<script>
    window.location = '${contextPath}/' + "Index.do?";
</script>
<% }
%>

<div>
    <h3><sd:Property>result_title</sd:Property> </h3>
    <div id="divListBank" style="text-align: left; width: 100%" align="center">
        <%            if (request.getSession()
                    .getAttribute("transStatus") != null && request.getSession().getAttribute("transStatus").toString().equalsIgnoreCase("success")) {
                String messageSuccess = request.getSession().getAttribute("messageSuccess") == null
                        ? "" : request.getSession().getAttribute("messageSuccess").toString();
        %>
        <div id="validate_moneyTransferInsize"  class="successMessage"><sd:Property>trans_success</sd:Property></div>
        <%=  StringEscapeUtils.escapeHtml3(messageSuccess)%>
        <%        } else {
        %>
        <div id="validate_moneyTransferInsize"  class="errorMessage"><sd:Property>trans_failed</sd:Property></div>
        <%            }
        %>
    </div>

</div>