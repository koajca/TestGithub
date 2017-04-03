<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<s:i18n name="com/viettel/config/Language">
    <div data-role="content" style="color: #007D80" >
        <tiles:insertAttribute name="body"/>
    </div>
</s:i18n>