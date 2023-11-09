package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.AdminUserDao;
import com.jiezipoi.mall.entity.AdminUser;
import com.jiezipoi.mall.utils.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AdminUserService {
    @Resource
    private AdminUserDao adminUserDao;
    private final MessageSource i18n;

    public AdminUserService(MessageSource i18n) {
        this.i18n = i18n;
    }

    public Response<?> login(String username, String password, HttpSession session) {
        boolean isValid = validateLoginData(username, password);
        Locale userLocale = LocaleContextHolder.getLocale();
        if (!isValid) {
            String message = i18n.getMessage("incorrect.username.or.password", null, userLocale);
            return new Response<String>(message, 1, null);
        }
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        AdminUser user = adminUserDao.login(username, md5Password);
        Response<String> response = new Response<>();
        if (user == null) {
            response.setCode(1);
            String message = i18n.getMessage("incorrect.username.or.password", null, userLocale);
            response.setMessage(message);
        } else {
            response.setCode(0);
            response.setMessage("valid");
            response.setData("admin/index");
            session.setAttribute("nickname", user.getNickName());
            session.setAttribute("userId", user.getId());
        }
        return response;
    }

    /**
     * return true if the username and password is valid
     * @param username entered by user
     * @param password entered by user
     * @return the values are valid or not
     */
    private boolean validateLoginData(String username, String password) {
        return username != null && password != null && !username.isEmpty() && !password.isEmpty();
    }

    public AdminUser getUser(int id) {
        return adminUserDao.getUserById(id);
    }

    public Response<?> setNickName(String nickName, int id) {
        nickName = nickName. trim();
        Locale userLocale = LocaleContextHolder.getLocale();
        Response<String> response = new Response<>();
        String pattern = "[A-Za-z0-9\\u4e00-\\u9fa5]+";
        if (nickName.isBlank() || !nickName.matches(pattern)) {
            String message = i18n.getMessage("invalid.nickname", null, userLocale);
            response.setCode(1);
            response.setMessage(message);
        } else {
            adminUserDao.updateNickname(id, nickName);
            String message = i18n.getMessage("saved", null, userLocale);
            response.setMessage(message);
            response.setCode(0);
        }
        return response;
    }

    public Response<?> updatePassword(int id, String originalPsw, String newPsw) {
        AdminUser user = adminUserDao.getUserById(id);
        Locale userLocale = LocaleContextHolder.getLocale();
        Response<?> response = new Response<>();
        String originalMD5 = DigestUtils.md5DigestAsHex(originalPsw.getBytes(StandardCharsets.UTF_8));
        String newMD5 = DigestUtils.md5DigestAsHex(newPsw.getBytes(StandardCharsets.UTF_8));
        if (!isValidPassword(newPsw)) {
            response.setCode(2);
            response.setMessage(i18n.getMessage("invalid.data", null, userLocale));
        } else if (user.getLoginPassword().equals(originalMD5)) {
            adminUserDao.updatePassword(id, newMD5);
            response.setCode(0);
            response.setMessage(i18n.getMessage("saved", null, userLocale));
        } else {
            response.setCode(1);
            response.setMessage(i18n.getMessage("incorrect.password", null, userLocale));
        }
        return response;
    }

    public boolean isValidPassword(String password) {
        String regex = "(?=.*[a-z]|.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=\\-\\[\\]{};:/.,<>?])[a-z|A-Z\\d~!@#$%^&*_+=\\-\\[\\]{};:/.,<>?]{8,16}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}