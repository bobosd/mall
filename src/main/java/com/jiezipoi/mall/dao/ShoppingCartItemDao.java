package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.ShoppingCartItem;
import org.apache.ibatis.annotations.Param;

public interface ShoppingCartItemDao {
    int insertSelective(@Param("item") ShoppingCartItem item);

    ShoppingCartItem selectByUserIdAndGoodsId(@Param("mallUserId") Long mallUserId, @Param("mallGoodsId") Long goodsId);

    int selectCountByUserId(Long mallUserId);
}
