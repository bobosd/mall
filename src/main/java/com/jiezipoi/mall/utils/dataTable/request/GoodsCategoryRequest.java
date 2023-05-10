package com.jiezipoi.mall.utils.dataTable.request;

public class GoodsCategoryRequest extends DataTableRequest {
    private Integer categoryLevel;
    private Long parentId;

    private String path;

    public Integer getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(Integer categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
