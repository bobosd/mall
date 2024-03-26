package com.jiezipoi.mall.entity;

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
}
