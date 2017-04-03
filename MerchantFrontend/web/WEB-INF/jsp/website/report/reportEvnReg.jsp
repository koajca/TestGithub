<%--
    Document   : reportEvnReg.jsp
    Created on : Jul 28, 2015
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

        MERCHANTGW.REPORTEVNREG =
                {
                    setupFormValidation: function()
                    {
                        //form validation rules
                        $("#reportEvnRegForm").validate({
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
                                                                                                        MERCHANTGW.REPORTEVNREG.DoSearch();
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
                                                                                                    exportExcel('REPORT_EVNREG');
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
                                                                                                                            var formData = $("#reportEvnRegForm").serialize() + '&' + $.param(tokenParam);
                                                                                                                            $.ajax({
                                                                                                                                type: "POST",
                                                                                                                                url: "Report!onReportEvnReg.do?",
                                                                                                                                cache: false,
                                                                                                                                data: formData,
                                                                                                                                success: MERCHANTGW.REPORTEVNREG.onSearchSuccess,
                                                                                                                                error: MERCHANTGW.REPORTEVNREG.onError
                                                                                                                            });
                                                                                                                            return false
                                                                                                                        }

                                                                                                                    }

                                                                                                            //when the dom has loaded setup form validation rules
                                                                                                            $(D).ready(function($) {
                                                                                                                MERCHANTGW.REPORTEVNREG.setupFormValidation();
                                                                                                                setDefaultDate();
                                                                                                                configdate();
                                                                                                            });
                                                                                                            
                                                                                                            


                                                                                                        })(jQuery, window, document);
                                                                                                        onError = function(data, status)
                                                                                                        {
                                                                                                            hideModal();
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

    </script>


    <form id="reportEvnRegForm" name="reportEvnRegForm"  novalidate="novalidate">
        <h3>Báo cáo đăng ký thanh toán hóa đơn điện</h3>
        <div id="errorMessage" style="display: none; " class="errorMessage"> </div>
        <table border="0" style="width: 100%; border-collapse: collapse; margin: 0; padding: 0">
            <tr style="height: 10px">
                <td style="height: 10px">
                    <div data-role="fieldcontain">
                        <label for="fromDate" ><sd:Property>transactionForm.fromDate</sd:Property></label>
                    <input id="fromDate" type="text" value="" name="fromDate" data-mini="true" readonly="true" />
                    </div>
                </td>
                <td style="height: 10px">
                    <div data-role="fieldcontain">
                        <label for="toDate" ><sd:Property>transactionForm.toDate</sd:Property></label>
                        <input id="toDate" type="text" value="" name="toDate" data-mini="true" />
                    </div>
                </td>
            </tr>

            <tr>
                <td colspan="2">
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
                <input data-mini="true" data-inline="true" type="submit" id="btnSearch" name="btnSearch" value="<sd:Property>transactionForm.btnExcel</sd:Property>" />
            </td>
        </tr>
    </table>
</form>
<script type="text/javascript">
    configdate = function() {

    }
//    $("#toDate").change(function() {
//        if ($("#toDate").val() != '') {
//            var vTo = $("#toDate").val().split("/");
//            var fDate = '01/' + vTo[1] + '/' + vTo[2];
//            $("#fromDate").val(fDate);
//        }
//    });
 
    $("#toDate").datepicker({
}).on("change", function() {
              var vTo = $("#toDate").val().split("/");
                  var fDate = '01/' + vTo[1] + '/' + vTo[2];
                  $("#fromDate").val(fDate);
  });
</script>
<style>
div.hasDatepicker {
	display: block;
	padding: 0;
	overflow: visible;
	margin: 8px 0;
	max-width:340px
}
.ui-datepicker {
	overflow: visible; margin: 0;
}
.ui-datepicker .ui-datepicker-header {
	position:relative;
	padding:.6em 0;
	border-bottom: 0;
	font-weight: bold;
}
.ui-datepicker .ui-datepicker-prev,
.ui-datepicker .ui-datepicker-next {
	padding: 1px 0 1px 2px;
	position:absolute;
	top: .6em;
	margin-top: 0;
	text-indent: -9999px;
	-webkit-border-radius: 1em;
	border-radius: 1em;
	vertical-align: middle;
	margin-right: .625em;
	width: 1.75em;
	height: 1.75em;
	white-space: nowrap !important;
}
.ui-datepicker .ui-datepicker-prev:after,
.ui-datepicker .ui-datepicker-next:after {
	left: 50%;
	margin-left: -11px;
	top: 50%;
	margin-top: -11px;
	content: "";
	position: absolute;
	display: block;
	width: 22px;
	height: 22px;
	background-color: rgba(0,0,0,.3) /*{global-icon-disc}*/;
	background-position: center center;
	background-repeat: no-repeat;
	-webkit-border-radius: 1em;
	border-radius: 1em;
}
.ui-datepicker .ui-datepicker-next:after{
	background-image: url("data:image/svg+xml;charset=US-ASCII,%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22iso-8859-1%22%3F%3E%3C!DOCTYPE%20svg%20PUBLIC%20%22-%2F%2FW3C%2F%2FDTD%20SVG%201.1%2F%2FEN%22%20%22http%3A%2F%2Fwww.w3.org%2FGraphics%2FSVG%2F1.1%2FDTD%2Fsvg11.dtd%22%3E%3Csvg%20version%3D%221.1%22%20id%3D%22Layer_1%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%20x%3D%220px%22%20y%3D%220px%22%20%20width%3D%2214px%22%20height%3D%2214px%22%20viewBox%3D%220%200%2014%2014%22%20style%3D%22enable-background%3Anew%200%200%2014%2014%3B%22%20xml%3Aspace%3D%22preserve%22%3E%3Cpolygon%20fill%3D%22%23FFF%22%20points%3D%2214%2C7%207%2C0%207%2C5%200%2C5%200%2C9%207%2C9%207%2C14%20%22%2F%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3C%2Fsvg%3E");
}
.ui-datepicker .ui-datepicker-prev:after{
	background-image: url("data:image/svg+xml;charset=US-ASCII,%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22iso-8859-1%22%3F%3E%3C!DOCTYPE%20svg%20PUBLIC%20%22-%2F%2FW3C%2F%2FDTD%20SVG%201.1%2F%2FEN%22%20%22http%3A%2F%2Fwww.w3.org%2FGraphics%2FSVG%2F1.1%2FDTD%2Fsvg11.dtd%22%3E%3Csvg%20version%3D%221.1%22%20id%3D%22Layer_1%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%20x%3D%220px%22%20y%3D%220px%22%20%20width%3D%2214px%22%20height%3D%2214px%22%20viewBox%3D%220%200%2014%2014%22%20style%3D%22enable-background%3Anew%200%200%2014%2014%3B%22%20xml%3Aspace%3D%22preserve%22%3E%3Cpolygon%20fill%3D%22%23FFF%22%20points%3D%227%2C5%207%2C0%200%2C7%207%2C14%207%2C9%2014%2C9%2014%2C5%20%22%2F%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3Cg%3E%3C%2Fg%3E%3C%2Fsvg%3E");
}
.ui-datepicker .ui-datepicker-prev {
	left:9px;
}
.ui-datepicker .ui-datepicker-next {
	right:2px;
}
.ui-datepicker .ui-datepicker-title {
	margin: 0 2.3em;
	line-height: 1.8em;
	text-align: center;
}
.ui-datepicker .ui-datepicker-title select {
	font-size:1em; margin:1px 0;
}
.ui-datepicker select.ui-datepicker-month-year {
	width: 100%;
}
.ui-datepicker select.ui-datepicker-month,
.ui-datepicker select.ui-datepicker-year {
	width: 49%;
}
.ui-datepicker table {
	width: 100%;
	border-collapse: collapse;
	margin:0;
}
.ui-datepicker td {
	border-width: 1px;
	padding: 0;
	border-style: solid;
	text-align: center;
	min-width: 41px;
}
.ui-datepicker td span,
.ui-datepicker a {
	display: block;
	text-align: center;
	text-decoration: none;
	/* from .ui-btn class */
	font-size: 16px;
	position: relative;
	text-overflow: ellipsis;
	overflow: hidden;
	white-space: nowrap;
	cursor: pointer;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
	text-decoration: none !important;
	font-weight: bold;
	border-width: 1px;
	border-style: solid;
}
.ui-datepicker td a {
	padding: .2em 0;
	font-weight: bold;
	margin: 0;
	border-width: 0;
}
.ui-datepicker-calendar th {
	padding-top: .4em;
	padding-bottom: .4em;
	border-width: 1px;
	border-style: solid;
}
.ui-datepicker-calendar th span,
.ui-datepicker-calendar span.ui-state-default {
	opacity: .7;
}
.ui-datepicker-calendar td a,
.ui-datepicker-calendar td span {
	padding: .6em .5em;
}
.ui-datepicker .ui-state-disabled {
	opacity: 1;
}
.ui-datepicker.ui-corner-all,
.ui-datepicker .ui-datepicker-header.ui-corner-all {
	border-radius: 0;
	-webkit-border-radius: 0;
}
</style>
