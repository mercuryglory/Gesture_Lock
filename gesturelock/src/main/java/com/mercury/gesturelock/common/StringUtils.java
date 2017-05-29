package com.mercury.gesturelock.common;

/**
 * 创建者:    Mercury
 * 创建时间:  2016/11/23
 * 描述:      ${TODO}
 */
public class StringUtils {

    public static boolean hasSame(String str,int num) {
        for (int i = 0; i < str.length(); i++) {
            if ((str.charAt(i))-'0' == num) {
                return true;
            }
        }
        return false;
    }
}
