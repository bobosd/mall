package com.jiezipoi.mall.controller.mall;

import com.jiezipoi.mall.config.JwtConfig;
import com.jiezipoi.mall.dto.MallUserDTO;
import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.entity.UserAddress;
import com.jiezipoi.mall.enums.UserStatus;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.exception.UnactivatedUserException;
import com.jiezipoi.mall.exception.UserNotFoundException;
import com.jiezipoi.mall.exception.VerificationCodeNotFoundException;
import com.jiezipoi.mall.service.JwtService;
import com.jiezipoi.mall.service.UserAddressService;
import com.jiezipoi.mall.service.UserService;
import com.jiezipoi.mall.service.VerificationCodeService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/user")
public class MallUserController {
    private final UserService userService;
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final VerificationCodeService verificationCodeService;
    private final UserAddressService userAddressService;

    public MallUserController(UserService userService, JwtConfig jwtConfig, JwtService jwtService, VerificationCodeService verificationCodeService, UserAddressService userAddressService) {
        this.userService = userService;
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
        this.verificationCodeService = verificationCodeService;
        this.userAddressService = userAddressService;
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
        if (!isValidNickName(nickname) || !isValidEmail(email) || !isValidPassword(password)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            userService.createUser(nickname, email, password);
        } catch (DuplicateKeyException e) {
            return new Response<>(CommonResponse.DATA_ALREADY_EXISTS);
        }

        return new Response<>(CommonResponse.SUCCESS);
    }

    @PostMapping("/login")
    @ResponseBody
    public Response<?> login(@RequestParam("email") String email,
                             @RequestParam("password") String password,
                             HttpServletResponse httpServletResponse) {
        if (email == null || password == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
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
    public String profilePage(Principal principal, ModelMap modelMap) {
        User user = userService.getUserByEmail(principal.getName());
        List<UserAddress> addresses = userAddressService.getUserAddresList(user.getUserId());
        addresses.forEach(userAddress -> {
            userAddress.setCreateTime(null);
            userAddress.setUserId(null);
        });
        modelMap.put("user", user);
        modelMap.put("addresses", addresses);
        return "mall/user-profile";
    }

    @GetMapping("/order")
    public String orderHistoryPage() {
        return "mall/user-order";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "mall/forgot-password";
    }

    @PostMapping("/forgot-password")
    @ResponseBody
    public Response<?> forgotPassword(@RequestParam("email") String email) {
        if (!isValidEmail(email)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            userService.sendResetPasswordEmail(email);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        } catch (UnactivatedUserException e) {
            return new Response<>(CommonResponse.FORBIDDEN);
        }
    }

    @GetMapping("/reset-password/{token}")
    public String resetPasswordPage(@PathVariable String token) {
        if (verificationCodeService.isValidCode(token)) {
            return "mall/reset-password";
        } else {
            return "mall/fallback";
        }

    }

    @PostMapping("/reset-password")
    @ResponseBody
    public Response<?> resetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        if (token == null || token.isBlank() || !isValidPassword(password)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            userService.resetPassword(token, password);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        } catch (CredentialsExpiredException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
    }

    private void setCredentialsCookie(User user, HttpServletResponse response) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateAndStoreRefreshToken(user);
        ResponseCookie accessCookie = jwtService.createJwtCookie(accessToken, jwtConfig.getAccessCookieName(), jwtConfig.getAccessTokenAge());
        ResponseCookie refreshCookie = jwtService.createJwtCookie(refreshToken, jwtConfig.getRefreshCookieName(), jwtConfig.getRefreshCookieAge());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private boolean isValidNickName(String nickname) {
        if (nickname == null) {
            return false;
        }
        Pattern nicknamePattern = Pattern.compile("^([a-zA-Z0-9]{3,})$");
        Matcher nicknameMatcher = nicknamePattern.matcher(nickname);
        return nickname.length() >= 3 && nicknameMatcher.matches();
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Pattern emailPattern = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Matcher emailMatcher = emailPattern.matcher(email);
        return emailMatcher.matches();
    }

    private boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=\\-\\[\\]{};:/.,<>?]).*$");
        Matcher passwordMatcher = passwordPattern.matcher(password);
        return password.length() >= 8 && passwordMatcher.matches();
    }
}