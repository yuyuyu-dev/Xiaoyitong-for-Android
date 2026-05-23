package com.example.schooltrade.api;

public class TransactionRequest {
    private Integer sellerId;
    private Integer goodsId;
    private String goodsTitle;
    private double price;

    public TransactionRequest(Integer sellerId, Integer goodsId, String goodsTitle, double price) {
        this.sellerId = sellerId;
        this.goodsId = goodsId;
        this.goodsTitle = goodsTitle;
        this.price = price;
    }

    public Integer getSellerId() { return sellerId; }
    public Integer getGoodsId() { return goodsId; }
    public String getGoodsTitle() { return goodsTitle; }
    public double getPrice() { return price; }
}
