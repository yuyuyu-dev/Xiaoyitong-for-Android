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
    private int category; // 0=全部 1=教材书籍 2=数码产品 3=生活用品 4=服饰鞋包 5=运动器材 6=其他
    private int viewCount; // 浏览量
    private int status;
    private String createTime;
    private String updateTime;

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
    public int getCategory() { return category; }
    public void setCategory(int category) { this.category = category; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
}