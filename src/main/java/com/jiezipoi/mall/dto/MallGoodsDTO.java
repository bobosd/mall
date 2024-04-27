package com.jiezipoi.mall.dto;

import com.jiezipoi.mall.entity.Goods;

import java.math.BigDecimal;

public class MallGoodsDTO {
    private Long goodsId;
    private String goodsName;
    private GoodsSearchBrandsDTO goodsBrand;
    private GoodsSearchCategoryDTO goodsCategory;
    private String goodsCoverImg;
    private BigDecimal sellingPrice;

    public MallGoodsDTO() {
    }

    public MallGoodsDTO(Goods goods) {
        this.goodsId = goods.getGoodsId();
        this.goodsName = goods.getGoodsName();
        this.goodsCoverImg = goods.getGoodsCoverImg();
        this.sellingPrice = goods.getSellingPrice();
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public GoodsSearchBrandsDTO getGoodsBrand() {
        return goodsBrand;
    }

    public GoodsSearchCategoryDTO getGoodsCategory() {
        return goodsCategory;
    }

    public String getGoodsCoverImg() {
        return goodsCoverImg;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public String getEuroFormatPrice() {
        return sellingPrice.toString().replace(".", ",") + "€";
    }
}
