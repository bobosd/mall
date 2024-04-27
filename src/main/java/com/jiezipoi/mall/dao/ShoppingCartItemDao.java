package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.dto.ShoppingCartItemDTO;
import com.jiezipoi.mall.entity.ShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingCartItemDao {
    int insertSelective(@Param("item") ShoppingCartItem item);

    ShoppingCartItem selectByUserIdAndGoodsId(@Param("mallUserId") Long mallUserId, @Param("mallGoodsId") Long goodsId);

    int selectCountByUserId(Long mallUserId);

    int updateByUserIdAndGoodsIdSelective(@Param("item") ShoppingCartItem item);

    List<ShoppingCartItemDTO> selectByUserId(@Param("userId") Long mallUserId);

    int deleteByGoodsIdAndUserId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
}
