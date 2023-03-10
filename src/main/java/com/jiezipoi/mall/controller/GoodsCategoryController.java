package com.jiezipoi.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/admin/product-category")
public class GoodsCategoryController {

    @GetMapping(value = "/product-category")
    public String categoryPage() {
        return "admin/product-category";
    }
}
