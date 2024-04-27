package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.IndexConfigDao;
import com.jiezipoi.mall.dto.IndexConfigDTO;
import com.jiezipoi.mall.dto.MallGoodsDTO;
import com.jiezipoi.mall.entity.IndexConfig;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndexConfigService {
    private final IndexConfigDao indexConfigDao;
    private final GoodsService goodsService;

    public IndexConfigService(IndexConfigDao indexConfigDao, GoodsService goodsService) {
        this.indexConfigDao = indexConfigDao;
        this.goodsService = goodsService;
    }



    public Response<?> getIndexConfig(Integer start, Integer limit, Integer configType) {
        if (start == null || limit == null || configType == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        List<IndexConfigDTO> indexConfigs = indexConfigDao.findIndexConfigList(start, limit, configType);
        int count = indexConfigDao.getTotalIndexConfigs(configType);
        DataTableResult dataTableResult = new DataTableResult(indexConfigs, count);
        Response<DataTableResult> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(dataTableResult);
        return response;
    }

    public void saveIndexConfig(IndexConfig indexConfig) throws NotFoundException {
        if (!goodsService.isGoodsExist(indexConfig.getGoodsId())) {
            throw new NotFoundException();
        }
        indexConfigDao.insertSelective(indexConfig);
    }

    public void updateIndexConfig(IndexConfig indexConfig) throws NotFoundException {
        IndexConfig temp = indexConfigDao.selectByPrimaryKey(indexConfig.getId());
        if (temp == null) {
            throw new NotFoundException();
        }

        if (goodsService.isGoodsExist(indexConfig.getGoodsId())) {
            indexConfigDao.updateByPrimaryKeySelective(indexConfig);
        } else {
            throw new NotFoundException();
        }
    }

    public List<MallGoodsDTO> getConfigGoodsForIndex(int configType) {
        List<IndexConfig> indexConfigs = indexConfigDao.findIndexConfigByType(configType);
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
        return goodsService.getGoodsListById(idsArray)
                .stream()
                .map(MallGoodsDTO::new)
                .toList();
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