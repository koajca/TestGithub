/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.wsservice;

import com.viettel.bankplus.wsservice.business.BusinessManager;
import com.viettel.bankplus.wsservice.business.RequestData;
import com.viettel.common.util.StringProcess;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 * @author cuongdv3@viettel.com.vn
 * @since Nov 12, 2014
 * @version 1.0
 */
@WebService(serviceName = "ServiceAPI")
public class ServiceAPI {

    /**
     * This is a sample web service operation
     * @param txt
     * @return 
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }

    /**
     * Web service operation
     * @param cmd
     * @param encrypted
     * @param signature
     * @return String - Du lieu cau truc json
     */
    @WebMethod(operationName = "getData")
    public String getData(@WebParam(name = "cmd") String cmd, @WebParam(name = "encrypted") String encrypted, @WebParam(name = "signature") String signature) {
        if(cmd == null || cmd.trim().equals("") ||encrypted == null || encrypted.trim().equals("") ||signature == null || signature.trim().equals("")){
            return null;
        }
        RequestData reqData = new RequestData();
        reqData.setCmd(cmd.toUpperCase());
        reqData.setEncrypted(encrypted);
        reqData.setSignature(signature);
        reqData.setIpAddress(getClientIP());
        BusinessManager.getInstance().onRequest(reqData);
        return BusinessManager.getInstance().getResponse(reqData.getUuid());
    }

    @Resource
    WebServiceContext context;

    @WebMethod(operationName = "getClientIP")
    protected String getClientIP() {
        if (context != null) {
            HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        }
        return "";
    }
}
