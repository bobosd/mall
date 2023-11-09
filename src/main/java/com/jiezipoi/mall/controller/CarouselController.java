package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.service.CarouselService;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.request.DataTableRequest;
import com.jiezipoi.mall.utils.dataTable.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class CarouselController {
    @Resource
    CarouselService carouselService;

    @RequestMapping(value = "/carousels/list", method = RequestMethod.POST)
    @ResponseBody
    public Response<?> list(@RequestBody DataTableRequest request) {
        String orderBy = null;
        if (!request.getOrder().isEmpty()) {
            Order order = request.getOrder().get(0);
            Map<Integer, String> columnMap = new HashMap<>();
            columnMap.put(3, "carousel_rank");
            columnMap.put(4, "create_time");
            orderBy = order.toOrderByString(columnMap);
        }
        return carouselService.getCarouselPage(request.getStart(), request.getLength(), orderBy);
    }

    @RequestMapping(value = "/carousels/save", method = RequestMethod.POST)
    @ResponseBody
    public Response<?> save(@RequestParam("image") MultipartFile image,
                            @RequestParam("redirectUrl") String redirectUrl,
                            @RequestParam("order") Integer order,
                            HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        return carouselService.saveCarousel(image, redirectUrl, order, userId);
    }

    @PostMapping("/carousels/update")
    @ResponseBody
    public Response<?> update(@RequestParam(value = "image", required = false) MultipartFile image,
                              @RequestParam("id") Integer id,
                              @RequestParam("redirectUrl") String redirectUrl,
                              @RequestParam("order") Integer order,
                              HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        return carouselService.updateCarousel(id, image, redirectUrl, order, userId);
    }

    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Response<?> info(@PathVariable("id") Integer id) {
        return carouselService.getCarouselById(id);
    }

    @PostMapping("/carousels/delete")
    @ResponseBody
    public Response<?> delete(@RequestParam("ids[]") Integer[] ids) {
        return carouselService.deleteBatch(ids);
    }
}
