package com.jiezipoi.mall.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiezipoi.mall.entity.Goods;
import com.jiezipoi.mall.entity.GoodsCategory;
import com.jiezipoi.mall.service.GoodsCategoryService;
import com.jiezipoi.mall.service.GoodsService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.request.GoodsCategoryRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
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
        return "admin/goods-list";
    }

    @GetMapping("/goods/form")
    public String createGoodsPage(ModelMap modelMap, HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        Goods goods = goodsService.getUserTempGoods(userId);
        modelMap.addAttribute("goods", goods);
        if (goods != null && goods.getGoodsCategoryId() != null) {
            Long categoryId = goods.getGoodsCategoryId();
            List<GoodsCategory> categories = categoryService.getGoodsCategoryAndParent(categoryId);
            modelMap.addAttribute("category", categories);
        }
        return "admin/goods-create";
    }

    @GetMapping("/goods/edit/{id}")
    public String goodsEditPage(ModelMap modelMap, @PathVariable Long id) {
        Response<?> response = goodsService.getGoodsById(id);
        if (response.getCode() == 200) {
            Goods goods = (Goods) response.getData();
            Long categoryId = goods.getGoodsCategoryId();
            List<GoodsCategory> categories = categoryService.getGoodsCategoryAndParent(categoryId);
            modelMap.addAttribute("goods", goods);
            modelMap.addAttribute("category", categories);
            return "admin/goods-edit";
        } else {
            return "admin/fallback";
        }
    }

    @PostMapping("/goods/update")
    @ResponseBody
    public Response<?> updateGoods(@RequestParam("goods") String goodsJsonString,
                                   @RequestParam(value = "coverImage", required = false) MultipartFile file) {
        try {
            Goods goods = new ObjectMapper().readValue(goodsJsonString, Goods.class);
            return goodsService.updateGoods(goods, file);
        } catch (JsonProcessingException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
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

    @PostMapping("/goods/temp/upload/cover-image")
    @ResponseBody
    public Response<?> uploadCoverImage(@RequestParam("image") MultipartFile image, HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        return goodsService.saveTempCoverImage(image, userId);
    }

    @PostMapping("/goods/list")
    @ResponseBody
    public Response<?> list(@RequestBody GoodsCategoryRequest request) {
        return goodsService.list(request.getStart(), request.getLength(), request.getPath(), request.getCategoryLevel());
    }

    @PostMapping("/goods/temp/upload/details")
    @ResponseBody
    public Response<?> uploadTempDetails(@RequestParam("file") MultipartFile file,
                                         @RequestParam("goodsId") HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        try {
            String url = goodsService.saveTempDetailsFile(file, userId);
            Response<String> response = new Response<>(CommonResponse.SUCCESS);
            response.setData(url);
            return response;
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/goods/upload/details")
    @ResponseBody
    public Response<?> uploadDetails(@RequestParam("file") MultipartFile file,
                                     @RequestParam("goodsId") String goodsId) {
        try {
            String url = goodsService.saveDetailsFile(file, goodsId);
            Response<String> response = new Response<>(CommonResponse.SUCCESS);
            response.setData(url);
            return response;
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }
}