package com.example.schooltrade.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.schooltrade.entity.User;

public class UserSession {
    private static User currentUser;
    private static SharedPreferences prefs;
    private static final String PREFS_NAME = "school_trade_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_REAL_NAME = "real_name";
    private static final String KEY_AVATAR = "avatar";

    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void saveLogin(User user, String token) {
        currentUser = user;
        if (prefs != null) {
            prefs.edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_USER_ID, user.getUserId())
                .putString(KEY_ACCOUNT, user.getAccount())
                .putString(KEY_REAL_NAME, user.getRealName() != null ? user.getRealName() : "")
                .putString(KEY_AVATAR, user.getAvatarUrl() != null ? user.getAvatarUrl() : "")
                .apply();
        }
    }

    /** 从本地恢复用户信息（用于自动登录） */
    public static User loadSavedUser() {
        if (prefs == null) return null;
        String token = prefs.getString(KEY_TOKEN, null);
        if (token == null || token.isEmpty()) return null;

        User user = new User();
        user.setUserId(prefs.getInt(KEY_USER_ID, 0));
        user.setAccount(prefs.getString(KEY_ACCOUNT, ""));
        user.setRealName(prefs.getString(KEY_REAL_NAME, ""));
        user.setAvatarUrl(prefs.getString(KEY_AVATAR, ""));
        currentUser = user;
        return user;
    }

    public static String getToken() {
        return prefs != null ? prefs.getString(KEY_TOKEN, null) : null;
    }

    public static boolean hasSavedLogin() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    public static void clear() {
        currentUser = null;
        if (prefs != null) {
            prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USER_ID)
                .remove(KEY_ACCOUNT)
                .remove(KEY_REAL_NAME)
                .remove(KEY_AVATAR)
                .apply();
        }
    }

    public static boolean isLogin() {
        return currentUser != null;
    }
}
