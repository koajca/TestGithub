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
<div data-role="controlgroup" data-type="horizontal" style="margin: 0px">
    <%        
        if ((base.getUserRole().equals(base.ROLE_ADMIN) || base.getUserRole().equals(base.ROLE_MEMBER)) && !base.getCpCode().equals("EVNHCM")) {

    %>
    <a href="Transaction.do" data-ajax="false" data-theme="a">Tra cứu giao dịch</a>
    <%            }
    %>
    <%    
        if ((base.getUserRole().equals(base.ROLE_ADMIN) || base.getUserRole().equals(base.ROLE_MEMBER)) && base.getCpCode().equals("EVNHCM")) {
    %>
    <a href="TransactionEVN.do" data-ajax="false" data-theme="a">Tra cứu giao dịch EVN</a>
    <%            }
    %>
    <%    
        if (base.getUserRole().equals(base.ROLE_ADMIN)) {
    %>
    <a href="Transaction!initTransConfirm.do" data-ajax="false" data-theme="a">Xác nhận giao dịch</a>
    <%            }
    %>
    <%    
        if (base.getUserRole().equals(base.ROLE_ADMIN) || base.getUserRole().equals(base.ROLE_MEMBER)) {
    %>
    <a href="Report.do" data-ajax="false" data-theme="a">Báo cáo giao dịch</a>

    <a href="Report!getReportRefund.do" data-ajax="false" data-theme="a">Báo cáo giao dịch hoàn tiền</a>
    <%            }
    %>
    <%    
        if ((base.getUserRole().equals(base.ROLE_ADMIN) || base.getUserRole().equals(base.ROLE_MEMBER)) && base.getCpCode().equals("EVNHCM")) {
    %>
    <a href="Report!getReportTotalEVN.do" data-ajax="false" data-theme="a">Báo cáo tổng hợp giao dịch EVN</a>
    <%            }
    %>
    <%    
        if ((base.getUserRole().equals(base.ROLE_ADMIN) || base.getUserRole().equals(base.ROLE_MEMBER)) && (base.getCpCode().equals("EVNNPC") || base.getParentCP().equals("EVNNPC")) || base.getCpCode().startsWith("PA") || base.getCpCode().startsWith("PM") || base.getCpCode().startsWith("PH") || base.getCpCode().startsWith("PN")) {
    %>
    <a href="Report!getReportEVNNPC.do" data-ajax="false" data-theme="a">Báo cáo GD thanh toán điện</a>

    <%            }
    %>

    <%    
        if (base.getUserRole().equals(base.ROLE_ADMIN)) {
    %>
    <a href="Transaction!transfer.do" data-ajax="false" data-theme="a">Tra cứu GD chuyển tiền</a>
    <%            }
    %>

    <%    
        if ((base.getUserRole().equals(base.ROLE_ADMIN) || base.getUserRole().equals(base.ROLE_MEMBER)) && (base.getCpCode().equals("EVNHNI") || base.getParentCP().equals("933"))) {
    %>
    <a href="Report!onEvnReg.do" data-ajax="false" data-theme="a">Báo cáo đăng ký thanh toán hóa đơn điện</a>
    <%            }
    %>

    <a href="Index!getChangePass.do" data-ajax="false" data-theme="a">Đổi mật khẩu</a>
    <a href="Index!onLogout.do" data-theme="a" data-ajax="false" >Thoát</a>
</div>
<!-- panel content goes here -->
<%    
    }
%>