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
    "vnDate",
    function(value, element, regexp) {
        var pattern =/^([0-9]{2})\/([0-9]{2})\/([0-9]{4})$/;
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
    },'Phải lớn hơn từ ngày.');
    
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
    },'Phải nhỏ hơn đến ngày.');
    //Khoi tao validate rule
    (function($, W, D)
    {
        var MERCHANTGW = {};

        MERCHANTGW.REPORTTOTAL =
            {
            setupFormValidation: function()
            {
                //form validation rules
                $("#reportTotalForm").validate({
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
                            required: "<sd:Property>ERROR_INFO_MISSING</sd:Property>"
                        },
                        'toDate': {
                            required: "<sd:Property>ERROR_INFO_MISSING</sd:Property>"
                        }
                    },
                    submitHandler: function(form) {
                        MERCHANTGW.REPORTTOTAL.DoSearch();
                    }
                });
            },
            
            onSearchSuccess: function(data, status)
            {
                if (typeof(data) === "string") {
                    location.href="Main.do";
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
                var formData = $("#reportTotalForm").serialize() + '&' + $.param(tokenParam);
                $.ajax({
                    type: "POST",
                    url: "Report!onReprotTotal.do?",
                    cache: false,
                    data: formData,
                    success: MERCHANTGW.REPORTTOTAL.onSearchSuccess,
                    error: MERCHANTGW.REPORTTOTAL.onError
                });
                return false
            }
            
        }

        //when the dom has loaded setup form validation rules
        $(D).ready(function($) {
            MERCHANTGW.REPORTTOTAL.setupFormValidation();
            setDefaultDate();
        });


    })(jQuery, window, document);
    onError = function(data, status)
    {
        hideModal();
    }
    bindData = function(data){
        var source =
            {
            localdata: data,
            datatype: "json"
        };
        var dataAdapter = new $.jqx.dataAdapter(source, {
            loadComplete: function (data) { },
            loadError: function (xhr, status, error) { }    
        });
        
        var getLocalization = function () {
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
            //            pagesize: 20,
            width: 980,
            showstatusbar: true,
            showaggregates: true,
            columnsresize: true,
            localization:getLocalization(),
            columns: [
                { text: 'STT', datafield: 'NULL', width: 40, cellsrenderer:rowIndex },
                { text: 'Ngân hàng', datafield: 'BANKCODE', width: 80, cellsrenderer:formatHTML },
                { text: 'Mã NCCDV', datafield: 'CPCODE', width: 50, cellsrenderer:formatHTML },
                { text: 'Tên NCCDV', datafield: 'CPNAME', width: 150, cellsrenderer:formatHTML },
                { text: 'Ngày/Tháng', datafield: 'MONDAY', width: 100, cellsrenderer:formatHTML,
                    aggregatesrenderer: function (aggregates, column, element, summaryData) {
                        return "<div style='width: 100%; height: 100%; text-align: right; padding-top: 6px'><b>Tổng</b></div>";
                    }},
                { text: 'Tổng số GD Thanh toán', datafield: 'TOTALPAY', width: 150, cellsalign: 'right', cellsformat: 'n',
                    aggregates: [{ '':
                                function (aggregatedValue, currentValue) {
                                return aggregatedValue + currentValue;
                            }
                        }
                    ],
                    aggregatesrenderer: function (aggregates, column, element, summaryData) {
                        var renderstring = "<div style='width: 100%; height: 100%; text-align: right; padding-top: 6px'><b>";
                        $.each(aggregates, function (key, value) {
                            renderstring += value;
                        });
                        renderstring += "</b></div>";
                        return renderstring;
                    }
                },
                { text: 'Tổng số GD hoàn tiền', datafield: 'TOTALREFUND', cellsalign: 'right', width: 150, cellsformat: 'n',
                    aggregates: [{ '':
                                function (aggregatedValue, currentValue) {
                                return aggregatedValue + currentValue;
                            }
                        }
                    ],
                    aggregatesrenderer: function (aggregates, column, element, summaryData) {
                        var renderstring = "<div style='width: 100%; height: 100%; text-align: right; padding-top: 6px'><b>";
                        $.each(aggregates, function (key, value) {
                            renderstring += value;
                        });
                        renderstring += "</b></div>";
                        return renderstring;
                    }},
                { text: 'Tổng tiền hoàn', datafield: 'REFUNDAMOUNT', width: 150, cellsalign: 'right', cellsformat: 'n', 
                    aggregates: [{ '':
                                function (aggregatedValue, currentValue) {
                                return aggregatedValue + currentValue;
                            }
                        }
                    ],
                    aggregatesrenderer: function (aggregates, column, element, summaryData) {
                        var renderstring = "<div style='width: 100%; height: 100%; text-align: right; padding-top: 6px'><b>";
                        $.each(aggregates, function (key, value) {
                            renderstring += value;
                        });
                        renderstring += "</b></div>";
                        return renderstring;
                    }},
                { text: 'Tổng tiền thanh toán', datafield: 'PAYAMOUNT', width: 150, cellsalign: 'right', cellsformat: 'n', 
                    aggregates: [{ '':
                                function (aggregatedValue, currentValue) {
                                return aggregatedValue + currentValue;
                            }
                        }
                    ],
                    aggregatesrenderer: function (aggregates, column, element, summaryData) {
                        var renderstring = "<div style='width: 100%; height: 100%; text-align: right; padding-top: 6px'><b>";
                        $.each(aggregates, function (key, value) {
                            renderstring += value;
                        });
                        renderstring += "</b></div>";
                        return renderstring;
                    }},
                { text: 'Tổng phí', datafield: 'TOTALFEE', width: 150, cellsalign: 'right', cellsformat: 'n', 
                    aggregates: [{ '':
                                function (aggregatedValue, currentValue) {
                                return aggregatedValue + currentValue;
                            }
                        }
                    ],
                    aggregatesrenderer: function (aggregates, column, element, summaryData) {
                        var renderstring = "<div style='width: 100%; height: 100%; text-align: right; padding-top: 6px'><b>";
                        $.each(aggregates, function (key, value) {
                            renderstring += value;
                        });
                        renderstring += "</b></div>";
                        return renderstring;
                    }}
            ]
        });           
    }
    rowIndex = function (row, columnfield, value, defaulthtml, columnproperties) {
        var num = row + 1;
        value = num;
        return value;
    }
    setDefaultDate = function(){
        var from = document.getElementById("fromDate");
        var to = document.getElementById("toDate");
        var now = new Date();
        var mon = now.getMonth()+1;
        var day = now.getDate();
        if(day.toString().length == 1) day = '0' + day;
        if(mon.toString().length == 1) mon = '0' + mon;
        var fromText =  '01/' + mon + '/' + now.getFullYear();
        var toText = day + '/' + mon + '/' + now.getFullYear();
        from.value = fromText;
        to.value = toText;
    }
    formatHTML = function (row, columnfield, value, defaulthtml, columnproperties) {
        value = escapeHtml(value);
        return value;
    }
    
    //    changeDate = function(){
    //        console.log('changeDate');
    //        $("#fromDate").datepicker({
    //            dateFormat: 'mm-yy',
    //            changeMonth: true,
    //            changeYear: true,
    //            showButtonPanel: true,
    // 
    //            onClose: function() {
    //                //                var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
    //                //                var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
    //                //                $(this).val($.datepicker.formatDate('yy-mm', new Date(year, month, 1)));
    //                var iMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
    //                var iYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
    //                console.log(iMonth);
    //                $(this).datepicker('setDate', new Date(iYear, iMonth, 1));
    //            },
    //            beforeShow: function(element) {
    //                console.log(element);
    //                setTimeout(function() { $(".ui-datepicker-calendar").hide(); }, 0);
    //            }
    //        });
    //    }
    //    
    //    $(document).ready(function(){
    // 
    //        changeDate();
    //        $("#fromDate").focus(function () {
    //            changeDate();
    //            //            $("#ui-datepicker-div").position({
    //            //                my: "center middle",
    //            //                at: "center bottom",
    //            //                of: $(this)
    //            //            });
    //        });
    //    });
