package com.example.schooltrade.api;

public class LoginResponse {
    private Integer userId;
    private String account;
    private String realName;
    private String avatarUrl;
    private String token;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
