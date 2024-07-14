package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.dto.MallGoodsDTO;
import com.jiezipoi.mall.entity.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GoodsDao {
    long insertSelective(Goods goods);

    List<Goods> list(@Param("start") int start,
                     @Param("limit") int limit,
                     @Param("path") String path,
                     @Param("keyword") String keyword,
                     @Param("colNumber") Integer colNumber,
                     @Param("dir") String dir);

    int selectCountTotalOfList(@Param("path") String path,
                               @Param("keyword") String keyword);

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

    Goods selectGoodsWithTagByGoodsId(@Param("goodsId") long goodsId);

    List<Goods> findGoodsByIds(@Param("ids") List<Long> goodsId);

    int updateByPrimaryKeySelective(Goods goods);

    List<MallGoodsDTO> findByTagContaining(@Param("keywordArr") String[] keywordArr);

    List<MallGoodsDTO> findByCategory(@Param("categoryId") Long categoryId);

    List<Goods> findByNameContaining(@Param("goodsName") String goodsName);

    int reduceStockByPrimaryKey(Map<Long, Integer> map);
}
