package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.AdminUserDao;
import com.jiezipoi.mall.entity.AdminUser;
import com.jiezipoi.mall.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class AdminUserService {
    @Resource
    private AdminUserDao adminUserDao;
    private MessageSource i18n;

    @Autowired
    public void i18nInjection(MessageSource source) {
        i18n = source;
    }
    public Result<?> login(String username, String password, HttpSession session) {
        boolean isValid = validateLoginData(username, password);
        Locale userLocale = LocaleContextHolder.getLocale();
        if (!isValid) {
            String message = i18n.getMessage("incorrect.username.or.password", null, userLocale);
            return new Result<String>(message, 1, null);
        }
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        AdminUser user = adminUserDao.login(username, md5Password);
        Result<String> result = new Result<>();
        if (user == null) {
            result.setCode(1);
            String message = i18n.getMessage("incorrect.username.or.password", null, userLocale);
            result.setMessage(message);
        } else {
            result.setCode(0);
            result.setMessage("valid");
            result.setData("/admin/index");
            session.setAttribute("nickname", user.getNickName());
            session.setAttribute("userId", user.getId());
        }
        return result;
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
}