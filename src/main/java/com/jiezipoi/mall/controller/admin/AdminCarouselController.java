package com.jiezipoi.mall.controller.admin;

import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.service.CarouselService;
import com.jiezipoi.mall.service.UserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.Order;
import com.jiezipoi.mall.utils.dataTable.request.DataTableRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminCarouselController {
    private final CarouselService adminCarouselService;
    private final UserService userService;

    public AdminCarouselController(CarouselService adminCarouselService, UserService userService) {
        this.adminCarouselService = adminCarouselService;
        this.userService = userService;
    }

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
        return adminCarouselService.getCarouselPage(request.getStart(), request.getLength(), orderBy);
    }

    @PreAuthorize("hasAuthority('carousel:write')")
    @PostMapping( "/carousels/save")
    @ResponseBody
    public Response<?> save(@RequestParam("image") MultipartFile image,
                            @RequestParam("redirectUrl") String redirectUrl,
                            @RequestParam("order") Integer order,
                            Principal principal) {
        if (!StringUtils.hasLength(redirectUrl) || order == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            long userId = userService.getUserIdByEmail(principal.getName());
            adminCarouselService.createCarousel(image, redirectUrl, order, userId);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (IOException | NotFoundException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('carousel:write')")
    @PostMapping("/carousels/update")
    @ResponseBody
    public Response<?> update(@RequestParam(value = "image", required = false) MultipartFile image,
                              @RequestParam("id") Integer id,
                              @RequestParam("redirectUrl") String redirectUrl,
                              @RequestParam("order") Integer order,
                              Principal principal) {
        if (id == null || redirectUrl == null || order == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            Long userId = userService.getUserIdByEmail(principal.getName());
            adminCarouselService.updateCarousel(id, image, redirectUrl, order, userId);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Response<?> info(@PathVariable("id") Integer id) {
        return adminCarouselService.getCarouselById(id);
    }

    @PreAuthorize("hasAuthority('carousel:write')")
    @PostMapping("/carousels/delete")
    @ResponseBody
    public Response<?> delete(@RequestParam("ids[]") Integer[] ids) {
        return adminCarouselService.deleteBatch(ids);
    }
}
