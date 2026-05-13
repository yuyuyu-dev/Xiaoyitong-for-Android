package com.example.schooltrade.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.schooltrade.entity.User;

public class UserDao {
    // 用户登录
    public static User login(String account, String pwd) {
        SQLiteDatabase db = DBUtil.getReadableDatabase();
        String sql = "SELECT * FROM UserInfo WHERE Account=? AND Password=? AND status=1";
        Cursor cursor = db.rawQuery(sql, new String[]{account, pwd});
        
        try {
            if (cursor.moveToFirst()) {
                User user = new User();
                user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("UserID")));
                user.setAccount(cursor.getString(cursor.getColumnIndexOrThrow("Account")));
                user.setRealName(cursor.getString(cursor.getColumnIndexOrThrow("RealName")));
                return user;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    // 用户注册
    public static boolean register(User user) {
        SQLiteDatabase db = DBUtil.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Account", user.getAccount());
        values.put("Password", user.getPassword());
        values.put("RealName", user.getRealName());
        
        long result = db.insert("UserInfo", null, values);
        return result > 0;
    }
}