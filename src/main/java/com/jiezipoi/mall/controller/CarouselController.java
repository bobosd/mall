package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.entity.Carousel;
import com.jiezipoi.mall.service.CarouselService;
import com.jiezipoi.mall.utils.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class CarouselController {
    @Resource
    CarouselService carouselService;

    @GetMapping("/carousels")
    public String carouselPage(HttpServletRequest request) {
        return "admin/index_setting/carousel";
    }

    @RequestMapping(value = "/carousels/list", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> list(@RequestParam("start") Integer start, @RequestParam("length") Integer limit) {
        return carouselService.getCarouselPage(start, limit);
    }

    @RequestMapping(value = "/carousels/save", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> save(@RequestBody Carousel carousel) {
        return carouselService.saveCarousel(carousel);
    }

    public Result<?> update(@RequestBody Carousel carousel) {
        return carouselService.updateCarousel(carousel);
    }

    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Result<?> info(@PathVariable("id") Integer id) {
        return carouselService.getCarouselById(id);
    }

    @PostMapping("/carousels/delete")
    public Result<?> delete(@RequestBody Integer[] ids) {
        return carouselService.deleteBatch(ids);
    }
}
