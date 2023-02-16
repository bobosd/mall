package com.jiezipoi.mall.utils;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {
    private int recordsTotal;
    private int pageSize;
    private int totalPage;
    private int currentPage;
    private List<?> list;

    /**
     * 分页
     * @param list          列表数据
     * @param totalCount    总记录数
     * @param pageSize      每页记录数
     * @param currentPage   当前页数
     */

    public PageResult(List<?> list, int totalCount, int pageSize, int currentPage) {
        this.list = list;
        this.recordsTotal = totalCount;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
