<%--
    Document   : transfermerchant.jsp
    Created on : Jul 22, 2015, 9:28:20 AM
    Author     : CuongDV3
--%>

<%@page import="com.viettel.common.util.ResourceBundleUtil"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sd" uri="struts-dojo-tags" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script>
    $.validator.addMethod(
            "greaterThan",
            function(value, element, params) {
                var from = document.getElementById("transferForm.fromDate").value.trim();
                var to = document.getElementById("transferForm.toDate").value.trim();
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
                var from = document.getElementById("transferForm.fromDate").value.trim();
                var to = document.getElementById("transferForm.toDate").value.trim();
                var vfrom = from.split("/");
                var vTo = to.split("/");
                if (!/Invalid|NaN/.test(new Date(vfrom[2], vfrom[1], vfrom[0])) && !/Invalid|NaN/.test(new Date(vTo[2], vTo[1], vTo[0]))) {
                    return new Date(vfrom[2], vfrom[1], vfrom[0]) <= new Date(vTo[2], vTo[1], vTo[0]);
                }
                return true;
            }, 'Phải nhỏ hơn đến ngày.');

    $.validator.addMethod(
            "regex",
            function(value, element, regexp) {
                var re = new RegExp(regexp);
                return this.optional(element) || re.test(value);
            },
            "Chỉ được phép nhập chữ hoặc số"
            );
    $.validator.addMethod(
            "specialChar",
            function(value, element, regexp) {
                var sc = "`~!#$%^&*()_-+{[]}|\/.,<>,;\"";
                if (value != null) {
                    for (i = 0; i < sc.length; i++) {
                        if (value.indexOf(sc[i]) > -1) {
                            return false;
                        }
                    }
                }
                return true;
            },
            "Không được nhập các ký tự đặc biệt."
            );
    $.validator.addMethod(
            "vnDate",
            function(value, element, regexp) {
                var pattern = /^([0-9]{2})\/([0-9]{2})\/([0-9]{4})$/;
                var re = new RegExp(pattern);
                return this.optional(element) || re.test(value);
            },
            "Ngày không đúng định dạng"
            );
    //Khoi tao validate rule
    (function($, W, D)
    {
        var MERCHANTGW = {};

        MERCHANTGW.TRANSFER =
                {
                    setupFormValidation: function()
                    {
                        //form validation rules
                        $("#transferForm").validate({
                            rules: {
                                'transactionForm.fromDate': {
                                    required: true,
                                    vnDate: true,
                                    smallerThan: true
                                },
                                'transactionForm.toDate': {
                                    required: true,
                                    vnDate: true,
                                    greaterThan: '#transferForm.fromDate'
                                },
                                'transactionForm.transId': {
                                    maxlength: 20,
                                    regex: "^[A-Za-z0-9]+$"
                                },
                                'transactionForm.orderId': {
                                    maxlength: 50,
                                    regex: "^[A-Za-z0-9]+$"
                                },
                                'transactionForm.billingCode': {
                                    maxlength: 30,
                                    regex: "^[A-Za-z0-9]+$"
                                }
                            },
                            messages: {
                                'transactionForm.fromDate': {
                                    required: "<sd:Property>ERROR_INFO_MISSING</sd:Property>"
                                                                    },
                                'transactionForm.toDate': {
                                    required: "<sd:Property>ERROR_INFO_MISSING</sd:Property>"
                                                                    },
                                'transactionForm.transId': {
                                    maxlength: "Độ dài không đúng quy định"
                                },
                                'transactionForm.orderId': {
                                    maxlength: "Độ dài không đúng quy định"
                                },
                                'transactionForm.billingCode': {
                                    maxlength: "Độ dài không đúng quy định"
                                }
                            },
                            submitHandler: function(form) {
                                MERCHANTGW.TRANSFER.DoSearch();
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
                                $("#errorMessage").html(errMsg);
                                $("#errorMessage").show("fast");
                            }
                            hideModal();
                    },
                    onError: function(data, status)
                    {
                        hideModal();
                    },
                    DoSearch: function() {
                        showModal();
                        $("#errorMessage").html('');
                        $("#errorMessage").hide();

                        var tokenParam = token.getTokenParam();
                        var formData = $("#transferForm").serialize() + '&' + $.param(tokenParam);
                        $.ajax({
                            type: "POST",
                            url: "Transaction!searchTransfer.do?",
                            cache: false,
                            data: formData,
                            success: MERCHANTGW.TRANSFER.onSearchSuccess,
                            error: MERCHANTGW.TRANSFER.onError
                        });
                        return false
                    }
                }

            //when the dom has loaded setup form validation rules
            $(D).ready(function($) {
                MERCHANTGW.TRANSFER.setupFormValidation();
                setDefaultDate();
            });
    })(jQuery, window, document);
    onError = function(data, status)
    {
        hideModal();
    }
    setDefaultDate = function() {
        var from = document.getElementById("transferForm.fromDate");
        var to = document.getElementById("transferForm.toDate");
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

        $("#transferResult").jqxGrid(
                {
                    source: dataAdapter,
                    pageable: true,
                    autoheight: true,
                    columnsresize: true,
                    pagesize: 20,
                    width: 940,
                    localization: getLocalization(),

                    columns: [
                        {text: 'STT', datafield: 'requestId', width: 30, cellsrenderer: rowIndex},
                        {text: 'Ngày thanh toán', datafield: 'payDate', width: 100, cellsrenderer: getSubDate},
                        {text: 'Mã NCCDV', datafield: 'cpCode', width: 80, cellsrenderer: formatHTML},
                        {text: 'Tên NCCDV', datafield: 'cpName', width: 150, cellsrenderer: formatHTML},
                        {text: 'Số tài khoản', datafield: 'bankAccount', width: 100, cellsrenderer: formatHTML},
                        {text: 'Ngân hàng', datafield: 'bankCode', width: 80, cellsrenderer: formatHTML},
                        {text: 'Chi nhành', datafield: 'bankBranch', width: 80, cellsrenderer: formatHTML},
                        {text: 'Số tiền sau đối soát(VND)', datafield: 'amount', width: 100, cellsalign: 'right', cellsformat: 'n'},
                        {text: 'Số tiền chuyển(VND)', datafield: 'payAmount', width: 100, cellsalign: 'right', cellsformat: 'n'},
                        {text: 'Ngày chuyển', datafield: 'requestDate', width: 100, cellsrenderer: getSubDate},
                        {text: 'Trạng thái', datafield: 'importDate', width: 90, cellsrenderer: getstatus}
                    ]
                });
    }

    formatHTML = function(row, columnfield, value, defaulthtml, columnproperties) {
        value = escapeHtml(value);
        return value;
    }
    rowIndex = function(row, columnfield, value, defaulthtml, columnproperties) {
        var num = row + 1;
        value = num;
        return value;
    }
    getstatus = function(row, columnfield, value, defaulthtml, columnproperties) {
        value = 'Thành công';
        return value;
    }
    getId = function(row, columnfield, value, defaulthtml, columnproperties) {
        return '<a href="javascript:void(0);" onclick="return viewDetail(\'' + value + '\');">' + value + '</a>';
    }
    getSubDate = function(row, columnfield, value, defaulthtml, columnproperties) {
        value = escapeHtml(value);
        value = value.substring(0,10);
        return value;
    }

</script>

<form id="transferForm" name="transferForm"  novalidate="novalidate">
    <h3>Tra cứu giao dịch chuyển tiền</h3>
    <div id="errorMessage" style="display: none; " class="errorMessage"> </div>
    <table border="0" style="width: 100%; border-collapse: collapse; margin: 0; padding: 0">
        <tr style="height: 10px">
            <td style="height: 10px">
                <div data-role="fieldcontain">
                    <label for="transferForm.fromDate" ><sd:Property>transactionForm.fromDate</sd:Property></label>
                        <input id="transferForm.fromDate" type="text" value="" name="transferForm.fromDate" data-role="date" data-mini="true" />
                    </div>
                </td>
                <td style="height: 10px">
                    <div data-role="fieldcontain">
                        <label for="transferForm.toDate" ><sd:Property>transactionForm.toDate</sd:Property></label>
                        <input id="transferForm.toDate" type="text" value="" name="transferForm.toDate" data-role="date" data-mini="true" />
                    </div>
                </td>
            </tr>

            <tr>

                <td>
                    <div data-role="fieldcontain">
                        <label for="transferForm.cpCode" >Chọn CP</label>
                        <select id="transferForm.contentProviderId" name="transferForm.contentProviderId" data-mini="true" data-inline="true" data-iconpos="noicon">
                            <option value="-1"><sd:Property>transactionForm.selectAll</sd:Property> </option>
                        <s:iterator value="listContentProvider" var="item" >
                            <option value='<s:property value="#item.CONTENT_PROVIDER_ID" />'><s:property value="#item.CP_NAME" /></option>
                        </s:iterator>
                    </select>
                </div>
            </td>
            <td>
                <div data-role="fieldcontain">
                    <label for="transferForm.transStatus"><sd:Property>transactionForm.transStatus</sd:Property></label>
                        <select id="transferForm.transStatus" name="transferForm.transStatus" data-mini="true" data-inline="true" data-iconpos="noicon">
                            <option value="2"><sd:Property>transactionForm.transStatus.2</sd:Property> </option>
                        </select>
                    </div>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input data-mini="true" data-inline="true" type="submit" id="btnSearch" name="btnSearch" value="<sd:Property>transactionForm.btnSearch</sd:Property>" />
            </td>
        </tr>
    </table>
</form>
<BR/>
<h3>Kết quả giao dịch</h3>
<div id="transferResult" name="transferResult" style="overflow-y: auto"></div> 
