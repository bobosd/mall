package com.jiezipoi.mall.bean;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@Configuration
public class SweetAlertMessage {
    private final MessageSource messageSource;
    private final Locale locale = LocaleContextHolder.getLocale();

    public SweetAlertMessage(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String errorServer() {
        return messageSource.getMessage("error.server", null, locale);
    }

    public String saved() {
        return messageSource.getMessage("saved", null, locale);
    }

    public String created() {
        return messageSource.getMessage("created", null, locale);
    }

    public String deleteSuccess() {
        return messageSource.getMessage("deleteSuccess", null, locale);
    }
}
