package com.jiezipoi.mall.dto;

import java.math.BigDecimal;

public class ShoppingCartItemDTO {
    private Long cartItemId;
    private Long goodsId;
    private Integer goodsCount;
    private String goodsName;
    private String goodsCoverImg;
    private BigDecimal sellingPrice;

    public Long getCartItemId() {
        return cartItemId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public String getGoodsCoverImg() {
        return goodsCoverImg;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }
}
