package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.dto.GoodsSearchDTO;
import com.jiezipoi.mall.entity.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsDao {
    long insertSelective(Goods goods);

    List<Goods> list(@Param("start") int start, @Param("limit") int limit);

    /**
     * get children goods of passed category ID
     * @param start pagination
     * @param limit display count
     * @param path category ID
     * @param level level of this category
     * @return List of goods
     */
    List<Goods> listByCategory(@Param("start") int start, @Param("limit") int limit,
                               @Param("path") String path, @Param("level") int level);

    int getGoodsCount();

    int getCategoryGoodsCount(@Param("path") String categoryPath);

    Goods selectGoodsById(@Param("goodsId") long goodsId);

    List<Goods> selectGoodsByIds(@Param("ids") long[] goodsId);

    int updateByPrimaryKeySelective(Goods goods);

    List<GoodsSearchDTO> selectGoodsByTagContaining(@Param("keywordArr") String[] keywordArr);

    List<GoodsSearchDTO> selectGoodsByCategory(@Param("categoryId") Long categoryId);
}
