package com.jiezipoi.mall.dto;

import com.jiezipoi.mall.entity.GoodsCategory;

public class CreateGoodsCategoryDTO {
    private GoodsCategory goodsCategory;
    private String parentPath;

    public GoodsCategory getGoodsCategory() {
        return goodsCategory;
    }

    public String getParentPath() {
        return parentPath;
    }
}
