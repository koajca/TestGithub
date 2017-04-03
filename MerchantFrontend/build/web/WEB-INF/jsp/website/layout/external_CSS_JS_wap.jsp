<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib  prefix="sx" uri="struts-dojo-tags" %>
<%@taglib prefix="sd" uri="struts-dojo-tags" %>


<% request.setAttribute("contextPath", request.getContextPath());%>
<% request.setAttribute("contextJSPath", "");%>
<link rel="stylesheet" href="${contextPath}/themes/custom.css" />
<link rel="stylesheet" href="${contextPath}/themes/jquery.mobile-1.4.2.min.css" />
<link rel="stylesheet" href="${contextPath}/themes/jquery.mobile.datepicker.css" />

<script src="${contextPath}/jscripts/jquery.js"></script>
<!--<script src="${contextPath}/jscripts/jquery-ui.min.js"></script>-->

<script src="${contextPath}/jscripts/jquery.mobile-1.4.2.min.js"></script>
<script src="${contextPath}/jscripts/jquery.ui.datepicker.js"></script>
<script src="${contextPath}/jscripts/jquery.mobile.datepicker.js"></script>

<!-- Jquery Widgets - Grid -->
<link rel="stylesheet" href="${contextPath}/jqwidgets/styles/jqx.base.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxgrid.js"></script>
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxgrid.columnsresize.js"></script>
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxgrid.selection.js"></script> 
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxgrid.filter.js"></script> 
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxgrid.edit.js"></script> 
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxgrid.sort.js"></script> 
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxgrid.pager.js"></script>  
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="${contextPath}/jqwidgets/jqxgrid.aggregates.js"></script> 

<!-- Jquery Widgets - Grid -->
<style>
    div.ui-input-text {
        width: 200px !important
    }
    span.ui-btn-inner {
        white-space: nowrap !important;
        overflow: visible !important;
    }
    div.ui-field-contain {
        margin: 0 !important;
    }
    .securityCode div.ui-input-text {
        width: 100px !important
    }
    input {
        min-height: 1em !important;
    }
    label {
        width: 150px !important;
        margin: 0.5em 0.5em 0 !important;
        font-size: 14px !important;
    }
    select {
        min-height: 1em !important;
    }
    .ui-btn {
        padding: 0.5em 1em !important;
    }
    .ui-datepicker .ui-datepicker-prev, .ui-datepicker .ui-datepicker-next {
        padding: 1px 0 1px 2px !important;
    }
    .jqm-navmenu-link {
        padding: 1px 0 1px 2px !important;
    }
    .vttModalWindow {
        font-family: Verdana,Arial,sans-serif;
        color: #007D80;
        font-size: 14px !important;
    }
    .listTransaction{
        width: 100%;
    }
    .listTransaction td
    {
        border: 1px solid #C5C5C5;
        padding: 2px 5px 2px 2px;
        font-size: 13px !important;
    }
    .ui-footer {
        bottom: 0px;
        position: fixed !important;
        width: 100%;
        border: none;
    }
    
    
    
    
  #cssmenu {
  background: #007d80;
  list-style: none;
  margin-top: 2px;
  padding: 0;
  width: 180px;
  float: left;
  -webkit-box-shadow: 5px 3px 3px 0px #ccc;
    -moz-box-shadow: 5px 3px 3px 0px #ccc;
    box-shadow: 5px 3px 3px 0px #ccc;
}
#cssmenu li {
  font: 67.5% "Lucida Sans Unicode", "Bitstream Vera Sans", "Trebuchet Unicode MS", "Lucida Grande", Verdana, Helvetica, sans-serif;
  margin: 0;
  padding: 0;
  list-style: none;
}
#cssmenu a {
  background: #007d80;
  border-bottom: 1px solid #fff;
  color: #ccc;
  display: block;
  margin: 0;
  padding: 8px 12px;
  text-decoration: none;
  font-weight: normal;
}
#cssmenu a:hover {
  background-color: #2580a2;
  color: #fff;
  padding-bottom: 8px;
}
</style>
<script type="text/javascript">
    $(document).on('mobileinit', function() {
        $.mobile.pageLoadErrorMessage = "<sd:Property>loading</sd:Property>";
        $.mobile.loadingMessageTheme = "a";
        $.mobile.loader.prototype.options.theme = "a";
        $.mobile.defaultPageTransition = "slide";
    });
    </script>
    <script src="${contextPath}/jscripts/jquery.validate.min.js"></script>
<script src="${contextPath}/jscripts/token.js"></script>

<script type="text/javascript">
    /**
     * kiem tra gia tri nhap vao co trong hay khong
     */
    checkIsNull = function(id) {
        var value = document.getElementById(id).value;
        if (value == null || value == '') {
            return false;
        }
        return true;
    }

    checkLength = function(id, minLength, maxLength) {
        var value = document.getElementById(id).value;
        if (value.length > maxLength || value.length < minLength) {
            return false;
        }
        return true;
    }

    checkIsInteger = function(id) {
        var value = document.getElementById(id).value;
        var intRegex = /^\d+$/;
        if (!intRegex.test(value)) {
            return false;
        }
        return true;
    }
    getHompage = function() {
        window.location = '${contextPath}/' + 'Main.do';
    }

    function showModal() {
        $.mobile.loading('show', {
            text: "<sd:Property>loading</sd:Property>",
            textVisible: true,
            theme: 'a',
            textonly: false,
            html: ''
        });
    }

    function hideModal() {
        //        $(".modalWindow").remove();
        $.mobile.loading('hide');

    }
    var MERCHANTGW = {};
    var INPUTNAME = '';

    //0 - mobile, 1 - account number, 2 - name,account
    var INPUTTYPE = 0;
    MERCHANTGW.UTIL =
        {
        QueryString: function(a) {
            var sPageURL = window.location.search.substring(1);
            var sURLVariables = sPageURL.split('&');
            for (var i = 0; i < sURLVariables.length; i++)
            {
                var sParameterName = sURLVariables[i].split('=');
                if (sParameterName[0] == sParam)
                {
                    return sParameterName[1];
                }
            }
        },
        showContactDialog: function(name, type) {
            BANKPLUSWAP.UTIL.setInputName(name);
            BANKPLUSWAP.UTIL.setInputType(type);
            //$.mobile.changePage('Contact!showDialog.do', {transition: 'pop', role: 'popup'});   
        },
        getInputName: function() {
            return INPUTNAME;
        },
        setInputName: function(name) {
            INPUTNAME = name;
        },
        getInputType: function() {
            return INPUTTYPE;
        },
        setInputType: function(type) {
            INPUTTYPE = type;
        },
        getInputValueByName: function(name) {
            var val = $("input[name='" + name + "']").val();
            return val;
        },
        setInputValueByName: function(name, val) {
            $("input[name='" + name + "']").val(val);
        }

    };
    function exportExcel(type){
        location.href = "Export.do?type=" + type;
    }
    function escapeHtml(unsafe) {
        return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
    }
</script>

<link rel="stylesheet" href="${contextPath}/jscripts/selectv2/js/select2.min.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jscripts/selectv2/js/select2.min.js"></script> 
