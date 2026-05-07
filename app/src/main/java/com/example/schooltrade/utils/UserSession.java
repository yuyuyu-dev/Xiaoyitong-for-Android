package com.example.schooltrade.utils;

import com.example.schooltrade.entity.User;

public class UserSession {
    // 单例保存当前登录用户
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    // 判断是否已登录
    public static boolean isLogin() {
        return currentUser != null;
    }
}