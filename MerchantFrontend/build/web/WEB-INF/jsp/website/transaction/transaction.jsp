<%--
    Document   : transaction.jsp
    Created on : Mar 11, 2014, 2:00:20 PM
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
                var from = document.getElementById("transactionForm.fromDate").value.trim();
                var to = document.getElementById("transactionForm.toDate").value.trim();
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
                var from = document.getElementById("transactionForm.fromDate").value.trim();
                var to = document.getElementById("transactionForm.toDate").value.trim();
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

        MERCHANTGW.TRANSACTION =
                {
                    setupFormValidation: function()
                    {
                        //form validation rules
                        $("#transactionForm").validate({
                            rules: {
                                'transactionForm.fromDate': {
                                    required: true,
                                    vnDate: true,
                                    smallerThan: true
                                },
                                'transactionForm.toDate': {
                                    required: true,
                                    vnDate: true,
                                    greaterThan: '#transactionForm.fromDate'
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
                MERCHANTGW.TRANSACTION.DoSearch();
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
            var formData = $("#transactionForm").serialize() + '&' + $.param(tokenParam);
            $.ajax({
                type: "POST",
                url: "Transaction!onSearch.do?",
                cache: false,
                data: formData,
                success: MERCHANTGW.TRANSACTION.onSearchSuccess,
                error: MERCHANTGW.TRANSACTION.onError
            });
            return false
        }

    }

    //when the dom has loaded setup form validation rules
    $(D).ready(function($) {
        MERCHANTGW.TRANSACTION.setupFormValidation();
        createElements();
        setDefaultDate();
    });


    })(jQuery, window, document);
                onError = function(data, status)
                {
                    hideModal();
                }
                setDefaultDate = function() {
                    var from = document.getElementById("transactionForm.fromDate");
                    var to = document.getElementById("transactionForm.toDate");
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

                    $("#transactionResult").jqxGrid(
                            {
                                source: dataAdapter,
                                pageable: true,
                                autoheight: true,
                                columnsresize: true,
                                pagesize: 20,
                                width: 1200,
                                localization: getLocalization(),
                                columns: [
                                    {text: 'STT', datafield: 'TEXTTRANSSTATUS', width: 30, cellsrenderer: rowIndex},
                                    {text: 'Ngân hàng', datafield: 'BANKCODE', width: 80, cellsrenderer: formatHTML},
                                    {text: 'Mã NCCDV', datafield: 'CPCODE', width: 80, cellsrenderer: formatHTML},
                                    {text: 'Tên NCCDV', datafield: 'CPNAME', width: 150, cellsrenderer: formatHTML},
                                    {text: 'Mã giao dịch', datafield: 'TRANSID', width: 150, cellsrenderer: getId},
                                    {text: 'Mã GD NCCDV', datafield: 'ORDERID', width: 80, cellsrenderer: formatHTML},
                                    {text: 'Mã khách hàng', datafield: 'BILLINGCODE', width: 100, cellsrenderer: formatHTML},
                                    {text: 'Số tiền (VND)', datafield: 'AMOUNT', width: 100, cellsalign: 'right', cellsformat: 'n'},
                                    {text: 'Ngày giao dịch', datafield: 'REQUESTDATE', width: 150, cellsrenderer: formatHTML},
                                    {text: 'Thông tin giao dịch', datafield: 'ORDERINFO', width: 150, cellsrenderer: formatHTML},
                                    {text: 'Trạng thái', datafield: 'TRANSSTATUS', width: 90, cellsrenderer: getstatus},
                                    {text: 'Trạng thái xác nhận', datafield: 'CONFIRMSTATUS', width: 90, cellsrenderer: getstatus}
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
                    if (value == '0') {
                        value = 'Chưa xử lý';
                    } else if (value == '1') {
                        value = 'Đang xử lý';
                    } else if (value == '2') {
                        value = 'Thành công';
                    } else if (value == '3') {
                        value = 'Đã hủy';
                    } else if (value == '4') {
                        value = 'Thất bại';
                    } else {
                        value = '';
                    }
                    return value;
                }
                getId = function(row, columnfield, value, defaulthtml, columnproperties) {
                    return '<a href="javascript:void(0);" onclick="return viewDetail(\'' + value + '\');">' + value + '</a>';
                }

                viewDetail = function(transId) {
                    showModal();
                    var url = '${contextPath}' + '/Transaction!viewDetail.do?transId=' + transId;
                    $("#transContent").load(url, function() {
                        $(this).trigger("create");
                    });
                    $('#transactionDetail').jqxWindow('open');
                    hideModal();
                    return false;
                }

                function createElements() {
                    $('#transactionDetail').jqxWindow({height: 600, width: '100%',
                        resizable: false, draggable: false, isModal: true, modalOpacity: 0.3, autoOpen: false
                    });
                }
</script>

<form id="transactionForm" name="transactionForm"  novalidate="novalidate">
    <h3><sd:Property>transaction_title</sd:Property></h3>
        <div id="errorMessage" style="display: none; " class="errorMessage"> </div>
        <table border="0" style="width: 80%; border-collapse: collapse; margin: 0; padding: 0">
            <tr style="height: 10px">
                <td style="height: 10px">
                    <div data-role="fieldcontain">
                        <label for="transactionForm.fromDate" ><sd:Property>transactionForm.fromDate</sd:Property></label>
                        <input id="transactionForm.fromDate" type="text" value="" name="transactionForm.fromDate" data-role="date" data-mini="true" />
                    </div>
                </td>
                <td style="height: 10px">
                    <div data-role="fieldcontain">
                        <label for="transactionForm.toDate" ><sd:Property>transactionForm.toDate</sd:Property></label>
                        <input id="transactionForm.toDate" type="text" value="" name="transactionForm.toDate" data-role="date" data-mini="true" />
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <div data-role="fieldcontain">
                        <label for="transactionForm.transId"  ><sd:Property>transactionForm.transId</sd:Property></label>
                        <input id="transactionForm.transId" type="text" value="" name="transactionForm.transId" data-mini="true" data-inline="true" maxlength="20" />
                    </div>
                </td>
                <td>
                    <div data-role="fieldcontain">
                        <label for="transactionForm.orderId" style="white-space: nowrap; width: 150px"><sd:Property>transactionForm.orderId</sd:Property></label>
                        <input id="transactionForm.orderId" type="text" value="" name="transactionForm.orderId" data-mini="true" data-inline="true" maxlength="50"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <div data-role="fieldcontain">
                        <label for="transactionForm.billingCode"  ><sd:Property>transactionForm.billingCode</sd:Property></label>
                        <input id="transactionForm.billingCode" type="text" value="" name="transactionForm.billingCode" data-mini="true" data-inline="true" maxlength="30" />
                    </div>
                </td>
                <td>
                    <div data-role="fieldcontain">
                        <label for="transactionForm.cpCode" >Chọn CP</label>
                        <select id="transactionForm.contentProviderId" name="transactionForm.contentProviderId" data-mini="true" data-inline="true" data-iconpos="noicon">
                            <option value="-1"><sd:Property>transactionForm.selectAll</sd:Property> </option>
                        <s:iterator value="listContentProvider" var="item" >
                            <option value='<s:property value="#item.CONTENT_PROVIDER_ID" />'><s:property value="#item.CP_NAME" /></option>
                        </s:iterator>
                    </select>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div data-role="fieldcontain">
                    <label for="transactionForm.bankCode" ><sd:Property>transactionForm.bankCode</sd:Property></label>
                        <select id="transactionForm.bankCode" name="transactionForm.bankCode" data-mini="true" data-inline="true" data-iconpos="noicon">
                            <option value="NONE"><sd:Property>transactionForm.selectAll</sd:Property> </option>
                            <option value="MB">MB</option>
                            <option value="VCB">VCB</option>
                            <option value="BIDV">BIDV</option>
                        </select>
                    </div>
                </td>
                <td>
                    <div data-role="fieldcontain">
                        <label for="transactionForm.transStatus"><sd:Property>transactionForm.transStatus</sd:Property></label>
                        <select id="transactionForm.transStatus" name="transactionForm.transStatus" data-mini="true" data-inline="true" data-iconpos="noicon">
                            <option value="NONE"><sd:Property>transactionForm.selectAll</sd:Property> </option>
                        <option value="0"><sd:Property>transactionForm.transStatus.0</sd:Property> </option>
                        <option value="1"><sd:Property>transactionForm.transStatus.1</sd:Property> </option>
                        <option value="2"><sd:Property>transactionForm.transStatus.2</sd:Property> </option>
                        <option value="3"><sd:Property>transactionForm.transStatus.3</sd:Property> </option>
                        <option value="4"><sd:Property>transactionForm.transStatus.4</sd:Property> </option>
                        </select>
                    </div>
                </td>
            </tr>            
            <tr>
                <td colspan="2">
                    <input data-mini="true" data-inline="true" type="submit" id="btnSearch" name="btnSearch" value="<sd:Property>transactionForm.btnSearch</sd:Property>" />
                <input data-mini="true" data-inline="true" type="button" id="btnExcel" name="btnExcel" value="<sd:Property>transactionForm.btnExcel</sd:Property>" onclick="exportExcel('TRANSACTION');" />
            </td>
        </tr>
    </table>
</form>
<BR/>
<h3>Kết quả giao dịch</h3>
<div id="transactionResult" name="transactionResult" style="overflow-y: auto"></div> 

<div id="transactionDetail" name="transactionDetail" style="display: none">
    <div>
        Chi tiết giao dịch</div>
    <div>
        <div id="transContent" name="transContent">

        </div>
    </div>
</div>