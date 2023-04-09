package com.jiezipoi.mall.utils.dataTable.request;

import com.jiezipoi.mall.utils.dataTable.Order;
import com.jiezipoi.mall.utils.dataTable.Search;

import java.util.List;

public class DataTableRequest {
    private Integer start;
    private Integer length;
    private Search search;
    private List<Order> order;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }
}