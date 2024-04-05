package com.jiezipoi.mall.entity;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;
import java.util.Objects;

public class GoodsTag implements Serializable {
    private Long goodsTagId;
    private String tagName;

    public Long getGoodsTagId() {
        return goodsTagId;
    }

    public void setGoodsTagId(Long goodsTagId) {
        this.goodsTagId = goodsTagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodsTag goodsTag = (GoodsTag) o;
        Collator collator = Collator.getInstance(Locale.getDefault());
        collator.setStrength(Collator.NO_DECOMPOSITION);
        return collator.compare(goodsTag.tagName, this.tagName) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagName);
    }
}
