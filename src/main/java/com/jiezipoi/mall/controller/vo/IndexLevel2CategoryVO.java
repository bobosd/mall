package com.jiezipoi.mall.controller.vo;

import com.jiezipoi.mall.entity.GoodsCategory;

import java.util.List;

public class IndexLevel2CategoryVO {
    private Long categoryId;
    private Long parentId;
    private Byte categoryLevel;
    private String categoryName;
    private List<IndexLevel3CategoryVO> children;

    public IndexLevel2CategoryVO() {

    }
    public IndexLevel2CategoryVO(GoodsCategory goodsCategory) {
        this.categoryId = goodsCategory.getId();
        this.parentId = goodsCategory.getParentId();
        this.categoryLevel = goodsCategory.getCategoryLevel();
        this.categoryName = goodsCategory.getCategoryName();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public List<IndexLevel3CategoryVO> getChildren() {
        return children;
    }

    public void setChildren(List<IndexLevel3CategoryVO> children) {
        this.children = children;
    }
}