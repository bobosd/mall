package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.GoodsBrand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsBrandDao {
    GoodsBrand selectGoodsBrandById(@Param("brandId") Long brandId);
    List<GoodsBrand> findAllGoodsBrand(@Param("start") Integer start,
                                       @Param("limit") Integer limit,
                                       @Param("keyword") String keyword);

    List<GoodsBrand> findByGoodsNameContaining(@Param("keyword") String keyword);

    int deleteGoodsBrandById(@Param("brandId") Long brandId);

    int insertGoodsBrand(@Param("brandName") String brandName);

    int updateGoodsBrandById(@Param("brandId") Long brandId, @Param("brandName") String brandName);

    int selectGoodsBrandCount();

    GoodsBrand selectGoodsBrandByName(@Param("brandName") String brandName);

    int countAssociatedGoodsByBrandId(@Param("goodsBrandId") Long goodsBrandId);
}
