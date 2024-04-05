package com.jiezipoi.mall.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiezipoi.mall.utils.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 认证异常后应该返回的内容
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    private final TemplateEngine templateEngine;

    public AuthenticationEntryPointImpl(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        if (request.getMethod().equals("GET")) {
            response.setContentType("text/html");
            Context thymeleafContext = new Context();
            String page = templateEngine.process("mall/fallback", thymeleafContext);
            PrintWriter printWriter = response.getWriter();
            printWriter.println(page);
        } else {
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
}