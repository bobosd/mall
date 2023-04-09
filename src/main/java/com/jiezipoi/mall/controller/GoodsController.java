package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    @GetMapping("/create-product")
    public String edit(HttpServletRequest request) {
        return "/admin/goods-edit";
    }
}