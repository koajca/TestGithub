
/*
 * Copyright(C) 2009 Viettel telecom
 *
 * EncryptManager.java, Feb 06, 2009
 */
package com.viettel.common.util.security;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.util.encoders.Base64;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.DataLengthException;

import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Hex;
import sun.misc.BASE64Encoder;

/**
 *
 * @author gameportal
 */
public final class EncryptManager {
    //logger

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EncryptManager.class);
    private static final char[] kDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
        'b', 'c', 'd', 'e', 'f'};

    public static String md5Encrypt(String password) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(password.getBytes(), 0, password.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return "";
    }

    public static String decryptRSA(String toDecrypt, String strPrivateKey) {

        RSAPrivateCrtKeyParameters rsaPrKey = getBCPrivateKeyFromString(strPrivateKey);
//        log.info("running decryptRSA ....");
        if (rsaPrKey == null) {
//            log.info("_RSAPrivateKey == null");
            return null;
        }

        try {
            AsymmetricBlockCipher theEngine = new RSAEngine();
            theEngine = new PKCS1Encoding(theEngine);
            theEngine.init(false, rsaPrKey);
            return new String(theEngine.processBlock(Base64.decode(toDecrypt), 0, Base64.decode(toDecrypt).length));
        } catch (Exception ex) {
            log.error(ex, ex);
//            log.error("decryptRSA", ex);
        }
        return null;
    }

    public static String encryptRSA(String toEncrypt, String strPublicKey) {
        RSAKeyParameters rsaPbKey = getBCPublicKeyFromString(strPublicKey);
        if (rsaPbKey == null) {
            return null;
        }

        try {
            AsymmetricBlockCipher theEngine = new RSAEngine();
            theEngine = new PKCS1Encoding(theEngine);
            theEngine.init(true, rsaPbKey);
            return new String(Base64.encode(theEngine.processBlock(toEncrypt.getBytes(), 0, toEncrypt.getBytes().length)));
        } catch (InvalidCipherTextException ex) {
            log.error("encryptRSA", ex);
        }
        return null;
    }

    static public String decrypt3DES_iv(ParametersWithIV keyiv, String data) {

//        CBCBlockCipher cipher;
        BufferedBlockCipher cipher;
        cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESedeEngine()), new ZeroBytePadding());
        cipher.init(false, keyiv);
        byte[] outText = new byte[cipher.getOutputSize(data.getBytes().length)];
        int outputLen = cipher.processBytes(data.getBytes(), 0, data.getBytes().length, outText, 0);
        try {
            cipher.doFinal(outText, outputLen);
        } catch (DataLengthException ex) {
            log.error(ex, ex);
        } catch (IllegalStateException ex) {
            log.error(ex, ex);
        } catch (InvalidCipherTextException ex) {
            log.error(ex, ex);
        }
        return new String(outText);
    }

    static public String encrypt3DES_iv(ParametersWithIV keyiv, String data) {

        BufferedBlockCipher cipher;
        cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESedeEngine()), new ZeroBytePadding());
        cipher.init(true, keyiv);
        byte[] outText = new byte[cipher.getOutputSize(data.getBytes().length)];
        int outputLen = cipher.processBytes(data.getBytes(), 0, data.getBytes().length, outText, 0);
        try {
            cipher.doFinal(outText, outputLen);
        } catch (DataLengthException ex) {
            log.error(ex, ex);
        } catch (IllegalStateException ex) {
            log.error(ex, ex);
        } catch (InvalidCipherTextException ex) {
            log.error(ex, ex);
        }
        return new String(outText);
    }

    private static RSAPrivateCrtKeyParameters getBCPrivateKeyFromString(String strPrivateKey) {
        try {
            PrivateKey prvKey = getPrivateKeyFromString(strPrivateKey);
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            RSAPrivateCrtKeySpec pkSpec = keyFac.getKeySpec(prvKey, RSAPrivateCrtKeySpec.class);
            RSAPrivateCrtKeyParameters priv = new RSAPrivateCrtKeyParameters(pkSpec.getModulus(), pkSpec.getPublicExponent(), pkSpec.getPrivateExponent(), pkSpec.getPrimeP(), pkSpec.getPrimeQ(), pkSpec.getPrimeExponentP(), pkSpec.getPrimeExponentQ(), pkSpec.getCrtCoefficient());
            return priv;
        } catch (Exception e) {
            log.error(e, e);

            return null;
        }

    }

    private static RSAKeyParameters getBCPublicKeyFromString(String strPublicKey) {
        try {
            PublicKey prvKey = getPublicKeyFromString(strPublicKey);

            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pkSpec = keyFac.getKeySpec(prvKey, RSAPublicKeySpec.class);
            RSAKeyParameters pub = new RSAKeyParameters(false, pkSpec.getModulus(), pkSpec.getPublicExponent());
            return pub;
        } catch (Exception e) {
            log.error(e, e);
            return null;
        }

    }

    public static String publicEncryptString(String message, String strPublicKey) {

        try {
            PublicKey publicKey = getPublicKeyFromString(strPublicKey);

            // 1. encode message
            String encodeMsg = encodeString(message, PASS_PHRASE);
//            String encodeMsg = new String(Base64.encode(message.getBytes()));
            // 2. encrypt message
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            byte[] input = encodeMsg.getBytes();

            Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            System.out.println("block size is_________" + cipher.getBlockSize());
            return byteToHex(cipher.doFinal(input));

        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return strPublicKey;
    }

    public static String privateDecryptString(String message, String strPrivateKey) {
        try {
            PrivateKey privateKey = getPrivateKeyFromString(strPrivateKey);

            // 1. decrypt message
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] input = hexToBytes(message);

            Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] plainText = cipher.doFinal(input);

            // 2. encode message
            return decodeString(new String(plainText), PASS_PHRASE.length());
//            return new String(Base64.decode(new String(plainText)));
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return strPrivateKey;
    }

    /**
     *
     * @param data
     * @return
     */
    public static String byteToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;

            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }

        return buf.toString();
    }

    public static byte[] hexToBytes(char[] hex) {
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127) {
                value -= 256;
            }
            raw[i] = (byte) value;
        }
        return raw;
    }

    public static byte[] hexToBytes(String hex) {
        return hexToBytes(hex.toCharArray());
    }

    /**
     * create a message signature by using private key
     *
     * @param data
     * @param strPrivateKey
     * @return
     */
    public static String createMsgSignature(String data, String strPrivateKey) {
        String encryptData = "";
        try {
            PrivateKey privateKey = getPrivateKeyFromString(strPrivateKey);
            java.security.Signature s = java.security.Signature.getInstance("SHA1withRSA");
            s.initSign(privateKey);
            s.update(data.getBytes());
            byte[] signature = s.sign();
            // Encrypt data
            encryptData = new String(Base64.encode(signature));
        } catch (Exception e) {
            log.error(e, e);
        }
        return encryptData;
    }

    /**
     * decrypt a message signature by using private key
     *
     * @param data
     * @param strPrivateKey
     * @return
     */
    public static boolean verifyMsgSignature(String encodeText, String strPublicKey, String input) {

        try {
            PublicKey publicKey = getPublicKeyFromString(strPublicKey);
            // decode base64
            byte[] base64Bytes = Base64.decode(encodeText);
            java.security.Signature sig = java.security.Signature.getInstance("SHA1WithRSA");
            sig.initVerify(publicKey);
            sig.update(input.getBytes());

            return sig.verify(base64Bytes);
        } catch (Exception e) {
            log.error(e, e);
        }
        return false;
    }

    public static PrivateKey getPrivateKeyFromString(String key) throws Exception {
        PrivateKey privateKey = null;
        try {
            PEMReader reader = new PEMReader(new StringReader(key), null, "SunRsaSign");
            KeyPair pemPair = (KeyPair) reader.readObject();

            reader.close();

            privateKey = (PrivateKey) pemPair.getPrivate();
        } catch (Exception e) {
            log.error(e, e);
        }
        return privateKey;

    }

    /**
     * create a public key from an encode string
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromString(String key) throws Exception {
        PublicKey publicKey = null;
        try {
            PEMReader reader = new PEMReader(new StringReader(key), null, "SunRsaSign");
            publicKey = (PublicKey) reader.readObject();

            reader.close();

        } catch (Exception e) {
            log.error(e, e);
        }
        return publicKey;
    }

//    public static RSAkeyObj genKeys() throws NoSuchAlgorithmException {
//
//        // Java Cryptography Provider
//        RSAkeyObj rsaobj = new RSAkeyObj();
//
//        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//        // Initialize and Generate
//        kpg.initialize(1024, new SecureRandom());
//        KeyPair keys = kpg.generateKeyPair();
//
//        // Retrieve keys
//        RSAPrivateKey privateKey = (RSAPrivateKey) keys.getPrivate();
//        RSAPublicKey publicKey = (RSAPublicKey) keys.getPublic();
////        System.out.println(publicKey.toString());
//        String privateKeyStr = new String(Base64.encode(privateKey.getEncoded()));
//        String publicKeyStr = new String(Base64.encode(publicKey.getEncoded()));
//        rsaobj.setPrivateKey(privateKeyStr);
//        rsaobj.setPublicKey(publicKeyStr);
//        return rsaobj;
//    }
//    public static byte[] hexToBytes(String hex) {
//        return hexToBytes(hex.toCharArray());
//    }
    public static String encodeString(String data, String sample) {

        StringBuffer res = new StringBuffer();

        if (data.length() > sample.length()) {
            for (int i = 0; i < sample.length(); i++) {
                res.append(sample.charAt(i));
                res.append(data.charAt(i));
            }
            res.append(data.substring(sample.length()));
        } else {
            for (int i = 0; i < data.length(); i++) {
                res.append(sample.charAt(i));
                res.append(data.charAt(i));
            }
            res.append(sample.substring(data.length()));
        }

        return new String(Base64.encode(res.toString().getBytes()));
    }

    public static String decodeString(String encode, int padd) {

        String res = new String(Base64.decode(encode));
        char[] item = new char[res.length() - padd];
        int j = 0;
        if (padd * 2 < res.length()) {
            // sample < data
            for (int i = 0; i < padd * 2; i++) {
                if (i % 2 == 1) {
                    item[j++] = res.charAt(i);
                }
            }
            for (int i = padd * 2; i < res.length(); i++) {
                item[j++] = res.charAt(i);
            }
        } else {
            System.out.println("padd = " + padd + " & len = " + res.length());
            // sample > data
            for (int i = 0; i < (res.length() - padd) * 2; i++) {
                if (i % 2 == 1) {
                    item[j++] = res.charAt(i);
                }
            }
        }

        return new String(item);
    }

    public static String encrypt3DES_iv(String message, String key) {

        byte[] skey = new byte[24];
//        System.arraycopy(Base64.decode(key), 0, skey, 0, skey.length);
        byte[] bKey = key.getBytes();
        System.arraycopy(bKey, 0, skey, 0, skey.length);
        System.out.println("key use to encryp: " + byteToHex(skey));
        byte[] iv = new byte[8];
        System.arraycopy(bKey, skey.length, iv, 0, iv.length);
        System.out.println("IV use to encryp: " + byteToHex(iv));

        ParametersWithIV keyiv = new ParametersWithIV(new KeyParameter(skey), iv);

        byte[] data = null;
        try {
            data = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.debug("ex", ex);
        }
        if (data != null) {
            BufferedBlockCipher cipherIV;
            cipherIV = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESedeEngine()), new ZeroBytePadding());
            cipherIV.init(true, keyiv);
            byte[] outText = new byte[cipherIV.getOutputSize(data.length)];
            int outputLen = cipherIV.processBytes(data, 0, data.length, outText, 0);
            try {
                cipherIV.doFinal(outText, outputLen);
            } catch (DataLengthException ex) {
                log.debug("ex", ex);
            } catch (IllegalStateException ex) {
                log.debug("ex", ex);
            } catch (InvalidCipherTextException ex) {
                log.debug("ex", ex);
            } catch (Exception ex) {
                log.debug("encrypt 3des error:" + ex);
            }
            return byteToHex(outText);
        } else {
            return "";
        }
    }

    public static String decrypt3DES_iv(String data, String key) {

        byte[] skey = new byte[24];
        byte[] bKey = key.getBytes();
        System.arraycopy(bKey, 0, skey, 0, skey.length);
        System.out.println("key use to decryp: " + byteToHex(skey));
        byte[] iv = new byte[8];
        System.arraycopy(bKey, skey.length, iv, 0, iv.length);
        System.out.println("IV use to decryp: " + byteToHex(iv));

        ParametersWithIV keyiv = new ParametersWithIV(new KeyParameter(skey), iv);

        BufferedBlockCipher cipherIV;
        cipherIV = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESedeEngine()), new ZeroBytePadding());
        cipherIV.init(false, keyiv);
        byte[] outText = new byte[cipherIV.getOutputSize(hexToBytes(data).length)];
        int outputLen = cipherIV.processBytes(hexToBytes(data), 0, hexToBytes(data).length, outText, 0);
        try {
            cipherIV.doFinal(outText, outputLen);
        } catch (DataLengthException ex) {
            System.out.println("ex" + ex);
        } catch (IllegalStateException ex) {
            System.out.println("ex" + ex);
        } catch (InvalidCipherTextException ex) {
            System.out.println("ex" + ex);
        }
        String returnText = "";
        try {
            returnText = new String(outText, "UTF-8");
            returnText = returnText.trim();
        } catch (UnsupportedEncodingException ex) {
            log.debug("ex", ex);
        }
        return returnText;
    }

    public static String DESKeyGen() {

        DESedeKeyGenerator keyGen = new DESedeKeyGenerator();
        keyGen.init(new KeyGenerationParameters(new SecureRandom(), 168));
        byte[] kB = keyGen.generateKey();
        System.out.println("skey:" + new String(Hex.encode(kB)));
        byte[] iv = new byte[8];
        SecureRandom r = new SecureRandom();
        r.nextBytes(iv);
        System.out.println("iv:" + new String(Hex.encode(iv)));
        if (kB.length != 24) {
            System.out.println("DESede bit key wrong length.");
        }
        byte[] sk = new byte[32];
        System.arraycopy(kB, 0, sk, 0, kB.length);
        System.arraycopy(iv, 0, sk, kB.length, iv.length);
        System.out.println("sk:" + new String(Hex.encode(sk)));
        return byteToHex(sk);
    }

//    public static void main(String[] args) {
////        String key = DESKeyGen();
////        System.out.println("key is: " + key);
////        key = "d957fd80f276ef80109d864c524a6d58370834f29b6dd9700000000000000000";
//////        System.out.println("encrypt: " + encrypt3DES_iv("hohohehe", key));
////        String xxx = decrypt3DES_iv(encrypt3DES_iv("hohohehsdf", key), key);
////
////        System.out.println("decrypt: " + xxx + " length" + xxx.length());
////                                MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIgynd6pvlCFd+/2QH9bIIZAwkUpwW3z73Nv1HiMQ8HLEig3sXsck7eYDJMtEXa69IXzVhZlrF7S5wnhHgwfk98b/OkQJhMAs2BQb6/EbqOytidYUA+r1fFS48EE0tR55JQVuwwV46/w4a4yu37LHV3VIQiDPwaSb49xOSOjaz+wIDAQAB
////                            MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIgynd6pvlCFd+/2QH9bIIZAwkUpwW3z73Nv1HiMQ8HLEig3sXsck7eYDJMtEXa69IXzVhZlrF7S5wnhHgwfk98b/OkQJhMAs2BQb6/EbqOytidYUA+r1fFS48EE0tR55JQVuwwV46/w4a4yu37LHV3VIQiDPwaSb49xOSOjaz+wIDAQAB
//        String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIgynd6pvlCFd+/2QH9bIIZAwkUpwW3z73Nv1HiMQ8HLEig3sXsck7eYDJMtEXa69IXzVhZlrF7S5wnhHgwfk98b/OkQJhMAs2BQb6/EbqOytidYUA+r1fFS48EE0tR55JQVuwwV46/w4a4yu37LHV3VIQiDPwaSb49xOSOjaz+wIDAQAB";
//        String privatekey = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAIiDKd3qm+UIV37/ZAf1sghkDCRSnBbfPvc2/UeIxDwcsSKDexexyTt5gMky0Rdrr0hfNWFmWsXtLnCeEeDB+T3xv86RAmEwCzYFBvr8Ruo7K2J1hQD6vV8VLjwQTS1HnklBW7DBXjr/DhrjK7fssdXdUhCIM/BpJvj3E5I6NrP7AgMBAAECgYEAh7KqWXgvA1ppb0p7qxXmpUSedTTRCENemevFlegz+PUkd4RvRbxqpn4/MMEc68UVdkF+FJXkQb9lXHkkmGD5WdBJNaezLx2j0fg1xSGA1DGY3MO1v7eFm4NWFNGU7GH2rbdVl/AsJRhFB6tCQM33qZWxWR92zVYermjO+tpjVkECQQC/ANniI283NSj4bSyorkyV3nrDxI//vgw4cKkU4jn14qb+vNjivNwvkicM9SVNNBElBWE29Fz67cKe3T854FXLAkEAtvdcrGJRSzqirIOvSQiQftdCGuojEwqhDOldilbw3x32YMFxTp3kPli4ORtPBknoKO9YTeCDz4FlyP7Arg7UkQJBAJ1fqCZpJ8OHK4C1A2zgX/3D18bhd/wxrIP8X3PzieoMm6ecFd+L2Kfhdgd5HQjlc5LbQeQzp0ChEhAvH18idc0CQQCgi5kT4gl3O3I2ci5CpXM0+WkV+NDiCneavsLHetZM0Ru5dsQXuc/nLS4pXd8Po5ZFJQ+U9iZy1BYeMfucP4MhAkEAs+WEa20JNF15lLBS5egZ/tAgbQXLF6BZF8pXB1usnUho4PJ2wu1eJtQNT6oR66xrpcUXCJSeG7COZSHHsWfw/Q==";
//
//        try {
//            SecureRandom random = new SecureRandom();
//            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
////            System.out.println("provider is" + generator.getProvider().values().toString());
//            generator.initialize(1024, random);
//
//            KeyPair pair = new KeyPair(getPublicKeyFromString(publickey), getPrivateKeyFromString(privatekey));
//
//            PEMWriter privWriter = new PEMWriter(new FileWriter("/root/privatekey.txt"));
//            privWriter.writeObject(pair);
//            privWriter.close();
//
//            privWriter = new PEMWriter(new FileWriter("/root/publickey.txt"));
//            privWriter.writeObject(pair.getPublic());
//            privWriter.close();
//
//
//            PEMReader reader = new PEMReader(new StringReader("-----BEGIN RSA PRIVATE KEY-----\n"
//                    + "MIICXwIBAAKBgQCIgynd6pvlCFd+/2QH9bIIZAwkUpwW3z73Nv1HiMQ8HLEig3sX\n"
//                    + "sck7eYDJMtEXa69IXzVhZlrF7S5wnhHgwfk98b/OkQJhMAs2BQb6/EbqOytidYUA\n"
//                    + "+r1fFS48EE0tR55JQVuwwV46/w4a4yu37LHV3VIQiDPwaSb49xOSOjaz+wIDAQAB\n"
//                    + "AoGBAIeyqll4LwNaaW9Ke6sV5qVEnnU00QhDXpnrxZXoM/j1JHeEb0W8aqZ+PzDB\n"
//                    + "HOvFFXZBfhSV5EG/ZVx5JJhg+VnQSTWnsy8do9H4NcUhgNQxmNzDtb+3hZuDVhTR\n"
//                    + "lOxh9q23VZfwLCUYRQerQkDN96mVsVkfds1WHq5ozvraY1ZBAkEAvwDZ4iNvNzUo\n"
//                    + "+G0sqK5Mld56w8SP/74MOHCpFOI59eKm/rzY4rzcL5InDPUlTTQRJQVhNvRc+u3C\n"
//                    + "nt0/OeBVywJBALb3XKxiUUs6oqyDr0kIkH7XQhrqIxMKoQzpXYpW8N8d9mDBcU6d\n"
//                    + "5D5YuDkbTwZJ6CjvWE3gg8+BZcj+wK4O1JECQQCdX6gmaSfDhyuAtQNs4F/9w9fG\n"
//                    + "4Xf8MayD/F9z84nqDJunnBXfi9in4XYHeR0I5XOS20HkM6dAoRIQLx9fInXNAkEA\n"
//                    + "oIuZE+IJdztyNnIuQqVzNPlpFfjQ4gp3mr7Cx3rWTNEbuXbEF7nP5y0uKV3fD6OW\n"
//                    + "RSUPlPYmctQWHjH7nD+DIQJBALPlhGttCTRdeZSwUuXoGf7QIG0FyxegWRfKVwdb\n"
//                    + "rJ1IaODydsLtXibUDU+qEeusa6XFFwiUnhuwjmUhx7Fn8P0=\n"
//                    + "-----END RSA PRIVATE KEY-----"), null, "SunRsaSign");
//            KeyPair pemPair = (KeyPair) reader.readObject();
//
//            reader.close();
//
//
//
//            RSAPrivateKey privateKey = (RSAPrivateKey) pemPair.getPrivate();
//            RSAPublicKey publicKey = (RSAPublicKey) pemPair.getPublic();
//
//            String privateKeyStr = new String(Base64.encode(privateKey.getEncoded()));
//            String publicKeyStr = new String(Base64.encode(publicKey.getEncoded()));
//
//
//            RSAkeyObj rsaobj = new RSAkeyObj();
//            rsaobj.setPrivateKey(privateKeyStr);
//            rsaobj.setPublicKey(publicKeyStr);
//            System.out.println("public key hehehe:" + rsaobj.getPublicKey());
//            System.out.println("private key hohoho:" + rsaobj.getPrivateKey());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    private static final byte[] HEX_CHAR = new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Helper function that dump an array of bytes in hex form
     *
     * @param buffer The bytes array to dump
     * @return A string representation of the array of bytes
     */
    public static final String dumpBytes(byte[] buffer) {
        if (buffer == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < buffer.length; i++) {
            sb.append("0x").append((char) (HEX_CHAR[(buffer[i] & 0x00F0) >> 4])).append(
                    (char) (HEX_CHAR[buffer[i] & 0x000F])).append(" ");
        }

        return sb.toString();
    }
    String dat = "test";
