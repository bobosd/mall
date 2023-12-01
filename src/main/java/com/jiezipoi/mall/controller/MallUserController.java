package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.config.SecurityConfig;
import com.jiezipoi.mall.service.MallUserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/user")
public class MallUserController {
    private final MallUserService mallUserService;
    private final SecurityConfig securityConfig;

    public MallUserController(MallUserService mallUserService, SecurityConfig securityConfig) {
        this.mallUserService = mallUserService;
        this.securityConfig = securityConfig;
    }

    @PostMapping("/signup")
    @ResponseBody
    public Response<?> userSignUp(@RequestParam("nickname") String nickname,
                                  @RequestParam("email") String email,
                                  @RequestParam("password") String password) {
        Response<String> response = null;
        Pattern nicknamePattern = Pattern.compile("[a-zA-Z\\d]+(\\s[a-zA-Z]+\\d+)*");
        Matcher nicknameMatcher = nicknamePattern.matcher(nickname);
        if (nickname.length() < 3 || !nicknameMatcher.matches()) {
            response = new Response<>(CommonResponse.INVALID_DATA);
            response.setMessage("invalid nickname");
        }

        Pattern emailPattern = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.matches()) {
            response = new Response<>(CommonResponse.INVALID_DATA);
            response.setMessage("invalid email");
        }

        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=\\-\\[\\]{};:/.,<>?]).*$");
        Matcher passwordMatcher = passwordPattern.matcher(password);
        if (password.length() < 8 || !passwordMatcher.matches()) {
            response = new Response<>(CommonResponse.INVALID_DATA);
            response.setMessage("invalid password");
        }

        if (response != null) {
            return response;
        }

        if (mallUserService.isExistingEmail(email)) {
            return new Response<>(CommonResponse.DATA_ALREADY_EXISTS);
        }

        mallUserService.userSignUp(nickname, email, password) ;
        return new Response<>(CommonResponse.SUCCESS);
    }

    @PostMapping("/login")
    @ResponseBody
    public Response<?> login(@RequestParam("email") String email,
                             @RequestParam("password") String password,
                             HttpServletResponse httpServletResponse) {
        String jwt;
        try {
            jwt = mallUserService.login(email, password);
        } catch (BadCredentialsException e) {
            jwt = null;
        }

        if (jwt == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        } else {
            long expireDuration = securityConfig.getSessionExpireDuration().toSeconds();
            ResponseCookie cookie = ResponseCookie.from("session-token", jwt)
                    .httpOnly(true) //不会被JS读取
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(expireDuration)
                    .secure(true) //https下才会被上传
                    .build();
            httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return new Response<>(CommonResponse.SUCCESS);
        }
    }
}
