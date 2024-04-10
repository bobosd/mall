package com.jiezipoi.mall.controller.admin;

import com.jiezipoi.mall.entity.IndexConfig;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.service.IndexConfigService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.request.IndexConfigRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Controller
@RequestMapping("/admin/index-config")
public class AdminConfigController {
    private final IndexConfigService indexConfigService;
    private final MessageSource messageSource;

    public AdminConfigController(IndexConfigService indexConfigService, MessageSource messageSource) {
        this.indexConfigService = indexConfigService;
        this.messageSource = messageSource;
    }

    @PostMapping(value = "/list")
    @ResponseBody
    public Response<?> list(@RequestBody IndexConfigRequest request) {
        return indexConfigService.getIndexConfig(request.getStart(), request.getLength(), request.getConfigType());
    }

    @PostMapping("/save")
    @ResponseBody
    public Response<?> save(@RequestBody IndexConfig indexConfig) {
        if (indexConfig.getConfigType() == null ||
                indexConfig.getId() == null ||
                indexConfig.getConfigName().isBlank() ||
                indexConfig.getConfigRank() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        try {
            indexConfigService.saveIndexConfig(indexConfig);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (NotFoundException e) {
            Response<String> response = new Response<>(CommonResponse.INVALID_DATA);
            Locale userLocale = LocaleContextHolder.getLocale();
            response.setMessage(messageSource.getMessage("goods.not.exist", null, userLocale));
            return response;
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Response<?> update(@RequestBody IndexConfig indexConfig) {
        if (indexConfig.getConfigType() == null ||
                indexConfig.getId() == null ||
                indexConfig.getConfigName().isBlank() ||
                indexConfig.getConfigRank() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        try {
            indexConfigService.updateIndexConfig(indexConfig);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (NotFoundException e) {
            Response<String> response = new Response<>(CommonResponse.INVALID_DATA);
            Locale userLocale = LocaleContextHolder.getLocale();
            response.setMessage(messageSource.getMessage("goods.not.exist", null, userLocale));
            return response;
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public Response<?> delete(@RequestBody long[] ids) {
        return indexConfigService.deleteBatch(ids);
    }
}