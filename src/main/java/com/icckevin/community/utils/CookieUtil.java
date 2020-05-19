package com.icckevin.community.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @description: 用于获取指定cookie的值
 * @author: iccKevin
 * @create: 2020-05-19 11:19
 **/
public class CookieUtil {
    public static String getCookie(HttpServletRequest request, String name){
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空!");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}