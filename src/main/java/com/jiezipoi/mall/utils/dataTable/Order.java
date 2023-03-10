package com.jiezipoi.mall.utils.dataTable;

import java.util.Map;

public class Order {
    private Integer column;
    private String dir;

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String toOrderByString(Map<Integer, String> columnMap) {
        return columnMap.get(column) + " " + dir;
    }
}