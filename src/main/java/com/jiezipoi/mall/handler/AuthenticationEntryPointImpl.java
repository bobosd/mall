package com.jiezipoi.mall.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiezipoi.mall.utils.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证异常后应该返回的内容
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        int STATUS_CODE = HttpStatus.SC_UNAUTHORIZED; //401
        Response<String> responseContent = new Response<>("Authentication failed",
                STATUS_CODE,
                null);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(responseContent);
        response.setStatus(STATUS_CODE);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println(jsonString);
    }
}