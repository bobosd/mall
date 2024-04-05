package com.jiezipoi.mall.dto;

import com.jiezipoi.mall.entity.GoodsBrand;

public class GoodsSearchBrandsDTO extends GoodsBrand {
    private boolean needHighLight;

    public boolean isNeedHighLight() {
        return needHighLight;
    }

    public void setNeedHighLight(boolean needHighLight) {
        this.needHighLight = needHighLight;
    }
}