</script>

<form id="reportTotalForm" name="reportTotalForm"  novalidate="novalidate">
    <h3>Báo cáo tổng hợp giao dịch</h3>
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
            <td colspan="2">
                <div data-role="fieldcontain">
                    <label for="reportType" >Theo ngày/tháng</label>
                    <select id="reportType" name="reportType" data-mini="true" data-inline="true" data-iconpos="noicon">
                        <option value="DAY">--- Ngày ---</option>
                        <option value="MON">--- Tháng ---</option>
                    </select>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div data-role="fieldcontain">
                    <label for="bankCode" ><sd:Property>transactionForm.bankCode</sd:Property></label>
                    <select id="bankCode" name="bankCode" data-mini="true" data-inline="true" data-iconpos="noicon">
                        <option value="NONE"><sd:Property>transactionForm.selectAll</sd:Property> </option>
                        <option value="MB">MB</option>
                        <option value="VCB">VCB</option>
                        <option value="BIDV">BIDV</option>
                    </select>
                </div>
            </td>
            <td>
                    <div data-role="fieldcontain">
                        <label for="contentProviderId">Chọn CP</label>
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
                <input data-mini="true" data-inline="true" type="button" id="btnExcel" name="btnExcel" value="<sd:Property>transactionForm.btnExcel</sd:Property>" onclick="exportExcel('REPORT_TOTAL');" />
            </td>
        </tr>
    </table>
</form>
<BR/>
<h3>Kết quả giao dịch</h3>
<div id="transactionResult" name="transactionResult"></div> 
