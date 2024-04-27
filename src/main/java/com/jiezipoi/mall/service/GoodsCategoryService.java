package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.GoodsCategoryDao;
import com.jiezipoi.mall.dto.CreateGoodsCategoryDTO;
import com.jiezipoi.mall.dto.IndexGoodsCategoryDTO;
import com.jiezipoi.mall.entity.GoodsCategory;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import com.jiezipoi.mall.utils.dataTable.request.GoodsTableRequest;
import jakarta.servlet.http.HttpSession;
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
    private final HttpSession session;

    public GoodsCategoryService(GoodsCategoryDao goodsCategoryDao, HttpSession httpSession) {
        this.goodsCategoryDao = goodsCategoryDao;
        this.session = httpSession;
    }

    public Response<?> getCategoriesPage(GoodsTableRequest request) {
        if (request == null ||
                request.getStart() == null ||
                request.getLength() == null ||
                request.getCategoryLevel() == null ||
                request.getParentId() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        String orderDir = request.getOrder()
                .get(0)
                .getDir();
        List<GoodsCategory> goodsCategories = goodsCategoryDao.findGoodsCategoryList(
                request.getCategoryLevel(), request.getParentId(), orderDir);
        DataTableResult dataTableResult = new DataTableResult(goodsCategories, goodsCategories.size());
        Response<DataTableResult> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(dataTableResult);
        return response;
    }

    /**
     * 用于创建category，接受一个DTO，含有category对象和一个用于表示父类path对象
     * 无法直接获取path是因为无法知道被插入的id为多少，所以需要先插入，然后拼接父类id
     * @param createGoodsCategoryDTO 包含GoodsCategory和父类的路径
     * @return 响应
     */
    @Transactional
    public Response<?> createCategory(CreateGoodsCategoryDTO createGoodsCategoryDTO) {
        GoodsCategory category = createGoodsCategoryDTO.getGoodsCategory();
        String parentPath = createGoodsCategoryDTO.getParentPath();
        if (category.getCategoryName() == null ||
                category.getCategoryLevel() == null ||
                category.getCategoryRank() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        GoodsCategory temp = goodsCategoryDao.selectByParentIdAndName(category.getParentId(), category.getCategoryName());
        if (temp != null) {
            return new Response<>(CommonResponse.DATA_ALREADY_EXISTS);
        }
        category.setCreateUser((int) session.getAttribute("userId"));
        goodsCategoryDao.insertSelective(category);
        long id = category.getGoodsCategoryId();
        String path;
        if (parentPath != null && parentPath.isBlank()) {
            path = Long.toString(id);
        } else {
            path = parentPath + "." + id;
        }
        if (goodsCategoryDao.updatePathById(id, path) > 0) {
            return new Response<>(CommonResponse.SUCCESS);
        } else {
            return new Response<>(CommonResponse.ERROR);
        }
    }

    public Response<?> updateGoodsCategory(GoodsCategory category) {
        if (category.getGoodsCategoryId() == null || category.getCategoryName() == null || category.getCategoryLevel() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        GoodsCategory temp = goodsCategoryDao.selectByPrimaryKey(category.getGoodsCategoryId());
        if (temp == null) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        }
        temp = goodsCategoryDao.selectByParentIdAndName(category.getParentId(), category.getCategoryName());
        if (temp != null && !temp.getGoodsCategoryId().equals(category.getGoodsCategoryId())) {
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
