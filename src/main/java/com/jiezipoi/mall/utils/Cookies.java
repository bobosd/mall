package com.jiezipoi.mall.utils;

import javax.servlet.http.Cookie;

public class Cookies {
    public static Cookie getValue(Cookie[] cookies, String key) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key))
                return cookie;
        }

        return null;
    }
}
