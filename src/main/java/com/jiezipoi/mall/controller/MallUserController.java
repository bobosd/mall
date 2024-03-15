package com.jiezipoi.mall.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiezipoi.mall.config.JwtConfig;
import com.jiezipoi.mall.dto.MallUserDTO;
import com.jiezipoi.mall.entity.MallUser;
import com.jiezipoi.mall.enums.UserStatus;
import com.jiezipoi.mall.exception.VerificationCodeNotFoundException;
import com.jiezipoi.mall.service.JwtService;
import com.jiezipoi.mall.service.MallUserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/user")
public class MallUserController {
    private final MallUserService mallUserService;
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;

    public MallUserController(MallUserService mallUserService, JwtConfig jwtConfig, JwtService jwtService) {
        this.mallUserService = mallUserService;
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
    }

    @GetMapping("/activate-account/{token}")
    public String userActivation(@PathVariable String token, HttpServletResponse response, ModelMap modelMap) {
        try {
            MallUser user = mallUserService.activateUser(token);
            MallUserDTO mallUserDTO = new MallUserDTO(user);
            setCredentialsCookie(user, response);
            ObjectMapper objectMapper = new ObjectMapper();
            String userJSON = objectMapper.writeValueAsString(mallUserDTO);
            modelMap.put("mallUser", userJSON);
            return "/mall/user-activation-success";
        } catch (VerificationCodeNotFoundException e) {
            return "/mall/fallback";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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

        mallUserService.userSignUp(nickname, email, password);
        return new Response<>(CommonResponse.SUCCESS);
    }

    @PostMapping("/login")
    @ResponseBody
    public Response<?> login(@RequestParam("email") String email,
                             @RequestParam("password") String password,
                             HttpServletResponse httpServletResponse) {
        MallUser mallUser;
        try {
            mallUser = mallUserService.findUser(email, password);
            if (mallUser.getUserStatus() == UserStatus.UNACTIVATED) {
                return new Response<>(CommonResponse.FORBIDDEN);
            }
        } catch (BadCredentialsException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        setCredentialsCookie(mallUser, httpServletResponse);
        Response<MallUserDTO> response = new Response<>();
        response.setResponse(CommonResponse.SUCCESS);
        response.setData(new MallUserDTO(mallUser));
        return response;
    }

    @PostMapping("/logout")
    @ResponseBody
    public Response<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> optionalCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(jwtConfig.getRefreshCookieName()))
                .findFirst();
        if (optionalCookie.isPresent()) {
            Cookie refreshTokenCookie = optionalCookie.get();
            mallUserService.logout(refreshTokenCookie.getValue());
        }
        ResponseCookie accessCookie = jwtService.createJwtCookie("", jwtConfig.getAccessCookieName(), Duration.ZERO);
        ResponseCookie refreshCookie = jwtService.createJwtCookie("", jwtConfig.getRefreshCookieName(), Duration.ZERO);
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return new Response<>(CommonResponse.SUCCESS);
    }

    private void setCredentialsCookie(MallUser user, HttpServletResponse response) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateAndStoreRefreshToken(user.getEmail());
        ResponseCookie accessCookie = jwtService.createJwtCookie(accessToken, jwtConfig.getAccessCookieName(), jwtConfig.getAccessTokenAge());
        ResponseCookie refreshCookie = jwtService.createJwtCookie(refreshToken, jwtConfig.getRefreshCookieName(), jwtConfig.getRefreshCookieAge());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}