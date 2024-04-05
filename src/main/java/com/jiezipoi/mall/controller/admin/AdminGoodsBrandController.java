package com.jiezipoi.mall.controller.admin;

import com.jiezipoi.mall.entity.GoodsBrand;
import com.jiezipoi.mall.exception.ForeignKeyConstraintException;
import com.jiezipoi.mall.service.GoodsBrandService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import com.jiezipoi.mall.utils.dataTable.request.DataTableRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin/goods/brand")
public class AdminGoodsBrandController {
    private final GoodsBrandService goodsBrandService;

    public AdminGoodsBrandController(GoodsBrandService goodsBrandService) {
        this.goodsBrandService = goodsBrandService;
    }

    @PostMapping("/search-containing")
    @ResponseBody
    public Response<?> searchContaining(@RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        List<GoodsBrand> goodsBrandList = goodsBrandService.getGoodsBrandList(search);
        Response<List<GoodsBrand>> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(goodsBrandList);
        return response;
    }

    @PostMapping("/list")
    @ResponseBody
    public Response<?> list(@RequestBody DataTableRequest request) {
        try {
            List<GoodsBrand> list = goodsBrandService.getGoodsBrandList(request);
            int count = goodsBrandService.getTotalBrandCount();
            DataTableResult result = new DataTableResult(list, count);
            Response<DataTableResult> response = new Response<>(CommonResponse.SUCCESS);
            response.setData(result);
            return response;
        } catch (IllegalArgumentException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
    }

    @PostMapping("/create")
    @ResponseBody
    public Response<?> createBrand(String brandName) {
        if (brandName == null || brandName.trim().isBlank()) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            goodsBrandService.createGoodsBrand(brandName);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (DuplicateKeyException e) {
            return new Response<>(CommonResponse.DATA_ALREADY_EXISTS);
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Response<?> updateBrand(Long id, String brandName) {
        if (id == null || brandName == null || brandName.trim().isBlank()) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        try {
            goodsBrandService.updateGoodsBrand(id, brandName);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (IllegalArgumentException e) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        } catch (DuplicateKeyException e) {
            return new Response<>(CommonResponse.DATA_ALREADY_EXISTS);
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public Response<?> deleteBrand(Long id) {
        if (id == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            goodsBrandService.deleteGoodsBrand(id);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (NoSuchElementException e) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        } catch (ForeignKeyConstraintException e) {
            return new Response<>(CommonResponse.DATA_INTEGRITY_VIOLATION);
        }
    }
}