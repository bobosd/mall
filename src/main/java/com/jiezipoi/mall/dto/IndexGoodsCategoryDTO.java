package com.jiezipoi.mall.dto;

import com.jiezipoi.mall.entity.GoodsCategory;

import java.util.List;

public class IndexGoodsCategoryDTO {
    private Long categoryId;
    private Long parentId;
    private String categoryName;
    private List<IndexGoodsCategoryDTO> children;

    public IndexGoodsCategoryDTO() {

    }

    public IndexGoodsCategoryDTO(GoodsCategory category) {
        this.categoryId = category.getGoodsCategoryId();
        this.parentId = category.getParentId();
        this.categoryName = category.getCategoryName();
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<IndexGoodsCategoryDTO> getChildren() {
        return children;
    }

    public void setChildren(List<IndexGoodsCategoryDTO> children) {
        this.children = children;
    }
}
