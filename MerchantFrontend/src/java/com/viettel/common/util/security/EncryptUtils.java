/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util.security;

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
    
    public static void main(String[] args){
        System.out.println(hashPassword("123456A@adasdsfsdfsdfsdfsdfsd"));
    }
}
