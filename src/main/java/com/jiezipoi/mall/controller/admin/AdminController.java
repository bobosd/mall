package com.jiezipoi.mall.controller.admin;

import com.jiezipoi.mall.enums.IndexConfigEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping(value = {"/index", "/", ""})
    public String index() {
        return "admin/admin-index";
    }

    @GetMapping("/carousel")
    public String carousel() {
        return "admin/carousel";
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