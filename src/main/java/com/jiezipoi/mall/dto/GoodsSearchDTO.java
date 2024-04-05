package com.jiezipoi.mall.dto;

import java.math.BigDecimal;

public class GoodsSearchDTO {
    private Long goodsId;
    private String goodsName;
    private GoodsSearchBrandsDTO goodsBrand;
    private GoodsSearchCategoryDTO goodsCategory;
    private String goodsCoverImg;
    private BigDecimal sellingPrice;

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
