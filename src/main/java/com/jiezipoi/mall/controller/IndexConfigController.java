package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.entity.IndexConfig;
import com.jiezipoi.mall.service.IndexConfigService;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.request.IndexConfigRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/index-config")
public class IndexConfigController {
    @Resource
    private IndexConfigService indexConfigService;

    @PostMapping(value = "/list")
    @ResponseBody
    public Response<?> list(@RequestBody IndexConfigRequest request) {
        return indexConfigService.getIndexConfig(request.getStart(), request.getLength(), request.getConfigType());
    }

    @PostMapping("/save")
    @ResponseBody
    public Response<?> save(@RequestBody IndexConfig indexConfig) {
        return indexConfigService.saveIndexConfig(indexConfig);
    }

    @PostMapping("/update")
    @ResponseBody
    public Response<?> update(@RequestBody IndexConfig indexConfig) {
        return indexConfigService.updateIndexConfig(indexConfig);
    }

    @PostMapping("/delete")
    @ResponseBody
    public Response<?> delete(@RequestBody long[] ids) {
        return indexConfigService.deleteBatch(ids);
    }
}