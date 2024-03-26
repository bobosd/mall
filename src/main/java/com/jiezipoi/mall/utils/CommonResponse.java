package com.jiezipoi.mall.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

public enum CommonResponse {
    SUCCESS("success", 200),
    DELETE_SUCCESS("delete.success", 200),
    INVALID_DATA("invalid.data", 400),
    FORBIDDEN("access.denied", 403),
    ERROR("save.failed", 404),
    DATA_NOT_EXIST("data.not.exists", 404),
    DATA_ALREADY_EXISTS("data.already.exists", 409),
    DATA_INTEGRITY_VIOLATION("data.integrity.violation", 409),
    INTERNAL_SERVER_ERROR("error.server", 500),
    ;

    private MessageSource messageSource;
    private final String i18nKey;
    private final int code;

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

    public int getCode() {
        return code;
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

