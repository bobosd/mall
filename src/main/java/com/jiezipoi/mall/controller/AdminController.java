package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.service.AdminUserService;
import com.jiezipoi.mall.utils.Result;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping("/index")
    public String index() {
        return "admin/admin-index";
    }

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }
    @ResponseBody
    @PostMapping("/login")
    public Result<?> login(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           HttpSession session) {
        return adminUserService.login(username, password, session);
    }
}
