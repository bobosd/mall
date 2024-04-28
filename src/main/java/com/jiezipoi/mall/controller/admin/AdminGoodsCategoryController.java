package com.jiezipoi.mall.controller.admin;

import com.jiezipoi.mall.dto.CreateGoodsCategoryDTO;
import com.jiezipoi.mall.entity.GoodsCategory;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.service.GoodsCategoryService;
import com.jiezipoi.mall.service.UserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import com.jiezipoi.mall.utils.dataTable.request.GoodsTableRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminGoodsCategoryController {
    private final GoodsCategoryService service;
    private final UserService userService;

    public AdminGoodsCategoryController(GoodsCategoryService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping(value = "/product-category")
    public String productCategory() {
        return "admin/product-category";
    }

    @PostMapping(value = "/product-category/list")
    @ResponseBody
    public Response<?> list(@RequestBody GoodsTableRequest request) {
        if (request == null ||
                request.getStart() == null ||
                request.getLength() == null ||
                request.getCategoryLevel() == null ||
                request.getParentId() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        DataTableResult page = service.getCategoriesPage(request);
        Response<DataTableResult> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(page);
        return response;
    }

    @PreAuthorize("hasAuthority('category:write')")
    @PostMapping("/product-category/create")
    @ResponseBody
    public Response<?> save(@RequestBody CreateGoodsCategoryDTO createGoodsCategoryDTO, Principal principal) {
        long userId = userService.getUserIdByEmail(principal.getName());
        GoodsCategory category = createGoodsCategoryDTO.getGoodsCategory();
        String parentPath = createGoodsCategoryDTO.getParentPath();
        if (category.getCategoryName() == null ||
                category.getCategoryLevel() == null ||
                category.getCategoryRank() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        if (parentPath == null || parentPath.isBlank() || countDotOccurrences(parentPath) != category.getCategoryLevel() - 1) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            service.createCategory(category, parentPath, userId);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (DuplicateKeyException e) {
            return new Response<>(CommonResponse.DATA_ALREADY_EXISTS);
        }

    }

    @PreAuthorize("hasAuthority('category:write')")
    @PostMapping("/product-category/update")
    @ResponseBody
    public Response<?> update(@RequestBody GoodsCategory category, Principal principal) {
        long userId = userService.getUserIdByEmail(principal.getName());
        if (category.getGoodsCategoryId() == null || category.getCategoryName() == null || category.getCategoryLevel() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            service.updateGoodsCategory(category, userId);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        } catch (DuplicateKeyException e) {
            return new Response<>(CommonResponse.DATA_ALREADY_EXISTS);
        }
    }

    @GetMapping("/product-category/info")
    @ResponseBody
    public Response<?> info(Long id) {
        if (id == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            GoodsCategory category = service.getGoodsCategoryById(id);
            Response<GoodsCategory> response = new Response<>(CommonResponse.SUCCESS);
            response.setData(category);
            return response;
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        }
    }

    @PreAuthorize("hasAuthority('category:write')")
    @PostMapping("/product-category/delete")
    @ResponseBody
    public Response<?> delete(@RequestParam("ids[]") Integer[] ids) {
        if (ids == null || ids.length < 1) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        service.deleteBatch(ids);
        return new Response<>(CommonResponse.DELETE_SUCCESS);
    }

    @PostMapping("/product-category/listByLevelAndParent")
    @ResponseBody
    public Response<?> listByLevelAndParent(Integer level, Long parentId) {
        if (level == null || parentId == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        List<GoodsCategory> list = service.selectByLevelAndParentIdsAndNumber(parentId, level);
        Response<List<GoodsCategory>> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(list);
        return response;
    }

    private int countDotOccurrences(String path) {
        String[] part = path.split("\\.");
        return part.length - 1;
    }
}