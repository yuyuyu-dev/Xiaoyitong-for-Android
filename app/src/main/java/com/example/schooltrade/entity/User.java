package com.example.schooltrade.entity;

public class User {
    private int userId;
    private String account;
    private String pwd;
    private String realName;
    private String phone;
    private String dormitory;
    private String avatarUrl;
    private int isRealAuth;
    // 无参构造
    public User() {}
    // 有参构造
    public int getUserId() { return userId; }
    // setter和getter方法
    public void setUserId(int userId) { this.userId = userId; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDormitory() { return dormitory; }
    public void setDormitory(String dormitory) { this.dormitory = dormitory; }
    public int getIsRealAuth() { return isRealAuth; }
    public void setIsRealAuth(int isRealAuth) { this.isRealAuth = isRealAuth; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public void setPassword(String pwd) {
        this.pwd = pwd;
    }

    public String getPassword() {
        return pwd;
    }
}