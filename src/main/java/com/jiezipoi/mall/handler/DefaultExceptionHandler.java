package com.jiezipoi.mall.handler;

import com.jiezipoi.mall.utils.Response;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public Response<String> handleAuthenticationException(Exception e, HttpServletResponse response) throws Exception {
        //let AccessDeniedHandler process it
        if (e instanceof AccessDeniedException) {
            throw e;
        }
        e.printStackTrace();
        return new Response<>("Error interno del servidor", HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
    }
}
