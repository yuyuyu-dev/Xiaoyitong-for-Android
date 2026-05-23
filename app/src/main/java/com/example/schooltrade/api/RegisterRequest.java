package com.example.schooltrade.api;

public class RegisterRequest {
    private String account;
    private String password;
    private String realName;

    public RegisterRequest(String account, String password, String realName) {
        this.account = account;
        this.password = password;
        this.realName = realName;
    }

    public String getAccount() { return account; }
    public String getPassword() { return password; }
    public String getRealName() { return realName; }
}
