package com.jiezipoi.mall.dto;

import java.util.Objects;

public class GoodsSearchCategoryDTO {
    private Long goodsCategoryId;
    private String goodsCategoryName;
    private boolean needHighLight;

    public Long getGoodsCategoryId() {
        return goodsCategoryId;
    }

    public void setGoodsCategoryId(Long goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
    }

    public String getGoodsCategoryName() {
        return goodsCategoryName;
    }

    public void setGoodsCategoryName(String goodsCategoryName) {
        this.goodsCategoryName = goodsCategoryName;
    }

    public boolean isNeedHighLight() {
        return needHighLight;
    }

    public void setNeedHighLight(boolean needHighLight) {
        this.needHighLight = needHighLight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodsSearchCategoryDTO that = (GoodsSearchCategoryDTO) o;

        if (!Objects.equals(goodsCategoryId, that.goodsCategoryId))
            return false;
        return Objects.equals(goodsCategoryName, that.goodsCategoryName);
    }

    @Override
    public int hashCode() {
        int result = goodsCategoryId != null ? goodsCategoryId.hashCode() : 0;
        result = 31 * result + (goodsCategoryName != null ? goodsCategoryName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GoodsSearchCategoryDTO{" +
                "goodsCategoryId=" + goodsCategoryId +
                ", goodsCategoryName='" + goodsCategoryName + '\'' +
                ", needHighLight=" + needHighLight +
                '}';
    }
}
