<%-- 
    Document   : menu
    Created on : Feb 1, 2013
    Author     : CuongDV
--%>

<%@page import="com.viettel.action.BaseAction"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="sd" uri="struts-dojo-tags"%>
<%
    BaseAction base = new BaseAction();
    if (base.isLoggedIn()) {
%>

<div data-role="panel" data-position="left" data-display="overlay" id="nav-panel" data-theme="c">
    <ul data-role="listview" data-theme="a" data-divider-theme="a" style="margin-top:0px;" class="nav-search">
        <li data-icon="delete" style="background-color:#111;">
            <a href="#" data-rel="close">Close menu</a>
        </li>
        <li>
            <a href="Index!getChangePass.do" data-ajax="false">Đổi mật khẩu</a>
        </li>
        <%
            if (base.getUserRole().equals(base.ROLE_ADMIN) || base.getUserRole().equals(base.ROLE_MEMBER)) {
        %>
        <li>
            <a href="Transaction.do" data-ajax="false">Tra cứu giao dịch</a>
        </li>
        <%            }
        %>
        <%
            if (base.getUserRole().equals(base.ROLE_ADMIN)) {
        %>
        <li>
            <a href="Transaction!initTransConfirm.do" data-ajax="false">Xác nhận giao dịch</a>
        </li>
        <%            }
        %>
        <%
            if (base.getUserRole().equals(base.ROLE_ADMIN) || base.getUserRole().equals(base.ROLE_MEMBER)) {
        %>
        <li>
            <a href="Report.do" data-ajax="false">Báo cáo giao dịch</a>
        </li>
        <li>
            <a href="Report!getReportRefund.do" data-ajax="false">Báo cáo giao dịch hoàn tiền</a>
        </li>
        <%            }
        %>
    </ul>

    <!-- panel content goes here -->
</div><!-- /panel -->
<%
    }
%>