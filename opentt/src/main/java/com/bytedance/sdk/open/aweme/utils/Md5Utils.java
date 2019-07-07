package com.bytedance.sdk.open.aweme.utils;

import java.security.MessageDigest;

/**
 * MD5 工具类
 * Created by yangzhirong on 2018/10/8.
 */
public class Md5Utils {

    private static final char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public Md5Utils() {
    }

    public static String hexDigest(String string) {
        String res = null;
        try {
            res = hexDigest(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String hexDigest(byte[] bytes) {
        String res = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bytes);
            byte[] tmp = messageDigest.digest();
            char[] charStr = new char[32];
            int k = 0;
            for (int i = 0; i < 16; ++i) {
                byte b = tmp[i];
                charStr[k++] = hexDigits[b >>> 4 & 15];
                charStr[k++] = hexDigits[b & 15];
            }
            res = new String(charStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
