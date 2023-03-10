package com.jiezipoi.mall.utils;

import java.io.Serializable;
import java.util.List;

public class DatatableResult implements Serializable {
    private int recordsTotal;
    private List<?> list;

    /**
     * 分页
     * @param list          列表数据
     * @param totalCount    总记录数
     */

    public DatatableResult(List<?> list, int totalCount) {
        this.list = list;
        this.recordsTotal = totalCount;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
