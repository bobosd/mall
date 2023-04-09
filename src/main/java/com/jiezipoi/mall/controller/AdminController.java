package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.entity.AdminUser;
import com.jiezipoi.mall.service.AdminUserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminUserService adminUserService;

    public AdminController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping(value = {"/index", "/"})
    public String index() {
        return "admin/admin-index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        if (request.getSession().getAttribute("userId") == null) {
            return "admin/login";
        } else {
            return "admin/admin-index";
        }
    }

    @ResponseBody
    @PostMapping("/login")
    public Result<?> login(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           HttpSession session) {
        return adminUserService.login(username, password, session);
    }

    @GetMapping("/user-setting")
    public String userSetting(ModelMap map, HttpSession session) {
        int id = (int) session.getAttribute("userId");
        AdminUser user = adminUserService.getUser(id);
        map.put("user", user);
        return "admin/user-setting";
    }

    @PostMapping("/setNickName")
    @ResponseBody
    public Result<?> setNickName(@RequestParam("nickname") String nickname,
                                 HttpSession session) {
        int id = (int) session.getAttribute("userId");
        Result<?> response = adminUserService.setNickName(nickname, id);
        if (response.getCode() == 0)
            session.setAttribute("nickname", nickname);
        return response;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("userId");
        request.getSession().removeAttribute("nickname");
        return "admin/login";
    }

    @GetMapping("/carousel")
    public String carousel() {
        return "admin/carousel";
    }

    @PostMapping("/updatePassword")
    @ResponseBody
    public Result<?> updatePassword(@RequestParam("originalPassword") String originalPsw,
                                    @RequestParam("newPassword") String newPsw,
                                    HttpSession session) {
        int id = (int) session.getAttribute("userId");
        return adminUserService.updatePassword(id, originalPsw, newPsw);
    }

    @GetMapping("/ckeditor")
    public String CKEditor() {
        return "admin/CKEditor";
    }

    @PostMapping("/ckeditor/upload")
    @ResponseBody
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        return new Result<>(CommonResponse.SUCCESS);
    }
}
