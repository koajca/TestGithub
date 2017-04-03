<%-- 
    Document   : reportEVNNPCjsp
    Created on : Dec 31, 2014, 11:43:42 AM
    Author     : longth1
--%>

<%@page import="com.viettel.common.util.ResourceBundleUtil"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sd" uri="struts-dojo-tags" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script>
    $.validator.addMethod(
            "vnDate",
            function(value, element, regexp) {
                var pattern = /^([0-9]{2})\/([0-9]{2})\/([0-9]{4})$/;
                var re = new RegExp(pattern);
                return this.optional(element) || re.test(value);
            },
            "Ngày không đúng định dạng"
            );
    $.validator.addMethod(
            "greaterThan",
            function(value, element, params) {
                var from = document.getElementById("fromDate").value.trim();
                var to = document.getElementById("toDate").value.trim();
                var vfrom = from.split("/");
                var vTo = to.split("/");
                if (!/Invalid|NaN/.test(new Date(vfrom[2], vfrom[1], vfrom[0])) && !/Invalid|NaN/.test(new Date(vTo[2], vTo[1], vTo[0]))) {
                    return new Date(vfrom[2], vfrom[1], vfrom[0]) <= new Date(vTo[2], vTo[1], vTo[0]);
                }
                return true;
            }, 'Phải lớn hơn từ ngày.');

    $.validator.addMethod(
            "smallerThan",
            function(value, element, params) {
                var from = document.getElementById("fromDate").value.trim();
                var to = document.getElementById("toDate").value.trim();
                var vfrom = from.split("/");
                var vTo = to.split("/");
                if (!/Invalid|NaN/.test(new Date(vfrom[2], vfrom[1], vfrom[0])) && !/Invalid|NaN/.test(new Date(vTo[2], vTo[1], vTo[0]))) {
                    return new Date(vfrom[2], vfrom[1], vfrom[0]) <= new Date(vTo[2], vTo[1], vTo[0]);
                }
                return true;
            }, 'Phải nhỏ hơn đến ngày.');
    //Khoi tao validate rule
    (function($, W, D)
    {
        var MERCHANTGW = {};

        MERCHANTGW.REPORTEVNNPC =
                {
                    setupFormValidation: function()
                    {
                        //form validation rules
                        $("#reportEVNNPCForm").validate({
                            rules: {
                                'fromDate': {
                                    required: true,
                                    vnDate: true,
                                    smallerThan: true
                                },
                                'toDate': {
                                    required: true,
                                    vnDate: true,
                                    greaterThan: true
                                }

                            },
                            messages: {
                                'fromDate': {
                                    required: "<sd:Property>ERROR_INFO_MISSING</sd:Property>",
                                                                        vnDate: "Ngày không đúng định dạng"
                                                                    },
                                                                    'toDate': {
                                                                        required: "<sd:Property>ERROR_INFO_MISSING</sd:Property>",
                                                                                                            vnDate: "Ngày không đúng định dạng"
                                                                                                        }
                                                                                                    },
                                                                                                    submitHandler: function(form) {
                                                                                                        MERCHANTGW.REPORTEVNNPC.DoSearch();
                                                                                                    }
                                                                                                });
                                                                                            },
                                                                                            onSearchSuccess: function(data, status)
                                                                                            {
                                                                                                if (typeof (data) === "string") {
                                                                                                    location.href = "Main.do";
                                                                                                    return;
                                                                                                }
                                                                                                var customInfo = data.customInfo;
                                                                                                if (customInfo[0] == 'success') {
                                                                                                    bindData(data.items);
                                                                                                } else {
                                                                                                    var errMsg = "<sd:Property>ERROR_SEARCH_TRANSACTION</sd:Property>";
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
                                                                                                                        DoSearch: function() {
                                                                                                                            showModal();

                                                                                                                            var tokenParam = token.getTokenParam();
                                                                                                                            var formData = $("#reportEVNNPCForm").serialize() + '&' + $.param(tokenParam);
                                                                                                                            $.ajax({
                                                                                                                                type: "POST",
                                                                                                                                url: "Report!onReprotEVNNPC.do?",
                                                                                                                                cache: false,
                                                                                                                                data: formData,
                                                                                                                                success: MERCHANTGW.REPORTEVNNPC.onSearchSuccess,
                                                                                                                                error: MERCHANTGW.REPORTEVNNPC.onError
                                                                                                                            });
                                                                                                                            return false
                                                                                                                        }

                                                                                                                    }

                                                                                                            //when the dom has loaded setup form validation rules
                                                                                                            $(D).ready(function($) {
                                                                                                                MERCHANTGW.REPORTEVNNPC.setupFormValidation();
                                                                                                                setDefaultDate();
                                                                                                            });


                                                                                                        })(jQuery, window, document);
                                                                                                        onError = function(data, status)
                                                                                                        {
                                                                                                            hideModal();
                                                                                                        }
                                                                                                        bindData = function(data) {
                                                                                                            var source =
                                                                                                                    {
                                                                                                                        localdata: data,
                                                                                                                        datatype: "json"
                                                                                                                    };
                                                                                                            var dataAdapter = new $.jqx.dataAdapter(source, {
                                                                                                                loadComplete: function(data) {
                                                                                                                },
                                                                                                                loadError: function(xhr, status, error) {
                                                                                                                }
                                                                                                            });

                                                                                                            var getLocalization = function() {
                                                                                                                var localizationobj = {};
                                                                                                                localizationobj.pagergotopagestring = "Đến trang:";
                                                                                                                localizationobj.pagershowrowsstring = "Số dòng mỗi trang:";
                                                                                                                localizationobj.pagerrangestring = " của ";
                                                                                                                localizationobj.pagernextbuttonstring = "Trang sau";
                                                                                                                localizationobj.pagerpreviousbuttonstring = "Trang trước";
                                                                                                                localizationobj.firstDay = 1;
                                                                                                                localizationobj.percentsymbol = "%";
                                                                                                                localizationobj.currencysymbol = "VND";
                                                                                                                localizationobj.currencysymbolposition = "after";
                                                                                                                localizationobj.thousandsseparator = ".";
                                                                                                                localizationobj.emptydatastring = "Không có dữ liệu";
                                                                                                                return localizationobj;
                                                                                                            }

                                                                                                            $("#transactionResult").jqxGrid(
                                                                                                                    {
                                                                                                                        source: dataAdapter,
//            pageable: true,
                                                                                                                        autoheight: true,
                                                                                                                        columnsheight: 50,
                                                                                                                        columnsresize: true,
//            pagesize: 20,
                                                                                                                        width: 980,
                                                                                                                        localization: getLocalization(),
                                                                                                                        columns: [
                                                                                                                            {text: 'STT', datafield: 'NULL', width: 40, cellsrenderer: rowIndex},
                                                                                                                            {text: 'Ngày GD', datafield: 'REQDATE', width: 80, cellsrenderer: formatHTML},
                                                                                                                            {text: 'Số hóa đơn', datafield: 'ORDERID', width: 100, cellsrenderer: formatHTML},
                                                                                                                            {text: 'Ma Kh EVN', datafield: 'BILLINGCODE', width: 100, cellsformat: 'n'},
                                                                                                                            {text: 'Ten KH', datafield: 'CUSTOMERNAME', width: 100, cellsformat: 'n'},
                                                                                                                            {text: 'Địa chỉ KH', datafield: 'CUSTADDRESS', width: 100, cellsformat: 'n'},
                                                                                                                            {text: 'Sổ quyển', datafield: 'NUMBERGCS', width: 100, cellsalign: 'right', cellsformat: 'n'},
                                                                                                                            {text: 'Số tiền', datafield: 'AMOUNT', width: 100, cellsalign: 'right', cellsformat: 'n'},
                                                                                                                            {text: 'MãCN', datafield: 'MACN', width: 100, cellsformat: 'n'},
                                                                                                                            {text: 'Mã GD<br/>Bankplus', datafield: 'TRANSID', width: 100, cellsformat: 'n'},
                                                                                                                            {text: 'Số điện thoại </br>thanh toán', datafield: 'MSISDN', width: 100, cellsformat: 'n'},
                                                                                                                            {text: 'Ngân hàng', datafield: 'BANKCODE', width: 100, cellsalign: 'right', cellsformat: 'n'},
                                                                                                                            {text: 'Trạng thái', datafield: 'CPTRANSTATUS', width: 100, cellsalign: 'center', cellsrenderer: statusDesc, cellsformat: 'c2'}
                                                                                                                        ]
                                                                                                                    });
                                                                                                        }
                                                                                                        rowIndex = function(row, columnfield, value, defaulthtml, columnproperties) {
                                                                                                            var num = row + 1;
                                                                                                            value = num;
                                                                                                            return value;
                                                                                                        }
                                                                                                        setDefaultDate = function() {
                                                                                                            var from = document.getElementById("fromDate");
                                                                                                            var to = document.getElementById("toDate");
                                                                                                            var now = new Date();
                                                                                                            var mon = now.getMonth() + 1;
                                                                                                            var day = now.getDate();
                                                                                                            if (day.toString().length == 1)
                                                                                                                day = '0' + day;
                                                                                                            if (mon.toString().length == 1)
                                                                                                                mon = '0' + mon;
                                                                                                            var fromText = '01/' + mon + '/' + now.getFullYear();
                                                                                                            var toText = day + '/' + mon + '/' + now.getFullYear();
                                                                                                            from.value = fromText;
                                                                                                            to.value = toText;
                                                                                                        }
                                                                                                        statusDesc = function(row, columnfield, value, defaulthtml, columnproperties) {

                                                                                                            if (value == '0') {
                                                                                                                return 'Chưa xử lý'
                                                                                                            }
                                                                                                            if (value == '1') {
                                                                                                                return 'Đang xử lý'
                                                                                                            }
                                                                                                            if (value == '2') {
                                                                                                                return 'Hoàn thành'
                                                                                                            }
                                                                                                            if (value == '3') {
                                                                                                                return 'Đã hủy'
                                                                                                            }
                                                                                                            if (value == '4') {
                                                                                                                return 'Thất bại'
                                                                                                            }

                                                                                                        }
                                                                                                        formatHTML = function(row, columnfield, value, defaulthtml, columnproperties) {
                                                                                                            value = escapeHtml(value);
                                                                                                            return value;
                                                                                                        }

    </script>

    <form id="reportEVNNPCForm" name="reportEVNNPCForm"  novalidate="novalidate">
        <h3>Báo cáo đối soát điện lực</h3>
        <div id="errorMessage" style="display: none; " class="errorMessage"> </div>
        <table border="0" style="width: 100%; border-collapse: collapse; margin: 0; padding: 0">
            <tr style="height: 10px">
                <td style="height: 10px">
                    <div data-role="fieldcontain">
                        <label for="fromDate" ><sd:Property>transactionForm.fromDate</sd:Property></label>
                        <input id="fromDate" type="text" value="" name="fromDate" data-role="date" data-mini="true" />
                    </div>
                </td>
                <td style="height: 10px">
                    <div data-role="fieldcontain">
                        <label for="toDate" ><sd:Property>transactionForm.toDate</sd:Property></label>
                        <input id="toDate" type="text" value="" name="toDate" data-role="date" data-mini="true" />
                    </div>
                </td>
            </tr>
            <tr>
                <td >
                    <div data-role="fieldcontain">
                        <label for="status" ><sd:Property>transactionForm.status</sd:Property></label>
                        <select id="status" name="status" data-mini="true" data-inline="true" data-iconpos="noicon">
                            <option value="NONE"><sd:Property>transactionForm.selectAll</sd:Property> </option>
                            <option value="0">Chưa xử lý</option>
                            <option value="1">Đang xử lý</option>
                            <option value="2">Hoàn thành</option>
                            <option value="3">Đã hủy</option>
                            <option value="4">Thất bại</option>
                        </select>
                    </div>
                </td>
                <td>
                    <div data-role="fieldcontain">
                        <label for="contentProviderId" >Chọn CP</label>
                        <select id="contentProviderId" name="contentProviderId" data-mini="true" data-inline="true" data-iconpos="noicon">
                            <option value="-1"><sd:Property>transactionForm.selectAll</sd:Property> </option>
                        <s:iterator value="listContentProvider" var="item" >
                            <option value='<s:property value="#item.CONTENT_PROVIDER_ID" />'><s:property value="#item.CP_NAME" /></option>
                        </s:iterator>
                    </select>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input data-mini="true" data-inline="true" type="submit" id="btnSearch" name="btnSearch" value="<sd:Property>btnReport</sd:Property>" />
                <input data-mini="true" data-inline="true" type="button" id="btnExcel" name="btnExcel" value="<sd:Property>transactionForm.btnExcel</sd:Property>" onclick="exportExcel('REPORT_EVNNPC');" />
            </td>
        </tr>
    </table>
</form>
<BR/>
<h3>Kết quả giao dịch</h3>
<div id="transactionResult" name="transactionResult"></div> 
