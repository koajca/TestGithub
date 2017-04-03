<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="com.viettel.common.util.ResourceBundleUtil"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="sd" uri="struts-dojo-tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<form id="loginForm" name="loginForm" method="POST" action="Index!onLogin.do?" novalidate="novalidate">
    <div style="width: 250px; margin: 0 auto;">
        <div id="errorMessage" style="display: none; " class="errorMessage"> </div>
        <label style="padding-left: 0"><sd:Property>login.merchantId</sd:Property></label>
        <div class="inputbox">
            <input id="loginForm.merchantId" type="text" value="" name="loginForm.merchantId" autocomplete="off" maxlength="15">
        </div>
        <label style="padding-left: 0"><sd:Property>login.username</sd:Property></label>
        <div class="inputbox">
            <input id="loginForm.username" type="text" value="" name="loginForm.username"  autocomplete="off" maxlength="50">
        </div>
        <label style="padding-left: 0"><sd:Property>login.password</sd:Property></label>
        <div class="inputbox">
            <input id="loginForm.password" type="password" value="" name="loginForm.password"  autocomplete="off" maxlength="50">
        </div>
        <label style="padding-left: 0"><sd:Property>login.security</sd:Property></label>
        <div data-inline="true">
            <input style="float: right; padding: 6px; margin-top: 5px" data-inline="true"  type="image" src="images/refresh.png" name="image" width="20" height="20" onclick="reloadCaptcha();
                return false;" />
            <img data-inline="true" id="imgCaptcha" src="${contextPath}/captcha?" boder="0" style="float: right; padding: 6px; margin-top: 0px"  height="40" valign="top" />
            <div class="securityCode"><input type="text" maxlength="6" autocomplete="off" data-inline="true" name="loginForm.securityCode" value="" id="loginForm.securityCode"></div>
            <div style="overflow: hidden; padding-right: .5em;">

            </div>
        </div>
        <div style="clear:both; margin-bottom: 8px;"></div>
        <div>
            <input type="submit" name="btnSubmit" id="btnSubmit" value="<sd:Property>login.submit</sd:Property>" data-theme="c" />
        </div>   
    </div>

</form>

<script>

    $("#errorMessage").html('');
    $("#errorMessage").hide();

    var errLogin = '${fn:escapeXml(errLogin)}';
    if (errLogin != '') {
        $("#errorMessage").html(errLogin);
        $("#errorMessage").show("fast");
        reloadCaptcha();
    }
    var errorMsgLogin = '${fn:escapeXml(errorMsgLogin)}';
    if (errorMsgLogin != "") {
        $("#errorMessage").html(errorMsgLogin);
        $("#errorMessage").show("fast");
    }
</script>
<script>
    var isLoginProcessing = false;
  
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
    //Khoi tao validate rule
    (function($, W, D)
    {
        var MERCHANTGW = {};

        MERCHANTGW.LOGIN =
            {
            setupFormValidation: function()
            {
                //form validation rules
                $("#loginForm").validate({
                    rules: {
                        'loginForm.merchantId': {
                            required: true,
                            rangelength: [1, 15],
                            regex: "^[A-Za-z0-9_]+$"
                        },
                        'loginForm.username': {
                            required: true,
                            rangelength: [1, 50],
                            regex: "^[A-Za-z0-9_]+$"
                        },
                        'loginForm.password': {
                            required: true,
                            rangelength: [1, 50]
                        },
                        'loginForm.securityCode': {
                            required: true,
                            rangelength: [1, 6],
                            regex: "^[A-Za-z0-9]+$"
                        }
                    },
                    messages: {
                        'loginForm.merchantId': {
                            required: "<sd:Property>ERROR_LOGIN_MERCHANTID_MISSING</sd:Property>",
                            rangelength: "<sd:Property>ERROR_LOGIN_MERCHANTID_LENGTH</sd:Property>"
                        },
                        'loginForm.username': {
                            required: "<sd:Property>ERROR_LOGIN_USERNAME_MISSING</sd:Property>",
                            rangelength: "<sd:Property>ERROR_LOGIN_USERNAME_LENGTH</sd:Property>"
                        },
                        'loginForm.password': {
                            required: "<sd:Property>ERROR_LOGIN_PASSWORD_MISSING</sd:Property>",
                            rangelength: "<sd:Property>ERROR_LOGIN_PASSWORD_LENGTH</sd:Property>"
                        },
                        'loginForm.securityCode': {
                            required: "<sd:Property>ERROR_LOGIN_SECURITY_MISSING</sd:Property>",
                            rangelength: "<sd:Property>ERROR_LOGIN_SECURITY_LENGTH</sd:Property>"
                        }
                    },
                    submitHandler: function(form) {
                        MERCHANTGW.LOGIN.DoLogin();
                    }
                });
            },
            
            onLoginSuccess: function(data, status)
            {
                if (typeof(data) === "string") {
                    $.mobile.changePage('Index.do');
                    return;
                }
                $('#btnSubmit').button('enable');
                isLoginProcessing = false;
                var customInfo = data.customInfo;
                if (customInfo[0] == 'success') {
                    var url = 'Main.do';
                    $.mobile.changePage(url);
                } else {
                    var errMsg = "<sd:Property>ERROR_LOGIN_INCORRECT</sd:Property>";
                    if (customInfo[1] != '') {
                        errMsg = customInfo[1];
                    }
                    $("#errorMessage").html(errMsg);
                    $("#errorMessage").show("fast");
                    reloadCaptcha();
                }
                
                hideModal();
            },

            onError: function(data, status)
            {
                $('#btnSubmit').button('enable');
                isLoginProcessing = false;
                MERCHANTGW.LOGIN.hideModal();
            },

            DoLogin: function() {
                if (isLoginProcessing) {
                    return;
                }
                showModal();
                $('#btnSubmit').button('disable');
                isLoginProcessing = true;
                $("#errorMessage").html('');
                $("#errorMessage").hide();

                var tokenParam = token.getTokenParam();
                var formData = $("#loginForm").serialize() + '&' + $.param(tokenParam);
                $.ajax({
                    type: "POST",
                    url: "Index!onLogin.do?",
                    cache: false,
                    data: formData,
                    success: MERCHANTGW.LOGIN.onLoginSuccess,
                    error: MERCHANTGW.LOGIN.onError
                });
                return false
            }
            
        }

        //when the dom has loaded setup form validation rules
        $(D).ready(function($) {
            MERCHANTGW.LOGIN.setupFormValidation();
        });


    })(jQuery, window, document);
    
    reloadCaptcha = function() {
        var randomnumber = Math.floor(Math.random() * 1100);
        var url = "${contextPath}/captcha?" + randomnumber;
        document.getElementById("imgCaptcha").src = url;
    }
    
</script>