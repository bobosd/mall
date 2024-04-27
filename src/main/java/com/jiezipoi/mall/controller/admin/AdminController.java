package com.jiezipoi.mall.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping(value = {"/index", "/", ""})
    public String index() {
        return "admin/admin-index";
    }

    @GetMapping("/goods-brand")
    public String productBrandPage() {
        return "admin/goods-brand";
    }
}