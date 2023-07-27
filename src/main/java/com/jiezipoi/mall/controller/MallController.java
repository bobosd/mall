package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.config.CarouselConfig;
import com.jiezipoi.mall.controller.vo.IndexLevel1CategoryVO;
import com.jiezipoi.mall.dao.CarouselDao;
import com.jiezipoi.mall.entity.Carousel;
import com.jiezipoi.mall.service.GoodsCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class MallController {
    @Resource
    private CarouselDao carouselDao;
    @Resource
    private CarouselConfig carouselConfig;

    @Resource
    private GoodsCategoryService categoryService;

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(ModelMap modelMap) {
        List<Carousel> carousels = carouselDao.findAllCarousel();
        String baseExposeUrl = carouselConfig.getExposeUrl();
        List<IndexLevel1CategoryVO> categories = categoryService.getIndexCategory();
        modelMap.put("carousels", carousels);
        modelMap.put("baseUrl", baseExposeUrl);
        modelMap.put("categories", categories);
        return "mall/index";
    }
}