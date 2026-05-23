package com.example.schooltrade.api;

public class GoodsRequest {
    private String title;
    private String content;
    private double price;
    private Integer publishType;
    private String wantGoods;
    private String imgUrl;
    private Integer category;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public Integer getPublishType() { return publishType; }
    public void setPublishType(Integer publishType) { this.publishType = publishType; }
    public String getWantGoods() { return wantGoods; }
    public void setWantGoods(String wantGoods) { this.wantGoods = wantGoods; }
    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
    public Integer getCategory() { return category; }
    public void setCategory(Integer category) { this.category = category; }
}
