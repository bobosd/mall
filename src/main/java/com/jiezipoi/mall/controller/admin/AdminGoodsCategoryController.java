package com.jiezipoi.mall.controller.admin;

import com.jiezipoi.mall.dto.CreateGoodsCategoryDTO;
import com.jiezipoi.mall.entity.GoodsCategory;
import com.jiezipoi.mall.service.GoodsCategoryService;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.request.GoodsTableRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminGoodsCategoryController {
    private final GoodsCategoryService service;

    public AdminGoodsCategoryController(GoodsCategoryService service) {
        this.service = service;
    }

    @GetMapping(value = "/product-category")
    public String productCategory() {
        return "admin/product-category";
    }

    @PostMapping(value = "/product-category/list")
    @ResponseBody
    public Response<?> list(@RequestBody GoodsTableRequest request) {
        return service.getCategoriesPage(request);
    }

    @PreAuthorize("hasAuthority('category:write')")
    @PostMapping("/product-category/create")
    @ResponseBody
    public Response<?> save(@RequestBody CreateGoodsCategoryDTO createGoodsCategoryDTO) {
        return service.createCategory(createGoodsCategoryDTO);
    }

    @PreAuthorize("hasAuthority('category:write')")
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

    @PreAuthorize("hasAuthority('category:write')")
    @PostMapping("/product-category/delete")
    @ResponseBody
    public Response<?> delete(@RequestParam("ids[]") Integer[] ids) {
        return service.deleteBatch(ids);
    }

    @PostMapping("/product-category/listByLevelAndParent")
    @ResponseBody
    public Response<?> listByLevelAndParent(Integer level, Long parentId) {
        return service.selectByLevelAndParentIdsAndNumber(parentId, level);
    }
}