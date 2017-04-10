/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util.security;

import com.viettel.security.PassTranformer;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since Mar 11, 2014
 * @version 1.0
 */
public class EncryptUtils {

    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        String hashed = BCrypt.hashpw(password, salt);
        return hashed;
    }

    public static boolean isHashedPasswordOK(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
    
    public static void main(String[] args) throws Exception{
        System.out.println(PassTranformer.decrypt("dd2395b5c6635d87e311e07490a43dbf45dc9a8ec2cc15db0ce1f124ff2fa92726e4ade62ec9b74483868c232b5f2577"));
    }
}
