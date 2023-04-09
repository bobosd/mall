package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.GoodsCategoryDao;
import com.jiezipoi.mall.entity.GoodsCategory;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Result;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import com.jiezipoi.mall.utils.dataTable.request.GoodsCategoryRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoodsCategoryService {
    private final GoodsCategoryDao goodsCategoryDao;
    private final HttpSession session;

    public GoodsCategoryService(GoodsCategoryDao goodsCategoryDao, HttpSession httpSession) {
        this.goodsCategoryDao = goodsCategoryDao;
        this.session = httpSession;
    }

    public Result<?> getCategoriesPage(GoodsCategoryRequest request) {
        if (request.getStart() == null ||
                request.getLength() == null ||
                request.getCategoryLevel() == null ||
                request.getParentId() == null) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }

        List<GoodsCategory> goodsCategories = goodsCategoryDao.findGoodsCategoryList(
                request.getCategoryLevel(),request.getParentId());
        DataTableResult dataTableResult = new DataTableResult(goodsCategories, goodsCategories.size());
        Result<DataTableResult> response = new Result<>(CommonResponse.SUCCESS);
        response.setData(dataTableResult);
        return response;
    }

    public Result<?> saveCategory(GoodsCategory category) {
        if (category.getCategoryName() == null ||
                category.getCategoryLevel() == null ||
                category.getParentId() == null ||
                category.getCategoryRank() == null) {
            System.out.println(category.getCategoryLevel());
            return new Result<>(CommonResponse.INVALID_DATA);
        }
        GoodsCategory temp = goodsCategoryDao.selectByLevelAndName(category.getCategoryLevel(), category.getCategoryName());
        if (temp != null) {
            return new Result<>(CommonResponse.DATA_ALREADY_EXISTS);
        }
        category.setCreateUser((int) session.getAttribute("userId"));
        if (goodsCategoryDao.insertSelective(category) > 0) {
            return new Result<>(CommonResponse.SUCCESS);
        } else {
            return new Result<>(CommonResponse.ERROR);
        }
    }

    public Result<?> updateGoodsCategory(GoodsCategory category) {
        if (category.getId() == null || category.getCategoryName() == null || category.getCategoryLevel() == null) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }
        GoodsCategory temp = goodsCategoryDao.selectByPrimaryKey(category.getId());
        if (temp == null) {
            return new Result<>(CommonResponse.DATA_NOT_EXIST);
        }
        temp = goodsCategoryDao.selectByLevelAndName(category.getCategoryLevel(), category.getCategoryName());
        if (temp != null && !temp.getId().equals(category.getId())) {
            return new Result<>(CommonResponse.DATA_ALREADY_EXISTS);
        }
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser((int) session.getAttribute("userId"));
        if (goodsCategoryDao.updateByPrimaryKeySelective(category) > 0) {
            return new Result<>(CommonResponse.SUCCESS);
        } else {
            return new Result<>(CommonResponse.ERROR);
        }
    }

    public Result<?> getGoodsCategoryById(Long id) {
        if (id == null) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }
        GoodsCategory category = goodsCategoryDao.selectByPrimaryKey(id);
        if (category == null) {
            return new Result<>(CommonResponse.DATA_NOT_EXIST);
        } else {
            Result<GoodsCategory> response = new Result<>(CommonResponse.SUCCESS);
            response.setData(category);
            return response;
        }
    }

    public Result<?> deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }
        goodsCategoryDao.deleteBatch(ids);
        return new Result<>(CommonResponse.DELETE_SUCCESS);
    }

    public Result<?> selectByLevelAndParentIdsAndNumber(Long parentId, int categoryLevel) {
        List<GoodsCategory> categories = goodsCategoryDao.selectByLevelAndParentIdsAndNumber(parentId, categoryLevel, 0);
        Result<List<GoodsCategory>> result = new Result<>(CommonResponse.SUCCESS);
        result.setData(categories);
        return result;
    }
}
