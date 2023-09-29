package com.jiezipoi.mall.service;

import com.jiezipoi.mall.controller.vo.IndexConfigGoodsVO;
import com.jiezipoi.mall.dao.GoodsDao;
import com.jiezipoi.mall.dao.IndexConfigDao;
import com.jiezipoi.mall.entity.Goods;
import com.jiezipoi.mall.entity.IndexConfig;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class IndexConfigService {
    private final IndexConfigDao indexConfigDao;
    private final GoodsDao goodsDao;
    private final MessageSource messageSource;

    public IndexConfigService(IndexConfigDao indexConfigDao, GoodsDao goodsDao, MessageSource messageSource) {
        this.indexConfigDao = indexConfigDao;
        this.goodsDao = goodsDao;
        this.messageSource = messageSource;
    }

    public Response<?> getIndexConfig(Integer start, Integer limit, Integer configType) {
        if (start == null || limit == null || configType == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        List<IndexConfig> indexConfigs = indexConfigDao.findIndexConfigList(start, limit, configType);
        int count = indexConfigDao.getTotalIndexConfigs(configType);
        DataTableResult dataTableResult = new DataTableResult(indexConfigs, count);
        Response<DataTableResult> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(dataTableResult);
        return response;
    }

    public Response<?> saveIndexConfig(IndexConfig indexConfig) {
        if (indexConfig.getConfigType() == null ||
                indexConfig.getConfigName().isBlank() ||
                indexConfig.getConfigRank() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        if (!hasValidGoodsId(indexConfig)) {
            Response<Object> response = new Response<>();
            Locale userLocale = LocaleContextHolder.getLocale();
            response.setResponse(CommonResponse.INVALID_DATA);
            response.setMessage(messageSource.getMessage("goods.not.exist", null, userLocale));
        }

        if (indexConfigDao.insertSelective(indexConfig) > 0) {
            return new Response<>(CommonResponse.SUCCESS);
        }
        return new Response<>(CommonResponse.ERROR);
    }

    public Response<?> updateIndexConfig(IndexConfig indexConfig) {
        System.out.println(indexConfig);
        if (indexConfig.getConfigType() == null ||
                indexConfig.getId() == null ||
                indexConfig.getConfigName().isBlank() ||
                indexConfig.getConfigRank() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        IndexConfig temp = indexConfigDao.selectByPrimaryKey(indexConfig.getId());
        if (temp == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        if (!hasValidGoodsId(indexConfig)) {
            Response<Object> response = new Response<>();
            Locale userLocale = LocaleContextHolder.getLocale();
            response.setResponse(CommonResponse.INVALID_DATA);
            response.setMessage(messageSource.getMessage("goods.not.exist", null, userLocale));
            return response;
        }

        indexConfigDao.updateByPrimaryKeySelective(indexConfig);
        return new Response<>(CommonResponse.SUCCESS);
    }

    private boolean hasValidGoodsId(IndexConfig indexConfig) {
        Goods goods = goodsDao.selectGoodsById(indexConfig.getGoodsId());
        return !(goods == null);
    }

    public List<IndexConfigGoodsVO> getConfigGoodsForIndex(int configType) {
        List<IndexConfig> indexConfigs = indexConfigDao.findIndexConfigByTypeAndNum(configType);
        if (indexConfigs.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> goodsIds = indexConfigs.stream()
                .map(IndexConfig::getGoodsId)
                .toList();
        long[] idsArray = new long[goodsIds.size()];
        for (int i = 0; i < goodsIds.size(); i++) {
            idsArray[i] = goodsIds.get(i);
        }
        //goods对象有太多信息，不能暴露给
        List<Goods> goodsList = goodsDao.selectGoodsByIds(idsArray);
        List<IndexConfigGoodsVO> goodsVOS = new ArrayList<>();
        goodsList.forEach(goods -> goodsVOS.add(new IndexConfigGoodsVO(goods)));
        return goodsVOS;
    }

    public Response<?> deleteBatch(long[] ids) {
        if (ids.length < 1) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        int deleted = indexConfigDao.deleteBatch(ids);
        if (deleted > 0) {
            return new Response<>(CommonResponse.DELETE_SUCCESS);
        } else {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        }
    }
}