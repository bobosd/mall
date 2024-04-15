package com.jiezipoi.mall.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    private final TemplateEngine templateEngine;

    public AccessDeniedHandlerImpl(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        if (request.getMethod().equals("GET")) {
            response.setContentType("text/html");
            Context thymeleafContext = new Context();
            String page = templateEngine.process("mall/fallback", thymeleafContext);
            PrintWriter printWriter = response.getWriter();
            printWriter.println(page);
        } else {
            Response<String> responseContent = new Response<>(CommonResponse.FORBIDDEN);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responseContent);
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(jsonString);
        }
    }
}
