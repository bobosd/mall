package com.jiezipoi.mall.controller.mall;

import com.jiezipoi.mall.config.CarouselConfig;
import com.jiezipoi.mall.dto.IndexGoodsCategoryDTO;
import com.jiezipoi.mall.dto.MallGoodsDTO;
import com.jiezipoi.mall.entity.Carousel;
import com.jiezipoi.mall.enums.IndexConfigType;
import com.jiezipoi.mall.service.CarouselService;
import com.jiezipoi.mall.service.GoodsCategoryService;
import com.jiezipoi.mall.service.IndexConfigService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MallController {
    private final CarouselService carouselService;
    private final CarouselConfig carouselConfig;
    private final GoodsCategoryService categoryService;
    private final IndexConfigService indexConfigService;

    public MallController(CarouselService carouselService, CarouselConfig carouselConfig, GoodsCategoryService categoryService,
                          IndexConfigService indexConfigService) {
        this.carouselService = carouselService;
        this.carouselConfig = carouselConfig;
        this.categoryService = categoryService;
        this.indexConfigService = indexConfigService;
    }

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(ModelMap modelMap) {
        List<Carousel> carousels = carouselService.getCarouselList();
        String baseExposeUrl = carouselConfig.getExposeUrl();
        List<IndexGoodsCategoryDTO> categories = categoryService.getIndexCategory();

        modelMap.put("carousels", carousels);
        modelMap.put("baseUrl", baseExposeUrl);
        modelMap.put("categories", categories);

        List<MallGoodsDTO> bestSelling =
                indexConfigService.getConfigGoodsForIndex(IndexConfigType.INDEX_GOODS_HOT.getType());
        List<MallGoodsDTO> newArrivals =
                indexConfigService.getConfigGoodsForIndex(IndexConfigType.INDEX_GOODS_NEW.getType());
        List<MallGoodsDTO> recommend =
                indexConfigService.getConfigGoodsForIndex(IndexConfigType.INDEX_GOODS_RECOMMEND.getType());
        modelMap.put("bestSelling", bestSelling);
        modelMap.put("newArrivals", newArrivals);
        modelMap.put("recommend", recommend);
        return "mall/index";
    }

    //get registration page
    @GetMapping("/signup")
    public String signupPage() {
        return "mall/signup";
    }

    //get login page
    @GetMapping("/login")
    public String loginPage() {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        return "mall/login";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    @GetMapping("/test")
    public String testPage() {
        return "mall/test";
    }
}
