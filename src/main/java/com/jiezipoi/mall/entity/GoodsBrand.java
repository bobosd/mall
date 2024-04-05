package com.jiezipoi.mall.entity;

import java.util.Objects;

public class GoodsBrand {
    private Long goodsBrandId;
    private String goodsBrandName;

    public Long getGoodsBrandId() {
        return goodsBrandId;
    }

    public void setGoodsBrandId(Long goodsBrandId) {
        this.goodsBrandId = goodsBrandId;
    }

    public String getGoodsBrandName() {
        return goodsBrandName;
    }

    public void setGoodsBrandName(String goodsBrandName) {
        this.goodsBrandName = goodsBrandName;
    }

    @Override
    public String toString() {
        return "GoodsBrand{" +
                "goodsBrandId=" + goodsBrandId +
                ", goodsBrandName='" + goodsBrandName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodsBrand that = (GoodsBrand) o;

        if (!Objects.equals(goodsBrandId, that.goodsBrandId)) return false;
        return Objects.equals(goodsBrandName, that.goodsBrandName);
    }

    @Override
    public int hashCode() {
        int result = goodsBrandId != null ? goodsBrandId.hashCode() : 0;
        result = 31 * result + (goodsBrandName != null ? goodsBrandName.hashCode() : 0);
        return result;
    }
}
