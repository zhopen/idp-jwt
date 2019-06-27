package com.hpe.cloud.management.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class PageParam {
    @JsonProperty("page_num")
    private int pageNum;
    @JsonProperty("page_size")
    private int pageSize;
    @JsonProperty("order_by")
    private String orderBy;
    @JsonProperty("order")
    private String order;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    @JsonProperty("query_key")
    private String queryKey;
    @JsonProperty("action")
    private String action;
    private Date fromDate;
    private Date toDate;

    public PageParam(){}

    public PageParam(int pageNum, int pageSize, String orderBy, String order, String startTime, String endTime, String queryKey, String action, Date fromDate, Date toDate) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.orderBy = orderBy;
        this.order = order;
        this.startTime = startTime;
        this.endTime = endTime;
        this.queryKey = queryKey;
        this.action = action;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getQueryKey() {
        return queryKey;
    }

    public void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "PageParam{" +
            "pageNum=" + pageNum +
            ", pageSize=" + pageSize +
            ", orderBy='" + orderBy + '\'' +
            ", order='" + order + '\'' +
            ", startTime='" + startTime + '\'' +
            ", endTime='" + endTime + '\'' +
            ", queryKey='" + queryKey + '\'' +
            ", action='" + action + '\'' +
            ", fromDate=" + fromDate +
            ", toDate=" + toDate +
            '}';
    }
}
