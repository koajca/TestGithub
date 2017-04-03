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
    "compareValue",
    function(value, element, params) {
        var newPass = document.getElementById("newPassword").value.trim();
        var reNewPass = document.getElementById("reNewPassword").value.trim();
        if(newPass != reNewPass) return false;
        return true;
    },'Mật khẩu không trùng nhau.');
    
    $.validator.addMethod(
    "strongPassword",
    function(value, element, params) {
        var rex = "^(?=.{8,})(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W).*$";
	 var patt = new RegExp(rex);
         return patt.test(value);
    },'Mật khẩu phải bao gồm chữ thường, chữ hoa, số và ký tự đặc biệt.');
    //Khoi tao validate rule
    (function($, W, D)
    {
        var MERCHANTGW = {};

        MERCHANTGW.USERS =
            {
            setupFormValidation: function()
            {
                //form validation rules
                $("#changePassForm").validate({
                    rules: {
                        'oldPassword': {
                            required: true,
                            maxlength: 50,
                            strongPassword: true
                        },
                        'newPassword': {
                            required: true,
                            maxlength: 50,
                            strongPassword: true
                        },
                        'reNewPassword': {
                            required: true,
                            maxlength: 50,
                            compareValue: true,
                            strongPassword: true
                        }
                    },
                    messages: {
                        'oldPassword': {
                            required: "*",
                            maxlength: "Quá độ dài quy định"
                        },
                        'newPassword': {
                            required: "*",
                            maxlength: "Quá độ dài quy định"
                        },
                        'reNewPassword': {
                            required: "*",
                            maxlength: "Quá độ dài quy định"
                        }
                    },
                    submitHandler: function(form) {
                        MERCHANTGW.USERS.DoChangePass();
                    }
                });
            },
            
            onSearchSuccess: function(data, status)
            {
                $("#changePassForm")[0].reset();
                var customInfo = data.customInfo;
                if (customInfo[0] == 'success') {
                    alert('Đổi mật khẩu thành công');
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

            DoChangePass: function() {
                showModal();

                var tokenParam = token.getTokenParam();
                var formData = $("#changePassForm").serialize() + '&' + $.param(tokenParam);
                $.ajax({
                    type: "POST",
                    url: "Index!doChangePass.do?",
                    cache: false,
                    data: formData,
                    success: MERCHANTGW.USERS.onSearchSuccess,
                    error: MERCHANTGW.USERS.onError
                });
                return false
            }
            
        }

        //when the dom has loaded setup form validation rules
        $(D).ready(function($) {
            MERCHANTGW.USERS.setupFormValidation();
        });


    })(jQuery, window, document);
    onError = function(data, status)
    {
        hideModal();
    }
    
    
</script>

<form id="changePassForm" name="changePassForm"  novalidate="novalidate">
    <h3>Đổi mật khẩu</h3>
    <div id="errorMessage" style="display: none; " class="errorMessage"> </div>
    <table border="0" style="width: 80%; border-collapse: collapse; margin: 0; padding: 0">
        <tr>
            <td>
                <div data-role="fieldcontain">
                    <label for="oldPassword" >Mật khẩu cũ</label>
                    <input id="oldPassword" type="password" value="" name="oldPassword" data-mini="true" />
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div data-role="fieldcontain">
                    <label for="newPassword" >Mật khẩu mới</label>
                    <input id="newPassword" type="password" value="" name="newPassword" data-mini="true" />
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div data-role="fieldcontain">
                    <label for="reNewPassword" >Nhập lại mật khẩu</label>
                    <input id="reNewPassword" type="password" value="" name="reNewPassword" data-mini="true" />
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <input data-mini="true" data-inline="true" type="submit" id="btnChangePass" name="btnChangePass" value="Đổi mật khẩu" />
            </td>
        </tr>
    </table>
</form>
