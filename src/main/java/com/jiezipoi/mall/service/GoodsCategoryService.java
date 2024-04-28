package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.GoodsCategoryDao;
import com.jiezipoi.mall.dto.IndexGoodsCategoryDTO;
import com.jiezipoi.mall.entity.GoodsCategory;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import com.jiezipoi.mall.utils.dataTable.Order;
import com.jiezipoi.mall.utils.dataTable.request.GoodsTableRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoodsCategoryService {
    private final GoodsCategoryDao goodsCategoryDao;

    public GoodsCategoryService(GoodsCategoryDao goodsCategoryDao) {
        this.goodsCategoryDao = goodsCategoryDao;
    }

    public DataTableResult getCategoriesPage(GoodsTableRequest request) {
        Order order = request.getOrder().get(0);
        Integer colNumber = order.getColumn();
        String dir = order.getDir();
        List<GoodsCategory> goodsCategories = goodsCategoryDao.findGoodsCategoryList(
                request.getCategoryLevel(), request.getParentId(), colNumber, dir);
        return new DataTableResult(goodsCategories, goodsCategories.size());
    }

    /**
     * 用于创建category，接受一个DTO，含有category对象和一个用于表示父类path对象
     * 无法直接获取path是因为无法知道被插入的id为多少，所以需要先插入，然后拼接父类id
     *
     * @param category 需要创建的category
     * @param parentPath 该category的路径
     * @param userId 创建的用户id
     */
    @Transactional
    public void createCategory(GoodsCategory category, String parentPath, long userId) {
        GoodsCategory temp = goodsCategoryDao.selectByCategoryName(category.getCategoryName());
        if (temp != null) {
            throw new DuplicateKeyException("Duplicated category name '" + category.getCategoryName() + "'");
        }
        category.setCreateUser(userId);
        goodsCategoryDao.insertSelective(category);
        long categoryId = category.getGoodsCategoryId();
        String path;
        if (parentPath == null || parentPath.isBlank()) {
            path = Long.toString(categoryId);
        } else {
            path = parentPath + "." + categoryId;
        }
        goodsCategoryDao.updatePathById(categoryId, path);
    }

    public void updateGoodsCategory(GoodsCategory category, long userId) throws NotFoundException {
        GoodsCategory temp = goodsCategoryDao.selectByPrimaryKey(category.getGoodsCategoryId());
        if (temp == null) {
            throw new NotFoundException();
        }
        temp = goodsCategoryDao.selectByCategoryName(category.getCategoryName());
        if (temp != null && temp.getGoodsCategoryId().equals(category.getGoodsCategoryId())) {
            throw new DuplicateKeyException("Duplicated category name '" + category.getCategoryName() + "'");
        }
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(userId);
        goodsCategoryDao.updateByPrimaryKeySelective(category);
    }

    public GoodsCategory getGoodsCategoryById(Long id) throws NotFoundException {
        GoodsCategory category = goodsCategoryDao.selectByPrimaryKey(id);
        if (category == null) {
            throw new NotFoundException();
        } else {
            return category;
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

    public void deleteBatch(Integer[] ids) {
        goodsCategoryDao.deleteBatch(ids);
    }

    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(Long parentId, int categoryLevel) {
        return goodsCategoryDao.selectByLevelAndParentIdsAndNumber(parentId, categoryLevel, 0);
    }

    public List<IndexGoodsCategoryDTO> getIndexCategory() {
        List<IndexGoodsCategoryDTO> level1Categories = goodsCategoryDao.selectByCategoryLevel(1)
                .stream()
                .map(IndexGoodsCategoryDTO::new)
                .toList();
        List<IndexGoodsCategoryDTO> level2Categories = goodsCategoryDao.selectByCategoryLevel(2)
                .stream()
                .map(IndexGoodsCategoryDTO::new)
                .toList();
        List<IndexGoodsCategoryDTO> level3Categories = goodsCategoryDao.selectByCategoryLevel(3)
                .stream()
                .map(IndexGoodsCategoryDTO::new)
                .toList();

        linkCategory(level2Categories, level3Categories);
        //处理一级和二级的层级关系
        linkCategory(level1Categories, level2Categories);
        return level1Categories;
    }

    private void linkCategory(List<IndexGoodsCategoryDTO> parentCategory, List<IndexGoodsCategoryDTO> childrenCategory) {
        //map => {level 2 ID: [CategoryObj...]}
        Map<Long, List<IndexGoodsCategoryDTO>> parentIdMap = childrenCategory.stream()
                .collect(Collectors.groupingBy(IndexGoodsCategoryDTO::getParentId));

        parentCategory.forEach(category -> {
            Long id = category.getCategoryId();
            if (parentIdMap.containsKey(id)) {
                List<IndexGoodsCategoryDTO> children = parentIdMap.get(id);
                category.setChildren(children);
            }
        });
    }
}
