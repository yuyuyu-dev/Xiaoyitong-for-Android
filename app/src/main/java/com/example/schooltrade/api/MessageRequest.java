package com.example.schooltrade.api;

public class MessageRequest {
    private Integer receiverId;
    private Integer goodsId;
    private String content;
    private String messageType;

    public MessageRequest(Integer receiverId, Integer goodsId, String content) {
        this.receiverId = receiverId;
        this.goodsId = goodsId;
        this.content = content;
        this.messageType = "text";
    }

    public Integer getReceiverId() { return receiverId; }
    public Integer getGoodsId() { return goodsId; }
    public String getContent() { return content; }
    public String getMessageType() { return messageType; }
}
