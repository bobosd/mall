package com.jiezipoi.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Random;

@Controller
public class MallController {
    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage() {
        Random random = new Random();
        random.nextDouble();
        return "mall/index";
    }
}
