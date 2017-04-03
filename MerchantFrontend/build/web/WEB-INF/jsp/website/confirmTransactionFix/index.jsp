<%-- 
    Document   : index
    Created on : 21-Oct-2016, 13:34:25
    Author     : anhld4
--%>

<%@page import="com.viettel.common.util.ResourceBundleUtil"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sd" uri="struts-dojo-tags" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script>
    var dataConfirm = Array();
    var originalData = Array();
    $("#divConfirmButton").hide();
    removeData = function(id) {
        dataConfirm = $.grep(dataConfirm, function(e) {
            return e.id !== id;
        });
    };
    addData = function(sid, sval) {
        removeData(sid);
        dataConfirm.push(
                {id: sid, val: sval}
        );
    };
    clearAllData = function() {
        dataConfirm = Array();
        originalData = Array();
    };
    getConfirmData = function() {
        var data = '';
        jQuery.each(dataConfirm, function() {
            if (data !== '')
                data += '&';
            data += this.id + '=' + this.val;
        });
        console.log(data);
        return data;
    };
    $.validator.addMethod(
            "greaterThan",
            function(value, element, params) {
                var from = document.getElementById("transactionConfirmForm.fromDate").value.trim();
                var to = document.getElementById("transactionConfirmForm.toDate").value.trim();
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
                var from = document.getElementById("transactionConfirmForm.fromDate").value.trim();
                var to = document.getElementById("transactionConfirmForm.toDate").value.trim();
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
                if (value !== null) {
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
        MERCHANTGW.FIXTRANSACTIONCONFIRM =
                {
                    setupFormValidation: function()
                    {
                        $("#transactionConfirmForm").validate({
                            rules: {
                                'transactionConfirmForm.fromDate': {
                                    required: true,
                                    vnDate: true,
                                    smallerThan: true
                                },
                                'transactionConfirmForm.toDate': {
                                    required: true,
                                    vnDate: true,
                                    greaterThan: true
                                }
                            },
                            messages: {
                                'transactionConfirmForm.fromDate': {
                                    required: "Thông tin bắt buộc"
                                },
                                'transactionConfirmForm.toDate': {
                                    required: "<sd:Property>ERROR_INFO_MISSING</sd:Property>"
                                }
                            },
                            submitHandler: function(form) {
                                MERCHANTGW.FIXTRANSACTIONCONFIRM.DoSearch();
                            }
                        });
                        //form xac nhan
                        $("#frmDoConfirm").validate({
                            submitHandler: function(form) {
                                MERCHANTGW.FIXTRANSACTIONCONFIRM.doConfirm();
                            }
                        });
                    },
                    onSearchSuccess: function(data, status)
                    {
                        if (typeof(data) === "string") {
                            location.href = "Main.do";
                            return;
                        }
                        $("#divConfirmButton").hide();
                        var customInfo = data.customInfo;
                        if (customInfo[0] === 'success') {
                            originalData = data.items;
                            bindData(data.items);
                            if (data.totalRow > 0) {
                                $("#divConfirmButton").show();
                            }
                        } else {
                            var errMsg = 'Lỗi, không thể tra cứu giao dịch';
                            if (customInfo[1] !== '') {
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
                        clearAllData();
                        $("#errorMessage").html('');
                        $("#errorMessage").hide();
                        var tokenParam = token.getTokenParam();
                        var formData = $("#transactionConfirmForm").serialize() + '&' + $.param(tokenParam);
                        $.ajax({
                            type: "POST",
                            url: "ConfirmTransFix!searchTransConfirm.do?",
                            cache: false,
                            data: formData,
                            success: MERCHANTGW.FIXTRANSACTIONCONFIRM.onSearchSuccess,
                            error: MERCHANTGW.FIXTRANSACTIONCONFIRM.onError
                        });
                        return false;
                    },
                    doConfirm: function() {
                        showModal();
                        $("#errorMessage").html('');
                        $("#errorMessage").hide();
                        console.log(dataConfirm);
                        var tokenParam = token.getTokenParam();
                        var formData = getConfirmData() + '&' + $.param(tokenParam);
                        $.ajax({
                            type: "POST",
                            url: "ConfirmTransFix!multiConfirm.do?",
                            cache: false,
                            data: formData,
                            success: MERCHANTGW.FIXTRANSACTIONCONFIRM.onConfirmSuccess,
                            error: MERCHANTGW.FIXTRANSACTIONCONFIRM.onError
                        });
                        return false;
                    },
                    onConfirmSuccess: function(data, status) {
                        if (typeof(data) === "string") {
                            location.href = "Main.do";
                            return;
                        }
                        var customInfo = data.customInfo;
                        var errMsg = '';
                        if (customInfo[0] === 'success') {
                            $('#transactionConfirmDetail').jqxWindow('hide');
                            errMsg = 'Bạn đã xác nhận giao dịch thành công';
                            MERCHANTGW.FIXTRANSACTIONCONFIRM.DoSearch();
                            if (customInfo[1] !== '') {
                                errMsg = customInfo[1];
                            }
                        } else {
                            var errMsg = 'Lỗi, không thể thực hiện xác nhận giao dịch';
                            if (customInfo[1] !== '') {
                                errMsg = customInfo[1];
                            }
                        }
                        if (errMsg !== '') {
                            alert(errMsg);
                        }
                        hideModal();
                    }

                };
        //when the dom has loaded setup form validation rules
        $(D).ready(function($) {
            MERCHANTGW.FIXTRANSACTIONCONFIRM.setupFormValidation();
            createDetailElements();
            setDefaultDate();
        });
    })(jQuery, window, document);
    onError = function(data, status)
    {
        hideModal();
    };
    bindData = function(data) {
        var source2 =
                {
                    localdata: [{"val": "2", "text": "Xác nhận"}, {"val": "3", "text": "Từ chối"}],
                    datatype: "json",
                    datafields: [
                        {name: 'val'},
                        {name: 'text'}
                    ]
                };
        var dataAdapter2 = new $.jqx.dataAdapter(source2, {
            loadComplete: function(data) {
            },
            loadError: function(xhr, status, error) {
            }
        });
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
        };
//        "<a href=\"Export.do?type=REPORT_TRANS_FIX\">Download file tại đây</a>"
        $("#exportReport").html("<input data-mini=\"true\" data-inline=\"true\" type=\"button\" id=\"btnExcel\" name=\"btnExcel\" value=\"Download file tại đây\" onclick=\"exportExcel('REPORT_TRANS_FIX');\" />");
        
        $("#transactionConfirmResult").jqxGrid(
                {
                    source: dataAdapter,
                    pageable: true,
                    autoheight: true,
                    selectionmode: 'singlecell',
                    editable: true,
                    columnsresize: true,
                    pagesize: 20,
                    width: 980,
                    localization: getLocalization(),
                    columns: [
                        {text: 'STT', datafield: 'TEXTTRANSSTATUS', width: 40, cellsrenderer: rowIndex},
                        {text: 'Mã GD YC', datafield: 'REQUEST_ID', width: 150, editable: false},
                        {text: 'Mã GD gốc', datafield: 'ORIGINAL_REQUEST_ID', width: 150, editable: false},
                        {text: 'Mã KH mới', datafield: 'BILLING_CODE', width: 120, editable: false},
                        {text: 'Mã KH gốc', datafield: 'ORIGINAL_BILLING_CODE', width: 120, editable: false},
                        {text: 'Trạng thái', datafield: 'STATUSDESC', width: 120, cellsrenderer: rowStatusDesc, editable: false},
                        {text: 'Xác nhận', datafield: 'CONFIRMSTATUS', width: 120, columntype: 'template', initeditor: function(row, cellvalue, editor, celltext, cellwidth, cellheight) {
                                var datarow = $("#transactionConfirmResult").jqxGrid('getrowdata', row);
                                if (datarow.TRANS_STATUS === '1') {
                                    editor.jqxDropDownList({source: dataAdapter2, displayMember: "text", valueMember: "val", placeHolder: "-- Chọn --", width: '120px'});
                                }
                            },
                            cellvaluechanging: function(row, column, columntype, oldvalue, newvalue) {
                                var datarow = $("#transactionConfirmResult").jqxGrid('getrowdata', row);
                                var name = 'txt' + datarow.TRANS_BANKPLUS_ID;
                                console.log(name);
                                var val = "";
                                var x = false;
                                if (newvalue === "") {
                                    val = oldvalue;
                                } else {
                                    val = newvalue;
                                }
                                if (val != null && (val === '2' || val === '3')) {
                                    console.log(val + ", " + x);
                                    x = true;
                                }
                                if (datarow.TRANS_STATUS === '1' && x) {
                                    addData(name, val);
                                    return getstatusConfirm(null, null, val, null, null);
                                } else {
                                    return null;
                                }
                            }
                        },
//                        { text: 'Trạng thái GD', datafield: 'TRANS_STATUS', width: 100, cellsrenderer:getstatus},
                        {text: 'Tiền gốc', datafield: 'ORIGINAL_AMOUNT', width: 90, cellsalign: 'right', cellsformat: 'n', editable: false},
                        {text: 'Tiền điều chỉnh', datafield: 'AMOUNT', width: 90, cellsalign: 'right', cellsformat: 'n', editable: false},
                        {text: 'Ngày GD', datafield: 'ORIGINAL_REQUEST_DATE', width: 150, editable: false},
                        {text: 'Ngày YC', datafield: 'REQUEST_DATE', width: 150, editable: false}
                    ]
                });
    };
    setDefaultDate = function() {
        var from = document.getElementById("transactionConfirmForm.fromDate");
        var to = document.getElementById("transactionConfirmForm.toDate");
        var now = new Date();
        var mon = now.getMonth() + 1;
        var day = now.getDate();
        if (day.toString().length === 1)
            day = '0' + day;
        if (mon.toString().length === 1)
            mon = '0' + mon;
        var fromText = '01/' + mon + '/' + now.getFullYear();
        var toText = day + '/' + mon + '/' + now.getFullYear();
        from.value = fromText;
        to.value = toText;
    };
    rowIndex = function(row, columnfield, value, defaulthtml, columnproperties) {
        var num = row + 1;
        value = num;
        return value;
    };
    rowStatusDesc = function(row, columnfield, value, defaulthtml, columnproperties) {
        var datarow = $("#transactionConfirmResult").jqxGrid('getrowdata', row);
        var status = datarow.TRANS_STATUS;
        if (status === '1') {
            value = 'Chờ xử lý';
        } else if (status === '2') {
            value = 'Đã xác nhận';
        } else if (status === '3') {
            value = 'Đã hủy/Từ chối';
        } else {
            value = '';
        }
        return value;
    };
    formatHTML = function(row, columnfield, value, defaulthtml, columnproperties) {
        value = escapeHtml(value);
        return value;
    };
    getstatusConfirm = function(row, columnfield, value, defaulthtml, columnproperties) {
        if (value === '2') {
            value = 'Xác nhận';
        } else if (value === '3') {
            value = 'Từ chối';
        } else {
            value = '';
        }
        return value;
    };
    rowConfirmStatusDesc = function(row, columnfield, value, defaulthtml, columnproperties) {
        var datarow = $("#transContent").jqxGrid('getrowdata', row);
        var status = datarow.CONFIRMSTATUS;
        if (status === '2') {
            value = 'Xác nhận';
        } else if (status === '3') {
            value = 'Từ chối';
        } else {
            value = '';
        }
        return value;
    };
    viewConfirmData = function() {
        console.log('viewConfirmData');
        var tempData = Array();
        console.log(dataConfirm);
        jQuery.each(originalData, function(k, v) {
            jQuery.each(dataConfirm, function(b, c) {
                var sid = c.id;
                sid = (sid + '').substring(3);
                if (v.TRANS_BANKPLUS_ID.toString() === sid) {
                    v.CONFIRMSTATUS = c.val;
                    tempData.push(v);
                }
            });
        });
        if (tempData.length > 0) {
            console.log(tempData);
            bindDataConfirm(tempData);
            $('#transactionConfirmDetail').jqxWindow('open');
        } else {
            alert('Bạn phải chọn trạng thái xác nhận ít nhất 1 giao dịch');
        }
    };
    closejqxWindow = function() {
        $('#transactionConfirmDetail').jqxWindow('close');
    };
    bindDataConfirm = function(data) {
        console.log('bindDataConfirm');
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
        };
        $("#transContent").jqxGrid(
                {
                    source: dataAdapter,
                    pageable: false,
                    autoheight: true,
                    selectionmode: 'singlecell',
                    width: 800,
                    localization: getLocalization(),
                    columns: [
                        {text: 'Mã GD YC', datafield: 'REQUEST_ID', width: 130, editable: false},
                        {text: 'Mã GD gốc', datafield: 'ORIGINAL_REQUEST_ID', width: 130, editable: false},
                        {text: 'Xác nhận', datafield: 'CONFIRMSTATUS', width: 70, cellsrenderer: rowConfirmStatusDesc},
                        {text: 'Mã KH mới', datafield: 'BILLING_CODE', width: 120, cellsrenderer: formatHTML, editable: false},
                        {text: 'Tiền GD', datafield: 'ORIGINAL_AMOUNT', width: 90, cellsalign: 'right', cellsformat: 'n', editable: false},
                        {text: 'Tiền điều chỉnh', datafield: 'AMOUNT', width: 100, cellsalign: 'right', cellsformat: 'n', editable: false},
                        {text: 'Ngày YC', datafield: 'REQUEST_DATE', width: 150, editable: false}
                    ]
                });
    };
    function createDetailElements() {
        $('#transactionConfirmDetail').jqxWindow({height: 500, width: 850,
            resizable: false, draggable: false, isModal: true, modalOpacity: 0.3, autoOpen: false
        });
    }
    ;</script>

    <form id="transactionConfirmForm" name="transactionConfirmForm"  novalidate="novalidate">
        <h3>Xác nhận điều chỉnh giao dịch</h3>
        <div id="errorMessage" style="display: none; " class="errorMessage"> </div>
        <table border="0" style="width: 100%; border-collapse: collapse; margin: 0; padding: 0">
            <tr style="height: 10px">
                <td style="height: 10px">
                    <div data-role="fieldcontain">
                        <label for="transactionConfirmForm.fromDate" ><sd:Property>transactionForm.fromDate</sd:Property></label>
                        <input id="transactionConfirmForm.fromDate" type="text" value="" name="transactionConfirmForm.fromDate" data-role="date" data-mini="true" />
                    </div>
                </td>
                <td style="height: 10px">
                    <div data-role="fieldcontain">
                        <label for="transactionConfirmForm.toDate" ><sd:Property>transactionForm.toDate</sd:Property></label>
                        <input id="transactionConfirmForm.toDate" type="text" value="" name="transactionConfirmForm.toDate" data-role="date" data-mini="true" />
                    </div>
                </td>
                <td style="height: 10px">
                    <div data-role="fieldcontain">
                        <label for="transactionConfirmForm.transStatus" ><sd:Property>transactionForm.transStatus</sd:Property></label>
                        <select id="transactionConfirmForm.transStatus" name="transactionConfirmForm.transStatus" data-mini="true" data-inline="true" data-iconpos="noicon">
                            <!--<option value="NONE"><sd:Property>transactionForm.selectAll</sd:Property> </option>-->
                            <option value="1">Chờ xử lý</option>
                            <option value="2">Đã xác nhận</option>
                            <option value="3">Từ chối</option>
                        </select>
                    </div>
                </td>
            </tr>

            <tr>
                <td colspan="3" style="text-align: center">
                    <input data-mini="true" data-inline="true" type="submit" id="btnSearch" name="btnSearch" value="<sd:Property>transactionForm.btnSearch</sd:Property>" />
            </td>
        </tr>
    </table>
</form>
<BR/>
<h3>Danh sách giao dịch</h3>
<div id="transactionConfirmResult" name="transactionConfirmResult"></div> 
<div id="exportReport" name="exportReport" style="text-align: center">

</div>
<div style="width: 900px; text-align: right; display: none;" id="divConfirmButton">
    <input data-mini="true" data-inline="true" type="button" id="btnConfirm" name="btnConfirm" value=" Xác nhận " onclick="viewConfirmData();" />
</div>
<br/>

<div id="transactionConfirmDetail" name="transactionConfirmDetail" style="color: #007D80; display: none">
    <div>
        Xác nhận trạng thái giao dịch</div>
    <div>
        <h3>Duyệt trạng thái giao dịch</h3>
        <div id="transContent" name="transContent">
        </div>

        <form id="frmDoConfirm" name="frmDoConfirm">
            <div style="width: 800px; text-align: right;">
                <input type="button" value=" Hủy bỏ " name="btnDoCancel" id="btnDoCancel" data-inline="true" data-mini="true" onclick="closejqxWindow();">
                <input type="submit" value=" Duyệt " name="btnDoConfirm" id="btnDoConfirm" data-inline="true" data-mini="true">
            </div>
        </form>
    </div>
</div>