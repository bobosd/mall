package com.jiezipoi.mall.controller.mall;

import com.jiezipoi.mall.config.JwtConfig;
import com.jiezipoi.mall.dto.MallUserDTO;
import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.enums.UserStatus;
import com.jiezipoi.mall.exception.UserNotFoundException;
import com.jiezipoi.mall.exception.VerificationCodeNotFoundException;
import com.jiezipoi.mall.service.JwtService;
import com.jiezipoi.mall.service.UserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/user")
public class MallUserController {
    private final UserService userService;
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;

    public MallUserController(UserService userService, JwtConfig jwtConfig, JwtService jwtService) {
        this.userService = userService;
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
    }

    @GetMapping("/activate-account/{token}")
    public String userActivation(@PathVariable String token, HttpServletResponse response) {
        try {
            if (token == null || token.isBlank()) {
                throw new VerificationCodeNotFoundException();
            }
            User user = userService.activateUser(token);
            setCredentialsCookie(user, response);
            return "mall/user-activation-success";
        } catch (VerificationCodeNotFoundException e) {
            System.out.println("verification failed");
            return "mall/fallback";
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

        if (userService.isExistingEmail(email)) {
            return new Response<>(CommonResponse.DATA_ALREADY_EXISTS);
        }

        userService.createUser(nickname, email, password);
        return new Response<>(CommonResponse.SUCCESS);
    }

    @PostMapping("/login")
    @ResponseBody
    public Response<?> login(@RequestParam("email") String email,
                             @RequestParam("password") String password,
                             HttpServletResponse httpServletResponse) {
        User user;
        try {
            user = userService.getUserByEmailAndPassword(email, password);
            if (user.getUserStatus() == UserStatus.UNACTIVATED) {
                return new Response<>(CommonResponse.FORBIDDEN);
            }
        } catch (BadCredentialsException | UserNotFoundException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        setCredentialsCookie(user, httpServletResponse);
        Response<MallUserDTO> response = new Response<>();
        response.setResponse(CommonResponse.SUCCESS);
        response.setData(new MallUserDTO(user));
        return response;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> optionalCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(jwtConfig.getRefreshCookieName()))
                .findFirst();
        if (optionalCookie.isPresent()) {
            Cookie refreshTokenCookie = optionalCookie.get();
            userService.logout(refreshTokenCookie.getValue());
        }
        ResponseCookie accessCookie = jwtService.createJwtCookie("", jwtConfig.getAccessCookieName(), Duration.ZERO);
        ResponseCookie refreshCookie = jwtService.createJwtCookie("", jwtConfig.getRefreshCookieName(), Duration.ZERO);
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "mall/user-profile";
    }

    @GetMapping("/order")
    public String orderHistoryPage() {
        return "mall/user-order";
    }

    private void setCredentialsCookie(User user, HttpServletResponse response) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateAndStoreRefreshToken(user);
        ResponseCookie accessCookie = jwtService.createJwtCookie(accessToken, jwtConfig.getAccessCookieName(), jwtConfig.getAccessTokenAge());
        ResponseCookie refreshCookie = jwtService.createJwtCookie(refreshToken, jwtConfig.getRefreshCookieName(), jwtConfig.getRefreshCookieAge());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}