package com.hpe.cloud.management.vo;

import java.util.List;

public class PageList<T> {

    private int count;
    private List<T> rows;

    public PageList() {
    }

    public PageList(int count, List<T> rows) {
        this.count = count;
        this.rows = rows;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
