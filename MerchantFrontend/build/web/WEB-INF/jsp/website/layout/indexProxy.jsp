<%-- 
    Document   : indexProxy
    Created on : Apr 1, 2011, 2:41:35 PM
    Author     : cn_longh
--%>

<%@page import="com.viettel.common.util.ResourceBundleUtil"%>
<%@page import="java.util.ResourceBundle"%>

<%
            response.sendRedirect(request.getContextPath() + "/Index.do?request_locale=vi_VN");
%>
