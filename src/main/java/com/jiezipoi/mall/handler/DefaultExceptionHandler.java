package com.jiezipoi.mall.handler;

import com.jiezipoi.mall.utils.Response;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public Response<String> handleAuthenticationException(Exception e) {
        e.printStackTrace();
        return new Response<>("Error interno del servidor", HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
    }
}
