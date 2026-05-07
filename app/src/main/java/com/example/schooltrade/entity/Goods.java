package com.example.schooltrade.entity;

public class Goods {
    private int goodsId;
    private int userId;
    private String title;
    private String content;
    private double price;
    private String imgUrl;
    private int publishType; // 0出售 1置换
    private String wantGoods;
    private int status;
    private String createTime;

    public int getGoodsId() { return goodsId; }
    public void setGoodsId(int goodsId) { this.goodsId = goodsId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
    public int getPublishType() { return publishType; }
    public void setPublishType(int publishType) { this.publishType = publishType; }
    public String getWantGoods() { return wantGoods; }
    public void setWantGoods(String wantGoods) { this.wantGoods = wantGoods; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}