package com.example.schooltrade.entity;

import java.io.Serializable;

public class Collect implements Serializable {
    private int collectId;
    private int userId;
    private int goodsId;
    private String createTime;

    public int getCollectId() { return collectId; }
    public void setCollectId(int collectId) { this.collectId = collectId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getGoodsId() { return goodsId; }
    public void setGoodsId(int goodsId) { this.goodsId = goodsId; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}