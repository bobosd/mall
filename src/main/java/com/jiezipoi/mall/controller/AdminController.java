package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.enums.IndexConfigEnum;
import com.jiezipoi.mall.entity.AdminUser;
import com.jiezipoi.mall.service.AdminUserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



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
    public Response<?> login(@RequestParam("username") String username,
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
    public Response<?> setNickName(@RequestParam("nickname") String nickname,
                                   HttpSession session) {
        int id = (int) session.getAttribute("userId");
        Response<?> response = adminUserService.setNickName(nickname, id);
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
    public Response<?> updatePassword(@RequestParam("originalPassword") String originalPsw,
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
    public Response<?> upload(@RequestParam("file") MultipartFile file) {
        return new Response<>(CommonResponse.SUCCESS);
    }

    @GetMapping("/index-config")
    public String newArrivalsPage(HttpServletRequest request, @RequestParam("configType") int configType) {
        IndexConfigEnum indexConfigEnum = IndexConfigEnum.getIndexConfigEnumByType(configType);
        if (indexConfigEnum.equals(IndexConfigEnum.DEFAULT)) {
            return "admin/fallback";
        }
        request.setAttribute("path", indexConfigEnum.getName());
        request.setAttribute("configType", configType);
        return "admin/index-config";
    }

    @GetMapping("/goods-brand")
    public String productBrandPage() {
        return "admin/goods-brand";
    }
}