package com.jiezipoi.mall.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiezipoi.mall.entity.Goods;
import com.jiezipoi.mall.entity.GoodsBrand;
import com.jiezipoi.mall.entity.GoodsCategory;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.service.GoodsBrandService;
import com.jiezipoi.mall.service.GoodsCategoryService;
import com.jiezipoi.mall.service.GoodsService;
import com.jiezipoi.mall.service.UserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import com.jiezipoi.mall.utils.dataTable.request.GoodsTableRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminGoodsController {
    private final GoodsService goodsService;
    private final GoodsCategoryService categoryService;
    private final GoodsBrandService goodsBrandService;
    private final UserService userService;

    public AdminGoodsController(GoodsService goodsService,
                                GoodsCategoryService categoryService,
                                GoodsBrandService goodsBrandService,
                                UserService userService) {
        this.goodsService = goodsService;
        this.categoryService = categoryService;
        this.goodsBrandService = goodsBrandService;
        this.userService = userService;
    }

    @GetMapping("/goods")
    public String goodsPage() {
        return "admin/goods-list";
    }

    @GetMapping("/goods/form")
    public String createGoodsPage(ModelMap modelMap, Principal principal) {
        try {
            long userId = userService.getUserIdByEmail(principal.getName());
            Goods goods = goodsService.getUserTempGoods(userId);
            modelMap.addAttribute("goods", goods);
            if (goods != null && goods.getGoodsCategoryId() != null) {
                Long categoryId = goods.getGoodsCategoryId();
                List<GoodsCategory> categories = categoryService.getGoodsCategoryAndParent(categoryId);
                modelMap.addAttribute("category", categories);
            }
            if (goods != null && goods.getGoodsBrandId() != null) {
                Long brandId = goods.getGoodsBrandId();
                GoodsBrand goodsBrand = goodsBrandService.getGoodsBrandById(brandId);
                modelMap.addAttribute("goodsBrand", goodsBrand);
            }
        } catch (IOException | ClassNotFoundException ignore) {
        }
        return "admin/goods-create";
    }

    @GetMapping("/goods/edit/{id}")
    public String goodsEditPage(ModelMap modelMap, @PathVariable Long id) {
        try {
            Goods goods = goodsService.getGoodsWithTagList(id);
            Long categoryId = goods.getGoodsCategoryId();
            List<GoodsCategory> categories = categoryService.getGoodsCategoryAndParent(categoryId);
            GoodsBrand goodsBrand = goodsBrandService.getGoodsBrandById(goods.getGoodsBrandId());
            modelMap.addAttribute("goodsBrand", goodsBrand);
            modelMap.addAttribute("goods", goods);
            modelMap.addAttribute("category", categories);
            return "admin/goods-edit";
        } catch (NullPointerException | NotFoundException e) {
            return "admin/fallback";
        }
    }

    @PreAuthorize("hasAuthority('goods:write')")
    @PostMapping("/goods/update")
    @ResponseBody
    public Response<?> updateGoods(@RequestParam("goods") String goodsJsonString,
                                   @RequestParam(value = "coverImage", required = false) MultipartFile file) {
        try {
            Goods goods = new ObjectMapper().readValue(goodsJsonString, Goods.class);
            if (!isValidGoodsToUpdate(goods)) {
                return new Response<>(CommonResponse.INVALID_DATA);
            }
            goodsService.updateGoods(goods, file);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (JsonProcessingException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('goods:write')")
    @PostMapping("/goods/create")
    @ResponseBody
    public Response<?> createGoods(@RequestBody Goods goods, Principal principal) {
        if (!isValidGoodsToCreate(goods)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        long userId = userService.getUserIdByEmail(principal.getName());
        try {
            goodsService.createGoods(goods, userId);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/goods/temp/upload/sync")
    @ResponseBody
    public Response<?> saveTempGoods(@RequestBody Goods goods, Principal principal) {
        long userId = userService.getUserIdByEmail(principal.getName());
        try {
            goodsService.saveTempGoods(goods, userId);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/goods/temp/upload/cover-image")
    @ResponseBody
    public Response<?> uploadCoverImage(@RequestParam("image") MultipartFile image, Principal principal) {
        try {
            long userId = userService.getUserIdByEmail(principal.getName());
            String url = goodsService.saveTempCoverImage(image, userId);
            Response<String> response = new Response<>(CommonResponse.SUCCESS);
            response.setData(url);
            return response;
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/goods/list")
    @ResponseBody
    public Response<?> list(@RequestBody GoodsTableRequest request) {
        DataTableResult result = goodsService.getGoodsPage(request);
        Response<DataTableResult> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(result);
        return response;
    }

    @PostMapping("/goods/temp/upload/details")
    @ResponseBody
    public Response<?> uploadTempDetails(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            long userId = userService.getUserIdByEmail(principal.getName());
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

    @PostMapping("/goods/search")
    @ResponseBody
    public Response<?> searchGoodsByName(@RequestParam("goodsName") String goodsName) {
        if (goodsName == null || goodsName.isBlank()) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        List<Goods> goods = goodsService.getGoodsListByGoodsName(goodsName);
        Response<List<Goods>> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(goods);
        return response;
    }


    //-- controller private methods --//

    private boolean isValidGoodsToCreate(Goods goods) {
        return goods.getGoodsName() != null &&
                !goods.getGoodsName().isBlank() &&
                goods.getGoodsIntro() != null &&
                !goods.getGoodsIntro().isBlank() &&
                goods.getGoodsBrandId() != null &&
                goods.getOriginalPrice() != null &&
                goods.getGoodsCategoryId() != null &&
                goods.getSellingPrice() != null &&
                goods.getStockNum() != null &&
                goods.getGoodsSellStatus() != null &&
                goods.getGoodsCoverImg() != null &&
                !goods.getGoodsCoverImg().isBlank() &&
                goods.getGoodsDetailContent() != null &&
                !goods.getGoodsDetailContent().isBlank();
    }

    private boolean isValidGoodsToUpdate(Goods goods) {
        return goods.getGoodsName() != null &&
                !goods.getGoodsName().isBlank() &&
                goods.getGoodsIntro() != null &&
                !goods.getGoodsIntro().isBlank() &&
                goods.getGoodsBrandId() != null &&
                goods.getOriginalPrice() != null &&
                goods.getGoodsCategoryId() != null &&
                goods.getSellingPrice() != null &&
                goods.getStockNum() != null &&
                goods.getGoodsSellStatus() != null &&
                goods.getGoodsDetailContent() != null &&
                !goods.getGoodsDetailContent().isBlank();
    }
}