package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.entity.Goods;
import com.jiezipoi.mall.entity.GoodsCategory;
import com.jiezipoi.mall.service.GoodsCategoryService;
import com.jiezipoi.mall.service.GoodsService;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.request.GoodsCategoryRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class GoodsController {
    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsCategoryService categoryService;

    @GetMapping("/goods")
    public String goodsPage() {
        return "/admin/goods";
    }

    @GetMapping("/create-product")
    public String edit(ModelMap modelMap, HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        Goods goods = goodsService.getUserTempGoods(userId);
        modelMap.addAttribute("goods", goods);
        if (goods != null && goods.getGoodsCategoryId() != null) {
            Long categoryId = goods.getGoodsCategoryId();
            List<GoodsCategory> categories = categoryService.getGoodsCategoryAndParent(categoryId);
            modelMap.addAttribute("category", categories);
        }
        return "/admin/goods-edit";
    }

    @PostMapping("/goods/create")
    @ResponseBody
    public Response<?> createGoods(@RequestBody Goods goods, HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        return goodsService.createGoods(goods, userId);
    }

    @PostMapping("/goods/saveTempGoods")
    @ResponseBody
    public Response<?> saveTempGoods(Goods goods, HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        return goodsService.saveTempGoods(goods, userId);
    }

    @PostMapping("/goods/upload-cover-image")
    @ResponseBody
    public Response<?> uploadCoverImage(@RequestParam("image") MultipartFile image, HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        return goodsService.uploadCoverImage(image, userId);
    }

    @PostMapping("/goods/upload-goods-detail-image")
    @ResponseBody
    public Response<?> uploadGoodsDetailImage(@RequestParam("file") MultipartFile image,
                                              @RequestParam(value = "goodsId", required = false) Integer goodsId,
                                              HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        return goodsService.uploadGoodsDetailImage(image, goodsId, userId);
    }

    @PostMapping("/goods/list")
    @ResponseBody
    public Response<?> list(@RequestBody GoodsCategoryRequest request) {
        return goodsService.list(request.getStart(), request.getLength(), request.getPath(), request.getCategoryLevel());
    }
}