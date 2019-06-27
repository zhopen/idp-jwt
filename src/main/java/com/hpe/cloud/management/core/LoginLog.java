package com.hpe.cloud.management.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.rkmk.annotations.OneToOne;
import com.github.rkmk.annotations.PrimaryKey;

import java.util.Date;

public class LoginLog {

    @PrimaryKey
    private int id;
    private int userId;
    private String action;
    private String message;

    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @OneToOne("user")
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "LoginLog{" +
            "id=" + id +
            ", userId=" + userId +
            ", action='" + action + '\'' +
            ", message='" + message + '\'' +
            ", createTime=" + createTime +
            ", user=" + user +
            '}';
    }
}
