<%--
    Document   : transactionResult.jsp
    Created on : Mar 11, 2014, 2:00:20 PM
    Author     : CuongDV3
--%>
<%@page import="com.viettel.action.BaseAction"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sd" uri="struts-dojo-tags" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<h3>Chi tiết giao dịch</h3>
<div style="float: left; width: 200px">Ngân hàng</div>
<div style="float: left"><s:property value="trans.BANKCODE"/></div>
<div style="clear: both" />
<div style="float: left; width: 200px">Mã NCCDV</div>
<div style="float: left"><s:property value="trans.CPCODE"/></div>
<div style="clear: both" />
<div style="float: left; width: 200px">Tên NCCDV</div>
<div style="float: left"><s:property value="trans.CPNAME"/></div>
<div style="clear: both" />
<div style="float: left; width: 200px">Mã giao dịch</div>
<div style="float: left"><s:property value="trans.TRANSID"/></div>

<div style="clear: both" />
<div style="float: left; width: 200px">Mã giao dịch NCCDV</div>
<div style="float: left"><s:property value="trans.ORDERID"/></div>

<div style="clear: both" />
<div style="float: left; width: 200px">Mã thanh toán</div>
<div style="float: left"><s:property value="trans.BILLINGCODE"/></div>

<div style="clear: both" />
<div style="float: left; width: 200px">Số tiền</div>
<div style="float: left"><s:property value="getText('{0,number,#,##0}',{trans.AMOUNT})"/> VND</div>

<div style="clear: both" />
<div style="float: left; width: 200px">Số tiền hoàn</div>
<div style="float: left"><s:property value="getText('{0,number,#,##0}',{trans.AMOUNT})"/>  VND</div>

<div style="clear: both" />
<div style="float: left; width: 200px">Thời gian giao dịch</div>
<div style="float: left"><s:property value="trans.REQUESTDATE"/></div>

<div style="clear: both" />
<div style="float: left; width: 200px">Thông tin thanh toán</div>
<div style="float: left"><s:property value="trans.ORDERINFO"/></div>
<div style="clear: both" />
<br/>
<h3>Chi tiết khách hàng</h3>
<div>Số thuê bao: <s:property value="trans.MSISDN.substring(0, 3)"/> _____ <s:property value="trans.MSISDN.substring(trans.MSISDN.length()-3, trans.MSISDN.length())" /></div>
<div>Chủ thuê bao: <s:property value="trans.CUSTOMERNAME"/></div>

<%
    BaseAction base = new BaseAction();
%>
<%
    if (base.getUserRole().equals(base.ROLE_ADMIN)) {
%>
<s:if test="%{trans.TRANSTYPE==0}">
    <s:if test="%{trans.TRANSSTATUS==2 || trans.TRANSSTATUS==1}">

        <h3>Tác động hoàn tiền</h3>
        <form id="refundForm" name="refundForm"  novalidate="novalidate">
            <div data-role="fieldcontain">
                <label for="amount" style="font-size: 13px !important">Số tiền hoàn (VND)</label>
                <input id="amount" type="text" value="<s:property value="trans.AMOUNT"/>" name="amount" disabled="disabled" data-mini="true" />
                <input id="transId" type="hidden" value="<s:property value="trans.TRANSID"/>" name="transId" />
                <input data-mini="true" data-inline="true" type="submit" id="btnRefund" name="btnRefund" value="Hoàn tiền" />
            </div>
            <div data-role="fieldcontain">
                <label for="note" style="font-size: 13px !important">Lý do</label>
                <textarea name="note" id="note" maxlength="1000"></textarea>
            </div>
        </form>
    </s:if>

    <s:if test="%{trans.TRANSSTATUS==1}">
        <h3>Xác nhận trạng thái</h3>
        <form id="confirmStatusForm" name="confirmStatusForm"  novalidate="novalidate">
            <div data-role="fieldcontain">
                <label for="confirmStatus" style="font-size: 13px !important">Trạng thái giao dịch</label>
                <select id="confirmStatus" name="confirmStatus" data-mini="true" data-inline="true" data-iconpos="noicon">
                    <option value="2" selected="selected">--- Thành công ---</option>
                    <option value="4">--- Thất bại ---</option>
                </select>
                <input id="transId" type="hidden" value="<s:property value="trans.TRANSID"/>" name="transId" />
                <input data-mini="true" data-inline="true" type="submit" id="btnConfirmStatus" name="btnConfirmStatus" value="Xác nhận" />
            </div>
        </form>
    </s:if>
</s:if>
<%    }
%>
<h3>Lịch sử</h3>
<div id="listTransaction" class="listTransaction">
    <table style="color: #00000; border-collapse: collapse; border: 1px solid #C5C5C5; margin-left: 10px; border-spacing: 5px;">
        <tr style="background-color: #E8E8E8">
            <td style="width: 100px">Loại GD</td>
            <td style="width: 150px">Mã GD</td>
            <td style="width: 150px">Mã GD gốc</td>
            <td style="width: 80px"> Số tiền (VND)</td>
            <td style="width: 200px">Ngày GD</td>
            <td style="width: 100px">Trạng thái</td>
        </tr>
        <s:iterator value="listTransHis" var="item" >
            <tr>
                <td>
                    <s:if test="%{#item.transType==0}">
                        Thanh toán
                    </s:if>
                    <s:elseif test="%{#item.transType==1}">
                        Truy vấn
                    </s:elseif>
                    <s:elseif test="%{#item.transType==2}">
                        Hoàn tiền
                    </s:elseif>
                </td>
                <td><s:property value="#item.transId" /></td>
                <td><s:property value="#item.originalTransId" /></td>
                <td style="text-align: right"><s:property value="getText('{0,number,#,##0}',{#item.amount})" /></td>
                <td><s:date name="#item.requestDate" format="dd/MM/yyyy HH:mm:ss" /></td>
                <td>
                    <s:if test="%{#item.transStatus==0}">
                        Chưa xử lý
                    </s:if>
                    <s:elseif test="%{#item.transStatus==1}">
                        Đang xử lý
                    </s:elseif>
                    <s:elseif test="%{#item.transStatus==2}">
                        Thành công
                    </s:elseif>
                    <s:elseif test="%{#item.transStatus==3}">
                        Đã hủy
                    </s:elseif>
                    <s:elseif test="%{#item.transStatus==4}">
                        Thất bại
                    </s:elseif>
                </td>
            </tr>
        </s:iterator>
    </table>
