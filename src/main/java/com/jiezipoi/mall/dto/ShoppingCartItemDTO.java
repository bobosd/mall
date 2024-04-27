package com.jiezipoi.mall.dto;

import java.math.BigDecimal;

public class ShoppingCartItemDTO {
    private Long cartItemId;
    private Long goodsId;
    private Integer goodsCount;
    private String goodsName;
    private String goodsCoverImg;
    private BigDecimal sellingPrice;

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public void setGoodsCoverImg(String goodsCoverImg) {
        this.goodsCoverImg = goodsCoverImg;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

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

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public String getSellingPriceInEuroFormat() {
        BigDecimal priceSum = sellingPrice.multiply(BigDecimal.valueOf(goodsCount));
        String priceStr = priceSum.toString();
        priceStr = priceStr.replaceAll("\\.", ",");
        return priceStr + "€";
    }
}
