package com.jiezipoi.mall.controller.vo;

import com.jiezipoi.mall.entity.GoodsCategory;

public class IndexLevel3CategoryVO {
    private Long categoryId;
    private Byte categoryLevel;
    private String categoryName;
    private Long parentId;

    public IndexLevel3CategoryVO(GoodsCategory goodsCategory) {
        this.categoryId = goodsCategory.getGoodsCategoryId();
        this.categoryLevel = goodsCategory.getCategoryLevel();
        this.categoryName = goodsCategory.getCategoryName();
        this.parentId = goodsCategory.getParentId();
    }

    public IndexLevel3CategoryVO() {

    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Byte getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(Byte categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
