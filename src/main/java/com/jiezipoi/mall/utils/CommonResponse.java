package com.jiezipoi.mall.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

public enum CommonResponse {
    SUCCESS("saved", 200),
    ERROR("save.failed", 404),
    DATA_NOT_EXIST("data.not.exists", 404),
    INVALID_DATA("invalid.data", 400),
    ;

    private MessageSource messageSource;
    private final String i18nKey;
    private int code;

    CommonResponse(String i18nKey, int code) {
        this.i18nKey = i18nKey;
        this.code = code;
    }

    public void setMessageSource(MessageSource i18n) {
        this.messageSource = i18n;
    }

    public String getMessage(Locale locale) {
        return this.messageSource.getMessage(i18nKey, null, locale);
    }

    @Component
    private static class i18nSourceInjector {
        @Autowired
        private MessageSource messageSource;

        @PostConstruct
        private void postConstruct() {
            for (CommonResponse response : CommonResponse.values()) {
                response.setMessageSource(messageSource);
            }
        }
    }
}

