<%-- 
    Document   : transactionEVN
    Created on : Jul 16, 2014, 2:06:43 PM
    Author     : pham
--%>

<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
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
        var from = document.getElementById("transactionEVNForm.fromDate").value.trim();
        var to = document.getElementById("transactionEVNForm.toDate").value.trim();
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
        var from = document.getElementById("transactionEVNForm.fromDate").value.trim();
        var to = document.getElementById("transactionEVNForm.toDate").value.trim();
        var vfrom = from.split("/");
        var vTo = to.split("/");
        if (!/Invalid|NaN/.test(new Date(vfrom[2], vfrom[1], vfrom[0])) && !/Invalid|NaN/.test(new Date(vTo[2], vTo[1], vTo[0]))) {
            return new Date(vfrom[2], vfrom[1], vfrom[0]) <= new Date(vTo[2], vTo[1], vTo[0]);
        }
        return true;
    },'Phải nhỏ hơn đến ngày.');
    
     $.validator.addMethod(
    "smallerNowFromThan",
    function(value, element, params) {
        var fromDate = document.getElementById("transactionEVNForm.fromDate").value.trim();
        var vfrom = fromDate.split("/");
        
        var Yearnow="<%=Calendar.getInstance().get(Calendar.YEAR)%>";
        var Monthnow="<%=Calendar.getInstance().get(Calendar.MONTH) + 1%> ";
        var Daynow="<%=Calendar.getInstance().get(Calendar.DATE)%>";

        if (!/Invalid|NaN/.test(new Date(vfrom[2], vfrom[1], vfrom[0])) ) {
            return new Date(vfrom[2], vfrom[1], vfrom[0]) <= new Date(Yearnow,Monthnow, Daynow);
        }
        
        return true;
    },'Phải nhỏ hơn ngày hiện tại.');
    
     $.validator.addMethod(
    "smallerNowToThan",
    function(value, element, params) {
        var fromDate = document.getElementById("transactionEVNForm.toDate").value.trim();
        var vfrom = fromDate.split("/");
        
        var Yearnow="<%=Calendar.getInstance().get(Calendar.YEAR)%>";
        var Monthnow="<%=Calendar.getInstance().get(Calendar.MONTH) + 1%> ";
        var Daynow="<%=Calendar.getInstance().get(Calendar.DATE)%>";

        if (!/Invalid|NaN/.test(new Date(vfrom[2], vfrom[1], vfrom[0])) ) {
            return new Date(vfrom[2], vfrom[1], vfrom[0]) <= new Date(Yearnow,Monthnow, Daynow);
        }

        return true;
    },'Phải nhỏ hơn ngày hiện tại.');
    
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
        var pattern =/^([0-9]{2})\/([0-9]{2})\/([0-9]{4})$/;
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
                $("#transactionEVNForm").validate({
                    rules: {
                        'transactionEVNForm.fromDate': {
                            required: true,
                            vnDate: true,
                            smallerThan: '#transactionEVNForm.toDate',
                            smallerNowFromThan: true
                        },
                        'transactionEVNForm.toDate': {
                            required: true,
                            vnDate: true,
                            smallerNowToThan: true,
                            greaterThan: '#transactionEVNForm.fromDate'
                            
                        }
                       
                    },
                    messages: {
                        'transactionEVNForm.fromDate': {
                            required: "<sd:Property>ERROR_INFO_MISSING</sd:Property>"
                        },
                        'transactionEVNForm.toDate': {
                            required: "<sd:Property>ERROR_INFO_MISSING</sd:Property>"
                        }
                       
                    },
                    submitHandler: function(form) {
                        MERCHANTGW.TRANSACTION.DoSearch();
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
                var formData = $("#transactionEVNForm").serialize() + '&' + $.param(tokenParam);
                $.ajax({
                    type: "POST",
                    url: "TransactionEVN!onSearch.do?",
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
            setDefaultDate();
            MERCHANTGW.TRANSACTION.setupFormValidation();
            //createElements();
            
            
        });


    })(jQuery, window, document);
    onError = function(data, status)
    {
        hideModal();
    }
    setDefaultDate = function(){
       
        var from = document.getElementById("transactionEVNForm.fromDate");
        var to = document.getElementById("transactionEVNForm.toDate");
        var now = new Date();
        var mon = now.getMonth()+1;
        var day = now.getDate();
        if(day.toString().length == 1) day = '0' + day;
        if(mon.toString().length == 1) mon = '0' + mon;
        var fromText = day + '/' + mon + '/' + now.getFullYear();
        var toText = day + '/' + mon + '/' + now.getFullYear();
        from.value = fromText;
        to.value = toText;
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
        
        $("#transactionEVNResult").jqxGrid(
        {
            source: dataAdapter,
            pageable: true,
            autoheight: true,
            columnsresize: true,
            pagesize: 20,
            width: 980,
            localization:getLocalization(),
            columns: [
                { text: 'STT', datafield: 'TEXTTRANSSTATUS', width: 60, cellsrenderer:rowIndex },
                { text: 'Mã Ngân hàng', datafield: 'BANKCODE', width: 140, cellsrenderer:formatHTML },
                { text: 'Ngày giao dịch', datafield: 'REQUESTDATE', width: 200, cellsrenderer:formatHTML},
                { text: 'Mã khách hàng', datafield: 'BILLINGCODE', width: 220, cellsrenderer:formatHTML },
                { text: 'Mã hóa đơn', datafield: 'ORDERID', width: 220, cellsrenderer:formatHTML },
                { text: 'Số tiền', datafield: 'AMOUNT', width: 200, cellsalign: 'right', cellsformat: 'n' }
                
            ]
        });           
    }
    formatHTML = function (row, columnfield, value, defaulthtml, columnproperties) {
        value = escapeHtml(value);
        return value;
    }
    rowIndex = function (row, columnfield, value, defaulthtml, columnproperties) {
        var num = row + 1;
        value = num;
        return value;
    }
    getstatus = function (row, columnfield, value, defaulthtml, columnproperties) {
        if(value == '0'){
            value = 'Chưa xử lý';
        }else if(value == '1'){
            value = 'Đang xử lý';
        }else if(value == '2'){
            value = 'Thành công';
        }else if(value == '3'){
            value = 'Đã hủy';
        }else if(value == '4'){
            value = 'Thất bại';
        }else{
            value = '';
        }
        return value;
    }
//    getId = function (row, columnfield, value, defaulthtml, columnproperties) {
//        return '<a href="javascript:void(0);" onclick="return viewDetail(\'' + value + '\');">' + value + '</a>';
//    }
//    
//    viewDetail = function(transId){
//        showModal();
//        var url = '${contextPath}' + '/Transaction!viewDetail.do?transId=' +transId ;
//        $("#transContent").load(url, function() {
//            $(this).trigger("create");
//        });
//        $('#transactionDetail').jqxWindow('open');
//        hideModal();
//        return false;
//    }
//     
//    function createElements() {
//        $('#transactionDetailEVN').jqxWindow({ height: 600, width: '100%',
//            resizable: false, draggable: false, isModal: true, modalOpacity: 0.3, autoOpen: false
//        });
//    }
</script>

<form id="transactionEVNForm" name="transactionEVNForm"  novalidate="novalidate">
    <h3><sd:Property>transactionEVN_title</sd:Property></h3>
    <div id="errorMessage" style="display: none; " class="errorMessage"> </div>
    <table border="0" style="width: 100%; border-collapse: collapse; margin: 0; padding: 0">
        <tr style="height: 10px">
             <td style="height: 10px" >
                <div data-role="fieldcontain">
                    <label for="transactionEVNForm.fromDate" ><sd:Property>transactionEVNForm.fromDate</sd:Property></label>
                    <input tabindex="1" id="transactionEVNForm.fromDate" type="text" value="" name="transactionEVNForm.fromDate" data-role="date" data-mini="true" />
                </div>
            </td>
            <td style="height: 10px" >
                <div data-role="fieldcontain">
                    <label for="transactionEVNForm.toDate" ><sd:Property>transactionEVNForm.toDate</sd:Property></label>
                    <input tabindex="2" id="transactionEVNForm.toDate" type="text" value="" name="transactionEVNForm.toDate" data-role="date" data-mini="true" />
                </div>
            </td>
            
        </tr>
       
     
        <tr>
            <td colspan="2" >
                <div data-role="fieldcontain">
                    <label for="transactionEVNForm.bankCode" ><sd:Property>transactionEVNForm.bankCode</sd:Property></label>
                    <select tabindex="3" id="transactionEVNForm.bankCode" name="transactionEVNForm.bankCode" data-mini="true" data-inline="true" data-iconpos="noicon">
                        <option value="NONE"><sd:Property>transactionEVNForm.selectAll</sd:Property> </option>
                        <option value="MB">MB</option>
                        <option value="VCB">VCB</option>
                        <option value="BIDV">BIDV</option>
                    </select>
                </div>
            </td>
        </tr>
      
        <tr>
            <td colspan="2">
                <input tabindex="4" data-mini="true" data-inline="true" type="submit" id="btnSearch" name="btnSearch" value="<sd:Property>transactionEVNForm.btnSearch</sd:Property>" />
                <input tabindex="5" data-mini="true" data-inline="true" type="button" id="btnExcel" name="btnExcel" value="<sd:Property>transactionEVNForm.btnExcel</sd:Property>" onclick="exportExcel('TRANSACTIONEVN');" />
            </td>
        </tr>
    </table>
</form>
<BR/>
<h3>Kết quả giao dịch</h3>
<div id="transactionEVNResult" name="transactionEVNResult"></div> 

