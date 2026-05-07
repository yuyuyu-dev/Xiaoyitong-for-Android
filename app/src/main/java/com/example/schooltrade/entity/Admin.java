package com.example.schooltrade.entity;

import java.io.Serializable;

public class Admin implements Serializable {
    private int adminId;
    private String account;
    private String password;
    private String realName;

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
}