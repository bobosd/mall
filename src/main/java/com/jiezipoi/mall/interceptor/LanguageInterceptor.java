package com.jiezipoi.mall.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;


@Component
public class LanguageInterceptor extends LocaleChangeInterceptor {
    public LanguageInterceptor() {
        super();
        this.setParamName("lang");
    }
}
