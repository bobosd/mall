package com.jiezipoi.mall.dao;


import com.jiezipoi.mall.entity.GoodsTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsTagDao {
    GoodsTag selectGoodsTagById(@Param("id") Long id);

    int insertTag(@Param("tagName") String tagName);

    List<GoodsTag> findByName(@Param("nameArray") String[] nameArray);

    int insertBatch(@Param("list") List<GoodsTag> list);

    int insertGoodsHasTag(@Param("goodsId") Long goodsId,@Param("tagList") List<GoodsTag> goodsTags);

    List<GoodsTag> findByGoodsId(@Param("goodsId") Long goodsId);

    int deleteGoodsHasTagByGoodsIdAndTagId(@Param("goodsId") Long goodsId, @Param("tagList") List<GoodsTag> tagList);
}
