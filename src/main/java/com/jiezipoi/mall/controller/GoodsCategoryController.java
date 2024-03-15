package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.dto.GoodsCategoryDTO;
import com.jiezipoi.mall.entity.GoodsCategory;
import com.jiezipoi.mall.service.GoodsCategoryService;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.request.GoodsCategoryRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class GoodsCategoryController {
    private final GoodsCategoryService service;

    public GoodsCategoryController(GoodsCategoryService service) {
        this.service = service;
    }

    @GetMapping(value = "/product-category")
    public String productCategory() {
        return "admin/product-category";
    }

    @PostMapping(value = "/product-category/list")
    @ResponseBody
    public Response<?> list(@RequestBody GoodsCategoryRequest request) {
        return service.getCategoriesPage(request);
    }

    @PostMapping("/product-category/save")
    @ResponseBody
    public Response<?> save(@RequestBody GoodsCategoryDTO goodsCategoryDTO) {
        return service.createCategory(goodsCategoryDTO);
    }

    @PostMapping("/product-category/update")
    @ResponseBody
    public Response<?> update(@RequestBody GoodsCategory category) {
        return service.updateGoodsCategory(category);
    }

    @GetMapping("/product-category/info")
    @ResponseBody
    public Response<?> info(Long id) {
        return service.getGoodsCategoryById(id);
    }

    @PostMapping("/product-category/delete")
    @ResponseBody
    public Response<?> delete(@RequestParam("ids[]") Integer[] ids) {
        return service.deleteBatch(ids);
    }

    @PostMapping("/product-category/listByLevelAndParent")
    @ResponseBody
    public Response<?> listByLevelAndParent(Integer level, Long parentId, String search) {
        System.out.println(search);
        return service.selectByLevelAndParentIdsAndNumber(parentId, level);
    }
}