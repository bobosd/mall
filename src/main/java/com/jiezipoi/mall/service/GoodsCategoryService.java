package com.jiezipoi.mall.service;

import com.jiezipoi.mall.controller.vo.IndexLevel1CategoryVO;
import com.jiezipoi.mall.controller.vo.IndexLevel2CategoryVO;
import com.jiezipoi.mall.controller.vo.IndexLevel3CategoryVO;
import com.jiezipoi.mall.dao.GoodsCategoryDao;
import com.jiezipoi.mall.entity.GoodsCategory;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import com.jiezipoi.mall.utils.dataTable.request.GoodsCategoryRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoodsCategoryService {
    private final GoodsCategoryDao goodsCategoryDao;
    private final HttpSession session;

    public GoodsCategoryService(GoodsCategoryDao goodsCategoryDao, HttpSession httpSession) {
        this.goodsCategoryDao = goodsCategoryDao;
        this.session = httpSession;
    }

    public Response<?> getCategoriesPage(GoodsCategoryRequest request) {
        if (request.getStart() == null ||
                request.getLength() == null ||
                request.getCategoryLevel() == null ||
                request.getParentId() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        List<GoodsCategory> goodsCategories = goodsCategoryDao.findGoodsCategoryList(
                request.getCategoryLevel(),request.getParentId());
        DataTableResult dataTableResult = new DataTableResult(goodsCategories, goodsCategories.size());
        Response<DataTableResult> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(dataTableResult);
        return response;
    }

    public Response<?> saveCategory(GoodsCategory category) {
        if (category.getCategoryName() == null ||
                category.getCategoryLevel() == null ||
                category.getPath() == null ||
                category.getCategoryRank() == null) {
            System.out.println(category.getCategoryLevel());
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        GoodsCategory temp = goodsCategoryDao.selectByLevelAndName(category.getCategoryLevel(), category.getCategoryName());
        if (temp != null) {
            return new Response<>(CommonResponse.DATA_ALREADY_EXISTS);
        }
        category.setCreateUser((int) session.getAttribute("userId"));
        if (goodsCategoryDao.insertSelective(category) > 0) {
            return new Response<>(CommonResponse.SUCCESS);
        } else {
            return new Response<>(CommonResponse.ERROR);
        }
    }

    public Response<?> updateGoodsCategory(GoodsCategory category) {
        if (category.getId() == null || category.getCategoryName() == null || category.getCategoryLevel() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        GoodsCategory temp = goodsCategoryDao.selectByPrimaryKey(category.getId());
        if (temp == null) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        }
        temp = goodsCategoryDao.selectByLevelAndName(category.getCategoryLevel(), category.getCategoryName());
        if (temp != null && !temp.getId().equals(category.getId())) {
            return new Response<>(CommonResponse.DATA_ALREADY_EXISTS);
        }
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser((int) session.getAttribute("userId"));
        if (goodsCategoryDao.updateByPrimaryKeySelective(category) > 0) {
            return new Response<>(CommonResponse.SUCCESS);
        } else {
            return new Response<>(CommonResponse.ERROR);
        }
    }

    public Response<?> getGoodsCategoryById(Long id) {
        if (id == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        GoodsCategory category = goodsCategoryDao.selectByPrimaryKey(id);
        if (category == null) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        } else {
            Response<GoodsCategory> response = new Response<>(CommonResponse.SUCCESS);
            response.setData(category);
            return response;
        }
    }

    public List<GoodsCategory> getGoodsCategoryAndParent(Long categoryId) {
        List<GoodsCategory> result = new ArrayList<>();
        GoodsCategory category = goodsCategoryDao.selectByPrimaryKey(categoryId);
        if (category != null) {
            long[] ids = category.getParentIdAsArray();
            result = goodsCategoryDao.selectByIds(ids);
        }
        return result;
    }

    public Response<?> deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        goodsCategoryDao.deleteBatch(ids);
        return new Response<>(CommonResponse.DELETE_SUCCESS);
    }

    public Response<?> selectByLevelAndParentIdsAndNumber(Long parentId, int categoryLevel) {
        List<GoodsCategory> categories = goodsCategoryDao.selectByLevelAndParentIdsAndNumber(parentId, categoryLevel, 0);
        Response<List<GoodsCategory>> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(categories);
        return response;
    }

    public List<IndexLevel1CategoryVO> getIndexCategory() {
        List<IndexLevel1CategoryVO> level1Categories = goodsCategoryDao.selectByCategoryLevel((byte) 1).stream()
                .map(IndexLevel1CategoryVO::new)
                .toList();
        List<IndexLevel2CategoryVO> level2Categories = goodsCategoryDao.selectByCategoryLevel((byte) 2)
                .stream()
                .map(IndexLevel2CategoryVO::new)
                .toList();
        List<IndexLevel3CategoryVO> level3Categories = goodsCategoryDao.selectByCategoryLevel((byte) 3)
                .stream()
                .map(IndexLevel3CategoryVO::new)
                .toList();

        //处理二级和三级的层级关系
        Map<Long, List<IndexLevel3CategoryVO>> l3CategoriesMap = level3Categories.stream()
                .collect(Collectors.groupingBy(IndexLevel3CategoryVO::getParentId));

        level2Categories.forEach(category -> {
            Long id = category.getCategoryId();
            if (l3CategoriesMap.containsKey(id)) {
                List<IndexLevel3CategoryVO> children = l3CategoriesMap.get(id);
                category.setChildren(children);
            }
        });

        //处理二级和三级的层级关系
        Map<Long, List<IndexLevel2CategoryVO>> l2CategoriesMap = level2Categories.stream()
                .collect(Collectors.groupingBy(IndexLevel2CategoryVO::getParentId));

        level1Categories.forEach(category -> {
            Long id = category.getCategoryId();
            if (l2CategoriesMap.containsKey(id)) {
                List<IndexLevel2CategoryVO> children = l2CategoriesMap.get(id);
                category.setChildren(children);
            }
        });

        return level1Categories;
    }
}
