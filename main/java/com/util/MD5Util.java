package com.util;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author 47550
 */
public class MD5Util {
    public static String getMD5String (String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main (String[] args) {
        System.out.println(getMD5String("qq123456"));

    }
}
