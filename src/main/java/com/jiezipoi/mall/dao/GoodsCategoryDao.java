package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.GoodsCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsCategoryDao {
    int deleteByPrimaryKey(Long categoryId);

    int insert(GoodsCategory category);

    long insertSelective(GoodsCategory category);

    GoodsCategory selectByPrimaryKey(Long id);

    GoodsCategory selectByCategoryName(@Param("categoryName") String categoryName);

    int updateByPrimaryKeySelective(GoodsCategory category);

    int updateByPrimaryKey(GoodsCategory category);

    List<GoodsCategory> findGoodsCategoryList(@Param("categoryLevel") Integer categoryLevel,
                                              @Param("parentId") Long parentId,
                                              @Param("colNumber") Integer orderBy,
                                              @Param("dir") String dir);

    List<GoodsCategory> findAllCategory();

    int getTotalGoodsCategories(@Param("categoryLevel") Integer categoryLevel,
                                @Param("parentId") Long parentId);

    int deleteBatch(Integer[] ids);

    List<GoodsCategory> findByLevelAndParentIdsAndNumber(@Param("parentIds") Long parentIds,
                                                         @Param("categoryLevel") int categoryLevel,
                                                         @Param("number") int number);

    List<GoodsCategory> findByIds(@Param("ids") long[] ids);

    List<GoodsCategory> findByCategoryLevel(@Param("level") int level);

    int updatePathById(@Param("id") Long id, @Param("path") String path);
}