//    String key =
    private static String PASS_PHRASE = "zaq1xsw2cde3vfr4";
//    private static Logger log = Logger.getLogger(EncryptManager.class);

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
//        byte[] sha1hash = new byte[40];
        byte[] sha1hash;
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public static String SHA1Base64(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA");
//        byte[] sha1hash = new byte[40];
        byte[] sha1hash;
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();

        return encodeBase64(sha1hash);
    }

    private static String encodeBase64(byte[] dataToEncode) {
        BASE64Encoder b64e = new BASE64Encoder();
        String strEncoded = "";
        try {
            strEncoded = b64e.encode(dataToEncode);
        } catch (Exception e) {
            log.error(e, e);
        }
        return strEncoded;
    }

    public static byte[] hexaString2ByteArray(String hexvalue) {
        String s;
        int i = 0;
        byte[] result = new byte[hexvalue.length() / 2];

        while (hexvalue.length() > 0) {
            s = hexvalue.substring(0, 2);
            int n = Integer.parseInt(s, 16);
            byte c = (byte) n;
            result[i] = c;
            hexvalue = hexvalue.substring(2);
            i++;
        }

        return result;
    }

    //tripledes ECB,nopadding
    public static String encryptionWorksOkUsingJCE(String textKey, String text) throws Exception {

        String algorithm = "DESede";
        String transformation = "DESede/ECB/NoPadding";

        byte[] keyValue = hexaString2ByteArray(textKey);
        byte[] skey = new byte[24];
//        System.arraycopy(Base64.decode(key), 0, skey, 0, skey.length);
        System.arraycopy(keyValue, 0, skey, 0, skey.length);

        DESedeKeySpec keySpec = new DESedeKeySpec(skey);

        /* Initialization Vector of 8 bytes set to zero. */
//        IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        SecretKey key = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
        Cipher encrypter = Cipher.getInstance(transformation);
        encrypter.init(Cipher.ENCRYPT_MODE, key);

        byte[] input = text.getBytes("UTF-8");

        byte[] encrypted = encrypter.doFinal(input);
//        System.out.println(convertToHex(encrypted));
        return convertToHex(encrypted);
    }
}
