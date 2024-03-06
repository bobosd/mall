package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.GoodsCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsCategoryDao {
    int deleteByPrimaryKey(Long categoryId);

    int insert(GoodsCategory category);

    long insertSelective(GoodsCategory category);

    GoodsCategory selectByPrimaryKey(Long id);

    GoodsCategory selectByLevelAndName(@Param("categoryLevel") Byte categoryLevel,
                                       @Param("categoryName") String categoryName);

    int updateByPrimaryKeySelective(GoodsCategory category);

    int updateByPrimaryKey(GoodsCategory category);

    List<GoodsCategory> findGoodsCategoryList(@Param("categoryLevel") Integer categoryLevel,
                                              @Param("parentId") Long parentId);

    int getTotalGoodsCategories(@Param("categoryLevel") Integer categoryLevel,
                                @Param("parentId") Long parentId);

    int deleteBatch(Integer[] ids);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("parentIds") Long parentIds,
                                                           @Param("categoryLevel") int categoryLevel,
                                                           @Param("number") int number);

    List<GoodsCategory> selectByIds(@Param("ids") long[] ids);

    List<GoodsCategory> selectByCategoryLevel(@Param("level") byte level);

    int updatePathById(@Param("id") Long id, @Param("path") String path);
}