</div>
<br/>
<br/>
<br/>
<script>
    //Khoi tao validate rule
    (function($, W, D)
    {
        var MERCHANTGW = {};

        MERCHANTGW.TRANSACTIONDETAIL =
            {
            setupFormValidation: function()
            {
                //form validation rules
                $("#confirmStatusForm").validate({
                    submitHandler: function(form) {
                        MERCHANTGW.TRANSACTIONDETAIL.DoConfirm();
                    }
                });
                $("#refundForm").validate({
                    rules: {
                        'note': {
                            required: true,
                            maxlength: 1000
                        }
                    },
                    messages: {
                        'note': {
                            required: "Thông tin bắt buộc",
                            maxlength: "Độ dài không đúng quy định"
                        }
                    },
                    submitHandler: function(form) {
                        MERCHANTGW.TRANSACTIONDETAIL.DoRefund();
                    }
                });
            },
            
            onSuccess: function(data, status)
            {
                if (typeof(data) === "string") {
                    location.href="Main.do";
                    return;
                }
                var customInfo = data.customInfo;
                if (customInfo[0] == 'success') {
                    var errMsg = 'Xác nhận giao dịch thành công';
                    $('#btnConfirmStatus').button('disable');
                    alert(errMsg);
                } else {
                    var errMsg = 'Lỗi, không thể thực hiện xác nhận giao dịch';
                    if (customInfo[1] != '') {
                        errMsg = customInfo[1];
                    }
                    alert(errMsg);
                }
                hideModal();
            },
            onRefundSuccess: function(data, status)
            {
                var customInfo = data.customInfo;
                if (customInfo[0] == 'success') {
                    var errMsg = 'Thực hiện yêu cầu hoàn tiền thành công';
                    $('#btnRefund').button('disable');
                    alert(errMsg);
                } else {
                    var errMsg = 'Lỗi, không thể thực hiện hoàn tiền giao dịch';
                    if (customInfo[1] != '') {
                        errMsg = customInfo[1];
                    }
                    alert(errMsg);
                }
                hideModal();
            },

            onError: function(data, status)
            {
                hideModal();
            },

            DoConfirm: function() {
                if(confirm('Bạn có đồng ý thực hiện xác nhận giao dịch?')){
                    showModal();
                    var tokenParam = token.getTokenParam();
                    var formData = $("#confirmStatusForm").serialize() + '&' + $.param(tokenParam);
                    $.ajax({
                        type: "POST",
                        url: "Transaction!doConfirm.do?",
                        cache: false,
                        data: formData,
                        success: MERCHANTGW.TRANSACTIONDETAIL.onSuccess,
                        error: MERCHANTGW.TRANSACTIONDETAIL.onError
                    });
                }
                return false;
            },
            
            DoRefund: function() {
                if(confirm('Bạn có đồng ý thực hiện yêu cầu hoàn tiền giao dịch?')){
                    showModal();
                    var tokenParam = token.getTokenParam();
                    var formData = $("#refundForm").serialize() + '&' + $.param(tokenParam);
                    $.ajax({
                        type: "POST",
                        url: "Transaction!doRefund.do?",
                        cache: false,
                        data: formData,
                        success: MERCHANTGW.TRANSACTIONDETAIL.onRefundSuccess,
                        error: MERCHANTGW.TRANSACTIONDETAIL.onError
                    });
                }
                return false;
            }
            
        }

        //when the dom has loaded setup form validation rules
        $(D).ready(function($) {
            MERCHANTGW.TRANSACTIONDETAIL.setupFormValidation();
        });
    })(jQuery, window, document);
</script>