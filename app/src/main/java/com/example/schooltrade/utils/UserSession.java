package com.example.schooltrade.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.schooltrade.entity.User;

public class UserSession {
    private static User currentUser;
    private static SharedPreferences prefs;
    private static final String PREFS_NAME = "school_trade_prefs";
    private static final String KEY_TOKEN = "token";

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

    public static void saveToken(String token) {
        if (prefs != null) {
            prefs.edit().putString(KEY_TOKEN, token).apply();
        }
    }

    public static String getToken() {
        return prefs != null ? prefs.getString(KEY_TOKEN, null) : null;
    }

    public static void clear() {
        currentUser = null;
        if (prefs != null) {
            prefs.edit().remove(KEY_TOKEN).apply();
        }
    }

    public static boolean isLogin() {
        return currentUser != null;
    }
}
