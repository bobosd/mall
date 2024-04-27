package com.jiezipoi.mall.controller.admin;

import com.jiezipoi.mall.entity.Carousel;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.service.CarouselService;
import com.jiezipoi.mall.service.UserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import com.jiezipoi.mall.utils.dataTable.request.DataTableRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminCarouselController {
    private final CarouselService adminCarouselService;
    private final UserService userService;

    public AdminCarouselController(CarouselService adminCarouselService, UserService userService) {
        this.adminCarouselService = adminCarouselService;
        this.userService = userService;
    }

    @GetMapping("/carousel")
    public String carouselPage() {
        return "admin/carousel";
    }

    @RequestMapping(value = "/carousels/list", method = RequestMethod.POST)
    @ResponseBody
    public Response<?> list(@RequestBody DataTableRequest request) {
        DataTableResult result = adminCarouselService.getCarouselPage(request);
        Response<DataTableResult> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(result);
        return response;
    }

    @PreAuthorize("hasAuthority('carousel:write')")
    @PostMapping( "/carousels/save")
    @ResponseBody
    public Response<?> save(@RequestParam("image") MultipartFile image,
                            @RequestParam("goodsId") Long goodsId,
                            @RequestParam("order") Integer order,
                            Principal principal) {
        if (goodsId == null || order == null || order < 0) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            long userId = userService.getUserIdByEmail(principal.getName());
            adminCarouselService.createCarousel(image, goodsId, order, userId);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('carousel:write')")
    @PostMapping("/carousels/update")
    @ResponseBody
    public Response<?> update(@RequestParam(value = "image", required = false) MultipartFile image,
                              @RequestParam("carouselId") Long carouselId,
                              @RequestParam("goodsId") Long goodsId,
                              @RequestParam("order") Integer order,
                              Principal principal) {
        if (carouselId == null || goodsId == null || order == null || order < 0) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            Long userId = userService.getUserIdByEmail(principal.getName());
            adminCarouselService.updateCarousel(carouselId, image, goodsId, order, userId);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Response<?> info(@PathVariable("id") Long id) {
        if (id == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            Carousel carousel = adminCarouselService.getCarouselById(id);
            Response<Carousel> response = new Response<>(CommonResponse.SUCCESS);
            response.setData(carousel);
            return response;
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
    }

    @PreAuthorize("hasAuthority('carousel:write')")
    @PostMapping("/carousels/delete")
    @ResponseBody
    public Response<?> removeCarousel(@RequestParam("id") Long id) {
        if (id == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        adminCarouselService.removeCarousel(id);
        return new Response<>(CommonResponse.SUCCESS);
    }
}
